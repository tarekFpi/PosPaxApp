package com.paymentsave.paymentsave.coreapp.fragments.viewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.repositories.PaxRepository;
import com.paymentsave.paymentsave.responses.PBLResponse.LinkResponse;
import com.paymentsave.paymentsave.responses.PBLResponse.PBLCreateResponse;
import com.paymentsave.paymentsave.responses.ReportListResponse.ReportListResponse;
import com.paymentsave.paymentsave.responses.TransactionDetailsResponse.TransactionDetailsResponse;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResponse;

import java.util.HashMap;

public class TransactionViewModel extends AndroidViewModel {
    private PaxRepository paxRepository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        paxRepository = new PaxRepository(application);
    }

    public MutableLiveData<ReportListResponse> getReportList(HashMap<String, String> map, String token) {
        return paxRepository.getReportListData(map, token);
    }

    public MutableLiveData<TransactionListResponse> getTransactionsList(HashMap<String, String> map, String token) {
        return paxRepository.getTransactionsListData(map, token);
    }

    public MutableLiveData<TransactionListResponse> getNextTransactionsList(String nextUrl, String token) {
        return paxRepository.getNextTransactionsList(nextUrl, token);
    }

    public MutableLiveData<ReportListResponse> getNextReportList(String nextUrl, String token) {
        return paxRepository.getNextReportList(nextUrl, token);
    }

    public MutableLiveData<TransactionDetailsResponse> getTransactionDetailsData(String token, String tnxId) {
        return paxRepository.getTransactionDetails(token, tnxId);
    }

    public MutableLiveData<Object> requestEmailReceipt(String token, String email, String tnxId) {
        return paxRepository.requestEmailReceipt(token, email, tnxId);
    }

    public MutableLiveData<Object> sendEmailPaymentLink(String token, String email, String tnxId) {
        return paxRepository.requestEmailPayment(token, email, tnxId);
    }

    public MutableLiveData<LinkResponse> getPblTnxList(String token, HashMap<String, String> map) {
        return paxRepository.getPblTnxListData(token, map);
    }

    public MutableLiveData<LinkResponse> getPblNextTnxList(String nextUrl, String token) {
        return paxRepository.getPblNextTransactionsList(nextUrl, token);
    }

    public MutableLiveData<PBLCreateResponse> createPBLLink(String token, HashMap<String, Object> map) {
        return paxRepository.createPBLLink(token, map);
    }

}
