package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fluper on 21/6/18.
 */

public class NationalityModel {

    @SerializedName("id")
    private int id;

    @SerializedName("nationality_id")
    private int nationality_id;

 @SerializedName("job_post_id")
    private int job_post_id;

    @SerializedName("nationality_name")
    private String nationality_name;

    public int getId() {
        return id;
    }

    public int getNationality_id() {
        return nationality_id;
    }

    public int getJob_post_id() {
        return job_post_id;
    }

    public String getNationality_name() {
        return nationality_name;
    }
}
