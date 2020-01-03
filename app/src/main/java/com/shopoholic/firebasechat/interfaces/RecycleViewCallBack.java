package com.shopoholic.firebasechat.interfaces;

import android.view.View;

/**
 * Created by appinventiv-pc on 8/3/18.
 */

public interface RecycleViewCallBack {
    void onClick(int position, View clickedView);
    void onLongClick(int position, View clickedView);
}
