package com.paymentsave.paymentsave.coreapp.fragments.supervisorPinChange;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.verifySupervisorPin.VerifyPinViewModel;
import com.paymentsave.paymentsave.network.SharedHelper;

import java.util.HashMap;

public class SupervisorPinChangeFragment extends Fragment {

    private VerifyPinViewModel viewModel;
    private Button requestSendBtn;
    private EditText midEt;
    private boolean isLoading = false;
    private RelativeLayout progressBtnLayout;
    private ImageButton backButton;

    public SupervisorPinChangeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_supervisor_pin_change, container, false);
        viewModel = ViewModelProviders.of(this).get(VerifyPinViewModel.class);
        requestSendBtn = view.findViewById(R.id.pin_change_req_btn);
        backButton = view.findViewById(R.id.back_button_spc);
        midEt = view.findViewById(R.id.min_enter_et);
        progressBtnLayout = view.findViewById(R.id.loading_progress_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        requestSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainUtils.validateEditText(midEt) && !isLoading){
                    isLoading = true;
                    requestSendBtn.setVisibility(View.GONE);
                    progressBtnLayout.setVisibility(View.VISIBLE);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("mid", midEt.getText().toString());
                    map.put("tid",SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
                    String token = SharedHelper.getToken(requireContext());
                    viewModel.changeSupervisorPin(token, map).observe(requireActivity(), new Observer<Object>() {
                        @Override
                        public void onChanged(Object o) {
                            isLoading = false;
                            requestSendBtn.setVisibility(View.VISIBLE);
                            progressBtnLayout.setVisibility(View.GONE);
                            if (o != null) {
                                showCustomDialog(requireContext(),view);
                            }
                        }
                    });
                }
            }
        });
    }

    private void showCustomDialog(Context context, View pageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pin_change_req_success, null);
        builder.setView(dialogView);

        Button dialogBackButton = dialogView.findViewById(R.id.go_back_btn);

        builder.setCancelable(false); // This makes the dialog non-dismissible

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        dialogBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something with the inputText
                alertDialog.dismiss();
                Navigation.findNavController(pageView).popBackStack();
            }
        });
    }
}