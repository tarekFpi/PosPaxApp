package com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.sub_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.transactions.TransactionAdapter;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.FileUtils;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CompletionAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TransactionAdapter";
    private static final int VIEW_TYPE_CANCELED_TRN = 1;
    private static final int VIEW_TYPE_SUCCESS_TRN = 2;

    public Context mContext;
    private List<TransactionListResult> mMessageList;
    private TransactionAdapter.OnTransactionItemClickListener onTransactionItemClickListener;

    public CompletionAdapter(Context context, List<TransactionListResult> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        TransactionListResult message = (TransactionListResult) mMessageList.get(position);
        if (message.isCancelled()) {
            // If the current user is the sender of the message
            return VIEW_TYPE_CANCELED_TRN;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_SUCCESS_TRN;
        }
    }


    // Inflates the appropriate layout according to the ViewType.
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CANCELED_TRN) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.completion_waiting_item, parent, false);
            return new CompletionAdapter.CancelTransactionHolder(view, parent);
        } else if (viewType == VIEW_TYPE_SUCCESS_TRN) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_success_model_item, parent, false);
            return new CompletionAdapter.SuccessTransactionHolder(view);
        }
        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        TransactionListResult nextMessage = null;
        try {
            nextMessage = mMessageList.get(position - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TransactionListResult message = (TransactionListResult) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CANCELED_TRN:
                ((CompletionAdapter.CancelTransactionHolder) holder).bind(message, nextMessage, position);
                break;
            case VIEW_TYPE_SUCCESS_TRN:
                ((CompletionAdapter.SuccessTransactionHolder) holder).bind(message, nextMessage, position);
        }
    }

    private class CancelTransactionHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainItemView;
        TextView cardNoTV, amtTV, statusTv, timeTV, dateTv;
        ImageView cardLogo;

        CancelTransactionHolder(View itemView, ViewGroup parent) {
            super(itemView);
            mainItemView = itemView.findViewById(R.id.cancel_main_rl);
            amtTV = itemView.findViewById(R.id.cancel_amt_tv);
            timeTV = itemView.findViewById(R.id.cancel_item_time_tv);
            cardNoTV = itemView.findViewById(R.id.cancel_item_card_no);
            cardLogo = itemView.findViewById(R.id.cancel_payment_icon);
            statusTv = itemView.findViewById(R.id.cancel_tnx_status);
            dateTv = itemView.findViewById(R.id.date_cItem_tv);
        }

        void bind(TransactionListResult transaction, TransactionListResult previousTransaction, int position) {
            if (!transaction.getPan().isEmpty())
                cardNoTV.setText(transaction.getPan());
            amtTV.setText(String.format("£ %s", transaction.getAmount()));
            timeTV.setText(DateUtils.getTime(transaction.getCreatedAt()));
            if (previousTransaction != null) {
                if (FileUtils.getDateFromString(previousTransaction.getCreatedAt()).equals(FileUtils.getDateFromString(transaction.getCreatedAt()))) {
                    dateTv.setVisibility(View.GONE);
                } else {
                    dateTv.setVisibility(View.VISIBLE);
                    dateTv.setText(DateUtils.getFormattedDate(transaction.getCreatedAt()));
                }
            } else {
                dateTv.setVisibility(View.VISIBLE);
                dateTv.setText(DateUtils.getFormattedDate(transaction.getCreatedAt()));
            }
            if (transaction.getCardType() != null && !transaction.getCardType().isEmpty()) {
                switch (transaction.getCardType()) {
                    case "Visa":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.visa_logo));
                        break;
                    default:
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.unknown_card));
                }
            } else {
                cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.unknown_card));
            }

            mainItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTransactionItemClickListener.onProgressItemClickListener(position,transaction);
                }
            });
        }
    }

    private class SuccessTransactionHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainItemView;
        TextView cardNoTV, amtTv, timeTv, statusTv ,dateTv;
        ImageView cardLogo;

        SuccessTransactionHolder(View itemView) {
            super(itemView);
            mainItemView = itemView.findViewById(R.id.success_main_rl);
            cardNoTV = itemView.findViewById(R.id.success_card_no_tv);
            amtTv = itemView.findViewById(R.id.success_amt_tv);
            timeTv = itemView.findViewById(R.id.success_card_time_tv);
            cardLogo = itemView.findViewById(R.id.success_payment_icon);
            statusTv = itemView.findViewById(R.id.success_tnx_status);
            dateTv = itemView.findViewById(R.id.date_sItem_tv);
        }

        void bind(TransactionListResult transaction, TransactionListResult previousTransaction, int position) {
            cardNoTV.setText(transaction.getPan());
            amtTv.setText(String.format("£ %s", transaction.getAmount()));
            timeTv.setText(DateUtils.getTime(transaction.getCreatedAt()));
            if (previousTransaction != null) {
                if (FileUtils.getDateFromString(previousTransaction.getCreatedAt()).equals(FileUtils.getDateFromString(transaction.getCreatedAt()))) {
                    dateTv.setVisibility(View.GONE);
                } else {
                    dateTv.setVisibility(View.VISIBLE);
                    dateTv.setText(DateUtils.getFormattedDate(transaction.getCreatedAt()));
                }
            } else {
                dateTv.setVisibility(View.VISIBLE);
                dateTv.setText(DateUtils.getFormattedDate(transaction.getCreatedAt()));
            }
            if (transaction.getCardType() != null && !transaction.getCardType().isEmpty()) {
                switch (transaction.getCardType()) {
                    case "Visa":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.visa_logo));
                        break;
                    default:
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.unknown_card));
                }
            } else {
                cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.unknown_card));
            }
            switch (transaction.getStatus()) {
                case 0:
                    statusTv.setText(R.string.declined);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.yellow_radious_bg));
                    break;
                case 1:
                    statusTv.setText(R.string.approved);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.green_radious_bg));
                    break;
                case 2:
                    statusTv.setText(R.string.canceled);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.red_radious_bg));
                    break;
            }
            mainItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTransactionItemClickListener.onProgressItemClickListener(position,transaction);
                }
            });
        }
    }

    public interface OnTransactionItemClickListener {
        void onProgressItemClickListener(int position);
    }

    public void setOnTransactionItemClickListener(TransactionAdapter.OnTransactionItemClickListener onTransactionItemClickListener) {
        this.onTransactionItemClickListener = onTransactionItemClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void itemChanged(List<TransactionListResult> mMessageList) {
        this.mMessageList = mMessageList;
        notifyDataSetChanged();
    }

    public void notifyItemChanged(List<TransactionListResult> mMessageList, int position) {
        this.mMessageList = mMessageList;
        notifyItemChanged(position);
    }
}
