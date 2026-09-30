package com.paymentsave.paymentsave.network;

import android.content.Context;

import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class HeaderInterceptor implements Interceptor {
    private Context context;

    public HeaderInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Add custom headers to the request
        Request modifiedRequest = originalRequest.newBuilder()
                .header("X-FCM-KEY", SharedHelper.getKeyFCM(context, AppConstants.SharedPref.DEVICE_TOKEN))
                .header("Pax-App-Version", String.format("v%s",BuildConfig.VERSION_NAME))
                .build();

        return chain.proceed(modifiedRequest);
    }
}
