package com.housemaid.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fluper on 20/7/18.
 */

public class ChattingModel {

    private String message;

    private String receiverName;

    private String senderName;

    private String timeStamp ;

    private String senderID ;

    private String receiverID ;

    private String deleteStatus ;

    private String receiverProfileImage ;

    private String senderProfileImage ;

    private String userIdentifierReceiver ;

    private String userIdentifierSender ;

    public ChattingModel(String message, String receiverName, String senderName, String timeStamp, String senderID, String receiverID, String deleteStatus, String receiverProfileImage, String senderProfileImage, String userIdentifierReceiver, String userIdentifierSender) {
        this.message = message;
        this.receiverName = receiverName;
        this.senderName = senderName;
        this.timeStamp = timeStamp;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.deleteStatus = deleteStatus;
        this.receiverProfileImage = receiverProfileImage;
        this.senderProfileImage = senderProfileImage;
        this.userIdentifierReceiver = userIdentifierReceiver;
        this.userIdentifierSender = userIdentifierSender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getReceiverProfileImage() {
        return receiverProfileImage;
    }

    public void setReceiverProfileImage(String receiverProfileImage) {
        this.receiverProfileImage = receiverProfileImage;
    }

    public String getSenderProfileImage() {
        return senderProfileImage;
    }

    public void setSenderProfileImage(String senderProfileImage) {
        this.senderProfileImage = senderProfileImage;
    }

    public String getUserIdentifierReceiver() {
        return userIdentifierReceiver;
    }

    public void setUserIdentifierReceiver(String userIdentifierReceiver) {
        this.userIdentifierReceiver = userIdentifierReceiver;
    }

    public String getUserIdentifierSender() {
        return userIdentifierSender;
    }

    public void setUserIdentifierSender(String userIdentifierSender) {
        this.userIdentifierSender = userIdentifierSender;
    }
}
