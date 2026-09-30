package com.paymentsave.paymentsave.coreapp.fragments.transactions;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.FileUtils;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private static final String TAG = "ReportAdapter";
    private Context context;
    private List<Report> reportList;

    private OnReportItemClickListener onReportItemClickListener;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_item_model, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        Report previousReport = null;
        try {
            previousReport = reportList.get(position - 1);
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }
        if (previousReport != null) {
            if (FileUtils.getDateFromString(report.getCreatedAt()).equals(FileUtils.getDateFromString(previousReport.getCreatedAt()))) {
                holder.dateTv.setVisibility(View.VISIBLE);
//                holder.dateTv.setVisibility(View.GONE);
                holder.dateTv.setText(DateUtils.getNormalDateString(report.getCreatedAt()));
            } else {
                holder.dateTv.setVisibility(View.VISIBLE);
//                holder.dateTv.setText(DateUtils.getFormattedDate(report.getCreatedAt()));
                holder.dateTv.setText(DateUtils.getNormalDateString(report.getCreatedAt()));
            }
        } else {
            holder.dateTv.setVisibility(View.VISIBLE);
//            holder.dateTv.setText(DateUtils.getFormattedDate(report.getCreatedAt()));
            holder.dateTv.setText(DateUtils.getNormalDateString(report.getCreatedAt()));
        }
//        holder.reportItemTime.setText(DateUtils.getTime(report.getCreatedAt()));
        holder.reportItemTime.setText(DateUtils.getFormattedOnlyTime(report.getCreatedAt()));

        double total_sale = Double.parseDouble(report.getSaleAmount()) / 100.0;
        double total_refund = Double.parseDouble(report.getRefundAmount()) / 100.0;
        double total_completion = Double.parseDouble(report.getCompletionAmount()) / 100.0;
        double total_gratuity = Double.parseDouble(report.getGratuityAmount()) / 100.0;

//        double totalAmt = total_sale - total_refund + total_completion + total_gratuity;
        double totalAmt = total_sale - total_refund + total_completion;

        holder.reportItemTotal.setText(String.format("£%s", String.format("%.2f", totalAmt)));
//        holder.reportItemTotal.setText(String.format("£ %s", totalAmt));
        int itemPosition = position;
//        holder.reportDetailsBtn.setOnClickListener(new View.OnClickListener() {
        holder.reportMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReportItemClickListener.onReportItemClickListener(itemPosition, report);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public void itemChanged(List<Report> reportList) {
        this.reportList = reportList;
        notifyDataSetChanged();
    }

    public void setOnReportItemClickListener(OnReportItemClickListener onReportItemClickListener) {
        this.onReportItemClickListener = onReportItemClickListener;
    }

    interface OnReportItemClickListener {
        void onReportItemClickListener(int position, Report report);
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView reportItemTime, reportItemTotal, dateTv;
        RelativeLayout reportMainLayout;
        LinearLayout reportDetailsBtn;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportItemTime = itemView.findViewById(R.id.report_item_time);
            reportItemTotal = itemView.findViewById(R.id.report_item_total);
            dateTv = itemView.findViewById(R.id.date_tv);
            reportMainLayout = itemView.findViewById(R.id.report_main_layout);
            reportDetailsBtn = itemView.findViewById(R.id.report_details_btn);
        }
    }

}
