package com.paymentsave.paymentsave.coreapp.fragments.reports.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.reports.ReportViewModel;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.AnalyticReportRepose.AnalyticReportResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class ReportCustomFormFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ReportFormFragment";
    DatePickerDialog fromDatePickerDialog, toDatePickerDialog;
    private FrameLayout mainFrame;
    private ReportViewModel viewModel;
    private Button viewSummeryBtn, disableSummeryBtn, fromDateBtn, toDateBtn;
    private RelativeLayout loadingBtn, customInputLayout;
    private LinearLayout customReportTodayLt;
    private String supervisorPin;
    private String fromDate = "";
    private String toDate = "";
    private TextView reportDate, totalAmt, totalTransactionCount, saleAmountTotal, gratuityAmountTotal, refundAmountTotal, reportTotalCashbackAmt, reportTotalCashbackCount;
    private TextView saleCountTextReport, refundCountTextReport, gratuityCountTextReport, cashbackCountTextReport;
    private PieChart pieChart;

    //    private FragmentReplacer fragmentReplacer;
    private TextView saleAmtCount, gratuityAmtCount, refundAmtCount;

    public ReportCustomFormFragment(String supervisorPin) {
        // Required empty public constructor
        this.supervisorPin = supervisorPin;
    }

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            fragmentReplacer = (FragmentReplacer) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement FragmentReplacer");
//        }
//    }


    @Override
    public void onResume() {
        fromDate = "";
        toDate = "";
        customInputLayout.setVisibility(View.VISIBLE);
        customReportTodayLt.setVisibility(View.GONE);
        fromDateBtn.setText(getString(R.string.choose_date));
        toDateBtn.setText(getString(R.string.choose_date));
        toggleSummeryBtn();
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = ViewModelProviders.of(this).get(ReportViewModel.class);
        View view = inflater.inflate(R.layout.fragment_report_custom_form, container, false);
        pieChart = view.findViewById(R.id.cr_pieChart_view);
        saleCountTextReport = view.findViewById(R.id.sale_count_txt_cr_report);
        refundCountTextReport = view.findViewById(R.id.refund_count_txt_cr_report);
        gratuityCountTextReport = view.findViewById(R.id.gratuity_count_txt_cr_report);
        cashbackCountTextReport = view.findViewById(R.id.cashback_count_txt_cr_report);

        customInputLayout = view.findViewById(R.id.custom_input_layout);
        customReportTodayLt = view.findViewById(R.id.custom_report_today_lt);
        mainFrame = view.findViewById(R.id.custom_form_report_frame);
        fromDateBtn = view.findViewById(R.id.report_from_date_btn);
        toDateBtn = view.findViewById(R.id.report_to_date_btn);
        loadingBtn = view.findViewById(R.id.report_pin_loading_progress_btn);
        viewSummeryBtn = view.findViewById(R.id.view_summery_btn);
        disableSummeryBtn = view.findViewById(R.id.disable_summery_btn);
        reportDate = view.findViewById(R.id.cr_report_date_tv);
        totalAmt = view.findViewById(R.id.cr_report_total_amt);
        totalTransactionCount = view.findViewById(R.id.cr_report_total_count);
        saleAmountTotal = view.findViewById(R.id.cr_report_total_sale_amt);
        gratuityAmountTotal = view.findViewById(R.id.cr_report_total_gratuity_amt);
        refundAmountTotal = view.findViewById(R.id.cr_report_total_refund_amt);

        reportTotalCashbackAmt = view.findViewById(R.id.cr_report_total_cashback_amt);
        reportTotalCashbackCount = view.findViewById(R.id.crs_report_total_cashback_count);

        saleAmtCount = view.findViewById(R.id.cr_report_total_sale_count);
        gratuityAmtCount = view.findViewById(R.id.crs_report_total_gratuity_count);
        refundAmtCount = view.findViewById(R.id.cr_report_total_refund_count);

        viewSummeryBtn.setOnClickListener(this);
        fromDateBtn.setOnClickListener(this);
        toDateBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fromDatePickerDialog = new DatePickerDialog(requireContext(),
                (datePicker, year, month, day) -> {
                    month = month + 1;
                    fromDate = String.format("%s-%s-%s", year, month, day);
                    toggleSummeryBtn();
                    fromDateBtn.setText(String.format("%s-%s-%s", year, month, day));
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        toDatePickerDialog = new DatePickerDialog(requireContext(),
                (datePicker, year, month, day) -> {
                    month = month + 1;
                    toDate = String.format("%s-%s-%s", year, month, day);
                    toggleSummeryBtn();
                    toDateBtn.setText(String.format("%s-%s-%s", year, month, day));
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

//        // Add one more day with max date
//        Calendar maxDate = Calendar.getInstance();
//        maxDate.add(Calendar.DAY_OF_MONTH, 1);
//        toDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        toDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.view_summery_btn) {
            viewSummeryBtn.setVisibility(View.GONE);
            loadingBtn.setVisibility(View.VISIBLE);
            String token = SharedHelper.getToken(requireContext());
            HashMap<String, Object> map = new HashMap<>();
            map.put("serial_no", MainUtils.getDeviceSerial(requireContext()));
            map.put("supervisor_pin", supervisorPin);
            map.put("date_from", fromDate);
            map.put("date_to", toDate);
            viewModel.getReportData(map, token).observe(requireActivity(), new Observer<AnalyticReportResponse>() {
                @Override
                public void onChanged(AnalyticReportResponse analyticReportResponse) {
                    viewSummeryBtn.setVisibility(View.VISIBLE);
                    loadingBtn.setVisibility(View.GONE);
                    customInputLayout.setVisibility(View.GONE);
                    customReportTodayLt.setVisibility(View.VISIBLE);

//                    fragmentReplacer.replaceFragment();
                    if (analyticReportResponse != null) {
                        reportDate.setText(String.format("%s - %s", fromDate, toDate));
                        totalAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getTotalAmount()));
                        totalTransactionCount.setText(String.format("%s transactions", analyticReportResponse.getData().getTotalCount()));
                        saleAmountTotal.setText(String.format("£%.2f", analyticReportResponse.getData().getSalesTransactionTotal()));
                        gratuityAmountTotal.setText(String.format("£%s", analyticReportResponse.getData().getGratuityTransactionTotal()));
                        refundAmountTotal.setText(String.format("£%.2f", analyticReportResponse.getData().getRefundTransactionTotal()));
                        reportTotalCashbackAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getCashbackTransactionTotal()));

                        try {
                            gratuityAmtCount.setText(String.format("%s transactions", analyticReportResponse.getData().getGratuityTransactionCount()));
                            refundAmtCount.setText(String.format("%s transactions", analyticReportResponse.getData().getRefundTransactionCount()));
                            saleAmtCount.setText(String.format("%s transactions", analyticReportResponse.getData().getSalesTransactionCount()));
                            reportTotalCashbackCount.setText(String.format("%s transactions", analyticReportResponse.getData().getCashbackTransactionCount()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        saleCountTextReport.setText(String.format("Sale - %s transactions", analyticReportResponse.getData().getSalesTransactionCount()));
                        refundCountTextReport.setText(String.format("Refund - %s transactions", analyticReportResponse.getData().getRefundTransactionCount()));
                        gratuityCountTextReport.setText(String.format("Gratuity - %s transactions", analyticReportResponse.getData().getGratuityTransactionCount()));
                        cashbackCountTextReport.setText(String.format("Cashback - %s transactions", analyticReportResponse.getData().getCashbackTransactionCount()));
                        showPieChart(analyticReportResponse.getData().getSalesTransactionTotal(), analyticReportResponse.getData().getRefundTransactionTotal(), analyticReportResponse.getData().getGratuityTransactionTotal(), analyticReportResponse.getData().getCashbackTransactionTotal());


//                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                        transaction.addToBackStack(null);
//                        transaction.replace(R.id.custom_form_report_frame, new ReportCustomFragment()).commit();
//                        transaction.replace(R.id.pager, new ReportCustomFragment()).commit();

//                        FragmentManager fm = requireActivity().getSupportFragmentManager();
//                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_reportPinVerifyFragment_to_reportFragment);
                    }
                }
            });

        } else if (view.getId() == R.id.report_from_date_btn) {
            fromDatePickerDialog.show();
        } else if (view.getId() == R.id.report_to_date_btn) {
            toDatePickerDialog.show();
        }
    }

    private void showPieChart(double sale, double refund, double gratuity, double cashBack) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "";

        // initializing data
        Map<String, Double> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Sale", sale);
        typeAmountMap.put("Refund", refund);
        typeAmountMap.put("Cashback", cashBack);
        typeAmountMap.put("Gratuity", gratuity);

        double total = sale + refund + gratuity;

        ArrayList<Integer> colors = new ArrayList<>();
        if (sale > 0) colors.add(Color.parseColor("#009ADA"));
        if (refund > 0) colors.add(Color.parseColor("#C90000"));
        if (cashBack > 0) colors.add(Color.parseColor("#7734EB"));
        if (gratuity > 0) colors.add(Color.parseColor("#F7CB88"));

        if (total == 0) {
            // Add a dummy entry to show "No data" or similar
            pieEntries.add(new PieEntry(1f, "No Data"));
            colors.clear();
            colors.add(Color.LTGRAY);  // use a neutral color
        } else {
            for (Map.Entry<String, Double> entry : typeAmountMap.entrySet()) {
                float value = entry.getValue().floatValue();
                if (value > 0) {
                    pieEntries.add(new PieEntry(value, entry.getKey()));
                }
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(3f);

        if (sale > 0 || refund > 0 || gratuity > 0) {
            // Enable and style value lines
            pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            pieDataSet.setValueLinePart1Length(0.4f);  // First segment of the line
            pieDataSet.setValueLinePart2Length(0.3f);  // Second segment (horizontal)
            pieDataSet.setValueLineColor(Color.DKGRAY);  // Line color
        }

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return total == 0 ? "" : String.format("%.2f", value);
            }
        });
        pieData.setDrawValues(true);
        pieData.setValueTextColor(Color.DKGRAY);

        pieChart.setData(pieData);
        pieChart.setDrawEntryLabels(false);
        pieChart.setDescription(null);
        pieChart.invalidate();
    }


    private void toggleSummeryBtn() {
        if (!toDate.isEmpty() && !fromDate.isEmpty()) {
            viewSummeryBtn.setVisibility(View.VISIBLE);
            disableSummeryBtn.setVisibility(View.GONE);
        } else {
            viewSummeryBtn.setVisibility(View.GONE);
            disableSummeryBtn.setVisibility(View.VISIBLE);
        }
    }
}