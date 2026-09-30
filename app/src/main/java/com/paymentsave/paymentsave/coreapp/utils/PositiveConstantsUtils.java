package com.paymentsave.paymentsave.coreapp.utils;

public class PositiveConstantsUtils {
    // Just a TAG used for debugging
    private static final String TAG = PositiveConstantsUtils.class.getSimpleName();
    public static String lastReceivedUTI; // used to make life easy when testing reversals of the last transaction
    public static String lastReceivedRRN; // used to make life easy when testing completions of the last transaction
    public static String lastReceivedAmount; // used to make life easy when testing completions of the last transaction
    public static final String INPUT_AMOUNT_FRAGMENT = "InputAmountFragment";
    public static final String CHOOSE_OPTION_FRAGMENT = "ChooseOptionFragment";
    public static final String RESPONSE_FRAGMENT = "ResponseFragment";
    // Static integer values
    public static final int SALE = 0;
    public static final int REFUND = 1;
    public static final int REVERSE_LAST = 2;
    public static final int REVERSE_BY_UTI = 3;
    public static final int PREAUTH = 4;
    public static final int COMPLETION = 5;
    public static final int QUERY_TRANSACTION = 6;
    public static final int CANCEL_TRANSACTION = 7;
    private final int PRINT_X = 8;
    private final int PRINT_Z = 9;
    private final int PRINT_HISTORY = 10;
    private final int EXIT = 11;
}

