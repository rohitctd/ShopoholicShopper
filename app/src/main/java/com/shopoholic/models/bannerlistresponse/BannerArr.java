
package com.shopoholic.models.bannerlistresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerArr {

    @SerializedName("banner_id")
    @Expose
    private String bannerId;
    @SerializedName("banner_name")
    @Expose
    private String bannerName;
    @SerializedName("banner_image")
    @Expose
    private String bannerImage;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("product_type")
    @Expose
    private String productType;
    @SerializedName("percentage")
    @Expose
    private String percentage;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("deal_name")
    @Expose
    private String dealName;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

}
