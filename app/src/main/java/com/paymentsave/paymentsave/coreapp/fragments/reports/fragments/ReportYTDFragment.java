package com.paymentsave.paymentsave.coreapp.fragments.reports.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.reports.ReportViewModel;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.AnalyticReportRepose.AnalyticReportResponse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReportYTDFragment extends Fragment {
    private static final String TAG = "ReportYTDFragment";
    private ProgressBar progressBar;
    private LinearLayout reportTodayLayout;
    private ReportViewModel viewModel;
    private TextView reportDate, reportTotalAmt, reportTotalCount;
    private TextView reportTotalSaleAmt, reportTotalSaleCount, reportTotalGratuityAmt, reportTotalGratuityCount, reportTotalRefundAmt, reportTotalRefundCount, reportTotalCashbackAmt, reportTotalCashbackCount;
    private TextView saleCountTextReport, refundCountTextReport, gratuityCountTextReport, cashbackCountTextReport;
    private String supervisorPin;
    private PieChart pieChart;

    public ReportYTDFragment(String supervisorPin) {
        this.supervisorPin = supervisorPin;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_y_t_d, container, false);
        viewModel = ViewModelProviders.of(this).get(ReportViewModel.class);
        progressBar = view.findViewById(R.id.report_ysd_insight_pb);
        reportTodayLayout = view.findViewById(R.id.report_ysd_today_lt);
        pieChart = view.findViewById(R.id.pieChart_view);

        reportDate = view.findViewById(R.id.report_ysd_date_tv);
        reportTotalAmt = view.findViewById(R.id.report_ysd_total_amt);
        reportTotalCount = view.findViewById(R.id.report_ysd_total_count);

        saleCountTextReport = view.findViewById(R.id.sale_count_txt_ysd_report);
        refundCountTextReport = view.findViewById(R.id.refund_count_txt_ysd_report);
        gratuityCountTextReport = view.findViewById(R.id.gratuity_count_txt_ysd_report);
        cashbackCountTextReport = view.findViewById(R.id.cashback_count_txt_ysd_report);

        reportTotalSaleAmt = view.findViewById(R.id.report_ysd_total_sale_amt);
        reportTotalSaleCount = view.findViewById(R.id.report_ysd_total_sale_count);
        reportTotalGratuityAmt = view.findViewById(R.id.report_ysd_total_gratuity_amt);
        reportTotalGratuityCount = view.findViewById(R.id.report_ysd_total_gratuity_count);
        reportTotalRefundAmt = view.findViewById(R.id.report_ysd_total_refund_amt);
        reportTotalRefundCount = view.findViewById(R.id.report_ysd_total_refund_count);
        reportTotalCashbackAmt = view.findViewById(R.id.report_ysd_total_cashback_amt);
        reportTotalCashbackCount = view.findViewById(R.id.report_ysd_total_cashback_count);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int numberOfDays = 1;
        String device_serial = MainUtils.getDeviceSerial(requireContext());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1 * numberOfDays);
        Date yesterdayDate = calendar.getTime();

        String fromDate = null;
        try {
            fromDate = DateUtils.formatSystemDate(yesterdayDate.toString());
        } catch (ParseException e) {
            /// Telemetry: ReportYTDFragment.onViewCreated — failed to format fromDate.
            logReceiveError("onViewCreated", String.valueOf(e.getMessage()), e);
        }

        String toDate = fromDate;

//        Date date = new Date();
//        String toDate = null;
//        toDate = DateUtils.getFormattedDateUSA(date.toString());

//        try {
//            toDate = DateUtils.formatSystemDate(yesterdayDate.toString());
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
        String token = SharedHelper.getToken(requireContext());
        HashMap<String, Object> map = new HashMap<>();
        map.put("serial_no", device_serial);
        map.put("supervisor_pin", supervisorPin);
        map.put("date_from", fromDate);
        map.put("date_to", toDate);
        viewModel.getReportData(map, token).observe(requireActivity(), new Observer<AnalyticReportResponse>() {
            @Override
            public void onChanged(AnalyticReportResponse analyticReportResponse) {
                progressBar.setVisibility(View.GONE);
                if (analyticReportResponse != null) {
                    reportDate.setText(DateUtils.getUSAFormattedDate(yesterdayDate.toString()));
//                    try {
//                        reportDate.setText(DateUtils.formatSystemDate(yesterdayDate.toString()));
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
                    reportTotalAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getTotalAmount()));
                    reportTotalCount.setText(String.format("%s transactions", analyticReportResponse.getData().getTotalCount()));

                    saleCountTextReport.setText(String.format("Sale - %s transactions", analyticReportResponse.getData().getSalesTransactionCount()));
                    refundCountTextReport.setText(String.format("Refund - %s transactions", analyticReportResponse.getData().getRefundTransactionCount()));
                    gratuityCountTextReport.setText(String.format("Gratuity - %s transactions", analyticReportResponse.getData().getGratuityTransactionCount()));
                    cashbackCountTextReport.setText(String.format("Cashback - %s transactions", analyticReportResponse.getData().getCashbackTransactionCount()));
                    showPieChart(analyticReportResponse.getData().getSalesTransactionTotal(), analyticReportResponse.getData().getRefundTransactionTotal(), analyticReportResponse.getData().getGratuityTransactionTotal(), analyticReportResponse.getData().getCashbackTransactionTotal());

                    reportTotalSaleAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getSalesTransactionTotal()));
                    reportTotalSaleCount.setText(String.format("%s transactions", analyticReportResponse.getData().getSalesTransactionCount()));
                    reportTotalGratuityAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getGratuityTransactionTotal()));
                    reportTotalGratuityCount.setText(String.format("%s transactions", analyticReportResponse.getData().getGratuityTransactionCount()));
                    reportTotalRefundAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getRefundTransactionTotal()));
                    reportTotalRefundCount.setText(String.format("%s transactions", analyticReportResponse.getData().getRefundTransactionCount()));
                    reportTotalCashbackAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getCashbackTransactionTotal()));
                    reportTotalCashbackCount.setText(String.format("%s transactions", analyticReportResponse.getData().getCashbackTransactionCount()));

                    reportTodayLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void showPieChart2(double sale, double refund, double gratuity) {

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "";

        //initializing data
        Map<String, Double> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Sale", sale);
        typeAmountMap.put("Gratuity", gratuity);
        typeAmountMap.put("Refund", refund);

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#009ADA"));
        colors.add(Color.parseColor("#C90000"));
        colors.add(Color.parseColor("#F7CB88"));


        //input data and fit data into pie chart entry
        for (String type : typeAmountMap.keySet()) {
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);

        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(5);

        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%.2f", value); // Keeps only 2 decimal places
            }
        });
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
//        pieChart.setCenterText("18-05-2024");
//        pieChart.setCenterText("18-05-2024\n-\n18-05-2024");
        pieChart.setDrawEntryLabels(false);
        pieChart.setDescription(null);
        pieChart.invalidate();

    }

    private void showPieChart(double sale, double refund, double gratuity, double cashback) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "";

        // initializing data
        Map<String, Double> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Sale", sale);
        typeAmountMap.put("Refund", refund);
        typeAmountMap.put("Cashback", cashback);
        typeAmountMap.put("Gratuity", gratuity);

        double total = sale + refund + gratuity;

        ArrayList<Integer> colors = new ArrayList<>();
        if (sale > 0) colors.add(Color.parseColor("#009ADA"));
        if (refund > 0) colors.add(Color.parseColor("#C90000"));
        if (cashback > 0) colors.add(Color.parseColor("#7734EB"));
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

    /**
     * Sends a fragment failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code ReportYTDFragment.onViewCreated}.
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