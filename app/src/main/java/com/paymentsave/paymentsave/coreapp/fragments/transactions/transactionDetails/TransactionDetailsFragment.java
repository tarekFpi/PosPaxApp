package com.paymentsave.paymentsave.coreapp.fragments.transactions.transactionDetails;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_UTI;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_REFUND;
import static com.paymentsave.paymentsave.coreapp.utils.PrintUtils.formatString;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eft.libpositive.PosIntegrate;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.home.HomeActivity;
import com.paymentsave.paymentsave.coreapp.activities.pdfView.TemplatePDF;
import com.paymentsave.paymentsave.coreapp.activities.test.printer.PrinterTester;
import com.paymentsave.paymentsave.coreapp.fragments.settings.fragments.verifySupervisorPin.VerifyPinViewModel;
import com.paymentsave.paymentsave.coreapp.fragments.viewModels.TransactionViewModel;
import com.paymentsave.paymentsave.coreapp.utils.DateUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.coreapp.utils.TransparentProgressDialog;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.PinVerifyResponse.PinVerifyResponse;
import com.paymentsave.paymentsave.responses.TransactionDetailsResponse.TransactionDetailsData;
import com.paymentsave.paymentsave.responses.TransactionListResponse.TransactionListResult;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Objects;


public class TransactionDetailsFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "TransactionDetails";
    private static final String ARG_PARAM = "tnx_uid";
    LinearLayout backBtn, baseLayout;
    TransactionViewModel viewModel;
    TextView tnxTypeTv, tnxNoteTv, tnxPaymentMethod, tnxStatusTxt, amtTV, dateTV, timeTV, statusTv, saleAmountTV, gratuityAmountTV, cashbackAmountTV;
    private RelativeLayout noteLayout;
    //Views
    private ImageView statusIv;
    private TextView statusTopTV;
    private BottomSheetDialog supervisorPinDialog, refundAmountDialog;
    private LinearLayout reasonLayout, gratuityLayout, cashbackLayout;
    private EditText refundReasonEt;

    private RelativeLayout paymentStatusLayout, printReceiptBtn, reversalBtn;
    private Button runRefundBtn;
    private FloatingActionButton refundBackBtn;
    private EditText refundAmtEt;
    private TextView maxRefundAmtText;
    private ImageView tnxStatusIv;
    // Transaction Details UID
    private String tnxId, tnxReceiptId, tnxTid, tnxPan, tnxCardType, tnxType, tnxMid, tnxTotal, tnxDatetime;
    private int tnxStatus;
    private boolean tnxIsApproved, tnxIsRefundable;
    private TransactionListResult transaction;
    private ImageButton pinVisibilityBtn;
    private ImageView pinLayoutBackBtn;
    private boolean visiblePinPass = false, isPrinting = false;
    private EditText pinPass1, pinPass2, pinPass3, pinPass4;
    private Button btn00, btn01, btn02, btn03, btn04, btn05, btn06, btn07, btn08, btn09, btnBackSpace;
    private VerifyPinViewModel pinViewModel;
    private TransparentProgressDialog pd;
    private ImageView img01;
    private TextWatcher refundTextInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!charSequence.toString().isEmpty()) {
                if (Double.parseDouble(tnxTotal) == Double.parseDouble(charSequence.toString())) {
                    reasonLayout.setVisibility(View.GONE);
                    runRefundBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_btn_bg));
                } else if (Double.parseDouble(charSequence.toString()) == 0.00) {
                    refundAmtEt.setError(getString(R.string.value_can_not_be_0_00));
                    refundAmtEt.requestFocus();
                    runRefundBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_gray));
                } else {
                    reasonLayout.setVisibility(View.VISIBLE);
                    runRefundBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_btn_bg));
                }
            } else {
                refundAmtEt.setError(getString(R.string.can_not_be_empty));
                refundAmtEt.requestFocus();
                reasonLayout.setVisibility(View.GONE);
                runRefundBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_gray));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TransactionDetailsData transactionData;
    private TemplatePDF templatePDF;
    private String actionType = "";
    View.OnClickListener keypadOnclickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn00:
                    setPin("0");
                    break;
                case R.id.btn01:
                    setPin("1");
                    break;
                case R.id.btn02:
                    setPin("2");
                    break;
                case R.id.btn03:
                    setPin("3");
                    break;
                case R.id.btn04:
                    setPin("4");
                    break;
                case R.id.btn05:
                    setPin("5");
                    break;
                case R.id.btn06:
                    setPin("6");
                    break;
                case R.id.btn07:
                    setPin("7");
                    break;
                case R.id.btn08:
                    setPin("8");
                    break;
                case R.id.btn09:
                    setPin("9");
                    break;
                case R.id.btnBackSpace:
                    backSpace();
                    break;
                case R.id.backButton:
                    resetPin();
                    visiblePinPass = false;
                    setPinVisibility(false);
                    supervisorPinDialog.dismiss();
                    break;
                case R.id.pinVisibilityBtn:
                    visiblePinPass = !visiblePinPass;
                    setPinVisibility(visiblePinPass);
                    break;
            }

        }
    };

    public TransactionDetailsFragment() {
        // Required empty public constructor
    }

    private void setPinVisibility(boolean visiblePinPass) {
        if (visiblePinPass) {
            // Show password
            pinPass1.setTransformationMethod(null);
            pinPass2.setTransformationMethod(null);
            pinPass3.setTransformationMethod(null);
            pinPass4.setTransformationMethod(null);
            pinVisibilityBtn.setImageResource(R.drawable.ic_baseline_visibility_24);
        } else {
            // Hide password
            pinPass1.setTransformationMethod(new PasswordTransformationMethod());
            pinPass2.setTransformationMethod(new PasswordTransformationMethod());
            pinPass3.setTransformationMethod(new PasswordTransformationMethod());
            pinPass4.setTransformationMethod(new PasswordTransformationMethod());
            pinVisibilityBtn.setImageResource(R.drawable.ic_baseline_visibility_off_24);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tnxId = getArguments().getString(ARG_PARAM);
            tnxReceiptId = getArguments().getString("tnx_receipt_id");
            tnxTid = getArguments().getString("tnx_tid");
            tnxMid = getArguments().getString("tnx_mid");
            tnxPan = getArguments().getString("tnx_pan");
            tnxCardType = getArguments().getString("tnx_card_type");
            tnxType = getArguments().getString("tnx_type");
            tnxTotal = getArguments().getString("tnx_total");
            tnxDatetime = getArguments().getString("tnx_datetime");
            tnxStatus = getArguments().getInt("tnx_status", 0);
            tnxIsApproved = getArguments().getBoolean("tnx_is_approved", false);
            tnxIsRefundable = getArguments().getBoolean("is_refundable", false);
            Gson gson = new Gson();
            String jsonObj = getArguments().getString("tnx_obj");
            transaction = gson.fromJson(jsonObj, TransactionListResult.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_details, container, false);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        pinViewModel = ViewModelProviders.of(this).get(VerifyPinViewModel.class);
        tnxTypeTv = view.findViewById(R.id.tnx_type_tv);
        tnxNoteTv = view.findViewById(R.id.tnx_note_tv);
        tnxPaymentMethod = view.findViewById(R.id.tnx_card_tv);
        statusIv = view.findViewById(R.id.status_iv);
        statusTopTV = view.findViewById(R.id.status_top_txt);
        tnxStatusIv = view.findViewById(R.id.tnx_status_iv);
        tnxStatusTxt = view.findViewById(R.id.tnx_status_text);
        amtTV = view.findViewById(R.id.tnx_details_amt_tv);
        dateTV = view.findViewById(R.id.tnx_date_tv);
        timeTV = view.findViewById(R.id.tnx_time_tv);
        statusTv = view.findViewById(R.id.tnx_status_tv);
        saleAmountTV = view.findViewById(R.id.sale_amount_txt);
        gratuityAmountTV = view.findViewById(R.id.gratuity_amount_txt);
        cashbackAmountTV = view.findViewById(R.id.cashback_amount_txt);
        img01 = view.findViewById(R.id.vector01);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseLayout = view.findViewById(R.id.base_layout);
        noteLayout = view.findViewById(R.id.note_layout);
        backBtn = view.findViewById(R.id.tnx_details_back_btn);
        reversalBtn = view.findViewById(R.id.reversal_btn);
        reversalBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        gratuityLayout = view.findViewById(R.id.gratuity_layout);
        cashbackLayout = view.findViewById(R.id.cashback_layout);
        paymentStatusLayout = view.findViewById(R.id.payment_status_lt);
        paymentStatusLayout.setOnClickListener(this);
        printReceiptBtn = view.findViewById(R.id.print_receipt_btn);
        printReceiptBtn.setOnClickListener(this);
        supervisorPinDialog = MainUtils.getSupervisorBottomSheet(getActivity(), getContext());
        pinPass1 = supervisorPinDialog.findViewById(R.id.pinInput1);
        pinPass2 = supervisorPinDialog.findViewById(R.id.pinInput2);
        pinPass3 = supervisorPinDialog.findViewById(R.id.pinInput3);
        pinPass4 = supervisorPinDialog.findViewById(R.id.pinInput4);
        pinLayoutBackBtn = supervisorPinDialog.findViewById(R.id.backButton);
        pinLayoutBackBtn.setOnClickListener(keypadOnclickListener);
        pinVisibilityBtn = supervisorPinDialog.findViewById(R.id.pinVisibilityBtn);
        pinVisibilityBtn.setOnClickListener(keypadOnclickListener);
        btn00 = supervisorPinDialog.findViewById(R.id.btn00);
        btn00.setOnClickListener(keypadOnclickListener);
        btn01 = supervisorPinDialog.findViewById(R.id.btn01);
        btn01.setOnClickListener(keypadOnclickListener);
        btn02 = supervisorPinDialog.findViewById(R.id.btn02);
        btn02.setOnClickListener(keypadOnclickListener);
        btn03 = supervisorPinDialog.findViewById(R.id.btn03);
        btn03.setOnClickListener(keypadOnclickListener);
        btn04 = supervisorPinDialog.findViewById(R.id.btn04);
        btn04.setOnClickListener(keypadOnclickListener);
        btn05 = supervisorPinDialog.findViewById(R.id.btn05);
        btn05.setOnClickListener(keypadOnclickListener);
        btn06 = supervisorPinDialog.findViewById(R.id.btn06);
        btn06.setOnClickListener(keypadOnclickListener);
        btn07 = supervisorPinDialog.findViewById(R.id.btn07);
        btn07.setOnClickListener(keypadOnclickListener);
        btn08 = supervisorPinDialog.findViewById(R.id.btn08);
        btn08.setOnClickListener(keypadOnclickListener);
        btn09 = supervisorPinDialog.findViewById(R.id.btn09);
        btn09.setOnClickListener(keypadOnclickListener);
        btnBackSpace = supervisorPinDialog.findViewById(R.id.btnBackSpace);
        btnBackSpace.setOnClickListener(keypadOnclickListener);

        refundAmountDialog = MainUtils.getRefundAmountBottomSheet(getActivity(), getContext());
        reasonLayout = refundAmountDialog.findViewById(R.id.reason_layout);
        refundAmtEt = refundAmountDialog.findViewById(R.id.refund_amt_et);
        refundReasonEt = refundAmountDialog.findViewById(R.id.refund_reason_et);
        maxRefundAmtText = refundAmountDialog.findViewById(R.id.max_refund_amt_txt);
        refundBackBtn = refundAmountDialog.findViewById(R.id.refundBackButton);
        runRefundBtn = refundAmountDialog.findViewById(R.id.run_refund_btn);
        refundAmtEt.addTextChangedListener(refundTextInputWatcher);
        refundBackBtn.setOnClickListener(this);
        runRefundBtn.setOnClickListener(this);

        templatePDF = new TemplatePDF(requireContext());
        templatePDF.openDocument();
        templatePDF.addMetaData("Order Receipt", "Order Receipt", "Paymentsave");
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.receipt);
        templatePDF.addImage(bm);


//        if (tnxType.equals("SALE_AUTO") && tnxIsApproved && transaction.getStatus() != 3 && transaction.getStatus() != 4) {
        if ((tnxType.equals("SALE_AUTO") || tnxType.equals("CASHBACK_AUTO")) && tnxIsApproved && (transaction.getStatus() != 3 && transaction.getStatus() != 4)) {
            if (!tnxIsRefundable && DateUtils.isVoidAble(transaction.getCreatedAt())) {
                paymentStatusLayout.setVisibility(View.VISIBLE);
                reversalBtn.setVisibility(View.GONE);
            } else {
                paymentStatusLayout.setVisibility(View.VISIBLE);
                reversalBtn.setVisibility(View.GONE);
            }
            if (transaction.isSubmitted()) {
                reversalBtn.setVisibility(View.GONE);
            }
        } else {
            paymentStatusLayout.setVisibility(View.GONE);
        }

        if (Double.parseDouble(transaction.getGratuityAmount()) > 0) {
            gratuityLayout.setVisibility(View.VISIBLE);
            gratuityAmountTV.setText(String.format("+ £%s", transaction.getGratuityAmount()));
        } else {
            gratuityLayout.setVisibility(View.GONE);
        }

        if (transaction.getCashbackAmount() != null && Double.parseDouble(transaction.getCashbackAmount()) > 0) {
            cashbackLayout.setVisibility(View.VISIBLE);
            cashbackAmountTV.setText(String.format("+ £%s", transaction.getCashbackAmount()));
        } else {
            cashbackLayout.setVisibility(View.GONE);
        }

//        if (tnxType.equals("SALE_AUTO") && tnxIsApproved && (transaction.getStatus() != 3 && transaction.getStatus() != 4)) {
//            if (!transaction.isSubmitted()) {
//                paymentStatusLayout.setVisibility(View.GONE);
//                reversalBtn.setVisibility(View.VISIBLE);
//            } else {
//                paymentStatusLayout.setVisibility(View.VISIBLE);
//                reversalBtn.setVisibility(View.GONE);
//            }
//        } else {
//            paymentStatusLayout.setVisibility(View.GONE);
//        }
        saleAmountTV.setText(String.format("£%s", transaction.getAmount()));
        tnxTypeTv.setText(tnxType);
        if (transaction.getTnxNote() != null && !TextUtils.isEmpty(transaction.getTnxNote())) {
            noteLayout.setVisibility(View.VISIBLE);
            tnxNoteTv.setText(transaction.getTnxNote());
        } else {
            noteLayout.setVisibility(View.GONE);
        }

        tnxPaymentMethod.setText(transaction.getPan());
        double total = Double.parseDouble(tnxTotal);
        amtTV.setText(String.format("£ %.2f", total));

        maxRefundAmtText.setText(String.format("Max Amount: %s", tnxTotal));
        dateTV.setText(DateUtils.getFormattedDate(tnxDatetime));
        timeTV.setText(DateUtils.getTime(tnxDatetime));
        switch (tnxStatus) {
            case 0:
                baseLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radius));
                statusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radious_bg));
                statusTopTV.setText(R.string.declined);
                statusTv.setText(R.string.declined);
                statusTv.setTextColor(getResources().getColor(R.color.yellow));
                tnxStatusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.cancel_icon));
                if (tnxType.equals("SALE_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.your_payment_was_declined));
                } else if (tnxType.equals("REFUND_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.refund_payment_was_declined));
                } else if (tnxType.equals("MANUAL_REVERSAL_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.transaction_void_was_declined));
                } else if (tnxType.equals("PREAUTH_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.pre_auth_payment_was_declined));
                }
                break;
            case 1:
                baseLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.green_radious));
                statusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.green_radious_bg));
                statusTopTV.setText(R.string.successful);
                statusTv.setText(R.string.approved);
                statusTv.setTextColor(getResources().getColor(R.color.green_text_color));
                tnxStatusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.success_icon));
                if (tnxType.equals("SALE_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.your_payment_was_successful));
                } else if (tnxType.equals("REFUND_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.refund_payment_was_successful));
                } else if (tnxType.equals("MANUAL_REVERSAL_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.payment_void_was_successful));
                } else if (tnxType.equals("PREAUTH_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.pre_auth_payment_sas_successful));
                }
                break;
            case 2:
                baseLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radius));
                statusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radious_bg));
                statusTopTV.setText(R.string.canceled);
                statusTv.setText(R.string.canceled);
                statusTv.setTextColor(getResources().getColor(R.color.error_color));
                tnxStatusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.cancel_icon));

                if (tnxType.equals("SALE_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.your_payment_was_cancelled));
                } else if (tnxType.equals("REFUND_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.refund_payment_was_cancelled));
                } else if (tnxType.equals("MANUAL_REVERSAL_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.payment_void_was_cancelled));
                } else if (tnxType.equals("PREAUTH_AUTO")) {
                    tnxStatusTxt.setText(getString(R.string.pre_auth_payment_was_cancelled));
                }
                break;
            case 3:
                baseLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radius));
                statusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radious_bg));
                statusTopTV.setText(R.string.refunded);
                statusTv.setText(R.string.refunded);
                statusTv.setTextColor(requireContext().getColor(R.color.error_color));
                tnxStatusIv.setVisibility(View.GONE);
                tnxStatusTxt.setVisibility(View.GONE);
                break;
            case 4:
                baseLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radius));
                statusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radious_bg));
                statusTopTV.setText(R.string.voided);
                statusTv.setText(R.string.voided);
                statusTv.setTextColor(requireContext().getColor(R.color.error_color));
                tnxStatusIv.setVisibility(View.GONE);
                tnxStatusTxt.setVisibility(View.GONE);
                break;
            case 5:
                baseLayout.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radius));
                statusIv.setImageDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.red_radious_bg));
                statusTopTV.setText(R.string.pending);
                statusTv.setText(R.string.pending);
                statusTv.setTextColor(requireContext().getColor(R.color.error_color));
                tnxStatusIv.setVisibility(View.GONE);
                tnxStatusTxt.setVisibility(View.GONE);
                break;
        }

//        viewModel.getTransactionDetailsData("", tnxId).observe(requireActivity(), new Observer<TransactionDetailsResponse>() {
//            @Override
//            public void onChanged(TransactionDetailsResponse transactionDetailsResponse) {
//                if (transactionDetailsResponse != null) {
//                    try {
//                        transactionData = transactionDetailsResponse.getData();
//                        if (transactionDetailsResponse.getData().getTransactionType().equals("SALE_AUTO") && transactionDetailsResponse.getData().isApproved()) {
//                            paymentStatusLayout.setVisibility(View.VISIBLE);
//                        } else {
//                            paymentStatusLayout.setVisibility(View.GONE);
//                        }
//                        tnxTypeTv.setText(transactionDetailsResponse.getData().getTransactionType());
//                        amtTV.setText(String.format("£%s", transactionDetailsResponse.getData().getAmount()));
//                        refundAmtEt.setText(transactionDetailsResponse.getData().getAmount());
//                        dateTV.setText(DateUtils.getFormattedDate(transactionDetailsResponse.getData().getCreatedAt()));
//                        timeTV.setText(DateUtils.getTime(transactionDetailsResponse.getData().getCreatedAt()));
//                        switch (transactionDetailsResponse.getData().getStatus()) {
//                            case 0:
//                                statusTv.setText(R.string.declined);
//                                statusTv.setTextColor(getResources().getColor(R.color.yellow));
//                                break;
//                            case 1:
//                                statusTv.setText(R.string.approved);
//                                statusTv.setTextColor(getResources().getColor(R.color.green_text_color));
//                                break;
//                            case 2:
//                                statusTv.setText(R.string.canceled);
//                                statusTv.setTextColor(getResources().getColor(R.color.error_color));
//                                break;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reversal_btn:
                actionType = "void";
                supervisorPinDialog.show();
                break;
            case R.id.tnx_details_back_btn:
                Navigation.findNavController(view).popBackStack();
//                Navigation.findNavController(view).navigate(R.id.action_transactionDetailsFragment_to_PDFViewFragment);
                break;
            case R.id.print_receipt_btn:
//                templatePDF.createTable(header, getOrdersData());
//                templatePDF.addRightParagraph(longText);
//                templatePDF.addImage(bm);
//                templatePDF.closeDocument();
//                templatePDF.viewPDF();
//                Toast.makeText(requireActivity(), "Features will be available soon", Toast.LENGTH_SHORT).show();
                if (!isPrinting) {
                    isPrinting = true;
                    showLoadingScreen();
                    String transactionType = transaction.getTransactionType();
                    new Thread(new Runnable() {
                        public void run() {
                            Drawable drawable = AppCompatResources.getDrawable(requireContext(), R.drawable.reciept_bw);
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            PrinterTester.getInstance().init();
                            PrinterTester.getInstance().printBitmap(bitmap);
                            PrinterTester.getInstance().setGray(3);
                            PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                                    EFontTypeExtCode.FONT_16_32);
                            PrinterTester.getInstance().printStr(SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TRADING_NAME) + "\n", "UTF-8");
                            PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                                    EFontTypeExtCode.FONT_16_16);
                            PrinterTester.getInstance().printStr(SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TRADING_ADDRESS) + "\n", "UTF-8");
                            PrinterTester.getInstance().printStr("THANK YOU\n\n\n", "UTF-8");

                            PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                                    EFontTypeExtCode.FONT_16_16);
                            PrinterTester.getInstance().printStr("**DUPLICATE RECEIPT**\n\n", "UTF-8");
                            PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                                    EFontTypeExtCode.FONT_16_16);

                            PrinterTester.getInstance().printStr(DateUtils.getFormattedDateUk(tnxDatetime) + "\n", "UTF-8");
                            PrinterTester.getInstance().printStr("MID: ***" + tnxMid.substring(tnxMid.length() - 5) + "\n", "UTF-8");
                            PrinterTester.getInstance().printStr("TID: *****" + tnxTid.substring(tnxTid.length() - 4) + "\n", "UTF-8");
                            PrinterTester.getInstance().printStr("RECEIPT NO.: " + tnxReceiptId + "\n", "UTF-8");
                            if (transaction.getTnxNote() != null) {
                                PrinterTester.getInstance().printStr(formatString("TNX. NOTE: ", transaction.getTnxNote() + "\n", 30), "UTF-8");
                            }

                            PrinterTester.getInstance().printStr("APP SEQ: " + BuildConfig.VERSION_CODE + "\n\n\n", "UTF-8");
                            if (transaction.getCardType() != null && !transaction.getCardType().isEmpty()) {
                                PrinterTester.getInstance().printStr(transaction.getCardType() + "\n", "UTF-8");
                            }
                            if (transaction.getPan() != null && !transaction.getPan().isEmpty()) {
                                PrinterTester.getInstance().printStr(transaction.getPan() + "\n", "UTF-8");
                            }


                            if (transactionType.equals("REFUND_AUTO")) {
                                PrinterTester.getInstance().printStr(formatString("REFUND AMOUNT", "£" + transaction.getAmount(), 30), "UTF-8");
                            } else if (transactionType.equals("CASHBACK_AUTO")) {
                                PrinterTester.getInstance().printStr(formatString("BASE AMOUNT", "£" + transaction.getAmount(), 30), "UTF-8");
                                PrinterTester.getInstance().printStr("\n", "UTF-8");
                                PrinterTester.getInstance().printStr(formatString("CASHBACK AMOUNT", "£" + transaction.getCashbackAmount(), 30), "UTF-8");
                                if (!transaction.getGratuityAmount().equals("0.00")) {
                                    PrinterTester.getInstance().printStr("\n", "UTF-8");
                                    PrinterTester.getInstance().printStr(formatString("Gratuity", "£" + transaction.getGratuityAmount(), 30), "UTF-8");
                                }
                            } else if (transactionType.equals("SALE_AUTO")) {
                                PrinterTester.getInstance().printStr(formatString("SALE AMOUNT", "£" + transaction.getAmount(), 30), "UTF-8");
                            } else if (transactionType.equals("MANUAL_REVERSAL_AUTO")) {
                                PrinterTester.getInstance().printStr(formatString("VOIDED AMOUNT", "£" + transaction.getAmount(), 30), "UTF-8");
                            }
                            if (transactionType.equals("SALE_AUTO") && !transaction.getGratuityAmount().equals("0.00")) {
                                PrinterTester.getInstance().printStr("\n", "UTF-8");
                                PrinterTester.getInstance().printStr(formatString("Gratuity", "£" + transaction.getGratuityAmount(), 30), "UTF-8");
                            }

                            PrinterTester.getInstance().printStr("\n--------------------------------\n", "UTF-8");
                            PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                                    EFontTypeExtCode.FONT_16_16);

                            PrinterTester.getInstance().printStr(formatString("TOTAL", "£" + tnxTotal, 23), "UTF-8");

                            if (transaction.getAuthCode() != null) {
                                PrinterTester.getInstance().printStr("\n\n\n", "UTF-8");
                                PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                                        Byte.parseByte("6"));
                                if (Objects.equals(transaction.getResponseCode(), "00")) {
                                    PrinterTester.getInstance().printStr("     AUTHORISED\n", "UTF-8");
                                } else {
                                    PrinterTester.getInstance().printStr("     NOT AUTHORISED\n", "UTF-8");
                                }
                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                                        EFontTypeExtCode.FONT_16_16);
                                PrinterTester.getInstance().printStr("     RESPONSE CODE : " + transaction.getResponseCode() + "\n", "UTF-8");
                                if (transaction.isPinVerified()) {
                                    PrinterTester.getInstance().printStr("       VERIFIED BY PIN\n", "UTF-8");
                                }

                                PrinterTester.getInstance().printStr("      AUTH CODE : " + transaction.getAuthCode() + "\n", "UTF-8");
                                if (transaction.getErrorText() != null) {
                                    PrinterTester.getInstance().printStr(String.format("(%s\n)", transaction.getErrorText()), "UTF-8");
                                }

                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                                        EFontTypeExtCode.FONT_16_16);
                                PrinterTester.getInstance().printStr("\nUTI : " + transaction.getUti() + "\n", "UTF-8");
                                PrinterTester.getInstance().printStr("RRN : " + transaction.getRrn() + "\n", "UTF-8");
                                PrinterTester.getInstance().printStr("Account : USER\n", "UTF-8");
                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                                        EFontTypeExtCode.FONT_16_16);
                                PrinterTester.getInstance().printStr("  **CARDHOLDER COPY**\n", "UTF-8");
                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                                        EFontTypeExtCode.FONT_16_16);
                                PrinterTester.getInstance().printStr("           PLEASE RETAIN RECEIPTS\n", "UTF-8");
                                PrinterTester.getInstance().printStr("\n\n\n\n\n", "UTF-8");
                            } else {
                                PrinterTester.getInstance().printStr("\n\n\n", "UTF-8");
                                PrinterTester.getInstance().spaceSet(Byte.parseByte("0"),
                                        Byte.parseByte("6"));
                                PrinterTester.getInstance().printStr("     NOT AUTHORISED\n", "UTF-8");
                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_24,
                                        EFontTypeExtCode.FONT_16_16);

                                if (transaction.getErrorText() != null) {
                                    PrinterTester.getInstance().printStr(String.format("(%s)\n", transaction.getErrorText()), "UTF-8");
                                }
                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                                        EFontTypeExtCode.FONT_16_16);
                                PrinterTester.getInstance().printStr("\nUTI : " + transaction.getUti() + "\n", "UTF-8");
                                PrinterTester.getInstance().printStr("RRN : " + transaction.getRrn() + "\n", "UTF-8");
                                PrinterTester.getInstance().printStr("Account : USER\n", "UTF-8");
                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32,
                                        EFontTypeExtCode.FONT_16_16);
                                PrinterTester.getInstance().printStr("  **CARDHOLDER COPY**\n", "UTF-8");
                                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_16,
                                        EFontTypeExtCode.FONT_16_16);
                                PrinterTester.getInstance().printStr("           PLEASE RETAIN RECEIPTS\n", "UTF-8");
                                PrinterTester.getInstance().printStr("\n\n\n\n\n", "UTF-8");
                            }


//                        PrinterTester.getInstance().printStr("--------------------------------\n" +
//                                "      Your Report Name\n" +
//                                "       123 Main Street\n" +
//                                "     City, State, Zip Code\n" +
//                                "   Phone: (123) 456-7890\n" +
//                                "   Date: 2024-01-24 12:34 PM\n" +
//                                "--------------------------------\n" +
//                                "Item         Qty    Price   Total\n" +
//                                "--------------------------------\n" +
//                                "Product 1     2      $10.00  $20.00\n" +
//                                "Product 2     1      $15.00  $15.00\n" +
//                                "--------------------------------\n" +
//                                "Subtotal                      $35.00\n" +
//                                "Tax (7%)                      $2.45\n" +
//                                "--------------------------------\n" +
//                                "Total                         $37.45\n" +
//                                "--------------------------------\n" +
//                                "Thank you for your purchase!\n\n\n\n\n\n\n" +
//                                "--------------------------------", "UTF-8");
                            final String status = PrinterTester.getInstance().start();
                        }
                    }).start();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isPrinting = false;
                            // Dismiss Loading Screen
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                        }
                    }, 2000);
                }
                break;
            case R.id.refundBackButton:
                refundAmountDialog.dismiss();
                break;
            case R.id.payment_status_lt:
//                dialog.show();
//                refundAmountDialog.show();
                actionType = "refund";
                supervisorPinDialog.show();
                break;
            case R.id.run_refund_btn:
                if (refundAmtEt.getText().length() > 0 && Double.parseDouble(refundAmtEt.getText().toString()) > 0.00) {
                    refundAmountDialog.dismiss();
                    float final_amt = Float.parseFloat(refundAmtEt.getText().toString());
                    float main_amt = Float.parseFloat(transaction.getAmount());
                    if (final_amt > main_amt || final_amt < main_amt) {
                        HomeActivity.refundUti = transaction.getUti();
                        HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                        args.put(CT_DISABLEPRINTING, "true");
                        args.put(CT_AMOUNT, String.valueOf(final_amt * 100).split("\\.")[0]);
                        args.put(CT_LANGUAGE, "en_GB");
                        try {
                            PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_REFUND, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Telemetry: TransactionDetailsFragment.onClick.run_refund_btn — refund failed to start.
                            logReceiveError("onClick.run_refund_btn", String.valueOf(e.getMessage()), e);
                        }
                    } else {
                        HomeActivity.refundUti = transaction.getUti();
                        HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                        args.put(CT_DISABLEPRINTING, "true");
                        args.put(CT_AMOUNT, String.valueOf(final_amt * 100).split("\\.")[0]);
                        args.put(CT_LANGUAGE, "en_GB");
                        try {
                            PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_REFUND, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Telemetry: TransactionDetailsFragment.onClick.run_refund_btn — refund failed to start.
                            logReceiveError("onClick.run_refund_btn", String.valueOf(e.getMessage()), e);
                        }
                    }
                }

                break;
        }
    }

    private void backSpace() {
        if (pinPass4.getText().length() > 0) {
            pinPass4.setText("");
        } else if (pinPass3.getText().length() > 0) {
            pinPass3.setText("");
        } else if (pinPass2.getText().length() > 0) {
            pinPass2.setText("");
        } else if (pinPass1.getText().length() > 0) {
            pinPass1.setText("");
        }
    }

    private void setPin(String pin) {
        if (pinPass1.getText().length() == 0) {
            pinPass1.setText(pin);
        } else if (pinPass2.getText().length() == 0) {
            pinPass2.setText(pin);
        } else if (pinPass3.getText().length() == 0) {
            pinPass3.setText(pin);
        } else if (pinPass4.getText().length() == 0) {
            pinPass4.setText(pin);
            verifySupervisorPin();
        }
    }

    private void verifySupervisorPin() {
        showLoadingScreen();
        String pin = pinPass1.getText().toString() + pinPass2.getText().toString() + pinPass3.getText().toString() + pinPass4.getText().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("mid", SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID));
        map.put("serial_no", MainUtils.getDeviceSerial(requireContext()));
        map.put("s_pin", pin);
        pinViewModel.verifySupervisorPin(map).observe(requireActivity(), new Observer<PinVerifyResponse>() {
            @Override
            public void onChanged(PinVerifyResponse pinVerifyResponse) {
                try {
                    pd.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Telemetry: TransactionDetailsFragment.verifySupervisorPin.onChanged — failed to dismiss loading dialog.
                    logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                }
                if (pinVerifyResponse != null && pinVerifyResponse.getCode() == HttpURLConnection.HTTP_OK) {
                    try {
                        supervisorPinDialog.dismiss();
                        pinPass1.setText("");
                        pinPass2.setText("");
                        pinPass3.setText("");
                        pinPass4.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                        // Telemetry: TransactionDetailsFragment.verifySupervisorPin.onChanged — failed to dismiss PIN dialog.
                        logReceiveError("verifySupervisorPin.onChanged", String.valueOf(e.getMessage()), e);
                    }
                    Toast.makeText(requireContext(), "Successfully Admin Verified", Toast.LENGTH_SHORT).show();
                    if (!actionType.isEmpty()) {
                        if (actionType.equals("void")) {
                            try {
                                Log.e(TAG, "onChanged: " + tnxTotal);
                                HomeActivity.refundUti = transaction.getUti();
                                HashMap<PosIntegrate.CONFIG_TYPE, String> conf_args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                                conf_args.put(CT_DISABLEPRINTING, "true");
                                conf_args.put(CT_AMOUNT, tnxTotal.replace(".", "").trim());
//                                conf_args.put(CT_AMOUNT, transaction.getAmount().replace(".", "").trim());
                                conf_args.put(CT_LANGUAGE, "en_GB");
                                conf_args.put(CT_UTI, transaction.getUti());
                                PosIntegrate.executeReversal(getActivity(), conf_args);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(requireActivity(), "Void Transaction Failed !", Toast.LENGTH_SHORT).show();
                                // Telemetry: TransactionDetailsFragment.verifySupervisorPin.onChanged.void — reversal failed to start.
                                logReceiveError("verifySupervisorPin.onChanged.void", String.valueOf(e.getMessage()), e);
                            }
                        }
                        if (actionType.equals("refund")) {
                            refundAmtEt.setText(tnxTotal);
                            refundReasonEt.setText("");
                            refundAmountDialog.show();
                        }
                    }
                } else {
                    resetPin();
                }
            }
        });
    }

    private void resetPin() {
        pinPass1.setText("");
        pinPass2.setText("");
        pinPass3.setText("");
        pinPass4.setText("");
    }

    private void showLoadingScreen() {
        try {
            pd = TransparentProgressDialog.getInstance(requireContext());
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Telemetry: TransactionDetailsFragment.showLoadingScreen — failed to show loading dialog.
            logReceiveError("showLoadingScreen", String.valueOf(e.getMessage()), e);
        }
    }

    /**
     * Sends a fragment failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code TransactionDetailsFragment.onClick.run_refund_btn}.
     * {@code message} must already be a string.
     */
    private void logReceiveError(String functionPath, String message, Throwable throwable) {
        Context context = getContext();
        if (context == null) {
            Log.e(TAG, message != null ? message : "", throwable);
            return;
        }

        String eventName = "TransactionDetailsFragment." + functionPath;
        TelemetryLogger.error(
                SharedHelper.getStringData(context, AppConstants.SharedPref.TID),
                eventName,
                message != null ? message : "",
                throwable
        );
    }
}