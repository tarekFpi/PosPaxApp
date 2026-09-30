package com.paymentsave.paymentsave.coreapp.fragments.transactions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.models.TransactionType;

import java.util.List;

public class TransactionTypeAdapter extends RecyclerView.Adapter<TransactionTypeAdapter.TransactionTypeViewHolder> {

    private Context context;
    private List<TransactionType> items;

    private OnTypeItemClickListener onTypeItemClickListener;

    public TransactionTypeAdapter(Context context, List<TransactionType> items) {
        this.context = context;
        this.items = items;
    }

    public class TransactionTypeViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public TransactionTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.filter_tnx_type_title);
        }
    }

    @NonNull
    @Override
    public TransactionTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tnx_type_filter_item, parent, false);
        return new TransactionTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionTypeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TransactionType transactionType = items.get(position);
        holder.title.setText(transactionType.getTitle());
        if (transactionType.isSelected()) {
            holder.title.setTextColor(context.getResources().getColor(R.color.white));
            holder.title.setBackground(AppCompatResources.getDrawable(context, R.drawable.radius_btn_bg));
        } else {
            holder.title.setTextColor(context.getResources().getColor(R.color.text_color));
            holder.title.setBackground(AppCompatResources.getDrawable(context, R.drawable.radius_gray));
        }
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTypeItemClickListener.OnTypeItemClickListener(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void itemChanged(List<TransactionType> transactionTypeList) {
        this.items = transactionTypeList;
        notifyDataSetChanged();
    }

    public void itemChanged(List<TransactionType> transactionTypeList,int position) {
        this.items = transactionTypeList;
        notifyItemChanged(position);
    }

    public interface OnTypeItemClickListener {
        void OnTypeItemClickListener(int position);
    }

    public void setOnTypeItemClickListener(OnTypeItemClickListener onTypeItemClickListener) {
        this.onTypeItemClickListener = onTypeItemClickListener;
    }

}
