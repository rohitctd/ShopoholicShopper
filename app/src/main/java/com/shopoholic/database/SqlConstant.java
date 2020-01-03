package com.shopoholic.database;

/**
 * Contains slite db constants
 */
public class SqlConstant {

    public static String SQLITE_MASTER = "select * from  SQLITE_MASTER where tbl_name='?'";
    public static String TABLE_REMINDERS = "reminders";
    private static String CREATE_TABLE = " create table IF NOT EXISTS ";
    public static String OPEN_BRACES = " ( ";
    public static String CLOSE_BRACES = " ) ";
    public static String DATA_TYPE_VARCHAR = " varchar ";
    public static final String COMMA = ", ";

    public static String TABLE_CONTACTS = "contacts";
    public static String TABLE_MEDIA_FILES = "media_files";

    public static String MESSAGE_SENDER = "message_sender";
    public static String MESSAGE_ID = "message_id";
    public static String MESSAGE_TYPE = "message_type";
    public static String MESSAGE_TEXT = "message_text";
    public static String MESSAGE_URI = "message_uri";
    public static String MESSAGE_THUMBNAIL = "message_thumbnail";
    public static String MESSAGE_TIMESTAMP = "message_timestamp";
    public static String MESSAGE_STATUS = "message_status";
    public static String MESSAGE_CAPTION = "message_caption";
    public static String MESSAGE_ROOM_ID = "message_room_id";
    public static String MESSAGE_RECEIVER_ID = "message_receiver_id";
    public static String MESSAGE_DURATION = "message_duration";
    public static String MESSAGE_LATITUDE = "latitude";
    public static String MESSAGE_LONGITUDE = "longitude";
    public static String MESSAGE_BLOCK= "isBlock";


    public static String CREATE_TABLE_MEDIA_FILES = new StringBuilder()
            .append(CREATE_TABLE).append(TABLE_MEDIA_FILES)
            .append(OPEN_BRACES).append(MESSAGE_SENDER)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_ID)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_TYPE)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_TEXT)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_URI)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_THUMBNAIL)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_TIMESTAMP)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_STATUS)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_CAPTION)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_ROOM_ID)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_RECEIVER_ID)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_DURATION)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_LATITUDE)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_LONGITUDE)
            .append(DATA_TYPE_VARCHAR).append(COMMA).append(MESSAGE_BLOCK)
            .append(DATA_TYPE_VARCHAR).append(CLOSE_BRACES).toString();

}
