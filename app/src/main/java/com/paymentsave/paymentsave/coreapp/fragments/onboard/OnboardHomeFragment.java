package com.paymentsave.paymentsave.coreapp.fragments.onboard;

import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_CANCELLED_TIMEOUT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_DISABLEPRINTING;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_RRN;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_UTI;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_COMPLETION;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_PREAUTH;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_REFUND;
import static com.eft.libpositive.PosIntegrate.TRANSACTION_TYPE.TRANSACTION_TYPE_SALE;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.eft.libpositive.PosIntegrate;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.MainUtils;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.cashback.CashbackActivity;
import com.paymentsave.paymentsave.coreapp.activities.gratuity.GratuityActivity;
import com.paymentsave.paymentsave.coreapp.activities.splitBillPay.SplitBillPayActivity;
import com.paymentsave.paymentsave.coreapp.utils.PositiveConstantsUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.weblink.WebLinkIntegrate;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class OnboardHomeFragment extends Fragment {
    private static final String TAG = "OnboardHomeFragment";
    EditText editTextAmount;
    Button nextButton, splitBillBtn;
    TextView invalidText;
    private TextView marqueTv;
    private RelativeLayout marqueLayout;
    private int trans_Type = 0;
    private boolean cashbackEnabled = false;
    private boolean gratuityEnabled = false;
    private boolean preAuthEnabled = false;

    private TextView noteBtn;
    private EditText noteEt;
    private ImageButton clearNoteBtn;

    private BottomSheetDialog splitBillOptionDialog;
    private int equalPeople = 0;
    private int customPeople = 0;
    private TextView equalSplitPeopleTxt, customSplitPeopleTxt;
    private ImageView logoIv;
    private Button splitNextBtn, equalSplitPlusBtn, equalSplitMinusBtn, customSplitPlusBtn, customSplitMinusBtn;

    public OnboardHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (this.getArguments() != null) {
            // get some arguments
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboard_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoIv = view.findViewById(R.id.home_logo_iv);
        String logoSrc = SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.BUSINESS_LOGO);
        Glide.with(requireContext())
                .load(logoSrc)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .placeholder(R.drawable.ic_ptsave)
                .error(R.drawable.ic_ptsave)
                .into(logoIv);

        marqueLayout = view.findViewById(R.id.marquee_layout);
        marqueTv = view.findViewById(R.id.alert_wrap_txt);
        if (!AppConstants.alertText.isEmpty()) {
            marqueLayout.setVisibility(View.VISIBLE);
            marqueTv.setSelected(true);
            marqueTv.setText(AppConstants.alertText);
        } else {
            marqueLayout.setVisibility(View.GONE);
        }

        // Split Payment Dialog Starts
        splitBillOptionDialog = MainUtils.getSplitOptionsBottomSheet(requireActivity(), requireContext());
        equalSplitPeopleTxt = splitBillOptionDialog.findViewById(R.id.equal_split_peoples_txt);
        customSplitPeopleTxt = splitBillOptionDialog.findViewById(R.id.custom_split_people_txt);
        equalSplitPlusBtn = splitBillOptionDialog.findViewById(R.id.equal_split_plus);
        equalSplitMinusBtn = splitBillOptionDialog.findViewById(R.id.equal_split_minus);
        customSplitPlusBtn = splitBillOptionDialog.findViewById(R.id.custom_split_plus);
        customSplitMinusBtn = splitBillOptionDialog.findViewById(R.id.custom_split_minus);
        splitNextBtn = splitBillOptionDialog.findViewById(R.id.split_option_next_btn);
        // Split Payment Dialog Ends

        // Setup any handles to view objects here
        editTextAmount = view.findViewById(R.id.ed_enter_amount);
        final EditText editTextRRN = view.findViewById(R.id.ed_enter_rrn_number);
        final EditText editTextCancelTime = view.findViewById(R.id.ed_enter_cancel_time);

        invalidText = view.findViewById(R.id.invalid_amount_text);
        Button oneBtn = view.findViewById(R.id.btnOne);
        Button twoBtn = view.findViewById(R.id.btnTwo);
        Button threeBtn = view.findViewById(R.id.btnThree);
        Button fourBtn = view.findViewById(R.id.btnFour);
        Button fiveBtn = view.findViewById(R.id.btnFive);
        Button sixBtn = view.findViewById(R.id.btnSix);
        Button sevenBtn = view.findViewById(R.id.btnSeven);
        Button eightBtn = view.findViewById(R.id.btnEight);
        Button nineBtn = view.findViewById(R.id.btnNine);
        Button zeroBtn = view.findViewById(R.id.btnZero);
        Button doubleZeroBtn = view.findViewById(R.id.btnDoubleZero);
        ImageButton backSpaceBtn = view.findViewById(R.id.btnC);

        oneBtn.setOnClickListener(this::numberEvent);
        twoBtn.setOnClickListener(this::numberEvent);
        threeBtn.setOnClickListener(this::numberEvent);
        fourBtn.setOnClickListener(this::numberEvent);
        fiveBtn.setOnClickListener(this::numberEvent);
        sixBtn.setOnClickListener(this::numberEvent);
        sevenBtn.setOnClickListener(this::numberEvent);
        eightBtn.setOnClickListener(this::numberEvent);
        nineBtn.setOnClickListener(this::numberEvent);
        zeroBtn.setOnClickListener(this::numberEvent);
        doubleZeroBtn.setOnClickListener(this::numberEvent);
        backSpaceBtn.setOnClickListener(this::numberEvent);

        editTextAmount.setShowSoftInputOnFocus(false);
        editTextRRN.setShowSoftInputOnFocus(false);
        editTextCancelTime.setShowSoftInputOnFocus(false);


        noteBtn = view.findViewById(R.id.note_btn);
        clearNoteBtn = view.findViewById(R.id.btnClose);
        noteEt = view.findViewById(R.id.note_et);

        clearNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.hideSoftInputFromWindow(noteEt.getWindowToken(), 0);
                }

                noteEt.setText("");
                noteEt.setVisibility(View.GONE);
                AppConstants.tnxNote = "";
                noteBtn.setVisibility(View.VISIBLE);
                noteBtn.setText(getString(R.string.add_note));
                clearNoteBtn.setVisibility(View.GONE);
            }
        });

        noteEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (noteEt.getText().toString()
                        .isEmpty()) {
                    clearNoteBtn.setVisibility(View.GONE);
                } else {
                    clearNoteBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        noteEt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)
                        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.hideSoftInputFromWindow(noteEt.getWindowToken(), 0);
                }

                noteEt.setVisibility(View.GONE);
                if (noteEt.getText().toString()
                        .isEmpty()) {
                    noteBtn.setVisibility(View.VISIBLE);
                    noteBtn.setText(getString(R.string.add_note));
                    clearNoteBtn.setVisibility(View.GONE);
                } else {
                    noteBtn.setVisibility(View.VISIBLE);
                    AppConstants.tnxNote = noteEt.getText().toString();
                    noteBtn.setText(noteEt.getText().toString());
                    clearNoteBtn.setVisibility(View.VISIBLE);
                }
                noteEt.clearFocus();
                return true;
            }
            return false;
        });

        noteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteBtn.setVisibility(View.GONE);
                noteEt.setVisibility(View.VISIBLE);

                noteEt.post(() -> {
                    noteEt.requestFocus();
                    noteEt.setCursorVisible(true);

                    InputMethodManager imm = (InputMethodManager)
                            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm != null) {
                        imm.showSoftInput(noteEt, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });

        equalSplitPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customPeople = 0;
                customSplitPeopleTxt.setText(String.format("%s", customPeople));

                equalPeople = equalPeople + 1;
                equalSplitPeopleTxt.setText(String.format("%s", equalPeople));
            }
        });
        equalSplitMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (equalPeople != 0) {
                    customPeople = 0;
                    customSplitPeopleTxt.setText(String.format("%s", customPeople));

                    equalPeople = equalPeople - 1;
                    equalSplitPeopleTxt.setText(String.format("%s", equalPeople));
                }
            }
        });
        customSplitPlusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                equalPeople = 0;
                equalSplitPeopleTxt.setText(String.format("%s", equalPeople));

                customPeople = customPeople + 1;
                customSplitPeopleTxt.setText(String.format("%s", customPeople));
            }
        });
        customSplitMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customPeople != 0) {
                    equalPeople = 0;
                    equalSplitPeopleTxt.setText(String.format("%s", equalPeople));

                    customPeople = customPeople - 1;
                    customSplitPeopleTxt.setText(String.format("%s", customPeople));
                }
            }
        });

        splitNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (equalPeople > 0 || customPeople > 0) {
                    splitBillOptionDialog.dismiss();

                    Intent intent = new Intent(requireContext(), SplitBillPayActivity.class);
                    intent.putExtra("total_amount", editTextAmount.getText().toString().split("£")[1]);
                    if (equalPeople > 0) {
                        intent.putExtra("total_people", equalPeople);
                        intent.putExtra("is_equal_split", true);
                    }
                    if (customPeople > 0) {
                        intent.putExtra("total_people", equalPeople);
                        intent.putExtra("is_equal_split", false);
                    }
                    requireContext().startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), getString(R.string.please_select_at_least_1_people), Toast.LENGTH_SHORT).show();
                }
            }
        });

        splitBillBtn = view.findViewById(R.id.split_bill_btn);
        boolean splitBillEnableValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.SPLIT_BILL);
        if (splitBillEnableValue) {
            splitBillBtn.setVisibility(View.VISIBLE);
        } else {
            splitBillBtn.setVisibility(View.GONE);
        }
        splitBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Float.parseFloat(editTextAmount.getText().toString().split("£")[1]) > 0) {
                    equalPeople = 0;
                    customPeople = 0;
                    equalSplitPeopleTxt.setText(String.format("%s", equalPeople));
                    customSplitPeopleTxt.setText(String.format("%s", customPeople));
                    splitBillOptionDialog.show();
                } else {
                    invalidText.setVisibility(View.VISIBLE);
                }
            }
        });

        nextButton = view.findViewById(R.id.button_submit);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Float.parseFloat(editTextAmount.getText().toString().split("£")[1]) > 0) {
                    // Hide Keyboard
                    MainUtils.hideKeyboard(view.getContext());
                    HashMap<PosIntegrate.CONFIG_TYPE, String> args = new HashMap<PosIntegrate.CONFIG_TYPE, String>();
                    float final_amt = Float.parseFloat(editTextAmount.getText().toString().split("£")[1]);

                    // String nAmount = String.valueOf(final_amt).replace(".", "").trim();
                    String main_amt = editTextAmount.getText().toString().split("£")[1];
                    String nAmount = editTextAmount.getText().toString().split("£")[1].replace(".", "").trim();

                    // =====================================================================
                    // TELEMETRY INFO: nextButton click — nAmount + current datetime
                    // =====================================================================
                    JSONObject amountJson = new JSONObject();
                    try {
                        amountJson.put("nAmount", nAmount);
                        amountJson.put("main_amt", main_amt);
                        amountJson.put("trans_type", trans_Type);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            amountJson.put(
                                    "Created At",
                                    formatter.format(ZonedDateTime.now(ZoneId.of("Europe/London")))
                            );
                        }
                    } catch (JSONException e) {
                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton — failed to build amount JSON.
                        logReceiveError("onViewCreated.nextButton", String.valueOf(e.getMessage()), e);
                    }

                    // Telemetry: OnboardHomeFragment.onViewCreated.nextButton — amount submitted with current datetime.
                    TelemetryLogger.info(
                            SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.TID),
                            TAG + ".onViewCreated.nextButton",
                            String.valueOf(amountJson)
                    );
                    // =====================================================================
                    // END TELEMETRY INFO: nextButton click — nAmount + current datetime
                    // =====================================================================


                    if (cashbackEnabled) {
                        Intent intent = new Intent(getActivity(), CashbackActivity.class);
                        intent.putExtra("main_amt", main_amt);
                        intent.putExtra("trans_type", trans_Type);
                        requireContext().startActivity(intent);
                    } else if (gratuityEnabled) {
                        Intent intent = new Intent(getActivity(), GratuityActivity.class);
                        intent.putExtra("main_amt", main_amt);
                        intent.putExtra("trans_type", trans_Type);
                        requireContext().startActivity(intent);
                    } else {
                        boolean printReceiptValue = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.RECEIPT_PRINTING);
                        args.put(CT_AMOUNT, nAmount);
                        // args.put(CT_AMOUNT_CASHBACK, "0");
//                        args.put(CT_DISABLEPRINTING, String.valueOf(printReceiptValue));
                        args.put(CT_DISABLEPRINTING, "true");
                        args.put(CT_LANGUAGE, "en_GB");
                        if (WebLinkIntegrate.enabled) {
                            try {
                                switch (trans_Type) {
                                    case PositiveConstantsUtils.SALE:
                                        WebLinkIntegrate.executeTransaction(getActivity(), "SALE", args);
                                        break;
                                    case PositiveConstantsUtils.REFUND:
                                        args.put(CT_RRN, editTextRRN.getText().toString());
                                        WebLinkIntegrate.executeTransaction(getActivity(), "REFUND", args);
                                        break;
                                    case PositiveConstantsUtils.REVERSE_LAST:
                                        WebLinkIntegrate.executeTransaction(getActivity(), "REVERSAL", args);
                                        break;
                                    case PositiveConstantsUtils.REVERSE_BY_UTI:
                                        args.put(CT_UTI, PositiveConstantsUtils.lastReceivedUTI);
                                        PosIntegrate.executeReversal(getActivity(), args);
                                        break;
                                    case PositiveConstantsUtils.PREAUTH:
                                        WebLinkIntegrate.executeTransaction(getActivity(), "PREAUTH", args);
                                        break;
                                    case PositiveConstantsUtils.COMPLETION:
                                        args.put(CT_RRN, editTextRRN.getText().toString());
                                        WebLinkIntegrate.executeTransaction(getActivity(), "COMPLETION", args);
                                        break;
                                    case PositiveConstantsUtils.CANCEL_TRANSACTION:
                                        args.put(CT_CANCELLED_TIMEOUT, editTextCancelTime.getText().toString());
                                        PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_SALE, args);
                                        break;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "WebLink execute failed: " + e);
                                // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.WebLink — transaction failed to start.
                                logReceiveError("onViewCreated.nextButton.WebLink", String.valueOf(e.getMessage()), e);
                            }
                        } else {

                            switch (trans_Type) {
                                case PositiveConstantsUtils.SALE:
                                    try {
                                        PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_SALE, args);

                                    } catch (Exception e) {
                                        Log.e(TAG, "Run Sale: " + e);
                                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.SALE — sale failed to start.
                                        logReceiveError("onViewCreated.nextButton.SALE", String.valueOf(e.getMessage()), e);
                                    }
                                    break;
                                case PositiveConstantsUtils.REFUND:
                                    try {
                                        PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_REFUND, args);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Run Refund: " + e);
                                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.REFUND — refund failed to start.
                                        logReceiveError("onViewCreated.nextButton.REFUND", String.valueOf(e.getMessage()), e);
                                    }
                                    break;
                                case PositiveConstantsUtils.REVERSE_LAST:
                                    try {
                                        PosIntegrate.executeReversal(getActivity(), args);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Reverse last: " + e);
                                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.REVERSE_LAST — reversal failed to start.
                                        logReceiveError("onViewCreated.nextButton.REVERSE_LAST", String.valueOf(e.getMessage()), e);
                                    }
                                    break;
                                case PositiveConstantsUtils.REVERSE_BY_UTI:
                                    try {
                                        args.put(CT_UTI, PositiveConstantsUtils.lastReceivedUTI);
                                        PosIntegrate.executeReversal(getActivity(), args);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Reverse by UTI: " + e);
                                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.REVERSE_BY_UTI — reversal failed to start.
                                        logReceiveError("onViewCreated.nextButton.REVERSE_BY_UTI", String.valueOf(e.getMessage()), e);
                                    }
                                    break;
                                case PositiveConstantsUtils.PREAUTH:
                                    try {
                                        PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_PREAUTH, args);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Run PreAuth: " + e);
                                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.PREAUTH — preauth failed to start.
                                        logReceiveError("onViewCreated.nextButton.PREAUTH", String.valueOf(e.getMessage()), e);
                                    }
                                    break;
                                case PositiveConstantsUtils.COMPLETION:
                                    try {
                                        args.put(CT_RRN, editTextRRN.getText().toString());
                                        PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_COMPLETION, args);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Run Completion: " + e);
                                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.COMPLETION — completion failed to start.
                                        logReceiveError("onViewCreated.nextButton.COMPLETION", String.valueOf(e.getMessage()), e);
                                    }
                                    break;
                                case PositiveConstantsUtils.CANCEL_TRANSACTION:
                                    try {
                                        args.put(CT_CANCELLED_TIMEOUT, editTextCancelTime.getText().toString());
                                        PosIntegrate.executeTransaction(getActivity(), TRANSACTION_TYPE_SALE, args);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Cancel transaction: " + e);
                                        // Telemetry: OnboardHomeFragment.onViewCreated.nextButton.CANCEL_TRANSACTION — cancel sale failed to start.
                                        logReceiveError("onViewCreated.nextButton.CANCEL_TRANSACTION", String.valueOf(e.getMessage()), e);
                                    }
                                    break;
                            }
                        }
                    }
                } else {
                    invalidText.setVisibility(View.VISIBLE);
                }
            }
        });

        cashbackEnabled = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.CASHBACK);
        gratuityEnabled = SharedHelper.getBooleanData(requireContext(), AppConstants.SharedPref.GRATUITY);
        preAuthEnabled = SharedHelper.getPreAuthData(requireContext(), AppConstants.SharedPref.PRE_AUTH);
        if (preAuthEnabled) {
            trans_Type = 4;
            nextButton.setText(getResources().getString(R.string.pre_auth));
        } else {
            trans_Type = 0;
            nextButton.setText(getResources().getString(R.string.run_sale));
        }

        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (Float.parseFloat(charSequence.toString().split("£")[1]) > 0) {
                    invalidText.setVisibility(View.GONE);
                    nextButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_btn_bg));
                    splitBillBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_btn_bg));
                } else {
                    nextButton.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_gray));
                    splitBillBtn.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.radius_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (WebLinkIntegrate.enabled) {
            Log.i(TAG, "WeblinkEnabled");
            if (trans_Type == PositiveConstantsUtils.COMPLETION || trans_Type == PositiveConstantsUtils.REFUND) {
                editTextRRN.setVisibility(View.VISIBLE);
                editTextRRN.setHint("Enter ID Here");
                if (PositiveConstantsUtils.lastReceivedRRN != null && PositiveConstantsUtils.lastReceivedRRN.length() > 0)
                    editTextRRN.setText(PositiveConstantsUtils.lastReceivedRRN);
                if (PositiveConstantsUtils.lastReceivedAmount != null && PositiveConstantsUtils.lastReceivedAmount.length() > 0)
                    editTextAmount.setText(PositiveConstantsUtils.lastReceivedAmount);
                editTextRRN.setInputType(InputType.TYPE_CLASS_TEXT);

            } else if (trans_Type == PositiveConstantsUtils.REVERSE_LAST || trans_Type == PositiveConstantsUtils.REVERSE_BY_UTI) {
                if (PositiveConstantsUtils.lastReceivedAmount != null && PositiveConstantsUtils.lastReceivedAmount.length() > 0)
                    editTextAmount.setText(PositiveConstantsUtils.lastReceivedAmount);
            }
            // if(trans_Type == CANCEL_TRANSACTION) {
            //     editTextCancelTime.setVisibility(View.VISIBLE);
            // }
        } else {
            Log.i(TAG, "Not WebLinked");
            if (trans_Type == PositiveConstantsUtils.COMPLETION) {
                editTextRRN.setVisibility(View.VISIBLE);
            }
            if (trans_Type == PositiveConstantsUtils.CANCEL_TRANSACTION) {
                editTextCancelTime.setVisibility(View.VISIBLE);
            }
        }

  //  InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//   imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onResume() {
        if (AppConstants.tnxNote.isEmpty()) {
            noteEt.setText("");
            noteBtn.setText(getString(R.string.add_note));
        }
        super.onResume();
    }

    public void numberEvent(View view) {

        String number = editTextAmount.getText().toString();

        if (view.getId() == R.id.btnOne) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.01;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnTwo) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.02;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnThree) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.03;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnFour) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.04;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnFive) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.05;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnSix) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.06;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnSeven) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.07;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnEight) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.08;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnNine) {
            double currentValue = Double.parseDouble(number.split("£")[1]);
            double updatedDecimal = currentValue * 10 + 0.09;
            number = "£" + String.format("%.2f", updatedDecimal);
        } else if (view.getId() == R.id.btnZero) {
            double currentValue = Double.parseDouble(number.split("£")[1]) * 10;
            number = "£" + String.format("%.2f", currentValue);
        } else if (view.getId() == R.id.btnDoubleZero) {
            double currentValue = Double.parseDouble(number.split("£")[1]) * 100;
            number = "£" + String.format("%.2f", currentValue);
        } else if (view.getId() == R.id.btnC) {
            float currentValue = Float.parseFloat(number.split("£")[1]);
//            float afterValue = (float) (currentValue * 0.1);
//            String value =String.format("%.3f", afterValue);
//            Log.e(TAG, "numberEvent: "+value);
//            String vv =  String.valueOf(currentValue * 100).split("\\.")[0];
//            Log.e(TAG, "numberEvent: "+vv);
            if (currentValue > 0.01) {
                double afterValue = currentValue * 0.1;
                String strValue = String.format("%.3f", afterValue);
//                String strValue = String.valueOf(afterValue);
//                Log.e(TAG, "numberEvent: " + strValue);
                if (afterValue < 0.00) {
                    number = "£0.00";
                } else {
                    try {
                        int dot_index = strValue.indexOf(".");
                        number = "£" + strValue.substring(0, dot_index) + strValue.substring(dot_index, (dot_index + 3));
                    } catch (Exception e) {
                        number = "£" + strValue + "0";
                        e.printStackTrace();
                        // Telemetry: OnboardHomeFragment.numberEvent — keypad backspace format failed.
                        logReceiveError("numberEvent", String.valueOf(e.getMessage()), e);
                    }
                }
            } else {
                number = "£0.00";
            }
        }
        float number_f = Float.parseFloat(number.split("£")[1]);
        if (number_f > 99999.99) {
            Toast.makeText(requireContext(), "Max digit limit exceeded", Toast.LENGTH_SHORT).show();
        } else {
            editTextAmount.setText(number);
        }
    }

    /**
     * Sends a fragment failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code OnboardHomeFragment.onViewCreated.nextButton.SALE}.
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