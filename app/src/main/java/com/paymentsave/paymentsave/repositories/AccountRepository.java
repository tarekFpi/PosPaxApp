package com.paymentsave.paymentsave.repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.utils.ErrorUtils;
import com.paymentsave.paymentsave.responses.AccountVerificationResponse.AccountVerificationResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationCodeResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationRequestResponse;
import com.paymentsave.paymentsave.responses.AnalyticReportRepose.AnalyticReportResponse;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.DeviceConfigResponse;
import com.paymentsave.paymentsave.responses.PinVerifyResponse.PinVerifyResponse;

import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository extends BaseRepository {
    private static final String TAG = "AccountRepository";

    public AccountRepository(Application application) {
        super(application);
    }

    public MutableLiveData<DeviceConfigResponse> getDeviceConfig(String serial) {
        final MutableLiveData<DeviceConfigResponse> liveData = new MutableLiveData<>();
        apiService.getDeviceConfig(serial).enqueue(new Callback<DeviceConfigResponse>() {
            @Override
            public void onResponse(Call<DeviceConfigResponse> call, Response<DeviceConfigResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DeviceConfigResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<AccountVerificationResponse> verifyAccountData(HashMap<String, String> queryMap) {
        final MutableLiveData<AccountVerificationResponse> liveData = new MutableLiveData<>();
        apiService.verifyAccountData(queryMap).enqueue(new Callback<AccountVerificationResponse>() {
            @Override
            public void onResponse(Call<AccountVerificationResponse> call, Response<AccountVerificationResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());

                }
            }

            @Override
            public void onFailure(Call<AccountVerificationResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<ActivationCodeResponse> validateTerminalActivation(HashMap<String, String> queryMap) {
        final MutableLiveData<ActivationCodeResponse> liveData = new MutableLiveData<>();
        apiService.validateTerminalActivation(queryMap).enqueue(new Callback<ActivationCodeResponse>() {
            @Override
            public void onResponse(Call<ActivationCodeResponse> call, Response<ActivationCodeResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ActivationCodeResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<ActivationRequestResponse> requestTerminalActivation(String token, String mid, String sn, String code) {
        final MutableLiveData<ActivationRequestResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.requestDeviceActivation(accessToken, mid, sn, code).enqueue(new Callback<ActivationRequestResponse>() {
            @Override
            public void onResponse(Call<ActivationRequestResponse> call, Response<ActivationRequestResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ActivationRequestResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }


    public MutableLiveData<Object> setSupervisorPin(HashMap<String, String> queMap) {
        final MutableLiveData<Object> liveData = new MutableLiveData<>();
        apiService.setSupervisorPin(queMap).enqueue(new Callback<Object>() {
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

    public MutableLiveData<PinVerifyResponse> verifySupervisorPin(HashMap<String, String> queryMap) {
        final MutableLiveData<PinVerifyResponse> liveData = new MutableLiveData<>();
        apiService.verifySupervisorPin(queryMap).enqueue(new Callback<PinVerifyResponse>() {
            @Override
            public void onResponse(Call<PinVerifyResponse> call, Response<PinVerifyResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PinVerifyResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<AnalyticReportResponse> getReportData(HashMap<String, Object> queryMap, String token) {
        final MutableLiveData<AnalyticReportResponse> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.getReportData(accessToken, queryMap).enqueue(new Callback<AnalyticReportResponse>() {
            @Override
            public void onResponse(Call<AnalyticReportResponse> call, Response<AnalyticReportResponse> response) {
                if (response.isSuccessful()) {
                    liveData.setValue(response.body());
                } else {
                    liveData.setValue(null);
                    ErrorUtils.handleError(context, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<AnalyticReportResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toasty.error(context, context.getString(R.string.failed_please_check_your_internet_connection), Toasty.LENGTH_SHORT).show();
                liveData.setValue(null);
            }
        });
        return liveData;
    }

    public MutableLiveData<Object> changeSupervisorPINRequest(String token, HashMap<String, String> queMap) {
        final MutableLiveData<Object> liveData = new MutableLiveData<>();
        String accessToken = "Corona " + token;
        apiService.requestPinChange(accessToken, queMap).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful() && response.code() == HttpsURLConnection.HTTP_OK) {
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
