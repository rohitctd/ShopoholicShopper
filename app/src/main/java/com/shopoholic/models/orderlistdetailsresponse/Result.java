
package com.shopoholic.models.orderlistdetailsresponse;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;

public class Result implements Serializable{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("hunt_id")
    @Expose
    private String huntId;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("actual_amount")
    @Expose
    private String actualAmount;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("commission")
    @Expose
    private String commission;
    @SerializedName("dilevered_by")
    @Expose
    private String dileveredBy;
    @SerializedName("shipping_id")
    @Expose
    private String shippingId;
    @SerializedName("dilevery_address")
    @Expose
    private String dileveryAddress;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("buddy_id")
    @Expose
    private String buddyId;
    @SerializedName("assign_date")
    @Expose
    private String assignDate;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("order_confirm_date")
    @Expose
    private String orderConfirmDate;
    @SerializedName("shipped_date")
    @Expose
    private String shippedDate;
    @SerializedName("out_for_delivery_date")
    @Expose
    private String outForDeliveryDate;
    @SerializedName("dilevery_date")
    @Expose
    private String dileveryDate;
    @SerializedName("dilevered_date")
    @Expose
    private String dileveredDate;
    @SerializedName("slot_id")
    @Expose
    private String slotId;
    @SerializedName("is_track_start")
    @Expose
    private String isTrackStart;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("is_refunded")
    @Expose
    private String isRefunded;
    @SerializedName("is_shared")
    @Expose
    private String isShared;
    @SerializedName("is_share_deal")
    @Expose
    private String isShareDeal;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("deal_name")
    @Expose
    private String dealName;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("shopper_name")
    @Expose
    private String shopperName;
    @SerializedName("shopper_latitude")
    @Expose
    private String shopperLatitude;
    @SerializedName("shopper_longitude")
    @Expose
    private String shopperLongitude;
    @SerializedName("merchant_id")
    @Expose
    private String merchantId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("buddy_first_name")
    @Expose
    private String buddyFirstName;
    @SerializedName("buddy_last_name")
    @Expose
    private String buddyLastName;
    @SerializedName("buddy_address")
    @Expose
    private String buddyAddress;
    @SerializedName("buddy_image")
    @Expose
    private String buddyImage;
    @SerializedName("merchant_name")
    @Expose
    private String merchantName;
    @SerializedName("merchant_address")
    @Expose
    private String merchantAddress;
    @SerializedName("merchant_latitude")
    @Expose
    private String merchantLatitude;
    @SerializedName("merchant_longitude")
    @Expose
    private String merchantLongitude;
    @SerializedName("merchant_number")
    @Expose
    private String merchantNumber;
    @SerializedName("buddy_number")
    @Expose
    private String buddyNumber;
    @SerializedName("buddy_country_id")
    @Expose
    private String buddyCountryId;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("deal_image")
    @Expose
    private String dealImage;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("is_rated")
    @Expose
    private String isRated;
    @SerializedName("home_delivery")
    @Expose
    private String homeDelivery;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("payment_type")
    @Expose
    private String paymentType;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("shopper_delivery_date")
    @Expose
    private String shopperDeliveryDate;
    @SerializedName("shopper_delivery_charge")
    @Expose
    private String shopperDeliveryCharge;

    @SerializedName("toll_free_number")
    @Expose
    private String tollFreeNumber;
    @SerializedName("slot_arr")
    @Expose
    private List<ServiceSlot> slotArr = null;
    @SerializedName("selected_date_arr")
    @Expose
    private List<String> selectedDateArr = null;

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

    public String getHuntId() {
        return huntId;
    }

    public void setHuntId(String huntId) {
        this.huntId = huntId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getDileveredBy() {
        return dileveredBy;
    }

    public void setDileveredBy(String dileveredBy) {
        this.dileveredBy = dileveredBy;
    }

    public String getShippingId() {
        return shippingId;
    }

    public void setShippingId(String shippingId) {
        this.shippingId = shippingId;
    }

    public String getDileveryAddress() {
        return dileveryAddress;
    }

    public void setDileveryAddress(String dileveryAddress) {
        this.dileveryAddress = dileveryAddress;
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

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderConfirmDate() {
        return orderConfirmDate;
    }

    public void setOrderConfirmDate(String orderConfirmDate) {
        this.orderConfirmDate = orderConfirmDate;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getOutForDeliveryDate() {
        return outForDeliveryDate;
    }

    public void setOutForDeliveryDate(String outForDeliveryDate) {
        this.outForDeliveryDate = outForDeliveryDate;
    }

    public String getDileveryDate() {
        return dileveryDate;
    }

    public void setDileveryDate(String dileveryDate) {
        this.dileveryDate = dileveryDate;
    }

    public String getDileveredDate() {
        return dileveredDate;
    }

    public void setDileveredDate(String dileveredDate) {
        this.dileveredDate = dileveredDate;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getIsTrackStart() {
        return isTrackStart;
    }

    public void setIsTrackStart(String isTrackStart) {
        this.isTrackStart = isTrackStart;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getIsRefunded() {
        return isRefunded;
    }

    public void setIsRefunded(String isRefunded) {
        this.isRefunded = isRefunded;
    }

    public String getIsShareDeal() {
        return isShareDeal;
    }

    public void setIsShareDeal(String isShareDeal) {
        this.isShareDeal = isShareDeal;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getShopperName() {
        return shopperName;
    }

    public void setShopperName(String shopperName) {
        this.shopperName = shopperName;
    }

    public String getShopperLatitude() {
        return shopperLatitude;
    }

    public void setShopperLatitude(String shopperLatitude) {
        this.shopperLatitude = shopperLatitude;
    }

    public String getShopperLongitude() {
        return shopperLongitude;
    }

    public void setShopperLongitude(String shopperLongitude) {
        this.shopperLongitude = shopperLongitude;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    public String getBuddyFirstName() {
        return buddyFirstName;
    }

    public void setBuddyFirstName(String buddyFirstName) {
        this.buddyFirstName = buddyFirstName;
    }

    public String getBuddyLastName() {
        return buddyLastName;
    }

    public void setBuddyLastName(String buddyLastName) {
        this.buddyLastName = buddyLastName;
    }

    public String getBuddyImage() {
        return buddyImage;
    }

    public void setBuddyImage(String buddyImage) {
        this.buddyImage = buddyImage;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantAddress() {
        return merchantAddress;
    }

    public void setMerchantAddress(String merchantAddress) {
        this.merchantAddress = merchantAddress;
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

    public String getBuddyNumber() {
        return buddyNumber;
    }

    public void setBuddyNumber(String buddyNumber) {
        this.buddyNumber = buddyNumber;
    }

    public String getBuddyCountryId() {
        return buddyCountryId;
    }

    public void setBuddyCountryId(String buddyCountryId) {
        this.buddyCountryId = buddyCountryId;
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

    public String getIsRated() {
        return isRated;
    }

    public void setIsRated(String isRated) {
        this.isRated = isRated;
    }

    public String getHomeDelivery() {
        return homeDelivery;
    }

    public void setHomeDelivery(String homeDelivery) {
        this.homeDelivery = homeDelivery;
    }

    public List<ServiceSlot> getSlotArr() {
        return slotArr;
    }

    public void setSlotArr(List<ServiceSlot> slotArr) {
        this.slotArr = slotArr;
    }

    public String getIsShared() {
        return isShared;
    }

    public void setIsShared(String isShared) {
        this.isShared = isShared;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode == null || currencyCode.equals("") ? "AED" : currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol == null ? "" : currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public List<String> getSelectedDateArr() {
        return selectedDateArr;
    }

    public void setSelectedDateArr(List<String> selectedDateArr) {
        this.selectedDateArr = selectedDateArr;
    }

    public String getBuddyAddress() {
        return buddyAddress == null ? "" : buddyAddress;
    }

    public void setBuddyAddress(String buddyAddress) {
        this.buddyAddress = buddyAddress;
    }

    public String getPaymentType() {
        return paymentType == null ? "" : paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getShopperDeliveryDate() {
        return shopperDeliveryDate;
    }

    public void setShopperDeliveryDate(String shopperDeliveryDate) {
        this.shopperDeliveryDate = shopperDeliveryDate;
    }

    public String getShopperDeliveryCharge() {
        return shopperDeliveryCharge;
    }

    public void setShopperDeliveryCharge(String shopperDeliveryCharge) {
        this.shopperDeliveryCharge = shopperDeliveryCharge;
    }

    public String getTollFreeNumber() {
        return tollFreeNumber == null ? "" : tollFreeNumber;
    }

    public void setTollFreeNumber(String tollFreeNumber) {
        this.tollFreeNumber = tollFreeNumber;
    }

    public String getProductType() {
        return productType == null ? "1" : productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
