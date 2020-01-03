
package com.shopoholic.models.bannerlistresponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("banner_arr")
    @Expose
    private List<BannerArr> bannerArr = null;

    public List<BannerArr> getBannerArr() {
        return bannerArr;
    }

    public void setBannerArr(List<BannerArr> bannerArr) {
        this.bannerArr = bannerArr;
    }

}
