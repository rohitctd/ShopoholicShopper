package com.dnitinverma.mylibrary.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.IBinder;
import android.provider.ContactsContract;

import com.dnitinverma.mylibrary.contacts.ContactSharedPrefernce;
import com.dnitinverma.mylibrary.contacts.ContactUpdate;


public class CopyContactService extends IntentService  {



    //    /**
//     * Creates an IntentService.  Invoked by your subclass's constructor.
//     *
//     * @param name Used to name the worker thread, important only for debugging.
//     */
    public CopyContactService() {
        super(CopyContactService.class.getName());
    }


    private Cursor getAllContacts() {
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
        return getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
    }

    private Cursor getAllEmailAddress() {
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
        return getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
    }


    @Override
    public IBinder onBind(Intent intent) {
        try {
            ContactSharedPrefernce.getInstance(this).putBoolean(ContactSharedPrefernce.PREF_KEY.IS_CONTACT_DB_UPDATING,true);
            ContactUpdate.getInstance().fetchAndPrepareContactDataForSync(this,intent.getIntExtra(ContactUpdate.CONTACT_SYNC_TYPE,0));

        } catch (SQLiteDatabaseLockedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            ContactSharedPrefernce.getInstance(this).putBoolean(ContactSharedPrefernce.PREF_KEY.IS_CONTACT_DB_UPDATING,true);
            ContactUpdate.getInstance().fetchAndPrepareContactDataForSync(this,intent.getIntExtra(ContactUpdate.CONTACT_SYNC_TYPE,0));

        } catch (SQLiteDatabaseLockedException e) {
            e.printStackTrace();
        }
    }
}
