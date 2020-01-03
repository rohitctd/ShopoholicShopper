
package com.shopoholic.models.productservicedetailsresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ServiceSlot implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("service_id")
    @Expose
    private String serviceId;
    @SerializedName("slot_start_date")
    @Expose
    private String slotStartDate;
    @SerializedName("slot_end_date")
    @Expose
    private String slotEndDate;
    @SerializedName("slot_start_time")
    @Expose
    private String slotStartTime;
    @SerializedName("slot_end_time")
    @Expose
    private String slotEndTime;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("is_available")
    @Expose
    private String isAvailable;
    @SerializedName("is_recursive")
    @Expose
    private String isRecursive;
    @SerializedName("all_days")
    @Expose
    private String allDays;
    private boolean isAllDay;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("currency")
    @Expose
    private String currency;

    private boolean isSelected = false;

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

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getSlotStartDate() {
        return slotStartDate;
    }

    public void setSlotStartDate(String slotStartDate) {
        this.slotStartDate = slotStartDate;
    }

    public String getSlotEndDate() {
        return slotEndDate;
    }

    public void setSlotEndDate(String slotEndDate) {
        this.slotEndDate = slotEndDate;
    }

    public String getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(String slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public String getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(String slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIsRecursive() {
        return isRecursive;
    }

    public void setIsRecursive(String isRecursive) {
        this.isRecursive = isRecursive;
    }

    public String getAllDays() {
        return allDays;
    }

    public void setAllDays(String allDays) {
        this.allDays = allDays;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
