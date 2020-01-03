
package com.shopoholic.models.countrymodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryBean {

    @SerializedName("CountryID")
    @Expose
    private Integer countryID;
    @SerializedName("CountryEnglishName")
    @Expose
    private String countryEnglishName;
    @SerializedName("CountryLocalName")
    @Expose
    private String countryLocalName;
    @SerializedName("CountryFlag")
    @Expose
    private String countryFlag;
    @SerializedName("ISOCode")
    @Expose
    private String iSOCode;
    @SerializedName("CountryCode")
    @Expose
    private String countryCode;
    @SerializedName("Currency")
    @Expose
    private String currency;
    @SerializedName("CurrencyCode")
    @Expose
    private String currencyCode;

    public Integer getCountryID() {
        return countryID;
    }

    public void setCountryID(Integer countryID) {
        this.countryID = countryID;
    }

    public String getCountryEnglishName() {
        return countryEnglishName;
    }

    public void setCountryEnglishName(String countryEnglishName) {
        this.countryEnglishName = countryEnglishName;
    }

    public String getCountryLocalName() {
        return countryLocalName;
    }

    public void setCountryLocalName(String countryLocalName) {
        this.countryLocalName = countryLocalName;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public String getISOCode() {
        return iSOCode;
    }

    public void setISOCode(String iSOCode) {
        this.iSOCode = iSOCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
