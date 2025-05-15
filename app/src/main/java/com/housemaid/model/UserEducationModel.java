package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 24/5/18.
 */

public class UserEducationModel implements Serializable{
    @SerializedName("user_id")
    private int user_id;

    @SerializedName("education_id")
    private int education_id;

    @SerializedName("education_name")
    private String  education_name;

    public String getEducation_name() {
        return education_name;
    }

    @SerializedName("education_detail")
    private EducationDetailModel educationDetailModel;

    public int getUser_id() {
        return user_id;
    }

    public int getEducation_id() {
        return education_id;
    }

    public EducationDetailModel getEducationDetailModel() {
        return educationDetailModel;
    }

    public class EducationDetailModel implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
