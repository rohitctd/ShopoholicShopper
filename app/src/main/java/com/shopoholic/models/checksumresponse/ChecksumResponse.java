
package com.shopoholic.models.checksumresponse;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChecksumResponse implements Parcelable
{

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("result")
    @Expose
    private Result result;
    public final static Creator<ChecksumResponse> CREATOR = new Creator<ChecksumResponse>() {


        @SuppressWarnings({
            "unchecked"
        })
        public ChecksumResponse createFromParcel(Parcel in) {
            return new ChecksumResponse(in);
        }

        public ChecksumResponse[] newArray(int size) {
            return (new ChecksumResponse[size]);
        }

    }
    ;

    protected ChecksumResponse(Parcel in) {
        this.code = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.msg = ((String) in.readValue((String.class.getClassLoader())));
        this.result = ((Result) in.readValue((Result.class.getClassLoader())));
    }

    public ChecksumResponse() {
    }

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(code);
        dest.writeValue(msg);
        dest.writeValue(result);
    }

    public int describeContents() {
        return  0;
    }

}
