package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.UserBean;

/**
 * interface to get user from firebase
 */

public interface FirebaseUserListener {
    void getUser(UserBean user);
    void updateUser(UserBean user);
}
