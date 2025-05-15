package com.housemaid.model;

import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 25/6/18.
 */

public class MaidWorkingStatesModel implements Serializable {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("state_id")
    private int state_id;

    @SerializedName("state_detail")
    private UserDetailModel state_detail;

    public UserDetailModel getState_detail() {
        return state_detail;
    }

    public void setState_detail(UserDetailModel state_detail) {
        this.state_detail = state_detail;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getState_id() {
        return state_id;
    }


}
