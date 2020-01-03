package com.shopoholic.models;

import java.io.Serializable;

/**
 * Class created by Sachin on 12-May-18.
 */
public class OrderBean implements Serializable{
    private int count = 0;
    private String status = "";
    private String startDate = "";
    private String endDate = "";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
