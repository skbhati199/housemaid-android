package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 3/7/18.
 */

public class MaidWorkExperiencesModel implements Serializable{


    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("detail")
    private String detail;

    @SerializedName("start_date")
    private String start_date;

    @SerializedName("end_date")
    private String end_date;

    @SerializedName("still_working")
    private String still_working;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getDetail() {
        return detail;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getStill_working() {
        return still_working;
    }
}
