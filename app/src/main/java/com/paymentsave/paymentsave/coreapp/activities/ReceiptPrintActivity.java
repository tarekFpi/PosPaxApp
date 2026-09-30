package com.paymentsave.paymentsave.coreapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.splash.SplashActivity;
import com.paymentsave.paymentsave.coreapp.fragments.viewModels.TransactionViewModel;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.coreapp.utils.QrUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.coreapp.utils.TransparentProgressDialog;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.request_models.TransactionRequest;

import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ReceiptPrintActivity extends AppCompatActivity {
    private static final String TAG = "ReceiptPrintActivity";
    private TransparentProgressDialog pd;
    private LinearLayout eReceiptBtn, qrReceiptBtn;
    private Button customerReceiptBtn, merchantReceiptBtn, noReceiptBtn;
    private TransactionRequest transactionRequest;
    private CountDownTimer countDownTimer;

    private ImageView resultImage;
    private TextView resultTitle, resultSubTitle;
    private AlertDialog dialog;

    private TransactionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_print);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        if (getIntent().getExtras() != null) {
            String responseString;
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                responseString = extras.getString("response");
                Log.e(TAG, "onCreate: " + responseString);
                if (responseString != null && !responseString.isEmpty()) {
                    try {
                        Gson gson = new Gson();
                        transactionRequest = gson.fromJson(responseString, TransactionRequest.class);
                    } catch (Exception e) {
                        Log.e(TAG, "onCreate: " + e.getMessage());
                        // Telemetry: ReceiptPrintActivity.onCreate — failed to parse receipt response.
                        logReceiveError("onCreate", String.valueOf(e.getMessage()), e);
                    }
                }
            }
        }


        pd = TransparentProgressDialog.getInstance(this);
        noReceiptBtn = findViewById(R.id.no_receipt_btn);
        resultImage = findViewById(R.id.result_icon);
        resultTitle = findViewById(R.id.result_title);
        resultSubTitle = findViewById(R.id.result_sub_title);

        if (transactionRequest.isApproved()) {
            PrintUtils.playAudio(this, true);
        } else {
            PrintUtils.playAudio(this, false);
        }

        if (transactionRequest.isApproved()) {
            resultImage.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.success_icon));
            resultTitle.setText(getString(R.string.approved));
            resultTitle.setTextColor(getResources().getColor(R.color.green_text_color));
        }

        if (!transactionRequest.isCancelled() && !transactionRequest.isApproved()) {
            resultImage.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.cancel_icon));
            resultTitle.setText(getString(R.string.declined));
            resultTitle.setTextColor(getResources().getColor(R.color.red));
        }

        noReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        merchantReceiptBtn = findViewById(R.id.merchant_receipt_btn);
        merchantReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            countDownTimer.cancel();
                            pd.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();

                            /// Telemetry: ReceiptPrintActivity.onCreate.merchantReceiptBtn — failed to cancel timer or dismiss loading.
                            logReceiveError("onCreate.merchantReceiptBtn", String.valueOf(e.getMessage()), e);
                        }
                        countDownTimer = new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                try {
                                    getOnBackPressedDispatcher().onBackPressed();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // Telemetry: ReceiptPrintActivity.onCreate.merchantReceiptBtn — failed to go back after merchant print.
                                    logReceiveError("onCreate.merchantReceiptBtn", String.valueOf(e.getMessage()), e);
                                }
                            }
                        };
                        countDownTimer.start();
                    }
                }, 3000);
                PrintUtils.printReceiptCopy(ReceiptPrintActivity.this, transactionRequest, "MERCHANT");
            }
        });

        eReceiptBtn = findViewById(R.id.email_receipt_btn);

        qrReceiptBtn = findViewById(R.id.qr_code);

        /// get eReceipt
       Boolean eReceipt =  SharedHelper.getBooleanData(ReceiptPrintActivity.this, AppConstants.SharedPref.ERECEIPT);

        /// get eReceipt with eReceiptBtn hide
       if(eReceipt.equals(true)){

           eReceiptBtn.setVisibility(View.VISIBLE);
       }else{
           eReceiptBtn.setVisibility(View.GONE);
       }

        qrReceiptBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            countDownTimer.cancel();
                            pd.show();
                            String encoded = Base64.encodeBase64String(transactionRequest.getUti().getBytes());
                            Bitmap bitmap = QrUtils.generateQrCode(BuildConfig.QR + "e-receipt/" + encoded, 800);
                            pd.dismiss();
                            showQrPopup(bitmap);

                        } catch (WriterException e) {
                            e.printStackTrace();
                            // Telemetry: ReceiptPrintActivity.onCreate.qrReceiptBtn — failed to generate QR receipt.
                            logReceiveError("onCreate.qrReceiptBtn", String.valueOf(e.getMessage()), e);
                        }
                    }
                }
        );

        eReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    countDownTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Telemetry: ReceiptPrintActivity.onCreate.eReceiptBtn — failed to cancel timer.
                    logReceiveError("onCreate.eReceiptBtn", String.valueOf(e.getMessage()), e);
                }
                showEmailPopup();
            }
        });


        customerReceiptBtn = findViewById(R.id.customer_receipt_btn);
        customerReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transactionRequest.getCardHolderReceipt() != null) {
                    String[] receiptTextList = transactionRequest.getCardHolderReceipt().split("&&");
                    List<String> receiptList = Arrays.asList(receiptTextList);
                    boolean isNotAuthorised = receiptList.contains("             NOT AUTHORISED             ");
                    Log.e(TAG, "isNotAuthorised: " + isNotAuthorised);
                }

                pd.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            customerReceiptBtn.setVisibility(View.GONE);
                            countDownTimer.cancel();
                            pd.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Telemetry: ReceiptPrintActivity.onCreate.customerReceiptBtn — failed to cancel timer or dismiss loading.
                            logReceiveError("onCreate.customerReceiptBtn", String.valueOf(e.getMessage()), e);
                        }
                        countDownTimer = new CountDownTimer(3000, 1000) {
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                try {
                               getOnBackPressedDispatcher().onBackPressed();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // Telemetry: ReceiptPrintActivity.onCreate.customerReceiptBtn — failed to go back after customer print.
                                    logReceiveError("onCreate.customerReceiptBtn", String.valueOf(e.getMessage()), e);
                                }
                            }
                        };
                        countDownTimer.start();
                    }
                }, 3000);
                PrintUtils.printReceiptCopy(ReceiptPrintActivity.this, transactionRequest, "CARDHOLDER");
            }
        });

        countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
//                NumberFormat f = new DecimalFormat("00");
//                long hour = (millisUntilFinished / 3600000) % 24;
//                long min = (millisUntilFinished / 60000) % 60;
//                long sec = (millisUntilFinished / 1000) % 60;
//                merchantPrintTimerTxt.setText(f.format(sec));
            }

            public void onFinish() {
                try {
                    getOnBackPressedDispatcher().onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();

                    /// Telemetry: ReceiptPrintActivity.onCreate.countDownTimer — failed to go back when timer finished.
                    logReceiveError("onCreate.countDownTimer", String.valueOf(e.getMessage()), e);
                }
            }
        };
        countDownTimer.start();

//        try {
//            countDownTimer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            pd.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: ReceiptPrintActivity.onDestroy — failed to dismiss loading dialog.
            logReceiveError("onDestroy", String.valueOf(e.getMessage()), e);
        }
    }

    public void showQrPopup(Bitmap qr) {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.qr_receipt_layout, null);
        dialog.setContentView(view);

        ImageView qrImage = view.findViewById(R.id.dialogQrImage);
        qrImage.setImageBitmap(qr);
        dialog.show();

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                try {
                    dialog.dismiss();
                    getOnBackPressedDispatcher().onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Telemetry: ReceiptPrintActivity.showQrPopup — failed to dismiss QR dialog or go back.
                    logReceiveError("showQrPopup", String.valueOf(e.getMessage()), e);
                }
            }
        };
        countDownTimer.start();
    }

    private void showEmailPopup() {

        // Create EditText
        EditText emailInput = new EditText(this);
        emailInput.setHint("Enter your email");
        emailInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setPadding(50, 40, 50, 40);

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();

                if (email.isEmpty()) {
                    emailInput.setError(null);
                    emailInput.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
                    return;
                }

                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError(null);
                    emailInput.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
                } else {
                    emailInput.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D32F2F")));
                    emailInput.setError("Invalid email");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // Build dialog
        dialog = new AlertDialog.Builder(this)
                .setTitle("Get E-Receipt")
                .setView(emailInput)
                .setPositiveButton("Send", null) // override later
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        // Equivalent to "onDestroy" for dialog
        dialog.setOnDismissListener(d -> {
            // cleanup here
            countDownTimer = new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    try {
                        getOnBackPressedDispatcher().onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: ReceiptPrintActivity.showEmailPopup — failed to go back after email dialog dismiss.
                        logReceiveError("showEmailPopup", String.valueOf(e.getMessage()), e);
                    }
                }
            };
            countDownTimer.start();
        });

        // Override positive button to prevent auto close
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                emailInput.setError("Email is required");
                emailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Enter a valid email");
                emailInput.requestFocus();
                return;
            }

            // 👉 Your API call here
            sendEReceipt(email);
        });


    }

    private void sendEReceipt(String email) {
        // Replace this with your real API call
        String token = SharedHelper.getToken(this);
        viewModel.requestEmailReceipt(token, email, transactionRequest.getUti()).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                if (o != null) {
                    dialog.dismiss();
                    try {
                        Toasty.success(
                                ReceiptPrintActivity.this,
                                "If this email address is correct, your receipt will be delivered shortly.",
                                Toasty.LENGTH_LONG
                        ).show();
                        getOnBackPressedDispatcher().onBackPressed();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: ReceiptPrintActivity.sendEReceipt — failed to show success toast or go back.
                        logReceiveError("sendEReceipt", String.valueOf(e.getMessage()), e);
                    }
                }
            }
        });
    }

    /**
     * Sends an activity failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code ReceiptPrintActivity.onCreate.merchantReceiptBtn}.
     * {@code message} must already be a string.
     */
    private void logReceiveError(String functionPath, String message, Throwable throwable) {
        String eventName = TAG + "." + functionPath;
        TelemetryLogger.error(
                SharedHelper.getStringData(this, AppConstants.SharedPref.TID),
                eventName,
                message != null ? message : "",
                throwable
        );
    }
}