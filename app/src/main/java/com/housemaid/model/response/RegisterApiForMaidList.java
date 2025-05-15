package com.housemaid.model.response;

import com.housemaid.model.FavouriteListModel;
import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 25/6/18.
 */

public class RegisterApiForMaidList {
    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public ArrayList<UserDetailModel> maidDetailModel;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<UserDetailModel> getMaidDetailModel() {
        return maidDetailModel;
    }

    public void setMaidDetailModel(ArrayList<UserDetailModel> maidDetailModel) {
        this.maidDetailModel = maidDetailModel;
    }
}
