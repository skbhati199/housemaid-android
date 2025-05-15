package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fluper on 14/5/18.
 */

public class OTPResponseModel {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public OTPResponseModel(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}
