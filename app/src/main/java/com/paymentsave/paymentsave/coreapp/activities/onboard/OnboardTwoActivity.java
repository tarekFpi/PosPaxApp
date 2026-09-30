package com.paymentsave.paymentsave.coreapp.activities.onboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paymentsave.paymentsave.R;

public class OnboardTwoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OnboardTwoActivity";

    private Button activateBtn, resendEmailBtn, textLinkBtn;


    private OnboardViewModel viewModel;
    private String deviceSerial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard_two);
        viewModel = ViewModelProviders.of(this).get(OnboardViewModel.class);
        if (getIntent()!= null){
            deviceSerial = getIntent().getStringExtra("device_serial");
        }
        textLinkBtn = findViewById(R.id.text_link_btn);
        textLinkBtn.setOnClickListener(this);
        resendEmailBtn = findViewById(R.id.resend_email_btn);
        activateBtn = findViewById(R.id.activate_btn);
        resendEmailBtn.setOnClickListener(this);

        activateBtn.setOnClickListener(this);




    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activate_btn:
//                dialog.show();
//                requestAccessLinkBtn.setVisibility(View.VISIBLE);
//                merchantIdEt.setError("Field Required");
//                merchantIdEt.requestFocus();
                break;
            case R.id.resend_email_btn:
            case R.id.text_link_btn:
                Toast.makeText(this, "Features will be available soon", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}