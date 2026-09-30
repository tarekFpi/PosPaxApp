package com.paymentsave.paymentsave.repositories;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.roomdb.database.PaxDB;
import com.paymentsave.paymentsave.coreapp.utils.ErrorUtils;
import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.PBLResponse.LinkResponse;
import com.paymentsave.paymentsave.responses.PBLResponse.PBLCreateResponse;
import com.paymentsave.paymentsave.responses.ReportCreateResponse.ReportCreateResponse;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;
import com.paymentsave.paymentsave.responses.ReportListResponse.ReportListResponse;
import com.paymentsave.paymentsave.responses.TransactionCreateResponse;
import com.paymentsave.paymentsave.responses.TransactionDetailsResponse.TransactionDetailsResponse;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResponse;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaxRepository extends BaseRepository {

    private static final String TAG = "PaxRepository";

    public PaxRepository(Application application) {
        super(application);
    }

    public MutableLiveData<TransactionCreateResponse> createTransactionData(TransactionRequest request, PaxDB localDb, String token) {
        final MutableLiveData<TransactionCreateResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.createTransaction(accessToken, request).enqueue(new Callback<TransactionCreateResponse>() {
            @Override
            public void onResponse(Call<TransactionCreateResponse> call, Response<TransactionCreateResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());

//                    Report business = null;
//                    BusinessTerminal businessTerminal = null;
//                    if (response.body().getData().getBusiness() != null) {
//                        business = new Report();
//                        business.setId(response.body().getData().getBusiness().getId());
//                        business.setCreatedAt(response.body().getData().getBusiness().getCreatedAt());
//                        business.setUpdatedAt(response.body().getData().getBusiness().getUpdatedAt());
//                        business.setMid(response.body().getData().getBusiness().getMid());
//                        business.setLogo(response.body().getData().getBusiness().getLogo());
//                        business.setLegalName(response.body().getData().getBusiness().getLegalName());
//                        business.setTradingName(response.body().getData().getBusiness().getTradingName());
//                        business.setTradingAddress(response.body().getData().getBusiness().getTradingAddress());
//                        business.setBusinessEmail(response.body().getData().getBusiness().getBusinessEmail());
//                        business.setBusinessPhoneNumber(response.body().getData().getBusiness().getBusinessPhoneNumber());
//                        business.setMerchant(response.body().getData().getBusiness().getMerchant());
//
//                        businessTerminal = new BusinessTerminal();
//                        businessTerminal.setId(response.body().getData().getBusinessTerminal().getId());
//                        businessTerminal.setTerminal(response.body().getData().getBusinessTerminal().getTerminal());
//                        businessTerminal.setPreAuthEnabled(response.body().getData().getBusinessTerminal().isPreAuthEnabled());
//                        businessTerminal.setCnp(response.body().getData().getBusinessTerminal().isCnp());
//                        businessTerminal.setAutoBatchEnabled(response.body().getData().getBusinessTerminal().isAutoBatchEnabled());
//                        businessTerminal.setBatchTime(response.body().getData().getBusinessTerminal().getBatchTime());
//                    }

//                    Transaction transaction = new Transaction();
//                    transaction.setId(response.body().getData().getId());
//                    if (response.body().getData().getBusiness() != null) {
//                        transaction.setBusiness(business);
//                        transaction.setBusinessTerminal(businessTerminal);
//                    }
//                    transaction.setCreatedAt(response.body().getData().getCreatedAt());
//                    transaction.setUpdatedAt(response.body().getData().getUpdatedAt());
//                    transaction.setTransactionType(response.body().getData().getTransactionType());
//                    transaction.setPan(response.body().getData().getPan());
//                    transaction.setUti(response.body().getData().getUti());
//                    transaction.setAmount(response.body().getData().getAmount());
//                    transaction.setDiscount(response.body().getData().getDiscount());
//                    transaction.setApproved(response.body().getData().isApproved());
//                    transaction.setCancelled(response.body().getData().isCancelled());
//                    transaction.setSigRequired(response.body().getData().isSigRequired());
//                    transaction.setPinVerified(response.body().getData().isPinVerified());
//                    transaction.setCurrency(response.body().getData().getCurrency());
//                    transaction.setTerminalId(response.body().getData().getTerminalId());
//                    transaction.setMerchantUid(response.body().getData().getMerchantUid());
//                    transaction.setCardType(response.body().getData().getCardType());
//                    transaction.setMerchant(response.body().getData().getMerchant());
//                    transaction.setStatus(response.body().getData().getStatus());
//
//                    AppConstants.executorService.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                localDb.transactionDao().insert(transaction);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });

                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<TransactionCreateResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<ReportCreateResponse> createReportData(Context context, Report request, String token) {
        final MutableLiveData<ReportCreateResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.createReport(accessToken, request).enqueue(new Callback<ReportCreateResponse>() {
            @Override
            public void onResponse(Call<ReportCreateResponse> call, Response<ReportCreateResponse> response) {
                if (response.isSuccessful()) {
//                    Toast.makeText(context, context.getString(R.string.report_successfully_generated), Toast.LENGTH_SHORT).show();
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
//                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ReportCreateResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<PBLCreateResponse> createPBLLink(String token, HashMap<String, Object> map) {
        final MutableLiveData<PBLCreateResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.createPBLTnxLink(accessToken, map).enqueue(new Callback<PBLCreateResponse>() {
            @Override
            public void onResponse(Call<PBLCreateResponse> call, Response<PBLCreateResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PBLCreateResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<ReportCreateResponse> updateReportData(Context context, Report request, String token, String id) {
        final MutableLiveData<ReportCreateResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.updateReport(accessToken, request, id).enqueue(new Callback<ReportCreateResponse>() {
            @Override
            public void onResponse(Call<ReportCreateResponse> call, Response<ReportCreateResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ReportCreateResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }


    public MutableLiveData<ReportListResponse> getReportListData(HashMap<String, String> map, String token) {
        final MutableLiveData<ReportListResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.getReportList(accessToken, map).enqueue(new Callback<ReportListResponse>() {
            @Override
            public void onResponse(Call<ReportListResponse> call, Response<ReportListResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ReportListResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<TransactionListResponse> getTransactionsListData(HashMap<String, String> map, String token) {
        final MutableLiveData<TransactionListResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.getTransactionsList(accessToken, map).enqueue(new Callback<TransactionListResponse>() {
            @Override
            public void onResponse(Call<TransactionListResponse> call, Response<TransactionListResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<TransactionListResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<LinkResponse> getPblTnxListData(String token, HashMap<String, String> map) {
        final MutableLiveData<LinkResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.getPblTrnList(accessToken, map).enqueue(new Callback<LinkResponse>() {
            @Override
            public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LinkResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<TransactionListResponse> getNextTransactionsList(String nextUrl, String token) {
        final MutableLiveData<TransactionListResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.getNextTransactionsList(accessToken, nextUrl).enqueue(new Callback<TransactionListResponse>() {
            @Override
            public void onResponse(Call<TransactionListResponse> call, Response<TransactionListResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<TransactionListResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<LinkResponse> getPblNextTransactionsList(String nextUrl, String token) {
        final MutableLiveData<LinkResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.getPblNextTrnList(accessToken, nextUrl).enqueue(new Callback<LinkResponse>() {
            @Override
            public void onResponse(Call<LinkResponse> call, Response<LinkResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LinkResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<ReportListResponse> getNextReportList(String nextUrl, String token) {
        final MutableLiveData<ReportListResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.getNextReportList(accessToken, nextUrl).enqueue(new Callback<ReportListResponse>() {
            @Override
            public void onResponse(Call<ReportListResponse> call, Response<ReportListResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ReportListResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<TransactionDetailsResponse> getTransactionDetails(String token, String id) {
        final MutableLiveData<TransactionDetailsResponse> liveData = new MutableLiveData<>();
        apiService.getTransactionDetails(token, id).enqueue(new Callback<TransactionDetailsResponse>() {
            @Override
            public void onResponse(Call<TransactionDetailsResponse> call, Response<TransactionDetailsResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<TransactionDetailsResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);

            }
        });
        return liveData;
    }

    public MutableLiveData<Object> requestEmailReceipt(String token, String email, String tnxId) {
        String accessToken = "Corona " + token;
        final MutableLiveData<Object> liveData = new MutableLiveData<>();
        apiService.requestEmailReceipt(accessToken, email, tnxId).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<Object> requestEmailPayment(String token, String email, String tnxId) {
        String accessToken = "Corona " + token;
        final MutableLiveData<Object> liveData = new MutableLiveData<>();
        apiService.requestEmailPayment(accessToken, email, tnxId).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }
}
