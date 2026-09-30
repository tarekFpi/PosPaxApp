package com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.verifySupervisorPin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.repositories.AccountRepository;
import com.paymentsave.paymentsave.responses.PinVerifyResponse.PinVerifyResponse;

import java.util.HashMap;

public class VerifyPinViewModel extends AndroidViewModel {
    private AccountRepository accountRepository;

    public VerifyPinViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
    }

    public MutableLiveData<PinVerifyResponse> verifySupervisorPin(HashMap<String, String> payload) {
        return accountRepository.verifySupervisorPin(payload);
    }

    public MutableLiveData<Object> changeSupervisorPin(String token, HashMap<String, String> payload) {
        return accountRepository.changeSupervisorPINRequest(token, payload);
    }
}
