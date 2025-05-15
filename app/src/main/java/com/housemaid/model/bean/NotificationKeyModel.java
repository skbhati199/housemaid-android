package com.housemaid.model.bean;

import com.housemaid.model.NotificationListModel;
import com.housemaid.model.OTPResponseModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fluper on 8/8/18.
 */

public class NotificationKeyModel {

    @SerializedName("hire_maid")
    private ArrayList<NotificationListModel> hireMaid;

    @SerializedName("invitation_registration")
    private ArrayList<NotificationListModel> invitationRegistration;

    @SerializedName("suggest_user")
    private ArrayList<NotificationListModel> suggestUser;

    @SerializedName("apply_joblisting")
    private ArrayList<NotificationListModel> applyJobListing;

    @SerializedName("apply_agency")
    private ArrayList<NotificationListModel> applyAgency;

    @SerializedName("maid_apply_agency")
    private ArrayList<NotificationListModel> maidApplyAgency;

    @SerializedName("request_for_maid")
    private ArrayList<NotificationListModel> requestForMaid;

    @SerializedName("suggest_maid")
    private ArrayList<NotificationListModel> suggestMaid;

    @SerializedName("highlight_job")
    private ArrayList<NotificationListModel> highlightJob;

    @SerializedName("live_conversation")
    private ArrayList<NotificationListModel> liveConversation;

    @SerializedName("daily_notification_list")
    private ArrayList<NotificationListModel> dailyNotifiaction;

    public ArrayList<NotificationListModel> getDailyNotifiaction() {
        return dailyNotifiaction;
    }

    public ArrayList<NotificationListModel> getHireMaid() {
        return hireMaid;
    }

    public ArrayList<NotificationListModel> getInvitationRegistration() {
        return invitationRegistration;
    }

    public ArrayList<NotificationListModel> getSuggestUser() {
        return suggestUser;
    }

    public ArrayList<NotificationListModel> getApplyJobListing() {
        return applyJobListing;
    }

    public ArrayList<NotificationListModel> getApplyAgency() {
        return applyAgency;
    }

    public ArrayList<NotificationListModel> getMaidApplyAgency() {
        return maidApplyAgency;
    }

    public ArrayList<NotificationListModel> getRequestForMaid() {
        return requestForMaid;
    }

    public ArrayList<NotificationListModel> getSuggestMaid() {
        return suggestMaid;
    }

    public ArrayList<NotificationListModel> getHighlightJob() {
        return highlightJob;
    }

    public ArrayList<NotificationListModel> getLiveConversation() {
        return liveConversation;
    }
}
