package com.housemaid.model;

import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 24/5/18.
 */

public class UserLanguageModel implements Serializable {

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("language_id")
    private int language_id;


    @SerializedName("id")
    private int id;

    @SerializedName("job_post_id")
    private int job_post_id;

    @SerializedName("language_name")
    private String language_name;

     @SerializedName("language_detail")
    private UserDetailModel language_detail;

    public UserDetailModel getLanguage_detail() {
        return language_detail;
    }

    public void setLanguage_detail(UserDetailModel language_detail) {
        this.language_detail = language_detail;
    }

    public int getId() {
        return id;
    }

    public int getJob_post_id() {
        return job_post_id;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getLanguage_id() {
        return language_id;
    }



}
