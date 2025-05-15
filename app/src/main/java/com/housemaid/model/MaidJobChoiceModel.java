package com.housemaid.model;

import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 24/5/18.
 */

public class MaidJobChoiceModel implements Serializable {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("job_choice_id")
    private int job_choice_id;

    @SerializedName("job_choice_detail")
    private UserDetailModel job_choice_detail;

    public UserDetailModel getJob_choice_detail() {
        return job_choice_detail;
    }

    public void setJob_choice_detail(UserDetailModel job_choice_detail) {
        this.job_choice_detail = job_choice_detail;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getJob_choice_id() {
        return job_choice_id;
    }


}
