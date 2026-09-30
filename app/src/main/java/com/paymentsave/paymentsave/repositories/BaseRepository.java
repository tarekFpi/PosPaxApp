package com.paymentsave.paymentsave.repositories;

import android.app.Application;
import android.content.Context;

import com.paymentsave.paymentsave.network.ApiService;
import com.paymentsave.paymentsave.network.RetrofitClient;
import com.paymentsave.paymentsave.network.SharedHelper;


public class BaseRepository {
    protected ApiService apiService;
    protected Context context;
    protected SharedHelper sharedHelper;

    public BaseRepository(Application application) {
        this.context = application.getApplicationContext();
        this.apiService = RetrofitClient.getApiService(application);
        this.sharedHelper = new SharedHelper(context);
    }
}
