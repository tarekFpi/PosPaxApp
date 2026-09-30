package com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.verifySupervisorPin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.PinVerifyResponse.PinVerifyResponse;

import java.net.HttpURLConnection;
import java.util.HashMap;

public class VerifyPinFragment extends Fragment implements View.OnClickListener {

    private FloatingActionButton backBtn;
    private Button mPinActivateBtn;
    private RelativeLayout loadingProgressBtn;
    private EditText pinPass1, pinPass2, pinPass3, pinPass4;
    private Button btn00, btn01, btn02, btn03, btn04, btn05, btn06, btn07, btn08, btn09, btnBackSpace;
    private ImageButton pinVisibilityBtn;
    private boolean visiblePinPass = false;
    private VerifyPinViewModel viewModel;
    private TextWatcher textWatcher = new TextWatcher() {
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
    private boolean isActivateBtnEnabled = false;

    private void toggleProceedBtn() {
        if (MainUtils.validateEditTextWithoutAlert(pinPass1) && MainUtils.validateEditTextWithoutAlert(pinPass2) && MainUtils.validateEditTextWithoutAlert(pinPass3) && MainUtils.validateEditTextWithoutAlert(pinPass4)) {
            isActivateBtnEnabled = true;
            mPinActivateBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_btn_bg));
        } else {
            isActivateBtnEnabled = false;
            mPinActivateBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_gray));
        }
    }

    public VerifyPinFragment() {
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
        View view = inflater.inflate(R.layout.fragment_verify_pin, container, false);
        viewModel = ViewModelProviders.of(this).get(VerifyPinViewModel.class);
        backBtn = view.findViewById(R.id.mPin_back_button);
        pinPass1 = view.findViewById(R.id.pinInput1);
        pinPass2 = view.findViewById(R.id.pinInput2);
        pinPass3 = view.findViewById(R.id.pinInput3);
        pinPass4 = view.findViewById(R.id.pinInput4);
        mPinActivateBtn = view.findViewById(R.id.mPin_activate_btn);
        loadingProgressBtn = view.findViewById(R.id.loading_progress_btn);
        pinVisibilityBtn = view.findViewById(R.id.pinVisibilityBtn);

        btn00 = view.findViewById(R.id.btn00);
        btn00.setOnClickListener(keypadOnclickListener);
        btn01 = view.findViewById(R.id.btn01);
        btn01.setOnClickListener(keypadOnclickListener);
        btn02 = view.findViewById(R.id.btn02);
        btn02.setOnClickListener(keypadOnclickListener);
        btn03 = view.findViewById(R.id.btn03);
        btn03.setOnClickListener(keypadOnclickListener);
        btn04 = view.findViewById(R.id.btn04);
        btn04.setOnClickListener(keypadOnclickListener);
        btn05 = view.findViewById(R.id.btn05);
        btn05.setOnClickListener(keypadOnclickListener);
        btn06 = view.findViewById(R.id.btn06);
        btn06.setOnClickListener(keypadOnclickListener);
        btn07 = view.findViewById(R.id.btn07);
        btn07.setOnClickListener(keypadOnclickListener);
        btn08 = view.findViewById(R.id.btn08);
        btn08.setOnClickListener(keypadOnclickListener);
        btn09 = view.findViewById(R.id.btn09);
        btn09.setOnClickListener(keypadOnclickListener);
        btnBackSpace = view.findViewById(R.id.btnBackSpace);
        btnBackSpace.setOnClickListener(keypadOnclickListener);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        backBtn.setOnClickListener(this);
        pinVisibilityBtn.setOnClickListener(this);
        mPinActivateBtn.setOnClickListener(this);

        pinPass1.addTextChangedListener(textWatcher);
        pinPass2.addTextChangedListener(textWatcher);
        pinPass3.addTextChangedListener(textWatcher);
        pinPass4.addTextChangedListener(textWatcher);
    }

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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mPin_back_button:
                Navigation.findNavController(view).popBackStack();
                break;
            case R.id.mPin_activate_btn:
                if (isActivateBtnEnabled) {
                    String pin = pinPass1.getText().toString() + pinPass2.getText().toString() + pinPass3.getText().toString() + pinPass4.getText().toString();
                    HashMap<String, String> map = new HashMap<>();
                    map.put("mid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID));
                    map.put("serial_no", MainUtils.getDeviceSerial(requireContext()));
                    map.put("s_pin", pin);
                    mPinActivateBtn.setVisibility(View.GONE);
                    loadingProgressBtn.setVisibility(View.VISIBLE);
                    viewModel.verifySupervisorPin(map).observe(requireActivity(), new Observer<PinVerifyResponse>() {
                        @Override
                        public void onChanged(PinVerifyResponse pinVerifyResponse) {
                            loadingProgressBtn.setVisibility(View.GONE);
                            mPinActivateBtn.setVisibility(View.VISIBLE);
                            if (pinVerifyResponse != null && pinVerifyResponse.getCode() == HttpURLConnection.HTTP_OK) {
                                Toast.makeText(requireContext(), "Successfully Admin Verified", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_adminPinFragment_to_adminMenuFragment);
                            } else {
                                resetPin();
                            }
                        }
                    });

//                    if (pin.equals("1200")) {
//                        Toast.makeText(requireContext(), "Successfully Admin Verified", Toast.LENGTH_SHORT).show();
//                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_adminPinFragment_to_adminMenuFragment);
//                    } else {
//                        Toast.makeText(requireContext(), "Invalid Pin Provided !", Toast.LENGTH_SHORT).show();
//                    }
                } else {
                    Toast.makeText(requireContext(), "Please verify the admin pin", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pinVisibilityBtn:
                visiblePinPass = !visiblePinPass;
                setPinVisibility(visiblePinPass);
                break;
        }
    }

    private void resetPin() {
        pinPass1.setText("");
        pinPass2.setText("");
        pinPass3.setText("");
        pinPass4.setText("");
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
}