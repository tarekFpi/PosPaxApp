package com.paymentsave.paymentsave.coreapp.fragments.settings.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.network.SharedHelper;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SupportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SupportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout backBtn;
    private TextView supportEmailTv, supportPhoneTv;

    public SupportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SupportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SupportFragment newInstance(String param1, String param2) {
        SupportFragment fragment = new SupportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_support_screen, container, false);
        supportEmailTv = view.findViewById(R.id.support_email_txt);
        supportPhoneTv = view.findViewById(R.id.support_phone_txt);
        String contactEmail = SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.CONTACT_SUPPORT_EMAIL);
        if(!Objects.equals(contactEmail, "")){
            supportEmailTv.setText(String.format("Email: %s",contactEmail));
        }
        String contactPhone = SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.CONTACT_SUPPORT_PHONE);
        if(!Objects.equals(contactPhone, "")){
            supportPhoneTv.setText(String.format("Phone: %s",contactPhone));
        }
        backBtn = view.findViewById(R.id.support_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });
        return view;
    }
}