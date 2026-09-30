package com.paymentsave.paymentsave.coreapp.fragments.payByLink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.zxing.WriterException;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.viewModels.TransactionViewModel;
import com.paymentsave.paymentsave.coreapp.utils.DialogHelper;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.PBLResponse.PBLCreateResponse;

import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class CreatePBLFragment extends Fragment {
    private static final String TAG = "CreatePBLFragment";
    private LinearLayout backBtn, loadingBtn;
    private EditText amountEditText, descriptionEditText;
    private Button createLinkButton;
    private boolean isEditing = false;
    private String digits = "";
    private TransactionViewModel viewModel;

    public CreatePBLFragment() {
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
        View view = inflater.inflate(R.layout.fragment_create_p_b_l, container, false);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingBtn = view.findViewById(R.id.createLinkButtonLoading);
        amountEditText = view.findViewById(R.id.amountEditText);
        amountEditText.setText("0.00");
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        createLinkButton = view.findViewById(R.id.createLinkButton);
        backBtn = view.findViewById(R.id.pbl_create_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });
        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double inti = Double.parseDouble(charSequence.toString());
                if (
                        inti > 0.00
                ) {
                    createLinkButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_btn_bg));
                } else {
                    createLinkButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isEditing) return;

                isEditing = true;

                // Keep only digits
                String newDigits = editable.toString().replaceAll("[^0-9]", "");

                // Never allow empty
                if (newDigits.isEmpty()) {
                    digits = "";
                    amountEditText.setText("0.00");
                    amountEditText.setSelection(amountEditText.getText().length());
                    isEditing = false;
                    return;
                }

                digits = newDigits;

                long value = Long.parseLong(digits);

                String formatted = String.format(
                        Locale.US,
                        "%.2f",
                        value / 100.0
                );

                amountEditText.setText(formatted);
                amountEditText.setSelection(formatted.length());

                isEditing = false;
            }
        });

        createLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!amountEditText.getText().toString().equals("0.00")) {
                    generatePblLink();
                } else {
                    Toasty.info(requireContext(), "Please enter a valid amount !", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generatePblLink() {
        createLinkButton.setVisibility(View.GONE);
        loadingBtn.setVisibility(View.VISIBLE);
        String token = SharedHelper.getToken(requireContext());
        String merchant_name = SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TRADING_NAME);
        String merchant_email = SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.BUSINESS_EMAIL);
        int businessId = SharedHelper.getIntData(
                requireContext(), AppConstants.SharedPref.BUSINESS_ID);
        HashMap<String, Object> map = new HashMap<>();
        map.put("business_ref", businessId);
        map.put("amount", amountEditText.getText().toString());
        if (!TextUtils.isEmpty(descriptionEditText.getText().toString())) {
            map.put("description", descriptionEditText.getText().toString());
        } else {
            map.put("description", "");
        }
        map.put("expires_duration", "24h");
        map.put("merchant_name", merchant_name);
        map.put("merchant_email", merchant_email);
        viewModel.createPBLLink(token, map).observe(
                this, new Observer<PBLCreateResponse>() {
                    @Override
                    public void onChanged(PBLCreateResponse pblCreateResponse) {
                        loadingBtn.setVisibility(View.GONE);
                        createLinkButton.setVisibility(View.VISIBLE);
                        if (pblCreateResponse != null) {
                            Log.e(TAG, "onChanged: " + pblCreateResponse.toString());
                            Toasty.success(requireContext(), "Link Created !", Toasty.LENGTH_SHORT).show();
                            amountEditText.setText(R.string._0_0_0);
                            descriptionEditText.setText("");
                            try {
                                DialogHelper.showSuccessPBLDialog(requireContext(), requireActivity(), pblCreateResponse
                                        , viewModel);
                            } catch (WriterException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                }
        );
    }
}