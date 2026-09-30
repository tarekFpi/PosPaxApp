package com.paymentsave.paymentsave.coreapp.fragments.reports;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.repositories.AccountRepository;
import com.paymentsave.paymentsave.responses.AnalyticReportRepose.AnalyticReportResponse;

import java.util.HashMap;

public class ReportViewModel extends AndroidViewModel {
    private AccountRepository accountRepository;
    public ReportViewModel(@NonNull Application application) {
        super(application);
        accountRepository = new AccountRepository(application);
    }
    public MutableLiveData<AnalyticReportResponse> getReportData(HashMap<String, Object> map, String token){
        return accountRepository.getReportData(map,token);
    }
}
