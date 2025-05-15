package com.housemaid.model.response;

import com.housemaid.model.CountryListModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 27/8/18.
 */

public class ErrorResponse {

    @SerializedName("message")
    public String message;

    public String getMessage() {
        return message;
    }
}
