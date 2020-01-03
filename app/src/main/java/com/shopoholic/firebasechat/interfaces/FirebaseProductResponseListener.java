package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.ChatRoomBean;
import com.shopoholic.firebasechat.models.ProductBean;

/**
 * Created by appinventiv-pc on 10/3/18.
 */

public interface FirebaseProductResponseListener {
    void getProductDetails(ProductBean productBean);
}
