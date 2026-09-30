package com.paymentsave.paymentsave.coreapp.activities.onboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.repositories.AccountRepository;
import com.paymentsave.paymentsave.responses.AccountVerificationResponse.AccountVerificationResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationCodeResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationRequestResponse;

import java.util.HashMap;

public class OnboardViewModel extends AndroidViewModel {
    private AccountRepository accountRepository;

    public OnboardViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
    }

    public MutableLiveData<AccountVerificationResponse> verifyAccountData(HashMap<String, String> querymap) {
        return accountRepository.verifyAccountData(querymap);
    }

    public MutableLiveData<ActivationCodeResponse> validateTerminalActivation(HashMap<String, String> querymap) {
        return accountRepository.validateTerminalActivation(querymap);
    }

    public MutableLiveData<ActivationRequestResponse> requestTerminalActivation(String token, String mid, String sn, String code) {
        return accountRepository.requestTerminalActivation(token, mid, sn, code);
    }
}
