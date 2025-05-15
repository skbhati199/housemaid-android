package com.housemaid.model;

import com.housemaid.model.MaidWorkingStatesModel;
import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 25/6/18.
 */

public  class MaidSkillsModel implements Serializable {
    @SerializedName("user_id")
    private int user_id;

    @SerializedName("state_id")
    private int state_id;

      @SerializedName("skill_name")
    private String skill_name;

    public String getSkill_name() {
        return skill_name;
    }

    @SerializedName("skill_detail")
    private UserDetailModel skill_detail;

    public UserDetailModel getSkill_detail() {
        return skill_detail;
    }

    public void setSkill_detail(UserDetailModel skill_detail) {
        this.skill_detail = skill_detail;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getState_id() {
        return state_id;
    }

}
