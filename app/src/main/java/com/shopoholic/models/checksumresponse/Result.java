
package com.shopoholic.models.checksumresponse;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result implements Parcelable
{

    @SerializedName("paytm_merchant_id")
    @Expose
    private String paytmMerchantId;
    @SerializedName("checksum")
    @Expose
    private String checksum;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("actual_amount")
    @Expose
    private String actualAmount;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    public final static Creator<Result> CREATOR = new Creator<Result>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return (new Result[size]);
        }

    }
    ;

    protected Result(Parcel in) {
        this.paytmMerchantId = ((String) in.readValue((String.class.getClassLoader())));
        this.checksum = ((String) in.readValue((String.class.getClassLoader())));
        this.orderNumber = ((String) in.readValue((String.class.getClassLoader())));
        this.actualAmount = ((String) in.readValue((String.class.getClassLoader())));
        this.customerId = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Result() {
    }

    public String getPaytmMerchantId() {
        return paytmMerchantId == null ? "" : paytmMerchantId;
    }

    public void setPaytmMerchantId(String paytmMerchantId) {
        this.paytmMerchantId = paytmMerchantId;
    }

    public String getChecksum() {
        return checksum == null ? "" : checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getOrderNumber() {
        return orderNumber == null ? "" : orderNumber;
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

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(paytmMerchantId);
        dest.writeValue(checksum);
        dest.writeValue(orderNumber);
        dest.writeValue(actualAmount);
        dest.writeValue(customerId);
    }

    public int describeContents() {
        return  0;
    }

}
