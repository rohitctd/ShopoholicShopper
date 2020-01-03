package com.dnitinverma.mylibrary.databaseLibrary;

/**
 * Created by appinventiv on 30/3/17.
 */
public class SqlConstant {

    public static String SQLITE_MASTER = "select * from  SQLITE_MASTER where tbl_name='?'";
    private static String CREATE_TABLE = " create table IF NOT EXISTS ";
    public static String OPEN_BRACES = " ( ";
    public static String CLOSE_BRACES = " ) ";
    public static String DATA_TYPE_VARCHAR = " VARCHAR ";
    public static final String AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String COMMA = ", ";

    public static String TABLE_CONTACTS = "CONTACTS";
    public static String CONTACT_SR_NUMBER = "CONTACT_SR_NUMBER";
    public static String CONTACT_NAME = "CONTACT_NAME";
    public static String CONTACT_COUNTRY_CODE="COUNTRY_CODE";
    public static String CONTACT_ID="CONTACT_ID";
    public static String CONTACT_MAIN_ID="CONTACT_MAIN_ID";
    public static String CONTACT_RAW_ID="CONTACT_RAW_ID";
    public static String CONTACT_IS_APP_USER="IS_APP_USER";
    public static String CONTACT_IMAGE = "CONTACT_IMAGE";
    public static String CONTACT_PHONE_NUMBER = "PHONE_NUMBER";
    public static String CONTACT_IS_SYNCHED = "CONTACT_IS_SYNCHED";
    public static String CONTACT_PHONE_WITH_CODE = "PHONE_WITH_CODE";
    public static String CONTACT_EMAIL="EMAIL";
    public static String CONTACT_ROW_ID="ROW_ID";
    public static String TABLE_EMAIL = "EMAIL_ADDRESSES";
    public static String EMAIL_CONTACT_ID="EMAIL_CONTACT_ID";
    public static String EMAIL_CONTACT_RAW_ID="EMAIL_CONTACT_RAW_ID";
    public static String EMAIL_CONTACT_MAIN_ID="EMAIL_CONTACT_MAIN_ID";
    public static String EMAIL_ADDRESS="EMAIL_ADDRESS";





    public static String CREATE_TABLE_CONTACTS = new StringBuilder()
            .append(CREATE_TABLE).append(TABLE_CONTACTS).append(OPEN_BRACES)
            .append(CONTACT_SR_NUMBER).append(AUTOINCREMENT).append(COMMA)
            .append(CONTACT_ID).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_MAIN_ID).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_RAW_ID).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_PHONE_NUMBER).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_COUNTRY_CODE).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_PHONE_WITH_CODE).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_NAME).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_EMAIL).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_ROW_ID).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_IMAGE).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_IS_APP_USER).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(CONTACT_IS_SYNCHED).append(DATA_TYPE_VARCHAR).append(CLOSE_BRACES).toString();

    public static String CREATE_TABLE_EMAIL = new StringBuilder()
            .append(CREATE_TABLE).append(TABLE_EMAIL).append(OPEN_BRACES)
            .append(EMAIL_CONTACT_ID).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(EMAIL_CONTACT_MAIN_ID).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(EMAIL_CONTACT_RAW_ID).append(DATA_TYPE_VARCHAR).append(COMMA)
            .append(EMAIL_ADDRESS).append(DATA_TYPE_VARCHAR)
            .append(CLOSE_BRACES).toString();

}
