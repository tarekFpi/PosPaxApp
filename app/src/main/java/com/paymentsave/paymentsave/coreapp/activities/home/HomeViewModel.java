package com.paymentsave.paymentsave.coreapp.activities.home;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.coreapp.roomdb.database.PaxDB;
import com.paymentsave.paymentsave.repositories.AccountRepository;
import com.paymentsave.paymentsave.repositories.PaxRepository;
import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.DeviceConfigResponse;
import com.paymentsave.paymentsave.responses.ReportCreateResponse.ReportCreateResponse;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;
import com.paymentsave.paymentsave.responses.TransactionCreateResponse;


public class HomeViewModel extends AndroidViewModel {
    private PaxRepository paxRepository;
    private AccountRepository accountRepository;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        paxRepository = new PaxRepository(application);
        accountRepository = new AccountRepository(application);
    }

    public MutableLiveData<TransactionCreateResponse> createTransaction(TransactionRequest request, PaxDB localDb, String token) {
        return paxRepository.createTransactionData(request, localDb, token);
    }

    public MutableLiveData<ReportCreateResponse> createReport(Context context, Report request, String token) {
        return paxRepository.createReportData(context, request, token);
    }

    public MutableLiveData<ReportCreateResponse> updateReport(Context context, Report request, String token, String id) {
        return paxRepository.updateReportData(context, request, token, id);
    }

    public MutableLiveData<DeviceConfigResponse> getDeviceConfig(String serial) {
        return accountRepository.getDeviceConfig(serial);
    }
}
