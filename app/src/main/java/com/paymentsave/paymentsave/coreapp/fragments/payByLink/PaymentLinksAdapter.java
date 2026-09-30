package com.paymentsave.paymentsave.coreapp.fragments.payByLink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.FileUtils;
import com.paymentsave.paymentsave.responses.PBLResponse.LinkItem;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;

import java.util.List;

public class PaymentLinksAdapter extends RecyclerView.Adapter<PaymentLinksAdapter.ReportViewHolder> {
    private static final String TAG = "ReportAdapter";
    private Context context;
    private List<LinkItem> linkList;
    private OnTnxItemClickListener onReportItemClickListener;

    public PaymentLinksAdapter(Context context, List<LinkItem> linkList) {
        this.context = context;
        this.linkList = linkList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pbl_link_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LinkItem linkItem = linkList.get(position);
        LinkItem previousItem = null;
        try {
            previousItem = linkList.get(position - 1);
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
        }

        holder.dateTv.setText(DateUtils.formatApiDateTime(linkItem.getCreatedAt()));
        holder.midTv.setText(String.format("Business MID: %s", linkItem.getBusinessMid()));
        holder.utiTv.setText(String.format("UTI: %s", linkItem.getUti()));
        holder.amountTv.setText(String.format("£ %s", linkItem.getAmount()));
        holder.statusTv.setText(linkItem.getStatus());

        if (linkItem.getStatus().equals("success")){
            holder.statusIv.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.green_circle));
        } else if (linkItem.getStatus().equals("pending")){
            holder.statusIv.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.yellow_radious_bg));
        } else {
            holder.statusIv.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.red_circle));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReportItemClickListener.onReportItemClickListener(position, linkItem);
            }
        });



//        if (previousReport != null) {
//            if (FileUtils.getDateFromString(report.getCreatedAt()).equals(FileUtils.getDateFromString(previousReport.getCreatedAt()))) {
//                holder.dateTv.setVisibility(View.VISIBLE);
////                holder.dateTv.setVisibility(View.GONE);
//                holder.dateTv.setText(DateUtils.getNormalDateString(report.getCreatedAt()));
//            } else {
//                holder.dateTv.setVisibility(View.VISIBLE);
////                holder.dateTv.setText(DateUtils.getFormattedDate(report.getCreatedAt()));
//                holder.dateTv.setText(DateUtils.getNormalDateString(report.getCreatedAt()));
//            }
//        }
//        else {
//            holder.dateTv.setVisibility(View.VISIBLE);
////            holder.dateTv.setText(DateUtils.getFormattedDate(report.getCreatedAt()));
//            holder.dateTv.setText(DateUtils.getNormalDateString(report.getCreatedAt()));
//        }
////        holder.reportItemTime.setText(DateUtils.getTime(report.getCreatedAt()));
//        holder.reportItemTime.setText(DateUtils.getFormattedOnlyTime(report.getCreatedAt()));
//
//        double total_sale = Double.parseDouble(report.getSaleAmount()) / 100.0;
//        double total_refund = Double.parseDouble(report.getRefundAmount()) / 100.0;
//        double total_completion = Double.parseDouble(report.getCompletionAmount()) / 100.0;
//        double total_gratuity = Double.parseDouble(report.getGratuityAmount()) / 100.0;
//        double totalAmt = total_sale - total_refund + total_completion + total_gratuity;
//        holder.reportItemTotal.setText(String.format("£%s", String.format("%.2f", totalAmt)));
////        holder.reportItemTotal.setText(String.format("£ %s", totalAmt));
//        int itemPosition = position;
////        holder.reportDetailsBtn.setOnClickListener(new View.OnClickListener() {
//        holder.reportMainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onReportItemClickListener.onReportItemClickListener(itemPosition, report);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return linkList.size();
    }

    public void itemChanged(List<LinkItem> reportList) {
        this.linkList = reportList;
        notifyDataSetChanged();
    }

    public void setOnTnxItemClickListener(OnTnxItemClickListener onReportItemClickListener) {
        this.onReportItemClickListener = onReportItemClickListener;
    }

    interface OnTnxItemClickListener {
        void onReportItemClickListener(int position, LinkItem linkItem);
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv, midTv, utiTv, amountTv, statusTv;
        ImageView statusIv;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.date_tv);
            midTv = itemView.findViewById(R.id.mid_tv);
            utiTv = itemView.findViewById(R.id.uti_tv);
            amountTv = itemView.findViewById(R.id.amount_tv);
            statusTv = itemView.findViewById(R.id.status_tv);
            statusIv = itemView.findViewById(R.id.status_iv);

        }
    }

}
