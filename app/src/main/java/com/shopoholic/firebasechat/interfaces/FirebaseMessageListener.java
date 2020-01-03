package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.ChatMessageBean;

import java.util.List;

/**
 * Created by appinventiv-pc on 10/3/18.
 */

public interface FirebaseMessageListener {
    void getMessages(ChatMessageBean message);
    void getMessagesList(List<ChatMessageBean> messagesList);
}
