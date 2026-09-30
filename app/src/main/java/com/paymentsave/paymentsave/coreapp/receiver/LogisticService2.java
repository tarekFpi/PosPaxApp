//package com.paymentsave.paymentsave.coreapp.receiver;
//
//
//import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_ZREPORT;
//import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_RECONCILIATION;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.core.app.NotificationCompat;
//
//import com.eft.libpositive.PosIntegrate;
//import com.paymentsave.paymentsave.AppConstants;
//import com.paymentsave.paymentsave.R;
//import com.paymentsave.paymentsave.network.SharedHelper;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class LogisticService2 extends Service {
//
//    private static final String CHANNEL_ID = "ForegroundServiceChannel";
//    private static final String TAG = "LogisticService";
//    private final Handler handler = new Handler();
//    private final Handler batchHandler = new Handler();
//
//    //    private final int interval = 3600000; // 1 hour in milliseconds
//    // private final int interval = 20000; // 20 seconds
//    private final int interval_second = 60000; // per minute
//    private final int interval = 20000; // 10 seconds
//
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        createNotificationChannel();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Logistic Service Running")
//                .setContentText("Monitoring data transmission")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build();
//
//        startForeground(1, notification);
//
//        handler.post(runnableCode);
//        batchHandler.post(batchRunnableCode);
//
//        return START_STICKY;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        handler.removeCallbacks(runnableCode);
//        batchHandler.removeCallbacks(batchRunnableCode);
//
//        // Cancel the notification to remove the badge
//        NotificationManager manager = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//        if (manager != null) {
//            manager.cancel(1);
//        }
//
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    private void callApi() {
//        Log.d(TAG, "callApi => " + "Checking batch time");
//        String token = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.TOKEN);
//        String mid = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.MID);
//        String tid = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.TID);
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("https://psa.paymentsave.co.uk/api/v1/transaction/terminal/auto-batch-queue/?mid=" + mid + "&tid=" + tid)
//                .addHeader("Authorization", "Corona " + token)
//                .build();
//
//        client.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                Log.e(TAG, "API call failed: " + e.getMessage());
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    try {
//                        String responseBody = response.body().string();
//                        JSONObject jsonObject = new JSONObject(responseBody);
//
//                        // Extract data from the JSON object
//                        // String value = jsonObject.getString("key");
//                        // Parse the "data" JSONObject
//                        JSONObject dataObject = jsonObject.getJSONObject("data");
//                        int count = dataObject.getInt("count");
//                        String next = dataObject.optString("next", null);
//                        String previous = dataObject.optString("previous", null);
//                        int totalPages = dataObject.getInt("total_pages");
//                        // Parse the "results" JSONArray
//                        JSONArray resultsArray = dataObject.getJSONArray("results");
//                        if (resultsArray.length() > 0) {
////                            for (int i = 0; i < resultsArray.length(); i++) {
////                                JSONObject result = resultsArray.getJSONObject(i);
//                            JSONObject result = resultsArray.getJSONObject(0);
//                            String reportTime = result.getString("report_time");
//                            SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, reportTime);
////                                boolean execureReport = compareReportTimeWithCurrentTime(reportTime);
////                                if (execureReport) {
////                                    executeBatch();
////                                }
////                                int id = result.getInt("id");
////                                String createdAt = result.getString("created_at");
////                                String updatedAt = result.getString("updated_at");
////                                String reportType = result.getString("report_type");
////
////                                String statusResult = result.getString("status");
////                                int business = result.getInt("business");
////                                int terminal = result.getInt("terminal");
////                            }
//                        } else {
//                            SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, "");
//                        }
//                    } catch (Exception e) {
//                        Log.e("MyForegroundService", "JSON parsing error: " + e.getMessage());
//                    }
//                } else {
//                    Log.e(TAG, "Error: " + response.code());
//                }
//            }
//        });
//    }
//
//    private boolean compareReportTimeWithCurrentTime(String reportTime) {
//        Log.d(TAG, "callApi => " + "Response");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            // Define the format of the date and time in your string
////            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
////
////            // Parse the input date-time string to a LocalDateTime object
////            LocalDateTime inputDateTime = LocalDateTime.parse(reportTime, formatter);
////
////            // Get the current date-time
////            LocalDateTime currentDateTime = LocalDateTime.now();
//
//            // Define the format of the date and time, including the time zone if needed
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//
//// Parse the input date-time string to a LocalDateTime object
//            LocalDateTime inputDateTime = LocalDateTime.parse(reportTime, formatter)
//                    .withSecond(0)
//                    .withNano(0); // Optionally reset nanoseconds too
//
//            // Specify the UK time zone (Europe/London)
//            ZoneId ukZone = ZoneId.of("Europe/London");
//            // Get the current date-time and set seconds to 0
//            LocalDateTime currentDateTime = LocalDateTime.now(ukZone)
//                    .withSecond(0)
//                    .withNano(0);
//
//            // Extract the year, month, day, hour, and minute for comparison
//            boolean isSameDate = inputDateTime.equals(currentDateTime);
////            boolean isSameHour = inputDateTime.getHour() == currentDateTime.getHour();
////            boolean isSameMinute = inputDateTime.getMinute() == currentDateTime.getMinute();
//
////            if (isSameDate && isSameHour && isSameMinute) {
//            if (isSameDate) {
//                return true;
//            } else {
//                return false;
//            }
//
//
////            // Define the format of the date and time in your string
////            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
////
////            // Parse the report time string to a LocalDateTime object
////            LocalDateTime reportDateTime = LocalDateTime.parse(reportTime, formatter);
////
////            // Get the current date and time
////            LocalDateTime currentDateTime = LocalDateTime.now();
////
////            // Compare the two LocalDateTime objects
////            if (reportDateTime.isEqual(currentDateTime)) {
////                return true;
////            }
//////            if (reportDateTime.isBefore(currentDateTime) || reportDateTime.isEqual(currentDateTime)) {
//////                return true;
//////            } else if (reportDateTime.isAfter(currentDateTime)) {
//////                SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, reportTime);
//////            }
////            return false;
//        }
//        return false;
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel serviceChannel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "Foreground Service Channel",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//
//            // Optionally, disable the badge
//            serviceChannel.setShowBadge(false);
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            if (manager != null) {
//                manager.createNotificationChannel(serviceChannel);
//            }
//        }
//    }
//
//    private void executeBatch() {
//        if (!SharedHelper.getToken(getApplicationContext()).isEmpty()) {
////            if (ServiceUtils.isAppInForeground(getApplicationContext())) {
//            // Execute Report
//            HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
//            args.put(CT_ZREPORT, "true");
//            PosIntegrate.executeReport(getApplicationContext(), TRANSACTION_TYPE_RECONCILIATION, args);
////            }
//        }
//    }
//
//    private final Runnable runnableCode = new Runnable() {
//        @Override
//        public void run() {
//            // Call your API here
//            callApi();
//
//            // Repeat this the same runnable code block after the interval
//            handler.postDelayed(runnableCode, interval);
//        }
//    };
//
//
//    private final Runnable batchRunnableCode = new Runnable() {
//        @Override
//        public void run() {
//            // Call your API here
//            String nextBatch = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME);
//            if (!nextBatch.isEmpty()) {
//                boolean isExecutable = compareReportTimeWithCurrentTime(nextBatch);
//                if (isExecutable) {
//                    SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, "");
//                    executeBatch();
//                }
//            }
//
//            // Repeat this the same runnable code block after the interval
//            batchHandler.postDelayed(batchRunnableCode, interval_second);
//        }
//    };
//}