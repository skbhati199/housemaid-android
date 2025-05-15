package com.housemaid.model;

import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 25/6/18.
 */

public class MaidWorkingStyleModel implements Serializable {
    @SerializedName("user_id")
    private int user_id;

    @SerializedName("style_id")
    private int state_id;

    @SerializedName("working_style_detail")
    private UserDetailModel working_style_detail;

    public UserDetailModel getWorking_style_detail() {
        return working_style_detail;
    }

    public void setWorking_style_detail(UserDetailModel working_style_detail) {
        this.working_style_detail = working_style_detail;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getState_id() {
        return state_id;
    }


}
