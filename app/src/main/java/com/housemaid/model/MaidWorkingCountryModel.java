package com.housemaid.model;

import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 9/7/18.
 */

public class MaidWorkingCountryModel implements Serializable{
    @SerializedName("user_id")
    private int user_id;

    @SerializedName("country_id")
    private int country_id;

    public int getUser_id() {
        return user_id;
    }

    public int getCountry_id() {
        return country_id;
    }

    public UserDetailModel getCountry_detail() {
        return country_detail;
    }

    @SerializedName("country_detail")

    private UserDetailModel country_detail;
}
