
package com.shopoholic.models.walletresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("result")
    @Expose
    private List<Result_> result = null;
    @SerializedName("total_record")
    @Expose
    private String totalRecord;

    public List<Result_> getResult() {
        return result;
    }

    public void setResult(List<Result_> result) {
        this.result = result;
    }

    public String getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(String totalRecord) {
        this.totalRecord = totalRecord;
    }

}
