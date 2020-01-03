
package com.shopoholic.models.notificationresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("notification_type")
    @Expose
    private String notificationType;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("hunt_id")
    @Expose
    private String huntId;
    @SerializedName("req_id")
    @Expose
    private String reqId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("deal_name")
    @Expose
    private String dealName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("sender_name")
    @Expose
    private String senderName;
    @SerializedName("receiver_name")
    @Expose
    private String receiverName;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("notification_action")
    @Expose
    private String notificationAction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getHuntId() {
        return huntId;
    }

    public void setHuntId(String huntId) {
        this.huntId = huntId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNotificationAction() {
        return notificationAction;
    }

    public void setNotificationAction(String notificationAction) {
        this.notificationAction = notificationAction;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }
}
