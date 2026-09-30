package com.paymentsave.paymentsave.coreapp.activities.splitBillPay;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_SALE;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eft.libpositive.PosIntegrate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.ReceiptPrintActivity;
import com.paymentsave.paymentsave.coreapp.activities.cashback.CashbackActivity;
import com.paymentsave.paymentsave.coreapp.activities.gratuity.GratuityActivity;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.request_models.TransactionRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class SplitBillPayActivity extends AppCompatActivity {

    private static final String TAG = "SplitBill";
    private FloatingActionButton backBtn;
    private Button cardPaymentBtm;
    private TextView memberCountTxt, amountValueTxt, totalAmtTxt, dueAmountTxt;
    private int initiatedPayment = 1;
    private int totalPeople = 1;
    private String totalAmount = "";
    private String dueAmount = "";
    private boolean isEqualSplit = false;

    private String responseString;
    private TransactionRequest transactionRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_bill_pay);

        if (getIntent().getExtras() != null) {
            totalAmount = getIntent().getExtras().getString("total_amount");
            dueAmount = getIntent().getExtras().getString("due_amount", totalAmount);
            isEqualSplit = getIntent().getExtras().getBoolean("is_equal_split");
            totalPeople = getIntent().getExtras().getInt("total_people", 1);
            initiatedPayment = getIntent().getExtras().getInt("initiated_payment", 1);
            responseString = getIntent().getExtras().getString("response", null);
            if (responseString != null && !responseString.isEmpty()) {
                Gson gson = new Gson();
                transactionRequest = gson.fromJson(responseString, TransactionRequest.class);
            }
        }

        if (SharedHelper.getStringData(SplitBillPayActivity.this, AppConstants.SharedPref.SPLIT_BILL_ID).isEmpty()) {
            String tid = SharedHelper.getStringData(SplitBillPayActivity.this, AppConstants.SharedPref.TID);
            String splitBillId = DateUtils.generateUniqueSplitBillId(tid);
            SharedHelper.putStringData(SplitBillPayActivity.this, AppConstants.SharedPref.SPLIT_BILL_ID, splitBillId);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplitBillPayActivity.this, R.style.AlertDialogTheme);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle("Are you sure you want to leave this page ?");
                alertDialog.setMessage("Please note that any details you have filled in will not be saved .");
                alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedHelper.putStringData(SplitBillPayActivity.this, AppConstants.SharedPref.SPLIT_BILL_ID, "");
                        Intent responseintent = new Intent(SplitBillPayActivity.this, HomeActivity.class);
                        responseintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(responseintent);
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.show();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        backBtn = findViewById(R.id.split_bill_back_btn);
        dueAmountTxt = findViewById(R.id.due_amount_txt);
        memberCountTxt = findViewById(R.id.member_count_text);
        amountValueTxt = findViewById(R.id.amount_value_text);
        totalAmtTxt = findViewById(R.id.total_amt_text);
        cardPaymentBtm = findViewById(R.id.card_payment_btn);

        if (transactionRequest != null && (transactionRequest.getTransactionType().equals("REFUND_AUTO") || transactionRequest.getTransactionType().equals("MANUAL_REVERSAL_AUTO"))) {
            Intent responseintent = new Intent(SplitBillPayActivity.this, HomeActivity.class);
            responseintent.putExtra("response_list", responseString);
            responseintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(responseintent);
        }

        Log.e(TAG, "onCreate: Split Split");
        if (transactionRequest != null && transactionRequest.isApproved()) {
            initiatedPayment = initiatedPayment + 1;
            AppConstants.previousPay = String.valueOf(Float.parseFloat(AppConstants.previousPay) + Float.parseFloat(transactionRequest.getAmount()));
            if (initiatedPayment > totalPeople || Float.parseFloat(AppConstants.previousPay) >= Float.parseFloat(totalAmount)) {
                AppConstants.isSplitBillRunning = false;
                SharedHelper.putStringData(SplitBillPayActivity.this, AppConstants.SharedPref.SPLIT_BILL_ID, "");
                AppConstants.splitBillTotalAmount = "0.00";
                AppConstants.initiatedPaymentNumber = 1;
                AppConstants.totalPeople = 1;
                AppConstants.dueAmount = "0.00";
                AppConstants.previousPay = "0.00";
                AppConstants.isEqualSplit = false;

                Intent responseintent = new Intent(SplitBillPayActivity.this, HomeActivity.class);
                responseintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(responseintent);

                // boolean customerReceipt = SharedHelper.getBooleanData(SplitBillPayActivity.this, AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
                // boolean merchantReceipt = SharedHelper.getBooleanData(SplitBillPayActivity.this, AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);

//                if (customerReceipt || merchantReceipt) {
//                    showSuccessPopup(this, totalAmount);
//                } else {
//                    Intent responseintent = new Intent(SplitBillPayActivity.this, HomeActivity.class);
//                    responseintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(responseintent);
//                }
            }

            dueAmount = String.format("%.2f", (Float.parseFloat(totalAmount) - Float.parseFloat(AppConstants.previousPay)));
        }

        totalAmtTxt.setText(String.format("Total: £%s", totalAmount));
        dueAmountTxt.setText(String.format("Due: %s", dueAmount));
        memberCountTxt.setText(String.format("Payment %s of %s", initiatedPayment, totalPeople));

        AppConstants.isSplitBillRunning = true;
        AppConstants.splitBillTotalAmount = totalAmount;
        AppConstants.dueAmount = dueAmount;
        AppConstants.initiatedPaymentNumber = initiatedPayment;
        AppConstants.totalPeople = totalPeople;
        AppConstants.isEqualSplit = isEqualSplit;

        if (isEqualSplit) {
            float amount = Float.parseFloat(totalAmount) / totalPeople;
            String mainAmount = "";
            if (initiatedPayment == totalPeople) {
                Log.e(TAG, "onCreate: " + dueAmount);
                float fAmount = (Float.parseFloat(dueAmount) - Float.parseFloat(amountValueTxt.getText().toString().split("£")[1]));
                // Convert the float to a BigDecimal for precise rounding
                BigDecimal bigAmount = new BigDecimal(Float.toString(fAmount));
                // Round to two decimal places using HALF_UP rounding mode
                BigDecimal roundedAmount = bigAmount.setScale(2, RoundingMode.HALF_UP);
                mainAmount = String.valueOf(roundedAmount);
            } else {
                mainAmount = String.format("%.2f", amount);
            }
            amountValueTxt.setText(String.format("£%s", mainAmount));
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppConstants.isSplitBillRunning = false;
                SharedHelper.putStringData(SplitBillPayActivity.this, AppConstants.SharedPref.SPLIT_BILL_ID, "");
                AppConstants.splitBillTotalAmount = "0.00";
                AppConstants.initiatedPaymentNumber = 1;
                AppConstants.totalPeople = 1;
                AppConstants.dueAmount = "0.00";
                AppConstants.previousPay = "0.00";
                AppConstants.isEqualSplit = false;
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        cardPaymentBtm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean gratuityEnabled = SharedHelper.getBooleanData(SplitBillPayActivity.this, AppConstants.SharedPref.GRATUITY);
                boolean cashbackEnabled = SharedHelper.getBooleanData(SplitBillPayActivity.this, AppConstants.SharedPref.CASHBACK);
                if (cashbackEnabled) {
                    Intent intent = new Intent(SplitBillPayActivity.this, CashbackActivity.class);
                    intent.putExtra("main_amt", amountValueTxt.getText().toString().split("£")[1]);
                    startActivity(intent);
                } else if (gratuityEnabled) {
                    Intent intent = new Intent(SplitBillPayActivity.this, GratuityActivity.class);
                    intent.putExtra("main_amt", amountValueTxt.getText().toString().split("£")[1]);
                    startActivity(intent);
                } else {
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    String nAmount = amountValueTxt.getText().toString().split("£")[1].replace(".", "").trim();
                    args.put(CT_DISABLEPRINTING, "true");
                    args.put(CT_AMOUNT, nAmount);
                    args.put(CT_LANGUAGE, "en_GB");
                    PosIntegrate.executeTransaction(SplitBillPayActivity.this, TRANSACTION_TYPE_SALE, args);
                }
            }
        });


        boolean printReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING);
        boolean customerReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
        boolean merchantReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);
        if (printReceiptValue && responseString != null && !transactionRequest.isCancelled()) {
            if (customerReceiptValue || merchantReceiptValue) {
                if (customerReceiptValue) {
                    PrintUtils.printReceiptCopy(SplitBillPayActivity.this, transactionRequest, "CARDHOLDER");
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (merchantReceiptValue) {
                            PrintUtils.printReceiptCopy(SplitBillPayActivity.this, transactionRequest, "MERCHANT");
                        }
                    }
                }, 3000);
            }

            Intent intent = new Intent(SplitBillPayActivity.this, ReceiptPrintActivity.class);
            intent.putExtra("response", responseString);
            startActivity(intent);

        }
    }

    public void showSuccessPopup(Context context, String totalAmount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = getLayoutInflater().inflate(R.layout.payment_success, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        // Prevent dismissing by tapping outside
        dialog.setCanceledOnTouchOutside(false);

        // Prevent dismissing with Back button
        dialog.setCancelable(false);

        TextView amtText = view.findViewById(R.id.split_success_amt);
        amtText.setText(String.format("£%s", totalAmount));
        Button backBtn = view.findViewById(R.id.home_back_btn);

        backBtn.setOnClickListener(v -> {
            dialog.dismiss(); // Only closes when you want
            Intent responseintent = new Intent(SplitBillPayActivity.this, HomeActivity.class);
            responseintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(responseintent);
        });

        dialog.show();
    }
}