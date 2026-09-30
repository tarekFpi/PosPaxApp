package com.paymentsave.paymentsave.coreapp.activities.gratuity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.models.GratuityOption;

import java.util.List;

public class GratuityAdapter extends ArrayAdapter<GratuityOption> {
    private static final String TAG = "GratuityAdapter";
    private final Context context;
    private final List<GratuityOption> options;
    private OnGratuityItemClickListener gratuityItemClickListener;

    public GratuityAdapter(Context context, List<GratuityOption> options) {
        super(context, 0, options);
        this.context = context;
        this.options = options;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        GratuityOption option = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gratuity_option, parent, false);
        }

        Button button = convertView.findViewById(R.id.gratuity_item_btn);
        if (option.getLabel().equals("PERCENTAGE")) {
            float perc = (float) Float.parseFloat(option.getAmount());
            Float floatFinal = Float.parseFloat(String.valueOf(option.getMainAmount() * perc / 100));
            String finalValue = String.format("%.02f", floatFinal);
            button.setText(String.format("%s%% (£%s)", perc, finalValue));
        } else {
            button.setText(String.format("£%s", option.getAmount()));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gratuityItemClickListener.onProgressItemClickListener(position, option);
            }
        });

        return convertView;
    }

    public void setGratuityItemClickListener(OnGratuityItemClickListener gratuityItemClickListener) {
        this.gratuityItemClickListener = gratuityItemClickListener;
    }

    public interface OnGratuityItemClickListener {
        void onProgressItemClickListener(int position, GratuityOption item);
    }
}

