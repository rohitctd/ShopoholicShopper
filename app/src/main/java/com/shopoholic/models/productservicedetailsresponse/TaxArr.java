package com.shopoholic.models.productservicedetailsresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TaxArr implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("tax_name")
    @Expose
    private String taxName;
    @SerializedName("tax_currency")
    @Expose
    private String taxCurrency;
    @SerializedName("tax_percentage")
    @Expose
    private String taxPercentage;
    @SerializedName("update_date")
    @Expose
    private String updateDate;
/*    @SerializedName("create_date")
    @Expose
    private String createDate;*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getTaxCurrency() {
        return taxCurrency;
    }

    public void setTaxCurrency(String taxCurrency) {
        this.taxCurrency = taxCurrency;
    }

    public String getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(String taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
/*
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }*/
}
