package com.paymentsave.paymentsave.coreapp.receiver;


import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_ZREPORT;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_RECONCILIATION;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.eft.libpositive.PosIntegrate;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.network.WebSocketUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogisticService extends Service {

    private static final String TAG = "LogisticService";
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private final Handler handler = new Handler();
    private final Handler batchHandler = new Handler();
    private final int interval_second = 60000;
    private final int interval = 20000;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (SharedHelper.getBooleanData(getApplicationContext(), AppConstants.SharedPref.INTEGRATION_MODE)) {
            try {
                // Close all previous connections
                WebSocketUtils.closeConnection();
            } catch (Exception e) {
                e.printStackTrace();
                // Telemetry: LogisticService.onStartCommand — failed to close previous websocket.
                logReceiveError("onStartCommand", String.valueOf(e.getMessage()), e);
            }
            // Establish new Connection
            WebSocketUtils.connectWebSocket(getApplicationContext());
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Logistic Service Running")
                .setContentText("Monitoring data transmission")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        startForeground(1, notification);

        handler.post(runnableCode);
        batchHandler.post(batchRunnableCode);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
        batchHandler.removeCallbacks(batchRunnableCode);

        // Cancel the notification to remove the badge
        NotificationManager manager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (manager != null) {
            manager.cancel(1);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void callApi() {
        String next_batch = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME);
        if (next_batch.isEmpty()) {
            Log.d(TAG, "callApi => " + "Checking batch time");
            String token = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.TOKEN);
            String mid = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.MID);
            String tid = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.TID);
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(BuildConfig.BASE_URL + "api/v1/transaction/terminal/auto-batch-queue/?mid=" + mid + "&tid=" + tid)
                    .addHeader("Authorization", "Corona " + token)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e(TAG, "API call failed: " + e.getMessage());
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseBody);

                            // Parse the "data" JSONObject
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            JSONArray resultsArray = dataObject.getJSONArray("results");
                            if (resultsArray.length() > 0) {
                                JSONObject result = resultsArray.getJSONObject(0);
                                String reportTime = result.getString("report_time");
                                SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, reportTime);
                            } else {
                                SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, "");
                            }
                        } catch (Exception e) {
                            Log.e("MyForegroundService", "JSON parsing error: " + e.getMessage());
                            // Telemetry: LogisticService.callApi.onResponse — failed to parse auto-batch queue JSON.
                            logReceiveError("callApi.onResponse", String.valueOf(e.getMessage()), e);
                        }
                    } else {
                        Log.e(TAG, "Error: " + response.code());
                    }
                }
            });
        }
    }

    private boolean compareReportTimeWithCurrentTime(String reportTime) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                // Define the format of the date and time, including the time zone if needed
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

                // Parse the input date-time string to a LocalDateTime object
                LocalDateTime inputDateTime = LocalDateTime.parse(reportTime, formatter)
                        .withSecond(0)
                        .withNano(0); // Optionally reset nanoseconds too

                // Specify the UK time zone (Europe/London)
                ZoneId ukZone = ZoneId.of("Europe/London");
                // Get the current date-time and set seconds to 0
                LocalDateTime currentDateTime = LocalDateTime.now(ukZone)
                        .withSecond(0)
                        .withNano(0);

                // Extract the year, month, day, hour, and minute for comparison
                boolean isSameDate = inputDateTime.equals(currentDateTime);
                boolean isTimePassed = !inputDateTime.isAfter(currentDateTime);

                if (isSameDate || isTimePassed) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                // Telemetry: LogisticService.compareReportTimeWithCurrentTime — failed to parse report time.
                logReceiveError("compareReportTimeWithCurrentTime", String.valueOf(e.getMessage()), e);
                return false;
            }
        }
        return false;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );

            // Optionally, disable the badge
            serviceChannel.setShowBadge(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private void executeBatch() {
        if (!SharedHelper.getToken(getApplicationContext()).isEmpty()) {
            try {
                // Execute Report
                HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args.put(CT_ZREPORT, "true");
                PosIntegrate.executeReport(getApplicationContext(), TRANSACTION_TYPE_RECONCILIATION, args);
            } catch (Exception e) {
                // Telemetry: LogisticService.executeBatch — Z report failed to start.
                logReceiveError("executeBatch", String.valueOf(e.getMessage()), e);
            }
        }
    }

    private final Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Call your API here
            callApi();

            // Repeat this the same runnable code block after the interval
            handler.postDelayed(runnableCode, interval);
        }
    };


    private final Runnable batchRunnableCode = new Runnable() {
        @Override
        public void run() {
            // Call your API here
            String nextBatch = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME);
            if (!nextBatch.isEmpty()) {
                boolean isExecutable = compareReportTimeWithCurrentTime(nextBatch);
                if (isExecutable) {
                    SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, "");
                    executeBatch();
                }
            }

            // Repeat this the same runnable code block after the interval
            batchHandler.postDelayed(batchRunnableCode, interval_second);
        }
    };

    /**
     * Sends a service failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code LogisticService.callApi.onResponse}.
     * {@code message} must already be a string.
     */
    private void logReceiveError(String functionPath, String message, Throwable throwable) {
        Context context = getApplicationContext();
        String eventName = TAG + "." + functionPath;
        TelemetryLogger.error(
                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                eventName,
                message != null ? message : "",
                throwable
        );
    }
}