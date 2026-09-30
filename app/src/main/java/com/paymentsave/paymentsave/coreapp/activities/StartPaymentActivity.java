package com.paymentsave.paymentsave.coreapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.CounterPay.CounterPayActivity;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.activities.splash.SplashActivity;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.network.SharedHelper;

import java.util.List;

public class StartPaymentActivity extends AppCompatActivity {

    Button startPaymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_payment);
        startPaymentBtn = findViewById(R.id.start_taking_payment_btn);
        if (Build.VERSION.SDK_INT >= 23) //Android MarshMellow Version or above
        {
            requestPermission();

        }
        startPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean integrationMode = SharedHelper.getBooleanData(StartPaymentActivity.this, AppConstants.SharedPref.INTEGRATION_MODE);
                if (integrationMode) {
                    Intent intent = new Intent(StartPaymentActivity.this, CounterPayActivity.class);
                    intent.putExtra("device_serial", MainUtils.getDeviceSerial(StartPaymentActivity.this));
                    startActivity(intent);
                } else {
                    String device_serial = MainUtils.getDeviceSerial(StartPaymentActivity.this);
                    Intent intent = new Intent(StartPaymentActivity.this, HomeActivity.class);
                    intent.putExtra("device_serial", device_serial);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void requestPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //do your action
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }
}