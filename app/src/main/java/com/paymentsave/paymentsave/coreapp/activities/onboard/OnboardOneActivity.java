package com.paymentsave.paymentsave.coreapp.activities.onboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.activities.setSupervisorPIN.SetSupervisorPinActivity;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.AccountVerificationResponse.AccountVerificationResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationCodeResponse;
import com.paymentsave.paymentsave.responses.ActivationCodeResponse.ActivationRequestResponse;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class OnboardOneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OnboardOneActivity";
    Button setupBtn;
    private String deviceSerial;
    private BottomSheetDialog dialog;
    private EditText merchantIdEt;
    private FloatingActionButton dialogBackBtn;
    private Button requestAccessLinkBtn, skipBtn;
    private OnboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_one);
        if (getIntent() != null) {
            deviceSerial = getIntent().getStringExtra("device_serial");
        }
        viewModel = ViewModelProviders.of(this).get(OnboardViewModel.class);
        dialog = MainUtils.getBottomSheet(OnboardOneActivity.this, this);
        merchantIdEt = dialog.findViewById(R.id.merchant_id_et);
        dialogBackBtn = dialog.findViewById(R.id.backButton);
        dialogBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        requestAccessLinkBtn = dialog.findViewById(R.id.request_access_link_btn);
        requestAccessLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MainUtils.validateEditText(merchantIdEt)) {

                    String mid = merchantIdEt.getText().toString();
                    requestAccessLinkBtn.setVisibility(View.GONE);
                    HashMap<String, String> query = new HashMap<>();
                    query.put("pax_sn", deviceSerial);

                    viewModel.validateTerminalActivation(query).observe(OnboardOneActivity.this, new Observer<ActivationCodeResponse>() {
                        @Override
                        public void onChanged(ActivationCodeResponse activationCodeResponse) {
                            if (activationCodeResponse != null && activationCodeResponse.getCode() == HttpURLConnection.HTTP_OK) {
                                String activation_code = activationCodeResponse.getData().getActivationCode();
                                boolean skipPin = activationCodeResponse.getData().isHasSupervisorPassword();
                                verify_account(mid, activation_code, skipPin);
                            } else {
                                requestAccessLinkBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        skipBtn = findViewById(R.id.skip_btn);
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OnboardOneActivity.this, R.style.AlertDialogTheme);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle("Exit App !!!");
                alertDialog.setMessage("Are you sure you want to close this app ?");
                alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.exit(0);
                    }
                  }).setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        setupBtn = findViewById(R.id.setup_btn);
        setupBtn.setOnClickListener(this);
        SharedHelper.putBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setup_btn:
//                Intent intent = new Intent(OnboardOneActivity.this, OnboardTwoActivity.class);
//                intent.putExtra("device_serial",deviceSerial);
//                startActivity(intent);
//                finish();

                dialog.show();
                requestAccessLinkBtn.setVisibility(View.VISIBLE);
                merchantIdEt.setError("Field Required");
                merchantIdEt.requestFocus();
                break;
        }
    }

    private void verify_account(String mid, String code, boolean skipPin) {
        HashMap<String, String> query = new HashMap<>();
        query.put("mid", mid);
        query.put("serial_no", deviceSerial);
        query.put("activation_code", code);
        viewModel.verifyAccountData(query).observe(OnboardOneActivity.this, new Observer<AccountVerificationResponse>() {
            @Override
            public void onChanged(AccountVerificationResponse accountVerificationResponse) {
                requestAccessLinkBtn.setVisibility(View.VISIBLE);
                if (accountVerificationResponse != null && accountVerificationResponse.getCode() == HttpURLConnection.HTTP_OK) {
                    String token = accountVerificationResponse.getData().getAccessToken();
                    SharedHelper.putToken(OnboardOneActivity.this, token);
                    if (skipPin) {
                        activate_terminal(token, mid, deviceSerial, code);
                    } else {
                        dialog.dismiss();
                        Intent intent = new Intent(OnboardOneActivity.this, SetSupervisorPinActivity.class);
                        intent.putExtra("mid", merchantIdEt.getText().toString());
                        intent.putExtra("serial_no", deviceSerial);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void activate_terminal(String token, String mid, String deviceSerial, String code) {
        viewModel.requestTerminalActivation(token, mid, deviceSerial, code).observe(OnboardOneActivity.this, new Observer<ActivationRequestResponse>() {
            @Override
            public void onChanged(ActivationRequestResponse activationRequestResponse) {
                dialog.dismiss();
                if (activationRequestResponse != null && activationRequestResponse.getCode() == HttpURLConnection.HTTP_OK) {
                    SharedHelper.putDeviceStatus(OnboardOneActivity.this, AppConstants.SharedPref.DEVICE_STATUS, true);
                    startActivity(new Intent(OnboardOneActivity.this, HomeActivity.class));
                }
            }
        });
    }
}