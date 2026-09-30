package com.paymentsave.paymentsave.coreapp.receiver;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launcher extends Service {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        executorService.execute(() -> {
            PackageManager pm = this.getPackageManager();
            try {
                Intent target = pm.getLaunchIntentForPackage("your.package.id");
                if (target != null) {
                    target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(target);
                    synchronized (this) {
                        try {
                            this.wait(3000); // Use wait inside synchronized block
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); // Restore interrupted status
                        }
                    }
                } else {
                    throw new ActivityNotFoundException();
                }
            } catch (ActivityNotFoundException ignored) {
            } finally {
                // Use handler to run on the main thread
                handler.post(this::stopSelf);
            }
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow(); // Shutdown the executor service
    }
}