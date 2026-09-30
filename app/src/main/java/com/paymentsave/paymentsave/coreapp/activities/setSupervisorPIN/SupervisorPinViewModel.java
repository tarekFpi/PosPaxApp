package com.paymentsave.paymentsave.coreapp.activities.setSupervisorPIN;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.repositories.AccountRepository;

import java.util.HashMap;

public class SupervisorPinViewModel extends AndroidViewModel {

    private AccountRepository accountRepository;

    public SupervisorPinViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
    }

    public MutableLiveData<Object> setSupervisorPin(HashMap<String,String> map){
        return accountRepository.setSupervisorPin(map);
    }



}
