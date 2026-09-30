package com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.sub_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.transactions.TransactionAdapter;
import com.paymentsave.paymentsave.coreapp.fragments.viewModels.TransactionViewModel;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResponse;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CompletionFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CompletionFragment";

    boolean loading, isDatePickerShowing = false;
    private FloatingActionButton backBtn;
    private ProgressBar completionProgressBar;
    private String selected_tab = "0";
    private ColorStateList def;
    private TextView item1;
    private TextView item2;
    private TextView select;
    private RecyclerView completionRecyclerView;
    private List<TransactionListResult> transactionItems = new ArrayList<>();
    private CompletionAdapter adapter;
    private String transactionNextUrl;
    private TransactionViewModel viewModel;
    private LinearLayout emptyBodyLayout;
    private Button dateFilterBtn;
    private MaterialDatePicker<Pair<Long, Long>> materialDatePicker;
    private Date fromDateFilter, toDateFilter;

    public CompletionFragment() {
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
        View view = inflater.inflate(R.layout.fragment_completion, container, false);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        completionProgressBar = view.findViewById(R.id.completion_progress_bar);
        backBtn = view.findViewById(R.id.completion_back_button);
        emptyBodyLayout = view.findViewById(R.id.empty_body_layout);
        completionRecyclerView = view.findViewById(R.id.completion_recycler_view);
        dateFilterBtn = view.findViewById(R.id.date_filter_btn);
        backBtn.setOnClickListener(this);
        item1 = view.findViewById(R.id.item1);
        item2 = view.findViewById(R.id.item2);
        select = view.findViewById(R.id.select);
        def = item2.getTextColors();
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        dateFilterBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setStackFromEnd(false);
        completionRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CompletionAdapter(getContext(), transactionItems);
        completionRecyclerView.setAdapter(adapter);
        adapter.setOnTransactionItemClickListener(new TransactionAdapter.OnTransactionItemClickListener() {
            @Override
            public void onProgressItemClickListener(int position, TransactionListResult transaction) {

            }
        });

        completionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // Scroll Condition Apply
                if (!loading) {
                    if (transactionNextUrl != null && !transactionNextUrl.equals("")) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == transactionItems.size() - 1) {
                            loading = true;
                            getNextTransactions(transactionNextUrl);
                        }
                    }
                }
            }
        });

        // Date Range Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTheme(R.style.MaterialCalendarTheme);
        materialDateBuilder.setTitleText("SELECT A DATE");
        materialDatePicker = materialDateBuilder.build();
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    try {
                        Date[] dates = DateUtils.parseDateRangeFromDatePicker(materialDatePicker.getHeaderText());
                        fromDateFilter = dates[0];
                        toDateFilter = dates[1];
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Telemetry: CompletionFragment.onViewCreated.materialDatePicker — failed to parse selected date range.
                        logReceiveError("onViewCreated.materialDatePicker", String.valueOf(e.getMessage()), e);
                    }
                    dateFilterBtn.setText(materialDatePicker.getHeaderText());
                    if (Objects.equals(selected_tab, "0"))
                        getTransactionData("PREAUTH_AUTO");
                    else
                        getTransactionData("TOPUPPREAUTH");
                });
        materialDatePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDatePickerShowing = false;
            }
        });

        setDefaultLast3Days();
        updateDateRangeText();

        // Get Transaction Data's
        getTransactionData("PREAUTH_AUTO");
    }

    private void updateDateRangeText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String from = sdf.format(fromDateFilter);
        String to = sdf.format(toDateFilter);

        // Example: "01 Sep 2025 - 03 Sep 2025"
        dateFilterBtn.setText(from + " - " + to);
    }

    private void setDefaultLast3Days() {
        // End = today 23:59:59.999 (local time)
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);

        // Start = end - 2 days, set to 00:00:00.000 (local time)
        Calendar start = (Calendar) end.clone();
        start.add(Calendar.DAY_OF_YEAR, -2);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        fromDateFilter = start.getTime();
        toDateFilter = end.getTime();
    }

    private void getNextTransactions(String url) {
        String token = SharedHelper.getToken(requireContext());
        viewModel.getNextTransactionsList(url, token).observe(this, new Observer<TransactionListResponse>() {
            @Override
            public void onChanged(TransactionListResponse transactionListResponse) {
                loading = false;
                if (transactionListResponse != null) {
                    if (transactionListResponse.getData().getNext() != null) {
                        transactionNextUrl = transactionListResponse.getData().getNext();
                    } else {
                        transactionNextUrl = null;
                    }
                    if (transactionListResponse.getData().getCount() > 0) {
                        transactionItems.addAll(transactionListResponse.getData().getResults());
                    }
                }
                adapter.itemChanged(transactionItems);
            }
        });
    }

    private void getTransactionData(String type) {
        emptyBodyLayout.setVisibility(View.GONE);
        completionProgressBar.setVisibility(View.VISIBLE);
        transactionItems.clear();
        adapter.itemChanged(transactionItems);
        String token = SharedHelper.getToken(requireContext());
        HashMap<String, String> map = new HashMap<>();
        map.put("transaction_type", type);
        if (fromDateFilter != null) {
            map.put("from_date", DateUtils.parseSystemStringDate(fromDateFilter.toString()));
        }
        if (toDateFilter != null) {
            map.put("to_date", DateUtils.parseSystemStringDate(toDateFilter.toString()));
        }
        map.put("terminal_id", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
        map.put("merchant", String.valueOf(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.USER_ID)));
        viewModel.getTransactionsList(map, token).observe(requireActivity(), new Observer<TransactionListResponse>() {
            @Override
            public void onChanged(TransactionListResponse transactionListResponse) {
                completionProgressBar.setVisibility(View.GONE);
                if (transactionListResponse != null) {
                    if (transactionListResponse.getData().getNext() != null) {
                        transactionNextUrl = transactionListResponse.getData().getNext();
                    } else {
                        transactionNextUrl = null;
                    }
                    if (transactionListResponse.getData().getCount() > 0) {
                        transactionItems.addAll(transactionListResponse.getData().getResults());
                    } else {
                        emptyBodyLayout.setVisibility(View.VISIBLE);
                    }
                }
                adapter.itemChanged(transactionItems);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_filter_btn:
                if (!isDatePickerShowing) {
                    try {
                        isDatePickerShowing = true;
                        materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    } catch (Exception e) {
                        e.printStackTrace();

                        /// Telemetry: CompletionFragment.onClick.date_filter_btn — failed to show date picker.
                        logReceiveError("onClick.date_filter_btn", String.valueOf(e.getMessage()), e);
                    }
                }
                break;
            case R.id.completion_back_button:
                Navigation.findNavController(v).popBackStack();
                break;
            case R.id.item1:
                selected_tab = "0";
                select.animate().x(0).setDuration(100);
                item1.setTextColor(Color.WHITE);
                item2.setTextColor(def);
                getTransactionData("PREAUTH_AUTO");
                break;

            case R.id.item2:
                selected_tab = "1";
                item1.setTextColor(def);
                item2.setTextColor(Color.WHITE);
                int size = item2.getWidth();
                select.animate().x(size).setDuration(100);
                getTransactionData("TOPUPPREAUTH");
                break;
        }
    }

    /**
     * Sends a fragment failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code CompletionFragment.onClick.date_filter_btn}.
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