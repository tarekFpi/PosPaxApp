package com.paymentsave.paymentsave.coreapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.paymentsave.paymentsave.coreapp.utils.NetworkUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkStateReceiver";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtils.getConnectivityStatusString(context);
        Log.e(TAG, "onReceive: " + status);
        checkInternetConnection(context);

//        if(status.isEmpty()) {
//            status="No Internet Connection";
//        }
//        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }

    private void checkInternetConnection(Context context) {
        executorService.execute(() -> {
            boolean hasInternet = false;
            try {
                // Attempt to reach a reliable site
                URL url = new URL("https://www.google.com");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(5000); // 5 seconds
                urlc.connect();
                hasInternet = (urlc.getResponseCode() == 200);
            } catch (Exception e) {
                hasInternet = false;
            }

            final boolean internetAvailable = hasInternet;
            // Update UI on the main thread
            mainThreadHandler.post(() -> {
                if (internetAvailable) {
                    Toast.makeText(context, "WiFi Connected with Internet", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "WiFi Connected, No Internet", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}


