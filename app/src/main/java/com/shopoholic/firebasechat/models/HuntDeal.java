package com.shopoholic.firebasechat.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.shopoholic.models.producthuntlistresponse.Result;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.models.productservicedetailsresponse.SlotSelectedDate;
import com.shopoholic.models.productservicedetailsresponse.TaxArr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class HuntDeal implements Serializable {
    private String id = "";
    private String huntTitle = "";
    private String categoryName = "";
    private String subCategoryName = "";
    private String userId = "";
    private String productType = "";
//    private String firstName = "";
//    private String lastName = "";
    private String huntImage = "";
    private String userType = "";
    private String isRecursive = "";
    private String currencyCode = "";
    private String currencySymbol = "";
    private String dealStartTime = "";
    private String dealEndTime = "";
    private String price = "";
    private String address = "";
    private String latitude = "";
    private String longitude = "";
    private List<ServiceSlot> serviceSlot = null;
    private List<SlotSelectedDate> slotSelectedDate = null;
    private String selectedSlots;
    private String slotDates;
    private ArrayList<TaxArr> taxArr = null;
    @Exclude
    private ArrayList<String> selectedDates;
    @Exclude
    private String taxes;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId == null ? "" : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

//    public String getFirstName() {
//        return firstName;
//    }

//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }

//    public String getLastName() {
//        return lastName;
//    }

//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }

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

    public String getIsRecursive() {
        return isRecursive;
    }

    public void setIsRecursive(String isRecursive) {
        this.isRecursive = isRecursive;
    }

    public String getCurrencyCode() {
        return currencyCode == null ? "" : currencyCode;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<ServiceSlot> getServiceSlot() {
        return serviceSlot;
    }

    public void setServiceSlot(List<ServiceSlot> serviceSlot) {
        this.serviceSlot = serviceSlot;
    }

    public List<SlotSelectedDate> getSlotSelectedDate() {
        return slotSelectedDate;
    }

    public void setSlotSelectedDate(List<SlotSelectedDate> slotSelectedDate) {
        this.slotSelectedDate = slotSelectedDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
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


    public String getSelectedSlots() {
        return selectedSlots == null ? "" : selectedSlots;
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

    public String getSlotDates() {
        return slotDates;
    }

    public void setSlotDates(String slotDates) {
        this.slotDates = slotDates;
    }


    public ArrayList<TaxArr> getTaxArr() {
        return taxArr == null ? new ArrayList<>() : taxArr;
    }

    public void setTaxArr(ArrayList<TaxArr> taxArr) {
        this.taxArr = taxArr;
    }

    public String getTaxes() {
        return taxes;
    }

    public void setTaxes(String taxes) {
        this.taxes = taxes;
    }

    public String getHuntTitle() {
        return huntTitle;
    }

    public void setHuntTitle(String huntTitle) {
        this.huntTitle = huntTitle;
    }

    /**
     * get hunt deal
     * @param hunt
     * @return
     */
    public HuntDeal getHuntDeal(Result hunt) {
        HuntDeal huntDeal = new HuntDeal();
        huntDeal.setId(hunt.getId());
        huntDeal.setHuntTitle(hunt.getHuntTitle());
        huntDeal.setCategoryName(hunt.getCategoryName());
        huntDeal.setSubCategoryName(hunt.getSubCatName());
        huntDeal.setAddress(hunt.getAddress());
        huntDeal.setProductType(hunt.getProductType());
        huntDeal.setLatitude(hunt.getLatitude());
        huntDeal.setLongitude(hunt.getLongitude());
        huntDeal.setCurrencySymbol(hunt.getCurrencySymbol());
        huntDeal.setPrice(hunt.getPrice());
        huntDeal.setHuntImage(hunt.getHuntImage());
        huntDeal.setUserType(hunt.getUserType());
        huntDeal.setUserId(hunt.getBuddyId());
        huntDeal.setCurrencyCode(hunt.getCurrencyCode());
        huntDeal.setCurrencySymbol(hunt.getCurrencySymbol());
        huntDeal.setIsRecursive(hunt.getIsRecursive());
        huntDeal.setServiceSlot(hunt.getServiceSlot());
        huntDeal.setSlotSelectedDate(hunt.getSlotSelectedDate());
        huntDeal.setSelectedSlots(hunt.getSelectedSlots());
        huntDeal.setSelectedDates(hunt.getSelectedDates());
        huntDeal.setTaxArr(hunt.getTaxArr());
        return huntDeal;
    }
}
