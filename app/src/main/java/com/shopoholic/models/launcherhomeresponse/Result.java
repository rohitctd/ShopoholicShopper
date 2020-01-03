
package com.shopoholic.models.launcherhomeresponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("banner_arr")
    @Expose
    private List<BannerArr> bannerArr = null;
    @SerializedName("popular_deals")
    @Expose
    private List<com.shopoholic.models.productdealsresponse.Result> popularDeals = null;

    public List<BannerArr> getBannerArr() {
        return bannerArr;
    }

    public void setBannerArr(List<BannerArr> bannerArr) {
        this.bannerArr = bannerArr;
    }

    public List<com.shopoholic.models.productdealsresponse.Result> getPopularDeals() {
        return popularDeals;
    }

    public void setPopularDeals(List<com.shopoholic.models.productdealsresponse.Result> popularDeals) {
        this.popularDeals = popularDeals;
    }

}
