package com.dnitinverma.mylibrary.contacts;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class ContactSharedPrefernce {

    private static SharedPreferences sSharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context _context;
    private static int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "jhaiho";
    private static ContactSharedPrefernce uniqueInstance;

    public enum PREF_KEY {

        IS_CONTACT_DB_UPDATING("contact_db_updating"),
        PHONE_BOOK_CONTACT_COUNT("phonebook_contact_count"),
        LAST_CONTACTS_UPDATED_TIME_STAMP("last_updated_time_stamp");

        public final String KEY;
        PREF_KEY(String key) {
            this.KEY = key;
        }
    }

    public static SharedPreferences appSharedPrefs;

    public ContactSharedPrefernce(Context context) {
        appSharedPrefs =  PreferenceManager.getDefaultSharedPreferences(context);
    }


    /**
     * Private Constructor for not allowing other classes to instantiate this
     * class
     */
    private ContactSharedPrefernce() {

    }

    /**
     * @param context of the class calling this method
     * @return instance of this class This method is the global point of access
     * for getting the only one instance of this class
     */
    public static synchronized ContactSharedPrefernce getInstance(Context context) {
        if (uniqueInstance == null) {
            _context = context;
            sSharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = sSharedPreferences.edit();
            uniqueInstance = new ContactSharedPrefernce();
        }
        return uniqueInstance;
    }


    public int getInt( PREF_KEY key) {
        return sSharedPreferences.getInt(key.KEY, 0);
    }

    public void putInt( PREF_KEY key, int value) {
        editor.putInt(key.KEY, value);
        // Commit the edits!
        editor.commit();
    }

    public void putLong( PREF_KEY key, long value) {
        editor.putLong(key.KEY, value);
        editor.commit();
    }

    public long getLong( PREF_KEY key) {
        return sSharedPreferences.getLong(key.KEY, 0);
    }

    public void putString( PREF_KEY key, String value) {
        editor.putString(key.KEY, value);
        // Commit the edits!
        editor.commit();
    }

    public  String getString(PREF_KEY key) {
        return sSharedPreferences.getString(key.KEY, "");
    }

    public void putBoolean( PREF_KEY key, boolean value) {
        editor.putBoolean(key.KEY, value);
        editor.commit();
    }


    public boolean getBoolean( PREF_KEY key) {
        return sSharedPreferences.getBoolean(key.KEY, false);
    }

    public void clearAllPrefs() {
        editor.clear();
        editor.commit();
    }

    public void clearSpecific(String key) {
        editor.remove(key);
       editor.commit();
    }

}

