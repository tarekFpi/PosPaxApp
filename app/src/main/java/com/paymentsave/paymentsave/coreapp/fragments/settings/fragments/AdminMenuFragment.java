package com.paymentsave.paymentsave.coreapp.fragments.settings.fragments;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_HISTORYREPORT;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_RECONCILIATION;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eft.libpositive.PosIntegrate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.weblink.WebLinkIntegrate;

import java.util.HashMap;


public class AdminMenuFragment extends Fragment implements View.OnClickListener {
    FloatingActionButton backBtn;
    SwitchCompat preAuthBtn, gratuityEnableBtn, receiptEnableBtn;
    LinearLayout historyReportBtn;
    private TextView settingMID, settingTID, settingSN, settingAppVersion;

    public AdminMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_menu, container, false);
        settingMID = view.findViewById(R.id.setting_mid);
        settingTID = view.findViewById(R.id.setting_tid);
        settingSN = view.findViewById(R.id.setting_sn);
        settingAppVersion = view.findViewById(R.id.setting_app_version);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        receiptEnableBtn = view.findViewById(R.id.receipt_print_enable_btn);
        receiptEnableBtn.setOnClickListener(this);
        historyReportBtn = view.findViewById(R.id.history_report);
        historyReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WebLinkIntegrate.enabled) {
                    Toast.makeText(requireContext(), "Not Supported in WebLink mode", Toast.LENGTH_SHORT).show();
                }
                HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args.put(CT_HISTORYREPORT, "true");
                PosIntegrate.executeReport(requireContext(), TRANSACTION_TYPE_RECONCILIATION, args);
            }
        });
        preAuthBtn = view.findViewById(R.id.pre_auth_btn);
        gratuityEnableBtn = view.findViewById(R.id.gratuity_enable_btn);
        backBtn = view.findViewById(R.id.admin_menu_back_button);
        backBtn.setOnClickListener(this);
        preAuthBtn.setOnClickListener(this);
        gratuityEnableBtn.setOnClickListener(this);
        boolean authValue = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
        if (authValue) {
            preAuthBtn.setChecked(true);
        }

        boolean gratuityValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
        if (gratuityValue) {
            gratuityEnableBtn.setChecked(true);
        }

        boolean printReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING);
        if (printReceiptValue) {
            receiptEnableBtn.setChecked(true);
        }

        settingMID.setText(String.format("MID : %s", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID)));
        settingTID.setText(String.format("TID : %s", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID)));
        settingSN.setText(String.format("S/N : %s", MainUtils.getDeviceSerial(requireContext())));
        settingAppVersion.setText(String.format("App Version : %s", String.format("v%s", BuildConfig.VERSION_NAME)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.admin_menu_back_button:
                Navigation.findNavController(view).popBackStack();
                break;
            case R.id.pre_auth_btn:
                boolean authValue = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.PRE_AUTH, !authValue);
                break;
            case R.id.gratuity_enable_btn:
                boolean gratuityValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY, !gratuityValue);
                break;
            case R.id.receipt_print_enable_btn:
                boolean receiptEnableValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING, !receiptEnableValue);
                break;
        }
    }
}