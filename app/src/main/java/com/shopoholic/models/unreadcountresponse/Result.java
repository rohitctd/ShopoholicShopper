
package com.shopoholic.models.unreadcountresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("unread_request_count")
    @Expose
    private Integer unreadRequestCount;
    @SerializedName("unread_hunt_count")
    @Expose
    private Integer unreadHuntCount;

    public Integer getUnreadRequestCount() {
        return unreadRequestCount == null ? 0 : unreadRequestCount;
    }

    public void setUnreadRequestCount(Integer unreadRequestCount) {
        this.unreadRequestCount = unreadRequestCount;
    }

    public Integer getUnreadHuntCount() {
        return unreadHuntCount == null ? 0 : unreadHuntCount;
    }

    public void setUnreadHuntCount(Integer unreadHuntCount) {
        this.unreadHuntCount = unreadHuntCount;
    }
}
