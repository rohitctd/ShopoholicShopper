
package com.shopoholic.models.preferredcategorymodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Result implements Serializable {

    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("light_image")
    @Expose
    private String lightImage;
    @SerializedName("dark_image")
    @Expose
    private String darkImage;
    @SerializedName("category_type")
    @Expose
    private String categoryType;

    private boolean isSelected;

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getLightImage() {
        return lightImage;
    }

    public void setLightImage(String lightImage) {
        this.lightImage = lightImage;
    }

    public String getDarkImage() {
        return darkImage;
    }

    public void setDarkImage(String darkImage) {
        this.darkImage = darkImage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }
}
