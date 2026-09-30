package com.paymentsave.paymentsave.network;

public interface Constants {

   // testing Server
    String BASE_URL = "https://stage.psapp.uk/";
    String MEDIA_URL = "https://stage.psapp.uk";
    String SOCKET_URL = "stage.psapp.uk/";

//    String BASE_URL = "https://pax-backend.psapp.uk/";
//    String MEDIA_URL = "https://pax-backend.psapp.uk";
//    String SOCKET_URL = "pax-backend.psapp.uk/";


    // Live Server
/*    String BASE_URL = "https://cloud.psapp.uk/";
    String MEDIA_URL = "https://cloud.psapp.uk";
    String SOCKET_URL = "cloud.psapp.uk/"; */

//    String BASE_URL = "https://paxuat.psapp.uk/";
//    String MEDIA_URL = "https://paxuat.psapp.uk";
//    String SOCKET_URL = "paxuat.psapp.uk/";

    // API Endpoints
    String PBL_TRANSACTION_CREATE_ENDPOINT = "api/v1/transaction/terminal/pbl-transaction-create/";
    String PBL_TRANSACTION_ENDPOINT = "api/v1/transaction/terminal/pbl-transactions/";
    String TRANSACTION_ENDPOINT = "api/v1/transaction/terminal/secure/";
    String TRANSACTION_UPDATE_ENDPOINT = "/api/v1/transaction/terminal/secure/{uti}/";
    String REPORT_ENDPOINT = "api/v1/transaction/terminal/transfer/";
    String ACCOUNT_VERIFY_ENDPOINT = "api/v1/account/validate-info/";
    String CONFIG_ENDPOINT = "api/v1/account/terminal/get-device-config/";
    String VALIDATE_ACTIVATION_ENDPOINT = "api/v1/account/terminal/validate-activation/";
    String SEND_RECEIPT_ENDPOINT = "api/v1/transaction/terminal/{uti}/send-receipt/";
    String SEND_EMAIL_ENDPOINT = "api/v1/transaction/terminal/{uti}/send-payment-email/";
    String REQUEST_ACTIVATION_ENDPOINT = "api/v1/account/terminal/request-activation/";
    String SUPERVISOR_PIN_ENDPOINT = "/api/v1/account/set-supervisor-pin/";
    String VERIFY_SUPERVISOR_PIN_ENDPOINT = "/api/v1/account/verify-supervisor-pin/";
    String INSIGHT_REPORT_ENDPOINT = "/api/v1/transaction/terminal/insight/report-new/";
    String CHANGE_PIN_REQUEST_ENDPOINT = "/authentication/api/v1/request-change-supervisorpin/";
}
