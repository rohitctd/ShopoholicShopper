package com.shopoholic.firebasechat.models;

import com.shopoholic.models.productservicedetailsresponse.TaxArr;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class created by Sachin on 28-Apr-18.
 */
public class ProductBean implements Serializable{
    private String id;
    private String quantity;
    private String currency;
    private String name;
    private String price;
    private String image;
    private String originalPrice;
    private String discount;
    private String dealStartTime;
    private String dealEndTime;
    private String homeDelivery;
    private String paymentMode;
    private String userType;
    private String buddyId;
    private String productType;
    private String slotId;
    private String deliveryCharges;
    private String merchantFirstName;
    private String merchantLastName;
    private String slotDates;
    private ArrayList<TaxArr> taxArr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDealStartTime() {
        return dealStartTime;
    }

    public void setDealStartTime(String dealStartTime) {
        this.dealStartTime = dealStartTime;
    }

    public String getDealEndTime() {
        return dealEndTime;
    }

    public void setDealEndTime(String dealEndTime) {
        this.dealEndTime = dealEndTime;
    }

    public String getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(String homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getMerchantFirstName() {
        return merchantFirstName;
    }

    public void setMerchantFirstName(String merchantFirstName) {
        this.merchantFirstName = merchantFirstName;
    }

    public String getMerchantLastName() {
        return merchantLastName;
    }

    public void setMerchantLastName(String merchantLastName) {
        this.merchantLastName = merchantLastName;
    }

    public String getSlotDates() {
        return slotDates;
    }

    public void setSlotDates(String slotDates) {
        this.slotDates = slotDates;
    }

    public ArrayList<TaxArr> getTaxArr() {
        return taxArr;
    }

    public void setTaxArr(ArrayList<TaxArr> taxArr) {
        this.taxArr = taxArr;
    }
}
