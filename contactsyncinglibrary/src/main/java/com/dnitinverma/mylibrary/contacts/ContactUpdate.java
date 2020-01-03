package com.dnitinverma.mylibrary.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.dnitinverma.mylibrary.R;
import com.dnitinverma.mylibrary.databaseLibrary.ContactsDatabase;
import com.dnitinverma.mylibrary.interfaces.ContactUpdationListener;
import com.dnitinverma.mylibrary.interfaces.ReceiveContactUpdateCallBack;
import com.dnitinverma.mylibrary.models.ContactResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;


public class ContactUpdate {
    public final static String CONTACT_UPDATE_INTENT_FILTER = "my_contacts_updated";
    public final static String CONTACT_UPDATE_TYPE = "contact_update_type";
    public final static String UPDATE_TYPE_FIRST_CONTACTS = "update_type_first_contacts";
    public final static String UPDATE_TYPE_NEW_CONTACTS = "update_type_new_contacts";
    public final static String UPDATE_TYPE_DELETE_CONTACTS = "update_type_delete_contacts";
    public final static String UPDATE_TYPE_EDIT_CONTACTS = "update_type_edit_contacts";
    public final static String CONTACT_LIST = "contact_list";

    private final int ADDED_CONTACT = 1;
    private final int EDITED_CONTACT = 2;
    private final int DELETED_CONTACT = 3;
    public final static String CONTACT_SYNC_TYPE = "contact_sync_type";
    public final static int SYNC_ALREADY_COMPLETED = 0;
    public final static int SYNC_ALL_CONTACTS = 1;
    public final static int SYNC_NON_SYNC_CONTACTS = 2;
    public final static int SYNC_UPDATED_CONTACTS = 3;
    private ArrayList<ContactResult> addedContactList = new ArrayList<>();
    private ArrayList<ContactResult> deletedContactList = new ArrayList<>();
    private ArrayList<ContactResult> editedContactList = new ArrayList<>();
    private ArrayList<ContactResult> mListDeviceContact = new ArrayList<>();
    private static ContactUpdate contactUpdate = null;
    private ContactUpdationListener mContactUpdationListener;
    private ReceiveContactUpdateCallBack receiveContactUpdateCallBack;
    private String countryCode = "+852";
    private String timeStamp = "";
    private String countryListJsonData;
    private boolean isDeletedContact = true;
    private ContactSharedPrefernce contactSharedPrefernce;

    public static ContactUpdate getInstance() {
        if (contactUpdate == null) {
            contactUpdate = new ContactUpdate();
        }
        return contactUpdate;
    }

    public void setContactUpdationListener(ContactUpdationListener mContactUpdationListener) {
        this.mContactUpdationListener = mContactUpdationListener;
    }


    public void setReceiveContactUpdateCallBack(ReceiveContactUpdateCallBack receiveContactUpdateCallBack) {
        this.receiveContactUpdateCallBack = receiveContactUpdateCallBack;
    }

    /**
     * method to start syncing service for contacts
     */
    public void startContactSyncing(Context context, Intent intent) {
        context.startService(intent);
    }


    //method to get contacts
    public void fetchAndPrepareContactDataForSync(Context context, int syncType) {
        contactSharedPrefernce = ContactSharedPrefernce.getInstance(context);
        timeStamp = contactSharedPrefernce.getString(ContactSharedPrefernce.PREF_KEY.LAST_CONTACTS_UPDATED_TIME_STAMP);
        switch (syncType) {
            case SYNC_ALREADY_COMPLETED:
                break;
            case SYNC_ALL_CONTACTS:
                timeStamp = "";
                contactSharedPrefernce.putString(ContactSharedPrefernce.PREF_KEY.LAST_CONTACTS_UPDATED_TIME_STAMP,timeStamp);
                mListDeviceContact.clear();
                readContacts(context);
                if (mContactUpdationListener != null)
                    mContactUpdationListener.showLocalContacts(mListDeviceContact);
                timeStamp = String.valueOf(System.currentTimeMillis());
                contactSharedPrefernce.putString(ContactSharedPrefernce.PREF_KEY.LAST_CONTACTS_UPDATED_TIME_STAMP, timeStamp);
//                if (receiveContactUpdateCallBack != null) {
//                    Intent intent = new Intent(CONTACT_UPDATE_INTENT_FILTER);
//                    intent.putExtra(CONTACT_UPDATE_TYPE, UPDATE_TYPE_FIRST_CONTACTS);
//                    intent.putExtra(CONTACT_LIST, mListDeviceContact);
//                    timeStamp = String.valueOf(System.currentTimeMillis());
//                    contactSharedPrefernce.putString(ContactSharedPrefernce.PREF_KEY.LAST_CONTACTS_UPDATED_TIME_STAMP, timeStamp);
//                }
                ContactsDatabase.getInstance(context).removeContactsData();
                ContactsDatabase.getInstance(context).saveContactsInfo(mListDeviceContact);
                receiveContactUpdateCallBack.databaseUpdated(true);
//                ContactsDatabase.getInstance(context).updateEmailInfoInContactsDB(ContactsDatabase.getInstance(context).fetchAllEmail());
                if (mContactUpdationListener != null)
                    mContactUpdationListener.allContacts(ContactsDatabase.getInstance(context).fetchAllContacts());
                break;
            case SYNC_NON_SYNC_CONTACTS:
                if (mContactUpdationListener != null)
                    mContactUpdationListener.nonSynchedContacts(ContactsDatabase.getInstance(context).fetchNonSynchedContacts());
                break;
            case SYNC_UPDATED_CONTACTS:
                mListDeviceContact.clear();
                readContacts(context);
                checkUpdatedContact(mListDeviceContact, context);
                // checkUniqueContacts(mListDeviceContact, context);
                break;

        }
        ContactSharedPrefernce.getInstance(context).putBoolean(ContactSharedPrefernce.PREF_KEY.IS_CONTACT_DB_UPDATING, false);
    }


    /**
     * method to read contacts
     */
    /**
     * method to read contacts
     */
    private void readContacts(Context context) {
        countryListJsonData = getCountryCodeList(context);
        countryCode = checkCountryCodeFromIsoCode(context);
        mListDeviceContact.clear();
        String selection = null;
        String[] selectionArgs = null;
        ArrayList<String> uniqueNumber=new ArrayList<>();

        String[] projectionToCheck = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.PHOTO_URI, ContactsContract.CommonDataKinds.Phone.STATUS};
        Cursor cursorToCheck = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionToCheck, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
        long dbCount = contactSharedPrefernce.getInt(ContactSharedPrefernce.PREF_KEY.PHONE_BOOK_CONTACT_COUNT);
        if (cursorToCheck.getCount() >= dbCount) {
            if (!timeStamp.equalsIgnoreCase("")) {
                isDeletedContact = false;
                selection = ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP + " >= " + timeStamp;
                //selectionArgs=new String[]{timeStamp};
            }
        }
        else
            isDeletedContact=true;
        timeStamp = String.valueOf(System.currentTimeMillis());
        contactSharedPrefernce.putString(ContactSharedPrefernce.PREF_KEY.LAST_CONTACTS_UPDATED_TIME_STAMP, timeStamp);
        contactSharedPrefernce.putInt(ContactSharedPrefernce.PREF_KEY.PHONE_BOOK_CONTACT_COUNT,cursorToCheck.getCount());
        Cursor data = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionToCheck, selection, selectionArgs, ContactsContract.Contacts.SORT_KEY_PRIMARY);
        ContactResult contactData;
        final int contactIdIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        final int contactRawIdIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID);
        final int contactPhoneNumberIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        final int contactPhotoIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
        final int contactNameIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        final int rowIdIndex = data.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID);
        while (data.moveToNext()) {
            contactData = new ContactResult();
            contactData.setPhoneNumber(data.getString(contactPhoneNumberIndex));
            if (data.getString(contactPhotoIndex) != null)
                contactData.setProfileImage(data.getString(contactPhotoIndex));
            contactData.setRowId(data.getString(rowIdIndex));
            contactData.setContactName(data.getString(contactNameIndex));
            contactData.setContactId(data.getString(contactIdIndex) + data.getString(contactRawIdIndex));
            contactData.setContactMainId(data.getString(contactIdIndex));
            contactData.setContactRawId(data.getString(contactRawIdIndex));
            contactData.setIsSynchedWithServer("0");
            contactData.setIsAppUser("0");
            if (contactData.getPhoneNumber().startsWith("0")) {
                contactData.setCountryCode(countryCode);
                contactData.setPhoneNumberWithCode(countryCode + contactData.getPhoneNumber().replaceFirst("^0+(?!$)", "").replaceAll("[\\D]", ""));
            } else if (contactData.getPhoneNumber().startsWith("+")) {
                contactData.setCountryCode("");
                String rawNumber = contactData.getPhoneNumber().substring(1);
                contactData.setPhoneNumberWithCode("+" + rawNumber.replaceAll("[\\D]", ""));
            } else {
                contactData.setCountryCode(countryCode);
                contactData.setPhoneNumberWithCode(countryCode + contactData.getPhoneNumber().replaceAll("[\\D]", ""));
            }
            if (contactData.getPhoneNumberWithCode() != null && !contactData.getPhoneNumberWithCode().equalsIgnoreCase("")) {
                /// Log.e("contactName :---",contactData.getDisplayName()+"code --- "+contactData.getCountryCode());
                if (!uniqueNumber.contains(contactData.getContactName()+contactData.getPhoneNumberWithCode())) {
                    mListDeviceContact.add(contactData);
                    uniqueNumber.add(contactData.getContactName()+contactData.getPhoneNumberWithCode());
                }
            }
        }
        // }
        data.close();
    }

    /*
    Method to check updated contact
   */
    /*
 Method to check updated contact
 */
    private void checkUpdatedContact(ArrayList<ContactResult> newContactList, Context context) {
        addedContactList.clear();
        editedContactList.clear();
        deletedContactList.clear();
        ArrayList<ContactResult> oldContactList = ContactsDatabase.getInstance(context).fetchAllContacts();
        if (!isDeletedContact) {
            for (ContactResult newContact : newContactList) {
                int UPDATED_STATUS = ADDED_CONTACT;
                int position = -1;
                for (ContactResult oldContact : oldContactList) {
                    position += 1;
                    if (newContact.getRowId().equalsIgnoreCase(oldContact.getRowId())) {
                        UPDATED_STATUS = EDITED_CONTACT;
                        break;
                    }
                }
                if (UPDATED_STATUS == ADDED_CONTACT)
                    addedContactList.add(newContact);
                else if (UPDATED_STATUS == EDITED_CONTACT) {
                    if (!newContact.getPhoneNumberWithCode().equalsIgnoreCase(oldContactList.get(position).getPhoneNumberWithCode())) {
                        oldContactList.get(position).setPhoneNumberWithCode(newContact.getPhoneNumberWithCode());
                        oldContactList.get(position).setPhoneNumber(newContact.getPhoneNumber());
                        oldContactList.get(position).setCountryCode(newContact.getCountryCode());
                        oldContactList.get(position).setIsAppUser("0");
                        oldContactList.get(position).setIsSynchedWithServer("0");
                        oldContactList.get(position).setProfileImage(newContact.getProfileImage());
                        oldContactList.get(position).setContactName(newContact.getContactName());
                        oldContactList.get(position).setEmail(newContact.getEmail());
                    } else if (!newContact.getContactName().equalsIgnoreCase(oldContactList.get(position).getContactName()) || !newContact.getProfileImage().equalsIgnoreCase(oldContactList.get(position).getProfileImage()) || !newContact.getEmail().equalsIgnoreCase(oldContactList.get(position).getEmail())) {
                        oldContactList.get(position).setContactName(newContact.getContactName());
                        if (oldContactList.get(position).getIsAppUser().equalsIgnoreCase("0")) {
                            oldContactList.get(position).setProfileImage(newContact.getProfileImage());
                            oldContactList.get(position).setEmail(newContact.getEmail());
                        }
                    }
                    editedContactList.add(oldContactList.get(position));
                }

            }
            if (addedContactList.size() > 0) {
                ContactsDatabase.getInstance(context).saveContactsInfo(addedContactList);
                if (mContactUpdationListener != null)
                    mContactUpdationListener.newlyAddedContacts(ContactsDatabase.getInstance(context).fetchNonSynchedContacts());
            }
            else if (editedContactList.size() > 0) {
                ContactsDatabase.getInstance(context).updateEditedContactsInDB(editedContactList);
                if (mContactUpdationListener != null)
                    mContactUpdationListener.editedContacts(ContactsDatabase.getInstance(context).fetchNonSynchedContacts());
            }
        } else {
            for (ContactResult oldContact : oldContactList) {
                boolean isDeleted = true;
                for (ContactResult newContact : newContactList) {
                    if (oldContact.getRowId().equalsIgnoreCase(newContact.getRowId())) {
                        isDeleted = false;
                        break;
                    }
                }
                if (isDeleted)
                    deletedContactList.add(oldContact);
            }
            if (deletedContactList.size() > 0) {
                ContactsDatabase.getInstance(context).deleteContacts(deletedContactList);
                if (mContactUpdationListener != null)
                    mContactUpdationListener.deletedContacts(deletedContactList);
            }
        }
    }


    /**
     * get country code ArrayList from raw folder
     *
     * @param context
     * @return
     */

    private String getCountryCodeList(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.countrydata);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * check country code from isocode
     *
     * @param context
     * @return
     */
    private String checkCountryCodeFromIsoCode(Context context) {
        String locale = null;
        //todo.... change when it updated
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            locale = context.getResources().getConfiguration().getLocales().get(0).getCountry();
//        } else {
//            locale = context.getResources().getConfiguration().locale.getCountry();
//        }
        locale = getUserCountry(context);
//        String countryCode = "+91";
        if (countryListJsonData != null && locale != null) {
            try {
                JSONArray jsonArray = new JSONArray(countryListJsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject dataCountry = jsonArray.getJSONObject(i);
                    if (dataCountry.getString("ISOCode").equalsIgnoreCase(locale)) {
                        countryCode = "+" + String.valueOf(dataCountry.getInt("CountryCode"));
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return countryCode;
    }


    /**
     * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
     * @param context Context reference to get the TelephonyManager instance from
     * @return country code or null
     */
    public String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return null;
    }
}



