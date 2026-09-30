package com.paymentsave.paymentsave.coreapp.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.logs.LogRecordBuilder;
import io.opentelemetry.api.logs.Logger;
import io.opentelemetry.api.logs.Severity;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.data.Body;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.resources.Resource;

/**
 * OpenTelemetry logger with durable offline queue.
 * <p>
 * Flow:
 * 1) Batch processor hands records to {@link OfflineAwareLogRecordExporter}
 * 2) Records are write-ahead saved to disk, then exported over OTLP/HTTP
 * 3) On export success the pending file is deleted
 * 4) On failure (or app kill) files remain and are retried by {@link #syncPendingLogs()}
 */
public final class TelemetryLogger {

    private static final String TAG = "TelemetryLogger";

    private static final AttributeKey<String> EVENT_NAME =
            AttributeKey.stringKey("event.name");

    private static final AttributeKey<String> ANDROID_LOG_TAG =
            AttributeKey.stringKey("android.log.tag");

    private static final AttributeKey<String> EXCEPTION_TYPE =
            AttributeKey.stringKey("exception.type");

    private static final AttributeKey<String> EXCEPTION_MESSAGE =
            AttributeKey.stringKey("exception.message");

    private static final AttributeKey<String> EXCEPTION_STACKTRACE =
            AttributeKey.stringKey("exception.stacktrace");

    /**
     * How frequently failed logs should be retried.
     */
    private static final long SYNC_INTERVAL_SECONDS = 30L;

    /**
     * A newly created pending file may still belong to an active export.
     */
    private static final long FRESH_BATCH_AGE_MILLISECONDS = 15_000L;

    /**
     * Maximum amount of time to wait for one retry request.
     * <p>
     * The OTLP exporter timeout is 10 seconds, so this is slightly higher.
     */
    private static final long RETRY_WAIT_SECONDS = 12L;

    /**
     * Do not try every stored file during one synchronization cycle.
     */
    private static final int MAX_RETRY_BATCHES_PER_RUN = 5;

    /**
     * Prevent pending log files from growing forever.
     */
    private static final int MAX_PENDING_BATCH_FILES = 200;
    private static final AtomicBoolean syncInProgress =
            new AtomicBoolean(false);
    private static volatile Logger otelLogger;
    private static volatile SdkLoggerProvider loggerProvider;
    private static volatile OfflineAwareLogRecordExporter offlineExporter;
    private static volatile OfflineLogStore offlineLogStore;
    private static volatile ScheduledExecutorService syncExecutor;

    private TelemetryLogger() {
        // Utility class.
    }

    /**
     * Initializes OpenTelemetry logging to OpenObserve (Panoptes).
     * <p>
     * Call this once from Application.onCreate().
     * <p>
     * Endpoint example:
     * https://panoptes-uat.psapp.uk/api/default/v1/logs
     *
     * @param context        Android application context
     * @param endpoint       complete OTLP HTTP logs endpoint
     * @param ingestEmail    OpenObserve service-account email
     * @param ingestToken    OpenObserve ingest token (not the admin password)
     * @param streamName     OpenObserve stream, e.g. terminal
     * @param serviceName    resource attribute service.name
     * @param serviceVersion resource attribute service.version
     * @param environment    resource attribute deployment.environment
     */
    public static synchronized void initialize(
            Context context,
            String endpoint,
            String ingestEmail,
            String ingestToken,
            String streamName,
            String serviceName,
            String serviceVersion,
            String environment
    ) {
        if (otelLogger != null) {
            Log.d(TAG, "TelemetryLogger is already initialized");
            return;
        }

        if (context == null) {
            throw new IllegalArgumentException(
                    "Context is required for offline log storage"
            );
        }

        if (endpoint == null || endpoint.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "OpenTelemetry endpoint is required"
            );
        }

        String authorizationHeader = buildBasicAuthHeader(
                ingestEmail,
                ingestToken
        );

        if (authorizationHeader == null) {
            Log.e(
                    TAG,
                    "OpenObserve ingest token is missing. "
                            + "Set OPENOBSERVE_INGEST_TOKEN in "
                            + "openobserve.properties. Logs will stay in Logcat."
            );
            return;
        }

        String safeServiceName = valueOrDefault(
                serviceName,
                "pax-terminal"
        );

        String safeServiceVersion = valueOrDefault(
                serviceVersion,
                "unknown"
        );

        String safeEnvironment = valueOrDefault(
                environment,
                "stg"
        );

        String safeStreamName = valueOrDefault(
                streamName,
                "terminal"
        );

        Context applicationContext = context.getApplicationContext();

        offlineLogStore = new OfflineLogStore(
                new File(
                        applicationContext.getFilesDir(),
                        "otel_log_pending"
                )
        );

        /*
         * Keep this API check if it is required by your current project or
         * OpenTelemetry dependency configuration.
         */
        if (android.os.Build.VERSION.SDK_INT
                < android.os.Build.VERSION_CODES.O) {

            Log.w(
                    TAG,
                    "OTLP export requires API 26+. "
                            + "Telemetry logs will only go to Logcat."
            );

            return;
        }

        Resource resource = Resource.getDefault().merge(
                Resource.create(
                        Attributes.builder()
                                .put(
                                        AttributeKey.stringKey("service.name"),
                                        safeServiceName
                                )
                                .put(
                                        AttributeKey.stringKey("service.version"),
                                        safeServiceVersion
                                )
                                .put(AttributeKey.stringKey(
                                           "deployment.environment"
                                   ),
                                   safeEnvironment
                                )
                                .build()
                )
        );

        OtlpHttpLogRecordExporter otlpExporter =
                OtlpHttpLogRecordExporter.builder()
                        .setEndpoint(endpoint.trim())
                        .addHeader("Authorization", authorizationHeader)
                        .addHeader("stream-name", safeStreamName)
                        .setTimeout(Duration.ofSeconds(10))
                        .build();

        offlineExporter = new OfflineAwareLogRecordExporter(
                otlpExporter,
                offlineLogStore
        );

        BatchLogRecordProcessor processor =
                BatchLogRecordProcessor.builder(offlineExporter)
                        .setScheduleDelay(Duration.ofSeconds(2))
                        .setMaxQueueSize(2048)
                        .setMaxExportBatchSize(512)
                        .build();

        loggerProvider = SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(processor)
                .build();

        OpenTelemetrySdk openTelemetrySdk =
                OpenTelemetrySdk.builder()
                        .setLoggerProvider(loggerProvider)
                        .build();

        otelLogger = openTelemetrySdk
                .getLogsBridge()
                .get(safeServiceName);

        /*
         * This starts the first pending-log synchronization immediately,
         * but it runs on the telemetry background thread.
         *
         * Do not call syncPendingLogsInternal() directly here.
         */
        startPeriodicSync();

        Log.i(
                TAG,
                "OpenTelemetry logging initialized for "
                        + safeServiceName
        );
    }

    public static void info(
            String tag,
            String eventName,
            String message
    ) {
        String safeTag = valueOrDefault(tag, TAG);
        String safeEventName = valueOrDefault(eventName, "unknown");
        String safeMessage = valueOrDefault(message, "");

        // Log.i(safeTag, safeMessage);
        Log.i(TAG, safeMessage);

        Logger logger = otelLogger;

        if (logger == null) {
            return;
        }

        try {
            logger.logRecordBuilder()
                    .setBody(safeMessage)
                    .setSeverity(Severity.INFO)
                    .setSeverityText("INFO")
                    .setAttribute(EVENT_NAME, safeEventName)
                    .setAttribute(ANDROID_LOG_TAG, safeTag)
                    .emit();
        } catch (Exception exception) {
            /*
             * Telemetry failure must never crash the main application.
             */
            Log.w(TAG, "Failed to emit INFO telemetry log", exception);
        }
    }

    public static void debug(
            String tag,
            String eventName,
            String message
    ) {
        String safeTag = valueOrDefault(tag, TAG);
        String safeEventName = valueOrDefault(eventName, "unknown");
        String safeMessage = valueOrDefault(message, "");

        // Log.d(safeTag, safeMessage);
        Log.d(TAG, safeMessage);

        Logger logger = otelLogger;

        if (logger == null) {
            return;
        }

        try {
            logger.logRecordBuilder()
                    .setBody(safeMessage)
                    .setSeverity(Severity.DEBUG)
                    .setSeverityText("DEBUG")
                    .setAttribute(EVENT_NAME, safeEventName)
                    .setAttribute(ANDROID_LOG_TAG, safeTag)
                    .emit();
        } catch (Exception exception) {
            Log.w(TAG, "Failed to emit DEBUG telemetry log", exception);
        }
    }

    public static void error(
            String tag,
            String eventName,
            String message,
            Throwable throwable
    ) {
        String safeTag = valueOrDefault(tag, TAG);
        String safeEventName = valueOrDefault(eventName, "unknown");
        String safeMessage = valueOrDefault(message, "");

        // Log.e(safeTag, safeMessage, throwable);
        Log.e(TAG, safeMessage, throwable);

        Logger logger = otelLogger;

        if (logger == null) {
            return;
        }

        try {
            LogRecordBuilder builder = logger.logRecordBuilder()
                    .setBody(safeMessage)
                    .setSeverity(Severity.ERROR)
                    .setSeverityText("ERROR")
                    .setAttribute(EVENT_NAME, safeEventName)
                    .setAttribute(ANDROID_LOG_TAG, safeTag);

            if (throwable != null) {
                builder.setAttribute(
                        EXCEPTION_TYPE,
                        throwable.getClass().getName()
                );

                if (throwable.getMessage() != null) {
                    builder.setAttribute(
                            EXCEPTION_MESSAGE,
                            throwable.getMessage()
                    );
                }

                builder.setAttribute(
                        EXCEPTION_STACKTRACE,
                        Log.getStackTraceString(throwable)
                );
            }

            builder.emit();
        } catch (Exception exception) {
            Log.w(TAG, "Failed to emit ERROR telemetry log", exception);
        }
    }

    /**
     * Requests synchronization of pending logs.
     * <p>
     * This method is safe to call from an Activity, Fragment or any main-thread
     * lifecycle method because it only submits work to the background executor.
     */
    public static void syncPendingLogs() {
        ScheduledExecutorService executor = syncExecutor;

        if (executor == null || executor.isShutdown()) {
            Log.d(TAG, "Telemetry synchronization executor is unavailable");
            return;
        }

        try {
            executor.execute(
                    TelemetryLogger::syncPendingLogsInternal
            );
        } catch (RejectedExecutionException exception) {
            Log.w(
                    TAG,
                    "Telemetry synchronization request was rejected",
                    exception
            );
        }
    }

    /**
     * Performs pending-log synchronization.
     * <p>
     * Never call this directly from the Android main thread.
     */
    private static void syncPendingLogsInternal() {
        OfflineAwareLogRecordExporter exporter = offlineExporter;
        OfflineLogStore store = offlineLogStore;

        if (exporter == null || store == null) {
            return;
        }

        if (!syncInProgress.compareAndSet(false, true)) {
            Log.d(TAG, "Pending log synchronization is already running");
            return;
        }

        try {
            int pendingBefore = store.pendingFileCount();

            if (pendingBefore == 0) {
                return;
            }

            Log.i(
                    TAG,
                    "Syncing "
                            + pendingBefore
                            + " pending log batch(es)"
            );

            int remaining = exporter.retryPendingBatches();

            Log.i(
                    TAG,
                    "Pending log synchronization finished. Remaining: "
                            + remaining
            );
        } catch (Exception exception) {
            Log.w(
                    TAG,
                    "Pending log synchronization failed",
                    exception
            );
        } finally {
            syncInProgress.set(false);
        }
    }

    public static int getPendingBatchCount() {
        OfflineLogStore store = offlineLogStore;

        if (store == null) {
            return 0;
        }

        try {
            return store.pendingFileCount();
        } catch (Exception exception) {
            Log.w(TAG, "Could not count pending log batches", exception);
            return 0;
        }
    }

    /**
     * Shuts down telemetry.
     * <p>
     * Normally, an Android Application object lives for the full process
     * lifetime, so manually calling this is usually unnecessary.
     */
    public static synchronized void shutdown() {
        stopPeriodicSync();

        SdkLoggerProvider provider = loggerProvider;

        /*
         * Clear global references first so no new logs are submitted while
         * shutdown is being performed.
         */
        otelLogger = null;
        loggerProvider = null;
        offlineExporter = null;
        offlineLogStore = null;

        if (provider != null) {
            try {
                /*
                 * The logger provider manages the processor and exporter.
                 * Do not call offlineExporter.shutdown() separately.
                 */
                provider.forceFlush();
                provider.shutdown();
            } catch (Exception exception) {
                Log.w(TAG, "Telemetry shutdown failed", exception);
            }
        }

        syncInProgress.set(false);

        Log.i(TAG, "TelemetryLogger shut down");
    }

    private static void startPeriodicSync() {
        stopPeriodicSync();

        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor(runnable -> {
                    Thread thread = new Thread(
                            runnable,
                            "telemetry-log-sync"
                    );

                    /*
                     * A daemon thread should not keep a non-Android JVM alive.
                     * Android process lifecycle remains controlled by Android.
                     */
                    thread.setDaemon(true);

                    return thread;
                });

        syncExecutor = executor;

        /*
         * Initial delay is zero:
         *
         * Pending batches are recovered immediately after initialization,
         * but the work runs on telemetry-log-sync, not the Android main thread.
         */
        executor.scheduleWithFixedDelay(
                TelemetryLogger::syncPendingLogsInternal,
                0L,
                SYNC_INTERVAL_SECONDS,
                TimeUnit.SECONDS
        );
    }

    private static void stopPeriodicSync() {
        ScheduledExecutorService executor = syncExecutor;

        syncExecutor = null;

        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private static String valueOrDefault(
            String value,
            String defaultValue
    ) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    /**
     * Builds {@code Authorization: Basic ...} for OpenObserve ingest.
     * Returns null when email or token is missing so we do not send unauthenticated requests.
     */
    private static String buildBasicAuthHeader(
            String email,
            String token
    ) {
        if (email == null || email.trim().isEmpty()
                || token == null || token.trim().isEmpty()) {
            return null;
        }

        String credentials = email.trim() + ":" + token.trim();
        String encoded = Base64.encodeToString(
                credentials.getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP
        );

        return "Basic " + encoded;
    }

    // -------------------------------------------------------------------------
    // Offline-aware exporter
    // -------------------------------------------------------------------------

    /**
     * Writes each export batch to disk before sending it to the collector.
     * <p>
     * The file is deleted only after the collector export succeeds.
     */
    private static final class OfflineAwareLogRecordExporter
            implements LogRecordExporter {

        private final LogRecordExporter delegate;

        private final OfflineLogStore store;

        private final Object retryLock = new Object();

        OfflineAwareLogRecordExporter(
                LogRecordExporter delegate,
                OfflineLogStore store
        ) {
            if (delegate == null) {
                throw new IllegalArgumentException(
                        "Delegate exporter is required"
                );
            }

            if (store == null) {
                throw new IllegalArgumentException(
                        "Offline log store is required"
                );
            }

            this.delegate = delegate;
            this.store = store;
        }

        @Override
        public CompletableResultCode export(
                Collection<LogRecordData> logs
        ) {
            if (logs == null || logs.isEmpty()) {
                return CompletableResultCode.ofSuccess();
            }

            final File pendingFile;

            try {
                /*
                 * Write-ahead storage:
                 * save the batch before attempting network export.
                 */
                pendingFile = store.writeBatch(logs);
            } catch (Exception exception) {
                Log.e(
                        TAG,
                        "Failed to persist telemetry batch before export",
                        exception
                );

                /*
                 * Disk storage failed, but still attempt a live export.
                 */
                try {
                    return delegate.export(logs);
                } catch (Throwable exportException) {
                    Log.e(
                            TAG,
                            "Live telemetry export also failed",
                            exportException
                    );

                    return CompletableResultCode.ofFailure();
                }
            }

            final CompletableResultCode exportResult;

            try {
                exportResult = delegate.export(logs);
            } catch (Throwable throwable) {
                Log.w(
                        TAG,
                        "OTLP export threw; batch kept: "
                                + pendingFile.getName(),
                        throwable
                );

                return CompletableResultCode.ofFailure();
            }

            exportResult.whenComplete(() -> {
                if (exportResult.isSuccess()) {
                    store.deleteBatch(pendingFile);
                } else {
                    Log.w(
                            TAG,
                            "OTLP export failed; batch kept for retry: "
                                    + pendingFile.getName()
                    );
                }
            });

            return exportResult;
        }

        /**
         * Retries previously stored batches.
         * <p>
         * Important behavior:
         * <p>
         * 1. Maximum five network attempts per synchronization cycle.
         * 2. Stops after the first endpoint/network failure.
         * 3. A broken endpoint therefore does not cause hundreds of sequential
         * 10-second timeouts.
         */
        int retryPendingBatches() {
            synchronized (retryLock) {
                List<File> pendingFiles = store.listBatchFiles();

                long currentTime = System.currentTimeMillis();

                int networkAttempts = 0;

                for (File pendingFile : pendingFiles) {
                    if (Thread.currentThread().isInterrupted()) {
                        Log.d(
                                TAG,
                                "Pending log retry interrupted"
                        );
                        break;
                    }

                    /*
                     * The original batch export may still be active.
                     */
                    if (currentTime - pendingFile.lastModified()
                            < FRESH_BATCH_AGE_MILLISECONDS) {
                        continue;
                    }

                    if (networkAttempts
                            >= MAX_RETRY_BATCHES_PER_RUN) {
                        Log.d(
                                TAG,
                                "Reached retry limit for this sync cycle"
                        );
                        break;
                    }

                    Collection<LogRecordData> records;

                    try {
                        records = store.readBatch(pendingFile);
                    } catch (Exception exception) {
                        /*
                         * A corrupted file cannot be exported successfully.
                         * Delete it so it does not permanently block all
                         * subsequent pending batches.
                         */
                        Log.e(
                                TAG,
                                "Pending batch is corrupted and will be removed: "
                                        + pendingFile.getName(),
                                exception
                        );

                        store.deleteBatch(pendingFile);
                        continue;
                    }

                    if (records == null || records.isEmpty()) {
                        store.deleteBatch(pendingFile);
                        continue;
                    }

                    networkAttempts++;

                    CompletableResultCode result;

                    try {
                        result = delegate.export(records);

                        /*
                         * This is blocking, but this method only runs on the
                         * telemetry-log-sync background thread.
                         */
                        result.join(
                                RETRY_WAIT_SECONDS,
                                TimeUnit.SECONDS
                        );
                    } catch (Throwable throwable) {
                        Log.w(
                                TAG,
                                "Retry request failed for "
                                        + pendingFile.getName()
                                        + ". Remaining files will wait for "
                                        + "the next synchronization cycle.",
                                throwable
                        );

                        /*
                         * The endpoint is probably unavailable. Do not repeat
                         * the same failure for every remaining file.
                         */
                        break;
                    }

                    if (result.isSuccess()) {
                        store.deleteBatch(pendingFile);

                        Log.d(
                                TAG,
                                "Pending batch exported successfully: "
                                        + pendingFile.getName()
                        );
                    } else {
                        Log.w(
                                TAG,
                                "Retry export failed for "
                                        + pendingFile.getName()
                                        + ". Stopping this synchronization cycle."
                        );

                        /*
                         * All files use the same endpoint. If this one failed,
                         * immediately trying all remaining files is wasteful.
                         */
                        break;
                    }
                }

                return store.pendingFileCount();
            }
        }

        @Override
        public CompletableResultCode flush() {
            try {
                return delegate.flush();
            } catch (Exception exception) {
                Log.w(TAG, "Telemetry exporter flush failed", exception);
                return CompletableResultCode.ofFailure();
            }
        }

        @Override
        public CompletableResultCode shutdown() {
            try {
                return delegate.shutdown();
            } catch (Exception exception) {
                Log.w(
                        TAG,
                        "Telemetry exporter shutdown failed",
                        exception
                );

                return CompletableResultCode.ofFailure();
            }
        }
    }

    // -------------------------------------------------------------------------
    // File-backed pending batch store
    // -------------------------------------------------------------------------

    private static final class OfflineLogStore {

        private final File directory;

        private final Object diskLock = new Object();

        OfflineLogStore(File directory) {
            if (directory == null) {
                throw new IllegalArgumentException(
                        "Pending log directory is required"
                );
            }

            this.directory = directory;

            if (!directory.exists() && !directory.mkdirs()) {
                Log.e(
                        TAG,
                        "Unable to create pending log directory: "
                                + directory.getAbsolutePath()
                );
            }
        }

        private static byte[] readAllBytes(
                File file
        ) throws Exception {
            if (file == null || !file.exists()) {
                throw new IllegalArgumentException(
                        "Pending telemetry file does not exist"
                );
            }

            long fileLength = file.length();

            if (fileLength < 0
                    || fileLength > Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Invalid pending telemetry file size: "
                                + fileLength
                );
            }

            byte[] bytes = new byte[(int) fileLength];

            try (FileInputStream inputStream =
                         new FileInputStream(file)) {

                int offset = 0;

                while (offset < bytes.length) {
                    int read = inputStream.read(
                            bytes,
                            offset,
                            bytes.length - offset
                    );

                    if (read < 0) {
                        break;
                    }

                    offset += read;
                }

                if (offset != bytes.length) {
                    throw new IllegalStateException(
                            "Could not completely read pending telemetry file: "
                                    + file.getName()
                    );
                }
            }

            return bytes;
        }

        private static JSONObject serializeRecord(
                LogRecordData record
        ) throws Exception {
            JSONObject json = new JSONObject();

            json.put(
                    "timestampEpochNanos",
                    Long.toString(
                            record.getTimestampEpochNanos()
                    )
            );

            json.put(
                    "observedTimestampEpochNanos",
                    Long.toString(
                            record.getObservedTimestampEpochNanos()
                    )
            );

            Severity severity = record.getSeverity();

            json.put(
                    "severity",
                    severity == null
                            ? Severity.INFO.name()
                            : severity.name()
            );

            json.put(
                    "severityText",
                    nullToEmpty(
                            record.getSeverityText()
                    )
            );

            json.put(
                    "body",
                    bodyToString(
                            record.getBody()
                    )
            );

            json.put(
                    "attributes",
                    attributesToJson(
                            record.getAttributes()
                    )
            );

            Resource resource = record.getResource();

            json.put(
                    "resourceAttributes",
                    attributesToJson(
                            resource == null
                                    ? Attributes.empty()
                                    : resource.getAttributes()
                    )
            );

            InstrumentationScopeInfo scope =
                    record.getInstrumentationScopeInfo();

            json.put(
                    "scopeName",
                    scope == null
                            ? ""
                            : nullToEmpty(scope.getName())
            );

            json.put(
                    "scopeVersion",
                    scope == null
                            ? ""
                            : nullToEmpty(scope.getVersion())
            );

            return json;
        }

        private static LogRecordData deserializeRecord(
                JSONObject json
        ) throws Exception {
            Attributes resourceAttributes =
                    jsonToAttributes(
                            json.optJSONObject(
                                    "resourceAttributes"
                            )
                    );

            Attributes attributes =
                    jsonToAttributes(
                            json.optJSONObject(
                                    "attributes"
                            )
                    );

            String severityName =
                    json.optString(
                            "severity",
                            Severity.INFO.name()
                    );

            Severity severity;

            try {
                severity = Severity.valueOf(
                        severityName
                );
            } catch (Exception ignored) {
                severity = Severity.INFO;
            }

            String scopeName = json.optString(
                    "scopeName",
                    "telemetry"
            );

            if (scopeName == null
                    || scopeName.trim().isEmpty()) {
                scopeName = "telemetry";
            }

            return new StoredLogRecordData(
                    Resource.create(
                            resourceAttributes
                    ),
                    InstrumentationScopeInfo.create(
                            scopeName,
                            emptyToNull(
                                    json.optString(
                                            "scopeVersion",
                                            ""
                                    )
                            ),
                            null
                    ),
                    parseLong(
                            json.optString(
                                    "timestampEpochNanos",
                                    "0"
                            )
                    ),
                    parseLong(
                            json.optString(
                                    "observedTimestampEpochNanos",
                                    "0"
                            )
                    ),
                    severity,
                    json.optString(
                            "severityText",
                            severity.name()
                    ),
                    Body.string(
                            json.optString(
                                    "body",
                                    ""
                            )
                    ),
                    attributes
            );
        }

        private static String bodyToString(
                Body body
        ) {
            if (body == null) {
                return "";
            }

            try {
                String value = body.asString();

                return value == null
                        ? ""
                        : value;
            } catch (Exception ignored) {
                return String.valueOf(body);
            }
        }

        private static JSONObject attributesToJson(
                Attributes attributes
        ) {
            JSONObject json = new JSONObject();

            if (attributes == null) {
                return json;
            }

            attributes.forEach((key, value) -> {
                try {
                    json.put(
                            key.getKey(),
                            value == null
                                    ? JSONObject.NULL
                                    : String.valueOf(value)
                    );
                } catch (Exception exception) {
                    Log.w(
                            TAG,
                            "Could not serialize telemetry attribute: "
                                    + key.getKey(),
                            exception
                    );
                }
            });

            return json;
        }

        private static Attributes jsonToAttributes(
                JSONObject json
        ) {
            if (json == null) {
                return Attributes.empty();
            }

            AttributesBuilder builder =
                    Attributes.builder();

            Iterator<String> keys = json.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                Object value = json.opt(key);

                if (value == null
                        || value == JSONObject.NULL) {
                    continue;
                }

                builder.put(
                        AttributeKey.stringKey(key),
                        String.valueOf(value)
                );
            }

            return builder.build();
        }

        private static long parseLong(
                String value
        ) {
            try {
                return Long.parseLong(value);
            } catch (Exception ignored) {
                return 0L;
            }
        }

        private static String nullToEmpty(
                String value
        ) {
            return value == null
                    ? ""
                    : value;
        }

        private static String emptyToNull(
                String value
        ) {
            if (value == null
                    || value.trim().isEmpty()) {
                return null;
            }

            return value;
        }

        File writeBatch(
                Collection<LogRecordData> logs
        ) throws Exception {
            synchronized (diskLock) {
                enforceRetentionLimit();

                JSONObject root = new JSONObject();

                JSONArray recordsJson = new JSONArray();

                for (LogRecordData record : logs) {
                    if (record != null) {
                        recordsJson.put(
                                serializeRecord(record)
                        );
                    }
                }

                root.put("records", recordsJson);

                String fileName =
                        "pending_"
                                + System.currentTimeMillis()
                                + "_"
                                + UUID.randomUUID()
                                + ".json";

                File target = new File(
                        directory,
                        fileName
                );

                File temporary = new File(
                        directory,
                        fileName + ".tmp"
                );

                byte[] bytes = root.toString()
                        .getBytes(StandardCharsets.UTF_8);

                try (FileOutputStream outputStream =
                             new FileOutputStream(temporary)) {

                    outputStream.write(bytes);
                    outputStream.flush();

                    /*
                     * Request that written bytes are synchronized to storage.
                     */
                    outputStream.getFD().sync();
                }

                if (!temporary.renameTo(target)) {
                    /*
                     * Fallback for devices/filesystems where rename fails.
                     */
                    try (FileOutputStream outputStream =
                                 new FileOutputStream(target)) {

                        outputStream.write(bytes);
                        outputStream.flush();
                        outputStream.getFD().sync();
                    }

                    if (!temporary.delete()) {
                        Log.w(
                                TAG,
                                "Could not remove temporary telemetry file: "
                                        + temporary.getName()
                        );
                    }
                }

                return target;
            }
        }

        Collection<LogRecordData> readBatch(
                File file
        ) throws Exception {
            synchronized (diskLock) {
                byte[] bytes = readAllBytes(file);

                JSONObject root = new JSONObject(
                        new String(
                                bytes,
                                StandardCharsets.UTF_8
                        )
                );

                JSONArray recordsJson =
                        root.getJSONArray("records");

                List<LogRecordData> records =
                        new ArrayList<>(recordsJson.length());

                for (int index = 0;
                     index < recordsJson.length();
                     index++) {

                    records.add(
                            deserializeRecord(
                                    recordsJson.getJSONObject(index)
                            )
                    );
                }

                return records;
            }
        }

        List<File> listBatchFiles() {
            synchronized (diskLock) {
                return listBatchFilesUnlocked();
            }
        }

        int pendingFileCount() {
            synchronized (diskLock) {
                return listBatchFilesUnlocked().size();
            }
        }

        void deleteBatch(File file) {
            synchronized (diskLock) {
                if (file == null || !file.exists()) {
                    return;
                }

                if (!file.delete()) {
                    Log.w(
                            TAG,
                            "Failed to delete pending telemetry batch: "
                                    + file.getAbsolutePath()
                    );
                }
            }
        }

        private void enforceRetentionLimit() {
            List<File> pendingFiles =
                    listBatchFilesUnlocked();

            while (pendingFiles.size()
                    >= MAX_PENDING_BATCH_FILES) {

                File oldest = pendingFiles.remove(0);

                if (!oldest.delete()) {
                    Log.w(
                            TAG,
                            "Could not delete oldest pending batch: "
                                    + oldest.getName()
                    );

                    break;
                }

                Log.w(
                        TAG,
                        "Dropped oldest pending batch to stay under limit: "
                                + oldest.getName()
                );
            }
        }

        private List<File> listBatchFilesUnlocked() {
            File[] files = directory.listFiles(
                    (dir, name) ->
                            name.startsWith("pending_")
                                    && name.endsWith(".json")
            );

            if (files == null || files.length == 0) {
                return new ArrayList<>();
            }

            List<File> pendingFiles =
                    new ArrayList<>();

            Collections.addAll(
                    pendingFiles,
                    files
            );

            /*
             * File names start with the creation timestamp, so normal string
             * ordering places the oldest batch first.
             */
            Collections.sort(
                    pendingFiles,
                    (left, right) ->
                            left.getName()
                                    .compareTo(right.getName())
            );

            return pendingFiles;
        }
    }

    /**
     * Minimal LogRecordData implementation used for re-exporting records
     * restored from offline JSON files.
     */
    private static final class StoredLogRecordData
            implements LogRecordData {

        private final Resource resource;

        private final InstrumentationScopeInfo instrumentationScopeInfo;

        private final long timestampEpochNanos;

        private final long observedTimestampEpochNanos;

        private final Severity severity;

        private final String severityText;

        private final Body body;

        private final Attributes attributes;

        StoredLogRecordData(
                Resource resource,
                InstrumentationScopeInfo instrumentationScopeInfo,
                long timestampEpochNanos,
                long observedTimestampEpochNanos,
                Severity severity,
                String severityText,
                Body body,
                Attributes attributes
        ) {
            this.resource = resource;
            this.instrumentationScopeInfo =
                    instrumentationScopeInfo;
            this.timestampEpochNanos =
                    timestampEpochNanos;
            this.observedTimestampEpochNanos =
                    observedTimestampEpochNanos;
            this.severity = severity;
            this.severityText = severityText;
            this.body = body;
            this.attributes = attributes == null
                    ? Attributes.empty()
                    : attributes;
        }

        @Override
        public Resource getResource() {
            return resource;
        }

        @Override
        public InstrumentationScopeInfo
        getInstrumentationScopeInfo() {
            return instrumentationScopeInfo;
        }

        @Override
        public long getTimestampEpochNanos() {
            return timestampEpochNanos;
        }

        @Override
        public long getObservedTimestampEpochNanos() {
            return observedTimestampEpochNanos;
        }

        @Override
        public SpanContext getSpanContext() {
            return SpanContext.getInvalid();
        }

        @Override
        public Severity getSeverity() {
            return severity;
        }

        @Override
        public String getSeverityText() {
            return severityText;
        }

        @Override
        public Body getBody() {
            return body;
        }

        @Override
        public Attributes getAttributes() {
            return attributes;
        }

        @Override
        public int getTotalAttributeCount() {
            return attributes.size();
        }
    }
}
