package com.cameraandgallery.interfaces;

/**
 * Created by appinventiv-pc on 20/12/17.
 */

import android.view.View;

/**
 * this interface is used to get the callback from the adapter
 */
public interface RecyclerCallBack {
    void onClick(int position, View view);
}

