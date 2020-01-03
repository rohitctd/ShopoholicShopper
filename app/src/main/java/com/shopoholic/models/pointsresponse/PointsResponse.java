
package com.shopoholic.models.pointsresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholic.models.walletresponse.WalletDetail;

import java.util.List;

public class PointsResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("walletDetail")
    @Expose
    private List<WalletDetail> walletDetail = null;
    @SerializedName("next")
    @Expose
    private Integer next;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<WalletDetail> getWalletDetail() {
        return walletDetail;
    }

    public void setWalletDetail(List<WalletDetail> walletDetail) {
        this.walletDetail = walletDetail;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

}
