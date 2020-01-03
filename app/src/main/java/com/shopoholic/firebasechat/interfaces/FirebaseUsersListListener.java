package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.UserBean;

import java.util.List;

/**
 * interface to get user from firebase
 */

public interface FirebaseUsersListListener {
    void getUsersList(List<UserBean> usersList);
}
