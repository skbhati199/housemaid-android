package com.housemaid.model.response;

import com.housemaid.model.SignUpModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 21/6/18.
 */

public class RegisterApiList {

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public ArrayList<SignUpModel> userJobListingModel;

    public ArrayList<SignUpModel> getUserJobListingModel() {
        return userJobListingModel;
    }

    public void setUserJobListingModel(ArrayList<SignUpModel> userJobListingModel) {
        this.userJobListingModel = userJobListingModel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    }

