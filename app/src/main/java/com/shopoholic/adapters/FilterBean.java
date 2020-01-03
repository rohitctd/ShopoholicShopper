package com.shopoholic.adapters;


import com.shopoholic.models.preferredcategorymodel.Result;
import com.shopoholic.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class created by Sachin on 25-Apr-18.
 */
public class FilterBean implements Serializable {
    private int count = 0;
    private String address;
    private int range = 20;
    private int maxRange = 20;
    private double latitude;
    private double longitude;
    private ArrayList<com.shopoholic.models.storelistresponse.Result> storeDetail;
    private Result categoryDetails;
    private com.shopoholic.models.subcategoryresponse.Result subCategoryDetails;
    private int discountPercentage = -1;
    private String expireDate;
    private int typeOfDeals = 1;
    private int postedBy = 0;
    private int type = 0;

    public String getAddress() {
        return address==null?"":address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getExpireDate() {
        return expireDate==null?"":expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public int getTypeOfDeals() {
        return typeOfDeals;
    }

    public void setTypeOfDeals(int typeOfDeals) {
        this.typeOfDeals = typeOfDeals;
    }

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    public Result getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(Result categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public com.shopoholic.models.subcategoryresponse.Result getSubCategoryDetails() {
        return subCategoryDetails;
    }

    public void setSubCategoryDetails(com.shopoholic.models.subcategoryresponse.Result subCategoryDetails) {
        this.subCategoryDetails = subCategoryDetails;
    }

    public ArrayList<com.shopoholic.models.storelistresponse.Result> getStoreDetail() {
        return storeDetail;
    }

    public void setStoreDetail(ArrayList<com.shopoholic.models.storelistresponse.Result> storeDetail) {
        this.storeDetail = storeDetail;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
