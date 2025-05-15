package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 21/8/18.
 */

public class StatusModel implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("paid_by")
    private int paid_by;

    @SerializedName("paid_to")
    private int paid_to;

    @SerializedName("email_status")
    private String email_status;

    @SerializedName("mobile_status")
    private String mobile_status;

    @SerializedName("agencyname_status")
    private String name_status;

    @SerializedName("address_status")
    private String address_status;

    @SerializedName("company_phone_status")
    private String company_phone_status;

    @SerializedName("message_status")
    private String message_status;

    @SerializedName("call_status")
    private String call_status;

    @SerializedName("request_maid_status")
    private String request_maid_status;

    public String getRequest_maid_status() {
        return request_maid_status;
    }

    public void setRequest_maid_status(String request_maid_status) {
        this.request_maid_status = request_maid_status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPaid_by(int paid_by) {
        this.paid_by = paid_by;
    }

    public void setPaid_to(int paid_to) {
        this.paid_to = paid_to;
    }

    public void setCompany_phone_status(String company_phone_status) {
        this.company_phone_status = company_phone_status;
    }

    public void setMessage_status(String message_status) {
        this.message_status = message_status;
    }

    public void setCall_status(String call_status) {
        this.call_status = call_status;
    }

    public String getMessage_status() {
        return message_status;
    }

    public String getCall_status() {
        return call_status;
    }

    public String getCompany_phone_status() {
        return company_phone_status;
    }

    public void setEmail_status(String email_status) {
        this.email_status = email_status;
    }

    public void setMobile_status(String mobile_status) {
        this.mobile_status = mobile_status;
    }

    public void setName_status(String name_status) {
        this.name_status = name_status;
    }

    public void setAddress_status(String address_status) {
        this.address_status = address_status;
    }

    public int getId() {
        return id;
    }

    public int getPaid_by() {
        return paid_by;
    }

    public int getPaid_to() {
        return paid_to;
    }

    public String getEmail_status() {
        return email_status;
    }

    public String getMobile_status() {
        return mobile_status;
    }

    public String getName_status() {
        return name_status;
    }

    public String getAddress_status() {
        return address_status;
    }
}