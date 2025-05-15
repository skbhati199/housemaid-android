package com.housemaid.model.response;

import com.housemaid.model.CountryListModel;
import com.housemaid.model.SignUpModel;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fluper on 14/5/18.
 */

public class RegisterApi {

    @SerializedName("message")
    public String message;

     @SerializedName("status")
    public String status;

    @SerializedName("response")
    public SignUpModel signUpModel;

    @SerializedName("notification")
    public RegisterApi notification;

    public String getStatus() {
        return status;
    }

    public RegisterApi getNotification() {
        return notification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SignUpModel getSignUpModel() {
        return signUpModel;
    }

    public void setSignUpModel(SignUpModel signUpModel) {
        this.signUpModel = signUpModel;
    }
}
