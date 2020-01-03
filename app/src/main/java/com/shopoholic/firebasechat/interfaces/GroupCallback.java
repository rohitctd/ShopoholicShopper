package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.UserBean;

import java.util.List;

/**
 * Interface handel group callback
 */

public interface GroupCallback {
    void onCreateGroup(String roomId);
    void onAddNewMember(List<UserBean> userBeans);
    void onCancel();
}
