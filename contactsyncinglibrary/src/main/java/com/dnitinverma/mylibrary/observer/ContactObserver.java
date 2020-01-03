package com.dnitinverma.mylibrary.observer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.dnitinverma.mylibrary.contacts.ContactSharedPrefernce;
import com.dnitinverma.mylibrary.contacts.ContactUpdate;
import com.dnitinverma.mylibrary.services.CopyContactService;

import java.util.Iterator;
import java.util.List;


public class ContactObserver extends ContentObserver {

    private static long sTime = 0;

    //private variables
    private Context mContext;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */


    public ContactObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
    }


    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.e("URI changes occurs ", "" + uri);
        Log.e("2_CONTACT_OBSERVER", "===============CONTACT_UPDATE_OCCURED==================");
        if (sTime == 0)
            sTime = System.currentTimeMillis();
        else if (System.currentTimeMillis() - sTime < 12000) {
            return;
        }
        sTime = System.currentTimeMillis();
        try {
                if (isMyServiceRunning(CopyContactService.class)&& !ContactSharedPrefernce.getInstance(mContext).getBoolean(ContactSharedPrefernce.PREF_KEY.IS_CONTACT_DB_UPDATING)) {
                    Log.e("CO_OB_RESTART", "on contact update sync has been restarted");
                    Intent intent = new Intent("com.jhaiho.services.contactserviceClass");
                    intent.setPackage(mContext.getPackageName());
                    intent.putExtra(ContactUpdate.CONTACT_SYNC_TYPE, ContactUpdate.SYNC_UPDATED_CONTACTS);
                    mContext.startService(intent);
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean deliverSelfNotifications() {
        return true;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.e("2_CONTACT_OBSERVER", "===============CONTACT_UPDATE_OCCURED==================");

        if (sTime == 0)
            sTime = System.currentTimeMillis();
        else if (System.currentTimeMillis() - sTime < 12000) {
            return;
        }
        sTime = System.currentTimeMillis();

        try {
                if (isMyServiceRunning(CopyContactService.class)&& !ContactSharedPrefernce.getInstance(mContext).getBoolean(ContactSharedPrefernce.PREF_KEY.IS_CONTACT_DB_UPDATING)) {
                    Log.e("CO_OB_RESTART", "on contact update sync has been restarted");
                    Intent intent = new Intent("com.jhaiho.services.contactserviceClass");
                    intent.setPackage(mContext.getPackageName());
                    intent.putExtra(ContactUpdate.CONTACT_SYNC_TYPE, ContactUpdate.SYNC_UPDATED_CONTACTS);
                    mContext.startService(intent);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onChange(selfChange);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        //checks service running or not
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                {

                    Intent intent = new Intent("com.jhaiho.services.contactserviceClass");
                    intent.setPackage(mContext.getPackageName());
                    mContext.stopService(intent);
                }
                return true;
            }
        }
        return true;
    }

    public void stopTimer() {
        sTime = 0;
    }
}

