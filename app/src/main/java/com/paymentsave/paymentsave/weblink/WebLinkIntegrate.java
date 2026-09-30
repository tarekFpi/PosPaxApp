package com.paymentsave.paymentsave.weblink;


import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_CASHBACK;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_AMOUNT_GRATUITY;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_LANGUAGE;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_RRN;
import static com.eft.libpositive.PosIntegrate.CONFIG_TYPE.CT_UTI;
import static com.eft.libpositive.wrappers.PositiveError.INVALID_ARG;
import static com.eft.libpositive.wrappers.PositiveError.NO_ERROR;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.eft.libpositive.PosIntegrate;
import com.eft.libpositive.wrappers.PositiveError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.coreapp.utils.PositiveConstantsUtils;
import com.paymentsave.paymentsave.coreapp.utils.TelemetryLogger;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.TransactionResponse;

import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class WebLinkIntegrate {

    private static final String TAG = "WebLinkIntegrate";
    public static boolean enabled = false;

    public static void updateUI(Activity activity, String title, ArrayList<TransactionResponse> responseList) {

//        if (activity instanceof CustomActivity) {
//            try {
//                Fragment fragmentToLaunch = new ResponseFragment();
//                Bundle intent = new Bundle();
//                intent.putParcelableArrayList("ResponseList", responseList);
//                intent.putString("Title", title);
//                fragmentToLaunch.setArguments(intent);
//                FragmentManager fragmentManager = activity.getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.custom_fragment, fragmentToLaunch).commitAllowingStateLoss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

//        if (activity instanceof MainActivity) {
//            try {
//                Intent intent = new Intent(activity, CustomActivity.class);
//                intent.putExtra("fragment_to_launch", MainActivity.RESPONSE_FRAGMENT);
//                intent.putExtra("Screen_Title", "Query");
//                intent.putExtra("Trans_Type", MainActivity.QUERY_TRANSACTION);
//                Bundle b = new Bundle();
//                b.putParcelableArrayList("ResponseList", responseList);
//                b.putString("Title", title);
//                intent.putExtras(b);
//                activity.startActivity(intent);
//            } catch ( Exception e ) {
//                e.printStackTrace();
//            }
//        }
    }

    public static PositiveError executeTransaction(Activity activity, String transactionType, HashMap<PosIntegrate.CONFIG_TYPE, String> args) {

        JSONObject j = new JSONObject();

        try {
            j.put("transType", transactionType);
            j.put("amountTrans", getIntFromString(args.get(CT_AMOUNT), 100));
            j.put("amountGratuity", getIntFromString(args.get(CT_AMOUNT_GRATUITY),0));
            j.put("amountCashback", getIntFromString(args.get(CT_AMOUNT_CASHBACK),0));

            if (args.get(CT_UTI) != null && args.get(CT_UTI).length() > 0)
                j.put("uti", getIntFromString(args.get(CT_UTI),0)); // only really for reversals and completions
            j.put("retrievalReferenceNumber", args.get(CT_RRN)); // only really for reversals and completions
            j.put("language", args.get(CT_LANGUAGE));
            j.put("reference", "Tst Transaction");

            performPost(activity, j);

        } catch ( Exception e) {
            e.printStackTrace();
            // Telemetry: WebLinkIntegrate.executeTransaction — failed to build or send the weblink request.
            logReceiveError(activity, "executeTransaction", String.valueOf(e.getMessage()), e);
        }
        return NO_ERROR;
    }


    public static PositiveError queryTransaction(Activity activity, HashMap<PosIntegrate.CONFIG_TYPE, String> args) {

        JSONObject j = new JSONObject();
        String uti = args.get(PosIntegrate.CONFIG_TYPE.CT_UTI);
        if (uti == null || uti.isEmpty())
            return INVALID_ARG;

        performGet(activity, uti, false);

        return NO_ERROR;
    }

    private class StatusEvent {
        public String statusDescription;
        public int statusValue;

        public StatusEvent(String statusDescription, int statusValue) {
            this.statusDescription = statusDescription;
            this.statusValue = statusValue;
        }
    }

    private static void jsonDebug(Activity activity, JSONObject json) {
        try {
            Log.i(TAG, "POST RESPONSE:" + json.toString(3));

            ArrayList responseList = new ArrayList<TransactionResponse>();
            for(int i = 0; i<json.names().length(); i++){
                String keyname = json.names().getString(i);
                String keyvalue = json.getString(json.names().getString(i));
                Log.v(TAG, "key = " + json.names().getString(i) + " value = " + keyvalue);
                if (keyname.contains("Statuses")) {

                    Gson gson = new Gson();
                    ArrayList<StatusEvent> events = gson.fromJson(keyvalue, new TypeToken<ArrayList<StatusEvent> >(){}.getType());
                    for (StatusEvent e : events ) {
                        responseList.add(new TransactionResponse("Status:" + e.statusValue, e.statusDescription));
                    }
                } else {
                    responseList.add(new TransactionResponse(keyname, keyvalue));
                }
            }
            updateUI(activity, "JSON Debug", responseList);

        } catch ( Exception e ) {
            e.printStackTrace();
            // Telemetry: WebLinkIntegrate.jsonDebug — failed to parse weblink response JSON.
            logReceiveError(activity, "jsonDebug", String.valueOf(e.getMessage()), e);
        }
    }

    private static void processPostResult(Activity activity, JSONObject postResult) {

        try {
            String uti = postResult.getString("uti");
            PositiveConstantsUtils.lastReceivedUTI = uti;
            if (uti != null && uti.length() > 0 ) {
                performGet(activity, uti, true);
            }

        } catch ( Exception e) {
            e.printStackTrace();
            // Telemetry: WebLinkIntegrate.processPostResult — failed to read UTI from weblink POST result.
            logReceiveError(activity, "processPostResult", String.valueOf(e.getMessage()), e);
        }
    }


    private static void processGetResult(Activity activity, JSONObject getResult, String uti, boolean recursive) {

        try {

            if (getResult != null && getResult.has("retrievalReferenceNumber"))
                PositiveConstantsUtils.lastReceivedRRN = getResult.getString("retrievalReferenceNumber");
            if (getResult != null && getResult.has("amountTrans"))
                PositiveConstantsUtils.lastReceivedAmount = getResult.getString("amountTrans");

            if (getResult.has("transApproved")) {
                Log.i(TAG, "Transaction Finished");
                return;
            } else if ( recursive ){

                Thread.sleep(500);
                performGet(activity, uti, recursive);
            }

        } catch ( Exception e) {
            e.printStackTrace();
            // Telemetry: WebLinkIntegrate.processGetResult — failed to process weblink GET result.
            logReceiveError(activity, "processGetResult", String.valueOf(e.getMessage()), e);
        }
    }

    private static void performGet(Activity activity, String uti, boolean recursive) {

        try {
            // to prevent recursive calls to get we put it on a new thread, so the last thread can die without hanging around
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AndroidNetworking.get("https://192.168.0.124:8080/POSitiveWebLink/1.0.0/rest/transaction?disablePrinting=true&uti=" + uti + "&tid=" + Build.SERIAL)
                            .setPriority(Priority.MEDIUM)
                            .addHeaders("Authorization", "Bearer 5870101439530719")
                            .setOkHttpClient(getLocalHttpClient(activity))
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    jsonDebug(activity, response);
                                    processGetResult(activity, response, uti, recursive);
                                }

                                @Override
                                public void onError(ANError error) {
                                    updateUI(activity, "Network Error", null);
                                }
                            });
                }
            }).start();

        } catch (Exception ex) {
            ex.printStackTrace();
            // Telemetry: WebLinkIntegrate.performGet — failed to start weblink GET request.
            logReceiveError(activity, "performGet", String.valueOf(ex.getMessage()), ex);
        }
    }

    private static void performPost(Activity activity, JSONObject request) {

            try {

                AndroidNetworking.post("https://192.168.0.124:8080/POSitiveWebLink/1.0.0/rest/transaction?disablePrinting=true&tid=" + Build.SERIAL)
                        .addStringBody(request.toString(3))
                        .addHeaders("Authorization", "Bearer 5870101439530719")
                        .setPriority(Priority.MEDIUM)
                        .setOkHttpClient(getLocalHttpClient(activity))
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                jsonDebug(activity, response);
                                processPostResult(activity, response);
                            }

                            @Override
                            public void onError(ANError error) {
                                updateUI(activity, "Network Error", null);
                            }
                        });
            } catch ( Exception e ) {
                e.printStackTrace();

                // Telemetry: WebLinkIntegrate.performPost — failed to start weblink POST request.
                logReceiveError(activity, "performPost", String.valueOf(e.getMessage()), e);
            }
    }


    private static int getIntFromString(String str, int defaultValue) {
        if (isValidString(str)) {
            try {
                return Integer.valueOf(str);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }
    private static boolean isValidString(String text) {
        return !(text == null || text.trim().isEmpty());
    }

    private static OkHttpClient getLocalHttpClient(Activity activity) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(120, TimeUnit.SECONDS);
            builder.readTimeout(120, TimeUnit.SECONDS);
            builder.writeTimeout(120, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            // Telemetry: WebLinkIntegrate.getLocalHttpClient — failed to build TLS client.
            logReceiveError(activity, "getLocalHttpClient", String.valueOf(e.getMessage()), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a weblink failure to OpenObserve.
     * Event name is the class plus the method path, e.g.
     * {@code WebLinkIntegrate.executeTransaction}.
     * {@code message} must already be a string.
     */
    private static void logReceiveError(Activity activity, String functionPath, String message, Throwable throwable) {
        String tid = "";
        if (activity != null) {
            tid = SharedHelper.getStringData(activity, AppConstants.SharedPref.TID);
        }

        String eventName = TAG + "." + functionPath;
        TelemetryLogger.error(
                tid,
                eventName,
                message != null ? message : "",
                throwable
        );
    }
}

