package com.paymentsave.paymentsave.coreapp.activities.cashback;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_CASHBACK;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_PREAUTH;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_SALE;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.eft.libpositive.PosIntegrate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.gratuity.GratuityActivity;
import com.paymentsave.paymentsave.coreapp.models.CashbackOption;
import com.paymentsave.paymentsave.network.SharedHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CashbackActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "GratuityActivity";
    List<CashbackOption> options = new ArrayList<>();
    private int trans_Type = 0;
    private float mainAmount = 0.0f;
    private String mainAmt = "";
    private int cashbackAmt = 0;
    private TextView mainAmtTxt;
    private Button noThanksBtn, execTenBtn, execFifteenBtn, execTwentyBtn, execThirtyBtn, cashbackCustomBtn;
    private FloatingActionButton backBtn;
    private ListView cashbackListView;
    private boolean gratuityEnabled = false;
    private List<Object> cashbackOptions = new ArrayList<>();
    private CashbackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashback);
        if (getIntent().getExtras() != null) {
            mainAmt = getIntent().getStringExtra("main_amt");
            mainAmount = Float.parseFloat(getIntent().getStringExtra("main_amt"));
            trans_Type = getIntent().getIntExtra("trans_type", 0);
        }

        mainAmtTxt = findViewById(R.id.main_amt_txt);
        mainAmtTxt.setText(String.format("£%s", mainAmt));

        cashbackListView = findViewById(R.id.cashback_lv);
        backBtn = findViewById(R.id.cashback_back_button);
        noThanksBtn = findViewById(R.id.no_thanks_cashback_btn);
        execTenBtn = findViewById(R.id.exec_ten_btn);
        execFifteenBtn = findViewById(R.id.exec_fifteen_btn);
        execTwentyBtn = findViewById(R.id.exec_twenty_btn);
        execThirtyBtn = findViewById(R.id.exec_thirty_btn);
        cashbackCustomBtn = findViewById(R.id.cashback_custom_btn);

        backBtn.setOnClickListener(this);
        noThanksBtn.setOnClickListener(this);
        execTenBtn.setOnClickListener(this);
        execFifteenBtn.setOnClickListener(this);
        execTwentyBtn.setOnClickListener(this);
        execThirtyBtn.setOnClickListener(this);
        cashbackCustomBtn.setOnClickListener(this);

        gratuityEnabled = SharedHelper.getBooleanData(this, AppConstants.SharedPref.GRATUITY);
        String cashbackOptionString = SharedHelper.getStringData(this, AppConstants.SharedPref.CASHBACK_OPTIONS);
        if (cashbackOptionString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Object>>() {
            }.getType();
            cashbackOptions = gson.fromJson(cashbackOptionString, type);
            for (Object item :
                    cashbackOptions) {
                options.add(new CashbackOption("£", item.toString()));
            }
        }

        adapter = new CashbackAdapter(this, options);
        cashbackListView.setAdapter(adapter);
        adapter.setCashbackItemClickListener(new CashbackAdapter.OnCashbackItemClickListener() {
            @Override
            public void onProgressItemClickListener(int position, CashbackOption item) {
                String cashback = String.format("%.02f", Float.parseFloat(item.getAmount()));
                Log.e(TAG, "onProgressItemClickListener: "+cashback );
                if (gratuityEnabled) {
                    Intent intent = new Intent(CashbackActivity.this, GratuityActivity.class);
                    intent.putExtra("main_amt", mainAmt);
                    intent.putExtra("cashback_amt", cashback.replace(".", "").trim());
                    intent.putExtra("trans_type", trans_Type);
                    startActivity(intent);
                } else {
                    boolean printReceiptValue = SharedHelper.getBooleanData(CashbackActivity.this, AppConstants.SharedPref.RECEIPT_PRINTING);
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                    args.put(CT_AMOUNT_CASHBACK, cashback.replace(".", "").trim());
//                    args.put(CT_DISABLEPRINTING, String.valueOf(printReceiptValue));
                    args.put(CT_DISABLEPRINTING, "true");
                    args.put(CT_LANGUAGE, "en_GB");
                    if (trans_Type == 0)
                        PosIntegrate.executeTransaction(CashbackActivity.this, TRANSACTION_TYPE_SALE, args);
                    else
                        PosIntegrate.executeTransaction(CashbackActivity.this, TRANSACTION_TYPE_PREAUTH, args);
                    finish();
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.cashback_back_button:
                finish();
                break;
            case R.id.no_thanks_cashback_btn:
                if (gratuityEnabled) {
                    Intent intent = new Intent(this, GratuityActivity.class);
                    intent.putExtra("main_amt", mainAmt);
                    intent.putExtra("cashback_amt", String.valueOf(0));
                    intent.putExtra("trans_type", trans_Type);
                    startActivity(intent);
                } else {
                    boolean printReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING);
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                    args.put(CT_AMOUNT_CASHBACK, String.valueOf(0));
//                    args.put(CT_DISABLEPRINTING, String.valueOf(printReceiptValue));
                    args.put(CT_DISABLEPRINTING, "true");
                    args.put(CT_LANGUAGE, "en_GB");
                    if (trans_Type == 0)
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_SALE, args);
                    else
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_PREAUTH, args);
                    finish();
                }
                break;
            case R.id.exec_ten_btn:
                cashbackAmt = 1000;
                if (gratuityEnabled) {
                    Intent intent = new Intent(this, GratuityActivity.class);
                    intent.putExtra("main_amt", mainAmt);
                    intent.putExtra("cashback_amt", String.valueOf(cashbackAmt));
                    intent.putExtra("trans_type", trans_Type);
                    startActivity(intent);
                } else {
                    boolean printReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING);
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                    args.put(CT_AMOUNT_CASHBACK, String.valueOf(cashbackAmt));
//                    args.put(CT_DISABLEPRINTING, String.valueOf(printReceiptValue));
                    args.put(CT_DISABLEPRINTING, "true");
                    args.put(CT_LANGUAGE, "en_GB");
                    if (trans_Type == 0)
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_SALE, args);
                    else
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_PREAUTH, args);
                    finish();
                }
                break;
            case R.id.exec_fifteen_btn:
                cashbackAmt = 1500;
                if (gratuityEnabled) {
                    Intent intent = new Intent(this, GratuityActivity.class);
                    intent.putExtra("main_amt", mainAmt);
                    intent.putExtra("cashback_amt", String.valueOf(cashbackAmt));
                    intent.putExtra("trans_type", trans_Type);
                    startActivity(intent);
                } else {
                    boolean printReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING);
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                    args.put(CT_AMOUNT_CASHBACK, String.valueOf(cashbackAmt));
//                    args.put(CT_DISABLEPRINTING, String.valueOf(printReceiptValue));
                    args.put(CT_DISABLEPRINTING, "true");
                    args.put(CT_LANGUAGE, "en_GB");
                    if (trans_Type == 0)
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_SALE, args);
                    else
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_PREAUTH, args);
                    finish();
                }
                break;
            case R.id.exec_twenty_btn:
                cashbackAmt = 2000;
                if (gratuityEnabled) {
                    Intent intent = new Intent(this, GratuityActivity.class);
                    intent.putExtra("main_amt", mainAmt);
                    intent.putExtra("cashback_amt", String.valueOf(cashbackAmt));
                    intent.putExtra("trans_type", trans_Type);
                    startActivity(intent);
                } else {
                    boolean printReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING);
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                    args.put(CT_AMOUNT_CASHBACK, String.valueOf(cashbackAmt));
//                    args.put(CT_DISABLEPRINTING, String.valueOf(printReceiptValue));
                    args.put(CT_DISABLEPRINTING, "true");
                    args.put(CT_LANGUAGE, "en_GB");
                    if (trans_Type == 0)
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_SALE, args);
                    else
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_PREAUTH, args);
                    finish();
                }
                break;
            case R.id.exec_thirty_btn:
                cashbackAmt = 3000;
                if (gratuityEnabled) {
                    Intent intent = new Intent(this, GratuityActivity.class);
                    intent.putExtra("main_amt", mainAmt);
                    intent.putExtra("cashback_amt", String.valueOf(cashbackAmt));
                    intent.putExtra("trans_type", trans_Type);
                    startActivity(intent);
                } else {
                    boolean printReceiptValue = SharedHelper.getBooleanData(this, AppConstants.SharedPref.RECEIPT_PRINTING);
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                    args.put(CT_AMOUNT_CASHBACK, String.valueOf(cashbackAmt));
//                    args.put(CT_DISABLEPRINTING, String.valueOf(printReceiptValue));
                    args.put(CT_DISABLEPRINTING, "true");
                    args.put(CT_LANGUAGE, "en_GB");
                    if (trans_Type == 0)
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_SALE, args);
                    else
                        PosIntegrate.executeTransaction(this, TRANSACTION_TYPE_PREAUTH, args);
                    finish();
                }
                break;
            case R.id.cashback_custom_btn:
                Intent intent = new Intent(CashbackActivity.this, CashbackCustomActivity.class);
                intent.putExtra("main_amt", mainAmt);
                intent.putExtra("cashback_amt", String.valueOf(cashbackAmt));
                intent.putExtra("trans_type", trans_Type);
                startActivity(intent);
                break;
        }
    }
}