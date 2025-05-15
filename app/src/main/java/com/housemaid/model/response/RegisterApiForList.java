package com.housemaid.model.response;

import com.housemaid.model.FavouriteListModel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 23/6/18.
 */

public class RegisterApiForList {

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public ArrayList<FavouriteListModel> favouriteListModelArrayList;

    public ArrayList<FavouriteListModel> getFavouriteListModelArrayList() {
        return favouriteListModelArrayList;
    }

    public void setFavouriteListModelArrayList(ArrayList<FavouriteListModel> favouriteListModelArrayList) {
        this.favouriteListModelArrayList = favouriteListModelArrayList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
