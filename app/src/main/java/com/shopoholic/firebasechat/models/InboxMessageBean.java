package com.shopoholic.firebasechat.models;


/**
 * Created by Sachin on 22-May-17.
 */

public class InboxMessageBean {
    private String roomId = "";
    private int unreadCount = 0;
    private ChatRoomBean chatRoomBean;
    private UserBean userBean;
    private ChatMessageBean chatLastMessageBean;
    private boolean isSeen;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public ChatRoomBean getChatRoomBean() {
        return chatRoomBean;
    }

    public void setChatRoomBean(ChatRoomBean chatRoomBean) {
        this.chatRoomBean = chatRoomBean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public ChatMessageBean getChatLastMessageBean() {
        return chatLastMessageBean;
    }

    public void setChatLastMessageBean(ChatMessageBean chatLastMessageBean) {
        this.chatLastMessageBean = chatLastMessageBean;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
