package com.housemaid.model.response;

import com.housemaid.model.bean.NotificationKeyModel;
import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 8/8/18.
 */

public class RegisterApiForNotification {

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public NotificationKeyModel notificationKeyModels;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationKeyModel getNotificationKeyModels() {
        return notificationKeyModels;
    }

    public void setNotificationKeyModels(NotificationKeyModel notificationKeyModels) {
        this.notificationKeyModels = notificationKeyModels;
    }
}
