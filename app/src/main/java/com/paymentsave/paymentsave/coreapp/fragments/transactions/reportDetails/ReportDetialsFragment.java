package com.paymentsave.paymentsave.coreapp.fragments.transactions.reportDetails;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.PrintUtils;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;

import java.util.Objects;


public class ReportDetialsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ReportDetialsFragment";
    private final String ARG_PARAM = "report_obj";
    private LinearLayout backBtn;
    private RelativeLayout printBtn;
    private TextView errorText, netAmount, reportType, reportDate, reportTime, reportSaleCount, reportSaleAmount, reportRefundCount, reportRefundAmount, reportCompletionCount, reportCompletionAmount, reportCashbackCount, reportCashbackAmount, reportGratuityCount, reportGratuityAmount;

    private Report report;

    private double total_sale, total_refund, total_completion, total_cashback, total_gratuity, totalAmount;

    public ReportDetialsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Gson gson = new Gson();
            String jsonObj = getArguments().getString(ARG_PARAM);
            report = gson.fromJson(jsonObj, Report.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_report_detials, container, false);
        errorText = view.findViewById(R.id.error_text);
        netAmount = view.findViewById(R.id.report_net_amt_tv);
        reportType = view.findViewById(R.id.report_type_tv);
        reportDate = view.findViewById(R.id.report_date_tv);
        reportTime = view.findViewById(R.id.report_time_tv);

        reportSaleCount = view.findViewById(R.id.report_sale_count);
        reportSaleAmount = view.findViewById(R.id.report_sale_amount);
        reportRefundCount = view.findViewById(R.id.report_refund_count);
        reportRefundAmount = view.findViewById(R.id.report_refund_amount);
        reportCompletionCount = view.findViewById(R.id.report_com_count);
        reportCompletionAmount = view.findViewById(R.id.report_com_amount);
        reportCashbackCount = view.findViewById(R.id.report_cashback_count);
        reportCashbackAmount = view.findViewById(R.id.report_cashback_amount);
        reportGratuityCount = view.findViewById(R.id.report_gratuity_count);
        reportGratuityAmount = view.findViewById(R.id.report_gratuity_amount);


        printBtn = view.findViewById(R.id.report_print_receipt_btn);
        printBtn.setOnClickListener(this);
        backBtn = view.findViewById(R.id.report_details_back_btn);
        backBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        total_sale = report.getSaleCount() * Double.parseDouble(report.getSaleAmount());
//        total_refund = report.getRefundCount() * Double.parseDouble(report.getRefundAmount());
//        total_completion = report.getCompletionCount() * Double.parseDouble(report.getCompletionAmount());

        if (report.getReportError() == null || report.getReportError().equals("null")) {
            printBtn.setVisibility(View.VISIBLE);
        } else {
            printBtn.setVisibility(View.GONE);
            if (Objects.equals(report.getReportError(), "REPORT_SAVED_BUT_NOT_SUBMITTED")) {
                errorText.setText(R.string.report_couldn_t_be_sumitted_due_to_connection_problem_report_will_be_submitted_next_time_a_successful_connection_is_made_to_the_host_this_report_will_not_be_reprinted);
            } else {
                errorText.setText(report.getReportError());
            }
        }

        total_sale = Double.parseDouble(report.getSaleAmount()) / 100.0;
        total_refund = Double.parseDouble(report.getRefundAmount()) / 100.0;
        total_completion = Double.parseDouble(report.getCompletionAmount()) / 100.0;
        total_cashback = Double.parseDouble(report.getCashbackAmount()) / 100.0;
        total_gratuity = Double.parseDouble(report.getGratuityAmount()) / 100.0;

//        totalAmount = total_sale - total_refund + total_completion + total_gratuity;
        totalAmount = total_sale - total_refund + total_completion;

        netAmount.setText(String.format("%.2f", totalAmount));
//        netAmount.setText(String.format("£%s", totalAmount));
        reportType.setText(report.getReportType());
        reportDate.setText(DateUtils.getFormattedDate(report.getCreatedAt()));
        reportTime.setText(DateUtils.getTime(report.getCreatedAt()));

        reportSaleCount.setText(String.format("x%s", report.getSaleCount()));
        reportSaleAmount.setText(String.format("£%.2f", total_sale));
        reportRefundCount.setText(String.format("x%s", report.getRefundCount()));
        reportRefundAmount.setText(String.format("£%.2f", total_refund));
        reportCompletionCount.setText(String.format("x%s", report.getCompletionCount()));
        reportCompletionAmount.setText(String.format("£%.2f", total_completion));
        reportCashbackCount.setText(String.format("x%s", report.getCashbackCount()));
        reportCashbackAmount.setText(String.format("£%.2f", total_cashback));
        reportGratuityCount.setText(String.format("x%s", report.getGratuityCount()));
        reportGratuityAmount.setText(String.format("£%.2f", total_gratuity));
    }

    public Bitmap createReceiptBitmap() {
        // Define the bitmap width and height
        int bitmapWidth = 400; // Adjust as necessary
        int bitmapHeight = 600; // Adjust based on content

        // Create a Bitmap
        Bitmap receiptBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);

        // Create a Canvas using the bitmap
        Canvas canvas = new Canvas(receiptBitmap);

        // Fill the canvas with a white color
        canvas.drawColor(Color.WHITE);

        // Create a Paint object for drawing text
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Draw some text on the canvas
        canvas.drawText("Receipt Header", 10, 25, paint);
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Item 1: $10", 10, 50, paint);
        canvas.drawText("Item 2: $15", 10, 75, paint);
        canvas.drawText("Total: $25", 10, 100, paint);

        // Return the bitmap
        return receiptBitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_details_back_btn:
                Navigation.findNavController(v).popBackStack();
                break;
            case R.id.report_print_receipt_btn:
                PrintUtils.printReportCopy(requireContext(), report);
                break;
        }
    }
}