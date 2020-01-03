package com.shopoholic.interfaces;

/**
 * Created by pardeep on 14/12/16.
 */

/**
 * this interface is used to get the callback from the dialog
 */
public interface MakeOfferDialogCallback {
    void onSelect(String message, String currency, String text, int type);
}
