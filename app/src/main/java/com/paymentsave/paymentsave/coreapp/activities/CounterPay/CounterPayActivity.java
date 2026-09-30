package com.paymentsave.paymentsave.coreapp.activities.CounterPay;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.pax.dal.entity.ENavigationKey;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.receiver.AwsIotTerminalClient;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.ReceiptPrintActivity;
import com.paymentsave.paymentsave.coreapp.activities.splash.SplashActivity;
import com.paymentsave.paymentsave.coreapp.activities.test.system.SysTester;
import com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.verifySupervisorPin.VerifyPinViewModel;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.coreapp.utils.TransparentProgressDialog;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.network.WebSocketUtils;
import com.paymentsave.paymentsave.request_models.TransactionRequest;
import com.paymentsave.paymentsave.responses.PinVerifyResponse.PinVerifyResponse;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class CounterPayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int MIN_DISTANCE = 250;
    private static final String TAG = "CounterPayActivity";
     private static final String brokerUatURL = "a8ysk4odmdnf8-ats.iot.eu-west-1.amazonaws.com"; //testing
   // private static final String brokerURL = "a8ysk4odmdnf8-ats.iot.eu-west-2.amazonaws.com"; //live
    private static String clientID = "210202841806";
    public boolean optionVisible = false;
    private AwsIotTerminalClient terminalClient;
    private TextView connectionTv;
    private ImageView businessLogo;
    private ProgressBar connectionPb;
    private LinearLayout connectionStatBg;
    private AppCompatImageButton optionsBtn;
    private TransactionRequest transactionRequest;
    private BottomSheetDialog supervisorPinDialog;
    private ImageView pinLayoutBackBtn;
    private ImageButton pinVisibilityBtn;
    private boolean visiblePinPass = false;
    private EditText pinPass1, pinPass2, pinPass3, pinPass4;
    private Button btn00, btn01, btn02, btn03, btn04, btn05, btn06, btn07, btn08, btn09, btnBackSpace;
    private TransparentProgressDialog pd;
    private VerifyPinViewModel viewModel;
    View.OnClickListener keypadOnclickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn00:
                    setPin("0");
                    break;
                case R.id.btn01:
                    setPin("1");
                    break;
                case R.id.btn02:
                    setPin("2");
                    break;
                case R.id.btn03:
                    setPin("3");
                    break;
                case R.id.btn04:
                    setPin("4");
                    break;
                case R.id.btn05:
                    setPin("5");
                    break;
                case R.id.btn06:
                    setPin("6");
                    break;
                case R.id.btn07:
                    setPin("7");
                    break;
                case R.id.btn08:
                    setPin("8");
                    break;
                case R.id.btn09:
                    setPin("9");
                    break;
                case R.id.btnBackSpace:
                    backSpace();
                    break;
            }

        }
    };

    private float x1, x2;

    @Override
    protected void onResume() {
        try {
            SysTester.getInstance().enableNavigationBar(false);
            SysTester.getInstance().showNavigationBar(false);
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: CounterPayActivity.onResume — failed to hide navigation bar.
            logReceiveError("onResume", String.valueOf(e.getMessage()), e);
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_pay);
        viewModel = ViewModelProviders.of(this).get(VerifyPinViewModel.class);
        businessLogo = findViewById(R.id.business_logo);
        String logoSrc = SharedHelper.getStringData(this, AppConstants.SharedPref.BUSINESS_LOGO);
        Glide.with(this
                )
                .load(logoSrc)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(businessLogo);

        connectionTv = findViewById(R.id.connection_text);
        connectionPb = findViewById(R.id.connection_progress_bar);
        connectionStatBg = findViewById(R.id.connection_stat_bg);
        optionsBtn = findViewById(R.id.cp_options_btn);
        supervisorPinDialog = MainUtils.getSupervisorBottomSheet(this, this);
        pinPass1 = supervisorPinDialog.findViewById(R.id.pinInput1);
        pinPass2 = supervisorPinDialog.findViewById(R.id.pinInput2);
        pinPass3 = supervisorPinDialog.findViewById(R.id.pinInput3);
        pinPass4 = supervisorPinDialog.findViewById(R.id.pinInput4);
        pinLayoutBackBtn = supervisorPinDialog.findViewById(R.id.backButton);
        pinLayoutBackBtn.setOnClickListener(this);
        pinVisibilityBtn = supervisorPinDialog.findViewById(R.id.pinVisibilityBtn);
        pinVisibilityBtn.setOnClickListener(this);
        btn00 = supervisorPinDialog.findViewById(R.id.btn00);
        btn00.setOnClickListener(keypadOnclickListener);
        btn01 = supervisorPinDialog.findViewById(R.id.btn01);
        btn01.setOnClickListener(keypadOnclickListener);
        btn02 = supervisorPinDialog.findViewById(R.id.btn02);
        btn02.setOnClickListener(keypadOnclickListener);
        btn03 = supervisorPinDialog.findViewById(R.id.btn03);
        btn03.setOnClickListener(keypadOnclickListener);
        btn04 = supervisorPinDialog.findViewById(R.id.btn04);
        btn04.setOnClickListener(keypadOnclickListener);
        btn05 = supervisorPinDialog.findViewById(R.id.btn05);
        btn05.setOnClickListener(keypadOnclickListener);
        btn06 = supervisorPinDialog.findViewById(R.id.btn06);
        btn06.setOnClickListener(keypadOnclickListener);
        btn07 = supervisorPinDialog.findViewById(R.id.btn07);
        btn07.setOnClickListener(keypadOnclickListener);
        btn08 = supervisorPinDialog.findViewById(R.id.btn08);
        btn08.setOnClickListener(keypadOnclickListener);
        btn09 = supervisorPinDialog.findViewById(R.id.btn09);
        btn09.setOnClickListener(keypadOnclickListener);
        btnBackSpace = supervisorPinDialog.findViewById(R.id.btnBackSpace);
        btnBackSpace.setOnClickListener(keypadOnclickListener);

        // Check connectivity
        checkConnectivity();

        // Get TID Client
        clientID = SharedHelper.getStringData(this, AppConstants.SharedPref.TID);

        // Mqtt Subscribe
        terminalClient =
                new AwsIotTerminalClient(
                        this,
                        brokerUatURL,
                        8883,
                        clientID
                );

        try {
            terminalClient.connectAndListen();
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: CounterPayActivity.onCreate.connectAndListen — MQTT connect failed.
            logReceiveError("onCreate.connectAndListen", String.valueOf(e.getMessage()), e);
        }

        //Init popup menu
        final PopupMenu popupMenu = new PopupMenu(
                this, //the context
                optionsBtn //UI view where to click to show the popup menu
        );

        //add menu xml to our popup menu
        popupMenu.getMenuInflater().inflate(R.menu.counter_pay_menu, popupMenu.getMenu());

        //handle popup menu item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //get id of menu item clicked
                int id = menuItem.getItemId();
                //handle clicks
                if (id == R.id.enable_general_mode) {
                    supervisorPinDialog.show();
                    return true;
                } else if (id == R.id.exit_app_btn) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CounterPayActivity.this, R.style.AlertDialogTheme);
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setTitle("Exit App !!!");
                    alertDialog.setMessage("Are you sure you want to close this app ?");
                    alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            WebSocketUtils.closeConnection();
                            finishAffinity();
                            System.exit(0);
                        }
                    }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialog.show();
                } else if (id == R.id.wifi_setting_btn) {
                    enableBackKey();
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    startActivity(intent);
                } else if (id == R.id.cellular_setting_btn) {
                    enableBackKey();
                    // Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                    // startActivity(intent);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setClassName("com.android.phone", "com.android.phone.MobileNetworkSettings");
                    startActivity(intent);
                }
                return false;
            }
        });

        optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        try {
            String passedString;
            Bundle extras = getIntent().getExtras();
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
                                PrintUtils.printReceiptCopy(CounterPayActivity.this, transactionRequest, "CARDHOLDER");
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (merchantReceiptValue) {
                                        PrintUtils.printReceiptCopy(CounterPayActivity.this, transactionRequest, "MERCHANT");
                                    }
                                }
                            }, 3000);
                        } else {
                            if (printReceiptValue) {
                                Intent intent = new Intent(CounterPayActivity.this, ReceiptPrintActivity.class);
                                intent.putExtra("response", passedString);
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());
            // Telemetry: CounterPayActivity.onCreate — failed to parse or print the transaction response.
            logReceiveError("onCreate", String.valueOf(e.getMessage()), e);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (!optionVisible) {
                        showPinVerificationDialog(this);
                    }
                } else {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pinVisibilityBtn:
                visiblePinPass = !visiblePinPass;
                setPinVisibility(visiblePinPass);
                break;
            case R.id.backButton:
                try {
                    supervisorPinDialog.dismiss();
                    pinPass1.setText("");
                    pinPass2.setText("");
                    pinPass3.setText("");
                    pinPass4.setText("");
                } catch (Exception e) {
                    e.printStackTrace();

                    /// Telemetry: CounterPayActivity.onClick — failed to dismiss PIN dialog.
                    logReceiveError("onClick", String.valueOf(e.getMessage()), e);
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        try {
            terminalClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            /// Telemetry: CounterPayActivity.onDestroy — MQTT disconnect failed.
            logReceiveError("onDestroy", String.valueOf(e.getMessage()), e);
        }
        super.onDestroy();
    }

    private void setPinVisibility(boolean visiblePinPass) {
        if (visiblePinPass) {
            // Show password
            pinPass1.setTransformationMethod(null);
            pinPass2.setTransformationMethod(null);
            pinPass3.setTransformationMethod(null);
            pinPass4.setTransformationMethod(null);
            pinVisibilityBtn.setImageResource(R.drawable.ic_baseline_visibility_24);
        } else {
            // Hide password
            pinPass1.setTransformationMethod(new PasswordTransformationMethod());
            pinPass2.setTransformationMethod(new PasswordTransformationMethod());
            pinPass3.setTransformationMethod(new PasswordTransformationMethod());
            pinPass4.setTransformationMethod(new PasswordTransformationMethod());
            pinVisibilityBtn.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }
    }

    private void backSpace() {
        if (pinPass4.getText().length() > 0) {
            pinPass4.setText("");
        } else if (pinPass3.getText().length() > 0) {
            pinPass3.setText("");
        } else if (pinPass2.getText().length() > 0) {
            pinPass2.setText("");
        } else if (pinPass1.getText().length() > 0) {
            pinPass1.setText("");
        }
    }

    private void setPin(String pin) {
        if (pinPass1.getText().length() == 0) {
            pinPass1.setText(pin);
        } else if (pinPass2.getText().length() == 0) {
            pinPass2.setText(pin);
        } else if (pinPass3.getText().length() == 0) {
            pinPass3.setText(pin);
        } else if (pinPass4.getText().length() == 0) {
            pinPass4.setText(pin);
            verifySupervisorPin();
        }
    }

    private void verifySupervisorPin() {
        showLoadingScreen();
        String pin = pinPass1.getText().toString() + pinPass2.getText().toString() + pinPass3.getText().toString() + pinPass4.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("mid", SharedHelper.getStringData(this, AppConstants.SharedPref.MID));
        map.put("serial_no", MainUtils.getDeviceSerial(this));
        map.put("s_pin", pin);
        viewModel.verifySupervisorPin(map).observe(this, new Observer<PinVerifyResponse>() {
            @Override
            public void onChanged(PinVerifyResponse pinVerifyResponse) {
                if (pinVerifyResponse != null && pinVerifyResponse.getCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        pd.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: CounterPayActivity.verifySupervisorPin.onChanged — failed to dismiss loading dialog after PIN success.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }
                    try {
                        supervisorPinDialog.dismiss();
                        pinPass1.setText("");
                        pinPass2.setText("");
                        pinPass3.setText("");
                        pinPass4.setText("");

                    } catch (Exception e) {
                        e.printStackTrace();

                        // Telemetry: CounterPayActivity.verifySupervisorPin.onChanged — failed to dismiss PIN sheet after success.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }
                    // Settings selected
                    SharedHelper.putBooleanData(CounterPayActivity.this, AppConstants.SharedPref.INTEGRATION_MODE, false);
                    try {
                        WebSocketUtils.closeConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: CounterPayActivity.verifySupervisorPin.onChanged — failed to close websocket.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }

                    try {
                        // close connection
                        terminalClient.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: CounterPayActivity.verifySupervisorPin.onChanged — failed to disconnect MQTT.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }


                    // Reload App
                    startActivity(new Intent(CounterPayActivity.this, SplashActivity.class));
                    finish();

                } else {

                    try {
                        pd.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();

                        /// Telemetry: CounterPayActivity.verifySupervisorPin.onChanged — failed to dismiss loading dialog after PIN failure.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }
                    resetPin();
                }
            }
        });
    }

    private void showLoadingScreen() {
        try {
            pd = TransparentProgressDialog.getInstance(this);
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: CounterPayActivity.showLoadingScreen — failed to show loading dialog.
            logReceiveError("showLoadingScreen", String.valueOf(e.getMessage()), e);
        }
    }

    private void resetPin() {
        pinPass1.setText("");
        pinPass2.setText("");
        pinPass3.setText("");
        pinPass4.setText("");
    }

    private void checkConnectivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                if (WebSocketUtils.isSocketConnected) {
                if (terminalClient.isConnected) {
                    connectionTv.setText(getString(R.string.connected));
                    connectionPb.setVisibility(View.GONE);
                    connectionStatBg.setBackground(AppCompatResources.getDrawable(CounterPayActivity.this, R.drawable.green_radious_bg));
                } else {
                    connectionTv.setText(getString(R.string.connecting));
                    connectionPb.setVisibility(View.VISIBLE);
                    connectionStatBg.setBackground(AppCompatResources.getDrawable(CounterPayActivity.this, R.drawable.gray_light_radius));
                }
                checkConnectivity();
            }
        }, 200);
    }

    private void enableBackKey() {
        SysTester.getInstance().showNavigationBar(true);
        SysTester.getInstance().enableNavigationBar(true);
        ENavigationKey eNavigationKey = ENavigationKey.BACK;
        SysTester.getInstance().enableNavigationKey(eNavigationKey, true);
    }

    public void showPinVerificationDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Admin PIN Verification");

        // Set up the input
        final EditText input = new EditText(context);
        input.setBackground(AppCompatResources.getDrawable(context, R.drawable.radius_gray_btn));
        input.setHint("* * * * * *");
        input.setGravity(Gravity.CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        // Set up the layout to add margin to EditText
        FrameLayout container = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );

        // Set margin (left, top, right, bottom)
        int margin = 50; // Change this value as needed
        params.setMargins(margin, margin, margin, margin);

        input.setLayoutParams(params);
        container.addView(input);

        builder.setView(container);

        // Set up the buttons
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pin = input.getText().toString();

                // Example PIN check, replace "1234" with your actual PIN validation logic
                if (pin.equals("102019")) {
                    Toast.makeText(context, "PIN Verified Successfully!", Toast.LENGTH_SHORT).show();
                    optionsBtn.setVisibility(View.VISIBLE);
                    optionVisible = true;
                } else {
                    Toast.makeText(context, "Invalid Admin PIN!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Sends an activity failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code CounterPayActivity.onCreate.connectAndListen}.
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