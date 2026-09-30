package com.paymentsave.paymentsave.coreapp.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.paymentsave.paymentsave.coreapp.activities.serviceAbort.ServiceAbortActivity;
import com.paymentsave.paymentsave.coreapp.activities.splash.SplashActivity;
import com.paymentsave.paymentsave.network.SharedHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;

public class ErrorUtils {
    private static final String TAG = "ErrorUtils";

    public static void handleError(Context context, ResponseBody baseResponse) {
        try {
            JSONObject jObjError = new JSONObject(baseResponse.string());
            if (Integer.parseInt(jObjError.get("code").toString()) == 401) {
                Toasty.error(context, "Session Expired", Toast.LENGTH_SHORT, true).show();
                SharedHelper.getLogOut(context);
                Intent intent = new Intent(context, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
            String message = jObjError.getString("message");
            if (!message.equals("null")) {
                Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
            }
            JSONObject errors = jObjError.getJSONObject("errors");
            if (errors.length() != 0) {
                Iterator<String> keys = errors.keys();
                JSONArray errorMsg = (JSONArray) errors.get(keys.next());
                Toasty.error(context, errorMsg.get(0).toString(), Toast.LENGTH_SHORT, true).show();
            }
            if (Integer.parseInt(jObjError.get("code").toString()) == 503) {
                Intent intent = new Intent(context, ServiceAbortActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
