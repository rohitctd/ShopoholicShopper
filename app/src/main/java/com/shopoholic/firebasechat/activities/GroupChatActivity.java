package com.shopoholic.firebasechat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cameraandgallery.activities.CameraGalleryActivity;
import com.dnitinverma.amazons3library.AmazonS3;
import com.dnitinverma.amazons3library.interfaces.AmazonCallback;
import com.dnitinverma.amazons3library.model.ImageBean;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.multiple_media_picker.ImagesGallery;
import com.multiple_media_picker.VideosGallery;
import com.shopoholic.R;
import com.shopoholic.activities.WebViewActivity;
import com.shopoholic.customviews.CustomTextView;
import com.shopoholic.database.AppDatabase;
import com.shopoholic.firebasechat.adapters.ChatListAdapter;
import com.shopoholic.firebasechat.adapters.LocationBean;
import com.shopoholic.firebasechat.dialogs.CustomDialogForCreateGroup;
import com.shopoholic.firebasechat.dialogs.CustomDialogForFiles;
import com.shopoholic.firebasechat.dialogs.CustomDialogForShareLocation;
import com.shopoholic.firebasechat.interfaces.FilePickerDialogCallback;
import com.shopoholic.firebasechat.interfaces.FirebaseMessageListener;
import com.shopoholic.firebasechat.interfaces.FirebaseProductResponseListener;
import com.shopoholic.firebasechat.interfaces.FirebaseRoomResponseListener;
import com.shopoholic.firebasechat.interfaces.GroupCallback;
import com.shopoholic.firebasechat.interfaces.LocationDialogCallback;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.ChatMessageBean;
import com.shopoholic.firebasechat.models.ChatRoomBean;
import com.shopoholic.firebasechat.models.ProductBean;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseChatUtils;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.firebasechat.utils.FirebaseEventListeners;
import com.shopoholic.interfaces.PopupItemDialogCallback;
import com.shopoholic.models.productservicedetailsresponse.Result;
import com.shopoholic.utils.AppSharedPreference;
import com.shopoholic.utils.AppUtils;
import com.shopoholic.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Class used for one to one chat
 */

public class GroupChatActivity extends AppCompatActivity implements Comparator<ChatMessageBean> {

    private final int REQUEST_STORAGE_LOCATION_PERMISSION = 101;
    private final int REQUEST_CAMERA = 1001, CROPPER_REQUEST_CODE = 1002, REQUEST_AUDIO = 1003, REQUEST_FILE = 1004,
            MULTIPLE_IMAGE_INTENT = 1005, MULTIPLE_VIDEO_INTENT = 1006;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_user_image)
    CircleImageView ivUserImage;
    @BindView(R.id.tv_title)
    CustomTextView tvTitle;
    @BindView(R.id.tv_typing)
    CustomTextView tvTyping;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.iv_more)
    ImageView ivMore;
    @BindView(R.id.layout_toolbar)
    Toolbar layoutToolbar;
    @BindView(R.id.iv_product_image)
    ImageView ivProductImage;
    @BindView(R.id.tv_product_name)
    CustomTextView tvProductName;
    @BindView(R.id.tv_product_price)
    CustomTextView tvProductPrice;
    @BindView(R.id.tv_product_quantity)
    CustomTextView tvProductQuantity;
    @BindView(R.id.rl_product)
    RelativeLayout rlProduct;
    @BindView(R.id.rv_chat)
    RecyclerView rvChat;
    @BindView(R.id.layout_no_data_found)
    CustomTextView layoutNoDataFound;
    @BindView(R.id.ll_product_deal)
    LinearLayout llProductDeal;
    @BindView(R.id.et_write_message)
    EditText etWriteMessage;
    @BindView(R.id.iv_attachments)
    ImageView ivAttachments;
    @BindView(R.id.iv_camera)
    ImageView ivCamera;
    @BindView(R.id.iv_send_messages)
    ImageView ivSendMessages;
    @BindView(R.id.ll_bottom_bar)
    LinearLayout llBottomBar;
    @BindView(R.id.tv_leave_status)
    AppCompatTextView tvLeaveStatus;


    private String loginUserId;
    private String loginUserName;
    private String roomId;
    private UserBean loginUser;
    private ChatRoomBean chatRoom;
    private FirebaseEventListeners messageListener, typingListener;
    private List<ChatMessageBean> chatMessagesList;
    private ChatListAdapter chatListAdapter;
    private LinearLayoutManager layoutManager;
    private HashMap<String, Object> memberDetails;
    private boolean isLoadingMessage;
    private Uri outputUri;
    private int maxImageSelect = 5, maxVideoSelect = 1;
    private ArrayList<UserBean> usersList;
    private FirebaseEventListeners usersListener;
    private FirebaseEventListeners memberStatusListener;
    private FirebaseEventListeners roomTitleListener;
    private CustomDialogForCreateGroup groupDialog;
    public List<String> selectMessagesId;
    public List<String> selectMsgUserId;
    private boolean leaveGroup;
    private int permissionType;
    private Uri mCapturedImageURI;
    private Result productDetails;
    private String productId = "";
    private ArrayList<String> imagesList;
    private boolean openPlacePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        initVariables();
        setListener();
        setAdapters();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (roomId != null) {
            AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_CHAT_ROOM, roomId);
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_LAST_UPDATES)
                    .child(loginUserId).setValue(ServerValue.TIMESTAMP);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (roomId != null) {
            FirebaseDatabaseQueries.getInstance().updateUnreadCount(this, roomId);
            AppSharedPreference.getInstance().putString(this, AppSharedPreference.PREF_KEY.CURRENT_CHAT_ROOM, "");
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_LAST_UPDATES)
                    .child(loginUserId).setValue(ServerValue.TIMESTAMP);
        }
    }

    /**
     * method to initialize the variables
     */
    private void initVariables() {
        chatMessagesList = new ArrayList<>();
        usersList = new ArrayList<>();
        selectMessagesId = new ArrayList<>();
        selectMsgUserId = new ArrayList<>();
        imagesList = new ArrayList<>();
        loginUser = FirebaseDatabaseQueries.getInstance().getCurrentUser(this);
        loginUserId = loginUser.getUserId();
        loginUserName = loginUser.getFirstName() + loginUser.getLastName();
        //get data from intent
        if (getIntent() != null && getIntent().getExtras() != null) {
            roomId = getIntent().getExtras().getString(FirebaseConstants.ROOM_ID, null);
            if (getIntent().hasExtra(FirebaseConstants.CHAT_ROOM_PRODUCT)) {
                productDetails = (Result) getIntent().getExtras().getSerializable(FirebaseConstants.CHAT_ROOM_PRODUCT);
                if (productDetails != null) {
                    tvProductName.setText(productDetails.getName());
                    String currency = productDetails.getCurrencySymbol();
//                    String currency = getString(productDetails.getCurrency().equals("2") ? R.string.rupees : productDetails.getCurrency().equals("1") ? R.string.dollar : R.string.singapuri_dollar);
                    tvProductPrice.setText(TextUtils.concat(currency + productDetails.getSellingPrice()));
                    tvProductQuantity.setText(TextUtils.concat(getString(R.string.quantity) + ": " + productDetails.getQuantity()));
                    productId = productDetails.getId();
                    try {
                        AppUtils.getInstance().setImages(this, productDetails.getDealImages().split(",")[0], ivProductImage, 0, R.drawable.ic_placeholder);
                    } catch (Exception ignored) {
                    }
                } else {
                    rlProduct.setVisibility(View.GONE);
                    llProductDeal.setVisibility(View.GONE);
                    ivAdd.setVisibility(View.GONE);
                }
            }
        }

        //check other user exists or not
        if (roomId == null) {
            FirebaseChatUtils.getInstance().showToast(this, getString(R.string.group_not_exists));
            finish();
        }

        FirebaseDatabaseQueries.getInstance().updateUnreadCount(this, roomId);
        etWriteMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        //set layout manager and adapter
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        chatListAdapter = new ChatListAdapter(this, chatMessagesList, usersList, new RecycleViewCallBack() {
            @Override
            public void onClick(int position, View clickedView) {
//                if (selectMessagesId.size() > 0) {
//                    if (selectMessagesId.contains(chatMessagesList.get(position).getMessageId())) {
//                        selectMessagesId.remove(chatMessagesList.get(position).getMessageId());
//                        selectMsgUserId.remove(chatMessagesList.get(position).getSenderId());
//                        chatListAdapter.notifyItemChanged(position);
//                    } else {
//                        selectMessagesId.add(chatMessagesList.get(position).getMessageId());
//                        selectMsgUserId.add(chatMessagesList.get(position).getSenderId());
//                        chatListAdapter.notifyItemChanged(position);
//                    }
//                    updateViews();
//                } else {
                int id = clickedView.getId();
                if (id == R.id.iv_play_video) {
                    Uri uri = Uri.parse(chatMessagesList.get(position).getMediaUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                    updateViews();
                } else if (id == R.id.ll_message) {
                    if (chatMessagesList.get(position).getType().equals(FirebaseConstants.LOCATION)) {
                        double latitude = chatMessagesList.get(position).getLatitude();
                        double longitude = chatMessagesList.get(position).getLongitude();
                        Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(Location)");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                    if (chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.FILE) ||
                            chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.PDF) ||
                            chatMessagesList.get(position).getType().equalsIgnoreCase(FirebaseConstants.SHEET)) {
                        Intent containerIntent = new Intent(GroupChatActivity.this, WebViewActivity.class);
                        containerIntent.putExtra(chatMessagesList.get(position).getType(), chatMessagesList.get(position).getMediaUrl());
                        startActivity(containerIntent);
                    }
                    if (chatMessagesList.get(position).getType().equals(FirebaseConstants.IMAGE)) {
                        showImages(chatMessagesList.get(position));
                    }
                    updateViews();
                } else if (id == R.id.tv_resend) {
                    chatMessagesList.get(position).setStatus(FirebaseConstants.PENDING);
                    startUpload(chatMessagesList.get(position));
                    chatListAdapter.notifyItemChanged(position);
                    updateViews();
                }
            }
//            }

            @Override
            public void onLongClick(int position, View clickedView) {
                switch (clickedView.getId()) {
                    case R.id.ll_message_row:
                        /*if (chatMessagesList.get(position).getSenderId().equals(loginUserId)) {
                            showDeleteDialog(chatMessagesList.get(position));
                        }*/
                        break;
                }
            }
        });

        //call to get room details
        getChatRoomDetails(roomId);
        //set support action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);

    }


    /**
     * method to show the message delete dialog
     */
    private void showDeleteDialog(final ChatMessageBean chatMessageBean) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.ic_launcher);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.add(getString(R.string.delete_message));

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        String messageId = chatMessageBean.getMessageId();
                        FirebaseDatabaseQueries.getInstance().setMessagesDeleteStatus(roomId, messageId);
                        if (chatMessagesList.get(0).getMessageId().equals(messageId)) {
                            if (chatMessagesList.size() > 1) {
                                ChatMessageBean message = chatMessagesList.get(0);
                                if (!chatMessagesList.get(1).getType().equals(FirebaseConstants.CHAT_TIME)) {
                                    message = chatMessagesList.get(1);
                                } else if (chatMessagesList.size() > 2) {
                                    message = chatMessagesList.get(2);
                                }
                                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId)
                                        .child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).setValue(message);
                            } else {
//                                FirebaseDatabaseQueries.getInstance().deleteRoom(roomId, FirebaseConstants.GROUP_CHAT, "", productId);
//                                setWalletDetail(RESULT_OK);
//                                finish();
                            }
                        }
                        break;
                }
            }
        });
        builderSingle.show();
    }


    /**
     * method to show the images
     *
     * @param chatMessage
     */
    public void showImages(ChatMessageBean chatMessage) {
        Intent intent = new Intent(this, FullScreenImageSliderActivity.class);
        intent.putExtra("imagelist", imagesList);
        intent.putExtra("from", chatRoom.getChatRoomTitle());
        for (int i = 0; i < imagesList.size(); i++) {
            if (imagesList.get(i).equalsIgnoreCase(chatMessage.getMediaUrl())) {
                intent.putExtra("pos", i);
            }
        }
        startActivity(intent);
    }

    /**
     * method to set listener on views
     */
    private void setListener() {
        etWriteMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (roomId != null) {
                    if (etWriteMessage.getText().toString().length() == 0) {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(loginUserId).setValue(false);
                        ivAttachments.setVisibility(View.VISIBLE);
                        ivCamera.setVisibility(View.VISIBLE);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(loginUserId).setValue(true);
                        ivAttachments.setVisibility(View.GONE);
                        ivCamera.setVisibility(View.GONE);
                    }

                }
            }
        });

        rvChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
              /*  if (dy > 0) {
                    // Scrolling up
                    ExpandViewAnimation.collapse(llProductDeal);
                } else {
                    // Scrolling down
                    ExpandViewAnimation.expand(llProductDeal);
                }*/
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoadingMessage && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    isLoadingMessage = true;
                    getPreviousMessages(Long.parseLong(chatMessagesList.get(chatMessagesList.size() - 1).getTimestamp().toString()) - 1);
                }
            }
        });
    }

    /**
     * method to get the previous messages
     *
     * @param timeStamp
     */
    private void getPreviousMessages(long timeStamp) {
        if (chatRoom != null) {
            memberDetails = (HashMap<String, Object>) chatRoom.getChatRoomMembers().get(loginUserId);
            FirebaseDatabaseQueries.getInstance().getPreviousMessages(roomId, Long.parseLong(memberDetails.get(FirebaseConstants.MEMBER_DELETE_NODE).toString()), timeStamp - 1, new FirebaseMessageListener() {
                @Override
                public void getMessages(ChatMessageBean message) {
                }

                @Override
                public void getMessagesList(List<ChatMessageBean> messagesList) {
                    if (messagesList.size() > 0) {
                        if (chatMessagesList.size() > 0)
                            chatMessagesList.remove(chatMessagesList.size() - 1);
                        int previousPosition = chatMessagesList.size() - 1;
                        for (int i = 0; i <= messagesList.size(); i++) {
                            ChatMessageBean time = null;
                            if (i == 0) {
                                if (chatMessagesList.size() > 0)
                                    time = FirebaseChatUtils.getInstance().getDateStamp(GroupChatActivity.this, chatMessagesList.get(chatMessagesList.size() - 1), messagesList.get(i));
                            } else if (i == messagesList.size()) {
                                time = FirebaseChatUtils.getInstance().getDateStamp(GroupChatActivity.this, messagesList.get(i - 1), null);
                            } else {
                                time = FirebaseChatUtils.getInstance().getDateStamp(GroupChatActivity.this, messagesList.get(i - 1), messagesList.get(i));
                            }
                            if (time != null) {
                                messagesList.add(i, time);
                                i++;
                            }
                        }
                        chatMessagesList.addAll(messagesList);
                        chatListAdapter.notifyItemRangeChanged(previousPosition, chatMessagesList.size() - 1);
                        isLoadingMessage = false;
                        updateViews();
                    }
                }
            });
        }
    }

    /**
     * method to set adapter in views
     */
    private void setAdapters() {
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatListAdapter);

        List<ChatMessageBean> previousMessages = (List<ChatMessageBean>) AppDatabase.fetchMediaFilesDetails(roomId);
        if (previousMessages != null && previousMessages.size() > 0) {
            layoutNoDataFound.setVisibility(View.GONE);
            for (int i = 0; i < previousMessages.size(); i++) {
                previousMessages.get(i).setStatus(FirebaseConstants.FAILED);
                setNewChatMessage(previousMessages.get(i));
                if (previousMessages.get(i) != null && previousMessages.get(i).getType().equals(FirebaseConstants.IMAGE))
                    imagesList.add(previousMessages.get(i).getMediaUrl());
            }
        }
    }

    /**
     * Method to get the chat room details
     *
     * @param roomId
     */
    private void getChatRoomDetails(String roomId) {
        FirebaseDatabaseQueries.getInstance().getRoomDetails(roomId, new FirebaseRoomResponseListener() {
            @Override
            public void getRoomId(String roomId) {
            }

            @Override
            public void getRoomDetails(ChatRoomBean chatRoomBean) {
                GroupChatActivity.this.chatRoom = chatRoomBean;
                if (chatRoomBean.getProductId() != null && !chatRoomBean.getProductId().equals("")) {
                    getProductDetails();
                }else {
                    for (int i = 0; i < chatRoom.getChatRoomMembers().size(); i++) {
                        String memberId = chatRoom.getChatRoomMembers().keySet().toArray()[i].toString();
                        if (((HashMap<String, Object>) chatRoom.getChatRoomMembers().get(memberId))
                                .get(FirebaseConstants.MEMBER_DELETE_NODE).toString().equals("0")) {
                            getMembersList(memberId);
                        }
                        if (memberId.equals(loginUserId))
                            setTypingListener();
                    }
                    tvTitle.setText(chatRoom.getChatRoomTitle());
                    FirebaseChatUtils.getInstance().setImageOnView(GroupChatActivity.this, chatRoom.getChatRoomPic(), ivUserImage, 0,
                            R.drawable.ic_friend_placeholder, null, true);
                    getLeaveStatus();
                    setMemberStatusListener();
                }
            }
        });
    }

    /**
     * get product details
     */
    private void getProductDetails() {
        FirebaseDatabaseQueries.getInstance().getProductDetails(chatRoom.getProductId(), new FirebaseProductResponseListener() {
            @Override
            public void getProductDetails(ProductBean productBean) {
                if (productBean.getProductType().equals("2")) productBean.setPrice(chatRoom.getPrice());
                chatRoom.setProduct(productBean);
                for (int i = 0; i < chatRoom.getChatRoomMembers().size(); i++) {
                    String memberId = chatRoom.getChatRoomMembers().keySet().toArray()[i].toString();
                    if (((HashMap<String, Object>) chatRoom.getChatRoomMembers().get(memberId))
                            .get(FirebaseConstants.MEMBER_DELETE_NODE).toString().equals("0")) {
                        getMembersList(memberId);
                    }
                    if (memberId.equals(loginUserId))
                        setTypingListener();
                }
                tvTitle.setText(chatRoom.getChatRoomTitle());
                FirebaseChatUtils.getInstance().setImageOnView(GroupChatActivity.this, chatRoom.getChatRoomPic(), ivUserImage, 0,
                        R.drawable.ic_friend_placeholder, null, true);
                getLeaveStatus();
                setMemberStatusListener();
            }
        });
    }

    /**
     * Method to set listener for member status
     */
    private void setMemberStatusListener() {
        memberStatusListener = new FirebaseEventListeners() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.child(FirebaseConstants.MEMBER_LEAVE_NODE).getValue().toString().equals("0")) {
                        getMembersList(dataSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    boolean isNew = true;
                    for (int i = 0; i < usersList.size(); i++) {
                        if (dataSnapshot.getKey().equals(usersList.get(i).getUserId())) {
                            if (dataSnapshot.child(FirebaseConstants.MEMBER_LEAVE_NODE).getValue().toString().equals("0")) {
                                chatRoom.getChatRoomMembers().put(dataSnapshot.getKey(), dataSnapshot.getValue());
                            } else {
                                usersList.remove(i);
                            }
                            isNew = false;
                            break;
                        }
                    }
                    if (isNew) {
                        usersList.add(dataSnapshot.getValue(UserBean.class));
                    }
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE)
                .addChildEventListener(memberStatusListener);

        roomTitleListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    if (dataSnapshot.getKey().equals(FirebaseConstants.CHAT_ROOM_TITLE_NODE)) {
                        tvTitle.setText(dataSnapshot.getValue().toString());
                        chatRoom.setChatRoomTitle(dataSnapshot.getValue().toString());
                    }
                    if (dataSnapshot.getKey().equals(FirebaseConstants.CHAT_ROOM_PIC_NODE)) {
                        FirebaseChatUtils.getInstance().setImageOnView(GroupChatActivity.this, dataSnapshot.getValue().toString(), ivUserImage, 0,
                                R.drawable.ic_friend_placeholder, null, true);
                        chatRoom.setChatRoomPic(dataSnapshot.getValue().toString());
                    }
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_TITLE_NODE)
                .addValueEventListener(roomTitleListener);
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_PIC_NODE)
                .addValueEventListener(roomTitleListener);
    }

    /**
     * Method to get login user leave status
     */
    private void getLeaveStatus() {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE)
                .child(loginUserId).child(FirebaseConstants.MEMBER_LEAVE_NODE).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Long timestamp = (Long) dataSnapshot.getValue();
                    if (timestamp != null)
                        if (timestamp == 0) {
                            tvLeaveStatus.setVisibility(View.GONE);
                            leaveGroup = false;
                            getChatMessages();
                        } else {
                            tvLeaveStatus.setVisibility(View.VISIBLE);
                            leaveGroup = true;
                            removeAllListeners();
                            getPreviousMessages(timestamp);
                        }
                }
            }
        });
    }

    /**
     * Method to get the details of all members of room
     */
    private void getMembersList(String userId) {

        usersListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserBean userBean = dataSnapshot.getValue(UserBean.class);
                    boolean newMember = true;
                    for (int i = 0; i < usersList.size(); i++) {
                        if (userBean != null && usersList.get(i).getUserId().equals(userBean.getUserId())) {
                            usersList.set(i, userBean);
                            newMember = false;
                            break;
                        }
                    }
                    if (newMember)
                        usersList.add(userBean);
                } else {
                    UserBean userBean = dataSnapshot.getValue(UserBean.class);
                    for (int i = 0; i < usersList.size(); i++) {
                        if (userBean != null && usersList.get(i).getUserId().equals(userBean.getUserId())) {
                            usersList.remove(i);
                            break;
                        }
                    }
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(userId).addValueEventListener(usersListener);
    }

    /**
     * Method to get all chat messages
     */
    private void getChatMessages() {
        memberDetails = (HashMap<String, Object>) chatRoom.getChatRoomMembers().get(loginUserId);
        messageListener = new FirebaseEventListeners() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    ChatMessageBean chatMessage = dataSnapshot.getValue(ChatMessageBean.class);
                    if (chatMessage != null && !chatMessage.getIsDeleted()) {
                        setChatMessageInList(chatMessage);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    ChatMessageBean chatMessage = dataSnapshot.getValue(ChatMessageBean.class);
                    if (chatMessage != null && chatMessage.getIsDeleted()) {
                        for (int i = 0; i < chatMessagesList.size(); i++) {
                            if (chatMessagesList.get(i).getMessageId() != null && chatMessagesList.get(i).getMessageId().equals(chatMessage.getMessageId())) {
                                if (chatMessagesList.size() > (i + 1) && (i == 0
                                        || chatMessagesList.get(i - 1).getType().equals(FirebaseConstants.CHAT_TIME))
                                        && chatMessagesList.get(i + 1).getType().equals(FirebaseConstants.CHAT_TIME)) {
                                    chatMessagesList.remove(i + 1);
                                    chatListAdapter.notifyItemRemoved(i + 1);
                                }
                                chatMessagesList.remove(i);
                                chatListAdapter.notifyItemRemoved(i);
                                chatListAdapter.notifyItemRangeChanged(i, chatMessagesList.size());
                                updateViews();
                                break;
                            }
                        }
                    } else {
                        if (chatMessage != null && chatMessage.getType().equals(FirebaseConstants.TEXT)) {
                            for (int i = 0; i < chatMessagesList.size(); i++) {
                                if (chatMessagesList.get(i).getMessageId() != null && chatMessagesList.get(i).getMessageId().equals(chatMessage.getMessageId())) {
                                    chatMessagesList.set(i, chatMessage);
                                    chatListAdapter.notifyItemChanged(i);
                                    updateViews();
                                    break;
                                }
                            }
                        } else if (chatMessage != null) {
                            setChatMessageInList(chatMessage);
                        }
                    }
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).child(roomId).orderByChild(FirebaseConstants.TIME_STAMP).limitToLast(50)
                .startAt(Long.parseLong(memberDetails.get(FirebaseConstants.MEMBER_DELETE_NODE).toString())).addChildEventListener(messageListener);

        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).child(roomId).orderByChild(FirebaseConstants.TIME_STAMP).limitToLast(50)
                .startAt(Long.parseLong(memberDetails.get(FirebaseConstants.MEMBER_DELETE_NODE).toString())).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    layoutNoDataFound.setVisibility(View.GONE);
                } else {
//                    layoutNoDataFound.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * method to set chat message in list
     *
     * @param chatMessage
     */
    private void setChatMessageInList(ChatMessageBean chatMessage) {
        boolean newMessage = true;
        if (chatMessage == null || !chatMessage.getIsBlock() || !chatMessage.getReceiverId().equals(loginUserId)) {
            if (chatMessage != null && !chatMessage.getType().equals(FirebaseConstants.TEXT)) {
                for (int i = 0; i < chatMessagesList.size(); i++) {
                    if (!chatMessagesList.get(i).getType().equals(FirebaseConstants.CHAT_TIME) &&
                            chatMessagesList.get(i).getMessageId().equals(chatMessage.getMessageId())) {
                        ChatMessageBean previousMessage = AppDatabase.fetchSingleMediaFilesDetails(chatMessage.getMessageId());
                        if (previousMessage == null) previousMessage = chatMessagesList.get(i);
                        chatMessage.setMediaUrl(previousMessage.getMediaUrl());
                        chatMessage.setThumbnail(previousMessage.getThumbnail());
                        chatMessagesList.set(i, chatMessage);
                        chatListAdapter.notifyItemChanged(i);
                        newMessage = false;
                        AppDatabase.removeMediaFileDetailsFromDb(chatMessage.getMessageId());
                        break;
                    }
                }
            }
            if (newMessage && chatMessage != null) {
                setNewChatMessage(chatMessage);
                if (chatMessage.getType().equals(FirebaseConstants.IMAGE))
                    imagesList.add(chatMessage.getMediaUrl());
            }
        }
    }

    /**
     * Method to get the typing status of other person
     */
    private void setTypingListener() {
        typingListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, Boolean> typingStatus = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    int count = 0;
                    String id = "";
                    Iterator it = typingStatus.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        if (!pair.getKey().equals(loginUserId) && (boolean) pair.getValue()) {
                            count++;
                            id = pair.getKey().toString();
                        }
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                    if (count > 0) {
                        if (count == 1) {
                            for (UserBean user : usersList) {
                                if (user.getUserId().equals(id)) {
                                    tvTyping.setVisibility(View.VISIBLE);
                                    tvTyping.setText(TextUtils.concat(user.getFirstName() + " " + getString(R.string.is_typing)));
                                    break;
                                }
                            }
                        } else {
                            tvTyping.setVisibility(View.VISIBLE);
                            tvTyping.setText(TextUtils.concat(count + " " + getString(R.string.are_typing)));
                        }
                    } else {
                        tvTyping.setVisibility(View.GONE);
                    }
                } else {
                    tvTyping.setVisibility(View.GONE);
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING)
                .addValueEventListener(typingListener);
    }

    /*private void setTypingListener() {
        typingListener = new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ArrayList<String> typingStatusList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Boolean> typingStatus = (HashMap<String, Boolean>) snapshot.getValue();
                        if (typingStatus != null && !typingStatus.keySet().toArray()[0].equals(loginUserId)
                                && typingStatus.containsValue(true)) {
                            typingStatusList.add(typingStatus.keySet().toArray()[0].toString());
                        }
                    }
                    if (typingStatusList.size() > 0){
                        if (typingStatusList.size() == 1){
                            for (UserBean user : usersList) {
                                if (user.getUserId().equals(typingStatusList.get(0))){
                                    tvTyping.setText(TextUtils.concat(user.getFirstName() + getString(R.string.is_typing)));
                                }
                            }
                        }else {
                            tvTyping.setText(TextUtils.concat(typingStatusList.size() + getString(R.string.are_typing)));
                        }
                    } else {
                        tvTyping.setVisibility(View.GONE);
                    }
                } else {
                    tvTyping.setVisibility(View.GONE);
                }
            }
        };
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING)
                .addValueEventListener(typingListener);
    }
*/

    /**
     * Method to set new message in message list
     *
     * @param chatMessage
     */
    private void setNewChatMessage(ChatMessageBean chatMessage) {
        if (chatMessagesList.size() > 0 && chatMessagesList.get(chatMessagesList.size() - 1).getType().equals(FirebaseConstants.CHAT_TIME)) {
            chatMessagesList.remove(chatMessagesList.size() - 1);
        }
        chatMessagesList.add(chatMessage);
        Collections.sort(chatMessagesList, this);

        ChatMessageBean time = null;
        if (chatMessagesList.size() == 1) {
            time = FirebaseChatUtils.getInstance().getDateStamp(GroupChatActivity.this, chatMessage, null);
            if (time != null) {
                chatMessagesList.add(time);
            }
        } else {
            for (int i = 1; i < chatMessagesList.size(); i++) {
                if (!chatMessagesList.get(i - 1).getType().equals(FirebaseConstants.CHAT_TIME) && !chatMessagesList.get(i).getType().equals(FirebaseConstants.CHAT_TIME)) {
                    time = FirebaseChatUtils.getInstance().getDateStamp(GroupChatActivity.this, chatMessagesList.get(i - 1), chatMessagesList.get(i));
                    if (time != null) {
                        chatMessagesList.add(i, time);
                        i++;
                    }
                }
            }
            if (chatMessagesList.size() > 0) {
                time = FirebaseChatUtils.getInstance().getDateStamp(GroupChatActivity.this, chatMessagesList.get(chatMessagesList.size() - 1), null);
                if (time != null) {
                    chatMessagesList.add(time);
                }
            }
        }

        chatListAdapter.notifyDataSetChanged();
        updateViews();
    }


    @Override
    public int compare(ChatMessageBean lhs, ChatMessageBean rhs) {
        if (lhs.getTimestamp() != null && rhs.getTimestamp() != null) {
            long lhsTime = Long.parseLong(lhs.getTimestamp().toString());
            long rhsTime = Long.parseLong(rhs.getTimestamp().toString());
            if (lhsTime < rhsTime) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @OnClick({R.id.iv_back, R.id.iv_add, R.id.iv_more, R.id.iv_attachments, R.id.iv_camera, R.id.iv_send_messages})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_add:
                Intent intent = new Intent(this, CreateGroupActivity.class);
                intent.putExtra(FirebaseConstants.OTHER_USER, usersList);
                intent.putExtra(FirebaseConstants.CHAT_ROOM_TITLE, chatRoom.getChatRoomTitle());
                intent.putExtra(FirebaseConstants.CHAT_ROOM_PIC, chatRoom.getChatRoomPic());
                intent.putExtra(FirebaseConstants.CHAT_ROOM_ID, chatRoom.getChatRoomId());
                intent.putExtra(FirebaseConstants.CREATE_GROUP, false);
                startActivityForResult(intent, Constants.IntentConstant.REQUEST_CREATE_GROUP);
                break;
            case R.id.iv_more:
                showMorePopUp(findViewById(R.id.iv_more));
                break;
            case R.id.iv_attachments:
                permissionType = Constants.IntentConstant.REQUEST_GALLERY;
                checkStorageLocationPermission();
                break;
            case R.id.iv_camera:
                permissionType = Constants.IntentConstant.REQUEST_CAMERA;
                checkStorageLocationPermission();
                break;
            case R.id.iv_send_messages:
                if (etWriteMessage.getText().toString().trim().length() != 0) {
                    String message = etWriteMessage.getText().toString().trim();
                    etWriteMessage.setText("");
                    ChatMessageBean chatMessage = new ChatMessageBean();
                    chatMessage.setMessageText(message);
                    chatMessage.setType(FirebaseConstants.TEXT);
                    chatMessage.setMediaUrl("");
                    chatMessage.setThumbnail("");
                    createMessage(chatMessage);
                }
                break;
        }
    }

    /**
     * method to show more popup
     *
     * @param view
     */
    private void showMorePopUp(View view) {
        AppUtils.getInstance().showMorePopUp(this, view, !leaveGroup ? getString(R.string.leave) : getString(R.string.left), "", "", 2, new PopupItemDialogCallback() {
            @Override
            public void onItemOneClick() {
                if (!leaveGroup) {
                    showLeaveDialog();
                }
            }

            @Override
            public void onItemTwoClick() {
            }

            @Override
            public void onItemThreeClick() {
            }
        });
    }


    /**
     * Method to create local message
     *
     * @param chatMessage
     */
    private void createMessage(ChatMessageBean chatMessage) {
        chatMessage.setIsBlock(false);
        chatMessage.setReceiverId(roomId);
        chatMessage.setSenderId(loginUserId);
        chatMessage.setRoomId(roomId);
        FirebaseDatabaseQueries.getInstance().sendChatMessage(chatMessage, usersList, FirebaseConstants.GROUP_CHAT, chatRoom.getChatRoomTitle(), "");
    }


    /**
     * Method to get the current location of user
     *
     * @param location
     */
    private void getCurrentLocation(LatLng location) {
        if (location != null) {
            double longitude = location.longitude;
            double latitude = location.latitude;
            LocationBean locationBean = new LocationBean();
            locationBean.setLatitude(latitude);
            locationBean.setLongitude(longitude);
            locationBean.setLocationUri(FirebaseChatUtils.getInstance().getStaticMapImage(this, latitude, longitude));
            sendLocation(locationBean);
        } else {
            FirebaseChatUtils.getInstance().showToast(GroupChatActivity.this, getString(R.string.unable_to_fetch_location));
        }
    }


    /**
     * Checks permission to Write external storage in Marshmallow and above devices
     */
    private void checkStorageLocationPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //do your check here

            if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, REQUEST_STORAGE_LOCATION_PERMISSION);
            } else {
                // permission already granted
                if (permissionType == Constants.IntentConstant.REQUEST_CAMERA) {
//                    File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name));
//                    if (!myDir.exists()) myDir.mkdir();
//                    String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
//                    File file = new File(myDir, fileName);
//                    outputUri = Uri.fromFile(file);
//                    ImageCropper.startCaptureImageActivity(GroupChatActivity.this, REQUEST_CAMERA, CROPPER_REQUEST_CODE);

//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.Media.TITLE, "shopoholic_" + System.currentTimeMillis() + ".jpeg");
//                    mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                    takePictureIntent.putExtra("return-data", true);
//                    startActivityForResult(takePictureIntent, REQUEST_CAMERA);

                    startActivityForResult(new Intent(this, CameraGalleryActivity.class)
                                    .putExtra("maxSelection", 5)
                            , Constants.IntentConstant.REQUEST_GALLERY);

                } else {
                    showDialog();
                }
            }
        } else {
            //before marshmallow
            if (permissionType == Constants.IntentConstant.REQUEST_CAMERA) {
//                File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/" + getString(R.string.app_name));
//                if (!myDir.exists()) myDir.mkdir();
//                String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
//                File file = new File(myDir, fileName);
//                outputUri = Uri.fromFile(file);
//                ImageCropper.startCaptureImageActivity(GroupChatActivity.this, REQUEST_CAMERA, CROPPER_REQUEST_CODE);

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.TITLE, "shopoholic_" + System.currentTimeMillis() + ".jpeg");
//                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                takePictureIntent.putExtra("return-data", true);
//                startActivityForResult(takePictureIntent, REQUEST_CAMERA);

                startActivityForResult(new Intent(this, CameraGalleryActivity.class)
                                .putExtra("maxSelection", 5)
                        , Constants.IntentConstant.REQUEST_GALLERY);
            } else {
                showDialog();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE_LOCATION_PERMISSION:
                boolean isRationalGalleryStorage = false;
                for (String permission : permissions) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission) &&
                            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        isRationalGalleryStorage = true;
                    }
                }
                boolean permissionsGranted = true;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted = false;
                    }
                }
                if (grantResults.length > 0 && permissionsGranted) {
                    checkStorageLocationPermission();
                } else if (isRationalGalleryStorage) {
                    AppUtils.getInstance().showToast(this, getString(R.string.enable_storage_permission));
                }
                break;

        }

    }

    /**
     * Show Location dialog
     *
     * @param location
     */
    private void sendLocation(LocationBean location) {
        ChatMessageBean chatMessage = new ChatMessageBean();
        chatMessage.setMessageText(FirebaseConstants.LOCATION);
        chatMessage.setType(FirebaseConstants.LOCATION);
        chatMessage.setMediaUrl(location.getLocationUri());
        chatMessage.setThumbnail("");
        chatMessage.setLatitude(location.getLatitude());
        chatMessage.setLongitude(location.getLongitude());
        createMessage(chatMessage);
    }


    /**
     * Show Location dialog
     *
     * @param location
     */
    private void showLocationDialog(final LocationBean location) {
        final CustomDialogForShareLocation dialog = new CustomDialogForShareLocation(this, location.getLocationUri(), new LocationDialogCallback() {

            @Override
            public void onShareLocation(String locationUri) {
                ChatMessageBean chatMessage = new ChatMessageBean();
                chatMessage.setMessageText(FirebaseConstants.LOCATION);
                chatMessage.setType(FirebaseConstants.LOCATION);
                chatMessage.setMediaUrl(locationUri);
                chatMessage.setThumbnail("");
                chatMessage.setLatitude(location.getLatitude());
                chatMessage.setLongitude(location.getLongitude());
                createMessage(chatMessage);
            }

            @Override
            public void onCancel() {

            }
        });
        dialog.show();
    }

    /**
     * Show Image picker dialog at bottom
     */
    private void showDialog() {
        final CustomDialogForFiles dialog = new CustomDialogForFiles(this, new FilePickerDialogCallback() {
            @Override
            public void onFilesSelection() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
//                intent.setType("text/plain");
                startActivityForResult(intent, REQUEST_FILE);

            }

            @Override
            public void onPhotosSelection() {
                Intent intent = new Intent(GroupChatActivity.this, ImagesGallery.class);
                intent.putExtra("selectedList", new ArrayList<String>());
                // Set the title
                intent.putExtra("title", getString(R.string.select_image));
                intent.putExtra("maxSelection", maxImageSelect); // Optional
                startActivityForResult(intent, MULTIPLE_IMAGE_INTENT);
            }

            @Override
            public void onVideosSelection() {
                Intent intent = new Intent(GroupChatActivity.this, VideosGallery.class);
                intent.putExtra("selectedList", new ArrayList<String>());
                // Set the title
                intent.putExtra("title", getString(R.string.select_video));
                intent.putExtra("maxSelection", maxVideoSelect); // Optional
                startActivityForResult(intent, MULTIPLE_VIDEO_INTENT);
            }

            @Override
            public void onLocationSelection() {
                if (!openPlacePicker) {
                    try {
                        openPlacePicker = true;
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(GroupChatActivity.this), Constants.IntentConstant.REQUEST_PLACE_PICKER);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        openPlacePicker = false;
                        e.printStackTrace();
                    }
                }
            }

        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (groupDialog != null && groupDialog.isShowing()) {
            groupDialog.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            openPlacePicker = false;
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUEST_CAMERA) {
//                    ImageCropper.activity(ImageCropper.getCapturedImageURI()).setGuidelines(CropImageView.Guidelines.OFF).
//                            setCropShape(CropImageView.CropShape.RECTANGLE).setBorderLineColor(Color.WHITE).setBorderCornerColor(Color.TRANSPARENT)
//                            .setAspectRatio(80, 80).setBorderLineThickness(5)
//                            .setOutputUri(outputUri).setActionbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                            .setAutoZoomEnabled(true).start(this);

                    if (mCapturedImageURI != null && !mCapturedImageURI.toString().equals("")) {
                        createMediaMessage(mCapturedImageURI, FirebaseConstants.IMAGE, null, "");
                    } else {
                        AppUtils.getInstance().showToast(this, getString(R.string.file_not_found));
                    }
                } else if (requestCode == CROPPER_REQUEST_CODE) {
//                    Bitmap thumbnailImage = FirebaseChatUtils.getInstance().compressBitmap(BitmapFactory.decodeFile(
//                            FirebaseChatUtils.getInstance().getImagePathFromUri(GroupChatActivity.this, outputUri), new BitmapFactory.Options()));
//                    Log.d("Size : ", String.valueOf(thumbnailImage.getByteCount()));
//                    createMediaMessage(outputUri, FirebaseConstants.IMAGE, thumbnailImage, "");
                    createMediaMessage(outputUri, FirebaseConstants.IMAGE, null, "");

                } else if (requestCode == MULTIPLE_IMAGE_INTENT && data != null) {
                    if (data.getStringArrayListExtra("result") != null) {
                        ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                        for (String filePath : selectionResult) {
//                            Bitmap thumbnailImage = FirebaseChatUtils.getInstance().compressBitmap(BitmapFactory.decodeFile(filePath, new BitmapFactory.Options()));
//                            Log.d("Size : ", String.valueOf(thumbnailImage.getByteCount()));
//                            createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.IMAGE, thumbnailImage, "");
                            createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.IMAGE, null, "");
                        }
                    }
                } else if (requestCode == Constants.IntentConstant.REQUEST_GALLERY && data != null && data.getExtras() != null) {
                    ArrayList<String> selectionResult = data.getExtras().getStringArrayList("result");
                    if (selectionResult != null) {
                        for (String filePath : selectionResult) {
                            createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.IMAGE, null, "");
                        }
                    }
                } else if (requestCode == MULTIPLE_VIDEO_INTENT && data != null) {
                    if (data.getStringArrayListExtra("result") != null) {
                        ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
                        for (String filePath : selectionResult) {
                            Bitmap thumbnailImage = FirebaseChatUtils.getInstance().compressBitmap(ThumbnailUtils.createVideoThumbnail
                                    (filePath, MediaStore.Images.Thumbnails.MINI_KIND));
                            Log.d("Size : ", String.valueOf(thumbnailImage.getByteCount()));
                            createMediaMessage(Uri.fromFile(new File(filePath)), FirebaseConstants.VIDEO, thumbnailImage, "");
                        }
                    }
                } else if (requestCode == REQUEST_FILE && data != null) {
                    try {
                        Uri pdfUri = data.getData();
                        if (pdfUri != null) {
                            if (pdfUri.toString().startsWith("content://com.google") || pdfUri.toString().startsWith("content://com.dropbox")) {
                                AppUtils.getInstance().showToast(this, getString(R.string.sharing_from_cloud_not_supported));
                            } else {
                                createMediaMessage(pdfUri, FirebaseConstants.PDF, null, "");
                            }
                        }
                    } catch (Exception e) {
                        AppUtils.getInstance().showToast(this, getString(R.string.uable_to_select_pdf_please_try_again_later));
                    }

                } else if (requestCode == Constants.IntentConstant.REQUEST_PLACE_PICKER) {
                    Place place = PlacePicker.getPlace(this, data);
                    getCurrentLocation(place.getLatLng());
                }
            }
        }
    }

    /**
     * method to create local media message
     *
     * @param outputUri
     * @param fileType
     * @param thumbnailImage
     */
    private void createMediaMessage(final Uri outputUri, String fileType, Bitmap thumbnailImage, String duration) {
        Uri thumbnailUri = null;
        if (thumbnailImage != null)
            thumbnailUri = FirebaseChatUtils.getInstance().getImageUri(this, thumbnailImage);
        final String messageId = FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).push().getKey();
        final ChatMessageBean chatMessage = new ChatMessageBean();
        chatMessage.setMessageId(messageId);
        chatMessage.setMessageText(fileType);
        chatMessage.setType(fileType);
        chatMessage.setMediaUrl(FirebaseChatUtils.getInstance().getImagePathFromUri(this, outputUri));
        chatMessage.setThumbnail(thumbnailUri == null ? "" : FirebaseChatUtils.getInstance().getImagePathFromUri(this, thumbnailUri));
        chatMessage.setTimestamp(Calendar.getInstance().getTimeInMillis());
        chatMessage.setStatus(FirebaseConstants.PENDING);
        chatMessage.setIsBlock(false);
        chatMessage.setReceiverId(roomId);
        chatMessage.setSenderId(loginUserId);
        chatMessage.setRoomId(roomId);
        setNewChatMessage(chatMessage);
        AppDatabase.saveMediaFilesDetailsInDb(chatMessage);

        startUpload(chatMessage);
    }


    /**
     * Method to add new member in group
     *
     * @param request
     */
    private void openGroupDialog(String request) {
        groupDialog = new CustomDialogForCreateGroup(this, request, usersList, chatRoom, new GroupCallback() {
            @Override
            public void onCreateGroup(String roomId) {
            }

            @Override
            public void onAddNewMember(List<UserBean> userBeans) {
                FirebaseDatabaseQueries.getInstance().addNewMembers(GroupChatActivity.this, roomId, userBeans);
                usersList.addAll(userBeans);
            }

            @Override
            public void onCancel() {
            }
        });
        groupDialog.show();
    }

    /**
     * dialog to leave user
     */
    private void showLeaveDialog() {
        new AlertDialog.Builder(this, R.style.DatePickerTheme).setTitle(R.string.leave_group)
                .setMessage(R.string.are_you_sure_to_leave_group)
                .setPositiveButton(R.string.leave, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabaseQueries.getInstance().leaveGroup(GroupChatActivity.this, roomId);
                        if (roomId != null) {
                            removeAllListeners();
                            tvLeaveStatus.setVisibility(View.VISIBLE);
                            tvTyping.setVisibility(View.GONE);
                            leaveGroup = true;
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();

    }

    /**
     * method to updates the views
     */
    private void updateViews() {
        if (layoutManager.findFirstVisibleItemPosition() == 0) {
            rvChat.smoothScrollToPosition(0);
        }
        if (chatMessagesList.size() == 0) {
//            layoutNoDataFound.setVisibility(View.VISIBLE);
            FirebaseDatabaseQueries.getInstance().deleteRoom(roomId, FirebaseConstants.GROUP_CHAT, "", productId);
        } else {
            layoutNoDataFound.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        removeAllListeners();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        removeAllListeners();
        super.onDestroy();
    }

    /**
     * method used to remove all event listeners
     */
    private void removeAllListeners() {
        if (roomId != null) {
            FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId)
                    .child(FirebaseConstants.CHAT_ROOM_IS_TYPING).child(loginUserId).setValue(false);
            if (messageListener != null && memberDetails != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.MESSAGES_NODE).child(roomId).removeEventListener((ChildEventListener) messageListener);
            }
            if (typingListener != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_IS_TYPING)
                        .removeEventListener((ValueEventListener) typingListener);
            }
            if (memberStatusListener != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_MEMBERS_NODE)
                        .removeEventListener((ChildEventListener) memberStatusListener);
            }
            if (roomTitleListener != null) {
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_TITLE_NODE)
                        .removeEventListener((ValueEventListener) roomTitleListener);
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).child(FirebaseConstants.CHAT_ROOM_PIC_NODE)
                        .removeEventListener((ValueEventListener) roomTitleListener);
            }
        }
        if (usersListener != null && usersList != null && usersList.size() > 0) {
            for (UserBean user : usersList)
                FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(user.getUserId()).addChildEventListener(usersListener);
        }
    }


    /**
     * upload file in S3
     *
     * @param chatMessage
     */
    private void startUpload(final ChatMessageBean chatMessage) {
        AmazonS3 mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(this);
        if (!chatMessage.getThumbnail().equals("")) {
            ImageBean bean = addDataInBean(chatMessage.getThumbnail());
            mAmazonS3.setCallback(new AmazonCallback() {
                @Override
                public void uploadSuccess(ImageBean bean) {
                    String filePath = bean.getServerUrl();
                    chatMessage.setThumbnail(filePath);
                    Uri outputUri = Uri.fromFile(new File(chatMessage.getMediaUrl()));
                    uploadMediaToS3(chatMessage, outputUri);
                }

                @Override
                public void uploadFailed(ImageBean bean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void uploadProgress(ImageBean bean) {
                    AppUtils.getInstance().printLogMessage(FirebaseConstants.TAG, "Uploaded " + bean.getProgress() + " %");
                }

                @Override
                public void uploadError(Exception e, ImageBean imageBean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }
            });
            mAmazonS3.uploadImage(bean);
        } else {
            if (chatMessage.getMediaUrl() != null) {
                Uri outputUri = Uri.fromFile(new File(chatMessage.getMediaUrl()));
                uploadMediaToS3(chatMessage, outputUri);
            } else {
                FirebaseChatUtils.getInstance().showToast(GroupChatActivity.this, getString(R.string.file_not_found_for_send));
            }
        }
    }

    /**
     * methos to upload media file to s3
     *
     * @param chatMessage
     * @param outputUri
     */
    private void uploadMediaToS3(final ChatMessageBean chatMessage, Uri outputUri) {
        AmazonS3 mAmazonS3 = new AmazonS3();
        mAmazonS3.setVariables(Constants.UrlConstant.AMAZON_POOLID, Constants.UrlConstant.BUCKET, Constants.UrlConstant.AMAZON_SERVER_URL, Constants.UrlConstant.END_POINT, Constants.UrlConstant.REGION);
        mAmazonS3.setActivity(this);
        if (chatMessage.getMediaUrl() != null) {
            ImageBean bean = addDataInBean(chatMessage.getMediaUrl());
            bean.setFileType(chatMessage.getType().equals(FirebaseConstants.VIDEO) ? 2 : chatMessage.getType().equals(FirebaseConstants.FILE) ? 3 : 1);
            mAmazonS3.setCallback(new AmazonCallback() {
                @Override
                public void uploadSuccess(ImageBean bean) {
                    String filePath = bean.getServerUrl();
                    chatMessage.setMediaUrl(filePath);
                    chatMessage.setStatus(FirebaseConstants.SEND);
                    createMessage(chatMessage);
                }

                @Override
                public void uploadFailed(ImageBean bean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }

                @Override
                public void uploadProgress(ImageBean bean) {
                    AppUtils.getInstance().printLogMessage(FirebaseConstants.TAG, "Uploaded " + bean.getProgress() + " %");
                }

                @Override
                public void uploadError(Exception e, ImageBean imageBean) {
                    chatMessage.setStatus(FirebaseConstants.FAILED);
                    chatListAdapter.notifyDataSetChanged();
                }
            });
            mAmazonS3.uploadImage(bean);
        } else {
            FirebaseChatUtils.getInstance().showToast(GroupChatActivity.this, getString(R.string.file_not_found_for_send));
        }
    }

    /**
     * create image bean object
     *
     * @param path
     * @return
     */
    private ImageBean addDataInBean(String path) {
        ImageBean bean = new ImageBean();
        bean.setId("1");
        bean.setName("sample");
        bean.setImagePath(path);
        return bean;
    }
}
