package com.paymentsave.paymentsave.coreapp.receiver;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_CASHBACK;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_GRATUITY;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_XREPORT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_ZREPORT;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_RECONCILIATION;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_REFUND;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_SALE;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.eft.libpositive.PosIntegrate;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.activities.splash.SplashActivity;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.Constants;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.request_models.TransactionRequest;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.*;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AwsIotTerminalClient {
    private static final String TAG = "AwsIotTerminal";
    public static MqttAndroidClient client;
    public static String responseTopic;
    // Prevents an old Activity's disconnect() from tearing down a newer instance's MQTT client
    private static volatile AwsIotTerminalClient activeInstance;
    private final Context context;
    private final String endpoint;      // e.g. a8yske4...-ats.iot.eu-west-2.amazonaws.com
    private final int port;             // 8883
    private final String terminalId;    // 210206602202
    private final String commandTopic;
    private final Object executorLock = new Object();
    public volatile boolean isConnected = false; // Track subscription state to prevent duplicates
    // Not final: disconnect() shuts these down; connectAndListen() must be able to recreate them
    private ExecutorService worker = Executors.newSingleThreadExecutor();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean isSubscribed = false; // Track subscription state to prevent duplicates
    // True after disconnect(); blocks late MQTT callbacks from submitting to a terminated pool
    private volatile boolean isReleased = false;
    private ScheduledFuture<?> keepAliveTask; // Task to periodically check connection health
    private ScheduledFuture<?> subscribeDelayTask; // Replaces Thread.sleep so disconnect cancels cleanly

    public AwsIotTerminalClient(Context context, String endpoint, int port, String terminalId) {
        this.context = context.getApplicationContext();
        this.endpoint = endpoint;
        this.port = port;
        this.terminalId = terminalId;

        this.commandTopic = "ps/" + terminalId + "/command";
        this.responseTopic = "ps/" + terminalId + "/response";
    }

//    public static MqttAndroidClient getClient(Context context) {
//        if (client != null)
//            return client;
//        else
//            return null;
//    }

    /**
     * Builds an SSLSocketFactory for AWS IoT mTLS:
     * - Trust store contains AmazonRootCA1
     */
    public static SSLSocketFactory getSocketFactory(
            InputStream caCrtFile,
            InputStream clientCrtFile,
            InputStream clientKeyFile,
            String keyPassword // can be any value; used to protect key inside the in-memory keystore
    ) throws Exception {

        // Ensure BC provider available
//        if (Security.getProvider("BC") == null) {
//            Security.addProvider(new BouncyCastleProvider());
//        }

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        // Read CA cert (single cert file)
        X509Certificate caCert;
        try (BufferedInputStream bis = new BufferedInputStream(caCrtFile)) {
            caCert = (X509Certificate) cf.generateCertificate(bis);
        }

        // Read client cert
        X509Certificate clientCert;
        try (BufferedInputStream bis = new BufferedInputStream(clientCrtFile)) {
            clientCert = (X509Certificate) cf.generateCertificate(bis);
        }

        // Read private key (PKCS#1 or PKCS#8)
        PrivateKey privateKey = loadPrivateKeyFromPem(clientKeyFile);

        // --- TrustStore with CA ---
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        trustStore.setCertificateEntry("amazonRootCA", caCert);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        // --- KeyStore with client cert + private key ---
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);

        keyStore.setKeyEntry(
                "client",
                privateKey,
                keyPassword.toCharArray(),
                new java.security.cert.Certificate[]{clientCert}
        );

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyPassword.toCharArray());

        // --- SSLContext ---
        // AWS IoT requires TLS 1.2 or higher. Use "TLS" which allows negotiation of TLS 1.2+
        // For Android, we can also try "TLSv1.2" explicitly if needed
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        // Configure SSL parameters to ensure TLS 1.2+ is used
        // Note: The socket factory will use these settings when creating sockets

        return sslContext.getSocketFactory();
    }

    private static PrivateKey loadPrivateKeyFromPem(InputStream keyFile) throws Exception {
        try (PEMParser pemParser = new PEMParser(new InputStreamReader(keyFile))) {
            Object obj = pemParser.readObject();
            if (obj == null) {
                throw new IllegalArgumentException("Empty PEM key file");
            }

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            // PKCS#1 format: -----BEGIN RSA PRIVATE KEY----- -> PEMKeyPair
            if (obj instanceof PEMKeyPair) {
                KeyPair kp = converter.getKeyPair((PEMKeyPair) obj);
                return kp.getPrivate();
            }

            // PKCS#8 format: -----BEGIN PRIVATE KEY----- -> PrivateKeyInfo
            if (obj instanceof PrivateKeyInfo) {
                return converter.getPrivateKey((PrivateKeyInfo) obj);
            }

            // Some keys may be wrapped differently; fail loudly with type info
            throw new IllegalArgumentException("Unsupported PEM object: " + obj.getClass().getName());
        }
    }

    public static void publishResponse(String payload) {
        try {
            MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            msg.setQos(1);

            client.publish(responseTopic, msg, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Log.i(TAG, "Published RESPONSE to " + responseTopic + " payload=" + payload);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Log.e(TAG, "Publish failed", exception);
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "Publish exception", e);
            // Telemetry: AwsIotTerminalClient.publishResponse — MQTT publish failed.
            logReceiveError(null, "publishResponse", String.valueOf(e.getMessage()), e);
        }
    }

    /**
     * Recreate worker/scheduler if they were shut down by a previous disconnect().
     * Without this, connectComplete/messageArrived crash with RejectedExecutionException.
     * Never recreates while released — that would race with disconnect().
     */
    private void ensureExecutorsAlive() {
        synchronized (executorLock) {
            if (isReleased) {
                return;
            }
            if (worker == null || worker.isShutdown()) {
                worker = Executors.newSingleThreadExecutor();
            }
            if (scheduler == null || scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
            }
        }
    }


    /**
     * Submit work safely. Late MQTT callbacks after disconnect() must not crash the main thread.
     */
    private void submitWork(Runnable task) {
        synchronized (executorLock) {
            if (isReleased) {
                Log.w(TAG, "Skipping work: client already released");
                return;
            }
            if (worker == null || worker.isShutdown()) {
                worker = Executors.newSingleThreadExecutor();
            }
            try {
                worker.submit(task);
            } catch (RejectedExecutionException e) {
                // Race: disconnect() shut the pool down between the check and submit
                Log.w(TAG, "Worker rejected task (executor terminated)", e);
            }
        }
    }

    /**
     * Delay subscribe with the scheduler instead of Thread.sleep.
     * disconnect()/shutdownNow() cancels this cleanly — no InterruptedException noise.
     */
    private void scheduleSubscribeAttempt(long delayMs) {
        synchronized (executorLock) {
            if (isReleased) {
                return;
            }
            if (scheduler == null || scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
            }
            if (subscribeDelayTask != null && !subscribeDelayTask.isDone()) {
                subscribeDelayTask.cancel(false);
            }
            try {
                subscribeDelayTask = scheduler.schedule(() -> {
                    if (isReleased || activeInstance != this) {
                        return;
                    }
                    if (client != null && client.isConnected() && !isSubscribed) {
                        subscribeToCommands();
                    } else if (isSubscribed) {
                        Log.d(TAG, "Already subscribed, skipping duplicate subscription");
                    } else {
                        Log.w(TAG, "Client not connected when attempting subscription");
                    }
                }, delayMs, TimeUnit.MILLISECONDS);
            } catch (RejectedExecutionException e) {
                Log.w(TAG, "Scheduler rejected subscribe delay (executor terminated)", e);
            }
        }
    }

    private void closeQuietly(MqttAndroidClient mqttClient) {
        if (mqttClient == null) {
            return;
        }
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (Exception e) {
            Log.w(TAG, "Previous MQTT disconnect failed: " + e.getMessage());
        }
        try {
            mqttClient.unregisterResources();
            mqttClient.close();
        } catch (Exception e) {
            Log.w(TAG, "Previous MQTT close failed: " + e.getMessage());
        }
    }

    public void connectAndListen() throws Exception {
        Log.d(TAG, "connectAndListen: ");
        synchronized (executorLock) {
            isReleased = false;
            if (worker == null || worker.isShutdown()) {
                worker = Executors.newSingleThreadExecutor();
            }
            if (scheduler == null || scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(1);
            }
        }

        // Same Activity recreation can call this again while still connected — avoid thrashing
        if (activeInstance == this && client != null && client.isConnected()) {
            Log.d(TAG, "Already connected, skipping duplicate connectAndListen");
            if (!isSubscribed) {
                scheduleSubscribeAttempt(0);
            }
            return;
        }

        activeInstance = this;
        isSubscribed = false;

        // Close any previous shared client before replacing it (static field)
        MqttAndroidClient previousClient = client;
        if (previousClient != null) {
            closeQuietly(previousClient);
            client = null;
        }

        String serverUri = "ssl://" + endpoint + ":" + port;

        client = new MqttAndroidClient(context, serverUri, terminalId);
        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (isReleased || activeInstance != AwsIotTerminalClient.this) {
                    Log.w(TAG, "connectComplete ignored: client already released or superseded");
                    return;
                }
                isConnected = true;
                // Log.i(TAG, "Connected. reconnect=" + reconnect + " uri=" + serverURI);
                // Reset subscription flag on reconnect
                if (reconnect) {
                    isSubscribed = false;
                }
                // Subscribe to commands topic - this is critical to maintain the connection
                // Without subscription, AWS IoT may close idle connections
                // Small delay so the session is fully ready (cancelable — no Thread.sleep)
                scheduleSubscribeAttempt(500);
            }

            @Override
            public void connectionLost(Throwable cause) {
                isConnected = false;
                isSubscribed = false; // Reset subscription flag on disconnection
                // stopKeepAliveMonitor(); // Stop monitoring when disconnected
                Log.w(TAG, "Connection lost", cause);
                if (cause != null) {
                    Log.w(TAG, "Connection lost reason: " + cause.getClass().getSimpleName() + " - " + cause.getMessage());
                    if (cause instanceof java.io.EOFException) {
                        Log.w(TAG, "EOFException typically indicates server closed connection - check keepalive, SSL/TLS, or AWS IoT policies");
                    }
                }
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                if (isReleased || activeInstance != AwsIotTerminalClient.this) {
                    return;
                }
                String raw = new String(message.getPayload(), StandardCharsets.UTF_8);
                // Log.i(TAG, "COMMAND on " + topic + ": " + raw);

                // Never block Paho callback thread with sleep; do work on a worker thread
                submitWork(() -> handleCommand(raw));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Optional: log delivery complete
            }
        });

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(20);
        options.setKeepAliveInterval(60);
        // Set MQTT version to match Python client (MQTTv311)
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        // Set max inflight to prevent message queue issues
        options.setMaxInflight(10);

        // mTLS socket factory
        SSLSocketFactory sslSocketFactory = getSocketFactory(
                context.getResources().openRawResource(R.raw.amazon_root_ca1_uat), //test
                context.getResources().openRawResource(R.raw.certificate_uat),   // certificate.pem.crt
                context.getResources().openRawResource(R.raw.private_key_uat),    // private.pem.key
                "changeit"
        );
        options.setSocketFactory(sslSocketFactory);
        client.connect(options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                // Log.i(TAG, "MQTT connect success");
                // Note: Subscription will happen in connectComplete callback
                // to avoid duplicate subscriptions
                triggerHeartBeat();
            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.e(TAG, "MQTT connect failed: " + (exception != null ? exception.getMessage() : "null"));
                if (exception != null) Log.e(TAG, "MQTT connect failed", exception);

                // Also log token/return codes (sometimes helps)
                if (asyncActionToken != null && asyncActionToken.getException() != null) {
                    Log.e(TAG, "Token exception", asyncActionToken.getException());
                }
            }
        });
    }

    private void subscribeToCommands() {
        if (client == null) {
            Log.e(TAG, "Cannot subscribe: client is null");
            return;
        }

        if (!client.isConnected()) {
            Log.w(TAG, "Cannot subscribe: client is not connected");
            return;
        }

        if (isSubscribed) {
            Log.d(TAG, "Already subscribed, skipping");
            return;
        }

        try {

            // Log.i(TAG, "Attempting to subscribe to " + commandTopic);
            client.subscribe(commandTopic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isSubscribed = true;
                    // Log.i(TAG, "Successfully subscribed");
                    // Start periodic connection health check to ensure keepalive is working
                    startKeepAliveMonitor();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    isSubscribed = false; // Reset flag on failure
                    Log.e(TAG, "Subscribe failed", exception);
                    // Retry subscription after a delay if it fails (cancelable schedule, not Thread.sleep)
                    if (client != null && client.isConnected()) {
                        scheduleSubscribeAttempt(2000);
                    }
                }
            });
        } catch (MqttException e) {
            Log.e(TAG, "Subscribe exception :", e);
            // Telemetry: AwsIotTerminalClient.subscribeToCommands — MQTT subscribe failed.
            logReceiveError(context, "subscribeToCommands", String.valueOf(e.getMessage()), e);
        }
    }

    private void handleCommand(String raw) {
        try {
            JSONObject cmd = new JSONObject(raw);
            JSONObject data = cmd.optJSONObject("data");
            String sessionId = cmd.optString("session_id", "");
            if (!TextUtils.isEmpty(sessionId)) {
                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, sessionId);
            }

            String deviceUid = cmd.optString("device_uid", "");
            if (!TextUtils.isEmpty(deviceUid)) {
                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_DEVICE_UID, deviceUid);
            }

            String type = String.valueOf(
                    cmd.optString("event", "")
            );
            HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
            switch (type) {
                case "CONNECT_PING":
                    try {

                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_PING — incoming ping payload.
                        TelemetryLogger.info(
                                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                "AwsIotTerminalClient.handleCommand.CONNECT_PING",
                                String.valueOf(data != null ? data : cmd)
                        );

                        JSONObject resp = new JSONObject();
                        resp.put("tid", SharedHelper.getStringData(context, AppConstants.SharedPref.TID));
                        resp.put("status", "CONNECTED");
                        publishResponse(resp.toString());
                    } catch (JSONException e) {
                        Log.e(TAG, "CONNECT_PING JSON failed", e);
                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_PING — failed to build ping response.
                        logReceiveError(context, "handleCommand.CONNECT_PING", String.valueOf(e.getMessage()), e);
                    }
                    break;
                case "CONNECT_RUN_SALE":
                    try {

                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_SALE — incoming sale payload.
                        TelemetryLogger.info(
                                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                "AwsIotTerminalClient.handleCommand.CONNECT_RUN_SALE",
                                String.valueOf(data)
                        );

                        String disablePrint = Boolean.toString(!data.optString("print_receipt", "false").equals("true"));
                        args.put(CT_AMOUNT, data.optString("amount"));
                        args.put(CT_AMOUNT_GRATUITY, data.optString("gratuity", "0"));
                        args.put(CT_AMOUNT_CASHBACK, data.optString("cashback", "0"));
                        args.put(CT_DISABLEPRINTING, disablePrint);
                        args.put(CT_LANGUAGE, "en_GB");
                        PosIntegrate.executeTransaction(context, TRANSACTION_TYPE_SALE, args);

                    } catch (Exception e) {
                        Log.d(TAG, "Run Sale Exception: " + e);
                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_SALE — sale transaction failed to start.
                        logReceiveError(context, "handleCommand.CONNECT_RUN_SALE", String.valueOf(e.getMessage()), e);
                    }
                    break;

                case "CONNECT_RUN_REFUND":
                    try {

                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_REFUND — incoming refund payload.
                        TelemetryLogger.info(
                                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                "AwsIotTerminalClient.handleCommand.CONNECT_RUN_REFUND",
                                String.valueOf(data)
                        );

                        String disablePrint = Boolean.toString(!data.optString("print_receipt", "false").equals("true"));
                        HomeActivity.refundUti = data.optString("refund_uti");
                        args.put(CT_AMOUNT, data.optString("amount"));
                        args.put(CT_DISABLEPRINTING, disablePrint);
                        args.put(CT_LANGUAGE, "en_GB");
                        PosIntegrate.executeTransaction(context, TRANSACTION_TYPE_REFUND, args);
                    } catch (Exception e) {
                        Log.d(TAG, "Run Refund Exception: " + e);
                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_REFUND — refund transaction failed to start.
                        logReceiveError(context, "handleCommand.CONNECT_RUN_REFUND", String.valueOf(e.getMessage()), e);
                    }

                    break;
                case "CONNECT_RUN_XREPORT":
                    try {

                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_XREPORT — incoming X report payload.
                        TelemetryLogger.info(
                                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                "AwsIotTerminalClient.handleCommand.CONNECT_RUN_XREPORT",
                                String.valueOf(data)
                        );

                        args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                        args.put(CT_DISABLEPRINTING, Boolean.toString(!data.optString("print_receipt", "false").equals("true")));
                        args.put(CT_XREPORT, "true");
                        PosIntegrate.executeReport(context, TRANSACTION_TYPE_RECONCILIATION, args);
                    } catch (Exception e) {
                        Log.e(TAG, "CONNECT_RUN_XREPORT failed", e);
                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_XREPORT — X report failed to start.
                        logReceiveError(context, "handleCommand.CONNECT_RUN_XREPORT", String.valueOf(e.getMessage()), e);
                    }
                    break;

                case "CONNECT_RUN_ZREPORT":
                    try {

                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_ZREPORT — incoming Z report payload.
                        TelemetryLogger.info(
                                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                "AwsIotTerminalClient.handleCommand.CONNECT_RUN_ZREPORT",
                                String.valueOf(data)
                        );

                        args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                        args.put(CT_DISABLEPRINTING, Boolean.toString(!data.optString("print_receipt", "false").equals("true")));
                        args.put(CT_ZREPORT, "true");
                        PosIntegrate.executeReport(context, TRANSACTION_TYPE_RECONCILIATION, args);
                    } catch (Exception e) {
                        Log.e(TAG, "CONNECT_RUN_ZREPORT failed", e);
                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_RUN_ZREPORT — Z report failed to start.
                        logReceiveError(context, "handleCommand.CONNECT_RUN_ZREPORT", String.valueOf(e.getMessage()), e);
                    }
                    break;
                case "CONNECT_CANCEL_TRANSACTION":
                    try {

                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_CANCEL_TRANSACTION — incoming cancel payload.
                        TelemetryLogger.info(
                                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                "AwsIotTerminalClient.handleCommand.CONNECT_CANCEL_TRANSACTION",
                                String.valueOf(data)
                        );

                        PosIntegrate.cancelTransaction(context);
                    } catch (Exception e) {
                        e.printStackTrace();

                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_CANCEL_TRANSACTION — cancel failed.
                        logReceiveError(context, "handleCommand.CONNECT_CANCEL_TRANSACTION", String.valueOf(e.getMessage()), e);
                    }
                    break;
                case "CONNECT_REPRINT_RECEIPT":
                    try {

                        /// Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_REPRINT_RECEIPT — incoming reprint payload.
                        TelemetryLogger.info(
                                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                "AwsIotTerminalClient.handleCommand.CONNECT_REPRINT_RECEIPT",
                                String.valueOf(data)
                        );

                        String tid = data.getString("tid");
                        if (data.has("transaction")) {
                            JSONObject trsObject = data.getJSONObject("transaction");
                            TransactionRequest transactionRequest = new TransactionRequest(
                                    trsObject.getInt("id"),
                                    trsObject.getString("created_at"),
                                    "",
                                    trsObject.getString("auth_code"),
                                    trsObject.getString("response_code"),
                                    "",
                                    trsObject.getString("receipt_id"),
                                    trsObject.getString("transaction_type"),
                                    trsObject.getString("pan"),
                                    trsObject.getString("uti"),
                                    trsObject.getString("amount"),
                                    trsObject.getString("cashback_amount"),
                                    trsObject.getString("gratuity_amount"),
                                    trsObject.getString("discount"),
                                    trsObject.getBoolean("approved"),
                                    trsObject.getBoolean("cancelled"),
                                    trsObject.getBoolean("sig_required"),
                                    trsObject.getBoolean("pin_verified"),
                                    trsObject.getString("currency"),
                                    trsObject.getString("terminal_id"),
                                    trsObject.getString("merchant_uid"),
                                    trsObject.getString("psn_code"),
                                    trsObject.getString("card_type"),
                                    trsObject.getInt("status"),
                                    0
                            );

                            if (!trsObject.getString("error_text").isEmpty() && trsObject.getString("error_text").contains(",")) {
                                String[] err = trsObject.getString("error_text").split(",");
                                if (err.length > 1)
                                    transactionRequest.setErrorText(err[1].trim());
                            }

                            PrintUtils.printReceiptCopy(context, transactionRequest, "CARDHOLDER");
                        } else {
                            Log.e(TAG, "onTextReceived: " + "No 'transaction' key found in receiptData.");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "onTextReceived: " + e.getMessage());
                        // Telemetry: AwsIotTerminalClient.handleCommand.CONNECT_REPRINT_RECEIPT — reprint failed.
                        logReceiveError(context, "handleCommand.CONNECT_REPRINT_RECEIPT", String.valueOf(e.getMessage()), e);
                    }
                    break;
                default:
                    Log.e(TAG, "handleCommand: Invalid Event");
            }

//            Log.i(TAG, "Processing payment for 10 seconds...");
//            Thread.sleep(10_000);
//            publishResponse(resp.toString());

        } catch (Exception e) {
            Log.e(TAG, "Command handling failed", e);
            /// Telemetry: AwsIotTerminalClient.handleCommand — unexpected command handling failure.
            logReceiveError(context, "handleCommand", String.valueOf(e.getMessage()), e);
        }
    }

    private void startKeepAliveMonitor() {
        if (isReleased) {
            return;
        }
        // Stop any existing monitor
        stopKeepAliveMonitor();

        try {
            ensureExecutorsAlive();
            // Check connection health every 30 seconds (half of keepalive interval)
            // This helps ensure the connection is being maintained
            keepAliveTask = scheduler.scheduleWithFixedDelay(() -> {
                if (isReleased) {
                    return;
                }
                if (client != null) {
                    triggerHeartBeat();
                    isConnected = client.isConnected();
                    if (!isConnected && isSubscribed) {
                        Log.w(TAG, "Connection lost but subscription flag still set - resetting");
                        isSubscribed = false;
                    }
                }
            }, 10, 10, TimeUnit.SECONDS);
        } catch (RejectedExecutionException e) {

            Log.w(TAG, "Scheduler rejected keep-alive task (executor terminated)", e);
            /// Telemetry: AwsIotTerminalClient.startKeepAliveMonitor — scheduler rejected the keep-alive task.
            logReceiveError(context, "startKeepAliveMonitor", String.valueOf(e.getMessage()), e);
        }
    }

    private void triggerHeartBeat() {
        String token = SharedHelper.getStringData(context, AppConstants.SharedPref.TOKEN);
        String tid = SharedHelper.getStringData(context, AppConstants.SharedPref.TID); // or wherever you store it

        RequestBody formBody = new FormBody.Builder()
                .add("tid", tid)
                .build();

        Request request = new Request.Builder()
                .url(Constants.BASE_URL + "api/v1/account/terminal/heartbeat/")
                .addHeader("Authorization", "Corona " + token)
                .post(formBody)
                .build();

        OkHttpClient httpClient = new OkHttpClient();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody body = response.body()) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "triggered live status");
                    } else {
                        Log.e(TAG, "Error: " + response.code() + " -> " + (body != null ? body.string() : ""));
                        if (response.code() == 401) {
                            SharedHelper.getLogOut(context);
                            Intent intent = new Intent(context, SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    }
                }
            }
        });
    }


    private void stopKeepAliveMonitor() {
        if (keepAliveTask != null && !keepAliveTask.isCancelled()) {
            keepAliveTask.cancel(true);
            keepAliveTask = null;
        }
    }

    public void disconnect() {
        // Mark released under lock so late MQTT callbacks cannot recreate/submit on the pools
        synchronized (executorLock) {
            isReleased = true;
            if (subscribeDelayTask != null && !subscribeDelayTask.isDone()) {
                subscribeDelayTask.cancel(false);
                subscribeDelayTask = null;
            }
            isConnected = false;
        }
        stopKeepAliveMonitor();

        // Only the active instance may tear down the shared static MQTT client.
        // Otherwise Activity A onDestroy disconnects Activity B's freshly created connection.
        boolean ownsSharedClient = (activeInstance == this);
        try {
            if (ownsSharedClient && client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            Log.e(TAG, "Disconnect error", e);

            /// Telemetry: AwsIotTerminalClient.disconnect — MQTT disconnect failed.
            logReceiveError(context, "disconnect", String.valueOf(e.getMessage()), e);
        } finally {
            isSubscribed = false;
            if (ownsSharedClient) {
                activeInstance = null;
            }
            synchronized (executorLock) {
                if (worker != null && !worker.isShutdown()) {
                    worker.shutdownNow();
                }
                if (scheduler != null && !scheduler.isShutdown()) {
                    scheduler.shutdownNow();
                }
            }
        }
    }


    /**
     * Sends a client failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code AwsIotTerminalClient.handleCommand.CONNECT_RUN_SALE}.
     * {@code message} must already be a string.
     */
    private static void logReceiveError(
            Context context,
            String functionPath,
            String message,
            Throwable throwable
    ) {
        Context telemetryContext = context;
        if (telemetryContext == null && activeInstance != null) {
            telemetryContext = activeInstance.context;
        }

        String tid = "";
        if (telemetryContext != null) {
            tid = SharedHelper.getStringData(
                    telemetryContext,
                    AppConstants.SharedPref.TID
            );
        }

        String eventName = "AwsIotTerminalClient." + functionPath;
        TelemetryLogger.error(
                tid,
                eventName,
                message != null ? message : "",
                throwable
        );
    }
}