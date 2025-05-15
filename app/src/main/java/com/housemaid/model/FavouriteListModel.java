package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fluper on 23/6/18.
 */

public class FavouriteListModel implements Serializable {


    @SerializedName("id")
    private int id;

    @SerializedName("to_job_id")
    private int to_job_id;

    @SerializedName("from_id")
    private int from_id;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("deleted_at")
    private String deleted_at;

    @SerializedName("job_post_detail")
    private SignUpModel job_post_detail;


    @SerializedName("status_paid")
    private StatusModel paidStatusModel;

    public StatusModel getPaidStatusModel() {
        return paidStatusModel;
    }
    public int getId() {
        return id;
    }

    public int getTo_job_id() {
        return to_job_id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public SignUpModel getJob_post_detail() {
        return job_post_detail;
    }
}
