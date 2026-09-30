package com.paymentsave.paymentsave.coreapp.fragments.settings.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.network.SharedHelper;

public class MainMenuFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton backBtn;
    SwitchCompat preAuthBtn, gratuityEnableBtn;
    private RelativeLayout completionBtn;

    public MainMenuFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        completionBtn = view.findViewById(R.id.mm_completion_btn);
        backBtn = view.findViewById(R.id.mm_back_button);
        preAuthBtn = view.findViewById(R.id.pre_auth_btn);
        gratuityEnableBtn = view.findViewById(R.id.gratuity_enable_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backBtn.setOnClickListener(this);
        preAuthBtn.setOnClickListener(this);
        gratuityEnableBtn.setOnClickListener(this);
        completionBtn.setOnClickListener(this);
        boolean authValue = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
        if (authValue) {
            preAuthBtn.setChecked(true);
        }

        boolean gratuityValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
        if (gratuityValue) {
            gratuityEnableBtn.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mm_back_button:
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
            case R.id.mm_completion_btn:
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.action_mainMenuFragment_to_completionFragment);
                break;
        }
    }
}