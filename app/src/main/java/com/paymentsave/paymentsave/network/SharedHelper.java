package com.paymentsave.paymentsave.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.BuildConfig;
import com.paymentsave.paymentsave.responses.DeviceConfigResponse.BusinessInfo;

import java.util.List;


public class SharedHelper {

    private static final String TAG = "SharedHelper";
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public SharedHelper(Context context) {
        SharedHelper.context = context;
    }

    public static void putBusinessInfo(BusinessInfo businessInfo, String tid, String support_email, String support_phone, String gratuity_type, boolean linkPaymentEnabled, List<Object> gratuity_options, List<Object> cashback_options,Boolean isEReceipt) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(AppConstants.SharedPref.USER_ID, businessInfo.getMerchant());
        editor.putString(AppConstants.SharedPref.LEGAL_NAME, businessInfo.getLegalName());
        editor.putString(AppConstants.SharedPref.TRADING_NAME, businessInfo.getTradingName());
        editor.putInt(AppConstants.SharedPref.BUSINESS_ID, businessInfo.getId());
        editor.putString(AppConstants.SharedPref.BUSINESS_LOGO, businessInfo.getLogo());
        editor.putString(AppConstants.SharedPref.BUSINESS_EMAIL, businessInfo.getBusinessEmail());
        editor.putString(AppConstants.SharedPref.BUSINESS_PHONE, businessInfo.getBusinessPhoneNumber());
        editor.putString(AppConstants.SharedPref.TRADING_ADDRESS, businessInfo.getTradingAddress());
        editor.putString(AppConstants.SharedPref.MID, businessInfo.getMid());
        editor.putString(AppConstants.SharedPref.TID, tid);
        editor.putString(AppConstants.SharedPref.GRATUITY_TYPE, gratuity_type);

        editor.putBoolean(AppConstants.SharedPref.ERECEIPT, isEReceipt);

        Log.e(TAG, "putBusinessInfo: "+linkPaymentEnabled );
        editor.putBoolean(AppConstants.SharedPref.LINK_PAYMENT_ENABLED, linkPaymentEnabled);

        Gson gson = new Gson();
        String gratuity_options_json = gson.toJson(gratuity_options);
        editor.putString(AppConstants.SharedPref.GRATUITY_OPTIONS, gratuity_options_json);

        String cashback_options_json = gson.toJson(cashback_options);
        editor.putString(AppConstants.SharedPref.CASHBACK_OPTIONS, cashback_options_json);

        editor.putString(AppConstants.SharedPref.CONTACT_SUPPORT_EMAIL, support_email);
        editor.putString(AppConstants.SharedPref.CONTACT_SUPPORT_PHONE, support_phone);
        editor.apply();
    }

    public static void putKeyFCM(Context context, String Key, String Value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID + ".fcm", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public static void putDeviceStatus(Context context, String Key, Boolean Value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }

    public static String getKeyFCM(Context context, String Key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID + ".fcm", Context.MODE_PRIVATE);
        return sharedPreferences.getString(Key, "");
    }

    public static void putBooleanData(Context context, String key, boolean value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getPreAuthData(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static void putGratuityData(Context context, String key, boolean value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanData(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static int getIntData(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static String getStringData(Context context, String key) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void putStringData(Context context, String key, String value) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

//
//    public static void putConnectionStatus(Context context, int id) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putInt(AppConstants.SOCKET_CONNECTION, id);
//        editor.apply();
//    }
//
//    public static int getConnectionStatus(Context context, String key) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getInt(key, 0);
//    }
//
//    public static void putUserImageId(Context context, int id) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putInt(AppConstants.USER_IMAGE_ID, id);
//        editor.apply();
//    }
//
//    public static void putRootViewHeight(Context context, int height) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putInt(AppConstants.ROOT_VIEW_HEIGHT, height);
//        editor.apply();
//    }
//
//    public static int getRootViewHeight(Context context) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getInt(AppConstants.ROOT_VIEW_HEIGHT, 0);
//    }
//
//    public static void putUser(Context context, ProfileResponse profileResponse) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putString(AppConstants.FIRST_NAME, profileResponse.getData().getFirstName());
//        editor.putString(AppConstants.LAST_NAME, profileResponse.getData().getLastName());
//        editor.putString(AppConstants.USER_NAME, profileResponse.getData().getFirstName() + " " + profileResponse.getData().getLastName());
//        editor.putString(AppConstants.USER_IMAGE, profileResponse.getData().getImageUrl() != null ? profileResponse.getData().getImageUrl().toString() : "");
//        editor.putInt(AppConstants.USER_ID, profileResponse.getData().getId());
//        editor.putString(AppConstants.USER_NUM, profileResponse.getData().getMobile());
//        editor.putString(AppConstants.USER_EMAIL, profileResponse.getData().getEmail());
//        editor.putString(AppConstants.USER_BIO, profileResponse.getData().getBio());
//        editor.putInt(AppConstants.USER_IMAGE_ID, profileResponse.getData().getImage());
//        editor.putString(AppConstants.USER_WEBSITE, profileResponse.getData().getWebsite());
//        editor.putString(AppConstants.USER_ADDRESS, profileResponse.getData().getAddress());
//        editor.putString(AppConstants.USER_ADDRESS, profileResponse.getData().getAddress());
//        editor.putBoolean(AppConstants.IS_ONLINE, profileResponse.getData().isOnline());
//        editor.apply();
//    }
//
//    public static void putUserI(Context context, ProfileUpdateResponse profileResponse) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putString(AppConstants.FIRST_NAME, profileResponse.getData().getFirstName());
//        editor.putString(AppConstants.LAST_NAME, profileResponse.getData().getLastName());
//        editor.putString(AppConstants.USER_NAME, profileResponse.getData().getFirstName() + " " + profileResponse.getData().getLastName());
//        editor.putString(AppConstants.USER_IMAGE, profileResponse.getData().getImageUrl() != null ? profileResponse.getData().getImageUrl().toString() : "");
//        editor.putInt(AppConstants.USER_ID, profileResponse.getData().getId());
//        editor.putString(AppConstants.USER_NUM, profileResponse.getData().getMobile());
//        editor.putString(AppConstants.USER_EMAIL, profileResponse.getData().getEmail());
//        editor.putString(AppConstants.USER_BIO, profileResponse.getData().getBio());
//        editor.putInt(AppConstants.USER_IMAGE_ID, profileResponse.getData().getImage());
//        editor.putString(AppConstants.USER_WEBSITE, profileResponse.getData().getWebsite());
//        editor.putString(AppConstants.USER_ADDRESS, profileResponse.getData().getAddress());
//        editor.apply();
//    }
//
//    public static String getUserInfo(Context context, String key) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(key, "");
//    }
//
//    public static boolean getUserInfoBoolean(Context context, String key) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getBoolean(key, false);
//    }
//
//    public static int getUserID(Context context, String key) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getInt(key, 0);
//    }
//
//    public static void putOnboardDisplayed(Context context, boolean bool) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putBoolean(AppConstants.ONBOARD, bool);
//        editor.apply();
//    }
//
//    public static Boolean getOnboardDisplayed(Context context) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getBoolean(AppConstants.ONBOARD, false);
//    }
//
//    public static String getToken(Context context) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(AppConstants.TOKEN, "");
//    }
//
//    public static boolean isLogin(Context context) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        String token = sharedPreferences.getString(AppConstants.TOKEN, "");
//        return !TextUtils.isEmpty(token);
//    }
//
//    public static boolean getBooleanData(Context context,String key) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getBoolean(key, false);
//    }
//
//    public static void logout(Context context) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.clear();
//        editor.apply();
//    }
//
//    public static void putBooleanData(Context context,String key,boolean data) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putBoolean(key, data);
//        editor.apply();
//    }

    //    public static String getAppLanguage(Context context,String key){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(key,"");
//    }
//
//    public static void putAppLanguage(Context context,String lang) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putString(AppConstants.LANGUAGE,lang);
//        editor.apply();
//    }
//
//    public static void putUserInfo(Context context, LoginResponse loginResponse) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putString(AppConstants.TOKEN, loginResponse.getData().getToken());
//        editor.putString(AppConstants.USER_NAME,loginResponse.getData().getName());
//        editor.putString(AppConstants.USER_IMAGE,loginResponse.getData().getImage());
//        editor.putInt(AppConstants.USER_ID, loginResponse.getData().getId());
//        editor.putString(AppConstants.USER_NUM, loginResponse.getData().getPhone());
//        editor.putString(AppConstants.USER_EMAIL, loginResponse.getData().getEmail());
//        if (loginResponse.getData().getRoad() != null) editor.putString(AppConstants.USER_ROAD, loginResponse.getData().getRoad());
//        if (loginResponse.getData().getArea() != null) editor.putString(AppConstants.USER_AREA, loginResponse.getData().getArea());
//        if (loginResponse.getData().getPost_office() != null) editor.putString(AppConstants.USER_POST_OFFICE, loginResponse.getData().getPost_office());
////        editor.putInt(AppConstants.USER_COUNTRY, loginResponse.getData().getCountry().getId());
//        editor.apply();
//    }
//
//    public static void putUserInfo(Context context, AccountVerifyResponse loginResponse) {
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putString(AppConstants.TOKEN, loginResponse.getData().getToken());
//        editor.putString(AppConstants.USER_NAME,loginResponse.getData().getName());
//        editor.putString(AppConstants.USER_IMAGE,loginResponse.getData().getImage());
//        editor.putInt(AppConstants.USER_ID, loginResponse.getData().getId());
//        editor.putString(AppConstants.USER_NUM, loginResponse.getData().getPhone());
//        editor.putString(AppConstants.USER_EMAIL, loginResponse.getData().getEmail());
//        if (loginResponse.getData().getRoad() != null) editor.putString(AppConstants.USER_ROAD, loginResponse.getData().getRoad());
//        if (loginResponse.getData().getArea() != null) editor.putString(AppConstants.USER_AREA, loginResponse.getData().getArea());
//        if (loginResponse.getData().getPost_office() != null) editor.putString(AppConstants.USER_POST_OFFICE, loginResponse.getData().getPost_office());
////        editor.putInt(AppConstants.USER_COUNTRY, loginResponse.getData().getCountry().getId());
//        editor.apply();
//    }
//
//    public static void putUserPersonalInfo(Context context, ProfileResponse profileResponse) {
//        Log.e(TAG, "putUserPersonalInfo: "+profileResponse.toString() );
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        if (profileResponse.getData().getName() != null){ editor.putString(AppConstants.USER_NAME,profileResponse.getData().getName());}
//        if (profileResponse.getData().getDob() != null) {editor.putString(AppConstants.USER_DOB, profileResponse.getData().getDob().toString());}
//        if (String.valueOf(profileResponse.getData().getCountry()) != null) {editor.putInt(AppConstants.USER_COUNTRY, profileResponse.getData().getCountry().getId());}
//        if (String.valueOf(profileResponse.getData().getGender()) != null){ editor.putInt(AppConstants.USER_GENDER, profileResponse.getData().getGender());}
//        if (profileResponse.getData().getImage() != null) {editor.putString(AppConstants.USER_IMAGE, profileResponse.getData().getImage());}
//        if (profileResponse.getData().getRoad() != null) editor.putString(AppConstants.USER_ROAD, profileResponse.getData().getRoad());
//        if (profileResponse.getData().getArea() != null) editor.putString(AppConstants.USER_AREA, profileResponse.getData().getArea());
//        if (profileResponse.getData().getPost_office() != null) editor.putString(AppConstants.USER_POST_OFFICE, profileResponse.getData().getPost_office());
//        editor.apply();
//    }
//
//    public static void putUserImage(Context context ,String key , String value){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.putString(key,value);
//        editor.apply();
//    }
//
//    public static String getUserDataString(Context context , String key){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(key,"");
//    }
//
//    public static int getUserDataInt(Context context , String key){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getInt(key,0);
//    }
//
    public static String getToken(Context context) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(AppConstants.SharedPref.TOKEN, "");
    }

    public static void putToken(Context context, String token) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(AppConstants.SharedPref.TOKEN, token);
        editor.apply();
    }

    //
//    public static String getUserName(Context context){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(AppConstants.USER_NAME,"");
//    }
//
//    public static String getUserImage(Context context){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(AppConstants.USER_IMAGE,"");
//    }
//
//    public static boolean isLogin(Context context){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        String token = sharedPreferences.getString(AppConstants.TOKEN,"");
//        return !TextUtils.isEmpty(token);
//    }
//
    public static void getLogOut(Context context) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
//
//
//    public static void clearSharedHelper (Context context){
//        sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        editor.remove(AppConstants.TOKEN);
//        editor.remove(AppConstants.USER_NAME);
//        editor.remove(AppConstants.USER_DOB);
//        editor.remove(AppConstants.USER_COUNTRY);
//        editor.remove(AppConstants.USER_GENDER);
//        editor.remove(AppConstants.USER_IMAGE);
//        editor.remove(AppConstants.USER_ROAD);
//        editor.remove(AppConstants.USER_AREA);
//        editor.remove(AppConstants.USER_POST_OFFICE);
//        editor.remove(AppConstants.TOKEN);
//        editor.remove(AppConstants.USER_ID);
//        editor.remove(AppConstants.USER_NUM);
//        editor.remove(AppConstants.USER_EMAIL);
//        editor.apply();
//    }
}
