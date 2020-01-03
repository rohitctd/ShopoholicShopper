package com.shopoholic.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;



public class AppSharedPreference {
    private static AppSharedPreference appSharedPreference;

    public static enum PREF_KEY {
        USER_ID("user_id"),
        DEVICE_TOKEN("device_token"),
        IS_TUTORIAL_SEEN("is_tutorial_seen"),
        IS_SIGN_UP("is_sign_up"),
        IS_PHONE_VALIDATE("is_phone_validate"),
        IS_EMAIL_VALIDATE("is_email_validate"),
        IS_CATEGORY_SELECTED("is_category_selected"),
        EMAIL("email"),
        PHONE_NO("phone_no"),
        COUNTRY_CODE("country_code"),
        VERIFIED_EMAIL("email"),
        VERIFIED_PHONE_NO("phone_no"),
        USER_NAME("user_name"),
        ACCESS_TOKEN("access_token"),
        FIRST_NAME("first_name"),
        LAST_NAME("last_name"),
        USER_IMAGE("image"),
        DOB("dob"),
        STATUS("status"),
        GENDER("gender"),
        ADDRESS("address"),
        ADDRESS2("address2"),
        CITY("city"),
        CURRENT_LANGUAGE("language"),
        STATE("state"),
        SIGNUP_DATE("signup_date"),
        PROFILE("profile"),
        ANNVERSARY("annversary"),
        LATITUDE("latitude"),
        LONGITUDE("longitude"),
        WALLET_POINTS("wallet_points"),
        IS_OTHER_INFO_SET("is_other_info_set"),
        IS_BANK_DETAIL_SET("is_bank_detail_set"),
        CURRENT_CHAT_ROOM("current_chat_room"),
        IS_CONTACTS_SYNCHED("isContactSynched"),
        CURRENT_LANGUAGE_CODE("current_language_code"),
        USER_ONLINE_STATUS("user_online_status"),
        IS_LOCATION_ON("isLocationOn"),
        IS_NOTIFICATION_ON("isNotificationOn"),
        IS_HOME_SEEN("isHomeSeen"),
        IS_SIDE_MENU_SEEN("isSideMenuSeen"),
        IS_HUNT_BUDDY_SEEN("isHuntBuddySeen"),
        ADMIN_COMMISSION("admin_commission");


        public final String KEY;

        PREF_KEY(String key) {
            this.KEY = key;
        }

    }

    public static AppSharedPreference getInstance() {
        if (appSharedPreference == null) {
            appSharedPreference = new AppSharedPreference();
        }
        return appSharedPreference;
    }

    public int getInt(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getInt(key.KEY, 0);
    }
    public void putInt(Context context, PREF_KEY key, int value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key.KEY, value);

        // Commit the edits!
        editor.apply();
    }
    public void putLong(Context context, PREF_KEY key, long value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key.KEY, value);
        editor.apply();
    }
    public void putFloat(Context context, PREF_KEY key, Float value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(key.KEY, value);
        editor.apply();
    }
    public Float getFloat(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getFloat(key.KEY, 0);
    }

    public long getLong(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getLong(key.KEY, 0);
    }

    public void putString(Context context, PREF_KEY key, String value) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key.KEY, value);

        // Commit the edits!
        editor.apply();
    }

    public String getString(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getString(key.KEY, "");
    }

    public void putBoolean(Context context, PREF_KEY key, boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key.KEY, value);
        editor.apply();
    }

    public boolean getBoolean(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(key.KEY, false);
    }

    public void putString(Context context, String key, String value) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);

        editor.apply();
    }

    public String getString(Context context, String key) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPref.getString(key, "");
    }

    public void clearAllPreferences(Context context) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    public void clearUserRelatedPreferences(Context context){

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("accessToken");
        editor.remove("IsLogin");
        editor.remove("email");
        editor.apply();
    }
}