package com.shopoholic.interfaces;

/**
 * Created by pardeep on 14/12/16.
 */

import com.shopoholic.models.productdealsresponse.Result;

/**
 * this interface is used to get the callback from the dialog
 */
public interface LocationDialogCallback {
    void onClick(double lati, double longi, String dealId);
}
