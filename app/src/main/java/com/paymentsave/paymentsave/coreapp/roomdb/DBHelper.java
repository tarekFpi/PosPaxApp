package com.paymentsave.paymentsave.coreapp.roomdb;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.roomdb.database.PaxDB;
import com.paymentsave.paymentsave.coreapp.roomdb.entity.Transaction;
import com.paymentsave.paymentsave.responses.TransactionResponse;
import com.paymentsave.paymentsave.coreapp.roomdb.entity.Report;

import java.util.ArrayList;
import java.util.Objects;

public class DBHelper {
    private static final String TAG = "DBHelper";
    private static volatile PaxDB localDb;

    // Private constructor to prevent instantiation
    private DBHelper() {
    }

    // Method to get the database instance
    public static PaxDB getDatabase(Context context) {
        if (localDb == null) {
            synchronized (DBHelper.class) {
                if (localDb == null) {
                    localDb = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    PaxDB.class,
                                    "pax-paymentsave-db" // Name of the database
                            ).fallbackToDestructiveMigration() // Handles schema changes; replace for production migrations
                            .build();
                }
            }
        }
        return localDb;
    }

    public static void storeTransactionDataLocal(Context context, ArrayList<TransactionResponse> responseList) {
        Transaction transaction = mapResponseListToEntity(responseList);
        AppConstants.executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (localDb != null) {
                        Transaction tnx = localDb.transactionDao().getTnxByUti(transaction.getUti());
                        if (tnx == null) {
                            Log.d(TAG, "run: Insert");
                            localDb.transactionDao().insert(transaction);
                        } else {
                            Log.d(TAG, "run: Update");
                            int ex_id = tnx.getId();
                            transaction.setId(ex_id);
                            localDb.transactionDao().updateTransaction(transaction);
                        }
                    } else {
                        Log.d(TAG, "run: Getting inside else");
                        PaxDB localDb = getDatabase(context.getApplicationContext());
                        localDb.transactionDao().insert(transaction);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "storeTransactionDataLocal: " + e);
                }
            }
        });
    }

    public static void storeReportDataLocal(Context context, ArrayList<TransactionResponse> responseList) {
        Report reportEntity = mapResponseListToReportEntity(responseList);
        AppConstants.executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    PaxDB paxDB = getDatabase(context);
                    paxDB.reportDao().insert(reportEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Transaction mapResponseListToEntity(ArrayList<TransactionResponse> responseList) {
        Transaction entity = new Transaction();
        for (TransactionResponse response : responseList) {
            switch (response.getTransResponseName()) {
                case "RequestID":
                    entity.setRequestID(response.getTransResponseValue());
                    break;
                case "TransactionNote":
                    entity.setTnxNote(response.getTransResponseValue());
                    break;
                case "ErrorText":
                    entity.setErrorText(response.getTransResponseValue());
                    break;
                case "Type":
                    entity.setTransType(response.getTransResponseValue());
                    break;
                case "UTI":
                    entity.setUti(response.getTransResponseValue());
                    break;
                case "Amount":
                    entity.setAmountTrans(response.getTransResponseValue());
                    break;
                case "Gratuity":
                    entity.setAmountGratuity(response.getTransResponseValue());
                    break;
                case "Cashback":
                    entity.setAmountCashback(response.getTransResponseValue());
                    break;
                case "Discount":
                    entity.setAmountDiscount(response.getTransResponseValue());
                    break;
                case "Approved":
                    entity.setTransApproved(Boolean.parseBoolean(response.getTransResponseValue()));
                    break;
                case "Cancelled":
                    entity.setTransCancelled(Boolean.parseBoolean(response.getTransResponseValue()));
                    break;
                case "SigRequired":
                    entity.setCvmSigRequired(Boolean.parseBoolean(response.getTransResponseValue()));
                    break;
                case "PINVerified":
                    entity.setCvmPinVerified(Boolean.parseBoolean(response.getTransResponseValue()));
                    break;
                case "Currency":
                    entity.setTransCurrencyCode(response.getTransResponseValue());
                    break;
                case "Version":
                    entity.setSoftwareVersion(response.getTransResponseValue());
                    break;
                case "IsResultResponse":
                    entity.setResultResponse(Objects.equals(response.getTransResponseValue(), "true"));
                    break;
                case "terminalId":
                    entity.setTerminalId(response.getTransResponseValue());
                    break;
                case "MerchantId":
                    entity.setMerchantId(response.getTransResponseValue());
                    break;
                case "ReceiptNumber":
                    entity.setReceiptNumber(response.getTransResponseValue());
                    break;
                case "RRN":
                    entity.setRetrievalReferenceNumber(response.getTransResponseValue());
                    break;
                case "ResponseCode":
                    entity.setResponseCode(response.getTransResponseValue());
                    break;
                case "Stan":
                    entity.setStan(response.getTransResponseValue());
                    break;
                case "AuthCode":
                    entity.setAuthorisationCode(response.getTransResponseValue());
                    break;
                case "MerchantTokenId":
                    entity.setMerchantTokenId(response.getTransResponseValue());
                    break;
                case "CardType":
                    entity.setCardType(response.getTransResponseValue());
                    break;
                case "AID":
                    entity.setEmvAid(response.getTransResponseValue());
                    break;
                case "TSI":
                    entity.setEmvTsi(response.getTransResponseValue());
                    break;
                case "TVR":
                    entity.setEmvTvr(response.getTransResponseValue());
                    break;
                case "CardHolder":
                    entity.setEmvCardholderName(response.getTransResponseValue());
                    break;
                case "Cryptogram":
                    entity.setEmvCryptogram(response.getTransResponseValue());
                    break;
                case "CryptogramType":
                    entity.setEmvCryptogramType(response.getTransResponseValue());
                    break;
                case "PAN":
                    entity.setCardPan(response.getTransResponseValue());
                    break;
                case "ExpiryDate":
                    entity.setCardExpiryDate(response.getTransResponseValue());
                    break;
                case "StartDate":
                    entity.setCardStartDate(response.getTransResponseValue());
                    break;
                case "Scheme":
                    entity.setCardScheme(response.getTransResponseValue());
                    break;
                case "PSN":
                    entity.setCardPanSequenceNumber(response.getTransResponseValue());
                    break;
                case "CardHolderReceipt":
                    if (response.getTransResponseValue() != null && !response.getTransResponseValue().isEmpty()) {
                        entity.setCardHolderReceipt(response.getTransResponseValue().split("&&"));
                    } else {
                        entity.setCardHolderReceipt(new String[]{});
                    }
                    break;
                case "MerchantReceipt":
                    if (response.getTransResponseValue() != null && !response.getTransResponseValue().isEmpty()) {
                        entity.setMerchantReceipt(response.getTransResponseValue().split("&&"));
                    } else {
                        entity.setMerchantReceipt(new String[]{});
                    }
                    break;
                case "Created At":
                    entity.setCreatedAt(response.getTransResponseValue());
                    break;
                default:
                    // Handle unexpected keys or log them
                    System.out.println("Unexpected key: " + response.getTransResponseName());
                    break;
            }
        }
        if (HomeActivity.refundUti != null) {
            entity.setExistingUti(HomeActivity.refundUti);
        }
        return entity;
    }

    public static Report mapResponseListToReportEntity(ArrayList<TransactionResponse> responseList) {
        Report entity = new Report();

        for (TransactionResponse response : responseList) {
            String key = response.getTransResponseName();
            String value = response.getTransResponseValue();

            switch (key) {
                case "RequestID":
                    entity.setRequestID(response.getTransResponseValue());
                    break;

                case "Report Type":
                    entity.setReportType(value);
                    break;

                case "Result Type":
                    entity.setResultType(value);
                    break;

                case "Completion Count":
                    entity.setCompletionCount(Integer.parseInt(value));
                    break;

                case "Completion Amount":
                    entity.setCompletionAmount(Double.parseDouble(value));
                    break;

                case "Cashback Count":
                    entity.setCashbackCount(Integer.parseInt(value));
                    break;

                case "Cashback Amount":
                    entity.setCashbackAmount(Double.parseDouble(value));
                    break;

                case "Gratuity Count":
                    entity.setGratuityCount(Integer.parseInt(value));
                    break;

                case "Gratuity Amount":
                    entity.setGratuityAmount(Double.parseDouble(value));
                    break;

                case "Refund Count":
                    entity.setRefundCount(Integer.parseInt(value));
                    break;

                case "Refund Amount":
                    entity.setRefundAmount(Double.parseDouble(value));
                    break;

                case "Sale Count":
                    entity.setSaleCount(Integer.parseInt(value));
                    break;

                case "Sale Amount":
                    entity.setSaleAmount(Double.parseDouble(value));
                    break;

                case "Is Report Response":
                    entity.setReportResponse(Boolean.parseBoolean(value));
                    break;

                case "Report Send Status Error":
                    entity.setReportError(value);
                    break;

                case "Receipt":
                    if (!value.isEmpty()) {
                        entity.setReceipt(value.split("&&"));
                    }
                    break;

                case "Created At":
                    entity.setCreatedAt(value);
                    break;

                default:
                    // Handle unexpected keys or log them
                    System.out.println("Unexpected key: " + key);
                    break;
            }
        }

        Gson gson = new Gson();
        String responseText = gson.toJson(responseList);
        entity.setResponseText(responseText);

        return entity;
    }

}
