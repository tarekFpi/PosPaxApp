package com.paymentsave.paymentsave.coreapp.receiver;

import static com.eft.libpositive.messages.IMessages.TRANSACTION_ACK_EVENT;
import static com.eft.libpositive.messages.IMessages.TRANSACTION_RESULT_EVENT;
import static com.eft.libpositive.messages.IMessages.TRANSACTION_STATUS_EVENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.eft.libpositive.PosIntegrate;
import com.eft.libpositive.wrappers.HistoryTransResult;
import com.eft.libpositive.wrappers.PositiveError;
import com.eft.libpositive.wrappers.PositiveReportResult;
import com.eft.libpositive.wrappers.PositiveTransResult;
import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.coreapp.activities.CounterPay.CounterPayActivity;
import com.paymentsave.paymentsave.coreapp.activities.splitBillPay.SplitBillPayActivity;
import com.paymentsave.paymentsave.coreapp.roomdb.DBHelper;
import com.paymentsave.paymentsave.coreapp.utils.ObjectUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.network.WebSocketUtils;
import com.paymentsave.paymentsave.responses.TransactionResponse;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.utils.PositiveConstantsUtils;
import com.paymentsave.paymentsave.weblink.WebLinkIntegrate;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

/**************************************************************************************************/
/**************************************************************************************************/

/*
   This class provides the receiver that is declared in the manifest.
   It is used to receive the response to a transaction and debug the result.
   Once the result is received it brings this app back to the front with a call to startActivity
*/

/**************************************************************************************************/

/**************************************************************************************************/
public class PositiveLaunchReceiver extends BroadcastReceiver {

    /* These need to match up with the manifest for app and the service (DOP NOT CHANGE) */
    private static final String TAGD = "PLR";
    private static final String TAG = "PositiveLaunchReceiver";
    ArrayList<TransactionResponse> responseList;
    ArrayList<HistoryTransResult> transHistoryList;
    String resultType;
    String responseTitle;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WebLinkIntegrate.enabled) return;

        if (intent != null) {
            try {

//            Log.d(TAGD, "onReceive: " + context.getPackageName());
//            Log.d(TAGD, "onReceive: " + intent.getComponent());
//            Log.d(TAGD, "onReceive: " + intent.getAction());
//            Log.d(TAGD, "onReceive: " + intent.getStringExtra("UTI"));
//            Log.d(TAGD, "onReceive: " + intent.getIntExtra("StatusEnum", -1));
//            Log.d(TAGD, "onReceive: " + intent.getStringExtra("StatusEvent"));
//            Log.d(TAGD, "onReceive: " + intent.getStringExtra("ReceiverResultType"));
//            Log.d(TAGD, "onReceive: " + intent.getBooleanExtra("Approved", false));
//            Log.d(TAGD, "onReceive: " + intent.getBooleanExtra("TransResponse", false));


                if (TRANSACTION_RESULT_EVENT.equals(intent.getAction())) {
                    Log.i(TAG, "TRANSACTION_RESULT_EVENT = " + intent.getAction());
                    Bundle extras = intent.getExtras();

                    JSONObject jsonObject = new JSONObject();

                    if (extras != null) {
                        for (String key : extras.keySet()) {
                            try {
                                Object value = extras.get(key);
                                jsonObject.put(key, value != null ? value : JSONObject.NULL);
                            } catch (JSONException e) {
                                Log.e(TAG, "Failed to convert extra: " + key, e);
                                // Telemetry: PositiveLaunchReceiver.onReceive — result extras could not be converted to JSON.
                                logReceiveError(context, "onReceive", String.valueOf(e.getMessage()), e);
                            }
                        }
                    }

                    TelemetryLogger.info(
                            SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                            "PositiveLaunchReceiver",
                            String.valueOf(jsonObject)
                    );



                    if (intent.hasExtra("ReceiverResultType"))
                        resultType = intent.getStringExtra("ReceiverResultType");
                    if (resultType != null && resultType.equals("Reports")) {
                        PositiveReportResult result = PosIntegrate.unpackReport(context, intent);

                        if (result != null) {
                            String eventName = "";
                            if (result.getReportType().equals("XReport")) {
                                responseTitle = "X Report ";
                                eventName = "XREPORT_COMD_RESULT";
                            } else {
                                responseTitle = "Z Report ";
                                eventName = "ZREPORT_COMD_RESULT";
                            }
                            populateReportList(context, result);
                            // Trigger Event
                            triggerResultEvent(context, result, eventName);
                        }
                    } else if (resultType != null && resultType.equals("Error")) {
                        String reason = intent.getStringExtra("ReceiverResultError");
                        HomeActivity.report_error_response = reason;
                        /// Trigger Event
                        triggerErrorResultEvent(context, reason, "EXECUTION-ERROR");
                    } else if (resultType != null && resultType.equals("History Reports")) {
                        transHistoryList = intent.getParcelableArrayListExtra("HistoryList");
                        responseTitle = "History Report ";
                        populateHistoryList();

                    } else {

                        // Unpack the transaction result. This will return null of the event was not for us, or if there was an error
                        PositiveTransResult result = PosIntegrate.unpackResult(context, intent);

                        if (result != null) {
                            boolean transFound = intent.getBooleanExtra("TransResponse", false);
                            if (transFound) {
                                //Populate Response list here
                                responseTitle = "Transaction Response ";
                                populateResponseList(context, result);
                                PositiveConstantsUtils.lastReceivedUTI = result.getUTI();                           // Trigger Event
                                triggerResultEvent(context, result);
                                // Debug the result
                                printDebugLog(result);
                            } else {
                                Log.i(TAG, "Transaction not found");
                                PositiveError error = result.getError();
                                String errorText = result.getErrorText();
                                Toast.makeText(context, "Transaction Not Found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    try {
                        displayTransactionDetails(context);
                    } catch (Exception e) {
                        Log.e(TAG, "displayTransactionDetails failed", e);
                        // Telemetry: PositiveLaunchReceiver.onReceive.displayTransactionDetails — nested display call failed.
                        logReceiveError(
                                context,
                                "onReceive.displayTransactionDetails",
                                String.valueOf(e.getMessage()),
                                e
                        );
                    }
                }
                else if (TRANSACTION_STATUS_EVENT.equals(intent.getAction())) {
                    Bundle extras = intent.getExtras();
                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>");
                    if (extras != null) {
                        for (String key : extras.keySet()) {
                            Object value = extras.get(key);
                            Log.d(TAG, key + " = " + value);
                        }
                    } else {
                        Log.d(TAG, "No extras found");
                    }
                    Log.d(TAG, ">>>>>>>>>>>>>>>>>>>>>");

                    JSONObject jsonObject = new JSONObject();

                    if (extras != null) {
                        for (String key : extras.keySet()) {
                            try {
                                Object value = extras.get(key);
                                jsonObject.put(key, value != null ? value : JSONObject.NULL);
                            } catch (JSONException e) {
                                Log.e(TAG, "Failed to convert extra: " + key, e);

                                // Telemetry: PositiveLaunchReceiver.onReceive — status extras could not be converted to JSON.
                                logReceiveError(context, "onReceive", String.valueOf(e.getMessage()), e);
                            }
                        }
                    }

                    TelemetryLogger.info(
                            SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                            "PositiveLaunchReceiver",
                            String.valueOf(jsonObject)
                    );

                      String statusEvent = intent.getStringExtra("StatusEvent");
                    if (statusEvent != null && statusEvent.equals("GetCard Screen Displayed")) {
                        String tnxType = intent.getStringExtra("TransType");
                        String uti = intent.getStringExtra("UTI");
                        String amountValue = intent.getStringExtra("Amount");
                        String cashbackValue = intent.getStringExtra("Cashback");
                        String tipValue = intent.getStringExtra("Tip");

                        Log.d(TAG, "Final Output: " + cashbackValue + " " + tipValue);

                        long amount = 0L;
                        long tip = 0L;
                        long cashback = 0L;

                        if (amountValue != null && !amountValue.trim().isEmpty()) {
                            try {
                                amount = Long.parseLong(amountValue.trim());
                                tip = Long.parseLong(tipValue.trim());
                                cashback = Long.parseLong(cashbackValue.trim());
                            } catch (NumberFormatException e) {
                                amount = 0L;
                                tip = 0L;
                                cashback = 0L;
                                // Telemetry: PositiveLaunchReceiver.onReceive — amount, tip, or cashback is not a valid number.
                                logReceiveError(
                                        context,
                                        "onReceive",
                                        String.valueOf(e.getMessage()),
                                        e
                                );
                            }
                        }

                        if (!TextUtils.isEmpty(uti) && !TextUtils.isEmpty(tnxType)) {
                            String tid = SharedHelper.getStringData(context, AppConstants.SharedPref.TID);
                            String mid = SharedHelper.getStringData(context, AppConstants.SharedPref.MID);
                            ArrayList<TransactionResponse> initialResponse = new ArrayList<>();
                            switch (tnxType) {
                                case "SALE":
                                case "SaleAuto":
                                    initialResponse.add(new TransactionResponse("Type", "SALE_AUTO"));
                                    break;
                                case "REFUND":
                                case "RefundAuto":
                                    initialResponse.add(new TransactionResponse("Type", "REFUND_AUTO"));
                                    break;
                                case "PREAUTH":
                                case "PreAuthAuto":
                                    initialResponse.add(new TransactionResponse("Type", "PREAUTH_AUTO"));
                                    break;
                                case "COMPLETION":
                                case "CompletionAuto":
                                    initialResponse.add(new TransactionResponse("Type", "COMPLETION_AUTO"));
                                    break;
                                case "MANUAL_REVERSAL":
                                case "ManualReversalAuto":
                                    initialResponse.add(new TransactionResponse("Type", "MANUAL_REVERSAL_AUTO"));
                                    break;
                                case "CARD_NOT_PRESENT":
                                    initialResponse.add(new TransactionResponse("Type", "CARD_NOT_PRESENT"));
                                    break;
                                case "CARD_NOT_PRESENT_REFUND":
                                    initialResponse.add(new TransactionResponse("Type", "CARD_NOT_PRESENT_REFUND"));
                                    break;
                            }

                            String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
                            if (!TextUtils.isEmpty(sessionId)) {
                                initialResponse.add(new TransactionResponse("RequestID", SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID)));
                            }

                            if (!TextUtils.isEmpty(AppConstants.tnxNote)) {
                                initialResponse.add(new TransactionResponse("TransactionNote", AppConstants.tnxNote));
                            }

                            initialResponse.add(new TransactionResponse("Amount", String.valueOf(amount)));
                            initialResponse.add(new TransactionResponse("Cashback", String.valueOf(cashback)));
                            initialResponse.add(new TransactionResponse("Gratuity", String.valueOf(tip)));
                            initialResponse.add(new TransactionResponse("UTI", uti));
                            initialResponse.add(new TransactionResponse("IsResultResponse", "false"));
                            initialResponse.add(new TransactionResponse("terminalId", tid));
                            initialResponse.add(new TransactionResponse("MerchantId", mid));

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                initialResponse.add(new TransactionResponse("Created At", String.valueOf(ZonedDateTime.now(ZoneId.of("Europe/London")))));
                            }

                            // Store Data
                            DBHelper.storeTransactionDataLocal(context, initialResponse);
                            // Push to Server
                            pushData(context, false, initialResponse);
                        }
                    }

                    String deviceUid = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_DEVICE_UID);
                    String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
                    if (!TextUtils.isEmpty(sessionId) && WebSocketUtils.isSocketConnected) {
                        String jsonString = "{\"event\":\"CONNECT_TERMINAL_NOTIFICATION\",\"session_id\":\"" + sessionId + "\",\"message\":\"" + statusEvent + "\"}";
                        WebSocketUtils.mWebSocketClient.send(jsonString);
                    }
                    if (safeIsConnected(AwsIotTerminalClient.client)) {
                        String jsonString = "{\"event\":\"CONNECT_TERMINAL_NOTIFICATION\"," + "\"session_id\":\"" + sessionId + "\"," + "\"message\":\"" + statusEvent + "\"," + "\"device_uid\":\"" + deviceUid + "\"}";
                        AwsIotTerminalClient.publishResponse(jsonString);
                    }
                }

//            else if (TRANSACTION_ACK_EVENT.equals(intent.getAction())) {
//                Bundle extras = intent.getExtras();
//                String tnxType = intent.getStringExtra("TransType");
//                String uti = intent.getStringExtra("UTI");
//                long amount = intent.getLongExtra("Amount", 0);
//                long tip = intent.getLongExtra("Tip", 0);
//                long cashback = intent.getLongExtra("Cashback", 0);
//                if (!TextUtils.isEmpty(uti) && !TextUtils.isEmpty(tnxType)) {
//                    String tid = SharedHelper.getStringData(context, AppConstants.SharedPref.TID);
//                    String mid = SharedHelper.getStringData(context, AppConstants.SharedPref.MID);
//                    ArrayList<TransactionResponse> initialResponse = new ArrayList<>();
//                    switch (tnxType) {
//                        case "SaleAuto":
//                            initialResponse.add(new TransactionResponse("Type", "SALE_AUTO"));
//                            break;
//                        case "RefundAuto":
//                            initialResponse.add(new TransactionResponse("Type", "REFUND_AUTO"));
//                            break;
//                    }
//
//                    String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
//                    if (!TextUtils.isEmpty(sessionId)) {
//                        initialResponse.add(new TransactionResponse("RequestID", SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID)));
//                    }
//                    if (!TextUtils.isEmpty(AppConstants.tnxNote)) {
//                        initialResponse.add(new TransactionResponse("TransactionNote", AppConstants.tnxNote));
//                    }
//
//                    initialResponse.add(new TransactionResponse("Amount", String.valueOf(amount)));
//                    initialResponse.add(new TransactionResponse("Cashback", String.valueOf(cashback)));
//                    initialResponse.add(new TransactionResponse("Gratuity", String.valueOf(tip)));
//                    initialResponse.add(new TransactionResponse("UTI", uti));
//                    initialResponse.add(new TransactionResponse("IsResultResponse", "false"));
//                    initialResponse.add(new TransactionResponse("terminalId", tid));
//                    initialResponse.add(new TransactionResponse("MerchantId", mid));
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        initialResponse.add(new TransactionResponse("Created At", String.valueOf(ZonedDateTime.now(ZoneId.of("Europe/London")))));
//                    }
//
//                    // Store Data
//                    DBHelper.storeTransactionDataLocal(context, initialResponse);
//                    // Push to Server
//                    pushData(context, false, initialResponse);
//                }
//            }
            } catch (Exception e) {
                Log.e(TAG, "onReceive failed for action: " + intent.getAction(), e);
                // Telemetry: PositiveLaunchReceiver.onReceive — unexpected failure while handling the broadcast.
                logReceiveError(context, "onReceive", String.valueOf(e.getMessage()), e);
            }
        } else {
            Log.d(TAG, "Receiver intent is null");
        }
    }

    private void triggerResultEvent(Context context, PositiveTransResult result) {
        try {
            // Create the main JSON object
            JSONObject json = new JSONObject();
            json.put("TransactionType", result.getTransType());
            json.put("UTI", result.getUTI());
            json.put("Amount", result.getAmountTrans());
            json.put("Cashback", result.getAmountCashback());
            json.put("Approved", result.isTransApproved());
            json.put("Gratuity", result.getAmountGratuity());
            json.put("Cancelled", result.isTransCancelled());
            json.put("SigRequired", result.isCvmSigRequired());
            json.put("PINVerified", result.isCvmPinVerified());
            json.put("Currency", result.getTransCurrencyCode());
            json.put("Tid", result.getTerminalId());
            json.put("Mid", result.getMerchantId());
            json.put("Version", result.getSoftwareVersion());
            json.put("CardHolderReceipt", result.getCardholderReceipt());
            json.put("MerchantHolderReceipt", result.getMerchantReceipt());
            if (!TextUtils.isEmpty(AppConstants.tnxNote)) {
                json.put("Reference", AppConstants.tnxNote);
            }

            // Check if transaction details are available
            if (result.isTransDetails()) {
                JSONObject transactionDetails = new JSONObject();
                transactionDetails.put("ReceiptNumber", result.getReceiptNumber());
                transactionDetails.put("RRN", result.getRetrievalReferenceNumber());
                transactionDetails.put("ResponseCode", result.getResponseCode());
                transactionDetails.put("Stan", result.getStan());
                transactionDetails.put("AuthCode", result.getAuthorisationCode());
                transactionDetails.put("MerchantTokenId", result.getMerchantTokenId());

                // Add card details if applicable
                String cardType = result.getCardType();
                transactionDetails.put("CardType", cardType);

                if ("EMV".equals(cardType) || "CTLS".equals(cardType)) {
                    JSONObject cardDetails = new JSONObject();
                    cardDetails.put("AID", result.getEmvAid());
                    cardDetails.put("TSI", result.getEmvTsi());
                    cardDetails.put("TVR", result.getEmvTvr());
                    cardDetails.put("CardHolder", result.getEmvCardholderName());
                    cardDetails.put("Cryptogram", byteArrayToHexString(result.getEmvCryptogram()));
                    cardDetails.put("CryptogramType", result.getEmvCryptogramType());
                    transactionDetails.put("CardDetails", cardDetails);
                }

                // Add remaining card information
                transactionDetails.put("PAN", result.getCardPan());
                transactionDetails.put("ExpiryDate", result.getCardExpiryDate());
                transactionDetails.put("StartDate", result.getCardStartDate());
                transactionDetails.put("Scheme", result.getCardScheme());
                transactionDetails.put("PSN", result.getCardPanSequenceNumber());

                // Add transaction details to main JSON
                json.put("TransactionDetails", transactionDetails);
            }

            String deviceUid = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_DEVICE_UID);
            String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
            if (!TextUtils.isEmpty(sessionId) && WebSocketUtils.isSocketConnected) {
                String eventTitle = "";
                if (result.getTransType().equals("SALE_AUTO")) {
                    eventTitle = "SALE_COMD_RESULT";
                } else if (result.getTransType().equals("CASHBACK_AUTO")) {
                    eventTitle = "SALE_COMD_RESULT";
                } else if (result.getTransType().equals("REFUND_AUTO")) {
                    eventTitle = "REFUND_COMD_RESULT";
                }
                String eventString = "{\"event\":\"" + eventTitle + "\",\"session_id\":\"" + sessionId + "\",\"message\":" + json + "}";
                WebSocketUtils.mWebSocketClient.send(eventString);
                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, "");
            }
            if (safeIsConnected(AwsIotTerminalClient.client)) {
                String eventTitle = "";
                if (result.getTransType().equals("SALE_AUTO")) {
                    eventTitle = "SALE_COMD_RESULT";
                } else if (result.getTransType().equals("CASHBACK_AUTO")) {
                    eventTitle = "SALE_COMD_RESULT";
                } else if (result.getTransType().equals("REFUND_AUTO")) {
                    eventTitle = "REFUND_COMD_RESULT";
                }
                String eventString = "{\"event\":\"" + eventTitle + "\"," + "\"session_id\":\"" + sessionId + "\"," + "\"device_uid\":\"" + deviceUid + "\"," + "\"message\":" + json + "}";
                AwsIotTerminalClient.publishResponse(eventString);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error generating transaction JSON: " + e.getMessage(), e);

            /// Telemetry: PositiveLaunchReceiver.triggerResultEvent — failed to build or publish the transaction result.
            logReceiveError(context, "triggerResultEvent", String.valueOf(e.getMessage()), e);
        }
    }

    private void triggerErrorResultEvent(Context context, String reason, String eventTitle) {
        try {
            String deviceUid = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_DEVICE_UID);
            String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
            if (!TextUtils.isEmpty(sessionId) && WebSocketUtils.isSocketConnected) {
                Gson gson = new Gson();
                String json = gson.toJson(reason);
                String eventString = "{\"event\":\"" + eventTitle + "\",\"session_id\":\"" + sessionId + "\",\"message\":" + json + "}";
                WebSocketUtils.mWebSocketClient.send(eventString);
                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, "");
            }
            if (safeIsConnected(AwsIotTerminalClient.client)) {
                Gson gson = new Gson();
                String json = gson.toJson(reason);
                String eventString = "{\"event\":\"" + eventTitle + "\"," + "\"session_id\":\"" + sessionId + "\"," + "\"device_uid\":\"" + deviceUid + "\"," + "\"message\":" + json + "}";
                AwsIotTerminalClient.publishResponse(eventString);
                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, "");
            }
        } catch (Exception e) {
            Log.e(TAG, "triggerErrorResultEvent failed: " + eventTitle, e);

            /// Telemetry: PositiveLaunchReceiver.triggerErrorResultEvent — failed to publish the execution error.
            logReceiveError(context, "triggerErrorResultEvent", String.valueOf(e.getMessage()), e);
        }
    }

    private void triggerResultEvent(Context context, PositiveReportResult result, String eventTitle) {
        try {
            String deviceUid = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_DEVICE_UID);
            String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
            if (!TextUtils.isEmpty(sessionId) && WebSocketUtils.isSocketConnected) {
                Gson gson = new Gson();
                String json = gson.toJson(result);
                String eventString = "{\"event\":\"" + eventTitle + "\",\"session_id\":\"" + sessionId + "\",\"message\":" + json + "}";
                WebSocketUtils.mWebSocketClient.send(eventString);
                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, "");
            }
            if (safeIsConnected(AwsIotTerminalClient.client)) {
                Gson gson = new Gson();
                String json = gson.toJson(result);
                String eventString = "{\"event\":\"" + eventTitle + "\"," + "\"session_id\":\"" + sessionId + "\"," + "\"device_uid\":\"" + deviceUid + "\"," + "\"message\":" + json + "}";
                AwsIotTerminalClient.publishResponse(eventString);
                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, "");
            }
        } catch (Exception e) {
            Log.e(TAG, "triggerResultEvent report failed: " + eventTitle, e);

            /// Telemetry: PositiveLaunchReceiver.triggerResultEvent — failed to publish the X/Z report result.
            logReceiveError(context, "triggerResultEvent", String.valueOf(e.getMessage()), e);
        }
    }

    private boolean safeIsConnected(MqttAndroidClient c) {
        if (c == null) return false;
        try {
            return c.isConnected();
        } catch (IllegalArgumentException e) {
            // Invalid ClientHandle => client/service not in a usable state
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void printDebugLog(PositiveTransResult result) {
        Log.i(TAG, "Transaction Type = " + result.getTransType());
        Log.i(TAG, "UTI = " + result.getUTI());
        Log.i(TAG, "Amount = " + result.getAmountTrans());
        Log.i(TAG, "Approved = " + result.isTransApproved());
        Log.i(TAG, "Gratuity = " + result.getAmountGratuity());
        Log.i(TAG, "Cashback = " + result.getAmountCashback());
        Log.i(TAG, "Cancelled = " + result.isTransCancelled());
        Log.i(TAG, "SigRequired = " + result.isCvmSigRequired());
        Log.i(TAG, "PINVerified = " + result.isCvmPinVerified());
        Log.i(TAG, "Currency = " + result.getTransCurrencyCode());
        Log.i(TAG, "Tid = " + result.getTerminalId());
        Log.i(TAG, "Mid = " + result.getMerchantId());
        Log.i(TAG, "Version = " + result.getSoftwareVersion());
        Log.i(TAG, "Cardholder Receipt = " + result.getCardholderReceipt());
        Log.i(TAG, "Merchant Receipt = " + result.getMerchantReceipt());
        Log.i(TAG, "TransDateTime = " + result.getTransDateTime());
        Log.i(TAG, "Error Code = " + result.getErrorCode());
        Log.i(TAG, "Error = " + result.getErrorText());
        Log.i(TAG, "Error Text = " + result.getErrorCode() + ", " + result.getErrorText());

        if (result.isTransDetails()) {
            Log.i(TAG, "Transaction Details:");
            Log.i(TAG, "ReceiptNumber = " + result.getReceiptNumber());
            Log.i(TAG, "RRN = " + result.getRetrievalReferenceNumber());
            Log.i(TAG, "ResponseCode = " + result.getResponseCode());
            Log.i(TAG, "Stan = " + result.getStan());
            Log.i(TAG, "AuthCode = " + result.getAuthorisationCode());
            Log.i(TAG, "MerchantTokenId = " + result.getMerchantTokenId());

            String cardType = result.getCardType();
            Log.i(TAG, "CardType = " + cardType);

            if (cardType.compareTo("EMV") == 0 || cardType.compareTo("CTLS") == 0) {

                Log.i(TAG, "AID = " + result.getEmvAid());
                Log.i(TAG, "TSI = " + result.getEmvTsi());
                Log.i(TAG, "TVR = " + result.getEmvTvr());
                Log.i(TAG, "CardHolder = " + result.getEmvCardholderName());
                Log.i(TAG, "Cryptogram = " + byteArrayToHexString(result.getEmvCryptogram()));
                Log.i(TAG, "CryptogramType = " + result.getEmvCryptogramType());

            }
            Log.i(TAG, "PAN = " + result.getCardPan());
            Log.i(TAG, "ExpiryDate = " + result.getCardExpiryDate());
            Log.i(TAG, "StartDate = " + result.getCardStartDate());
            Log.i(TAG, "Scheme = " + result.getCardScheme());
            Log.i(TAG, "PSN = " + result.getCardPanSequenceNumber());
            Log.i(TAG, "Card Holder Receipt = " + result.getCardholderReceipt());
            Log.i(TAG, "Merchant Receipt = " + result.getMerchantReceipt());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime local = LocalDateTime.parse(result.getTransDateTime(), formatter);
                    ZoneId zone = ZoneId.of("Europe/London");
                    ZonedDateTime zoned = local.atZone(zone);
                    Log.i(TAG, "Created At = " + zoned);
                } catch (Exception e) {
                    Log.i(TAG, "Created At = " + ZonedDateTime.now(ZoneId.of("Europe/London")));
                }
            }
        }
    }

    private void populateHistoryList() {
        responseList = new ArrayList<>();
        for (HistoryTransResult historyItem : transHistoryList) {
            responseList.add(new TransactionResponse("Type", "" + historyItem.getTransType()));
            responseList.add(new TransactionResponse("Amount", "" + historyItem.getTransAmount() / 100));
            responseList.add(new TransactionResponse("Status", "" + historyItem.getTransApproved()));
            responseList.add(new TransactionResponse("Date Time", "" + historyItem.getTransDate()));
            responseList.add(new TransactionResponse("PAN", "" + historyItem.getTransPan()));
            responseList.add(new TransactionResponse("RNN", "" + historyItem.getRnn()));
            responseList.add(new TransactionResponse("Receipt No", "" + historyItem.getReceiptNo()));
            responseList.add(new TransactionResponse("", ""));
        }
    }

    private void displayTransactionDetails(Context context) {

        if (responseList != null && !responseList.isEmpty()) {
            boolean isCancelled = false;
            boolean isReportData = false;
            boolean isApproved = false;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                isReportData = responseList.stream().anyMatch(response -> response.getTransResponseName().equals("Is Report Response") && response.getTransResponseValue().trim().equals("true"));

                isCancelled = responseList.stream().anyMatch(response -> response.getTransResponseName().equals("Cancelled") && response.getTransResponseValue().trim().equals("true"));

                isApproved = responseList.stream().anyMatch(response -> response.getTransResponseName().equals("Approved") && response.getTransResponseValue().trim().equals("true"));
            }

            pushData(context, isReportData, responseList);

//            if (isApproved) {
//                // Refresh transaction reference
//                AppConstants.tnxNote = "";
//            }

            if (AppConstants.isSplitBillRunning && !isReportData) {
                Gson gson = new Gson();
                String responseListJson = gson.toJson(HomeActivity.generateTransactionObject(context, responseList));

                Intent responseintent = new Intent(context, SplitBillPayActivity.class);
                responseintent.putExtra("response", responseListJson);

                responseintent.putExtra("total_amount", AppConstants.splitBillTotalAmount);
                responseintent.putExtra("total_people", AppConstants.totalPeople);
                responseintent.putExtra("due_amount", AppConstants.dueAmount);
                responseintent.putExtra("initiated_payment", AppConstants.initiatedPaymentNumber);
                responseintent.putExtra("is_equal_split", AppConstants.isEqualSplit);

                responseintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(responseintent);

            } else if (!isCancelled && !isReportData) {

                try {
                    // Refresh transaction reference
                    AppConstants.tnxNote = "";

                    Gson gson = new Gson();
                    String responseListJson = gson.toJson(ObjectUtils.generateTransactionRequest(context, responseList));
                    Intent responseintent;
                    if (SharedHelper.getBooleanData(context, AppConstants.SharedPref.INTEGRATION_MODE)) {
                        responseintent = new Intent(context, CounterPayActivity.class);
                    } else {
                        responseintent = new Intent(context, HomeActivity.class);
                    }
                    responseintent.putExtra("response_list", responseListJson);
                    responseintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(responseintent);
                } catch (Exception e) {

                    Log.e(TAG, "displayTransactionDetails: " + e.getMessage(), e);
                    // Telemetry: PositiveLaunchReceiver.displayTransactionDetails — failed to open the result screen.
                    logReceiveError(
                            context,
                            "displayTransactionDetails",
                            String.valueOf(e.getMessage()),
                            e
                    );
                }

            } else {
                HomeActivity.report_response = responseList;
            }
        }
    }

    private void pushData(Context context, boolean isReportData, ArrayList<TransactionResponse> responseList) {
        if (isReportData) DBHelper.storeReportDataLocal(context, responseList);
        else DBHelper.storeTransactionDataLocal(context, responseList);

        // Schedule the worker
//            WorkRequest syncWorkRequest = new OneTimeWorkRequest.Builder(SyncManager.class)
//                    .setConstraints(new Constraints.Builder()
//                            .setRequiredNetworkType(NetworkType.CONNECTED)
//                            .build())
//                    .build();
//            HomeActivity.workManager.enqueue(syncWorkRequest);

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(SyncManager.class).build();
        WorkManager.getInstance(context).enqueue(workRequest);
    }

    private void populateReportList(Context context, PositiveReportResult result) {
        responseList = new ArrayList<>();
        String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
        if (!TextUtils.isEmpty(sessionId)) {
            responseList.add(new TransactionResponse("RequestID", SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID)));
        }
        responseList.add(new TransactionResponse("PS App Version", String.format("v%s", BuildConfig.VERSION_NAME)));
        responseList.add(new TransactionResponse("Report Type", "" + result.getReportType()));
        responseList.add(new TransactionResponse("Result Type", "" + result.getResultType()));
        responseList.add(new TransactionResponse("Report Totals", "" + result.getReportTotals()));
        responseList.add(new TransactionResponse("Completion Count", "" + result.getCompletionCount()));
        responseList.add(new TransactionResponse("Completion Amount", "" + result.getCompletionAmount()));
        responseList.add(new TransactionResponse("Cashback Count", "" + result.getCashbackCount()));
        responseList.add(new TransactionResponse("Cashback Amount", "" + result.getCashbackAmount()));
        responseList.add(new TransactionResponse("Gratuity Count", "" + result.getGratuityCount()));
        responseList.add(new TransactionResponse("Gratuity Amount", "" + result.getGratuityAmount()));
        responseList.add(new TransactionResponse("Refund Count", "" + result.getRefundCount()));
        responseList.add(new TransactionResponse("Refund Amount", "" + result.getRefundAmount()));
        responseList.add(new TransactionResponse("Sale Count", "" + result.getSaleCount()));
        responseList.add(new TransactionResponse("Sale Amount", "" + result.getSaleAmount()));
        responseList.add(new TransactionResponse("Report Send Status Error", "" + result.getReportSendStatusError()));
        responseList.add(new TransactionResponse("Is Report Response", "" + result.isReportResponse()));
        if (result.getReceipt() != null) {
            responseList.add(new TransactionResponse("Receipt", "" + String.join("&&", result.getReceipt())));
        } else {
            responseList.add(new TransactionResponse("Receipt", ""));
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            responseList.add(new TransactionResponse("Created At", "" + ZonedDateTime.now(ZoneId.of("Europe/London"))));
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (result.getReceipt().length > 0) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime local = LocalDateTime.parse(result.getReceipt()[9].trim(), formatter);
                    ZoneId zone = ZoneId.of("Europe/London");
                    ZonedDateTime zoned = local.atZone(zone);
                    responseList.add(new TransactionResponse("Created At", "" + zoned));
                } catch (Exception e) {
                    responseList.add(new TransactionResponse("Created At", "" + ZonedDateTime.now(ZoneId.of("Europe/London"))));
                }
            } else {
                responseList.add(new TransactionResponse("Created At", "" + ZonedDateTime.now(ZoneId.of("Europe/London"))));
            }
        }

        JSONObject reportJson = new JSONObject();
        try {
            for (TransactionResponse item : responseList) {
                reportJson.put(
                        item.getTransResponseName(),
                        item.getTransResponseValue() != null
                                ? item.getTransResponseValue()
                                : JSONObject.NULL
                );
            }
        } catch (JSONException e) {
            // Telemetry: PositiveLaunchReceiver.populateReportList — failed to build report JSON.
            logReceiveError(context, "populateReportList", String.valueOf(e.getMessage()), e);
        }

        /// Telemetry: PositiveLaunchReceiver.populateReportList — full report payload.
        TelemetryLogger.info(
                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                TAG + ".populateReportList",
                String.valueOf(reportJson)
        );

        printReportDebugLog(result);
    }

    private void printReportDebugLog(PositiveReportResult result) {
        Log.i(TAG, "Result Type = " + result.getResultType());
        Log.i(TAG, "Report Type = " + result.getReportType());
        Log.i(TAG, "Report Totals = " + result.getReportTotals());
        Log.i(TAG, "Completion Count = " + result.getCompletionCount());
        Log.i(TAG, "Cashback Amount = " + result.getCashbackAmount());
        Log.i(TAG, "Completion Amount = " + result.getCompletionAmount());
        Log.i(TAG, "Result Type = " + result.getResultType());
        Log.i(TAG, "Cashback Count = " + result.getCashbackCount());
        Log.i(TAG, "Gratuity Amount = " + result.getGratuityAmount());
        Log.i(TAG, "Gratuity Count = " + result.getGratuityCount());
        Log.i(TAG, "Refund Amount = " + result.getRefundAmount());
        Log.i(TAG, "Refund Count = " + result.getRefundCount());
        Log.i(TAG, "Sale Amount = " + result.getSaleAmount());
        Log.i(TAG, "Sale Count = " + result.getSaleCount());
        Log.i(TAG, "Pennies Amount = " + result.getPenniesAmount());
        Log.i(TAG, "Pennies Count = " + result.getPenniesCount());
        Log.i(TAG, "Is Report Response = " + result.isReportResponse());
        Log.i(TAG, "Receipt Send Error = " + result.getReportSendStatusError());
        Log.i(TAG, "Receipt = " + Arrays.toString(result.getReceipt()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (result.getReceipt().length > 0) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime local = LocalDateTime.parse(result.getReceipt()[9].trim(), formatter);
                    ZoneId zone = ZoneId.of("Europe/London");
                    ZonedDateTime zoned = local.atZone(zone);
                    Log.i(TAG, "Created At = " + zoned);
                } catch (Exception e) {
                    Log.i(TAG, "Created At = " + ZonedDateTime.now(ZoneId.of("Europe/London")));
                }
            } else {
                Log.i(TAG, "Created At = " + ZonedDateTime.now(ZoneId.of("Europe/London")));
            }
        }
    }

    private void populateResponseList(Context context, PositiveTransResult result) {
        responseList = new ArrayList<>();
        String sessionId = SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID);
        if (!TextUtils.isEmpty(sessionId)) {
            responseList.add(new TransactionResponse("RequestID", SharedHelper.getStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID)));
        }
        if (!TextUtils.isEmpty(AppConstants.tnxNote)) {
            responseList.add(new TransactionResponse("TransactionNote", AppConstants.tnxNote));
        }
        responseList.add(new TransactionResponse("PS App Version", String.format("v%s", BuildConfig.VERSION_NAME)));
        responseList.add(new TransactionResponse("Type", "" + result.getTransType()));
        responseList.add(new TransactionResponse("UTI", "" + result.getUTI()));
        responseList.add(new TransactionResponse("Amount", "" + result.getAmountTrans()));
        responseList.add(new TransactionResponse("Gratuity", "" + result.getAmountGratuity()));
        responseList.add(new TransactionResponse("Cashback", "" + result.getAmountCashback()));
        responseList.add(new TransactionResponse("Discount", "" + result.getAmountDiscount()));
        responseList.add(new TransactionResponse("Approved", "" + result.isTransApproved()));
        responseList.add(new TransactionResponse("Deferred", "" + result.isTransDeferred()));
        responseList.add(new TransactionResponse("Partially Approved", "" + result.isTransPartiallyApproved()));
        responseList.add(new TransactionResponse("Cancelled", "" + result.isTransCancelled()));
        responseList.add(new TransactionResponse("SigRequired", "" + result.isCvmSigRequired()));
        responseList.add(new TransactionResponse("PINVerified", "" + result.isCvmPinVerified()));
        responseList.add(new TransactionResponse("Currency", "" + result.getTransCurrencyCode()));
        responseList.add(new TransactionResponse("Version", "" + result.getSoftwareVersion()));
        responseList.add(new TransactionResponse("terminalId", "" + result.getTerminalId()));
        responseList.add(new TransactionResponse("MerchantId", "" + result.getMerchantId()));
        responseList.add(new TransactionResponse("TransDateTime", "" + result.getTransDateTime()));
        if (result.getErrorText() != null && !TextUtils.isEmpty(result.getErrorText().trim())) {
            responseList.add(new TransactionResponse("ErrorText", String
                    .format("%s, %s", result.getErrorCode(), result.getErrorText()
                    )));
        }

        responseList.add(new TransactionResponse("IsResultResponse", "true"));
        if (result.getCardholderReceipt() != null) {
            responseList.add(new TransactionResponse("CardHolderReceipt", "" + String.join("&&", result.getCardholderReceipt())));
        } else {
            responseList.add(new TransactionResponse("CardHolderReceipt", ""));
        }

        if (result.getMerchantReceipt() != null) {
            responseList.add(new TransactionResponse("MerchantReceipt", "" + String.join("&&", result.getMerchantReceipt())));
        } else {
            responseList.add(new TransactionResponse("MerchantReceipt", ""));
        }

        if (result.isTransDetails()) {
            responseList.add(new TransactionResponse("ReceiptNumber", "" + result.getReceiptNumber()));
            if (result.getRetrievalReferenceNumber() != null) {
                responseList.add(new TransactionResponse("RRN", "" + result.getRetrievalReferenceNumber()));
            }
            if (result.getResponseCode() != null) {
                responseList.add(new TransactionResponse("ResponseCode", "" + result.getResponseCode()));
            }

            if (result.getStan() != null) {
                responseList.add(new TransactionResponse("Stan", "" + result.getStan()));
            }
            if (result.getAuthorisationCode() != null) {
                responseList.add(new TransactionResponse("AuthCode", "" + result.getAuthorisationCode()));
            }
            if (result.getMerchantTokenId() != null) {
                responseList.add(new TransactionResponse("MerchantTokenId", "" + result.getMerchantTokenId()));
            }

            String cardType = result.getCardType();
            Log.i(TAG, "CardType = " + cardType);
            responseList.add(new TransactionResponse("CardType", "" + cardType));
            if (cardType.compareTo("EMV") == 0 || cardType.compareTo("CTLS") == 0) {
                responseList.add(new TransactionResponse("AID", "" + result.getEmvAid()));
                responseList.add(new TransactionResponse("TSI", "" + result.getEmvTsi()));
                responseList.add(new TransactionResponse("TVR", "" + result.getEmvTvr()));
                responseList.add(new TransactionResponse("CardHolder", "" + result.getEmvCardholderName()));
                responseList.add(new TransactionResponse("Cryptogram", "" + byteArrayToHexString(result.getEmvCryptogram())));
                responseList.add(new TransactionResponse("CryptogramType", "" + result.getEmvCryptogramType()));

            }

            if (result.getCardPan() != null) {
                responseList.add(new TransactionResponse("PAN", "" + result.getCardPan()));
            }
            if (result.getCardExpiryDate() != null) {
                responseList.add(new TransactionResponse("ExpiryDate", "" + result.getCardExpiryDate()));
            }
            if (result.getCardStartDate() != null) {
                responseList.add(new TransactionResponse("StartDate", "" + result.getCardStartDate()));
            }
            if (result.getCardScheme() != null && !result.getCardScheme().equals("")) {
                responseList.add(new TransactionResponse("Scheme", "" + result.getCardScheme()));
            }
            if (result.getCardPanSequenceNumber() != null) {
                responseList.add(new TransactionResponse("PSN", "" + result.getCardPanSequenceNumber()));
            }

        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            responseList.add(new TransactionResponse("Created At", "" + ZonedDateTime.now(ZoneId.of("Europe/London"))));
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime local = LocalDateTime.parse(result.getTransDateTime(), formatter);
                ZoneId zone = ZoneId.of("Europe/London");
                ZonedDateTime zoned = local.atZone(zone);
                responseList.add(new TransactionResponse("Created At", "" + zoned));
            } catch (Exception e) {
                responseList.add(new TransactionResponse("Created At", "" + ZonedDateTime.now(ZoneId.of("Europe/London"))));
            }
        }

        JSONObject responseJson = new JSONObject();
        try {
            for (TransactionResponse item : responseList) {
                responseJson.put(
                        item.getTransResponseName(),
                        item.getTransResponseValue() != null
                                ? item.getTransResponseValue()
                                : JSONObject.NULL
                );
            }
        } catch (JSONException e) {
            /// Telemetry: PositiveLaunchReceiver.populateResponseList — failed to build transaction JSON.
            logReceiveError(context, "populateResponseList", String.valueOf(e.getMessage()), e);
        }

        /// Telemetry: PositiveLaunchReceiver.populateResponseList — full transaction payload.
        TelemetryLogger.info(
                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                TAG + ".populateResponseList",
                String.valueOf(responseJson)
        );
    }

    public String byteArrayToHexString(final byte[] byteArray) {
        if (byteArray == null) {
            return "";
            //throw new IllegalArgumentException("Argument 'byteArray' cannot be null");
        }
        int readBytes = byteArray.length;
        StringBuilder hexData = new StringBuilder();
        int onebyte;
        for (int i = 0; i < readBytes; i++) {
            onebyte = (0x000000ff & byteArray[i]) | 0xffffff00;
            hexData.append(Integer.toHexString(onebyte).substring(6));
        }
        return hexData.toString().toUpperCase();
    }


    /**
     * Sends a receiver failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code PositiveLaunchReceiver.onReceive.displayTransactionDetails}.
     * {@code message} must already be a string.
     */
    private void logReceiveError(
            Context context,
            String functionPath,
            String message,
            Throwable throwable
    ) {
        String eventName = TAG + "." + functionPath;
        TelemetryLogger.error(
                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                eventName,
                message != null ? message : "",
                throwable
        );
    }

}