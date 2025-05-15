package com.housemaid.model.bean;

import com.housemaid.model.OTPResponseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fluper on 23/5/18.
 */

public class UserBean {

    @SerializedName("id")
    private int id;

    private String name;

    @SerializedName("country_code")
    private String country_code;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    @SerializedName("otp_verified")
    private String otp_verified;

    @SerializedName("complete_profile")
    private String complete_profile;

    @SerializedName("status")
    private String status;

    @SerializedName("device_token")
    private String device_token;

    @SerializedName("device_type")
    private int device_type;

    @SerializedName("remember_token")
    private String remember_token;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("otp_response")
    private OTPResponseModel otpResponseModel;

    public String getName() {
        return name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getOtp_verified() {
        return otp_verified;
    }

    public String getComplete_profile() {
        return complete_profile;
    }

    public String getStatus() {
        return status;
    }

    public String getDevice_token() {
        return device_token;
    }

    public int getDevice_type() {
        return device_type;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public OTPResponseModel getOtpResponseModel() {
        return otpResponseModel;
    }


}
