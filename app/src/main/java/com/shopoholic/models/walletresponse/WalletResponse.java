
package com.shopoholic.models.walletresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private Boolean msg;
    @SerializedName("total_balance")
    @Expose
    private String totalBalance;
    @SerializedName("result")
    @Expose
    private List<Result_> result;
    @SerializedName("wallet_Detail")
    @Expose
    private List<WalletDetail> walletDetail;
    @SerializedName("next")
    @Expose
    private Integer next;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Boolean getMsg() {
        return msg;
    }

    public void setMsg(Boolean msg) {
        this.msg = msg;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public List<Result_> getResult() {
        return result;
    }

    public void setResult(List<Result_> result) {
        this.result = result;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public List<WalletDetail> getWalletDetail() {
        return walletDetail;
    }

    public void setWalletDetail(List<WalletDetail> walletDetail) {
        this.walletDetail = walletDetail;
    }
}
