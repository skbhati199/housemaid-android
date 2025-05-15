package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 8/8/18.
 */

public class NotificationListModel {


    @SerializedName("type")
    private String type;

    @SerializedName("id")
    private int id;

    @SerializedName("completed_status")
    private int completed_status;

    @SerializedName("reason_to_hire")
    private String reason_to_hire;

    @SerializedName("status_by_admin")
    private String status_by_admin;

    @SerializedName("notification_text")
    private String notification_text;

    @SerializedName("body_message")
    private String body_message;

    @SerializedName("reason_to_suggest")
    private String reason_to_suggest;


    @SerializedName("notification_status")
    private int notification_status;

    @SerializedName("sender_id")
    private String sender_id ;

    @SerializedName("agency_id")
    private String agency_id;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("maid_id")
    private String maid_id;

    @SerializedName("job_id")
    private String job_id;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("sender_name")
    private String sender_name;

    @SerializedName("sender_images")
    private String sender_images;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_images() {
        return sender_images;
    }

    public void setSender_images(String sender_images) {
        this.sender_images = sender_images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompleted_status() {
        return completed_status;
    }

    public void setCompleted_status(int completed_status) {
        this.completed_status = completed_status;
    }

    public String getReason_to_hire() {
        return reason_to_hire;
    }

    public void setReason_to_hire(String reason_to_hire) {
        this.reason_to_hire = reason_to_hire;
    }

    public String getStatus_by_admin() {
        return status_by_admin;
    }

    public void setStatus_by_admin(String status_by_admin) {
        this.status_by_admin = status_by_admin;
    }

    public String getNotification_text() {
        return notification_text;
    }

    public void setNotification_text(String notification_text) {
        this.notification_text = notification_text;
    }

    public String getBody_message() {
        return body_message;
    }

    public void setBody_message(String body_message) {
        this.body_message = body_message;
    }

    public String getReason_to_suggest() {
        return reason_to_suggest;
    }

    public void setReason_to_suggest(String reason_to_suggest) {
        this.reason_to_suggest = reason_to_suggest;
    }

    public int getNotification_status() {
        return notification_status;
    }

    public void setNotification_status(int notification_status) {
        this.notification_status = notification_status;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getAgency_id() {
        return agency_id;
    }

    public void setAgency_id(String agency_id) {
        this.agency_id = agency_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMaid_id() {
        return maid_id;
    }

    public void setMaid_id(String maid_id) {
        this.maid_id = maid_id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
