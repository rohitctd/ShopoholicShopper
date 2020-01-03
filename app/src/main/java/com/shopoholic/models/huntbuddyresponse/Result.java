
package com.shopoholic.models.huntbuddyresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholic.models.productservicedetailsresponse.ServiceSlot;
import com.shopoholic.models.productservicedetailsresponse.SlotSelectedDate;
import com.shopoholic.models.productservicedetailsresponse.TaxArr;

import java.util.ArrayList;

public class Result {

    @SerializedName("buddy_id")
    @Expose
    private String id;
    @SerializedName("bid_price")
    @Expose
    private String bidPrice;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("hunt_image")
    @Expose
    private String huntImage;
    @SerializedName("is_assigned")
    @Expose
    private String isAssigned;
    @SerializedName("buddy_delivery_date")
    @Expose
    private String buddyDeliveryDate;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("selected_date_arr")
    @Expose
    private ArrayList<SlotSelectedDate> slotSelectedDate = null;
    @SerializedName("slot_arr")
    @Expose
    private ArrayList<ServiceSlot> serviceSlot = null;
    @SerializedName("tax_arr")
    @Expose
    private ArrayList<TaxArr> taxArr = null;

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
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

    public String getHuntImage() {
        return huntImage;
    }

    public void setHuntImage(String huntImage) {
        this.huntImage = huntImage;
    }

    public String getIsAssigned() {
        return isAssigned;
    }

    public void setIsAssigned(String isAssigned) {
        this.isAssigned = isAssigned;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuddyDeliveryDate() {
        return buddyDeliveryDate;
    }

    public void setBuddyDeliveryDate(String buddyDeliveryDate) {
        this.buddyDeliveryDate = buddyDeliveryDate;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
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

    public ArrayList<TaxArr> getTaxArr() {
        return taxArr;
    }

    public void setTaxArr(ArrayList<TaxArr> taxArr) {
        this.taxArr = taxArr;
    }
}
