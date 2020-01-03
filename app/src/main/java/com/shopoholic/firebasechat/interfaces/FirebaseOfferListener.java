package com.shopoholic.firebasechat.interfaces;

import com.shopoholic.firebasechat.models.ChatMessageBean;
import com.shopoholic.firebasechat.models.OfferModel;

import java.util.List;

/**
 * Created by appinventiv-pc on 10/3/18.
 */

public interface FirebaseOfferListener {
    void getOffer(OfferModel offer);
}
