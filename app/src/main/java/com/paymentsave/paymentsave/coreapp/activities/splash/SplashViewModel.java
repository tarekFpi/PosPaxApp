package com.paymentsave.paymentsave.coreapp.activities.splash;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.repositories.AccountRepository;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.DeviceConfigResponse;

public class SplashViewModel extends AndroidViewModel {
    private AccountRepository accountRepository;
    public SplashViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
    }

    public MutableLiveData<DeviceConfigResponse> getDeviceConfig(String serial){
        return accountRepository.getDeviceConfig(serial);
    }
}
