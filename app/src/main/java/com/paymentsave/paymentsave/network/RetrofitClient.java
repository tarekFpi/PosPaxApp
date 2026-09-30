package com.paymentsave.paymentsave.network;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getInstance(Context context) {

        HeaderInterceptor headerInterceptor = new HeaderInterceptor(context);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.redactHeader("");
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                // .addInterceptor(logging)
                .addInterceptor(new RequestSizeInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 50, TimeUnit.SECONDS)).build();
        AndroidNetworking.initialize(context, okHttpClient);
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        return getInstance(context).create(ApiService.class);
    }
}

