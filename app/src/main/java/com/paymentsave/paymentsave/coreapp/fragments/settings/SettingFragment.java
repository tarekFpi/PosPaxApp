package com.paymentsave.paymentsave.coreapp.fragments.settings;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_XREPORT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_ZREPORT;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_RECONCILIATION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eft.libpositive.PosIntegrate;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pax.dal.entity.ENavigationKey;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.CounterPay.CounterPayActivity;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.activities.test.system.SysTester;
import com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.verifySupervisorPin.VerifyPinViewModel;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.coreapp.utils.TransparentProgressDialog;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.network.WebSocketUtils;
import com.paymentsave.paymentsave.responses.PinVerifyResponse.PinVerifyResponse;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;
import com.paymentsave.paymentsave.weblink.WebLinkIntegrate;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SettingFragment";
    private int report_id;
    private LinearLayout mainMenuTab, adminMenuTab;
    private ImageView mainMenuIcon, adminMenuIcon, logoIv;
    private TextView mainMenuText, adminMenuText, settingMID, settingTID, settingSN, settingAppVersion;

    private RelativeLayout mmCompletionBtn, zReportBtn, xReportBtn;
    private RelativeLayout exitAppBtn, wifiSettingBtn, cellularSettingBtn, powerSettingBtn, apnsSettingBtn, displaySettingBtn, appSettingBtn, supportBtn, pblCreateBtn;
    private SwitchCompat integrationSwitchBtn, preAuthBtn, cashbackEnableBtn, gratuityEnableBtn, receiptEnableBtn, splitBillEnableBtn, customerPrintEnableBtn, merchantPrintEnableBtn;
    private LinearLayout mainMenuLayout, adminMenuLayout, printOptionLayout, linkPaymentLayout;
    private boolean isMainMenuSelected = true;
    private BottomSheetDialog supervisorPinDialog;

    private ImageButton pinVisibilityBtn;
    private ImageView pinLayoutBackBtn;
    private boolean visiblePinPass = false;
    private EditText pinPass1, pinPass2, pinPass3, pinPass4;
    private Button btn00, btn01, btn02, btn03, btn04, btn05, btn06, btn07, btn08, btn09, btnBackSpace;
    private VerifyPinViewModel viewModel;
    private TransparentProgressDialog pd;
    private CountDownTimer countDownTimer;
    private LinearLayout changeSupervisorPin, autoBatchLayoutBtn, systemInfoLayoutBtn, userManagerLayoutBtn, dccRatesLayoutBtn;
    private String actionType = "Admin Menu";
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

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        viewModel = ViewModelProviders.of(this).get(VerifyPinViewModel.class);
        mainMenuIcon = view.findViewById(R.id.main_menu_icon);
        mainMenuText = view.findViewById(R.id.main_menu_text);
        adminMenuIcon = view.findViewById(R.id.admin_menu_icon);
        adminMenuText = view.findViewById(R.id.admin_menu_text);
        settingMID = view.findViewById(R.id.setting_mid);
        settingTID = view.findViewById(R.id.setting_tid);
        settingSN = view.findViewById(R.id.setting_sn);
        settingAppVersion = view.findViewById(R.id.setting_app_version);

        mainMenuTab = view.findViewById(R.id.main_menu_tab);
        adminMenuTab = view.findViewById(R.id.admin_menu_tab);
        mainMenuTab.setOnClickListener(this);
        adminMenuTab.setOnClickListener(this);

        zReportBtn = view.findViewById(R.id.z_report_btn);
        xReportBtn = view.findViewById(R.id.x_report_btn);
        mmCompletionBtn = view.findViewById(R.id.mm_completion_btn);
        zReportBtn.setOnClickListener(this);
        xReportBtn.setOnClickListener(this);
        mmCompletionBtn.setOnClickListener(this);

        mainMenuLayout = view.findViewById(R.id.main_menu_layout);
        adminMenuLayout = view.findViewById(R.id.admin_menu_layout);
        printOptionLayout = view.findViewById(R.id.print_options_layout);
        linkPaymentLayout = view.findViewById(R.id.link_payment_layout);

        systemInfoLayoutBtn = view.findViewById(R.id.system_info_layout_btn);
        systemInfoLayoutBtn.setOnClickListener(this);
        changeSupervisorPin = view.findViewById(R.id.change_supervisor_pin);
        changeSupervisorPin.setOnClickListener(this);
        userManagerLayoutBtn = view.findViewById(R.id.user_manager_layout_btn);
        userManagerLayoutBtn.setOnClickListener(this);
        dccRatesLayoutBtn = view.findViewById(R.id.dcc_rate_layout_btn);
        dccRatesLayoutBtn.setOnClickListener(this);
        autoBatchLayoutBtn = view.findViewById(R.id.auto_batch_layout_btn);
        autoBatchLayoutBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logoIv = view.findViewById(R.id.setting_dev_logo);
        boolean enabledLinkPayment = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.LINK_PAYMENT_ENABLED);

        if (!enabledLinkPayment) {
            linkPaymentLayout.setVisibility(View.GONE);
        }
        settingMID.setText(String.format("MID : %s", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID)));
        settingTID.setText(String.format("TID : %s", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID)));
        settingSN.setText(String.format("S/N : %s", MainUtils.getDeviceSerial(requireContext())));
        settingAppVersion.setText(String.format("App Version : %s", String.format("v%s", BuildConfig.VERSION_NAME)));
        String logoSrc = SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.BUSINESS_LOGO);
        Glide.with(requireContext())
                .load(logoSrc)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(logoIv);

        supervisorPinDialog = MainUtils.getSupervisorBottomSheet(requireActivity(), requireContext());
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

        pblCreateBtn = view.findViewById(R.id.create_pbl_btn);
        pblCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_paymentLinksFragment);
            }
        });

        supportBtn = view.findViewById(R.id.support_btn);
        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_settingFragment_to_supportFragment);
            }
        });


        exitAppBtn = view.findViewById(R.id.exit_app_btn);
        exitAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogTheme);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle("Exit App !!!");
                alertDialog.setMessage("Are you sure you want to close this app ?");
                alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity(requireActivity());
                        System.exit(0);
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        displaySettingBtn = view.findViewById(R.id.display_setting_btn);
        displaySettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBackKey();
                Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(intent);
            }
        });
        apnsSettingBtn = view.findViewById(R.id.apns_setting_btn);
        apnsSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBackKey();
                Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
                startActivity(intent);
            }
        });
        powerSettingBtn = view.findViewById(R.id.power_setting_btn);
        powerSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBackKey();
                Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                ResolveInfo resolveInfo = getActivity().getPackageManager().resolveActivity(powerUsageIntent, 0);
                // check that the Battery app exists on this device
                if (resolveInfo != null) {
                    startActivity(powerUsageIntent);
                }
            }
        });
        wifiSettingBtn = view.findViewById(R.id.wifi_setting_btn);
        wifiSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBackKey();
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }
        });
        cellularSettingBtn = view.findViewById(R.id.cellular_setting_btn);
        cellularSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableBackKey();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.phone", "com.android.phone.MobileNetworkSettings");
                startActivity(intent);
            }
        });
        appSettingBtn = view.findViewById(R.id.app_setting_btn);
        appSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                enableBackKey();
//                Intent intent = new Intent(requireActivity(), AppSettingsActivity.class);
//                startActivity(intent);
            }
        });

        merchantPrintEnableBtn = view.findViewById(R.id.receipt_merchant_enable_btn);
        merchantPrintEnableBtn.setOnClickListener(this);
        customerPrintEnableBtn = view.findViewById(R.id.receipt_customer_enable_btn);
        customerPrintEnableBtn.setOnClickListener(this);
        splitBillEnableBtn = view.findViewById(R.id.split_bill_enable_btn);
        splitBillEnableBtn.setOnClickListener(this);
        receiptEnableBtn = view.findViewById(R.id.receipt_print_enable_btn);
        receiptEnableBtn.setOnClickListener(this);
        cashbackEnableBtn = view.findViewById(R.id.cashback_enable_btn);
        cashbackEnableBtn.setOnClickListener(this);
        gratuityEnableBtn = view.findViewById(R.id.gratuity_enable_btn);
        gratuityEnableBtn.setOnClickListener(this);
        preAuthBtn = view.findViewById(R.id.pre_auth_btn);
        preAuthBtn.setOnClickListener(this);
        integrationSwitchBtn = view.findViewById(R.id.interlink_enable_btn);
        integrationSwitchBtn.setOnClickListener(this);

        boolean isPre_authActivated = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
        if (isPre_authActivated) {
            mmCompletionBtn.setVisibility(View.VISIBLE);
        }
        boolean authValue = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
        if (authValue) {
            preAuthBtn.setChecked(true);
        }

        boolean integrationModeValue = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.INTEGRATION_MODE);
        if (integrationModeValue) {
            integrationSwitchBtn.setChecked(true);
        }

        boolean cashbackValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CASHBACK);
        if (cashbackValue) {
            cashbackEnableBtn.setChecked(true);
        }

        boolean gratuityValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
        if (gratuityValue) {
            gratuityEnableBtn.setChecked(true);
        }

        boolean printReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING);
        boolean customerPrintReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
        boolean merchantPrintReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);
        if (customerPrintReceiptValue) {
            customerPrintEnableBtn.setChecked(true);
        }
        if (merchantPrintReceiptValue) {
            merchantPrintEnableBtn.setChecked(true);
        }
        if (printReceiptValue) {
            receiptEnableBtn.setChecked(true);
            printOptionLayout.setVisibility(View.VISIBLE);
        } else {
            printOptionLayout.setVisibility(View.GONE);
        }


        boolean splitBillEnableValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.SPLIT_BILL);
        if (splitBillEnableValue) {
            splitBillEnableBtn.setChecked(true);
        }

        countDownTimer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                try {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }

                    if (HomeActivity.report_error_response != null) {
                        PrintUtils.openFailedDialog(requireContext(), HomeActivity.report_error_response);
                        HomeActivity.report_error_response = null;
                    } else {

                        if (HomeActivity.report_response != null) {
                            Report report = HomeActivity.generateReportObject(requireContext(), HomeActivity.report_response);
                            report.setStatus("SUBMITTED");
                            PrintUtils.openDialog(requireContext(), report);
                            HomeActivity.report_response = null;
//                        String token = SharedHelper.getToken(requireContext());
//                        HomeActivity.viewModel.updateReport(requireContext(), report, token, String.valueOf(report_id)).observe(requireActivity(), new Observer<ReportCreateResponse>() {
//                            @Override
//                            public void onChanged(ReportCreateResponse reportCreateResponse) {
//                                if (reportCreateResponse != null) {
//                                    PrintUtils.openDialog(requireContext(), report);
//                                }
//                            }
//                        });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    /// Telemetry: SettingFragment.onViewCreated.countDownTimer — failed to show report result dialog.
                    logReceiveError("onViewCreated.countDownTimer", String.valueOf(e.getMessage()), e);
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMainMenuSelected) {
            mainMenuTab.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.left_active_toggle_tab));
            mainMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.white));
            mainMenuText.setTextColor(requireContext().getResources().getColor(R.color.white));

            adminMenuTab.setBackground(null);
            adminMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.black));
            adminMenuText.setTextColor(requireContext().getResources().getColor(R.color.black));
            boolean isPre_authActivated = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
            if (isPre_authActivated) {
                mmCompletionBtn.setVisibility(View.VISIBLE);
            } else {
                mmCompletionBtn.setVisibility(View.GONE);
            }
            mainMenuLayout.setVisibility(View.VISIBLE);
            adminMenuLayout.setVisibility(View.GONE);
        } else {
            adminMenuTab.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.right_active_toggle_tab));
            adminMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.white));
            adminMenuText.setTextColor(requireContext().getResources().getColor(R.color.white));

            mainMenuTab.setBackground(null);
            mainMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.black));
            mainMenuText.setTextColor(requireContext().getResources().getColor(R.color.black));
            boolean authValue = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
            if (authValue) {
                preAuthBtn.setChecked(true);
            }

            boolean gratuityValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
            if (gratuityValue) {
                gratuityEnableBtn.setChecked(true);
            }

            boolean printReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING);
            boolean customerPrintReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
            boolean merchantPrintReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);
            if (customerPrintReceiptValue) {
                customerPrintEnableBtn.setChecked(true);
            }
            if (merchantPrintReceiptValue) {
                merchantPrintEnableBtn.setChecked(true);
            }
            if (printReceiptValue) {
                receiptEnableBtn.setChecked(true);
                printOptionLayout.setVisibility(View.VISIBLE);
            } else {
                printOptionLayout.setVisibility(View.GONE);
            }
            mainMenuLayout.setVisibility(View.GONE);
            adminMenuLayout.setVisibility(View.VISIBLE);
        }
         countDownTimer.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.system_info_layout_btn:
                PrintUtils.printSystemInfo(requireContext());
                break;
            case R.id.auto_batch_layout_btn:
                Navigation.findNavController(v).navigate(R.id.action_settingFragment_to_autoBatchSettingFragment);
                break;
            case R.id.dcc_rate_layout_btn:
            case R.id.user_manager_layout_btn:
                Toasty.info(requireContext(), "Features coming soon", Toasty.LENGTH_SHORT).show();
                break;
            case R.id.change_supervisor_pin:
                Navigation.findNavController(v).navigate(R.id.action_settingFragment_to_supervisorPinChangeFragment);
//                HashMap<String, String> map = new HashMap<>();
//                map.put("mid",SharedHelper.getStringData(requireContext(),AppConstants.SharedPref.MID));
//                map.put("tid",SharedHelper.getStringData(requireContext(),AppConstants.SharedPref.TID));
//                String token = SharedHelper.getToken(requireContext());

//                viewModel.changeSupervisorPin(token, map).observe(this, new Observer<Object>() {
//                    @Override
//                    public void onChanged(Object o) {
//                        if (o != null) {
//
//                        }
//                    }
//                });

                break;
            case R.id.main_menu_tab:
                isMainMenuSelected = true;
                mainMenuTab.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.left_active_toggle_tab));
                mainMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.white));
                mainMenuText.setTextColor(requireContext().getResources().getColor(R.color.white));

                adminMenuTab.setBackground(null);
                adminMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.black));
                adminMenuText.setTextColor(requireContext().getResources().getColor(R.color.black));
                boolean isPre_authActivated = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
                if (isPre_authActivated) {
                    mmCompletionBtn.setVisibility(View.VISIBLE);
                } else {
                    mmCompletionBtn.setVisibility(View.GONE);
                }
                mainMenuLayout.setVisibility(View.VISIBLE);
                adminMenuLayout.setVisibility(View.GONE);
                break;
            case R.id.admin_menu_tab:
                if (isMainMenuSelected) {
                    actionType = "Admin Menu";
                    supervisorPinDialog.show();
                    visiblePinPass = false;
                }
                break;
            case R.id.z_report_btn:
                actionType = "ZReport";
                supervisorPinDialog.show();
                visiblePinPass = false;
                break;
            case R.id.x_report_btn:
                actionType = "XReport";
                supervisorPinDialog.show();
                visiblePinPass = false;
                break;
            case R.id.mm_completion_btn:
                Navigation.findNavController(v).navigate(R.id.action_settingFragment_to_completionFragment);
                break;
            case R.id.pre_auth_btn:
                boolean authValue2 = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.PRE_AUTH, !authValue2);
                break;
            case R.id.interlink_enable_btn:
                boolean integrationValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.INTEGRATION_MODE);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.INTEGRATION_MODE, !integrationValue);
                if (!integrationValue) {
                    showLoadingScreen();

                    startActivity(new Intent(requireContext(), CounterPayActivity.class));
                    try {
                        pd.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: SettingFragment.onClick.interlink_enable_btn — failed to dismiss loading dialog.
                        logReceiveError("onClick.interlink_enable_btn", String.valueOf(e.getMessage()), e);
                    }

                    requireActivity().finish();

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        WebSocketUtils.connectWebSocket(requireContext()).thenAccept(isConnected -> {
//                            pd.dismiss();
//                            if (isConnected) {
//                                startActivity(new Intent(requireContext(), CounterPayActivity.class));
//                                requireActivity().finish();
//                            } else {
//                                showThreadWarningToast("Integration mode failed. Please check your internet connection and try again.");
//                            }
//                        }).exceptionally(e -> {
//                            pd.dismiss();
//                            // Log.e(TAG, "Integration mode connection error: " + e.getMessage());
//                            showThreadWarningToast("Integration mode failed with exception. Please try again later.");
//                            return null;
//                        });
//                    }
                } else {
                    try {
                        WebSocketUtils.closeConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: SettingFragment.onClick.interlink_enable_btn — failed to close websocket.
                        logReceiveError("onClick.interlink_enable_btn", String.valueOf(e.getMessage()), e);
                    }
                }
                break;
            case R.id.cashback_enable_btn:
                boolean cashbackValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CASHBACK);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.CASHBACK, !cashbackValue);
                break;
            case R.id.gratuity_enable_btn:
                boolean gratuityValue2 = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY, !gratuityValue2);
                break;
            case R.id.receipt_print_enable_btn:
                boolean receiptEnableValue2 = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING, !receiptEnableValue2);
                if (!receiptEnableValue2) {
                    printOptionLayout.setVisibility(View.VISIBLE);
                    boolean cr = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
                    boolean merchantPrintReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);
                    if (cr) {
                        customerPrintEnableBtn.setChecked(true);
                    }
                    if (merchantPrintReceiptValue) {
                        merchantPrintEnableBtn.setChecked(true);
                    }
                } else {
                    SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING, false);
                    SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING, false);
                    customerPrintEnableBtn.setChecked(false);
                    merchantPrintEnableBtn.setChecked(false);
                    printOptionLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.receipt_customer_enable_btn:
                boolean customerReceiptEnableValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING, !customerReceiptEnableValue);
                break;
            case R.id.receipt_merchant_enable_btn:
                boolean merchantReceiptEnableValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING, !merchantReceiptEnableValue);
                break;
            case R.id.split_bill_enable_btn:
                boolean splitBillEnableValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.SPLIT_BILL);
                SharedHelper.putBooleanData(requireContext(), AppConstants.SharedPref.SPLIT_BILL, !splitBillEnableValue);
                break;
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
                    // Telemetry: SettingFragment.onClick.backButton — failed to dismiss PIN dialog.
                    logReceiveError("onClick.backButton", String.valueOf(e.getMessage()), e);
                }
                break;
        }
    }

    private void showThreadWarningToast(String msg) {
        requireActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toasty.info(requireContext(), msg, Toasty.LENGTH_LONG).show();
            }
        });
    }


    private void enableBackKey() {
        SysTester.getInstance().showNavigationBar(true);
        SysTester.getInstance().enableNavigationBar(true);
        ENavigationKey eNavigationKey = ENavigationKey.BACK;
        SysTester.getInstance().enableNavigationKey(eNavigationKey, true);
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

    private void verifySupervisorPin() {
        showLoadingScreen();
        String pin = pinPass1.getText().toString() + pinPass2.getText().toString() + pinPass3.getText().toString() + pinPass4.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("mid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID));
        map.put("serial_no", MainUtils.getDeviceSerial(requireContext()));
        map.put("s_pin", pin);
        viewModel.verifySupervisorPin(map).observe(requireActivity(), new Observer<PinVerifyResponse>() {
            @Override
            public void onChanged(PinVerifyResponse pinVerifyResponse) {
                if (pinVerifyResponse != null && pinVerifyResponse.getCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        pd.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: SettingFragment.verifySupervisorPin.onChanged — failed to dismiss loading dialog after PIN success.
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
                        // Telemetry: SettingFragment.verifySupervisorPin.onChanged — failed to dismiss PIN dialog after success.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }
                    switch (actionType) {
                        case "Admin Menu":
                            Toast.makeText(requireContext(), "Successfully Admin Verified", Toast.LENGTH_SHORT).show();
                            isMainMenuSelected = false;
                            adminMenuTab.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.right_active_toggle_tab));
                            adminMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.white));
                            adminMenuText.setTextColor(requireContext().getResources().getColor(R.color.white));

                            mainMenuTab.setBackground(null);
                            mainMenuIcon.setColorFilter(requireContext().getResources().getColor(R.color.black));
                            mainMenuText.setTextColor(requireContext().getResources().getColor(R.color.black));
                            boolean authValue = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
                            if (authValue) {
                                preAuthBtn.setChecked(true);
                            }

                            boolean gratuityValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
                            if (gratuityValue) {
                                gratuityEnableBtn.setChecked(true);
                            }

                            boolean printReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING);
                            boolean customerPrintReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CUSTOMER_RECEIPT_PRINTING);
                            boolean merchantPrintReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.MERCHANT_RECEIPT_PRINTING);
                            if (customerPrintReceiptValue) {
                                customerPrintEnableBtn.setChecked(true);
                            }
                            if (merchantPrintReceiptValue) {
                                merchantPrintEnableBtn.setChecked(true);
                            }
                            if (printReceiptValue) {
                                receiptEnableBtn.setChecked(true);
                                printOptionLayout.setVisibility(View.VISIBLE);
                            } else {
                                printOptionLayout.setVisibility(View.GONE);
                            }
                            mainMenuLayout.setVisibility(View.GONE);
                            adminMenuLayout.setVisibility(View.VISIBLE);
                            break;
                        case "ZReport":
                            if (WebLinkIntegrate.enabled) {
                                Toast.makeText(requireContext(), "Not Supported in WebLink mode", Toast.LENGTH_SHORT).show();
                            }
                            createBatchReport("ZReport");
                            break;
                        case "XReport":
                            if (WebLinkIntegrate.enabled) {
                                Toast.makeText(requireContext(), "Not Supported in WebLink mode", Toast.LENGTH_SHORT).show();
                            }
                            createBatchReport("XReport");
                            break;
                    }
                } else {
                    try {
                        pd.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: SettingFragment.verifySupervisorPin.onChanged — failed to dismiss loading dialog after PIN failure.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }
                    resetPin();
                }
            }
        });
    }

    private void resetPin() {
        pinPass1.setText("");
        pinPass2.setText("");
        pinPass3.setText("");
        pinPass4.setText("");
    }

    private void showLoadingScreen() {
        try {
            pd = TransparentProgressDialog.getInstance(requireContext());
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: SettingFragment.showLoadingScreen — failed to show loading dialog.
            logReceiveError("showLoadingScreen", String.valueOf(e.getMessage()), e);
        }
    }

    private void createBatchReport(String reportType) {
        showLoadingScreen();
        try {
            if (reportType.equals("ZReport")) {
                HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args.put(CT_ZREPORT, "true");
                PosIntegrate.executeReport(requireContext(), TRANSACTION_TYPE_RECONCILIATION, args);
            } else {
                HashMap<PosIntegrate.CONFIG_TYPE, String> args2 = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args2.put(CT_XREPORT, "true");
                PosIntegrate.executeReport(requireContext(), TRANSACTION_TYPE_RECONCILIATION, args2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: SettingFragment.createBatchReport — X/Z report failed to start.
            logReceiveError("createBatchReport", String.valueOf(e.getMessage()), e);
        }
//        String token = SharedHelper.getToken(requireContext());
//        Report report = new Report();
//        report.setIsReportResponse(true);
//        report.setReportType(reportType);
//        report.setMerchant(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.USER_ID));
//        report.setBusiness(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.BUSINESS_ID));
//        report.setTid(SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
//        HomeActivity.viewModel.createReport(requireContext(), report, token).observe(requireActivity(), new Observer<ReportCreateResponse>() {
//            @Override
//            public void onChanged(ReportCreateResponse reportCreateResponse) {
//                if (reportCreateResponse != null) {
//                    report_id = reportCreateResponse.getData().getId();
//                    if (reportType.equals("ZReport")) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                countDownTimer.start();
//                            }
//                        }, 2000);
//                        HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
//                        args.put(CT_ZREPORT, "true");
//                        PosIntegrate.executeReport(requireContext(), TRANSACTION_TYPE_RECONCILIATION, args);
//                    }
//                    else {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                countDownTimer.start();
//                            }
//                        }, 2000);
//                        HashMap<PosIntegrate.CONFIG_TYPE, String> args2 = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
//                        args2.put(CT_XREPORT, "true");
//                        PosIntegrate.executeReport(requireContext(), TRANSACTION_TYPE_RECONCILIATION, args2);
//                    }
//                } else {
//                    try {
//                        pd.dismiss();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    PrintUtils.openFailedDialog(requireContext());
//                }
//            }
//        });
//
    }

    /**
     * Sends a fragment failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code SettingFragment.createBatchReport}.
     * {@code message} must already be a string.
     */
    private void logReceiveError(String functionPath, String message, Throwable throwable) {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, message != null ? message : "", throwable);
            return;
        }

        String eventName = TAG + "." + functionPath;
        TelemetryLogger.error(
                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                eventName,
                message != null ? message : "",
                throwable
        );
    }
}