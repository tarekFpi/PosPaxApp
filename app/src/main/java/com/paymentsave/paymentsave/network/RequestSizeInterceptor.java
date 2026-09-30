package com.paymentsave.paymentsave.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class RequestSizeInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long bodySize = 0;

        RequestBody requestBody = request.body();

        if (requestBody != null) {
            try {
                long contentLength = requestBody.contentLength();

                if (contentLength != -1) {
                    bodySize = contentLength;
                } else {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    bodySize = buffer.size();
                }

            } catch (Exception e) {
                Log.e("REQ_SIZE", "Failed to calculate request size", e);
            }
        }

        Log.d("REQ_SIZE", "URL: " + request.url());
        Log.d("REQ_SIZE", "Method: " + request.method());
        Log.d("REQ_SIZE", "Body size: " + bodySize + " bytes");
        Log.d("REQ_SIZE", "Body size KB: " + (bodySize / 1024.0) + " KB");

        return chain.proceed(request);
    }
}
