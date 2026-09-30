package com.paymentsave.paymentsave.coreapp.fragments.transactions;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eft.libpositive.PosIntegrate;
import com.eft.libpositive.PositiveLib;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.viewModels.TransactionViewModel;
import com.paymentsave.paymentsave.coreapp.models.TransactionType;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.ReportListResponse.Report;
import com.paymentsave.paymentsave.responses.ReportListResponse.ReportListResponse;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResponse;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResult;

import org.checkerframework.checker.index.qual.Positive;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class TransactionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransactionFragment";
    String transactionNextUrl;
    String reportNextUrl;
    boolean loading = false;
    private TextView titleText;
    private EditText transactionSearchEt;
    private String searchText = "";
    private ProgressBar progressBar;
    private TransactionViewModel viewModel;
    private RecyclerView transactionRecyclerView, tnxTypeRecycler, reportRecyclerView;
    private TransactionAdapter adapter;
    private TransactionTypeAdapter typeAdapter;
    private List<TransactionType> transactionTypeList = new ArrayList<>();
    private TextView pageCountText;
    private LinearLayout pageCountView, no_history_illustration, transactionDateLayout;
    private List<TransactionListResult> transactionListResultList = new ArrayList<>();
    private List<Report> reportList = new ArrayList<>();
    private ReportAdapter reportAdapter;
    private BottomSheetDialog filterDialog;
    private ImageButton filterBtn;
    private DatePickerDialog mDialog;
    private Calendar mCalendar;
    private Button filterSelectedDateRemoveBtn, durationFilterBtn;
    private Button filterDateChooseBtn;
    private String transType = "";
    private MaterialDatePicker materialDatePicker;
    private String filterFromDate, filterToDate;
    private Date fromDateFilter, toDateFilter;
    private CheckBox slot1, slot2, slot3, slot4;
    private final TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!charSequence.toString().isEmpty()) searchText = charSequence.toString();
            else searchText = "";
            getTransactionData();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private boolean isDatePickerShowing = false;
    private String[] filterSlots;
    private String selectedDurationFilter = "Last 3 Days";
    private String appliedDurationFilter = "Last 3 Days";


    public TransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateTransactionTypeData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        titleText = view.findViewById(R.id.title_text);
        no_history_illustration = view.findViewById(R.id.no_history_illustration);
        transactionSearchEt = view.findViewById(R.id.transaction_search_et);
        pageCountText = view.findViewById(R.id.page_count_txt);
        pageCountView = view.findViewById(R.id.page_count_view);
        filterBtn = view.findViewById(R.id.filter_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        titleText.setText(String.format("Transactions (%s)", appliedDurationFilter));
        filterBtn.setOnClickListener(this);
        filterDialog = MainUtils.getTnxFilterBottomSheet(requireActivity(), getContext());
        Button filterApplyBtn = filterDialog.findViewById(R.id.filter_apply_btn);
        transactionDateLayout = filterDialog.findViewById(R.id.transaction_date_layout);
        filterDateChooseBtn = filterDialog.findViewById(R.id.filter_choose_date_btn);
        filterSelectedDateRemoveBtn = filterDialog.findViewById(R.id.selected_date_remove_btn);
        durationFilterBtn = filterDialog.findViewById(R.id.filter_duration_btn);
        assert durationFilterBtn != null;
        durationFilterBtn.setText(appliedDurationFilter);

        slot1 = filterDialog.findViewById(R.id.slot1);
        slot2 = filterDialog.findViewById(R.id.slot2);
        slot3 = filterDialog.findViewById(R.id.slot3);
        slot4 = filterDialog.findViewById(R.id.slot4);
        Button filterClearBtn = filterDialog.findViewById(R.id.filter_clear_btn);
        ImageButton filterCloseBtn = filterDialog.findViewById(R.id.filter_close_btn);


        assert filterCloseBtn != null;
        filterCloseBtn.setOnClickListener(this);
        assert filterApplyBtn != null;
        filterApplyBtn.setOnClickListener(this);
        assert filterClearBtn != null;
        filterClearBtn.setOnClickListener(this);
        assert filterDateChooseBtn != null;
        filterDateChooseBtn.setOnClickListener(this);
        filterSelectedDateRemoveBtn.setOnClickListener(this);
        durationFilterBtn.setOnClickListener(this);


        transactionSearchEt.addTextChangedListener(searchTextWatcher);
        progressBar = view.findViewById(R.id.transaction_list_pb);

        tnxTypeRecycler = view.findViewById(R.id.tnx_type_recycler);
        LinearLayoutManager layoutHManager = new LinearLayoutManager(requireActivity());
        layoutHManager.setOrientation(RecyclerView.HORIZONTAL);
        tnxTypeRecycler.setLayoutManager(layoutHManager);
        tnxTypeRecycler.setAdapter(typeAdapter);
        typeAdapter.setOnTypeItemClickListener(new TransactionTypeAdapter.OnTypeItemClickListener() {
            @Override
            public void OnTypeItemClickListener(int position) {
                for (int i = 0; i < transactionTypeList.size(); i++) {
                    transactionTypeList.get(i).setSelected(false);
                }
                transactionTypeList.get(position).setSelected(true);
                typeAdapter.itemChanged(transactionTypeList);
                transactionListResultList.clear();
                adapter.itemChanged(transactionListResultList);
                TransactionType item = transactionTypeList.get(position);
                transType = item.getValue();
                transactionNextUrl = null;
                reportNextUrl = null;
                getTransactionData();
            }
        });

        reportRecyclerView = view.findViewById(R.id.report_recycler_view);
        reportRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        reportAdapter = new ReportAdapter(requireActivity(), reportList);
        reportRecyclerView.setAdapter(reportAdapter);
        reportAdapter.setOnReportItemClickListener(new ReportAdapter.OnReportItemClickListener() {
            @Override
            public void onReportItemClickListener(int position, Report report) {

                Bundle bundle = new Bundle();
                bundle.putInt("id", report.getId());
                bundle.putString("result_type", report.getReportType());
                bundle.putString("report_type", report.getResultType());
                bundle.putInt("completion_count", report.getCompletionCount());
                bundle.putString("completion_amount", report.getCompletionAmount());
                bundle.putInt("cashback_count", report.getCashbackCount());
                bundle.putString("cashback_amount", report.getCashbackAmount());
                bundle.putInt("gratuity_count", report.getGratuityCount());
                bundle.putString("gratuity_amount", report.getGratuityAmount());
                bundle.putInt("refund_count", report.getRefundCount());
                bundle.putString("refund_amount", report.getRefundAmount());
                bundle.putInt("sale_count", report.getSaleCount());
                bundle.putString("sale_amount", report.getSaleAmount());
                bundle.putBoolean("is_report_response", report.isIsReportResponse());
                bundle.putInt("merchant", report.getMerchant());
                Gson gson = new Gson();
                String json = gson.toJson(report);
                bundle.putString("report_obj", json);
                Navigation.findNavController(view).navigate(R.id.action_transactionFragment_to_reportDetialsFragment, bundle);

            }
        });
        reportRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (reportNextUrl != null && !reportNextUrl.equals("")) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == transactionListResultList.size() - 1) {
                            loading = true;
                            getNextReportList(reportNextUrl);
                        }
                    }
                }
            }
        });

        transactionRecyclerView = view.findViewById(R.id.transaction_recycler_view);
        transactionRecyclerView.setNestedScrollingEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(false);
        transactionRecyclerView.setLayoutManager(layoutManager);
        adapter = new TransactionAdapter(requireActivity(), transactionListResultList);
        transactionRecyclerView.setAdapter(adapter);
        transactionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        assert linearLayoutManager != null;
                        Log.e(TAG, "onScrolled: " + "Total Size-" + transactionListResultList.size() + " Last Item Visible-" + linearLayoutManager.findFirstCompletelyVisibleItemPosition());
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == transactionListResultList.size() - 6) {
                            loading = true;
                            getNextTransactions(transactionNextUrl);
                        }
                    }
                }
            }
        });

        adapter.setOnTransactionItemClickListener(new TransactionAdapter.OnTransactionItemClickListener() {
            @Override
            public void onProgressItemClickListener(int position, TransactionListResult transaction) {
                TransactionListResult item = transactionListResultList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("tnx_uid", item.getUti());
                bundle.putString("tnx_tid", item.getTerminalId());
                bundle.putString("tnx_pan", item.getPan());
                bundle.putString("tnx_receipt_id", item.getReceiptId());
                bundle.putString("tnx_card_type", item.getCardType());
                bundle.putString("tnx_mid", item.getMerchantUid());
                bundle.putString("tnx_type", item.getTransactionType());
                Double totalAmt = 0.00;
                if (item.getTransactionType().equals("SALE_AUTO") || item.getTransactionType().equals("CASHBACK_AUTO")) {
                    BigDecimal transactionAmount = new BigDecimal(transaction.getAmount());
                    BigDecimal gratuityAmount = new BigDecimal(transaction.getGratuityAmount());
                    BigDecimal cashbackAmount;
                    if (transaction.getCashbackAmount() == null) {
                        cashbackAmount = new BigDecimal("0.00");
                    } else {
                        cashbackAmount = new BigDecimal(transaction.getCashbackAmount());
                    }
                    BigDecimal bigAmt = transactionAmount.add(gratuityAmount);
                    BigDecimal subAmt = bigAmt.add(cashbackAmount);
//                    totalAmt = Double.parseDouble(String.format("%.2f", bigAmt));
                    totalAmt = Double.parseDouble(subAmt.toString());
                } else {
                    totalAmt = Double.parseDouble(item.getAmount());
                }
                String amountTotal = String.format("%.2f", totalAmt);
                bundle.putString("tnx_total", amountTotal);
                bundle.putString("tnx_datetime", item.getCreatedAt());
                bundle.putInt("tnx_status", item.getStatus());
                bundle.putBoolean("tnx_is_approved", item.isApproved());
                boolean isRefundable = false;
                try {
                    TransactionListResult previousItem = transactionListResultList.get(position - 1);
                    isRefundable = (previousItem != null);
                } catch (Exception e) {
//                    e.printStackTrace();
                    Log.e(TAG, "onProgressItemClickListener: " + e.getMessage());
                    // Telemetry: TransactionFragment.onViewCreated.onProgressItemClickListener — previous item lookup failed.
                    logReceiveError("onViewCreated.onProgressItemClickListener", String.valueOf(e.getMessage()), e);
                }
                bundle.putBoolean("is_refundable", isRefundable);
                Gson gson = new Gson();
                String json = gson.toJson(item);
                bundle.putString("tnx_obj", json);
                Navigation.findNavController(view).navigate(R.id.action_transactionFragment_to_transactionDetailsFragment, bundle);
            }
        });

        adapter.setOnSeeMoreItemClickListener(new TransactionAdapter.OnSeeMoreBtnClickListener() {
            @Override
            public void onSeeMoreClickListener() {
                try {
                    if (filterFromDate != null && filterToDate != null) {
                        filterDateChooseBtn.setText(String.format("%s - %s", filterFromDate, filterToDate));
                    } else {
                        filterDateChooseBtn.setText(getString(R.string.choose_date));
                    }
                    selectedDurationFilter = appliedDurationFilter;
                    durationFilterBtn.setText(appliedDurationFilter);
                    if (selectedDurationFilter.equals("Custom")) {
                        transactionDateLayout.setVisibility(View.VISIBLE);
                    } else {
                        transactionDateLayout.setVisibility(View.GONE);
                    }
                    filterDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Telemetry: TransactionFragment.onViewCreated.onSeeMoreClickListener — failed to show filter dialog.
                    logReceiveError("onViewCreated.onSeeMoreClickListener", String.valueOf(e.getMessage()), e);
                }
            }
        });

//        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    performSearch();
//                    return true;
//                }
//                return false;
//            }
//        });


        //Date Range Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTheme(R.style.MaterialCalendarTheme);
        materialDateBuilder.setTitleText("SELECT A DATE");
        materialDatePicker = materialDateBuilder.build();
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    // if the user clicks on the positive
                    // button that is ok button update the
                    // selected date
//                        filterDateChooseBtn.setText("Selected Date is : " + materialDatePicker.getHeaderText());

                    try {
                        Date[] dates = DateUtils.parseDateRangeFromDatePicker(materialDatePicker.getHeaderText());
                        fromDateFilter = dates[0];
                        toDateFilter = dates[1];
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // Telemetry: TransactionFragment.onViewCreated.materialDatePicker — date range parse failed.
                        logReceiveError("onViewCreated.materialDatePicker", String.valueOf(e.getMessage()), e);
//                        throw new RuntimeException(e);
                    }

                    filterDateChooseBtn.setText(materialDatePicker.getHeaderText());
                    // in the above statement, getHeaderText
                    // will return selected date preview from the
                    // dialog
                });
        materialDatePicker.addOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDatePickerShowing = false;
            }
        });

        // Get instance of calendar
        // mCalendar will be set to current/today's date
        mCalendar = Calendar.getInstance();

        // Creating a simple calendar dialog.
        // It was 9 Aug 2021 when this program was
        // developed.
        mDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int mYear, int mMonth, int mDay) {
                mCalendar.set(Calendar.YEAR, mYear);
                mCalendar.set(Calendar.MONTH, mMonth);
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String dateTime = simpleDateFormat.format(mCalendar.getTime());
                filterDateChooseBtn.setText(dateTime);
                toggleFilterDateClear(true);
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

//        // Changing mCalendar date from current to
//        // some random MIN day 15/08/2021 15 Aug 2021
//        // If we want the same current day to be the MIN
//        // day, then mCalendar is already set to today and
//        // the below code will be unnecessary
//        final int minDay = 15;
//        final int minMonth = 8;
//        final int minYear = 2021;
//        mCalendar.set(minYear, minMonth - 1, minDay);
//        mDialog.getDatePicker().setMinDate(
//                mCalendar.getTimeInMillis());
//
//        // Changing mCalendar date from current to
//        // some random MAX day 20/08/2021 20 Aug 2021
//        final int maxDay = 20;
//        final int maxMonth = 8;
//        final int maxYear = 2021;
//        mCalendar.set(maxYear, maxMonth - 1, maxDay);
//        mDialog.getDatePicker().setMaxDate(
//                mCalendar.getTimeInMillis());
//        setLocalData();
        getTransactionData();
    }

    public void showRadioPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.duration_filter_layout, null);
        builder.setView(view);
        builder.setTitle("Choose an Option");

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        // Loop through children to find the one matching selectedDurationFilter
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View child = radioGroup.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) child;
                if (radioButton.getText().toString().equals(selectedDurationFilter)) {
                    radioButton.setChecked(true);
                    break;
                }
            }
        }

        builder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadio = view.findViewById(selectedId);
                if (selectedRadio != null) {
                    String selectedText = selectedRadio.getText().toString();
                    switch (selectedText) {
                        case "Custom":
                            transactionDateLayout.setVisibility(View.VISIBLE);
                            break;
                        case "Last 3 Days":
                            transactionDateLayout.setVisibility(View.GONE);
                            break;
                        case "Today":
                            transactionDateLayout.setVisibility(View.GONE);
                            break;
                        case "Yesterday":
                            transactionDateLayout.setVisibility(View.GONE);
                            break;
                        case "This Week":
                            transactionDateLayout.setVisibility(View.GONE);
                            break;
                        case "Last Week":
                            transactionDateLayout.setVisibility(View.GONE);
                            break;
                    }

                    selectedDurationFilter = selectedText;
                    durationFilterBtn.setText(selectedDurationFilter);

                    Toast.makeText(requireContext(), "Selected: " + selectedText, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void generateTransactionTypeData() {
        transactionTypeList.clear();
        typeAdapter = new TransactionTypeAdapter(requireActivity(), transactionTypeList);
        transactionTypeList.add(new TransactionType("All", "", true));
        transactionTypeList.add(new TransactionType("Sale", "SALE_AUTO", false));
        transactionTypeList.add(new TransactionType("ZReport", "Z-REPORT", false));
        transactionTypeList.add(new TransactionType("XReport", "X-REPORT", false));
        transactionTypeList.add(new TransactionType("Refund", "REFUND_AUTO", false));
        transactionTypeList.add(new TransactionType("Pre-Auth", "PREAUTH_AUTO", false));
        transactionTypeList.add(new TransactionType("Declined", "DECLINED", false));
        transactionTypeList.add(new TransactionType("Cancelled", "CANCELLED", false));
        typeAdapter.itemChanged(transactionTypeList);
    }

    private void toggleFilterDateClear(boolean visible) {
        if (visible) {
            filterSelectedDateRemoveBtn.setVisibility(View.VISIBLE);
        } else {
            filterDateChooseBtn.setText(getString(R.string.choose_date));
            mCalendar = Calendar.getInstance();
            filterSelectedDateRemoveBtn.setVisibility(View.GONE);
        }
    }

//    private void setLocalData() {
//        AppConstants.executorService.execute(new Runnable() {
//            @Override
//            public void run() {
//                progressBar.setVisibility(View.VISIBLE);
//
//                try {
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.add(Calendar.DATE, -2); // Subtract 2 days
//                    String twoDaysAgo = sdf.format(calendar.getTime());
//                    HomeActivity.localDb.transactionDao().deleteRecordsOlderThanTwoDays(twoDaysAgo);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                transactionListResultList.clear();
////   updateAdapter(transactionListResultList);
//
//                for (Transaction transaction :
//                        HomeActivity.localDb.transactionDao().getAll()) {
//                    TransactionListResult listItem = new TransactionListResult();
//                    listItem.setId(transaction.getId());
//                    listItem.setCreatedAt(transaction.getCreatedAt());
//                    listItem.setUpdatedAt(transaction.getUpdatedAt());
//                    listItem.setReceiptId(transaction.getReceiptId());
//                    listItem.setTransactionType(transaction.getTransactionType());
//                    listItem.setPan(transaction.getPan());
//                    listItem.setUti(transaction.getUti());
//                    listItem.setAmount(transaction.getAmount());
//                    listItem.setDiscount(transaction.getDiscount());
//                    listItem.setApproved(transaction.isApproved());
//                    listItem.setCancelled(transaction.isCancelled());
//                    listItem.setSigRequired(transaction.isSigRequired());
//                    listItem.setPinVerified(transaction.isPinVerified());
//                    listItem.setCurrency(transaction.getCurrency());
//                    listItem.setTerminalId(transaction.getTerminalId());
//                    listItem.setMerchantUid(transaction.getMerchantUid());
//                    listItem.setCardType(transaction.getCardType());
//                    listItem.setMerchant(transaction.getMerchant());
//                    listItem.setStatus(transaction.getStatus());
//                    transactionListResultList.add(listItem);
//                }
//                Collections.reverse(transactionListResultList);
//                updateAdapter(transactionListResultList);
//            }
//        });
//    }

    private void updateAdapter(List<TransactionListResult> transactionListResultList) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // WORK on UI thread here
                adapter.itemChanged(transactionListResultList);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getTransactionData() {
        if (transType.equals("Z-REPORT")) {
            transactionRecyclerView.setVisibility(View.GONE);
            reportRecyclerView.setVisibility(View.VISIBLE);
            reportList.clear();
            reportAdapter.itemChanged(reportList);
            progressBar.setVisibility(View.VISIBLE);
            no_history_illustration.setVisibility(View.GONE);
            String token = SharedHelper.getToken(requireContext());
            HashMap<String, String> map = new HashMap<>();
            map.put("report_type", "ZReport");
            map.put("tid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
            if (fromDateFilter != null) {
                map.put("from_date", DateUtils.parseSystemStringDate(fromDateFilter.toString()));
            }
            if (toDateFilter != null) {
                map.put("to_date", DateUtils.parseSystemStringDate(toDateFilter.toString()));
            }
            map.put("merchant", String.valueOf(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.USER_ID)));
            viewModel.getReportList(map, token).observe(requireActivity(), new Observer<ReportListResponse>() {
                @Override
                public void onChanged(ReportListResponse reportListResponse) {
                    progressBar.setVisibility(View.GONE);
                    reportList.clear();
                    if (reportListResponse != null) {
                        if (reportListResponse.getData().getResults().size() > 0) {
                            reportList.addAll(reportListResponse.getData().getResults());
                        }
                        if (reportListResponse.getData().getResults().isEmpty()) {
                            no_history_illustration.setVisibility(View.VISIBLE);
                        }
                    }

                    reportAdapter.itemChanged(reportList);
                }
            });

        } else if (transType.equals("X-REPORT")) {
            transactionRecyclerView.setVisibility(View.GONE);
            reportRecyclerView.setVisibility(View.VISIBLE);
            reportList.clear();
            reportAdapter.itemChanged(reportList);
            progressBar.setVisibility(View.VISIBLE);
            no_history_illustration.setVisibility(View.GONE);
            String token = SharedHelper.getToken(requireContext());
            HashMap<String, String> map = new HashMap<>();
            map.put("report_type", "XReport");
            map.put("tid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
            if (fromDateFilter != null) {
                map.put("from_date", DateUtils.parseSystemStringDate(fromDateFilter.toString()));
            }
            if (toDateFilter != null) {
                map.put("to_date", DateUtils.parseSystemStringDate(toDateFilter.toString()));
            }
            map.put("merchant", String.valueOf(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.USER_ID)));
            viewModel.getReportList(map, token).observe(requireActivity(), new Observer<ReportListResponse>() {
                @Override
                public void onChanged(ReportListResponse reportListResponse) {
                    progressBar.setVisibility(View.GONE);
                    if (reportListResponse != null) {
                        reportList.clear();
                        if (reportListResponse.getData().getResults().size() > 0) {
                            reportList.addAll(reportListResponse.getData().getResults());
                        }
                        if (reportListResponse.getData().getNext() != null) {
                            reportNextUrl = reportListResponse.getData().getNext();
                        }
                        if (reportListResponse.getData().getResults().isEmpty()) {
                            no_history_illustration.setVisibility(View.VISIBLE);
                        }
                        reportAdapter.itemChanged(reportList);
                    }
                }
            });

        } else if (transType.equals("DECLINED")) {
            transactionRecyclerView.setVisibility(View.VISIBLE);
            reportRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            no_history_illustration.setVisibility(View.GONE);
            String token = SharedHelper.getToken(requireContext());
            HashMap<String, String> map = new HashMap<>();
            map.put("search", searchText);
            map.put("merchant_uid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID));
            map.put("terminal_id", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
            if (fromDateFilter != null) {
                map.put("from_date", DateUtils.parseSystemStringDate(fromDateFilter.toString()));
            }
            if (toDateFilter != null) {
                map.put("to_date", DateUtils.parseSystemStringDate(toDateFilter.toString()));
            }
            List<String> selectedSlots = new ArrayList<>();

            if (slot1 != null && slot1.isChecked()) {
                selectedSlots.add("4am - 10am");
            }
            if (slot2 != null && slot2.isChecked()) {
                selectedSlots.add("10am - 4pm");
            }
            if (slot3 != null && slot3.isChecked()) {
                selectedSlots.add("4pm - 10pm");
            }
            if (slot4 != null && slot4.isChecked()) {
                selectedSlots.add("10pm - 4am");
            }
            map.put("slots", selectedSlots.toString());
            map.put("status", "0");
            map.put("merchant", String.valueOf(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.USER_ID)));
            viewModel.getTransactionsList(map, token).observe(requireActivity(), new Observer<TransactionListResponse>() {
                @Override
                public void onChanged(TransactionListResponse transactionListResponse) {
                    progressBar.setVisibility(View.GONE);
                    transactionListResultList.clear();
                    if (transactionListResponse != null) {
                        if (transactionListResponse.getData().getResults().size() > 0) {
//                        pageCountView.setVisibility(View.VISIBLE);
                            transactionListResultList.addAll(transactionListResponse.getData().getResults());
                        } else {
//                        pageCountView.setVisibility(View.GONE);
                        }

                        if (transactionListResponse.getData().getNext() != null) {
                            transactionNextUrl = transactionListResponse.getData().getNext();
                        } else {
                            transactionNextUrl = null;
                            transactionListResultList.add(transactionListResultList.size(), new TransactionListResult());
                        }

                        if (transactionListResponse.getData().getResults().isEmpty()) {
                            no_history_illustration.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter.itemChanged(transactionListResultList);
                }
            });
        } else if (transType.equals("CANCELLED")) {
            transactionRecyclerView.setVisibility(View.VISIBLE);
            reportRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            no_history_illustration.setVisibility(View.GONE);
            String token = SharedHelper.getToken(requireContext());
            HashMap<String, String> map = new HashMap<>();
            map.put("search", searchText);
            map.put("merchant_uid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID));
            map.put("terminal_id", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
            if (fromDateFilter != null) {
                map.put("from_date", DateUtils.parseSystemStringDate(fromDateFilter.toString()));
            }
            if (toDateFilter != null) {
                map.put("to_date", DateUtils.parseSystemStringDate(toDateFilter.toString()));
            }
            List<String> selectedSlots = new ArrayList<>();

            if (slot1 != null && slot1.isChecked()) {
                selectedSlots.add("4am - 10am");
            }
            if (slot2 != null && slot2.isChecked()) {
                selectedSlots.add("10am - 4pm");
            }
            if (slot3 != null && slot3.isChecked()) {
                selectedSlots.add("4pm - 10pm");
            }
            if (slot4 != null && slot4.isChecked()) {
                selectedSlots.add("10pm - 4am");
            }
            map.put("slots", selectedSlots.toString());
            map.put("status", "2");
            map.put("merchant", String.valueOf(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.USER_ID)));
            viewModel.getTransactionsList(map, token).observe(requireActivity(), new Observer<TransactionListResponse>() {
                @Override
                public void onChanged(TransactionListResponse transactionListResponse) {
                    progressBar.setVisibility(View.GONE);
                    transactionListResultList.clear();
                    if (transactionListResponse != null) {

                        if (transactionListResponse.getData().getResults().size() > 0) {
//                        pageCountView.setVisibility(View.VISIBLE);
                            transactionListResultList.addAll(transactionListResponse.getData().getResults());
                        } else {
//                        pageCountView.setVisibility(View.GONE);
                        }

                        if (transactionListResponse.getData().getNext() != null) {
                            transactionNextUrl = transactionListResponse.getData().getNext();
                        } else {
                            transactionNextUrl = null;
                            transactionListResultList.add(transactionListResultList.size(), new TransactionListResult());
                        }
                        if (transactionListResponse.getData().getResults().isEmpty()) {
                            no_history_illustration.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter.itemChanged(transactionListResultList);
                }
            });

        } else {

            transactionRecyclerView.setVisibility(View.VISIBLE);
            reportRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            no_history_illustration.setVisibility(View.GONE);
            String token = SharedHelper.getToken(requireContext());
            HashMap<String, String> map = new HashMap<>();
            map.put("search", searchText);
            map.put("merchant_uid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID));
            map.put("terminal_id", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID));
            if (fromDateFilter != null) {
                map.put("from_date", DateUtils.parseSystemStringDate(fromDateFilter.toString()));
            }
            if (toDateFilter != null) {
                map.put("to_date", DateUtils.parseSystemStringDate(toDateFilter.toString()));
            }
            List<String> selectedSlots = new ArrayList<>();

            if (slot1 != null && slot1.isChecked()) {
                selectedSlots.add("4am - 10am");
            }
            if (slot2 != null && slot2.isChecked()) {
                selectedSlots.add("10am - 4pm");
            }
            if (slot3 != null && slot3.isChecked()) {
                selectedSlots.add("4pm - 10pm");
            }
            if (slot4 != null && slot4.isChecked()) {
                selectedSlots.add("10pm - 4am");
            }
            map.put("slots", selectedSlots.toString());
            map.put("transaction_type", transType);
            map.put("merchant", String.valueOf(SharedHelper.getIntData(requireContext(), AppConstants.SharedPref.USER_ID)));
            viewModel.getTransactionsList(map, token).observe(requireActivity(), new Observer<TransactionListResponse>() {
                @Override
                public void onChanged(TransactionListResponse transactionListResponse) {
                    transactionListResultList.clear();
                    if (transactionListResponse != null) {

                        if (transactionListResponse.getData().getResults().size() > 0) {
//                        pageCountView.setVisibility(View.VISIBLE);
                            transactionListResultList.addAll(transactionListResponse.getData().getResults());
                        } else {
//                        pageCountView.setVisibility(View.GONE);
                        }

                        if (transactionListResponse.getData().getNext() != null) {
                            transactionNextUrl = transactionListResponse.getData().getNext();
                        } else {
                            transactionNextUrl = null;
                            transactionListResultList.add(transactionListResultList.size(), new TransactionListResult());
                        }
                        if (transactionListResponse.getData().getResults().isEmpty()) {
                            no_history_illustration.setVisibility(View.VISIBLE);
                        }
                    }
                    adapter.itemChanged(transactionListResultList);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    void getNextTransactions(String nextUrl) {
        String token = SharedHelper.getToken(requireContext());
        viewModel.getNextTransactionsList(nextUrl, token).observe(this, new Observer<TransactionListResponse>() {
            @Override
            public void onChanged(TransactionListResponse transactionListResponse) {
                loading = false;
                if (transactionListResponse != null) {

                    if (transactionListResponse.getData().getResults().size() > 0) {
                        transactionListResultList.addAll(transactionListResponse.getData().getResults());
                    }

                    if (transactionListResponse.getData().getNext() != null) {
                        transactionNextUrl = transactionListResponse.getData().getNext();
                    } else {
                        transactionNextUrl = null;
                        transactionListResultList.add(transactionListResultList.size(), new TransactionListResult());
                    }
                }
                adapter.itemChanged(transactionListResultList);
            }
        });
    }

    void getNextReportList(String nextUrl) {
        String token = SharedHelper.getToken(requireContext());
        viewModel.getNextReportList(nextUrl, token).observe(requireActivity(), new Observer<ReportListResponse>() {
            @Override
            public void onChanged(ReportListResponse reportListResponse) {

                if (reportListResponse != null) {
                    if (reportListResponse.getData().getNext() != null) {
                        reportNextUrl = reportListResponse.getData().getNext();
                    } else {
                        reportNextUrl = null;
                    }
                    if (reportListResponse.getData().getResults().size() > 0) {
                        reportList.addAll(reportListResponse.getData().getResults());
                    }
                }
                reportAdapter.itemChanged(reportList);

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.filter_btn:
                try {
                    if (filterFromDate != null && filterToDate != null) {
                        filterDateChooseBtn.setText(String.format("%s - %s", filterFromDate, filterToDate));
                    } else {
                        filterDateChooseBtn.setText(getString(R.string.choose_date));
                    }
                    selectedDurationFilter = appliedDurationFilter;
                    durationFilterBtn.setText(appliedDurationFilter);
                    if (selectedDurationFilter.equals("Custom")) {
                        transactionDateLayout.setVisibility(View.VISIBLE);
                    } else {
                        transactionDateLayout.setVisibility(View.GONE);
                    }
                    filterDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();

                    // Telemetry: TransactionFragment.onClick.filter_btn — failed to show filter dialog.
                    logReceiveError("onClick.filter_btn", String.valueOf(e.getMessage()), e);
                }
                break;
            case R.id.filter_apply_btn:
                try {

                    appliedDurationFilter = durationFilterBtn.getText().toString();
                    titleText.setText(String.format("Transactions (%s)", appliedDurationFilter));
                    switch (appliedDurationFilter) {
                        case "Last 3 Days":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDate today = LocalDate.now();
                                LocalDate threeDaysAgo = today.minusDays(2);

                                fromDateFilter = Date.from(threeDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
                                toDateFilter = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            }
                            break;

                        case "Today":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDate today = LocalDate.now();

                                fromDateFilter = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
                                toDateFilter = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            }
                            break;

                        case "Yesterday":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDate yesterday = LocalDate.now().minusDays(1);

                                fromDateFilter = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
                                toDateFilter = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            }
                            break;

                        case "This Week":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDate today = LocalDate.now();
                                LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

                                fromDateFilter = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
                                toDateFilter = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            }
                            break;

                        case "Last Week":
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDate today = LocalDate.now();
                                LocalDate startOfLastWeek = today.with(java.time.DayOfWeek.MONDAY).minusWeeks(1);
                                LocalDate endOfLastWeek = startOfLastWeek.plusDays(6);

                                fromDateFilter = Date.from(startOfLastWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
                                toDateFilter = Date.from(endOfLastWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            }
                            break;
                        case "Custom":
                            if (!filterDateChooseBtn.getText().equals(getString(R.string.choose_date))) {
                                filterFromDate = filterDateChooseBtn.getText().toString().split(" – ")[0].trim();
                                filterToDate = filterDateChooseBtn.getText().toString().split(" – ")[1].trim();
                                Log.e(TAG, "onClick: FromDate-" + filterFromDate + " ToDate-" + filterToDate);
                            }
                            break;
                    }
                    filterDialog.cancel();

                } catch (Exception e) {
                    e.printStackTrace();
                    // Telemetry: TransactionFragment.onClick.filter_apply_btn — failed to apply filter.
                    logReceiveError("onClick.filter_apply_btn", String.valueOf(e.getMessage()), e);
                }
                getTransactionData();
                appliedDurationFilter = "Last 3 Days";
                selectedDurationFilter = appliedDurationFilter;
                titleText.setText(String.format("Transactions (%s)", appliedDurationFilter));
                toDateFilter = null;
                fromDateFilter = null;
                filterFromDate = null;
                filterToDate = null;
                slot1.setChecked(false);
                slot2.setChecked(false);
                slot3.setChecked(false);
                slot4.setChecked(false);

                try {
                    filterDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Telemetry: TransactionFragment.onClick.filter_clear_btn — failed to dismiss filter dialog.
                    logReceiveError("onClick.filter_clear_btn", String.valueOf(e.getMessage()), e);
                }
                getTransactionData();
                break;
            case R.id.filter_close_btn:
                try {
                    filterDialog.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Telemetry: TransactionFragment.onClick.filter_close_btn — failed to close filter dialog.
                    logReceiveError("onClick.filter_close_btn", String.valueOf(e.getMessage()), e);
                }
                break;
            case R.id.filter_choose_date_btn:
                // Display the calendar dialog
                // mDialog.show();
                if (!isDatePickerShowing) {
                    try {
                        isDatePickerShowing = true;
                        materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: TransactionFragment.onClick.filter_choose_date_btn — failed to show date picker.
                        logReceiveError("onClick.filter_choose_date_btn", String.valueOf(e.getMessage()), e);
                    }
                }
                break;
            case R.id.selected_date_remove_btn:
                toggleFilterDateClear(false);
                break;
            case R.id.filter_duration_btn:
                showRadioPopup();
                break;
        }
    }

    /**
     * Sends a fragment failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code TransactionFragment.onClick.filter_apply_btn}.
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