package com.shopoholic.firebasechat.models;

import java.io.Serializable;

/**
 * Created by appinventiv-pc on 6/3/18.
 */

public class UserBean implements Serializable{
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String userImage;
    private String deviceToken;
    private String deviceType;
    private String countryCode;
    private String mobileNumber;
    private boolean onlineStatus;

    private boolean isSelected = false;

    public UserBean() {
    }

    public String getUserId() {
        return userId == null ? "" : userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName == null ? "" : firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName == null ? "" : lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImage() {
        return userImage == null ? "" : userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getDeviceToken() {
        return deviceToken == null ? "" : deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCountryCode() {
        return countryCode == null ? "" : countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobileNumber() {
        return mobileNumber == null ? "" : mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getDeviceType() {
        return deviceType == null ? "1" : deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
