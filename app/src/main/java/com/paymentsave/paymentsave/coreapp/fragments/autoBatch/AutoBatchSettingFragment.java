package com.paymentsave.paymentsave.coreapp.fragments.autoBatch;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paymentsave.paymentsave.R;

import java.util.Calendar;

public class AutoBatchSettingFragment extends Fragment implements View.OnClickListener {

    private LinearLayout backBtn, batchTimeLayout;
    private SwitchCompat autoBatchBtn;
    private Button setTimeBtn;
    private TextView autoBatchTimeTxt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auto_batch_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        autoBatchBtn = view.findViewById(R.id.auto_batch_btn);
        batchTimeLayout = view.findViewById(R.id.batch_time_layout);
        setTimeBtn = view.findViewById(R.id.set_time_btn);
        autoBatchTimeTxt = view.findViewById(R.id.auto_batch_time_txt);
        backBtn = view.findViewById(R.id.auto_batch_back_btn);
        autoBatchBtn.setOnClickListener(this);
        setTimeBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_time_btn:
                // on below line we are getting the
                // instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                        (view, hourOfDay, minute1) -> {
                            // on below line we are setting selected time
                            autoBatchTimeTxt.setText(String.format("%s:%s", hourOfDay, minute1));
                        }, hour, minute, false);
                timePickerDialog.show();
                break;
            case R.id.auto_batch_back_btn:
                Navigation.findNavController(v).popBackStack();
                break;
            case R.id.auto_batch_btn:
                if (autoBatchBtn.isChecked())
                    batchTimeLayout.setVisibility(View.VISIBLE);
                else
                    batchTimeLayout.setVisibility(View.GONE);
                break;
        }
    }
}