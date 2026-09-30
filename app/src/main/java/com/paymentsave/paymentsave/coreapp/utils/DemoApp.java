package com.paymentsave.paymentsave.coreapp.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.paymentsave.paymentsave.BuildConfig;

public class DemoApp extends Application {

    private static IDAL dal;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        initializeOpenObserveLogging();
        dal = getDal();
    }

    /**
     * Starts OTLP log export to Panoptes (OpenObserve) as early as possible
     * so receivers and background work are covered, not only SplashActivity.
     */
    private void initializeOpenObserveLogging() {
        try {
            TelemetryLogger.initialize(
                    this,
                    BuildConfig.OPENOBSERVE_ENDPOINT,
                    BuildConfig.OPENOBSERVE_INGEST_EMAIL,
                    BuildConfig.OPENOBSERVE_INGEST_TOKEN,
                    BuildConfig.OPENOBSERVE_STREAM,
                    BuildConfig.OPENOBSERVE_SERVICE_NAME,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.OPENOBSERVE_ENVIRONMENT
            );
        } catch (Exception exception) {
            Log.e(
                    "DemoApp",
                    "OpenObserve logging failed to start",
                    exception
            );
        }
    }
    
    public static IDAL getDal(){
        if(dal == null){
            try {
                long start = System.currentTimeMillis();
                dal = NeptuneLiteUser.getInstance().getDal(appContext);
                Log.i("Test","get dal cost:"+(System.currentTimeMillis() - start)+" ms");
            } catch (Exception e) {
                e.printStackTrace();
//                Toast.makeText(appContext, "error occurred,DAL is null.", Toast.LENGTH_LONG).show();
            }
        }
        return dal;
    }
    
}
