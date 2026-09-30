package com.paymentsave.paymentsave;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppConstants {
    public static ExecutorService executorService = Executors.newFixedThreadPool(4);
    // Alert Constants
    public static String alertText = "";
    // Split bill constants
    public static String tnxNote = "";
    public static boolean isSplitBillRunning = false;
    public static String splitBillTotalAmount = "0.00";
    public static String dueAmount = "0.00";
    public static String previousPay = "0.00";
    public static int totalPeople = 1;
    public static int initiatedPaymentNumber = 1;
    public static boolean isEqualSplit = false;

    // Share preference references
    public interface SharedPref {
        String DEVICE_TOKEN = "DEVICE_TOKEN";
        String DEVICE_STATUS = "DEVICE_STATUS";
        String INTEGRATION_MODE = "INTEGRATION_MODE";
        String PRE_AUTH = "PRE_AUTH";
        String RECEIPT_PRINTING = "RECEIPT_PRINTING";
        String CUSTOMER_RECEIPT_PRINTING = "CUSTOMER_RECEIPT_PRINTING";
        String MERCHANT_RECEIPT_PRINTING = "MERCHANT_RECEIPT_PRINTING";
        String SPLIT_BILL = "SPLIT_BILL";
        String CASHBACK = "CASHBACK";
        String GRATUITY = "GRATUITY";
        String TOKEN = "TOKEN";
        String USER_ID = "USER_ID";
        String ERECEIPT = "ERECEIPT"; //eReceipt
        String LEGAL_NAME = "LEGAL_NAME";
        String MID = "MID";
        String TID = "TID";
        String SPLIT_BILL_ID = "SPLIT_BILL_ID";
        String GRATUITY_TYPE = "GRATUITY_TYPE";
        String LINK_PAYMENT_ENABLED = "LINK_PAYMENT_ENABLED";
        String GRATUITY_OPTIONS = "GRATUITY_OPTIONS";
        String CASHBACK_OPTIONS = "CASHBACK_OPTIONS";
        String TRANSACTION_SESSION_ID = "TRANSACTION_SESSION_ID";
        String TRANSACTION_DEVICE_UID = "TRANSACTION_DEVICE_UID";
        String TRADING_NAME = "TRADING_NAME";
        String TRADING_ADDRESS = "TRADING_ADDRESS";
        String BUSINESS_PHONE = "BUSINESS_PHONE";
        String BUSINESS_EMAIL = "BUSINESS_EMAIL";
        String BUSINESS_LOGO = "BUSINESS_LOGO";
        String BUSINESS_ID = "BUSINESS_ID";
        String NEXT_BATCH_TIME = "NEXT_BATCH_TIME";
        String CONTACT_SUPPORT_EMAIL = "CONTACT_SUPPORT_EMAIL";
        String CONTACT_SUPPORT_PHONE = "CONTACT_SUPPORT_PHONE";

    }
}
