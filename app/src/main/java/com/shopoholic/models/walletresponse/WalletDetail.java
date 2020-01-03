
package com.shopoholic.models.walletresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletDetail {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("amount")
    @Expose
    private String points;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
