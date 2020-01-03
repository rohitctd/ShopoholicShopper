package com.shopoholic.firebasechat.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopoholic.R;
import com.shopoholic.activities.HomeActivity;
import com.shopoholic.firebasechat.activities.GroupChatActivity;
import com.shopoholic.firebasechat.activities.SingleChatActivity;
import com.shopoholic.firebasechat.adapters.MessageListAdapter;
import com.shopoholic.firebasechat.interfaces.FirebaseRoomResponseListener;
import com.shopoholic.firebasechat.interfaces.FirebaseUserListener;
import com.shopoholic.firebasechat.interfaces.RecycleViewCallBack;
import com.shopoholic.firebasechat.models.ChatMessageBean;
import com.shopoholic.firebasechat.models.ChatRoomBean;
import com.shopoholic.firebasechat.models.HuntDeal;
import com.shopoholic.firebasechat.models.InboxMessageBean;
import com.shopoholic.firebasechat.models.NotificationBean;
import com.shopoholic.firebasechat.models.ProductBean;
import com.shopoholic.firebasechat.models.UserBean;
import com.shopoholic.firebasechat.utils.FirebaseConstants;
import com.shopoholic.firebasechat.utils.FirebaseDatabaseQueries;
import com.shopoholic.firebasechat.utils.FirebaseEventListeners;
import com.shopoholic.models.productservicedetailsresponse.Result;
import com.shopoholic.utils.AppSharedPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * fragment to show user messages details
 */

public class ChatFragment extends Fragment implements Comparator<InboxMessageBean> {

    @BindView(R.id.rv_message_list)
    RecyclerView rvMessageList;
    @BindView(R.id.tv_no_data)
    AppCompatTextView tvNoData;
    Unbinder unbinder;
    private View rootView;
    private Activity mActivity;
    private List<InboxMessageBean> messagesList;
    private MessageListAdapter messageListAdapter;
    private List<String> allRoomsId;
    private HashMap<String, InboxMessageBean> inboxMessageBeanHashMap;
    private HashMap<String, FirebaseEventListeners> userListener, lastMessageListener, roomInfoListener;
    private FirebaseEventListeners inboxListener;
    private String loginUserId, loginUserName;
    private DatabaseReference firebaseDatabaseRef;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initVariables();
        setListeners();
        getNotificationData();
        getUserMessagesList();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Method to initialize the variables
     */
    private void initVariables() {
        mActivity = getActivity();
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference();
        messagesList = new ArrayList<>();
        allRoomsId = new ArrayList<>();
        inboxMessageBeanHashMap = new HashMap<>();
        userListener = new HashMap<>();
        lastMessageListener = new HashMap<>();
        roomInfoListener = new HashMap<>();
        loginUserId = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.USER_ID);
        loginUserName = AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.FIRST_NAME) +
                AppSharedPreference.getInstance().getString(mActivity, AppSharedPreference.PREF_KEY.LAST_NAME);

        messageListAdapter = new MessageListAdapter(mActivity, messagesList, new RecycleViewCallBack() {
            @Override
            public void onClick(int position, View clickedView) {
                if (position >= 0 && messagesList.size() > 0) {
                    if (messagesList.get(position).getChatRoomBean() != null) {
                        Result product = getProductDetail(messagesList.get(position).getChatRoomBean().getProduct());
                        HuntDeal huntDeal = messagesList.get(position).getChatRoomBean().getHuntDeal();
                        if (messagesList.get(position).getChatRoomBean().getChatRoomType().equals(FirebaseConstants.SINGLE)) {
                            Intent intent = new Intent(mActivity, SingleChatActivity.class);
                            intent.putExtra(FirebaseConstants.OTHER_USER, messagesList.get(position).getUserBean());
                            intent.putExtra(FirebaseConstants.ROOM_ID, messagesList.get(position).getRoomId());
                            intent.putExtra(FirebaseConstants.CHAT_ROOM_PRODUCT, product);
                            intent.putExtra(FirebaseConstants.CHAT_ROOM_HUNT, huntDeal);
                            intent.putExtra(FirebaseConstants.IS_PRODUCT, product != null);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(mActivity, GroupChatActivity.class);
                            intent.putExtra(FirebaseConstants.ROOM_ID, messagesList.get(position).getRoomId());
                            intent.putExtra(FirebaseConstants.CHAT_ROOM_PRODUCT, product);
                            intent.putExtra(FirebaseConstants.CHAT_ROOM_HUNT, huntDeal);
                            intent.putExtra(FirebaseConstants.IS_PRODUCT, product != null);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onLongClick(int position, View clickedView) {
                switch (clickedView.getId()) {
                    case R.id.rl_message_row:
//                        showDeleteDialog(position);
                        break;
                }
            }
        });
    }



    /**
     * method to show the list dialog
     */
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mActivity);
        builderSingle.setIcon(R.drawable.ic_launcher);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.add(getString(R.string.delete_chat));

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            switch (which) {
                case 0:
                    String otherUserId = "";
                    String productId = "";
                    InboxMessageBean messageBean = messagesList.get(position);
                    if (messageBean.getChatRoomBean().getChatRoomType().equals(FirebaseConstants.SINGLE)){
                        otherUserId = messageBean.getUserBean().getUserId();
                    }
                    if (messageBean.getChatRoomBean().getProduct() != null){
                        productId = messageBean.getChatRoomBean().getProduct().getId();
                    }
                    FirebaseDatabaseQueries.getInstance().deleteRoom(messageBean.getRoomId(), messageBean.getChatRoomBean().getChatRoomType(), otherUserId, productId);
                    break;
            }
        });
        builderSingle.show();
    }
    /**
     * method to get the product details
     * @param chatProduct
     * @return
     */
    private Result getProductDetail(ProductBean chatProduct) {
        Result product = null;
        if (chatProduct != null) {
            product = new Result();
            product.setId(chatProduct.getId());
            product.setQuantity(chatProduct.getQuantity());
            product.setCurrency(chatProduct.getCurrency());
            product.setCurrencySymbol(chatProduct.getCurrency());
            product.setName(chatProduct.getName());
            product.setSellingPrice(chatProduct.getPrice());
            product.setDealImages(chatProduct.getImage());
            product.setOrignalPrice(chatProduct.getOriginalPrice());
            product.setDiscount(chatProduct.getDiscount());
            product.setDealStartTime(chatProduct.getDealStartTime());
            product.setDealEndTime(chatProduct.getDealEndTime());
            product.setHomeDelivery(chatProduct.getHomeDelivery());
            product.setPaymentMethod(chatProduct.getPaymentMode());
            product.setUserType(chatProduct.getUserType());
            product.setUserId(chatProduct.getBuddyId());
            product.setProductType(chatProduct.getProductType());
            product.setSelectedSlots(chatProduct.getSlotId());
            product.setDileveryCharge(chatProduct.getDeliveryCharges());
            product.setFirstName(chatProduct.getMerchantFirstName());
            product.setLastName(chatProduct.getMerchantLastName());
        }
        return product;
    }

    /**
     * Method to set listener in views
     */
    private void setListeners() {
        rvMessageList.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        rvMessageList.setAdapter(messageListAdapter);
    }

    /**
     * Method to get data on notification click
     */
    private void getNotificationData() {
        if (mActivity instanceof HomeActivity) {
            Intent receivedIntent = ((HomeActivity) mActivity).getIntent();
            if (receivedIntent != null && receivedIntent.getExtras() != null && receivedIntent.hasExtra(FirebaseConstants.NOTIFICATION)) {
                final NotificationBean notificationBean = (NotificationBean) receivedIntent.getExtras().getSerializable(FirebaseConstants.NOTIFICATION);
                if (notificationBean != null) {
                    FirebaseDatabaseQueries.getInstance().getRoomDetails(notificationBean.getRoomId(), new FirebaseRoomResponseListener() {
                        @Override
                        public void getRoomId(String roomId) {}
                        @Override
                        public void getRoomDetails(final ChatRoomBean chatRoomBean) {
                            if (chatRoomBean.getProductId() != null && !chatRoomBean.getProductId().equals("")) {
                                getProductDetails(chatRoomBean, notificationBean);
                            }else if (chatRoomBean.getHuntId() != null && !chatRoomBean.getHuntId().equals("")) {
                                getHuntDetails(chatRoomBean, notificationBean);
                            }else {
                                setData(null, notificationBean);
                            }
                        }
                    });
                }
                ((HomeActivity) mActivity).setIntent(null);
            }
        }
    }


    /**
     * method to get product details
     * @param chatRoomBean
     * @param notificationBean
     */
    private void getProductDetails(final ChatRoomBean chatRoomBean, final NotificationBean notificationBean) {
        FirebaseDatabaseQueries.getInstance().getProductDetails(chatRoomBean.getProductId(), productBean -> {
            if (productBean.getProductType().equals("2")) productBean.setPrice(chatRoomBean.getPrice());
            chatRoomBean.setProduct(productBean);
            final Result product = getProductDetail(productBean);
            setData(product, notificationBean);
        });
    }


    /**
     * method to get hunt details
     * @param chatRoomBean
     * @param notificationBean
     */
    private void getHuntDetails(final ChatRoomBean chatRoomBean, final NotificationBean notificationBean) {
        FirebaseDatabaseQueries.getInstance().getHuntDetails(chatRoomBean.getHuntId(), huntDeal -> {
            chatRoomBean.setHuntDeal(huntDeal);
            setHuntDeal(huntDeal, notificationBean);
        });
    }

    /**
     * function to set data
     * @param product
     * @param notificationBean
     */
    private void setData(Result product, NotificationBean notificationBean) {
        if (notificationBean.getRoomType().equals(FirebaseConstants.SINGLE)) {
            FirebaseDatabaseQueries.getInstance().getUser(notificationBean.getSenderId(), new FirebaseUserListener() {
                @Override
                public void getUser(UserBean user) {
                    Intent intent = new Intent(mActivity, SingleChatActivity.class)
                            .putExtra(FirebaseConstants.OTHER_USER, user)
                            .putExtra(FirebaseConstants.ROOM_ID, notificationBean.getRoomId())
                            .putExtra(FirebaseConstants.CHAT_ROOM_PRODUCT, product)
                            .putExtra(FirebaseConstants.IS_PRODUCT, true);
                    startActivity(intent);
                }

                @Override
                public void updateUser(UserBean user) {}
            });
        } else {
            Intent intent = new Intent(mActivity, GroupChatActivity.class)
                    .putExtra(FirebaseConstants.ROOM_ID, notificationBean.getRoomId())
                    .putExtra(FirebaseConstants.CHAT_ROOM_PRODUCT, product)
                    .putExtra(FirebaseConstants.IS_PRODUCT, true);
            startActivity(intent);
        }
    }

    /**
     * function to set hunt data
     * @param huntDeal
     * @param notificationBean
     */
    private void setHuntDeal(HuntDeal huntDeal, NotificationBean notificationBean) {
        if (notificationBean.getRoomType().equals(FirebaseConstants.SINGLE) ||
                notificationBean.getRoomType().equals(FirebaseConstants.HUNT) ) {
            FirebaseDatabaseQueries.getInstance().getUser(notificationBean.getSenderId(), new FirebaseUserListener() {
                @Override
                public void getUser(UserBean user) {
                    Intent intent = new Intent(mActivity, SingleChatActivity.class)
                            .putExtra(FirebaseConstants.OTHER_USER, user)
                            .putExtra(FirebaseConstants.ROOM_ID, notificationBean.getRoomId())
                            .putExtra(FirebaseConstants.CHAT_ROOM_HUNT, huntDeal)
                            .putExtra(FirebaseConstants.IS_PRODUCT, false);
                    startActivity(intent);
                }

                @Override
                public void updateUser(UserBean user) {}
            });
        } else {
            Intent intent = new Intent(mActivity, GroupChatActivity.class)
                    .putExtra(FirebaseConstants.ROOM_ID, notificationBean.getRoomId())
                    .putExtra(FirebaseConstants.CHAT_ROOM_HUNT, huntDeal)
                    .putExtra(FirebaseConstants.IS_PRODUCT, false);
            startActivity(intent);
        }
    }

    /**
     * method to get user message list
     */
    private void getUserMessagesList() {
        tvNoData.setVisibility(View.VISIBLE);
        inboxListener = new FirebaseEventListeners() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null) {
                    tvNoData.setVisibility(View.GONE);
                    String otherUserId;
                    if (dataSnapshot.getKey() != dataSnapshot.getValue() && dataSnapshot.getKey().contains("_")){
                        otherUserId = dataSnapshot.getKey().split("_")[0];
                    }else {
                        otherUserId = dataSnapshot.getKey();
                    }
                    InboxMessageBean inboxMessageBean = new InboxMessageBean();
                    inboxMessageBean.setRoomId(dataSnapshot.getValue().toString());
                    inboxMessageBeanHashMap.put(inboxMessageBean.getRoomId(), inboxMessageBean);
                    getRoomData(otherUserId, inboxMessageBeanHashMap, dataSnapshot.getValue().toString());
                    getLastMessage(inboxMessageBeanHashMap, dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (int i=0; i<messagesList.size(); i++) {
                        String roomId = dataSnapshot.getValue().toString();
                        if (messagesList.get(i).getRoomId().equals(roomId)){
                            messagesList.remove(i);
                            inboxMessageBeanHashMap.remove(roomId);
                            messageListAdapter.notifyItemRemoved(i);
                            messageListAdapter.notifyItemRangeChanged(i, messagesList.size());
                        }
                    }
                }
            }

        };
        //call for getting rooms from inbox
        firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(loginUserId).addChildEventListener(inboxListener);
    }

    /**
     * Method to get room data
     *
     * @param otherUserId
     * @param inboxMessageBeanHashMap
     * @param roomId
     */
    private void getRoomData(final String otherUserId, final HashMap<String, InboxMessageBean> inboxMessageBeanHashMap, final String roomId) {
        roomInfoListener.put(roomId, new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatRoomBean chatRoomBean;
                    try {
                        chatRoomBean = dataSnapshot.getValue(ChatRoomBean.class);
                    }catch (Exception e) {
                        chatRoomBean = FirebaseDatabaseQueries.getInstance().getRoomInfo(dataSnapshot);
                    }
//                    ChatRoomBean chatRoomBean = new Gson().fromJson(dataSnapshot.getValue().toString(), ChatRoomBean.class);
                    if (chatRoomBean != null && inboxMessageBeanHashMap.get(roomId) != null) {
                        if (chatRoomBean.getProductId() != null && !chatRoomBean.getProductId().equals("")) {
                            ChatRoomBean finalChatRoomBean = chatRoomBean;
                            FirebaseDatabaseQueries.getInstance().getProductDetails(chatRoomBean.getProductId(), productBean -> {
                                if (productBean.getProductType().equals("2"))
                                    productBean.setPrice(finalChatRoomBean.getPrice());
                                finalChatRoomBean.setProduct(productBean);
                                setMessages(finalChatRoomBean, roomId, otherUserId);
                            });
                        }else if (chatRoomBean.getHuntId() != null && !chatRoomBean.getHuntId().equals("")) {
                            ChatRoomBean finalChatRoomBean = chatRoomBean;
                            FirebaseDatabaseQueries.getInstance().getHuntDetails(chatRoomBean.getHuntId(), huntDeal -> {
                                if (huntDeal.getProductType().equals("2"))
                                    huntDeal.setPrice(finalChatRoomBean.getPrice());
                                finalChatRoomBean.setHuntDeal(huntDeal);
                                setMessages(finalChatRoomBean, roomId, otherUserId);
                            });
                        }else {
                            setMessages(chatRoomBean, roomId, otherUserId);
                        }
                    }
                }
            }

        });
        firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).addValueEventListener(roomInfoListener.get(roomId));
    }

    /**
     * method to set the message in list
     * @param chatRoomBean
     * @param roomId
     * @param otherUserId
     */
    private void setMessages(ChatRoomBean chatRoomBean, String roomId, String otherUserId) {
        inboxMessageBeanHashMap.get(roomId).setChatRoomBean(chatRoomBean);
        if (inboxMessageBeanHashMap.get(roomId).getChatRoomBean().getChatRoomType().equals(FirebaseConstants.SINGLE)) {
            getUserDetails(otherUserId, roomId, inboxMessageBeanHashMap);
        } else {
            updateList();
        }
        long userTimeStamp = Long.parseLong(chatRoomBean.getChatLastUpdates().get(loginUserId).toString());
        long timeStamp = Long.parseLong(chatRoomBean.getChatLastUpdate().toString());
        if (timeStamp <= userTimeStamp) {
            inboxMessageBeanHashMap.get(roomId).setSeen(true);
        } else {
            inboxMessageBeanHashMap.get(roomId).setSeen(false);
        }
        messageListAdapter.notifyDataSetChanged();
    }

    /**
     * method to get the last message
     *
     * @param inboxMessageBeanHashMap
     * @param roomId
     */
    private void getLastMessage(final HashMap<String, InboxMessageBean> inboxMessageBeanHashMap, final String roomId) {
        lastMessageListener.put(roomId, new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    ChatMessageBean message;
                    if (dataSnapshot.child(loginUserId).getValue() != null) {
                        message = dataSnapshot.child(loginUserId).child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).getValue(ChatMessageBean.class);
                    } else {
                        message = dataSnapshot.child(FirebaseConstants.CHAT_LAST_MESSAGE_NODE).getValue(ChatMessageBean.class);
                    }
                    if (inboxMessageBeanHashMap.get(roomId) != null) inboxMessageBeanHashMap.get(roomId).setChatLastMessageBean(message);
                    updateList();

                }
            }
        });
        firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId)
                .addValueEventListener(lastMessageListener.get(roomId));
    }

    /**
     * Method to get the user details
     */
    private void getUserDetails(String userId, final String roomId, final HashMap<String, InboxMessageBean> inboxMessageBeanHashMap) {
        FirebaseDatabase.getInstance().getReference().child(FirebaseConstants.USERS_NODE).child(userId).addListenerForSingleValueEvent(new FirebaseEventListeners() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    UserBean userBean = dataSnapshot.getValue(UserBean.class);
                    if (inboxMessageBeanHashMap.get(roomId) != null) inboxMessageBeanHashMap.get(roomId).setUserBean(userBean);
                    updateList();
                }
            }
        });

    }

    /**
     * Method to update message list
     */
    private void updateList() {
        if (messagesList != null && messageListAdapter != null) {
            messagesList.clear();
            messagesList.addAll(inboxMessageBeanHashMap.values());
            Collections.sort(messagesList, ChatFragment.this);
            int position = messagesList.size() - 1;
            if (position >= 0)
                messageListAdapter.notifyDataSetChanged();

            updateViews();
        }
    }


    @Override
    public int compare(InboxMessageBean lhs, InboxMessageBean rhs) {
        if (lhs.getChatLastMessageBean() != null && rhs.getChatLastMessageBean() != null &&
                lhs.getChatLastMessageBean().getTimestamp() != null && rhs.getChatLastMessageBean().getTimestamp() != null) {
            long lhsTime = (long) lhs.getChatLastMessageBean().getTimestamp();
            long rhsTime = (long) rhs.getChatLastMessageBean().getTimestamp();
            if (lhsTime < rhsTime) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * method to updates the views
     */
    private void updateViews() {
        if (isAdded()) {
            if (messagesList.size() == 0) {
                tvNoData.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroy() {
        removeAllListeners();
        super.onDestroy();
    }

    /**
     * Method to remove all listeners
     */
    private void removeAllListeners() {
        if (inboxListener != null) {
            firebaseDatabaseRef.child(FirebaseConstants.INBOX_NODE).child(loginUserId).removeEventListener((ChildEventListener) inboxListener);
        }
        for (String roomId : allRoomsId) {
//            if (seenListener != null && seenListener.get(roomId) != null && roomId != null) {
//                firebaseDatabaseRef.chil1d(FirebaseConstant.CHATINFO).child(roomId).child(FirebaseConstant.LASTUPDATES)
//                        .removeEventListener(seenListener.get(roomId));
//            }
            if (roomId != null && roomInfoListener != null && roomInfoListener.get(roomId) != null) {
                firebaseDatabaseRef.child(FirebaseConstants.ROOM_INFO_NODE).child(roomId).removeEventListener((ValueEventListener) roomInfoListener.get(roomId));
            }
//            if (titleListener != null && titleListener.get(roomId) != null && roomId != null) {
//                firebaseDatabaseRef.child("rooms").child(roomId).removeEventListener(titleListener.get(roomId));
//            }
//            if (chatTitleListener != null && chatTitleListener.get(roomId) != null && roomId != null) {
//                firebaseDatabaseRef.child(FirebaseConstant.CHATINFO).child(roomId).child(FirebaseConstant.CHATTITLE)
//                        .removeEventListener(chatTitleListener.get(roomId));
//            }
            if (roomId != null && lastMessageListener != null && lastMessageListener.get(roomId) != null) {
                firebaseDatabaseRef.child(FirebaseConstants.LAST_MESSAGE_NODE).child(roomId).removeEventListener((ValueEventListener) lastMessageListener.get(roomId));
            }
            if (roomId != null && userListener != null && userListener.get(roomId) != null && inboxMessageBeanHashMap.get(roomId) != null && inboxMessageBeanHashMap.get(roomId).getUserBean() != null) {
                firebaseDatabaseRef.child(FirebaseConstants.USERS_NODE).child(inboxMessageBeanHashMap.get(roomId).getUserBean().getUserId()).removeEventListener((ValueEventListener) userListener.get(roomId));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
