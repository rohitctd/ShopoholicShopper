package com.dnitinverma.mylibrary.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.dnitinverma.mylibrary.observer.ContactObserver;


public class ContactMonitorService extends IntentService {

    //private variables
    private ContactObserver mObserver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ContactMonitorService(String name) {
        super(name);
    }
    public ContactMonitorService() {
        super("name");
    }
//    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("CMS","=========================CMS==========================");
        Log.e("Contact monitor","Contact monitor service started");
        Log.e("CMS","=========================CMS==========================");
        //runAsForeground();
        //registerContactsContentObserver();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mObserver = new ContactObserver(null, getApplicationContext());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getApplicationContext().getContentResolver().unregisterContentObserver(mObserver);
        } else {
            getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
        }

    }

//    @Override
//    public void onCreate() {
//
////        mObserver = new ContactObserver(null, this);
////
////        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
////            this.getContentResolver().unregisterContentObserver(mObserver);
////        } else {
////            this.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
////        }
//
//        super.onCreate();
//    }

//    private void registerContactsContentObserver(){
//         mObserver = new ContactObserver(null, this);
//        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
//    }
//
//    private void runAsForeground(){
//        Intent notificationIntent = new Intent();
//        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0,
//                notificationIntent, PendingIntent.FLAG_ONE_SHOT);
//
//        Notification notification=new NotificationCompat.Builder(this)
//                .setContentText("test")
//                .setContentIntent(pendingIntent).build();
//        startForeground(1, notification);
//    }

    @Override
    public void onDestroy() {
        mObserver.stopTimer();
        getApplicationContext().getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
        super.onTaskRemoved(rootIntent);
    }
}
