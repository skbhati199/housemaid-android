package com.housemaid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fluper on 23/5/18.
 */

public class UserImageModel implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("type")
    private int type;

    @SerializedName("status_by_admin")
    private int status_by_admin;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("deleted_at")
    private String deleted_at;

    @SerializedName("image")
    public ImageModel imageModel;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getType() {
        return type;
    }

    public int getStatus_by_admin() {
        return status_by_admin;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public ImageModel getImageModel() {
        return imageModel;
    }

    public String getDeleted_at() {
        return deleted_at;

    }


    public class ImageModel implements  Serializable{
        @SerializedName("big")
        private String big;

        @SerializedName("small")
        private String small;

        @SerializedName("thumbnail")
        private String thumbnail;

        public String getBig() {
            return big;
        }

        public String getSmall() {
            return small;
        }

        public String getThumbnail() {
            return thumbnail;
        }
    }

}
