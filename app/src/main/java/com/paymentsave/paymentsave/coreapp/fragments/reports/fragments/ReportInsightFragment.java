package com.paymentsave.paymentsave.coreapp.fragments.reports.fragments;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.reports.ReportViewModel;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.AnalyticReportRepose.AnalyticReportResponse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReportInsightFragment extends Fragment {

    private static final String TAG = "ReportInsightFragment";
    private ProgressBar progressBar;
    private LinearLayout reportTodayLayout;
    private ReportViewModel viewModel;
    private TextView reportDate, reportTotalAmt, reportTotalCount;
    private TextView reportTotalSaleAmt, reportTotalSaleCount, reportTotalGratuityAmt, reportTotalGratuityCount, reportTotalRefundAmt, reportTotalRefundCount, reportTotalCashbackAmt, reportTotalCashbackCount;
    private TextView saleCountTextReport, refundCountTextReport, gratuityCountTextReport, cashBackCounterReport;
    private String supervisorPin = "";
    private PieChart pieChart;

    public ReportInsightFragment(String supervisorPin) {
        this.supervisorPin = supervisorPin;
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
        View view = inflater.inflate(R.layout.fragment_report_insight, container, false);
        viewModel = ViewModelProviders.of(this).get(ReportViewModel.class);
        progressBar = view.findViewById(R.id.report_insight_pb);
        reportTodayLayout = view.findViewById(R.id.report_today_lt);
        pieChart = view.findViewById(R.id.pieChart_view);

        reportDate = view.findViewById(R.id.report_date_tv);
        reportTotalAmt = view.findViewById(R.id.report_total_amt);
        reportTotalCount = view.findViewById(R.id.report_total_count);

        saleCountTextReport = view.findViewById(R.id.sale_count_txt_report);
        refundCountTextReport = view.findViewById(R.id.refund_count_txt_report);
        gratuityCountTextReport = view.findViewById(R.id.gratuity_count_txt_report);
        cashBackCounterReport = view.findViewById(R.id.cashback_count_txt_report);

        reportTotalSaleAmt = view.findViewById(R.id.report_total_sale_amt);
        reportTotalSaleCount = view.findViewById(R.id.report_total_sale_count);
        reportTotalGratuityAmt = view.findViewById(R.id.report_total_gratuity_amt);
        reportTotalGratuityCount = view.findViewById(R.id.report_total_gratuity_count);
        reportTotalRefundAmt = view.findViewById(R.id.report_total_refund_amt);
        reportTotalRefundCount = view.findViewById(R.id.report_total_refund_count);
        reportTotalCashbackAmt = view.findViewById(R.id.report_total_cashback_amt);
        reportTotalCashbackCount = view.findViewById(R.id.report_total_cashback_count);

        return view;
    }


    private void initPieChart() {
        //using percentage as values instead of amount
        pieChart.setUsePercentValues(true);

        //remove the description label on the lower left corner, default true if not set
        pieChart.getDescription().setEnabled(false);

        //enabling the user to rotate the chart, default true
        pieChart.setRotationEnabled(true);
        //adding friction when rotating the pie chart
        pieChart.setDragDecelerationFrictionCoef(0.9f);
        //setting the first entry start from right hand side, default starting from top
        pieChart.setRotationAngle(0);

        //highlight the entry when it is tapped, default true if not set
        pieChart.setHighlightPerTapEnabled(true);
        //adding animation so the entries pop up from 0 degree
//        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        //setting the color of the hole in the middle, default white
        pieChart.setHoleColor(Color.parseColor("#000000"));

    }

    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
//        for (int i = 0; i < count ; i++) {
//            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
//                    parties[i % parties.length],
//                    getResources().getDrawable(R.drawable.blue_circle)));
//        }

        Map<String, Integer> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Toys", 200);
        typeAmountMap.put("Snacks", 230);
        typeAmountMap.put("Clothes", 100);
        for (String type : typeAmountMap.keySet()) {
            entries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
//        data.setValueTypeface(tfLight);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }

    private void showPieChart2(double sale, double refund, double gratuity) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "";

        //initializing data
        Map<String, Double> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Sale", sale);
        typeAmountMap.put("Gratuity", gratuity);
        typeAmountMap.put("Refund", refund);

//        typeAmountMap.put("Stationary",500);
//        typeAmountMap.put("Phone",50);

        //initializing colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#009ADA"));
        colors.add(Color.parseColor("#C90000"));
//        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#F7CB88"));
//        colors.add(Color.parseColor("#890567"));
//        colors.add(Color.parseColor("#a35567"));
//        colors.add(Color.parseColor("#ff5f67"));
//        colors.add(Color.parseColor("#3ca567"));

        //input data and fit data into pie chart entry
        for (String type : typeAmountMap.keySet()) {
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);

//        pieDataSet.setSliceSpace(3f);
//        pieDataSet.setSelectionShift(5f);
//        pieDataSet.setValueLinePart1OffsetPercentage(80.f);
//        pieDataSet.setValueLinePart1Length(0.4f);
//        pieDataSet.setValueLinePart2Length(0.4f);
//        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


//        PieDataSet pieDataSet = new PieDataSet(pieEntries,label);
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
//        pieChart.setDrawRoundedSlices(true);
        pieChart.invalidate();

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


        double total = sale + refund + gratuity + cashBack;

        ArrayList<Integer> colors = new ArrayList<>();
        if (sale > 0) colors.add(Color.parseColor("#009ADA"));
        if (refund > 0) colors.add(Color.parseColor("#C90000"));
        if (cashBack > 0) colors.add(Color.parseColor("#7734EB"));
        if (gratuity > 0) colors.add(Color.parseColor("#F7CB88"));


        if (total == 0) {
            // Add a dummy entry to show "No data" or similar
            pieEntries.add(new PieEntry(1f, "No Data"));
            colors.clear();
            colors.add(Color.LTGRAY);  /// use a neutral color
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

        if (sale > 0 || refund > 0 || gratuity > 0 || cashBack > 0) {
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

    private void showPieChart3(double sale, double refund, double gratuity) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "";

        // Initializing data
        Map<String, Double> typeAmountMap = new HashMap<>();
        typeAmountMap.put("Sale", sale);
        typeAmountMap.put("Gratuity", gratuity);
        typeAmountMap.put("Refund", refund);

        double total = sale + refund + gratuity;

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#009ADA"));  // Sale - Blue
        colors.add(Color.parseColor("#F7CB88"));  // Gratuity - Yellow
        colors.add(Color.parseColor("#C90000"));  // Refund - Red

        if (total == 0) {
            // Show a neutral slice if there's no data
            pieEntries.add(new PieEntry(1f, "No Data"));
            colors.clear();
            colors.add(Color.LTGRAY);  // Neutral gray
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
        pieDataSet.setSliceSpace(5f);

        // Enable and style value lines
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.6f);  // First segment of the line
        pieDataSet.setValueLinePart2Length(0.3f);  // Second segment (horizontal)
        pieDataSet.setValueLineColor(Color.DKGRAY);  // Line color

        // Format values
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return total == 0 ? "" : String.format("%.2f", value);
            }
        });
        pieData.setDrawValues(true);
        pieData.setValueTextColor(Color.DKGRAY);

        // Setup PieChart
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawEntryLabels(false); // Hide slice labels inside the pie
        pieChart.setDrawHoleEnabled(false); // Optional: Set true for donut chart
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setDescription(null); // Remove description label
        pieChart.getLegend().setEnabled(true); // Enable legend if needed

        // Refresh the chart
        pieChart.invalidate();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String device_serial = MainUtils.getDeviceSerial(requireContext());


//        setData(3,0.0f);
        Date date = new Date();
        String fromDate = DateUtils.getFormattedDateUSA(date.toString());
        String toDate = DateUtils.getFormattedDateUSA(date.toString());

        int numberOfDays = 1;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDays);
        Date nextDate = calendar.getTime();

//        try {
//            toDate = DateUtils.formatSystemDate(nextDate.toString());
//        } catch (ParseException e) {
//            e.printStackTrace();
////            throw new RuntimeException(e);
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
                    reportDate.setText(DateUtils.getUSAFormattedDate(date.toString()));
                    reportTotalAmt.setText(String.format("£%.2f", analyticReportResponse.getData().getTotalAmount()));
                    reportTotalCount.setText(String.format("%s transactions", analyticReportResponse.getData().getTotalCount()));

                    saleCountTextReport.setText(String.format("Sale - %s transactions", analyticReportResponse.getData().getSalesTransactionCount()));
                    refundCountTextReport.setText(String.format("Refund - %s transactions", analyticReportResponse.getData().getRefundTransactionCount()));
                    gratuityCountTextReport.setText(String.format("Gratuity - %s transactions", analyticReportResponse.getData().getGratuityTransactionCount()));
                    cashBackCounterReport.setText(String.format("Cashback - %s transactions", analyticReportResponse.getData().getCashbackTransactionCount()));
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
}