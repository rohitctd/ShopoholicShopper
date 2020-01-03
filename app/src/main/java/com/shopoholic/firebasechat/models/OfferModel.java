package com.shopoholic.firebasechat.models;


public class OfferModel {
    private String status; //0-Reject, 1-Accept, 2-Request, 3-Counter
//    private String message;
//    private String currency;
    private String price;
    private long timestamp;

    public OfferModel() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public String getMessage() {
//        return message;
//    }

//    public void setMessage(String message) {
//        this.message = message;
//    }

//    public String getCurrency() {
//        return currency;
//    }

//    public void setCurrency(String currency) {
//        this.currency = currency;
//    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
