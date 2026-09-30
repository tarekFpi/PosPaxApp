package com.paymentsave.paymentsave.coreapp.activities.splash;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.pax.dal.entity.ENavigationKey;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.CounterPay.CounterPayActivity;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.activities.onboard.OnboardOneActivity;
import com.paymentsave.paymentsave.coreapp.activities.test.system.SysTester;
import com.paymentsave.paymentsave.coreapp.receiver.LogisticService;
import com.paymentsave.paymentsave.coreapp.roomdb.DBHelper;
import com.paymentsave.paymentsave.coreapp.utils.NetworkUtils;
import com.paymentsave.paymentsave.coreapp.utils.ServiceUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.DeviceConfigData;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.DeviceConfigResponse;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    public static int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private SplashViewModel viewModel;
    private String device_serial;
    private Dialog dialog;
    private ImageView logoImv;
    private boolean provisionalDialogShowing = false;


    private String getTodayLogFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(new Date());
        return "device_log_" + date + ".txt";
    }

    private void writeLog(String message) {
        String fileName = getTodayLogFileName();
        File logFile = new File(getFilesDir(), fileName);

        try {
            // Create the file if it doesn't exist
            if (!logFile.exists()) {
                logFile.createNewFile(); // May throw IOException
            }

            // Append to the file
            try (FileWriter writer = new FileWriter(logFile, true)) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String timestamp = sdf.format(new Date());
                writer.append(timestamp).append(" - ").append(message).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            // Telemetry: SplashActivity.writeLog — failed to append today's device log file.
            logReceiveError("writeLog", String.valueOf(e.getMessage()), e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        viewModel = ViewModelProviders.of(this).get(SplashViewModel.class);

        if (!ServiceUtils.isServiceRunning(this, LogisticService.class)) {
            Intent serviceIntent = new Intent(this, LogisticService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } else {
            Log.d("ServiceCheck", "Service is already running.");
        }

        // Initialize Firebase
        // FirebaseApp.initializeApp(SplashActivity.this);
        // Initialize RoomDB
        DBHelper.getDatabase(SplashActivity.this);

        Log.i(TAG, "Getting Network Status: " + NetworkUtils.getConnectivityStatusString(this));

//        if (NetworkUtils.getConnectivityStatusString(this) == 0) {
//            openDialog();
//        }

        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "checkSelfPermission: ");
            boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_PHONE_STATE");

            if (shouldProvideRationale) {
                // After the user sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permission Required")
                        .setMessage("This app needs phone state permission to function.")
                        .setPositiveButton(
                                "OK", (dialog, which) ->
                                        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{"android.permission.READ_PHONE_STATE"}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE))
                        .setNegativeButton(
                                "Cancel", (dialog, which) -> {
                                    dialog.dismiss();
                                    finish();
                                }).create().show();
            } else {
                Log.e(TAG, "Has no permission: ");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_PHONE_STATE"}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {
            getDeviceConfigData();
        }
    }

    public void openDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.network_alert);
        Button settingBtn = dialog.findViewById(R.id.setting_btn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBackKey();
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }
        });
        Button exitBtn = dialog.findViewById(R.id.exit_btn);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        dialog.setCancelable(false);
        dialog.setTitle(R.string.app_name);
        dialog.show();
    }

    private void enableBackKey() {
        SysTester.getInstance().showNavigationBar(true);
        SysTester.getInstance().enableNavigationBar(true);
        ENavigationKey eNavigationKey = ENavigationKey.BACK;
        SysTester.getInstance().enableNavigationKey(eNavigationKey, true);
    }

    @Override
    protected void onResume() {
        try {
            SysTester.getInstance().enableNavigationBar(false);
            SysTester.getInstance().showNavigationBar(false);
            if (NetworkUtils.getConnectivityStatusString(this) != 0) {

                try {
                    dialog.dismiss();
                } catch (Exception ex) {
                    Log.i(TAG, "Dismiss existing dialogs");
                }

                if (ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
                    boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_PHONE_STATE");
                    if (!shouldProvideRationale && provisionalDialogShowing) {
                        // User checked "Don't ask again". Guide them to settings.
                        new AlertDialog.Builder(this).setTitle("Permission Denied").setMessage("This app cannot function without phone state permission. Please allow it in app settings.")
                                .setPositiveButton(
                                        "Go to Settings", (dialog, which) -> {
                                            // Guide the user to the app's settings
                                            openApplicationSettings();
                                            dialog.dismiss();
                                        }).setNegativeButton(
                                        "Close app", (dialog, which) ->
                                                finish()
                                ).setCancelable(false).create().show();
                    }
                } else {
                    getDeviceConfigData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: SplashActivity.onResume — failed to hide nav bar or refresh device config.
            logReceiveError("onResume", String.valueOf(e.getMessage()), e);
        }
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: ");
        if (requestCode == 1) { // If request is canceled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, do the phone state-related task you need to do.
                Log.e(TAG, "onRequestPermissionsResult: Granted");
                getDeviceConfigData();
            } else {
                // Permission denied, disable the functionality that depends on this permission.
                boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_PHONE_STATE");
                if (!shouldProvideRationale) {
                    // User checked "Don't ask again". Guide them to settings.
                    provisionalDialogShowing = true;
                    new AlertDialog.Builder(this).setTitle("Permission Denied").setMessage("This app cannot function without phone state permission. Please allow it in app settings.").setPositiveButton("Go to Settings", (dialog, which) -> {
                        // Guide the user to the app's settings
                        provisionalDialogShowing = false;
                        openApplicationSettings();
                        dialog.dismiss();
                    }).setNegativeButton(
                            "Close app", (dialog, which) ->
                                    finish()
                    ).setCancelable(false).create().show();
                } else {
                    // Close the app or disable the functionality that depends on the permission.
                    // Permission denied.
                    finish();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getDeviceConfigData() {
        device_serial = MainUtils.getDeviceSerial(this);
        SharedHelper.putStringData(getApplicationContext(), AppConstants.SharedPref.NEXT_BATCH_TIME, "");
        viewModel.getDeviceConfig(device_serial).observe(SplashActivity.this, new Observer<DeviceConfigResponse>() {
            @Override
            public void onChanged(DeviceConfigResponse deviceConfigResponse) {
                if (deviceConfigResponse != null) {
                    SharedHelper.putBusinessInfo(deviceConfigResponse.getData().getBusiness(), deviceConfigResponse.getData().getTid(), deviceConfigResponse.getData().getSupportEmail(), deviceConfigResponse.getData().getSupportPhone(), deviceConfigResponse.getData().getGratuityType(), deviceConfigResponse.getData().getBusiness().isLinkPaymentEnabled(), deviceConfigResponse.getData().getGratuityOptions(), deviceConfigResponse.getData().getCashbackOptions(), deviceConfigResponse.getData().isEReceipt());
                    if (deviceConfigResponse.getData().getMarchantAlerts() != null) {
                        if (!deviceConfigResponse.getData().getMarchantAlerts().isEmpty()) {
                            AppConstants.alertText = deviceConfigResponse.getData().getMarchantAlerts();
                        }
                    }
                }

                if (!SharedHelper.getToken(SplashActivity.this).isEmpty() && SharedHelper.getBooleanData(SplashActivity.this, AppConstants.SharedPref.DEVICE_STATUS)) {
                    if (SharedHelper.getBooleanData(SplashActivity.this, AppConstants.SharedPref.INTEGRATION_MODE)) {
                        Intent intent = new Intent(SplashActivity.this, CounterPayActivity.class);
                        intent.putExtra("device_serial", device_serial);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        intent.putExtra("device_serial", device_serial);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, OnboardOneActivity.class);
                    intent.putExtra("device_serial", device_serial);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void openApplicationSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Sends an activity failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code SplashActivity.onResume}.
     * {@code message} must already be a string.
     */
    private void logReceiveError(String functionPath, String message, Throwable throwable) {
        String eventName = TAG + "." + functionPath;
        TelemetryLogger.error(
                SharedHelper.getStringData(this, AppConstants.SharedPref.TID),
                eventName,
                message != null ? message : "",
                throwable
        );
    }
}