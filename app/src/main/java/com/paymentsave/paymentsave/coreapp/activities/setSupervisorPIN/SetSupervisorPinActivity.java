package com.paymentsave.paymentsave.coreapp.activities.setSupervisorPIN;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.StartPaymentActivity;
import com.paymentsave.paymentsave.coreapp.utils.TransparentProgressDialog;
import com.paymentsave.paymentsave.network.SharedHelper;

import java.util.HashMap;

public class SetSupervisorPinActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton pinVisibilityBtn;
    EditText pinPass1, pinPass2, pinPass3, pinPass4;
    TextView invalidInputAlert;
    Button btn00, btn01, btn02, btn03, btn04, btn05, btn06, btn07, btn08, btn09, btnBackSpace, setUpNewDeviceBtn;
    private TransparentProgressDialog pd;
    boolean visiblePinPass = false;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            boolean inputOneHasFocus = pinPass1.hasFocus();
            boolean inputTwoHasFocus = pinPass2.hasFocus();
            boolean inputThreeHasFocus = pinPass3.hasFocus();
            boolean inputFourHasFocus = pinPass4.hasFocus();

            if (charSequence.length() == 1 && inputOneHasFocus) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!visiblePinPass) {
                            pinPass1.setTransformationMethod(new PasswordTransformationMethod());
                        }
                        pinPass2.requestFocus();
                    }
                }, 300);
            }
            if (charSequence.length() == 1 && inputTwoHasFocus) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!visiblePinPass) {
                            pinPass2.setTransformationMethod(new PasswordTransformationMethod());
                        }
                        pinPass3.requestFocus();
                    }
                }, 300);
            }
            if (charSequence.length() == 1 && inputThreeHasFocus) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!visiblePinPass) {
                            pinPass3.setTransformationMethod(new PasswordTransformationMethod());
                        }
                        pinPass4.requestFocus();
                    }
                }, 300);
            }

            if (charSequence.length() == 0 && inputFourHasFocus) {
                pinPass3.requestFocus();
            }
            if (charSequence.length() == 0 && inputThreeHasFocus) {
                pinPass2.requestFocus();
            }
            if (charSequence.length() == 0 && inputTwoHasFocus) {
                pinPass1.requestFocus();
            }

            toggleProceedBtn();

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private SupervisorPinViewModel viewModel;

    private void toggleProceedBtn() {
        if (MainUtils.validateEditTextWithoutAlert(pinPass1) && MainUtils.validateEditTextWithoutAlert(pinPass2) && MainUtils.validateEditTextWithoutAlert(pinPass3) && MainUtils.validateEditTextWithoutAlert(pinPass4)) {
            invalidInputAlert.setVisibility(View.GONE);
            setUpNewDeviceBtn.setBackground(getResources().getDrawable(R.drawable.radius_btn_bg));
        } else {
            setUpNewDeviceBtn.setBackground(getResources().getDrawable(R.drawable.radius_gray));
        }
    }

    View.OnClickListener keypadOnclickListener = new View.OnClickListener() {
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

    private String mid, serialNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_supervisor_pin);
        viewModel = ViewModelProviders.of(this).get(SupervisorPinViewModel.class);
        if (getIntent() != null) {
            mid = getIntent().getStringExtra("mid");
            serialNo = getIntent().getStringExtra("serial_no");
        }
        pinPass1 = findViewById(R.id.pinInput1);
        pinPass2 = findViewById(R.id.pinInput2);
        pinPass3 = findViewById(R.id.pinInput3);
        pinPass4 = findViewById(R.id.pinInput4);
        invalidInputAlert = findViewById(R.id.invalid_input_alert);
        pinVisibilityBtn = findViewById(R.id.pinVisibilityBtn);
        pinVisibilityBtn.setOnClickListener(this);
        pinPass1.addTextChangedListener(textWatcher);
        pinPass2.addTextChangedListener(textWatcher);
        pinPass3.addTextChangedListener(textWatcher);
        pinPass4.addTextChangedListener(textWatcher);

        setUpNewDeviceBtn = findViewById(R.id.setup_your_new_device_btn);
        setUpNewDeviceBtn.setOnClickListener(this);
        btnBackSpace = findViewById(R.id.btnBackSpace);
        btnBackSpace.setOnClickListener(keypadOnclickListener);

        btn00 = findViewById(R.id.btn00);
        btn00.setOnClickListener(keypadOnclickListener);
        btn01 = findViewById(R.id.btn01);
        btn01.setOnClickListener(keypadOnclickListener);
        btn02 = findViewById(R.id.btn02);
        btn02.setOnClickListener(keypadOnclickListener);
        btn03 = findViewById(R.id.btn03);
        btn03.setOnClickListener(keypadOnclickListener);
        btn04 = findViewById(R.id.btn04);
        btn04.setOnClickListener(keypadOnclickListener);
        btn05 = findViewById(R.id.btn05);
        btn05.setOnClickListener(keypadOnclickListener);
        btn06 = findViewById(R.id.btn06);
        btn06.setOnClickListener(keypadOnclickListener);
        btn07 = findViewById(R.id.btn07);
        btn07.setOnClickListener(keypadOnclickListener);
        btn08 = findViewById(R.id.btn08);
        btn08.setOnClickListener(keypadOnclickListener);
        btn09 = findViewById(R.id.btn09);
        btn09.setOnClickListener(keypadOnclickListener);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pinVisibilityBtn:
                visiblePinPass = !visiblePinPass;
                setPinVisibility(visiblePinPass);
                break;
            case R.id.setup_your_new_device_btn:
                if (MainUtils.validateEditTextWithoutAlert(pinPass1) && MainUtils.validateEditTextWithoutAlert(pinPass2) && MainUtils.validateEditTextWithoutAlert(pinPass3) && MainUtils.validateEditTextWithoutAlert(pinPass4)) {

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.pin_confirm_alert, null);
                    dialogBuilder.setView(dialogView);
                    Button cancelButton = dialogView.findViewById(R.id.pp_cancel_btn);
                    Button confirmButton = dialogView.findViewById(R.id.pp_confirm_btn);
                    AlertDialog alertDialog = dialogBuilder.create();
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    confirmButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            showLoadingScreen();
                            String pin = pinPass1.getText().toString() + pinPass2.getText().toString() + pinPass3.getText().toString() + pinPass4.getText().toString();
                            HashMap<String, String> map = new HashMap<>();
                            map.put("mid", mid);
                            map.put("serial_no", serialNo);
                            map.put("s_pin", pin);
                            viewModel.setSupervisorPin(map).observe(SetSupervisorPinActivity.this, new Observer<Object>() {
                                @Override
                                public void onChanged(Object o) {
                                    try {
                                        pd.dismiss();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (o != null) {
                                        SharedHelper.putDeviceStatus(SetSupervisorPinActivity.this, AppConstants.SharedPref.DEVICE_STATUS, true);
                                        startActivity(new Intent(SetSupervisorPinActivity.this, StartPaymentActivity.class));
                                        finish();
                                    }
                                }
                            });

                        }
                    });
                    alertDialog.show();
                } else {
                    invalidInputAlert.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    void setPinVisibility(boolean visiblePinPass) {
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

    void setPin(String pin) {
        if (pinPass1.getText().length() == 0) {
            pinPass1.setText(pin);
        } else if (pinPass2.getText().length() == 0) {
            pinPass2.setText(pin);
        } else if (pinPass3.getText().length() == 0) {
            pinPass3.setText(pin);
        } else if (pinPass4.getText().length() == 0) {
            pinPass4.setText(pin);
        }
    }

    private void showLoadingScreen() {
        try {
            pd = TransparentProgressDialog.getInstance(this);
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}