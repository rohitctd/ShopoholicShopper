
package com.shopoholic.models.purchaseorderresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Result implements Serializable{

    @SerializedName("dilevered_by")
    @Expose
    private String dileveredBy;
    @SerializedName("dilevery_address")
    @Expose
    private String dileveryAddress;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("deal_image")
    @Expose
    private String dealImage;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("dilevered_date")
    @Expose
    private String dileveredDate;
    @SerializedName("service_name")
    @Expose
    private String serviceName;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("buddy_id")
    @Expose
    private String buddyId;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("deal_name")
    @Expose
    private String dealName;
    @SerializedName("dilevery_date")
    @Expose
    private String dileveryDate;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("assign_date")
    @Expose
    private String assignDate;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("actual_amount")
    @Expose
    private String actualAmount;

    public String getDileveredBy() {
        return dileveredBy;
    }

    public void setDileveredBy(String dileveredBy) {
        this.dileveredBy = dileveredBy;
    }

    public String getDileveryAddress() {
        return dileveryAddress;
    }

    public void setDileveryAddress(String dileveryAddress) {
        this.dileveryAddress = dileveryAddress;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDealImage() {
        return dealImage;
    }

    public void setDealImage(String dealImage) {
        this.dealImage = dealImage;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDileveredDate() {
        return dileveredDate;
    }

    public void setDileveredDate(String dileveredDate) {
        this.dileveredDate = dileveredDate;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getBuddyId() {
        return buddyId;
    }

    public void setBuddyId(String buddyId) {
        this.buddyId = buddyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDealName() {
        return dealName == null ? "" : dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getDileveryDate() {
        return dileveryDate;
    }

    public void setDileveryDate(String dileveryDate) {
        this.dileveryDate = dileveryDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

}
