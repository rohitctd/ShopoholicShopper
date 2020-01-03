
package com.shopoholic.models.producthuntlistresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.models.productservicedetailsresponse.SlotSelectedDate;
import com.shopoholic.models.productservicedetailsresponse.TaxArr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Result implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("subcat_id")
    @Expose
    private String subcatId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("hunt_title")
    @Expose
    private String huntTitle;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("expected_delivery_date")
    @Expose
    private String expectedDeliveryDate;
    @SerializedName("target_area")
    @Expose
    private String targetArea;
    @SerializedName("target_lat")
    @Expose
    private String targetLat;
    @SerializedName("target_long")
    @Expose
    private String targetLong;
    @SerializedName("choose_buddy")
    @Expose
    private String chooseBuddy;
    @SerializedName("price_start")
    @Expose
    private String priceStart;
    @SerializedName("price_end")
    @Expose
    private String priceEnd;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("country_id")
    @Expose
    private String countryId;
    @SerializedName("hunt_image")
    @Expose
    private String huntImage;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("sub_cat_name")
    @Expose
    private String subCatName;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("buddy_accept_count")
    @Expose
    private String buddyAcceptCount;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("buddy_id")
    @Expose
    private String buddyId;
    @SerializedName("hunt_start_date")
    @Expose
    private String huntStartDate;
    @SerializedName("hunt_end_date")
    @Expose
    private String huntEndDate;
    @SerializedName("is_recursive")
    @Expose
    private String isRecursive;
    @SerializedName("is_read")
    @Expose
    private String isRead;
    @SerializedName("notification_action")
    @Expose
    private String notificationAction;
    @SerializedName("selected_date_arr")
    @Expose
    private ArrayList<SlotSelectedDate> slotSelectedDate = null;
    @SerializedName("slot_arr")
    @Expose
    private ArrayList<ServiceSlot> serviceSlot = null;

    private ArrayList<String> imagesList;

    @SerializedName("selected_slots")
    @Expose
    private String selectedSlots;
    @SerializedName("selected_dates")
    @Expose
    private ArrayList<String> selectedDates;
    @SerializedName("tax_arr")
    @Expose
    private ArrayList<TaxArr> taxArr = null;

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

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(String expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public String getTargetArea() {
        return targetArea == null ? "" : targetArea;
    }

    public void setTargetArea(String targetArea) {
        this.targetArea = targetArea;
    }

    public String getTargetLat() {
        return targetLat == null ? "" : targetLat;
    }

    public void setTargetLat(String targetLat) {
        this.targetLat = targetLat;
    }

    public String getTargetLong() {
        return targetLong == null ? "" : targetLong;
    }

    public void setTargetLong(String targetLong) {
        this.targetLong = targetLong;
    }

    public String getChooseBuddy() {
        return chooseBuddy == null ? "" : chooseBuddy;
    }

    public void setChooseBuddy(String chooseBuddy) {
        this.chooseBuddy = chooseBuddy;
    }

    public String getPriceStart() {
        return priceStart;
    }

    public void setPriceStart(String priceStart) {
        this.priceStart = priceStart;
    }

    public String getPriceEnd() {
        return priceEnd;
    }

    public void setPriceEnd(String priceEnd) {
        this.priceEnd = priceEnd;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getHuntImage() {
        return huntImage;
    }

    public void setHuntImage(String huntImage) {
        this.huntImage = huntImage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBuddyAcceptCount() {
        return buddyAcceptCount;
    }

    public void setBuddyAcceptCount(String buddyAcceptCount) {
        this.buddyAcceptCount = buddyAcceptCount;
    }

    public ArrayList<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBuddyId() {
        return buddyId == null ? "" : buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getHuntStartDate() {
        return huntStartDate;
    }

    public void setHuntStartDate(String huntStartDate) {
        this.huntStartDate = huntStartDate;
    }

    public String getHuntEndDate() {
        return huntEndDate;
    }

    public void setHuntEndDate(String huntEndDate) {
        this.huntEndDate = huntEndDate;
    }

    public String getIsRecursive() {
        return isRecursive;
    }

    public void setIsRecursive(String isRecursive) {
        this.isRecursive = isRecursive;
    }

    public ArrayList<SlotSelectedDate> getSlotSelectedDate() {
        return slotSelectedDate;
    }

    public void setSlotSelectedDate(ArrayList<SlotSelectedDate> slotSelectedDate) {
        this.slotSelectedDate = slotSelectedDate;
    }

    public ArrayList<ServiceSlot> getServiceSlot() {
        return serviceSlot;
    }

    public void setServiceSlot(ArrayList<ServiceSlot> serviceSlot) {
        this.serviceSlot = serviceSlot;
    }

    public String getSelectedSlots() {
        return selectedSlots;
    }

    public void setSelectedSlots(String selectedSlots) {
        this.selectedSlots = selectedSlots;
    }

    public ArrayList<String> getSelectedDates() {
        return selectedDates;
    }

    public void setSelectedDates(ArrayList<String> selectedDates) {
        this.selectedDates = selectedDates;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public ArrayList<TaxArr> getTaxArr() {
        return taxArr;
    }

    public void setTaxArr(ArrayList<TaxArr> taxArr) {
        this.taxArr = taxArr;
    }

    public String getHuntTitle() {
        return huntTitle == null ? "" : huntTitle;
    }

    public void setHuntTitle(String huntTitle) {
        this.huntTitle = huntTitle;
    }

    public void setNotificationAction(String notificationAction) {
        this.notificationAction = notificationAction;
    }

    public String getNotificationAction() {
        return notificationAction;
    }
}
