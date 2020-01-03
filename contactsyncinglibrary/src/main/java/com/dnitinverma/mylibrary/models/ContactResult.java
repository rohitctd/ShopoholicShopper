
package com.dnitinverma.mylibrary.models;


import java.io.Serializable;

public class ContactResult implements Serializable{

    private String countryCode="";
    private String phoneNumber="";
    private String email="";
    private String contactId="";
    private String contactMainId="";
    private String contactRawId="";
    private String profileImage="''";
    private String businessAddress="";
    private String customerId="";
    private String rowId="";
    private String contactName="";
    private String PhoneNumberWithCode="";
    private String isAppUser="0";
    private String isSynchedWithServer="0";
    private int contactSrNumber=1;
    private int contactType=1;//0 for header and 1 for contact and 3 or default for progressbar
    private int isPhoneContact=1;//1 for phone contact and 0 for non phone contact

    public ContactResult(int contactType,String contactName) {
        this.contactType = contactType;
        this.contactName=contactName;
    }

    public String getContactMainId() {
        return contactMainId;
    }

    public void setContactMainId(String contactMainId) {
        this.contactMainId = contactMainId;
    }

    public String getContactRawId() {
        return contactRawId;
    }

    public void setContactRawId(String contactRawId) {
        this.contactRawId = contactRawId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public int getContactType() {
        return contactType;
    }

    public void setContactType(int contactType) {
        this.contactType = contactType;
    }

    public int getIsPhoneContact() {
        return isPhoneContact;
    }

    public void setIsPhoneContact(int isPhoneContact) {
        this.isPhoneContact = isPhoneContact;
    }

    public ContactResult(String contactName) {
        this.contactName = contactName;
    }

    public ContactResult() {
    }

    public int getContactSrNumber() {
        return contactSrNumber;
    }

    public void setContactSrNumber(int contactSrNumber) {
        this.contactSrNumber = contactSrNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumberWithCode() {
        return PhoneNumberWithCode;
    }

    public void setPhoneNumberWithCode(String phoneNumberWithCode) {
        PhoneNumberWithCode = phoneNumberWithCode;
    }

    public String getIsAppUser() {
        return isAppUser;
    }

    public void setIsAppUser(String isAppUser) {
        this.isAppUser = isAppUser;
    }

    public String getIsSynchedWithServer() {
        return isSynchedWithServer;
    }

    public void setIsSynchedWithServer(String isSynchedWithServer) {
        this.isSynchedWithServer = isSynchedWithServer;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getRowId() {
        return rowId;
    }
}
