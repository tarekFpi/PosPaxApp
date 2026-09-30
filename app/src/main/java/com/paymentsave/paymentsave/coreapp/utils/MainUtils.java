package com.paymentsave.paymentsave.coreapp.utils;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.activities.test.system.SysTester;

public class MainUtils {
    private static final String TAG = "MainUtils";

    public static String getDeviceSerial(Context context) {
        try {
            // String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            // Log.e(TAG, "getDeviceSerial: "+ deviceId);
            return SysTester.getInstance().getTerminfo().split("\n")[0].split(":")[1];
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static BottomSheetDialog getBottomSheet(Activity activity, Context context) {
        View view = activity.getLayoutInflater().inflate(R.layout.action_link_bottom_sheet, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogStyle);
        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.setContentView(view);
        return dialog;
    }

    public static BottomSheetDialog getRefundAmountBottomSheet(Activity activity, Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogStyle);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.refund_amt_bottom_sheet, (LinearLayout) activity.findViewById(R.id.modalBottomSheetContainer));
        bottomSheetDialog.setContentView(bottomSheetView);
        return bottomSheetDialog;
    }

    public static BottomSheetDialog getTnxFilterBottomSheet(Activity activity, Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.transaction_filter_bottom_sheet, (LinearLayout) activity.findViewById(R.id.modalBottomSheetContainer));
        bottomSheetDialog.setContentView(bottomSheetView);
        return bottomSheetDialog;
    }

    public static BottomSheetDialog getSupervisorBottomSheet(Activity activity, Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.supervisor_pin_bottom_sheet, (LinearLayout) activity.findViewById(R.id.modalBottomSheetContainer));
        bottomSheetDialog.setContentView(bottomSheetView);
        return bottomSheetDialog;
    }

    public static BottomSheetDialog getSplitOptionsBottomSheet(Activity activity, Context context) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.split_bill_type_layout, (LinearLayout) activity.findViewById(R.id.modalBottomSheetContainer));
        bottomSheetDialog.setContentView(bottomSheetView);
        return bottomSheetDialog;
    }

    public static boolean validateEditText(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Please enter a value");
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateEditTextWithoutAlert(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public static void hideKeyboard(Context context) {
        try {
            Activity activity = (Activity) context;
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if (activity.getCurrentFocus() != null && (activity.getCurrentFocus().getWindowToken() != null)) {
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
