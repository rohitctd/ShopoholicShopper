package com.shopoholic.models;

import com.shopoholic.models.preferredcategorymodel.Result;

import java.io.Serializable;
import java.util.ArrayList;

public class AddProductServiceModel implements Serializable{

    private boolean isProduct;
    private Result category;
    private ArrayList<String> imagesList;
    private com.shopoholic.models.subcategoryresponse.Result subCategory;


    private String id;
    private String currency;
    private String dealName;
    private String productServiceName;
    private String dealDetailStartTiming;
    private String dealDetailEndTiming;
    private String description;
    private String originalPrice;
    private String validityStartDate;
    private String validityEndDate;
    private String dealPostingDate;
    private String priceRange;
    private String sellingPrice;
    private String address;
    private String latitude;
    private String longitude;
    private String paymentMode;

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    private String quantity;
    private String deliveryCharges;

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getProductServiceName() {
        return productServiceName;
    }

    public void setProductServiceName(String productServiceName) {
        this.productServiceName = productServiceName;
    }

    public String getDealDetailStartTiming() {
        return dealDetailStartTiming;
    }

    public void setDealDetailStartTiming(String dealDetailStartTiming) {
        this.dealDetailStartTiming = dealDetailStartTiming;
    }

    public String getDealDetailEndTiming() {
        return dealDetailEndTiming;
    }

    public void setDealDetailEndTiming(String dealDetailEndTiming) {
        this.dealDetailEndTiming = dealDetailEndTiming;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(String validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public String getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(String validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    public String getDealPostingDate() {
        return dealPostingDate;
    }

    public void setDealPostingDate(String dealPostingDate) {
        this.dealPostingDate = dealPostingDate;
    }

    public Result getCategory() {
        return category == null ? new Result() : category;
    }

    public void setCategory(Result category) {
        this.category = category;
    }

    public com.shopoholic.models.subcategoryresponse.Result getSubCategory() {
        return subCategory == null ? new com.shopoholic.models.subcategoryresponse.Result() : subCategory;
    }

    public void setSubCategory(com.shopoholic.models.subcategoryresponse.Result subCategory) {
        this.subCategory = subCategory;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isProduct() {
        return isProduct;
    }

    public void setProduct(boolean product) {
        isProduct = product;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public ArrayList<String> getImagesList() {
        return imagesList == null ? new ArrayList<String>() : imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
