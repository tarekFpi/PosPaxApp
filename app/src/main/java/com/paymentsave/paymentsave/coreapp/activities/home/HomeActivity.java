package com.paymentsave.paymentsave.coreapp.activities.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.ReceiptPrintActivity;
import com.paymentsave.paymentsave.coreapp.roomdb.DBHelper;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.DeviceConfigResponse;
import com.paymentsave.paymentsave.responses.TransactionResponse;
import com.paymentsave.paymentsave.coreapp.activities.test.system.SysTester;
import com.paymentsave.paymentsave.coreapp.roomdb.database.PaxDB;
import com.paymentsave.paymentsave.coreapp.utils.FloatView;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    public static String report_error_response;
    public static ArrayList<TransactionResponse> report_response;
    public static String refundUti = null;
    public static HomeViewModel viewModel;
    public static PaxDB localDb;
    public FloatView floatView;
    private TransactionRequest transactionRequest;

    public static Report generateReportObject(Context context, ArrayList<TransactionResponse> responseList) {
        Report report = new Report();
        for (TransactionResponse response : responseList) {
            switch (response.getTransResponseName()) {
                case "Is Report Response":
                    String reportValue = response.getTransResponseValue();
                    boolean reportBoolVar = false;
                    if (reportValue.trim().equals("true")) {
                        reportBoolVar = true;
                    }
                    report.setIsReportResponse(reportBoolVar);
                    break;
                case "Report Type":
                    report.setReportType(response.getTransResponseValue());
                    break;
                case "Result Type":
                    report.setResultType(response.getTransResponseValue());
                    break;
                case "Created At":
                    report.setCreatedAt(response.getTransResponseValue());
                    break;
                case "Completion Count":
                    report.setCompletionCount(Integer.parseInt(response.getTransResponseValue()));
                    break;
                case "Cashback Count":
                    report.setCashbackCount(Integer.parseInt(response.getTransResponseValue()));
                    break;
                case "Gratuity Count":
                    report.setGratuityCount(Integer.parseInt(response.getTransResponseValue()));
                    break;
                case "Refund Count":
                    report.setRefundCount(Integer.parseInt(response.getTransResponseValue()));
                    break;
                case "Sale Count":
                    report.setSaleCount(Integer.parseInt(response.getTransResponseValue()));
                    break;
                case "Completion Amount":
                    report.setCompletionAmount(response.getTransResponseValue());
                    break;
                case "Cashback Amount":
                    report.setCashbackAmount(response.getTransResponseValue());
                    break;
                case "Gratuity Amount":
                    report.setGratuityAmount(response.getTransResponseValue());
                    break;
                case "Refund Amount":
                    report.setRefundAmount(response.getTransResponseValue());
                    break;
                case "Sale Amount":
                    report.setSaleAmount(response.getTransResponseValue());
                    break;
                case "Report Send Status Error":
                    report.setReportError(response.getTransResponseValue());
                    break;
            }
        }
        report.setMerchant(SharedHelper.getIntData(context, AppConstants.SharedPref.USER_ID));
        Gson gson = new Gson();
        String responseText = gson.toJson(responseList);
        report.setResponseText(responseText);
        report.setBusiness(SharedHelper.getIntData(context, AppConstants.SharedPref.BUSINESS_ID));
        report.setTid(SharedHelper.getStringData(context, AppConstants.SharedPref.TID));
        return report;
    }

    public static TransactionRequest generateTransactionObject(Context context, ArrayList<TransactionResponse> responseList) {
        TransactionRequest transactionRequest = new TransactionRequest();
        for (TransactionResponse response : responseList) {
            switch (response.getTransResponseName()) {
                case "Type":
                    transactionRequest.setTransactionType(response.getTransResponseValue());
                    break;
                case "UTI":
                    transactionRequest.setUti(response.getTransResponseValue());
                    break;
                case "Amount":
                    double amt = Double.parseDouble(response.getTransResponseValue());
//                    String finalAmt = String.valueOf(amt / 100);
                    String finalAmt = String.format("%.2f", (amt / 100));
                    transactionRequest.setAmount(finalAmt);
                    break;
                case "AuthCode":
                    transactionRequest.setAuthCode(response.getTransResponseValue());
                    break;
                case "Gratuity":
                    try {
                        double g_amt = Double.parseDouble(response.getTransResponseValue());
                        String g_finalAmt = String.valueOf(g_amt / 100);
                        transactionRequest.setGratuityAmount(g_finalAmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: HomeActivity.generateTransactionObject — failed to parse gratuity.
                        logReceiveError(context, "generateTransactionObject", String.valueOf(e.getMessage()), e);
                    }
                    break;
                case "Cashback":
                    try {
                        double c_amt = Double.parseDouble(response.getTransResponseValue());
                        String c_finalAmt = String.valueOf(c_amt / 100);
                        transactionRequest.setCashbackAmount(c_finalAmt);
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: HomeActivity.generateTransactionObject — failed to parse cashback.
                        logReceiveError(context, "generateTransactionObject", String.valueOf(e.getMessage()), e);
                    }
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
                case "TransactionNote":
                    transactionRequest.setTnxNote(response.getTransResponseValue());
                    break;
                case "ReceiptNumber":
                    transactionRequest.setReceiptId(response.getTransResponseValue());
                    break;
                case "Stan":
                    break;
//                case "CardType":
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
        return transactionRequest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        try {
            floatView = FloatView.getInstance(HomeActivity.this);
            floatView.createFloatView(20, 20);
        } catch (Exception e) {
            e.printStackTrace();

            /// Telemetry: HomeActivity.onCreate — failed to create float view.
            logReceiveError(this, "onCreate", String.valueOf(e.getMessage()), e);
        }

        /// Initialize Local Database
        localDb = DBHelper.getDatabase(getApplicationContext());
//      localDb = Room.databaseBuilder(getApplicationContext(),
//       PaxDB.class, "pax-paymentsave-db").build();


        String passedString;
        Bundle extras = getIntent().getExtras();
        try {
            if (extras != null) {
                passedString = extras.getString("response_list");
                if (passedString != null && !passedString.isEmpty()) {
                    Gson gson = new Gson();
                    transactionRequest = gson.fromJson(passedString, TransactionRequest.class);
                    if (!transactionRequest.isCancelled()) {
                        boolean printReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING);
                        boolean customerReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
                        boolean merchantReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);
                        if (customerReceiptValue || merchantReceiptValue) {
                            if (customerReceiptValue) {
                                PrintUtils.printReceiptCopy(HomeActivity.this, transactionRequest, "CARDHOLDER");
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (merchantReceiptValue) {
                                        PrintUtils.printReceiptCopy(HomeActivity.this, transactionRequest, "MERCHANT");
                                    }
                                }
                            }, 3000);
                        }
                        if (printReceiptValue) {
                            Intent intent = new Intent(HomeActivity.this, ReceiptPrintActivity.class);
                            intent.putExtra("response", passedString);
                            startActivity(intent);
                        }

                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
            // Telemetry: HomeActivity.onCreate — failed to parse or print the transaction response.
            logReceiveError(this, "onCreate", String.valueOf(e.getMessage()), e);
        }

        viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        String device_serial = MainUtils.getDeviceSerial(this);
        viewModel.getDeviceConfig(device_serial).observe(HomeActivity.this, new Observer<DeviceConfigResponse>() {
            @Override
            public void onChanged(DeviceConfigResponse deviceConfigResponse) {
                if (deviceConfigResponse != null) {
                    SharedHelper.putBusinessInfo(deviceConfigResponse.getData().getBusiness(), deviceConfigResponse.getData().getTid(), deviceConfigResponse.getData().getSupportEmail(), deviceConfigResponse.getData().getSupportPhone(), deviceConfigResponse.getData().getGratuityType(), deviceConfigResponse.getData().getBusiness().isLinkPaymentEnabled(), deviceConfigResponse.getData().getGratuityOptions(), deviceConfigResponse.getData().getCashbackOptions(), deviceConfigResponse.getData().isEReceipt());
                }
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.onboardFragment, R.id.transactionFragment, R.id.settingFragment).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        // Listen for changes in the navigation destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Hide the status and navigation bar when a fragment is selected
            // hideSystemUI();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the float view instance
        if (floatView != null) {
            floatView.release();
        }
        if (floatView != null) {
            floatView.removeFloatView();
        }
    }

    @Override
    protected void onResume() {
        try {
            SysTester.getInstance().enableNavigationBar(false);
            SysTester.getInstance().showNavigationBar(false);
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: HomeActivity.onResume — failed to hide navigation bar.
            logReceiveError(this, "onResume", String.valueOf(e.getMessage()), e);
        }
        super.onResume();
    }


    /**
     * Sends an activity failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code HomeActivity.generateTransactionObject}.
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
}