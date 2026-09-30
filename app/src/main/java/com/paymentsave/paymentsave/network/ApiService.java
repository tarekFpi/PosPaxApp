package com.paymentsave.paymentsave.network;

import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.AccountVerificationResponse.AccountVerificationResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationCodeResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationRequestResponse;
import com.paymentsave.paymentsave.responses.AnalyticReportRepose.AnalyticReportResponse;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.DeviceConfigResponse;
import com.paymentsave.paymentsave.responses.PBLResponse.LinkResponse;
import com.paymentsave.paymentsave.responses.PBLResponse.PBLCreateResponse;
import com.paymentsave.paymentsave.responses.PinVerifyResponse.PinVerifyResponse;
import com.paymentsave.paymentsave.responses.ReportCreateResponse.ReportCreateResponse;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;
import com.paymentsave.paymentsave.responses.ReportListResponse.ReportListResponse;
import com.paymentsave.paymentsave.responses.TransactionCreateResponse;
import com.paymentsave.paymentsave.responses.TransactionDetailsResponse.TransactionDetailsResponse;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResponse;
import com.paymentsave.paymentsave.responses.TransactionUpdateResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiService {

    @POST(Constants.CONFIG_ENDPOINT)
    @FormUrlEncoded
    Call<DeviceConfigResponse> getDeviceConfig(@Field("serial_no") String serial);

    @POST(Constants.REQUEST_ACTIVATION_ENDPOINT)
    @FormUrlEncoded
    Call<ActivationRequestResponse> requestDeviceActivation(@Header("Authorization") String token, @Field("terminal_mid") String mid, @Field("terminal_sn") String sn, @Field("activation_code") String code);

    @POST(Constants.ACCOUNT_VERIFY_ENDPOINT)
    Call<AccountVerificationResponse> verifyAccountData(@Body HashMap<String, String> map);

    @POST(Constants.SUPERVISOR_PIN_ENDPOINT)
    Call<Object> setSupervisorPin(@Body HashMap<String, String> map);

    @POST(Constants.VERIFY_SUPERVISOR_PIN_ENDPOINT)
    Call<PinVerifyResponse> verifySupervisorPin(@Body HashMap<String, String> map);

    @POST(Constants.CHANGE_PIN_REQUEST_ENDPOINT)
    Call<Object> requestPinChange(@Header("Authorization") String token, @Body HashMap<String, String> map);

    @POST(Constants.TRANSACTION_ENDPOINT)
    Call<TransactionCreateResponse> createTransaction(@Header("Authorization") String token, @Body TransactionRequest request);

    @PATCH(Constants.TRANSACTION_UPDATE_ENDPOINT)
    Call<TransactionUpdateResponse> createOrUpdateTransaction(@Header("Authorization") String token, @Body TransactionRequest request, @Path("uti") String uti);

    @POST(Constants.REPORT_ENDPOINT)
    Call<ReportCreateResponse> createReport(@Header("Authorization") String token, @Body Report request);

    @PATCH("api/v1/transaction/terminal/transfer/{id}/")
    Call<ReportCreateResponse> updateReport(@Header("Authorization") String token, @Body Report request, @Path("id") String tnxId);

    @GET(Constants.VALIDATE_ACTIVATION_ENDPOINT)
    Call<ActivationCodeResponse> validateTerminalActivation(@QueryMap HashMap<String, String> map);

    @GET(Constants.REPORT_ENDPOINT)
    Call<ReportListResponse> getReportList(@Header("Authorization") String token, @QueryMap HashMap<String, String> map);

    @GET(Constants.TRANSACTION_ENDPOINT)
    Call<TransactionListResponse> getTransactionsList(@Header("Authorization") String token, @QueryMap HashMap<String, String> map);

    @GET
    Call<TransactionListResponse> getNextTransactionsList(@Header("Authorization") String token, @Url String url);

    @GET(Constants.PBL_TRANSACTION_ENDPOINT)
    Call<LinkResponse> getPblTrnList(@Header("Authorization") String token, @QueryMap HashMap<String, String> map);

    @GET
    Call<LinkResponse> getPblNextTrnList(@Header("Authorization") String token, @Url String url);

    @GET
    Call<ReportListResponse> getNextReportList(@Header("Authorization") String token, @Url String url);

    @GET("api/v1/transaction/transaction/{id}/")
    Call<TransactionDetailsResponse> getTransactionDetails(@Header("Authorization") String token, @Path("id") String tnxId);

    @POST(Constants.INSIGHT_REPORT_ENDPOINT)
    Call<AnalyticReportResponse> getReportData(@Header("Authorization") String token, @Body HashMap<String, Object> map);

    @POST(Constants.PBL_TRANSACTION_CREATE_ENDPOINT)
    Call<PBLCreateResponse> createPBLTnxLink(@Header("Authorization") String token, @Body HashMap<String, Object> map);

    @POST(Constants.SEND_RECEIPT_ENDPOINT)
    @FormUrlEncoded
    Call<Object> requestEmailReceipt(@Header("Authorization") String token, @Field("email") String email, @Path("uti") String tnxId);

    @POST(Constants.SEND_EMAIL_ENDPOINT)
    @FormUrlEncoded
    Call<Object> requestEmailPayment(@Header("Authorization") String token, @Field("email") String email, @Path("uti") String tnxId);


//    @GET
//    Call<ChatResponse> getHomeChats(@Header("Authorization") String token, @Url String url);

//    @GET(Constants.CONTACT_LIST_ENDPOINT)
//    Call<CreateContactResponse> getContacts(@Header("Authorization") String token);

}
