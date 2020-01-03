
package com.shopoholic.models.blockdealresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cat_id")
    @Expose
    private String catId;
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
    @SerializedName("dilevery_charge")
    @Expose
    private String dileveryCharge;
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
    @SerializedName("deal_url")
    @Expose
    private String dealUrl;
    @SerializedName("discount_validate_start")
    @Expose
    private String discountValidateStart;
    @SerializedName("discount_validate_end")
    @Expose
    private String discountValidateEnd;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("merchant_address")
    @Expose
    private String merchantAddress;
    @SerializedName("merchant_address2")
    @Expose
    private String merchantAddress2;
    @SerializedName("merchant_latitude")
    @Expose
    private String merchantLatitude;
    @SerializedName("merchant_longitude")
    @Expose
    private String merchantLongitude;
    @SerializedName("merchant_number")
    @Expose
    private String merchantNumber;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("deal_image")
    @Expose
    private String dealImage;
    @SerializedName("user_type")
    @Expose
    private String userType;

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

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getDileveryCharge() {
        return dileveryCharge;
    }

    public void setDileveryCharge(String dileveryCharge) {
        this.dileveryCharge = dileveryCharge;
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

    public String getDealUrl() {
        return dealUrl;
    }

    public void setDealUrl(String dealUrl) {
        this.dealUrl = dealUrl;
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

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
    }

    public String getMerchantAddress2() {
        return merchantAddress2;
    }

    public void setMerchantAddress2(String merchantAddress2) {
        this.merchantAddress2 = merchantAddress2;
    }

    public String getMerchantLatitude() {
        return merchantLatitude;
    }

    public void setMerchantLatitude(String merchantLatitude) {
        this.merchantLatitude = merchantLatitude;
    }

    public String getMerchantLongitude() {
        return merchantLongitude;
    }

    public void setMerchantLongitude(String merchantLongitude) {
        this.merchantLongitude = merchantLongitude;
    }

    public String getMerchantNumber() {
        return merchantNumber;
    }

    public void setMerchantNumber(String merchantNumber) {
        this.merchantNumber = merchantNumber;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
