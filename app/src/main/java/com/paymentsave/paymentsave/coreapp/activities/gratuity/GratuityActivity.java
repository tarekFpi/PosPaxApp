package com.paymentsave.paymentsave.coreapp.activities.gratuity;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_CASHBACK;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_GRATUITY;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_PREAUTH;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_SALE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.eft.libpositive.PosIntegrate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.models.GratuityOption;
import com.paymentsave.paymentsave.network.SharedHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GratuityActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GratuityActivity";
    List<GratuityOption> options = new ArrayList<>();
    private int trans_Type = 0;
    private float mainAmount = 0.0f;
    private String mainAmt = "";
    private String cashbackAmt = "";
    private FloatingActionButton backBtn;
    private Button backBtn2;
    private LinearLayout customGratuityBtn, noThanksBtn;
    private ListView gratuityListView;
    private List<Object> gratuityOptions = new ArrayList<>();
    private GratuityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gratuity);
        if (getIntent().getExtras() != null) {
            cashbackAmt = getIntent().getStringExtra("cashback_amt");
            mainAmt = getIntent().getStringExtra("main_amt");
            mainAmount = Float.parseFloat(getIntent().getStringExtra("main_amt"));
            trans_Type = getIntent().getIntExtra("trans_type", 0);
        }
        customGratuityBtn = findViewById(R.id.custom_gratuity_btn_lv);
        noThanksBtn = findViewById(R.id.no_thanks_btn);
        gratuityListView = findViewById(R.id.gratuity_list_view);
        backBtn = findViewById(R.id.gratuity_back_button);
        backBtn2 = findViewById(R.id.gratuity_back_button2);

        backBtn.setOnClickListener(this);
        backBtn2.setOnClickListener(this);
        customGratuityBtn.setOnClickListener(this);
        noThanksBtn.setOnClickListener(this);

        String gratuityType = SharedHelper.getStringData(this, AppConstants.SharedPref.GRATUITY_TYPE);
        String gratuityOptionString = SharedHelper.getStringData(this, AppConstants.SharedPref.GRATUITY_OPTIONS);
        if (gratuityOptionString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Object>>() {
            }.getType();
            gratuityOptions = gson.fromJson(gratuityOptionString, type);
            for (Object item :
                    gratuityOptions) {
                options.add(new GratuityOption(gratuityType, item.toString(), mainAmount));
            }
        }

        adapter = new GratuityAdapter(this, options);
        gratuityListView.setAdapter(adapter);
        adapter.setGratuityItemClickListener(new GratuityAdapter.OnGratuityItemClickListener() {
            @Override
            public void onProgressItemClickListener(int position, GratuityOption item) {
                if (item.getLabel().equals("PERCENTAGE")) {
                    int perc = (int) Float.parseFloat(item.getAmount());
                    Float floatFinal = Float.parseFloat(String.valueOf(item.getMainAmount() * perc / 100));
                    String finalValue = String.format("%.02f", floatFinal);
                    runSaleWithGratuity(finalValue);
                } else {
                    runSaleWithGratuity(item.getAmount());
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gratuity_back_button:
            case R.id.gratuity_back_button2:
                getOnBackPressedDispatcher().onBackPressed();
                break;
            case R.id.no_thanks_btn:
                boolean printReceiptValue = SharedHelper.getBooleanData(GratuityActivity.this, AppConstants.SharedPref.RECEIPT_PRINTING);
                HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
                args.put(CT_AMOUNT_CASHBACK, cashbackAmt);
                // args.put(CT_DISABLEPRINTING, String.valueOf(!printReceiptValue));
                args.put(CT_DISABLEPRINTING, "true");
                args.put(CT_LANGUAGE, "en_GB");
                if (trans_Type == 0)
                    PosIntegrate.executeTransaction(GratuityActivity.this, TRANSACTION_TYPE_SALE, args);
                else
                    PosIntegrate.executeTransaction(GratuityActivity.this, TRANSACTION_TYPE_PREAUTH, args);
                finish();
                break;
            case R.id.custom_gratuity_btn_lv:
                Intent intent = new Intent(GratuityActivity.this, GratuityCustomActivity.class);
                intent.putExtra("main_amt", mainAmt);
                intent.putExtra("cashback_amt", cashbackAmt);
                intent.putExtra("trans_type", trans_Type);
                startActivity(intent);
                break;
        }
    }

    private void runSaleWithGratuity(String gratuityAmt) {
        boolean printReceiptValue = SharedHelper.getBooleanData(GratuityActivity.this, AppConstants.SharedPref.RECEIPT_PRINTING);
        HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
        args.put(CT_AMOUNT, mainAmt.replace(".", "").trim());
        args.put(CT_AMOUNT_GRATUITY, String.valueOf(Double.parseDouble(gratuityAmt) * 100).split("\\.")[0]);
        args.put(CT_AMOUNT_CASHBACK, cashbackAmt);
        // args.put(CT_DISABLEPRINTING, String.valueOf(!printReceiptValue));
        args.put(CT_DISABLEPRINTING, "true");
        args.put(CT_LANGUAGE, "en_GB");
        if (trans_Type == 0)
            PosIntegrate.executeTransaction(GratuityActivity.this, TRANSACTION_TYPE_SALE, args);
        else
            PosIntegrate.executeTransaction(GratuityActivity.this, TRANSACTION_TYPE_PREAUTH, args);
        finish();
    }
}