package com.shopoholic.firebasechat.utils;

import com.shopoholic.BuildConfig;

/**
 * Constants used in Firebase
 */

public class FirebaseConstants {

    //file type constants
    public static final int FILE_IMAGE = 101;
    public static final int FILE_VIDEO = 102;
    public static final int FILE_AUDIO = 103;
    public static final int FILE_PDF = 104;
    public static final int FILE_TEXT = 105;

    public static final long OFFER_TIME = 7200000;

    //firebase database constants
    public static final String EMAIL = "email";
    public static final String OTHER_USER = "otherUser";
    public static final String ROOM_ID = "roomId";
    public static final String PENDING = "pending";
    public static final String SEND = "sent";
    public static final String READ = "seen";
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String TEXT_FILE = "text file";
    public static final String FILE = "file";
    public static final String PDF = "pdf";
    public static final String SHEET = "sheet";
    public static final String AUDIO = "audio";
    public static final String SINGLE_CHAT = "single";
    public static final String GROUP_CHAT = "group";
    public static final String TIME_STAMP = "timestamp";
    public static final String CHAT_TIME = "chat_time";
    public static final String MESSAGE_STATUS = "status";
    public static final String SINGLE = "single";
    public static final String HUNT = "hunt";
    public static final String ONLINE = "online";
    public static final String ACTION = "action";
    public static final String CHAT_ROOM_IS_TYPING = "chatRoomIsTyping";
    public static final String CREATE = "create";
    public static final String DETAILS = "details";
    public static final String ADD_NEW = "addNew";
    public static final String PROFILE = "profile";
    public static final String UPDATE = "rename";
    public static final String LOCATION = "location";
    public static final String DEVICE_TOKEN = "deviceToken";
    public static final String FAILED = "failed";
    public static final String IS_DELETE = "isDeleted";
    public static final String IS_PRODUCT = "isProduct";
    public static final String CHAT_ROOM_ID = "chatRoomId";
    public static final String CHAT_ROOM_TYPE = "chatRoomType";
    public static final String CHAT_ROOM_TITLE = "chatRoomTitle";
    public static final String CHAT_ROOM_PIC = "chatRoomPic";
    public static final String CHAT_LAST_UPDATE = "chatLastUpdate";
    public static final String CHAT_LAST_UPDATES = "chatLastUpdates";
    public static final String CHAT_ROOM_MEMBERS = "chatRoomMembers";
    public static final String USER_ID = "userId";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String USER_IMAGE = "userImage";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String MOBILE_NO = "mobileNumber";
    public static final String ONLINE_STATUS = "onlineStatus";
    public static final String CREATE_GROUP = "createGroup";
    public static final String OFFER = "offer";
    public static final String STATUS = "status";
    public static final String CURRENCY = "currency";
    public static final String PRICE = "price";
    public static final String MESSAGE = "message";
    public static final String OFFER_PRICE = "offerPrice";
    public static final String IS_SHARED = "isShared";

    //firebase database node constants
    public static final String USERS_NODE = "users";
    public static final String INBOX_NODE = "inbox";
    public static final String UNREAD_MESSAGES_COUNT_NODE = "unreadMessages";
    public static final String ROOM_INFO_NODE = "roomInfo";
    public static final String MEMBER_DELETE_NODE = "memberDelete";
    public static final String MEMBER_JOIN_NODE = "memberJoin";
    public static final String MEMBER_LEAVE_NODE = "memberLeave";
    public static final String MESSAGES_NODE = "messages";
    public static final String LAST_MESSAGE_NODE = "lastMessage";
    public static final String CHAT_LAST_MESSAGE_NODE = "chatLastMessage";
    public static final String BLOCK_NODE = "block";
    public static final String CHAT_ROOM_MEMBERS_NODE = "chatRoomMembers";
    public static final String ONLINE_STATUS_NODE = "onlineStatus";
    public static final String STATUS_NODE = "status";
    public static final String CHAT_LAST_UPDATES_NODE = "chatLastUpdates";
    public static final String CHAT_LAST_UPDATE_NODE = "chatLastUpdate";
    public static final String CHAT_ROOM_TITLE_NODE = "chatRoomTitle";
    public static final String CHAT_ROOM_PIC_NODE = "chatRoomPic";
    public static final String PRODUCT_ID = "id";
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_CURRENCY = "currency";
    public static final String PRODUCT_CURRENCY_CODE = "currencyCode";
    public static final String PRODUCT_CURRENCY_SYMBOL = "currencySymbol";
    public static final String PRODUCT_QUANTITY = "quantity";
    public static final String PRODUCT_IMAGE = "image";
    public static final String CHAT_ROOM_PRODUCT = "product";
    public static final String CHAT_ROOM_HUNT = "huntDeal";
    public static final String CHAT_ROOM_PRODUCT_ID = "productId";
    public static final String CHAT_ROOM_HUNT_ID = "huntId";
    public static final String ORIGINAL_PRICE = "originalPrice";
    public static final String DISCOUNT = "discount";
    public static final String DEAL_START_TIME = "dealStartTime";
    public static final String DEAL_END_TIME = "dealEndTime";
    public static final String HOME_DELIVERY = "homeDelivery";
    public static final String PAYMENT_MODE = "paymentMode";
    public static final String USER_TYPE = "userType";
    public static final String BUDDY_ID = "buddyId";
    public static final String PRODUCT_TYPE = "productType";
    public static final String SLOT_ID = "slotId";
    public static final String SLOT_DATES = "slotDates";
    public static final String DELIVERY_CHARGES = "deliveryCharges";
    public static final String MERCHANT_FIRST_NAME = "merchantFirstName";
    public static final String MERCHANT_LAST_NAME = "merchantLastName";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String TAG = "log_tag";
    public static final String ID = "id";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String SUB_CATEGORY_NAME = "subCategoryName";
    public static final String UNREAD_COUNT = "unreadCount";
    public static final String TAX_ARR = "taxArr";
    public static final String HUNT_TITLE = "huntTitle";
    public static final String HUNT_IMAGE = "huntImage";
    public static final String SELECTED_SLOTS = "selectedSlots";


    //notification constants
    public static final String SEND_PUSH_NOTIFICATION = "https://fcm.googleapis.com/fcm/send";
    public static final String NOTIFICATION = "notification";
    public static final String NOTIFICATION_CHANNEL_GROUP = "notification_channel_group";
    public static final String PARAM_CONTENT_TYPE = "Content-Type";
    public static final String PARAM_AUTHORIZATION = "Authorization";
    public static final String APPLICATION_JSON = "application/json";
    public static final String FIREBASE_SERVER_KEY = "key=" + BuildConfig.FIREBASE_SERVER_KEY;
    public static final String ADDRESS = "address";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
}
