
package com.shopoholic.models.huntbuddyresponse;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopoholic.models.productservicedetailsresponse.TaxArr;

public class HuntBuddyResponse {

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("result")
    @Expose
    private List<Result> result = null;
    @SerializedName("next")
    @Expose
    private Integer next;
    @SerializedName("tax_arr")
    @Expose
    private ArrayList<TaxArr> taxArr = null;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    public ArrayList<TaxArr> getTaxArr() {
        return taxArr;
    }

    public void setTaxArr(ArrayList<TaxArr> taxArr) {
        this.taxArr = taxArr;
    }
}
