package com.housemaid.model.response;

import com.housemaid.model.SignUpModel;
import com.housemaid.model.StatusModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fluper on 21/8/18.
 */

public class CreditStatusApi {

        @SerializedName("message")
        public String message;

        @SerializedName("response")
        public StatusModel statusModel;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    public StatusModel getStatusModel() {
        return statusModel;
    }

    public void setStatusModel(StatusModel statusModel) {
        this.statusModel = statusModel;
    }
}

