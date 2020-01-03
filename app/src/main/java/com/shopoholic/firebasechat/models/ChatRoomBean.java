package com.shopoholic.firebasechat.models;

import com.shopoholic.models.producthuntlistresponse.Result;
import com.shopoholic.models.productservicedetailsresponse.TaxArr;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class created by Sachin on 22-Apr-18.
 */
public class ChatRoomBean {
    private String chatRoomId;
    private String chatRoomType;
    private String chatRoomTitle;
    private String chatRoomPic;
    private Object chatLastUpdate;
    private HashMap<String, Boolean> chatRoomIsTyping;
    private HashMap<String, Object> chatLastUpdates;
    private HashMap<String, Object> chatRoomMembers;
    private ProductBean product;
    private HuntDeal huntDeal;
    private String productId;
    private String huntId;
    private String buddyId;
    private String slotId;
    private String price;
    private String slotDates;
    private String isShared;
    private ArrayList<TaxArr> taxArr = null;

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getChatRoomType() {
        return chatRoomType;
    }

    public void setChatRoomType(String chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    public String getChatRoomTitle() {
        return chatRoomTitle;
    }

    public void setChatRoomTitle(String chatRoomTitle) {
        this.chatRoomTitle = chatRoomTitle;
    }

    public String getChatRoomPic() {
        return chatRoomPic;
    }

    public void setChatRoomPic(String chatRoomPic) {
        this.chatRoomPic = chatRoomPic;
    }

    public Object getChatLastUpdate() {
        return chatLastUpdate;
    }

    public void setChatLastUpdate(Object chatLastUpdate) {
        this.chatLastUpdate = chatLastUpdate;
    }

    public HashMap<String, Boolean> getChatRoomIsTyping() {
        return chatRoomIsTyping;
    }

    public void setChatRoomIsTyping(HashMap<String, Boolean> chatRoomIsTyping) {
        this.chatRoomIsTyping = chatRoomIsTyping;
    }

    public HashMap<String, Object> getChatLastUpdates() {
        return chatLastUpdates;
    }

    public void setChatLastUpdates(HashMap<String, Object> chatLastUpdates) {
        this.chatLastUpdates = chatLastUpdates;
    }

    public HashMap<String, Object> getChatRoomMembers() {
        return chatRoomMembers;
    }

    public void setChatRoomMembers(HashMap<String, Object> chatRoomMembers) {
        this.chatRoomMembers = chatRoomMembers;
    }

    public ProductBean getProduct() {
        return product;
    }

    public void setProduct(ProductBean product) {
        this.product = product;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getPrice() {
        return price == null || price.equals("") ? "0" : price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public HuntDeal getHuntDeal() {
        return huntDeal;
    }

    public void setHuntDeal(HuntDeal huntDeal) {
        this.huntDeal = huntDeal;
    }

    public String getHuntId() {
        return huntId;
    }

    public void setHuntId(String huntId) {
        this.huntId = huntId;
    }

    public String getSlotDates() {
        return slotDates;
    }

    public void setSlotDates(String slotDates) {
        this.slotDates = slotDates;
    }

    public String getIsShared() {
        return isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }

    public ArrayList<TaxArr> getTaxArr() {
        return taxArr;
    }

    public void setTaxArr(ArrayList<TaxArr> taxArr) {
        this.taxArr = taxArr;
    }
}
