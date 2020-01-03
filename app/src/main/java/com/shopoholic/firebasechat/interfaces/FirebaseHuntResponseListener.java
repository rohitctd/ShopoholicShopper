package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.HuntDeal;

/**
 * Created by appinventiv-pc on 10/3/18.
 */

public interface FirebaseHuntResponseListener {
    void getHuntDetails(HuntDeal huntData);
}
