package com.paymentsave.paymentsave.coreapp.activities.cashback;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.annotation.NonNull;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.models.CashbackOption;
import java.util.List;

public class CashbackAdapter extends ArrayAdapter<CashbackOption> {
    private static final String TAG = "CashbackAdapter";
    private final Context context;
    private final List<CashbackOption> options;
    private CashbackAdapter.OnCashbackItemClickListener gratuityItemClickListener;

    public CashbackAdapter(Context context, List<CashbackOption> options) {
        super(context, 0, options);
        this.context = context;
        this.options = options;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CashbackOption option = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cashback_option, parent, false);
        }

        Button button = convertView.findViewById(R.id.cashback_item_btn);
        assert option != null;
        button.setText(String.format("£%.02f", Float.parseFloat(option.getAmount())));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gratuityItemClickListener.onProgressItemClickListener(position, option);
            }
        });

        return convertView;
    }

    public void setCashbackItemClickListener(CashbackAdapter.OnCashbackItemClickListener gratuityItemClickListener) {
        this.gratuityItemClickListener = gratuityItemClickListener;
    }

    public interface OnCashbackItemClickListener {
        void onProgressItemClickListener(int position, CashbackOption item);
    }
}