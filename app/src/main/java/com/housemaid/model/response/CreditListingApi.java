package com.housemaid.model.response;

import com.housemaid.model.StatusModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fluper on 5/9/18.
 */

public class CreditListingApi {

    @SerializedName("message")
    public String message;

    @SerializedName("response")
    public CreditListingModel statusModel;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CreditListingModel getStatusModel() {
        return statusModel;
    }

    public void setStatusModel(CreditListingModel statusModel) {
        this.statusModel = statusModel;
    }


    public class CreditListingModel {
        @SerializedName("id")
        private Integer id;
        @SerializedName("slot1")
        private String slot1;
        @SerializedName("slot2")
        private String slot2;
        @SerializedName("slot3")
        private String slot3;
        @SerializedName("slot4")
        private String slot4;
        @SerializedName("credit")
        private String credit;
        @SerializedName("user_type")
        private String userType;

        public String getCredit() {
            return credit;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSlot1() {
            return slot1;
        }

        public void setSlot1(String slot1) {
            this.slot1 = slot1;
        }

        public String getSlot2() {
            return slot2;
        }

        public void setSlot2(String slot2) {
            this.slot2 = slot2;
        }

        public String getSlot3() {
            return slot3;
        }

        public void setSlot3(String slot3) {
            this.slot3 = slot3;
        }

        public String getSlot4() {
            return slot4;
        }

        public void setSlot4(String slot4) {
            this.slot4 = slot4;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

    }
}
