package com.paymentsave.paymentsave.coreapp.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.TransactionResponse;

import java.util.ArrayList;

public class ObjectUtils {
    private static final String TAG = "ObjectUtils";

    public static TransactionRequest generateTransactionRequest(Context context, ArrayList<TransactionResponse> responseList) {
        TransactionRequest transactionRequest = new TransactionRequest();
        for (TransactionResponse response : responseList) {
            switch (response.getTransResponseName()) {
                case "RequestID":
                    transactionRequest.setEposSessionId(response.getTransResponseValue());
                    break;
                case "Type":
                    transactionRequest.setTransactionType(response.getTransResponseValue());
                    break;
                case "TransactionNote":
                    transactionRequest.setTnxNote(response.getTransResponseValue());
                    break;
                case "ErrorText":
                    transactionRequest.setErrorText(response.getTransResponseValue());
                    break;
                case "IsResultResponse":
                    transactionRequest.setResultResponse(Boolean.parseBoolean(response.getTransResponseValue()));
                    break;
                case "Gratuity":
                    try {
                        double amt = Double.parseDouble(response.getTransResponseValue());
                        String finalAmt = String.valueOf(amt / 100);
                        transactionRequest.setGratuityAmount(finalAmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "Cashback":
                    try {
                        double amt = Double.parseDouble(response.getTransResponseValue());
                        String finalAmt = String.valueOf(amt / 100);
                        transactionRequest.setCashbackAmount(finalAmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "UTI":
                    transactionRequest.setUti(response.getTransResponseValue());
                    break;
                case "ExistingUti":
                    transactionRequest.setExistingUti(response.getTransResponseValue());
                    break;
                case "Amount":
                    double amt = Double.parseDouble(response.getTransResponseValue());
                    String finalAmt = String.valueOf(amt / 100);
                    transactionRequest.setAmount(finalAmt);
                    break;
                case "AuthCode":
                    transactionRequest.setAuthCode(response.getTransResponseValue());
                    break;
                case "RRN":
                    transactionRequest.setRrn(response.getTransResponseValue());
                    break;
                case "Discount":
                    transactionRequest.setDiscount(response.getTransResponseValue());
                    break;
                case "Approved":
                    String liveApprovedData = response.getTransResponseValue();
                    boolean boolvar = false;
                    if (liveApprovedData.equals("true")) {
                        boolvar = true;
                    }
                    transactionRequest.setApproved(boolvar);
                    break;
                case "Cancelled":
                    String liveCancelData = response.getTransResponseValue();
                    boolean cancelBoolVar = false;
                    if (liveCancelData.trim().equals("true")) {
                        cancelBoolVar = true;
                    }
                    transactionRequest.setCancelled(cancelBoolVar);
                    break;
                case "SigRequired":
                    String liveSigRequiredData = response.getTransResponseValue();
                    boolean boolvar3 = false;
                    if (liveSigRequiredData.equals("true")) {
                        boolvar3 = true;
                    }
                    transactionRequest.setSigRequired(boolvar3);
                    break;
                case "PINVerified":
                    String livePINVerifiedData = response.getTransResponseValue();
                    boolean boolvar4 = false;
                    if (livePINVerifiedData.equals("true")) {
                        boolvar4 = true;
                    }
                    transactionRequest.setPinVerified(boolvar4);
                    break;
                case "Currency":
                    transactionRequest.setCurrency(response.getTransResponseValue());
                    break;
                case "Version":
                    break;
                case "terminalId":
                    transactionRequest.setTerminalId(response.getTransResponseValue());
                    break;
                case "MerchantId":
                    transactionRequest.setMerchantUid(response.getTransResponseValue());
                    break;
                case "ReceiptNumber":
                    transactionRequest.setReceiptId(response.getTransResponseValue());
                    break;
                case "Stan":
                    transactionRequest.setStan(response.getTransResponseValue());
                    break;
                case "Scheme":
                    transactionRequest.setCardType(response.getTransResponseValue());
                    break;
                case "PAN":
                    transactionRequest.setPan(response.getTransResponseValue());
                    break;
                case "PSN":
                    transactionRequest.setPsnCode(response.getTransResponseValue());
                    break;
                case "ResponseCode":
                    transactionRequest.setResponseCode(response.getTransResponseValue());
                    break;
                case "CardHolderReceipt":
                    transactionRequest.setCardHolderReceipt(response.getTransResponseValue());
                    break;
                case "MerchantReceipt":
                    transactionRequest.setMerchantReceipt(response.getTransResponseValue());
                    break;
            }
        }


        if (transactionRequest.isResultResponse()) {
            if (transactionRequest.isCancelled()) {
                transactionRequest.setStatus(2);
            } else {
                if (transactionRequest.isApproved()) {
                    transactionRequest.setStatus(1);
                } else if (!transactionRequest.isApproved() && !transactionRequest.isCancelled()) {
                    transactionRequest.setStatus(0);
                } else {
                    transactionRequest.setStatus(5);
                }
            }
        } else {
            transactionRequest.setStatus(5);
        }

        transactionRequest.setMerchant(SharedHelper.getIntData(context, AppConstants.SharedPref.USER_ID));
        transactionRequest.setBusiness(SharedHelper.getIntData(context, AppConstants.SharedPref.BUSINESS_ID));

        Gson gson = new Gson();
        String responseText = gson.toJson(responseList);
        transactionRequest.setResponseText(responseText);
        return transactionRequest;
    }

}
