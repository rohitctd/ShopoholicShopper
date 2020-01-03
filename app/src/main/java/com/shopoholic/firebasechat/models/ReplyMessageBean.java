package com.shopoholic.firebasechat.models;

/**
 * Created by appinventiv-pc on 24/3/18.
 */

public class ReplyMessageBean {
    private String messageId;
    private String messgeText;
    private String mediaUrl;
    private String messageType;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessgeText() {
        return messgeText;
    }

    public void setMessgeText(String messgeText) {
        this.messgeText = messgeText;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
