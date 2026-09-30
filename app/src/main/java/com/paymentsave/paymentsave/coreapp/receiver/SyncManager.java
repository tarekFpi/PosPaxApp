package com.paymentsave.paymentsave.coreapp.receiver;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.roomdb.DBHelper;
import com.paymentsave.paymentsave.coreapp.roomdb.entity.Report;
import com.paymentsave.paymentsave.coreapp.roomdb.entity.Transaction;
import com.paymentsave.paymentsave.coreapp.utils.ObjectUtils;
import com.paymentsave.paymentsave.network.ApiService;
import com.paymentsave.paymentsave.network.RetrofitClient;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.ReportCreateResponse.ReportCreateResponse;
import com.paymentsave.paymentsave.responses.TransactionCreateResponse;
import com.paymentsave.paymentsave.responses.TransactionResponse;
import com.paymentsave.paymentsave.responses.TransactionUpdateResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class SyncManager extends Worker {
    private static final String TAG = "SyncManager";

    public SyncManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static ArrayList<TransactionResponse> mapEntityToResponseList(Transaction entity) {
        ArrayList<TransactionResponse> responseList = new ArrayList<>();

        if (entity.getTransType() != null) {
            responseList.add(new TransactionResponse("Type", entity.getTransType()));
        }
        if (entity.getRequestID() != null) {
            responseList.add(new TransactionResponse("RequestID", entity.getRequestID()));
        }
        if (entity.getErrorText() != null) {
            responseList.add(new TransactionResponse("ErrorText", entity.getErrorText()));
        }
        if (entity.getTnxNote() != null) {
            responseList.add(new TransactionResponse("TransactionNote", entity.getTnxNote()));
        }
        if (entity.isResultResponse()) {
            responseList.add(new TransactionResponse("IsResultResponse", "true"));
        } else {
            responseList.add(new TransactionResponse("IsResultResponse", "false"));
        }
        if (entity.getUti() != null) {
            responseList.add(new TransactionResponse("UTI", entity.getUti()));
        }
        if (entity.getExistingUti() != null) {
            responseList.add(new TransactionResponse("ExistingUti", entity.getExistingUti()));
        }
        if (entity.getAmountTrans() != null) {
            responseList.add(new TransactionResponse("Amount", entity.getAmountTrans()));
        }
        if (entity.getAmountGratuity() != null) {
            responseList.add(new TransactionResponse("Gratuity", entity.getAmountGratuity()));
        }
        if (entity.getAmountCashback() != null) {
            responseList.add(new TransactionResponse("Cashback", entity.getAmountCashback()));
        }
        if (entity.getAmountDiscount() != null) {
            responseList.add(new TransactionResponse("Discount", entity.getAmountDiscount()));
        }
        responseList.add(new TransactionResponse("Approved", String.valueOf(entity.isTransApproved())));
        responseList.add(new TransactionResponse("Cancelled", String.valueOf(entity.isTransCancelled())));
        responseList.add(new TransactionResponse("SigRequired", String.valueOf(entity.isCvmSigRequired())));
        responseList.add(new TransactionResponse("PINVerified", String.valueOf(entity.isCvmPinVerified())));
        if (entity.getTransCurrencyCode() != null) {
            responseList.add(new TransactionResponse("Currency", entity.getTransCurrencyCode()));
        }
        if (entity.getSoftwareVersion() != null) {
            responseList.add(new TransactionResponse("Version", entity.getSoftwareVersion()));
        }
        if (entity.getTerminalId() != null) {
            responseList.add(new TransactionResponse("terminalId", entity.getTerminalId()));
        }
        if (entity.getMerchantId() != null) {
            responseList.add(new TransactionResponse("MerchantId", entity.getMerchantId()));
        }
        if (entity.getReceiptNumber() != null) {
            responseList.add(new TransactionResponse("ReceiptNumber", entity.getReceiptNumber()));
        }
        if (entity.getRetrievalReferenceNumber() != null) {
            responseList.add(new TransactionResponse("RRN", entity.getRetrievalReferenceNumber()));
        }
        if (entity.getResponseCode() != null) {
            responseList.add(new TransactionResponse("ResponseCode", entity.getResponseCode()));
        }
        if (entity.getStan() != null) {
            responseList.add(new TransactionResponse("Stan", entity.getStan()));
        }
        if (entity.getAuthorisationCode() != null) {
            responseList.add(new TransactionResponse("AuthCode", entity.getAuthorisationCode()));
        }
        if (entity.getMerchantTokenId() != null) {
            responseList.add(new TransactionResponse("MerchantTokenId", entity.getMerchantTokenId()));
        }
        if (entity.getCardType() != null) {
            responseList.add(new TransactionResponse("CardType", entity.getCardType()));
        }
        if (entity.getEmvAid() != null) {
            responseList.add(new TransactionResponse("AID", entity.getEmvAid()));
        }
        if (entity.getEmvTsi() != null) {
            responseList.add(new TransactionResponse("TSI", entity.getEmvTsi()));
        }
        if (entity.getEmvTvr() != null) {
            responseList.add(new TransactionResponse("TVR", entity.getEmvTvr()));
        }
        if (entity.getEmvCardholderName() != null) {
            responseList.add(new TransactionResponse("CardHolder", entity.getEmvCardholderName()));
        }
        if (entity.getEmvCryptogram() != null) {
            responseList.add(new TransactionResponse("Cryptogram", entity.getEmvCryptogram()));
        }
        if (entity.getEmvCryptogramType() != null) {
            responseList.add(new TransactionResponse("CryptogramType", entity.getEmvCryptogramType()));
        }
        if (entity.getCardPan() != null) {
            responseList.add(new TransactionResponse("PAN", entity.getCardPan()));
        }
        if (entity.getCardExpiryDate() != null) {
            responseList.add(new TransactionResponse("ExpiryDate", entity.getCardExpiryDate()));
        }
        if (entity.getCardStartDate() != null) {
            responseList.add(new TransactionResponse("StartDate", entity.getCardStartDate()));
        }
        if (entity.getCardScheme() != null) {
            responseList.add(new TransactionResponse("Scheme", entity.getCardScheme()));
        }
        if (entity.getCardPanSequenceNumber() != null) {
            responseList.add(new TransactionResponse("PSN", entity.getCardPanSequenceNumber()));
        }
        if (entity.getCardHolderReceipt() != null) {
            responseList.add(new TransactionResponse("CardHolderReceipt", String.join("&&", entity.getCardHolderReceipt())));
        }
        if (entity.getMerchantReceipt() != null) {
            responseList.add(new TransactionResponse("MerchantReceipt", String.join("&&", entity.getMerchantReceipt())));
        }
        if (entity.getCreatedAt() != null) {
            responseList.add(new TransactionResponse("Created At", entity.getCreatedAt()));
        }

        return responseList;
    }

    private com.paymentsave.paymentsave.responses.ReportListResponse.Report generateReportRequest(Context context, Report report) {
        com.paymentsave.paymentsave.responses.ReportListResponse.Report report1 = new com.paymentsave.paymentsave.responses.ReportListResponse.Report();
        report1.setCreatedAt(report.getCreatedAt());
        report1.setReportType(report.getReportType());
        report1.setResultType(report.getResultType());
        report1.setEposSessionId(report.getRequestID());
        report1.setCompletionCount(report.getCompletionCount());
        report1.setCompletionAmount(String.valueOf(report.getCompletionAmount()));
        report1.setCashbackCount(report.getCashbackCount());
        report1.setCashbackAmount(String.valueOf(report.getCashbackAmount()));
        report1.setGratuityCount(report.getGratuityCount());
        report1.setGratuityAmount(String.valueOf(report.getGratuityAmount()));
        report1.setRefundCount(report.getRefundCount());
        report1.setRefundAmount(String.valueOf(report.getRefundAmount()));
        report1.setSaleCount(report.getSaleCount());
        report1.setSaleAmount(String.valueOf(report.getSaleAmount()));
        report1.setIsReportResponse(report.isReportResponse());

        report1.setMerchant(SharedHelper.getIntData(context, AppConstants.SharedPref.USER_ID));
        report1.setTid(SharedHelper.getStringData(context, AppConstants.SharedPref.TID));
        report1.setBusiness(SharedHelper.getIntData(context, AppConstants.SharedPref.BUSINESS_ID));
        report1.setResponseText(report.getResponseText());
        report1.setReportError(report.getReportError());
        return report1;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "Data Sync Started");
        // Fetch unSynced transactions from the database
        List<Transaction> unSyncedTransactions = getUnSyncedTransactions();
        // Fetch unSynced reports from the database
        List<Report> unSyncedReports = getUnSyncedReports();

        // Send data to the server
        for (Transaction transaction : unSyncedTransactions) {
            sendTransactionDataToServer(transaction);
        }

        // Send data to the server
        for (Report report : unSyncedReports) {
            sendReportsDataToServer(report);
        }
        Log.i(TAG, "Data Sync Completed");
        return Result.success();
    }

    List<Transaction> getUnSyncedTransactions() {
        return DBHelper.getDatabase(getApplicationContext()).transactionDao().getAllUnSyncedData(false);
    }

    List<Report> getUnSyncedReports() {
        return DBHelper.getDatabase(getApplicationContext()).reportDao().getAllUnSyncedData(false);
    }

    void sendTransactionDataToServer(Transaction transaction) {

        ArrayList<TransactionResponse> responseList = mapEntityToResponseList(transaction);
        TransactionRequest transactionRequest = ObjectUtils.generateTransactionRequest(getApplicationContext(), responseList);
        Log.d(TAG, "sendTransactionDataToServer: " + transactionRequest.toString());

        String splitBillId = SharedHelper.getStringData(getApplicationContext(), AppConstants.SharedPref.SPLIT_BILL_ID);
        if (AppConstants.isSplitBillRunning && !splitBillId.isEmpty()) {
            transactionRequest.setSplitBillId(splitBillId);
        }

        // Get Shared Token
        String token = SharedHelper.getToken(getApplicationContext());
        String accessToken = "Corona " + token;
        // Initialize Retrofit
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());

        // Make API Call
        Gson gson = new Gson();
        String json = gson.toJson(transactionRequest);
        if (transactionRequest.isResultResponse()) {
            Log.d(TAG, "sendTransactionDataToServer: Update: " + transaction.getUti());
            Call<TransactionUpdateResponse> call = apiService.createOrUpdateTransaction(accessToken, transactionRequest, transactionRequest.getUti());
            call.enqueue(new retrofit2.Callback<TransactionUpdateResponse>() {
                @Override
                public void onResponse(Call<TransactionUpdateResponse> call, retrofit2.Response<TransactionUpdateResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "sendTransactionDataToServer: Update Response: " + response.body().getData().getUti());
                        AppConstants.executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                // Update transaction as synced in local database
                                DBHelper.getDatabase(getApplicationContext()).transactionDao().updateSyncStatus(transaction.getId(), true);
                            }
                        });
                    } else {
                        Log.e(TAG, "Transaction Sync Failed: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<TransactionUpdateResponse> call, Throwable t) {
                    Log.e(TAG, "API Call Failed: " + t.getMessage());
                }
            });
        } else {

            Log.d(TAG, "sendTransactionDataToServer: Create: " + transaction.getUti());
            Call<TransactionCreateResponse> call = apiService.createTransaction(accessToken, transactionRequest);
            call.enqueue(new retrofit2.Callback<TransactionCreateResponse>() {
                @Override
                public void onResponse(Call<TransactionCreateResponse> call, retrofit2.Response<TransactionCreateResponse> response) {
                    if (response.isSuccessful() && response.code() == 201) {
                        Log.d(TAG, "sendTransactionDataToServer: Create Response: " + response.body());
                        AppConstants.executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                // Update transaction as synced in local database
//                                DBHelper.getDatabase(getApplicationContext()).transactionDao().updateSyncStatus(transaction.getId(), true);
                            }
                        });
                    } else {
                        Log.e(TAG, "Transaction Sync Failed: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<TransactionCreateResponse> call, Throwable t) {
                    Log.e(TAG, "API Call Failed: " + t.getMessage());
                }
            });
        }
    }

    void sendReportsDataToServer(Report report) {
        // Get Shared Token
        String token = SharedHelper.getToken(getApplicationContext());
        String accessToken = "Corona " + token;
        // Initialize Retrofit
        ApiService apiService = RetrofitClient.getApiService(getApplicationContext());

        com.paymentsave.paymentsave.responses.ReportListResponse.Report reportRequest = generateReportRequest(getApplicationContext(), report);

        // Make API Call
        Call<ReportCreateResponse> call = apiService.createReport(accessToken, reportRequest);
        call.enqueue(new retrofit2.Callback<ReportCreateResponse>() {
            @Override
            public void onResponse(Call<ReportCreateResponse> call, retrofit2.Response<ReportCreateResponse> response) {
                if (response.isSuccessful()) {
                    AppConstants.executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            // Update transaction as synced in local database
                            DBHelper.getDatabase(getApplicationContext()).reportDao().updateSyncStatus(report.getId(), true);
                        }
                    });
                } else {
                    Log.e(TAG, "Report Sync Failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ReportCreateResponse> call, Throwable t) {
                Log.e(TAG, "API Call Failed: " + t.getMessage());
            }
        });
    }

}
