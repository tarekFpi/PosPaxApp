package com.paymentsave.paymentsave.coreapp.fragments.settings;

import static androidx.core.app.ActivityCompat.finishAffinity;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_XREPORT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_ZREPORT;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_RECONCILIATION;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.eft.libpositive.PosIntegrate;
import com.pax.dal.entity.ENavigationKey;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.test.system.SysTester;
import com.paymentsave.paymentsave.weblink.WebLinkIntegrate;

import java.util.HashMap;

public class Setting2Fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Setting2Fragment";
    RelativeLayout exitAppBtn, wifiSettingBtn, cellularSettingBtn, powerSettingBtn, apnsSettingBtn, displaySettingBtn;
    RelativeLayout zReportBtn, xReportBtn, reversalBtn, cardNotPresentBtn,completionBtn, appSettingBtn;
    LinearLayout adminMenuBtn;

    public Setting2Fragment() {
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
        View view = inflater.inflate(R.layout.fragment_setting2, container, false);
        adminMenuBtn = view.findViewById(R.id.admin_menu_btn);
        zReportBtn = view.findViewById(R.id.z_report_btn);
        xReportBtn = view.findViewById(R.id.x_report_btn);
        completionBtn = view.findViewById(R.id.mm_completion_btn);

        reversalBtn = view.findViewById(R.id.reversal_btn);
        cardNotPresentBtn = view.findViewById(R.id.card_not_present_btn);
        appSettingBtn = view.findViewById(R.id.app_setting_btn);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_settingFragment_to_mainMenuFragment);
//                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_settingFragment_to_adminMenuFragment);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_settingFragment_to_adminPinFragment);
//                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_settingFragment_to_printStrFragment);
            }
        });
        zReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WebLinkIntegrate.enabled) {
                    Toast.makeText(requireContext(), "Not Supported in WebLink mode", Toast.LENGTH_SHORT).show();
                }
                HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args.put(CT_ZREPORT, "true");
                PosIntegrate.executeReport(requireContext(), TRANSACTION_TYPE_RECONCILIATION, args);
            }
        });
        xReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WebLinkIntegrate.enabled) {
                    Toast.makeText(requireContext(), "Not Supported in WebLink mode", Toast.LENGTH_SHORT).show();
                }
                HashMap<PosIntegrate.CONFIG_TYPE, String> args2 = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args2.put(CT_XREPORT, "true");
//                args2.put(CT_DISABLEPRINTING, "true");
                PosIntegrate.executeReport(requireContext(), TRANSACTION_TYPE_RECONCILIATION, args2);
            }
        });
        reversalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double final_amt = 100.00;
                HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                args.put(CT_AMOUNT, String.valueOf(final_amt * 100).split("\\.")[0]);
                args.put(CT_LANGUAGE, "en_GB");
//                args.put(CT_UTI, MainActivity.lastReceivedUTI);
                PosIntegrate.executeReversal(getActivity(), args);
            }
        });
        cardNotPresentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        completionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_settingFragment_to_completionFragment);
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
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivity(intent);
            }
        });
        appSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
            }
        });
    }

    @Override
    public void onClick(View v) {
    }

    private void enableBackKey(){
        SysTester.getInstance().showNavigationBar(true);
        SysTester.getInstance().enableNavigationBar(true);
        ENavigationKey eNavigationKey = ENavigationKey.BACK;
        SysTester.getInstance().enableNavigationKey(eNavigationKey, true);

    }

    public static void makeZReport(Context context){
        HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
        args.put(CT_ZREPORT, "true");
        PosIntegrate.executeReport(context, TRANSACTION_TYPE_RECONCILIATION, args);
    }

    private void setFragment(Fragment fragment) {
//        FragmentManager transaction = getActivity().getSupportFragmentManager();
//        transaction.beginTransaction()
//                .replace(R.id.settings_frame_layout, fragment) //<---replace a view in your layout (id: container) with the newFragment
//                .commit();
    }
}