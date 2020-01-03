package com.shopoholic.firebasechat.adapters;

/**
 * Created by appinventiv-pc on 15/3/18.
 */

public class LocationBean {
    private double latitude;
    private double longitude;
    private String locationUri;

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

    public String getLocationUri() {
        return locationUri;
    }

    public void setLocationUri(String locationUri) {
        this.locationUri = locationUri;
    }
}
