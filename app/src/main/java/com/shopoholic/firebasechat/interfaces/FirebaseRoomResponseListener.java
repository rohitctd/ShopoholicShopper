package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.ChatRoomBean;

/**
 * Created by appinventiv-pc on 10/3/18.
 */

public interface FirebaseRoomResponseListener {
    void getRoomId(String roomId);
    void getRoomDetails(ChatRoomBean chatRoomBean);
}
