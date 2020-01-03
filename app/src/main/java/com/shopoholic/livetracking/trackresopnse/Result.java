
package com.shopoholic.livetracking.trackresopnse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("Blat")
    @Expose
    private Double blat;
    @SerializedName("Blong")
    @Expose
    private Double blong;
    @SerializedName("Bsocketid")
    @Expose
    private String bsocketid;
    @SerializedName("U_user_id")
    @Expose
    private Integer uUserId;
    @SerializedName("order_id")
    @Expose
    private Integer orderId;

    public Double getBlat() {
        return blat;
    }

    public void setBlat(Double blat) {
        this.blat = blat;
    }

    public Double getBlong() {
        return blong;
    }

    public void setBlong(Double blong) {
        this.blong = blong;
    }

    public String getBsocketid() {
        return bsocketid;
    }

    public void setBsocketid(String bsocketid) {
        this.bsocketid = bsocketid;
    }

    public Integer getUUserId() {
        return uUserId;
    }

    public void setUUserId(Integer uUserId) {
        this.uUserId = uUserId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

}
