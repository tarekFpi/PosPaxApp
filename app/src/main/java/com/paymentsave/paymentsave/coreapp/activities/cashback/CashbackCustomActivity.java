package com.paymentsave.paymentsave.coreapp.activities.cashback;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_CASHBACK;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_PREAUTH;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_SALE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.eft.libpositive.PosIntegrate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.gratuity.GratuityActivity;
import com.paymentsave.paymentsave.network.SharedHelper;

import java.util.HashMap;

public class CashbackCustomActivity extends AppCompatActivity {
    EditText editTextAmount;
    private int transType = 0;
    private double mainAmount;
    private String mainAmt = "";
    private Button backBtn, submitBtn;
    private FloatingActionButton backBtn2;
    private TextView errorTextView;

    private boolean gratuityEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashback_custom);
        if (getIntent().getExtras() != null) {
            mainAmt = getIntent().getStringExtra("main_amt");
            mainAmount = Double.parseDouble(getIntent().getStringExtra("main_amt"));
            transType = getIntent().getIntExtra("trans_type", 0);
        }
        errorTextView = findViewById(R.id.cashback_invalid_amount_text);
        submitBtn = findViewById(R.id.cashback_custom_submit_btn);
        editTextAmount = findViewById(R.id.cashback_ed_enter_amount);
        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Float.parseFloat(charSequence.toString().split("£")[1]) > 0) {
                    errorTextView.setVisibility(View.GONE);
                    submitBtn.setBackground(AppCompatResources.getDrawable(CashbackCustomActivity.this, R.drawable.radius_btn_bg));
                } else {
                    submitBtn.setBackground(AppCompatResources.getDrawable(CashbackCustomActivity.this, R.drawable.radius_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        backBtn2 = findViewById(R.id.custom_cashback_back_btn);
        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        // Buttons in UI
        Button oneBtn = findViewById(R.id.btnOne);
        Button twoBtn = findViewById(R.id.btnTwo);
        Button threeBtn = findViewById(R.id.btnThree);
        Button fourBtn = findViewById(R.id.btnFour);
        Button fiveBtn = findViewById(R.id.btnFive);
        Button sixBtn = findViewById(R.id.btnSix);
        Button sevenBtn = findViewById(R.id.btnSeven);
        Button eightBtn = findViewById(R.id.btnEight);
        Button nineBtn = findViewById(R.id.btnNine);
        Button zeroBtn = findViewById(R.id.btnZero);
        Button doubleZeroBtn = findViewById(R.id.btnDoubleZero);
        ImageButton backSpaceBtn = findViewById(R.id.btnC);

        oneBtn.setOnClickListener(this::numberEvent);
        twoBtn.setOnClickListener(this::numberEvent);
        threeBtn.setOnClickListener(this::numberEvent);
        fourBtn.setOnClickListener(this::numberEvent);
        fiveBtn.setOnClickListener(this::numberEvent);
        sixBtn.setOnClickListener(this::numberEvent);
        sevenBtn.setOnClickListener(this::numberEvent);
        eightBtn.setOnClickListener(this::numberEvent);
        nineBtn.setOnClickListener(this::numberEvent);
        zeroBtn.setOnClickListener(this::numberEvent);
        doubleZeroBtn.setOnClickListener(this::numberEvent);
        backSpaceBtn.setOnClickListener(this::numberEvent);

        gratuityEnabled = SharedHelper.getBooleanData(this, AppConstants.SharedPref.GRATUITY);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Float.parseFloat(editTextAmount.getText().toString().split("£")[1]) > 0) {
                    String nAmount = editTextAmount.getText().toString().split("£")[1].replace(".", "").trim();
                    if (gratuityEnabled) {
                        Intent intent = new Intent(CashbackCustomActivity.this, GratuityActivity.class);
                        intent.putExtra("main_amt", mainAmt);
                        intent.putExtra("cashback_amt", nAmount);
                        intent.putExtra("trans_type", transType);
                        startActivity(intent);
                    } else {
                        // Hide Keyboard
                        MainUtils.hideKeyboard(view.getContext());
                        boolean printReceiptValue = SharedHelper.getBooleanData(CashbackCustomActivity.this, AppConstants.SharedPref.RECEIPT_PRINTING);
                        HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                        args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                        args.put(CT_AMOUNT_CASHBACK, nAmount);
//                        args.put(CT_DISABLEPRINTING, String.valueOf(!printReceiptValue));
                        args.put(CT_DISABLEPRINTING, "true");
                        args.put(CT_LANGUAGE, "en_GB");
                        if (transType == 0)
                            PosIntegrate.executeTransaction(CashbackCustomActivity.this, TRANSACTION_TYPE_SALE, args);
                        else
                            PosIntegrate.executeTransaction(CashbackCustomActivity.this, TRANSACTION_TYPE_PREAUTH, args);
                    }
                } else {
                    errorTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void numberEvent(View view) {

        String number = editTextAmount.getText().toString();

        if (view.getId() == R.id.btnOne) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.01;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnTwo) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.02;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnThree) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.03;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnFour) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.04;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnFive) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.05;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnSix) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.06;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnSeven) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.07;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnEight) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.08;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnNine) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.09;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnZero) {
            double currentValue = Double.parseDouble(number.split("£")[1]) * 10;
            number = "£" + String.format("%.2f", currentValue);
        } else if (view.getId() == R.id.btnDoubleZero) {
            double currentValue = Double.parseDouble(number.split("£")[1]) * 100;
            number = "£" + String.format("%.2f", currentValue);
        } else if (view.getId() == R.id.btnC) {
            float currentValue = Float.parseFloat(number.split("£")[1]);
            if (currentValue > 0.01) {
                double afterValue = currentValue * 0.1;
                String strValue = String.format("%.3f", afterValue);
                if (afterValue < 0.00) {
                    number = "£0.00";
                } else {
                    try {
                        int dot_index = strValue.indexOf(".");
                        number = "£" + strValue.substring(0, dot_index) + strValue.substring(dot_index, (dot_index + 3));
                    } catch (Exception e) {
                        number = "£" + strValue + "0";
                        e.printStackTrace();
                    }
                }
            } else {
                number = "£0.00";
            }
        }
        editTextAmount.setText(number);
    }
}