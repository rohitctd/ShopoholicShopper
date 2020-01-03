
package com.shopoholic.models.productdealsresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Result implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("subcat_id")
    @Expose
    private String subcatId;
    @SerializedName("custom_attribute")
    @Expose
    private String customAttribute;
    @SerializedName("orignal_price")
    @Expose
    private String orignalPrice;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("availability")
    @Expose
    private String availability;
    @SerializedName("deal_store")
    @Expose
    private String dealStore;
    @SerializedName("home_delivery")
    @Expose
    private String homeDelivery;
    @SerializedName("publish_date")
    @Expose
    private String publishDate;
    @SerializedName("deal_start_time")
    @Expose
    private String dealStartTime;
    @SerializedName("deal_end_time")
    @Expose
    private String dealEndTime;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("is_save_deal")
    @Expose
    private String isSaveDeal;
    @SerializedName("deal_url")
    @Expose
    private String dealUrl;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("discount_validate_start")
    @Expose
    private String discountValidateStart;
    @SerializedName("discount_validate_end")
    @Expose
    private String discountValidateEnd;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("store_name")
    @Expose
    private String storeName;
    @SerializedName("store_description")
    @Expose
    private String storeDescription;
    @SerializedName("store_location")
    @Expose
    private String storeLocation;
    @SerializedName("store_latitude")
    @Expose
    private String storeLatitude;
    @SerializedName("store_longitude")
    @Expose
    private String storeLongitude;
    @SerializedName("store_image")
    @Expose
    private String storeImage;
    @SerializedName("deal_images")
    @Expose
    private String dealImage;
    @SerializedName("is_favourite")
    @Expose
    private String isFavourite;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("is_expired")
    @Expose
    private String isExpire;
    @SerializedName("reshared_count")
    @Expose
    private String resharedCount;
    @SerializedName("buddy_address")
    @Expose
    private String buddyAddress;
    @SerializedName("buddy_latitude")
    @Expose
    private String buddyLatitude;
    @SerializedName("buddy_longitude")
    @Expose
    private String buddyLongitude;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("kyc_status")
    @Expose
    private String kycStatus;

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSubcatId() {
        return subcatId;
    }

    public void setSubcatId(String subcatId) {
        this.subcatId = subcatId;
    }

    public String getCustomAttribute() {
        return customAttribute;
    }

    public void setCustomAttribute(String customAttribute) {
        this.customAttribute = customAttribute;
    }

    public String getOrignalPrice() {
        return orignalPrice;
    }

    public void setOrignalPrice(String orignalPrice) {
        this.orignalPrice = orignalPrice;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getDealStore() {
        return dealStore;
    }

    public void setDealStore(String dealStore) {
        this.dealStore = dealStore;
    }

    public String getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(String homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getIsSaveDeal() {
        return isSaveDeal;
    }

    public void setIsSaveDeal(String isSaveDeal) {
        this.isSaveDeal = isSaveDeal;
    }

    public String getDealUrl() {
        return dealUrl;
    }

    public void setDealUrl(String dealUrl) {
        this.dealUrl = dealUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscountValidateStart() {
        return discountValidateStart;
    }

    public void setDiscountValidateStart(String discountValidateStart) {
        this.discountValidateStart = discountValidateStart;
    }

    public String getDiscountValidateEnd() {
        return discountValidateEnd;
    }

    public void setDiscountValidateEnd(String discountValidateEnd) {
        this.discountValidateEnd = discountValidateEnd;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getStoreLatitude() {
        return storeLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        this.storeLatitude = storeLatitude;
    }

    public String getStoreLongitude() {
        return storeLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        this.storeLongitude = storeLongitude;
    }

    public String getStoreImage() {
        return storeImage;
    }

    public void setStoreImage(String storeImage) {
        this.storeImage = storeImage;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public String getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(String isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIsExpire() {
        return isExpire;
    }

    public void setIsExpire(String isExpire) {
        this.isExpire = isExpire;
    }

    public String getBuddyAddress() {
        return buddyAddress;
    }

    public void setBuddyAddress(String buddyAddress) {
        this.buddyAddress = buddyAddress;
    }

    public String getBuddyLatitude() {
        return buddyLatitude;
    }

    public void setBuddyLatitude(String buddyLatitude) {
        this.buddyLatitude = buddyLatitude;
    }

    public String getBuddyLongitude() {
        return buddyLongitude;
    }

    public void setBuddyLongitude(String buddyLongitude) {
        this.buddyLongitude = buddyLongitude;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getResharedCount() {
        return resharedCount == null ? "" : resharedCount;
    }

    public void setResharedCount(String resharedCount) {
        this.resharedCount = resharedCount;
    }
}
