package com.paymentsave.paymentsave.coreapp.fragments.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.FileUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResult;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter {

    private static final String TAG = "TransactionAdapter";
    private static final int VIEW_TYPE_CANCELED_TRN = 1;
    private static final int VIEW_TYPE_SUCCESS_TRN = 2;
    private static final int VIEW_TYPE_LOAD_MORE = 3;

    public Context mContext;
    private List<TransactionListResult> mMessageList;
    private OnTransactionItemClickListener onTransactionItemClickListener;
    private OnSeeMoreBtnClickListener onSeeMoreBtnClickListener;

    public TransactionAdapter(Context context, List<TransactionListResult> messageList) {
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

        if (message.getId() != 0) {
            if (message.isCancelled()) {
                // If the current user is the sender of the message
                return VIEW_TYPE_CANCELED_TRN;
            } else {
                // If some other user sent the message
                return VIEW_TYPE_SUCCESS_TRN;
            }
        } else
            return VIEW_TYPE_LOAD_MORE;

    }


    /// Inflates the appropriate layout according to the ViewType.
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_CANCELED_TRN) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_canceled_model_item, parent, false);
            return new CancelTransactionHolder(view, parent);
        } else if (viewType == VIEW_TYPE_SUCCESS_TRN) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_success_model_item, parent, false);
            return new SuccessTransactionHolder(view);
        } else if (viewType == VIEW_TYPE_LOAD_MORE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.load_more_model_item, parent, false);
            return new LoadingMoreItemHolder(view);
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
            Log.e(TAG, "onBindViewHolder: " + e.getMessage());
            // position 0 has no previous item — do not send that expected bound as ERROR.
            if (!(e instanceof IndexOutOfBoundsException)) {
                // Telemetry: TransactionAdapter.onBindViewHolder — previous item lookup failed.
                logReceiveError("onBindViewHolder", String.valueOf(e.getMessage()), e);
            }
        }

        try {
            TransactionListResult message = (TransactionListResult) mMessageList.get(position);

            switch (holder.getItemViewType()) {
                case VIEW_TYPE_CANCELED_TRN:
                    ((CancelTransactionHolder) holder).bind(message, nextMessage, position);
                    break;
                case VIEW_TYPE_SUCCESS_TRN:
                    ((SuccessTransactionHolder) holder).bind(message, nextMessage, position);
                    break;
                case VIEW_TYPE_LOAD_MORE:
                    ((LoadingMoreItemHolder) holder).bind(message, nextMessage, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: TransactionAdapter.onBindViewHolder — failed to bind transaction row.
            logReceiveError("onBindViewHolder", String.valueOf(e.getMessage()), e);
        }
    }

    public void setOnTransactionItemClickListener(OnTransactionItemClickListener onTransactionItemClickListener) {
        this.onTransactionItemClickListener = onTransactionItemClickListener;
    }

    public void setOnSeeMoreItemClickListener(OnSeeMoreBtnClickListener onSeeMoreBtnClickListener) {
        this.onSeeMoreBtnClickListener = onSeeMoreBtnClickListener;
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

    public interface OnTransactionItemClickListener {
        void onProgressItemClickListener(int position, TransactionListResult item);
    }

    public interface OnSeeMoreBtnClickListener {
        void onSeeMoreClickListener();
    }

    private class CancelTransactionHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainItemView;
        TextView cardNoTV, amtTV, statusTv, timeTV, dateTv;
        ImageView cardLogo, splitBillIv, reversalIv;

        CancelTransactionHolder(View itemView, ViewGroup parent) {
            super(itemView);
            mainItemView = itemView.findViewById(R.id.cancel_main_rl);
            amtTV = itemView.findViewById(R.id.cancel_amt_tv);
            timeTV = itemView.findViewById(R.id.cancel_item_time_tv);
            cardNoTV = itemView.findViewById(R.id.cancel_item_card_no);
            cardLogo = itemView.findViewById(R.id.cancel_payment_icon);
            statusTv = itemView.findViewById(R.id.cancel_tnx_status);
            dateTv = itemView.findViewById(R.id.date_cItem_tv);
            splitBillIv = itemView.findViewById(R.id.split_bill_iv);
            reversalIv = itemView.findViewById(R.id.reversal_iv);
        }

        void bind(TransactionListResult transaction, TransactionListResult previousTransaction, int position) {

            if (transaction != null && transaction.getPan() != null && !transaction.getPan().isEmpty())
                cardNoTV.setText(transaction.getPan());
            else cardNoTV.setText("*************");


            if (transaction.getTransactionType().equals("SALE_AUTO") || transaction.getTransactionType().equals("CASHBACK_AUTO")) {
                BigDecimal transactionAmount = new BigDecimal(transaction.getAmount());
                BigDecimal gratuityAmount = new BigDecimal(transaction.getGratuityAmount());
                BigDecimal cashbackAmount;
                if (transaction.getCashbackAmount() == null) {
                    cashbackAmount = new BigDecimal("0.00");
                } else {
                    cashbackAmount = new BigDecimal(transaction.getCashbackAmount());
                }
                BigDecimal mainAmt = transactionAmount.add(gratuityAmount);
                BigDecimal totalAmt = mainAmt.add(cashbackAmount);
                amtTV.setText(String.format("£ %.2f", totalAmt));
            } else {
                amtTV.setText(String.format("£ %s", transaction.getAmount()));
            }

            if (transaction.getTransactionType().equals("REFUND_AUTO") || transaction.getTransactionType().equals("REFUND")) {
                reversalIv.setVisibility(View.VISIBLE);
            } else {
                reversalIv.setVisibility(View.GONE);
            }

            timeTV.setText(DateUtils.getTime(transaction.getCreatedAt()));
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
                case 3:
                    statusTv.setText(R.string.refunded);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.red_radious_bg));
                    break;
                case 4:
                    statusTv.setText(R.string.voided);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.red_radious_bg));
                    break;
                case 5:
                    statusTv.setText(R.string.pending);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.red_radious_bg));
                    break;

            }

            if (previousTransaction != null && previousTransaction.getId() != 0) {
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
                    case "Mastercard":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.master_logo));
                        break;
                    case "AMEX":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.amex_logo));
                        break;
                    case "DISCOVER":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.discover_card_icon));
                        break;
                    case "UPI":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.upi_card_icon));
                        break;
                    default:
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.unknown_card));
                }
            } else {
                cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.unknown_card));
            }

            if (transaction.getSplitBillId() != null && !transaction.getSplitBillId().isEmpty()) {
                splitBillIv.setVisibility(View.VISIBLE);
            } else {
                splitBillIv.setVisibility(View.GONE);
            }

            mainItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTransactionItemClickListener.onProgressItemClickListener(position, transaction);
                }
            });
        }
    }

    private class SuccessTransactionHolder extends RecyclerView.ViewHolder {
        RelativeLayout mainItemView;
        TextView cardNoTV, amtTv, timeTv, statusTv, dateTv;
        ImageView cardLogo, splitBillIv, reversalIv;

        SuccessTransactionHolder(View itemView) {
            super(itemView);
            mainItemView = itemView.findViewById(R.id.success_main_rl);
            cardNoTV = itemView.findViewById(R.id.success_card_no_tv);
            amtTv = itemView.findViewById(R.id.success_amt_tv);
            timeTv = itemView.findViewById(R.id.success_card_time_tv);
            cardLogo = itemView.findViewById(R.id.success_payment_icon);
            statusTv = itemView.findViewById(R.id.success_tnx_status);
            dateTv = itemView.findViewById(R.id.date_sItem_tv);
            splitBillIv = itemView.findViewById(R.id.split_bill_iv);
            reversalIv = itemView.findViewById(R.id.reversal_iv);
        }

        void bind(TransactionListResult transaction, TransactionListResult previousTransaction, int position) {
            if (transaction != null && transaction.getPan() != null && !transaction.getPan().isEmpty())
                cardNoTV.setText(transaction.getPan());
            else cardNoTV.setText("*************");

            if (transaction.getTransactionType().equals("SALE_AUTO") || transaction.getTransactionType().equals("CASHBACK_AUTO")) {
                BigDecimal transactionAmount = new BigDecimal(transaction.getAmount());
                BigDecimal gratuityAmount = new BigDecimal(transaction.getGratuityAmount());
                BigDecimal cashbackAmount;
                if (transaction.getCashbackAmount() == null) {
                    cashbackAmount = new BigDecimal("0.00");
                } else {
                    cashbackAmount = new BigDecimal(transaction.getCashbackAmount());
                }
                BigDecimal mainAmt = transactionAmount.add(gratuityAmount);
                BigDecimal totalAmt = mainAmt.add(cashbackAmount);
                amtTv.setText(String.format("£ %.2f", totalAmt));
            } else {
                amtTv.setText(String.format("£ %s", transaction.getAmount()));
            }

            if (transaction.getTransactionType().equals("REFUND_AUTO") || transaction.getTransactionType().equals("REFUND")) {
                reversalIv.setVisibility(View.VISIBLE);
            } else {
                reversalIv.setVisibility(View.GONE);
            }

            timeTv.setText(DateUtils.getTime(transaction.getCreatedAt()));

            if (previousTransaction != null && previousTransaction.getId() != 0) {
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
                    case "Mastercard":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.master_logo));
                        break;
                    case "AMEX":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.amex_logo));
                        break;
                    case "DISCOVER":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.discover_card_icon));
                        break;
                    case "UPI":
                        cardLogo.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.upi_card_icon));
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
                case 3:
                    statusTv.setText(R.string.refunded);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.red_radious_bg));
                    break;
                case 4:
                    statusTv.setText(R.string.voided);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.red_radious_bg));
                    break;
                case 5:
                    statusTv.setText(R.string.pending);
                    statusTv.setTextColor(mContext.getResources().getColor(R.color.white));
                    statusTv.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.red_radious_bg));
                    break;
            }

            if (transaction.getSplitBillId() != null && !transaction.getSplitBillId().isEmpty()) {
                splitBillIv.setVisibility(View.VISIBLE);
            } else {
                splitBillIv.setVisibility(View.GONE);
            }
            mainItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTransactionItemClickListener.onProgressItemClickListener(position, transaction);
                }
            });
        }
    }

    private class LoadingMoreItemHolder extends RecyclerView.ViewHolder {

        public Button seeMoreBtn;

        public LoadingMoreItemHolder(@NonNull View itemView) {
            super(itemView);
            seeMoreBtn = itemView.findViewById(R.id.see_more_btn);
        }

        void bind(TransactionListResult transaction, TransactionListResult previousTransaction, int position) {
            seeMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSeeMoreBtnClickListener.onSeeMoreClickListener();
                }
            });
        }
    }

    /**
     * Sends an adapter failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code TransactionAdapter.onBindViewHolder}.
     * {@code message} must already be a string.
     */
    private void logReceiveError(String functionPath, String message, Throwable throwable) {
        Context context = mContext;
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