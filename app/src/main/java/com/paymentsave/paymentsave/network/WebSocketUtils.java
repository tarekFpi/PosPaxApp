/*
 * Copyright (c) 2024 Tecognize Solutions Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package com.paymentsave.paymentsave.network;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_CASHBACK;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_GRATUITY;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_XREPORT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_ZREPORT;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_RECONCILIATION;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_REFUND;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_SALE;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.eft.libpositive.PosIntegrate;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;

import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import tech.gusavila92.websocketclient.WebSocketClient;


public class WebSocketUtils {

    private static final String TAG = "WebSocketUtils";
    public static WebSocketClient mWebSocketClient;
    public static boolean isSocketConnected = false;

    public static void closeConnection() {
        try {
            mWebSocketClient.close();
            Log.d(TAG, "PS-connect: Connection Closed");
        } catch (Exception e) {
            Log.e(TAG, "closeConnection Error: " + e.getMessage());
            // Telemetry: WebSocketUtils.closeConnection — failed to close the socket.
            logReceiveError(null, "closeConnection", String.valueOf(e.getMessage()), e);
        }
    }

    public static void reconnect(final Context context) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSocketConnected) {
                    // Reinitialize mWebSocketClient with a new instance
                    connectWebSocket(context);
                }
            }
        }, 500);
    }


    public static CompletableFuture<Boolean> connectWebSocket(Context context) {
        CompletableFuture<Boolean> connectionResult = null;
        String device_token = SharedHelper.getStringData(context, AppConstants.SharedPref.TOKEN);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            connectionResult = new CompletableFuture<>();
            URI uri;
            String url = "wss://" + Constants.SOCKET_URL + "ws/ps-connect/device/?tid=" + SharedHelper.getStringData(context, AppConstants.SharedPref.TID) + "&device_token=" + device_token;
            try {
                uri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                // Telemetry: WebSocketUtils.connectWebSocket — websocket URL is not a valid URI.
                logReceiveError(context, "connectWebSocket", String.valueOf(e.getMessage()), e);
                connectionResult.completeExceptionally(e);
                return connectionResult;
            }

            CompletableFuture<Boolean> finalConnectionResult = connectionResult;
            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen() {
                    Log.d(TAG, "PS-connect: Connection Opened");
                    isSocketConnected = true;
                    finalConnectionResult.complete(true); // Connection established successfully
                }

                @Override
                public void onException(Exception e) {
                    if (Objects.equals(e.getMessage(), "cloud.psapp.uk"))
                        Log.d(TAG, "onException: Unable to connect to the server.");
                    else
                        Log.d(TAG, "onException: " + e.getMessage());

                    isSocketConnected = false;
                    // Handle connection error
                    finalConnectionResult.completeExceptionally(e);
                }


                @Override
                public void onCloseReceived() {
                    Log.d(TAG, "PS-connect: Connection Closed");
                    isSocketConnected = false;
                    finalConnectionResult.complete(false); // Connection closed unexpectedly
                }

                @Override
                public void onTextReceived(String message) {
                    // Handle messages here...
                    JsonParser parser = new JsonParser();
                    JsonObject json = (JsonObject) parser.parse(message);
                    try {
                        String event = json.get("event").getAsString();
                        Log.d(TAG, "connectEvent: " + event);
                        String sessionId;
                        HashMap<PosIntegrate.CONFIG_TYPE, String> args;
                        switch (event) {
                            case "CONNECT_RUN_SALE":
                                sessionId = json.get("session_id").getAsString();
                                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, sessionId);
                                JsonObject data = json.get("data").getAsJsonObject();

                                /// Telemetry: WebSocketUtils.onTextReceived.CONNECT_RUN_SALE — incoming sale payload.
                                TelemetryLogger.info(
                                        SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                        TAG + ".onTextReceived.CONNECT_RUN_SALE",
                                        String.valueOf(data)
                                );

                                int amount = data.get("amount").getAsInt();
                                int gratuity = 0;
                                int cashback = 0;
                                if (data.has("gratuity")) {
                                    gratuity = data.get("gratuity").getAsInt();
                                }
                                if (data.has("cashback")) {
                                    cashback = data.get("cashback").getAsInt();
                                }

                                args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                                args.put(CT_AMOUNT, String.valueOf(amount));
                                args.put(CT_AMOUNT_GRATUITY, String.valueOf(gratuity));
                                args.put(CT_AMOUNT_CASHBACK, String.valueOf(cashback));
                                args.put(CT_DISABLEPRINTING, "true");
                                args.put(CT_LANGUAGE, "en_GB");
                                try {
                                    PosIntegrate.executeTransaction(context, TRANSACTION_TYPE_SALE, args);
                                } catch (Exception e) {
                                    Log.d(TAG, "Run Sale Exception: " + e);
                                    // Telemetry: WebSocketUtils.onTextReceived.CONNECT_RUN_SALE — sale failed to start.
                                    logReceiveError(context, "onTextReceived.CONNECT_RUN_SALE", String.valueOf(e.getMessage()), e);
                                }
                                break;
                            case "CONNECT_RUN_REFUND":
                                sessionId = json.get("session_id").getAsString();
                                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, sessionId);
                                JsonObject refundData = json.get("data").getAsJsonObject();

                                // Telemetry: WebSocketUtils.onTextReceived.CONNECT_RUN_REFUND — incoming refund payload.
                                TelemetryLogger.info(
                                        SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                        TAG + ".onTextReceived.CONNECT_RUN_REFUND",
                                        String.valueOf(refundData)
                                );

                                HomeActivity.refundUti = refundData.get("refund_uti").getAsString();
                                int refundAmount = refundData.get("amount").getAsInt();

                                args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                                args.put(CT_DISABLEPRINTING, "true");
                                args.put(CT_AMOUNT, String.valueOf(refundAmount));
                                args.put(CT_LANGUAGE, "en_GB");
                                try {
                                    PosIntegrate.executeTransaction(context, TRANSACTION_TYPE_REFUND, args);
                                } catch (Exception e) {
                                    Log.d(TAG, "Run Sale Exception: " + e);
                                    // Telemetry: WebSocketUtils.onTextReceived.CONNECT_RUN_REFUND — refund failed to start.
                                    logReceiveError(context, "onTextReceived.CONNECT_RUN_REFUND", String.valueOf(e.getMessage()), e);
                                }
                                break;
                            case "CONNECT_RUN_ZREPORT":
                            case "CONNECT_RUN_XREPORT":
                                sessionId = json.get("session_id").getAsString();
                                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, sessionId);
                                JsonObject reportData = json.get("data").getAsJsonObject();

                                // Telemetry: WebSocketUtils.onTextReceived.CONNECT_RUN_XREPORT|ZREPORT — incoming report payload.
                                TelemetryLogger.info(
                                        SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                        TAG + ".onTextReceived." + event,
                                        String.valueOf(reportData)
                                );

                                String deviceTid = reportData.get("tid").getAsString();
                                boolean printReceipt = reportData.get("print_receipt").getAsBoolean();
                                if (Objects.equals(deviceTid, SharedHelper.getStringData(context, AppConstants.SharedPref.TID))) {
                                    try {
                                        if (event.equals("CONNECT_RUN_ZREPORT")) {
                                            args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                                            args.put(CT_DISABLEPRINTING, Boolean.toString(!printReceipt));
                                            args.put(CT_ZREPORT, "true");
                                            PosIntegrate.executeReport(context, TRANSACTION_TYPE_RECONCILIATION, args);
                                        }
                                        if (event.equals("CONNECT_RUN_XREPORT")) {
                                            args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                                            args.put(CT_DISABLEPRINTING, Boolean.toString(!printReceipt));
                                            args.put(CT_XREPORT, "true");
                                            PosIntegrate.executeReport(context, TRANSACTION_TYPE_RECONCILIATION, args);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Run Report Exception: " + e);
                                        // Telemetry: WebSocketUtils.onTextReceived.CONNECT_RUN_REPORT — X/Z report failed to start.
                                        logReceiveError(context, "onTextReceived." + event, String.valueOf(e.getMessage()), e);
                                    }
                                }

                                break;
                            case "CONNECT_CANCEL_TRANSACTION":
                                sessionId = json.get("session_id").getAsString();
                                SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, sessionId);

                                // Telemetry: WebSocketUtils.onTextReceived.CONNECT_CANCEL_TRANSACTION — incoming cancel command.
                                TelemetryLogger.info(
                                        SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                        TAG + ".onTextReceived.CONNECT_CANCEL_TRANSACTION",
                                        String.valueOf(json)
                                );

                                try {
                                    PosIntegrate.cancelTransaction(context);
                                    if (!TextUtils.isEmpty(sessionId) && WebSocketUtils.isSocketConnected) {
                                        JSONObject payload = new JSONObject();
                                        payload.put("cancelling", true);
                                        payload.put("tid", SharedHelper.getStringData(context, AppConstants.SharedPref.TID));

                                        String eventString = "{\"event\":\"CANCEL_COMD_RECEIVED\",\"session_id\":\"" + sessionId + "\",\"message\":" + payload + "}";
                                        WebSocketUtils.mWebSocketClient.send(eventString);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // Telemetry: WebSocketUtils.onTextReceived.CONNECT_CANCEL_TRANSACTION — cancel failed to start.
                                    logReceiveError(context, "onTextReceived.CONNECT_CANCEL_TRANSACTION", String.valueOf(e.getMessage()), e);
                                }
                                break;
                            case "CONNECT_REPRINT_RECEIPT":
                                sessionId = json.get("session_id").getAsString();
                                JsonObject receiptData = json.get("data").getAsJsonObject();
                                // Log.e(TAG, "onTextReceived: "+receiptData );

                                /// Telemetry: WebSocketUtils.onTextReceived.CONNECT_REPRINT_RECEIPT — incoming reprint payload.
                                TelemetryLogger.info(
                                        SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                                        TAG + ".onTextReceived.CONNECT_REPRINT_RECEIPT",
                                        String.valueOf(receiptData)
                                );

                                String tid = receiptData.get("tid").getAsString();
                                if (receiptData.has("transaction")) {
                                    try {
                                        JsonElement transactionElement = receiptData.get("transaction");

                                        if (transactionElement.isJsonPrimitive()) {
                                            // The transaction is a stringified JSON array
                                            String transactionString = transactionElement.getAsString();

                                            // Parse the stringified JSON array
                                            JsonArray transactionArray = new JsonParser().parse(transactionString).getAsJsonArray();

                                            if (transactionArray.size() > 0) {
                                                JsonObject transactionObject = transactionArray.get(0).getAsJsonObject();

                                                if (transactionObject.has("cardHolderReceipt")) {
                                                    JsonArray cardHolderReceiptArray = transactionObject.getAsJsonArray("cardHolderReceipt");

                                                    // Convert the JsonArray to a List<String>
                                                    List<String> cardHolderReceiptList = new ArrayList<>();
                                                    for (JsonElement element : cardHolderReceiptArray) {
                                                        cardHolderReceiptList.add(element.getAsString());
                                                    }
                                                    // Print the receipt lines or use them as needed
                                                    PrintUtils.rePrintCardHolderReceipt(context, cardHolderReceiptList);

                                                    String responseText = "{\"event\":\"REPRINT_COMD_RECEIVED\",\"session_id\":\"" + sessionId + "\",\"message\":{\"tid\":\"" + tid + "\",\"printing\":true}}";
                                                    mWebSocketClient.send(responseText);
                                                } else {
                                                    Log.e(TAG, "onTextReceived: " + "No 'cardHolderReceipt' key found in the transaction object.");
                                                }
                                            } else {
                                                Log.e(TAG, "onTextReceived: " + "The 'transaction' array is empty.");
                                            }
                                        } else {
                                            Log.e(TAG, "onTextReceived: " + "The 'transaction' key is not a string.");
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "onTextReceived: " + e.getMessage());
                                        // Telemetry: WebSocketUtils.onTextReceived.CONNECT_REPRINT_RECEIPT — reprint payload could not be printed.
                                        logReceiveError(context, "onTextReceived.CONNECT_REPRINT_RECEIPT", String.valueOf(e.getMessage()), e);
                                    }
                                } else {
                                    Log.e(TAG, "onTextReceived: " + "No 'transaction' key found in receiptData.");
                                }
                                break;
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Event Parse Error: " + e.getMessage());
                        e.printStackTrace();
                        // Telemetry: WebSocketUtils.onTextReceived — incoming socket message could not be parsed.
                        logReceiveError(context, "onTextReceived", String.valueOf(e.getMessage()), e);
                    }
                }

                @Override
                public void onBinaryReceived(byte[] data) {
                }

                @Override
                public void onPingReceived(byte[] data) {
                }

                @Override
                public void onPongReceived(byte[] data) {
                }
            };

            mWebSocketClient.setConnectTimeout(10000);
            mWebSocketClient.setReadTimeout(60000);
            mWebSocketClient.enableAutomaticReconnection(500);
            mWebSocketClient.connect();
        }

        return connectionResult;
    }

    /**
     * Sends a websocket failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code WebSocketUtils.onTextReceived.CONNECT_RUN_SALE}.
     * {@code message} must already be a string.
     */
    private static void logReceiveError(Context context, String functionPath, String message, Throwable throwable) {
        String tid = "";
        if (context != null) {
            tid = SharedHelper.getStringData(context, AppConstants.SharedPref.TID);
        }

        String eventName = TAG + "." + functionPath;
        TelemetryLogger.error(
                tid,
                eventName,
                message != null ? message : "",
                throwable
        );
    }


//    public static void connectWebSocket(Context context) {
//        URI uri;
//        String ulr = "wss://" + Constants.SOCKET_URL + "ws/device/?tid=" + SharedHelper.getStringData(context, AppConstants.SharedPref.TID);
//        try {
//            uri = new URI(ulr);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        mWebSocketClient = new WebSocketClient(uri) {
//            @Override
//            public void onOpen() {
//                Log.d(TAG, "PS-connect: Connection Opened");
//                isSocketConnected = true;
////                SharedHelper.putConnectionStatus(context, 1);
//            }
//
//            @Override
//            public void onTextReceived(String message) {
//                JsonParser parser = new JsonParser();
//                JsonObject json = (JsonObject) parser.parse(message);
//                try {
//                    String event = json.get("event").getAsString();
//                    Log.d(TAG, "connectEvent: " + event);
//                    switch (event) {
//                        case "CONNECT_RUN_SALE":
//                            String sessionId = json.get("session_id").getAsString();
//                            SharedHelper.putStringData(context, AppConstants.SharedPref.TRANSACTION_SESSION_ID, sessionId);
//                            JsonObject data = json.get("data").getAsJsonObject();
//                            int amount = data.get("amount").getAsInt();
//
//                            HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
//                            args.put(CT_AMOUNT, String.valueOf(amount));
//                            args.put(CT_DISABLEPRINTING, "true");
//                            args.put(CT_LANGUAGE, "en_GB");
//                            try {
//                                PosIntegrate.executeTransaction(context, TRANSACTION_TYPE_SALE, args);
//                            } catch (Exception e) {
//                                Log.d(TAG, "Run Sale Exception: " + e);
//                            }
//                            break;
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG, "Event Parse Error: " + e.getMessage());
//                    e.printStackTrace();
//                }
////                Gson gson = new Gson();
////                try {
////                    ConversionMessageResult object = gson.fromJson(json.get("data").toString(), ConversionMessageResult.class);
////                    if (object.getSender().getId() != SharedHelper.getUserID(context, AppConstants.USER_ID)) {
////
////                        if (SharedHelper.getBooleanData(context, AppConstants.SWITCH_NOTIFICATION)) {
////                            AppConstants.showNotification(context, object);
////                        }
////
////                        String convId = String.valueOf(object.getConversation());
////                        String msgId = String.valueOf(object.getId());
////                        String jsonString = "{\n" +
////                                "  \"event\":\"message_received\",\n" +
////                                "  \"data\":{\n" +
////                                "  \"conversation\":" + convId + ",\n" +
////                                "  \"message\": " + Integer.parseInt(msgId) + "\n" +
////                                "  }\n" +
////                                "}\n";
////                        mWebSocketClient.send(jsonString);
////                    }
////                }
////                catch (Exception ex) {
////                    ex.printStackTrace();
////                    try {
////                        // For Conv Create Event
////                        String data = json.get("data").toString();
////                        JsonObject jsonData = (JsonObject) parser.parse(data);
////                        int convo_id = Integer.parseInt(jsonData.get("id").toString());
////                        int convo_type = Integer.parseInt(jsonData.get("convo_type").toString());
////                        String receiver_json = jsonData.get("receiver").toString();
////                        Result result;
////                        if (convo_type == 0){
////                            Receiver receiver_object = gson.fromJson(receiver_json, Receiver.class);
////                            result = new Result(convo_id,null,convo_type,true,null,null,0,receiver_object);
////
////                        } else {
////                            String group_name = jsonData.get("group_name").toString();
////                            String group_image = jsonData.get("group_image").toString();
////                            result = new Result(convo_id,group_name,convo_type,true,group_image,null,0,null);
////                        }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }
////
////                Intent intent = new Intent(AppConstants.SOCKET_CONNECTION);
////                intent.putExtra("message", message);
////                context.sendBroadcast(intent);
//
//            }
//
//            @Override
//            public void onBinaryReceived(byte[] data) {
//            }
//
//            @Override
//            public void onPingReceived(byte[] data) {
//            }
//
//            @Override
//            public void onPongReceived(byte[] data) {
//            }
//
//            @Override
//            public void onException(Exception e) {
//                Log.d(TAG, "onException: " + e.getMessage());
////                Toast.makeText(context,""+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCloseReceived() {
//                Log.d(TAG, "onCloseReceived: Connection Closed");
//                isSocketConnected = false;
////                SharedHelper.putConnectionStatus(context, 0);
//            }
//        };
//        // mWebSocketClient.addHeader("Authorization",token);
//        mWebSocketClient.setConnectTimeout(10000);
//        mWebSocketClient.setReadTimeout(60000);
//        mWebSocketClient.enableAutomaticReconnection(500);
//        mWebSocketClient.connect();
//    }

}
