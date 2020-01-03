package com.dnitinverma.mylibrary.databaseLibrary;

import android.content.ContentValues;
import android.content.Context;

import com.dnitinverma.mylibrary.models.ContactResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_COUNTRY_CODE;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_EMAIL;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_ID;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_IMAGE;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_IS_APP_USER;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_IS_SYNCHED;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_MAIN_ID;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_NAME;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_PHONE_NUMBER;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_PHONE_WITH_CODE;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_RAW_ID;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_ROW_ID;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.CONTACT_SR_NUMBER;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.EMAIL_ADDRESS;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.EMAIL_CONTACT_ID;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.EMAIL_CONTACT_MAIN_ID;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.EMAIL_CONTACT_RAW_ID;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.TABLE_CONTACTS;
import static com.dnitinverma.mylibrary.databaseLibrary.SqlConstant.TABLE_EMAIL;


/**
 * Class used to communicate with database
 */
public class ContactsDatabase {

    private static ContactsDatabase mInstance;
    private static Context context;

    public static ContactsDatabase getInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new ContactsDatabase();
        }
        context = mContext;
        return mInstance;
    }

    public void removeContactsData() {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        dataBaseProvider.deleteData(TABLE_CONTACTS);
    }

    public void removeEmailData() {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        dataBaseProvider.deleteData(TABLE_EMAIL);
    }

    /**
     * Save all Contacts from phoneBook in local storage
     *
     * @param
     */
    public void saveContactsInfo(ArrayList<ContactResult> contactModelList) {
        List<ContentValues> contentValue = new ArrayList<>();
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        for (int i = 0; i < contactModelList.size(); i++) {
            if (contactModelList.get(i) != null) {
                ContentValues contentValues = new ContentValues();
                ContactResult contactModel = contactModelList.get(i);
                contentValues.put(CONTACT_ID, contactModel.getContactId());
                contentValues.put(CONTACT_ROW_ID, contactModel.getRowId());
                contentValues.put(CONTACT_NAME, contactModel.getContactName());
                contentValues.put(CONTACT_IMAGE, contactModel.getProfileImage());
                contentValues.put(CONTACT_COUNTRY_CODE, contactModel.getCountryCode());
                contentValues.put(CONTACT_PHONE_NUMBER, contactModel.getPhoneNumber());
                contentValues.put(CONTACT_PHONE_WITH_CODE, contactModel.getPhoneNumberWithCode());
                contentValues.put(CONTACT_IS_APP_USER, contactModel.getIsAppUser());
                contentValues.put(CONTACT_EMAIL, contactModel.getEmail());
                contentValues.put(CONTACT_MAIN_ID, contactModel.getContactMainId());
                contentValues.put(CONTACT_RAW_ID, contactModel.getContactRawId());
                contentValues.put(CONTACT_IS_SYNCHED, contactModel.getIsSynchedWithServer());
                contentValue.add(contentValues);
            }
        }
        dataBaseProvider.insertAll(SqlConstant.TABLE_CONTACTS, contentValue);
    }

    /**
     * Save all emails from phoneBook in local storage
     *
     * @param
     */
    public void saveEmailInfo(ArrayList<ContactResult> emailList) {
        List<ContentValues> contentValue = new ArrayList<>();
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        for (int i = 0; i < emailList.size(); i++) {
            if (emailList.get(i) != null) {
                ContentValues contentValues = new ContentValues();
                ContactResult contactModel = emailList.get(i);
                contentValues.put(EMAIL_CONTACT_ID, contactModel.getContactId());
                contentValues.put(EMAIL_ADDRESS, contactModel.getEmail());
                contentValues.put(EMAIL_CONTACT_MAIN_ID, contactModel.getContactMainId());
                contentValues.put(EMAIL_CONTACT_RAW_ID, contactModel.getContactRawId());
                contentValue.add(contentValues);
            }
        }
        dataBaseProvider.insertAll(SqlConstant.TABLE_EMAIL, contentValue);
    }


    /**
     * update all Contacts with email  in local storage
     *
     * @param
     */
    public void updateEmailInfoInContactsDB(ArrayList<ContactResult> emailList) {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        if (emailList != null && emailList.size() > 0) {
            for (int i = 0; i < emailList.size(); i++) {
                ContactResult contactModel=emailList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(CONTACT_EMAIL, contactModel.getEmail());
                dataBaseProvider.update(TABLE_CONTACTS, contentValues, CONTACT_MAIN_ID + "=\"" + emailList.get(i).getContactMainId() + "\"", null);
            }
        }
    }

    /**
     * fetch All email from local storage
     *
     * @return
     */
    public ArrayList<ContactResult> fetchAllEmail() {

        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        String selectQuery = "SELECT  * FROM " + TABLE_EMAIL;
        ArrayList<ContactResult> contactList = new ArrayList<>();
        List<ContentValues> contentValues = dataBaseProvider.getData(SqlConstant.TABLE_EMAIL, selectQuery);
        if (contentValues != null && contentValues.size() > 0) {
            Iterator<ContentValues> iterator = contentValues.iterator();
            while (iterator.hasNext()) {
                ContentValues contentValue = (ContentValues) iterator.next();
                ContactResult contactModel = new ContactResult();
                contactModel.setContactId(contentValue
                        .getAsString(SqlConstant.EMAIL_CONTACT_ID));
                contactModel.setContactMainId(contentValue
                        .getAsString(SqlConstant.EMAIL_CONTACT_MAIN_ID));
                contactModel.setContactRawId(contentValue
                        .getAsString(SqlConstant.EMAIL_CONTACT_RAW_ID));
                contactModel.setEmail(contentValue
                        .getAsString(SqlConstant.EMAIL_ADDRESS));
                contactList.add(contactModel);
            }
        }
        return contactList;
    }


    /**
     * fetch All contacts from local storage
     *
     * @return
     */
    public ArrayList<ContactResult> fetchAllContacts() {

        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        ArrayList<ContactResult> contactList = new ArrayList<>();
        List<ContentValues> contentValues = dataBaseProvider.getData(SqlConstant.TABLE_CONTACTS, selectQuery);
        if (contentValues != null && contentValues.size() > 0) {
            Iterator<ContentValues> iterator = contentValues.iterator();
            while (iterator.hasNext()) {
                ContentValues contentValue = (ContentValues) iterator.next();
                ContactResult contactModel = new ContactResult();
                contactModel.setContactSrNumber(contentValue
                        .getAsInteger(SqlConstant.CONTACT_SR_NUMBER));
                contactModel.setContactId(contentValue
                        .getAsString(SqlConstant.CONTACT_ID));
                contactModel.setContactMainId(contentValue
                        .getAsString(SqlConstant.CONTACT_MAIN_ID));
                contactModel.setContactRawId(contentValue
                        .getAsString(SqlConstant.CONTACT_RAW_ID));
                contactModel.setRowId(contentValue
                        .getAsString(SqlConstant.CONTACT_ROW_ID));
                contactModel.setContactName(contentValue
                        .getAsString(SqlConstant.CONTACT_NAME));
                contactModel.setIsAppUser(contentValue
                        .getAsString(SqlConstant.CONTACT_IS_APP_USER));
                contactModel.setCountryCode(contentValue
                        .getAsString(SqlConstant.CONTACT_COUNTRY_CODE));
                contactModel.setPhoneNumber(contentValue
                        .getAsString(SqlConstant.CONTACT_PHONE_NUMBER));
                contactModel.setProfileImage(contentValue
                        .getAsString(SqlConstant.CONTACT_IMAGE));
                contactModel.setEmail(contentValue
                        .getAsString(SqlConstant.CONTACT_EMAIL));
                contactModel.setIsSynchedWithServer(contentValue
                        .getAsString(SqlConstant.CONTACT_IS_SYNCHED));
                contactModel.setPhoneNumberWithCode(contentValue
                        .getAsString(SqlConstant.CONTACT_PHONE_WITH_CODE));
                contactList.add(contactModel);
            }
        }
        return contactList;
    }

    /**
     * fetch All contacts from local storage
     *
     * @return
     */
    public ArrayList<ContactResult> fetchNonSynchedContacts() {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_IS_SYNCHED + " = '0'";
        ArrayList<ContactResult> contactList = new ArrayList<>();
        List<ContentValues> contentValues = dataBaseProvider.getData(SqlConstant.TABLE_CONTACTS, selectQuery);
        if (contentValues != null && contentValues.size() > 0) {
            Iterator<ContentValues> iterator = contentValues.iterator();
            while (iterator.hasNext()) {
                ContentValues contentValue = (ContentValues) iterator.next();
                ContactResult contactModel = new ContactResult();
                contactModel.setContactSrNumber(contentValue
                        .getAsInteger(SqlConstant.CONTACT_SR_NUMBER));
                contactModel.setContactId(contentValue
                        .getAsString(SqlConstant.CONTACT_ID));
                contactModel.setContactMainId(contentValue
                        .getAsString(SqlConstant.CONTACT_MAIN_ID));
                contactModel.setContactRawId(contentValue
                        .getAsString(SqlConstant.CONTACT_RAW_ID));
                contactModel.setRowId(contentValue
                        .getAsString(SqlConstant.CONTACT_ROW_ID));
                contactModel.setContactName(contentValue
                        .getAsString(SqlConstant.CONTACT_NAME));
                contactModel.setIsAppUser(contentValue
                        .getAsString(SqlConstant.CONTACT_IS_APP_USER));
                contactModel.setCountryCode(contentValue
                        .getAsString(SqlConstant.CONTACT_COUNTRY_CODE));
                contactModel.setPhoneNumber(contentValue
                        .getAsString(SqlConstant.CONTACT_PHONE_NUMBER));
                contactModel.setProfileImage(contentValue
                        .getAsString(SqlConstant.CONTACT_IMAGE));
                contactModel.setEmail(contentValue
                        .getAsString(SqlConstant.CONTACT_EMAIL));
                contactModel.setIsSynchedWithServer(contentValue
                        .getAsString(SqlConstant.CONTACT_IS_SYNCHED));
                contactModel.setPhoneNumberWithCode(contentValue
                        .getAsString(SqlConstant.CONTACT_PHONE_WITH_CODE));
                contactList.add(contactModel);
            }
        }
        return contactList;
    }

    /**
     * fetch All contacts from local storage
     *
     * @return
     */
    public boolean checkContactInDB(String phoneNumberWithCode) {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_PHONE_WITH_CODE + " = '"+phoneNumberWithCode+"'";
        List<ContentValues> contentValues = dataBaseProvider.getData(SqlConstant.TABLE_CONTACTS, selectQuery);
        if (contentValues != null && contentValues.size() > 0) {
            Iterator<ContentValues> iterator = contentValues.iterator();
            while (iterator.hasNext()) {
                return true;
            }
        }
        return false;
    }

    /**
     * method to check contacts count in DB
     * @return
     */
    public long checkContactsCountInDB()
    {
        DataBaseProvider dataBaseProvider=DataBaseProvider.getInstance(context);
        return dataBaseProvider.getContactsCount();
    }

    /**
     * method to check contacts count in DB
     * @return
     */
    public long checkEmailsCountInDB()
    {
        DataBaseProvider dataBaseProvider=DataBaseProvider.getInstance(context);
        return dataBaseProvider.getEmailsCount();
    }

    /**
     * fetch All contacts from local storage
     *
     * @return
     *//*
    public String fetchContactName(String phoneNumber) {
        String name ="";
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_PHONE_WITH_CODE + " = '" + phoneNumber.trim() + "'";
        List<ContentValues> contentValues = dataBaseProvider.getData(SqlConstant.TABLE_CONTACTS, selectQuery);
        if (contentValues != null && contentValues.size() > 0) {
            Iterator<ContentValues> iterator = contentValues.iterator();
            while (iterator.hasNext()) {
                ContentValues contentValue = (ContentValues) iterator.next();
               name = contentValue
                        .getAsString(SqlConstant.CONTACT_NAME);

            }
        }
        return name;
    }*/





//    /**
//     * fetch All contacts from local storage
//     *
//     * @return
//     */
//    public ArrayList<ContactResult> fetchContactsGreaterThanLastIndex(int previousLastSynchedIndex,int currentLastSynchedIndex) {
//        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
//        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " WHERE " + CONTACT_SR_NUMBER + " BETWEEN "+previousLastSynchedIndex+" AND "+currentLastSynchedIndex;
//        ArrayList<ContactResult> contactList = new ArrayList<>();
//        List<ContentValues> contentValues = dataBaseProvider.getData(SqlConstant.TABLE_CONTACTS, selectQuery);
//        if (contentValues != null && contentValues.size() > 0) {
//            Iterator<ContentValues> iterator = contentValues.iterator();
//            while (iterator.hasNext()) {
//                ContentValues contentValue = (ContentValues) iterator.next();
//                ContactResult contactModel = new ContactResult();
//                contactModel.setContactSrNumber(contentValue
//                        .getAsInteger(SqlConstant.CONTACT_SR_NUMBER));
//                contactModel.setContactId(contentValue
//                        .getAsString(SqlConstant.CONTACT_ID));
//                contactModel.setContactName(contentValue
//                        .getAsString(SqlConstant.CONTACT_NAME));
//                contactModel.setIsAppUser(contentValue
//                        .getAsString(SqlConstant.CONTACT_IS_APP_USER));
//                contactModel.setCountryCode(contentValue
//                        .getAsString(SqlConstant.CONTACT_COUNTRY_CODE));
//                contactModel.setPhoneNumber(contentValue
//                        .getAsString(SqlConstant.CONTACT_PHONE_NUMBER));
//                contactModel.setProfileImage(contentValue
//                        .getAsString(SqlConstant.CONTACT_IMAGE));
//                contactModel.setEmail(contentValue
//                        .getAsString(SqlConstant.CONTACT_EMAIL));
//                contactModel.setIsSynchedWithServer(contentValue
//                        .getAsString(SqlConstant.CONTACT_IS_SYNCHED));
//                contactModel.setPhoneNumberWithCode(contentValue
//                        .getAsString(SqlConstant.CONTACT_PHONE_WITH_CODE));
//                contactList.add(contactModel);
//            }
//        }
//        return contactList;
//    }

    /**
     * this method is used to get the max value of a column
     * @return
     */
    public int getMaxIndexValueFromDB()
    {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
       return dataBaseProvider.getMaxColumnData(TABLE_CONTACTS,CONTACT_SR_NUMBER);
    }

    /**
     * this method is used to delete particular contacts
     */
    public void deleteContacts(ArrayList<ContactResult> list) {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        for (ContactResult contactResult : list) {
            dataBaseProvider.delete(SqlConstant.TABLE_CONTACTS, SqlConstant.CONTACT_ROW_ID + " =? ", new String[]{contactResult.getRowId()});
        }

    }

    /**
     * update the Contact with JhaiHo User  in local storage
     *
     * @param
     */
    public void updateContactsSyncStatus( int lastIndex) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_IS_SYNCHED, "1");
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        dataBaseProvider.update(TABLE_CONTACTS, contentValues, CONTACT_SR_NUMBER + "<=\"" + lastIndex + "\"", null);
    }

    /**
     * update the Contact with JhaiHo User  in local storage
     *
     * @param
     */
    public void updateAllAppUsersInDB( List<ContentValues> contentValues) {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        dataBaseProvider.updateAll(TABLE_CONTACTS, contentValues);
    }

    /**
     * update all the edited Contacts in local storage
     *
     * @param
     */
    public void updateEditedContactsInDB(ArrayList<ContactResult> updatedContactList) {
        DataBaseProvider dataBaseProvider = DataBaseProvider.getInstance(context);
        if (updatedContactList != null && updatedContactList.size() > 0) {
            for (int i = 0; i < updatedContactList.size(); i++) {
                ContactResult contactModel=updatedContactList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(CONTACT_ID, contactModel.getContactId());
                contentValues.put(CONTACT_NAME, contactModel.getContactName());
                contentValues.put(CONTACT_IMAGE, contactModel.getProfileImage());
                contentValues.put(CONTACT_COUNTRY_CODE, contactModel.getCountryCode());
                contentValues.put(CONTACT_PHONE_NUMBER, contactModel.getPhoneNumber());
                contentValues.put(CONTACT_PHONE_WITH_CODE, contactModel.getPhoneNumberWithCode());
                contentValues.put(CONTACT_IS_APP_USER, contactModel.getIsAppUser());
                contentValues.put(CONTACT_EMAIL, contactModel.getEmail());
                contentValues.put(CONTACT_IS_SYNCHED, contactModel.getIsSynchedWithServer());
                dataBaseProvider.update(TABLE_CONTACTS, contentValues, CONTACT_ROW_ID + "=\"" + updatedContactList.get(i).getRowId() + "\"", null);
            }
        }
    }


}
