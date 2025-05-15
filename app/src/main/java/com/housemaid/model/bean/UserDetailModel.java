package com.housemaid.model.bean;

import com.housemaid.model.MaidJobChoiceModel;
import com.housemaid.model.MaidSkillsModel;
import com.housemaid.model.MaidWorkExperiencesModel;
import com.housemaid.model.MaidWorkingStatesModel;
import com.housemaid.model.MaidWorkingStyleModel;
import com.housemaid.model.StatusModel;
import com.housemaid.model.UserEducationModel;
import com.housemaid.model.UserImageModel;
import com.housemaid.model.UserLanguageModel;
import com.housemaid.model.UserPetProblemModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by fluper on 21/6/18.
 */

public class UserDetailModel implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("job_id")
    private int job_id;

    public int getJob_id() {
        return job_id;
    }

    public String getJob_listing_title_name() {
        return job_listing_title_name;
    }

    @SerializedName("name")
    private String name;

    @SerializedName("country_code")
    private String country_code;


    @SerializedName("is_invited_by_agency")
    private int is_invited_by_agency;

    public int getIs_invited_by_agency() {
        return is_invited_by_agency;
    }

    @SerializedName("is_favourite")
    private int is_favourite;

    @SerializedName("is_hired")
    private Boolean is_hired;

    public Boolean getIs_hired() {
        return is_hired;
    }

    public int getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(int is_favourite) {
        this.is_favourite = is_favourite;
    }

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("email")
    private String email;

    @SerializedName("gender")
    private String gender;

    @SerializedName("fees_currency")
    private String fees_currency;

    public String getGender() {
        return gender;
    }

    @SerializedName("dob")
    private String dob;

    public String getDob() {
        return dob;
    }

    @SerializedName("otp")
    private String otp;

    @SerializedName("otp_verified")
    private int otp_verified;

    @SerializedName("complete_profile")
    private int complete_profile;

    @SerializedName("status")
    private String status;

    @SerializedName("user_type")
    private String user_type;

    @SerializedName("total_experience")
    private int total_experience;

    @SerializedName("device_token")
    private String device_token;

    @SerializedName("device_type")
    private int device_type;

    @SerializedName("notification_status")
    private int notification_status;

    @SerializedName("photo_email_status")
    private int photo_email_status;

    @SerializedName("country_name")
    private String country_name;

    @SerializedName("country_id")
    private int country_id;

    @SerializedName("city_id")
    private String city_id;

    @SerializedName("state_id")
    private int state_id;

    @SerializedName("state_name")
    private String state_name;

    public String getCountry_name() {
        return country_name;
    }

    public String getState_name() {
        return state_name;
    }

    @SerializedName("district")
    private String district;

    @SerializedName("job_listing_title_name")
    private String job_listing_title_name;

    @SerializedName("nationality_id")
    private String nationality_id;

    @SerializedName("nationality_name")
    private String nationality_name;

    @SerializedName("nationality")
    private String nationality;

    public String getNationality() {
        return nationality;
    }

    public String getNationality_name() {
        return nationality_name;
    }

    @SerializedName("marital_status")
    private String marital_status;

    @SerializedName("kids")
    private String kids;

    @SerializedName("hi_job")
    private String hi_job;

    @SerializedName("company_name")
    private String company_name;

    @SerializedName("authorised_person")
    private String authorised_person;

    @SerializedName("tax_no")
    private String tax_no;

    @SerializedName("company_phone")
    private String company_phone;

    @SerializedName("tax_administration")
    private String tax_administration;

    @SerializedName("step_for_maid_profile")
    private int step_for_maid_profile;

    @SerializedName("work_status")
    private String work_status;

    @SerializedName("driving_licence")
    private String driving_licence;

    @SerializedName("smoke")
    private String smoke;

    @SerializedName("alcohol")
    private String alcohol;

    @SerializedName("can_live_with_family")
    private String can_live_with_family;

    @SerializedName("travel_situation")
    private String travel_situation;

    @SerializedName("expected_fees")
    private String expected_fees;


    @SerializedName("maid_education")
    private String maid_education;

    @SerializedName("maid_certificate")
    private String maid_certificate;

    @SerializedName("maid_about_me")
    private String maid_about_me;

    @SerializedName("remember_token")
    private String remember_token;

    @SerializedName("location_text")
    private String location_text;
/*
    @SerializedName("latitude")
    private double latitude;*/
/*

    @SerializedName("longitude")
    private double longitude;
*/

    @SerializedName("maid_work_experience")
    private String maid_work_experience;

    public String getMaid_work_experience() {
        return maid_work_experience;
    }


    @SerializedName("maid_work_experiences")
    private ArrayList<MaidWorkExperiencesModel> MaidWorkExperiencesModel;

    public ArrayList<MaidWorkExperiencesModel> getMaidWorkExperiencesModel() {
        return MaidWorkExperiencesModel;
    }

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("deleted_at")
    private String deleted_at;

    @SerializedName("to_maid_id")
    private int to_maid_id;


    @SerializedName("maid_id")
    private int maid_id;

    @SerializedName("hire_to")
    private int hire_to;

    @SerializedName("hire_by")
    private int hire_by;

    public int getHire_to() {
        return hire_to;
    }

    public int getHire_by() {
        return hire_by;
    }

    @SerializedName("completed_status")
    private int completed_status;

    @SerializedName("rated")
    private int rated;

    public int getCompleted_status() {
        return completed_status;
    }

    public int getRated() {
        return rated;
    }

    public int getMaid_id() {
        return maid_id;
    }

    @SerializedName("from_id")
    private int from_id;

    public int getTo_maid_id() {
        return to_maid_id;
    }

    public int getFrom_id() {
        return from_id;
    }

    @SerializedName("rating")
    private float rating;

    @SerializedName("agency_name")
    private String agency_name;

    public float getRating() {
        return rating;
    }

    public String getAgency_name() {
        return agency_name;
    }

    @SerializedName("maid_can_work_country_name")
    private String maid_can_work_country_name;

    public String getMaid_can_work_country_name() {
        return maid_can_work_country_name;
    }

    @SerializedName("userimages")
    private ArrayList<UserImageModel> userImageModel;

    @SerializedName("maid_images")
    private ArrayList<UserImageModel> maidBookedImageModel;

    public ArrayList<UserImageModel> getMaidBookedImageModel() {
        return maidBookedImageModel;
    }

    public ArrayList<UserImageModel> getUserImagesModel() {
        return userImageModel;
    }

    @SerializedName("user_languages")
    private ArrayList<UserLanguageModel> userLanguageModel;

    @SerializedName("user_educations")
    private ArrayList<UserEducationModel> userEducationModel;

    @SerializedName("user_pet_problems")
    private ArrayList<UserPetProblemModel> userPetProblemModel;

    @SerializedName("maid_working_states")
    private ArrayList<MaidWorkingStatesModel> maidWorkingStatesModels;

    @SerializedName("maid_working_city")
    private ArrayList<MaidWorkingCityModel> maidWorkingCityModels;

    public ArrayList<MaidWorkingDistrictModel> getMaidWorkingDistrictModels() {
        return maidWorkingDistrictModels;
    }

    @SerializedName("maid_working_district")
    private ArrayList<MaidWorkingDistrictModel> maidWorkingDistrictModels;

    public ArrayList<MaidWorkingCityModel> getMaidWorkingCityModels() {
        return maidWorkingCityModels;
    }

    @SerializedName("maid_job_choices")
    private ArrayList<MaidJobChoiceModel> maidJobChoiceModels;

    @SerializedName("maid_working_style")
    private ArrayList<MaidWorkingStyleModel> maidWorkingStyleModels;

    @SerializedName("maid_skills")
    private ArrayList<MaidSkillsModel> maidSkillsModels;

    @SerializedName("user_images")
    private ArrayList<UserImageModel> maidImageModel;

    @SerializedName("status_paid")
    private StatusModel paidStatusModel;

    public int getTotal_experience() { return total_experience; }

    public String getFees_currency() {
        return fees_currency;
    }

    public StatusModel getPaidStatusModel() {
        return paidStatusModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public int getOtp_verified() {
        return otp_verified;
    }

    public void setOtp_verified(int otp_verified) {
        this.otp_verified = otp_verified;
    }

    public int getComplete_profile() {
        return complete_profile;
    }

    public void setComplete_profile(int complete_profile) {
        this.complete_profile = complete_profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public int getDevice_type() {
        return device_type;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public int getNotification_status() {
        return notification_status;
    }

    public void setNotification_status(int notification_status) {
        this.notification_status = notification_status;
    }

    public int getPhoto_email_status() {
        return photo_email_status;
    }

    public void setPhoto_email_status(int photo_email_status) {
        this.photo_email_status = photo_email_status;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getNationality_id() {
        return nationality_id;
    }

    public void setNationality_id(String nationality_id) {
        this.nationality_id = nationality_id;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public void setMarital_status(String marital_status) {
        this.marital_status = marital_status;
    }

    public String getKids() {
        return kids;
    }

    public void setKids(String kids) {
        this.kids = kids;
    }

    public String getHi_job() {
        return hi_job;
    }

    public void setHi_job(String hi_job) {
        this.hi_job = hi_job;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getAuthorised_person() {
        return authorised_person;
    }

    public void setAuthorised_person(String authorised_person) {
        this.authorised_person = authorised_person;
    }

    public String getTax_no() {
        return tax_no;
    }

    public void setTax_no(String tax_no) {
        this.tax_no = tax_no;
    }

    public String getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(String company_phone) {
        this.company_phone = company_phone;
    }

    public String getTax_administration() {
        return tax_administration;
    }

    public void setTax_administration(String tax_administration) {
        this.tax_administration = tax_administration;
    }

    public int getStep_for_maid_profile() {
        return step_for_maid_profile;
    }

    public void setStep_for_maid_profile(int step_for_maid_profile) {
        this.step_for_maid_profile = step_for_maid_profile;
    }

    public String getWork_status() {
        return work_status;
    }

    public void setWork_status(String work_status) {
        this.work_status = work_status;
    }

    public String getDriving_licence() {
        return driving_licence;
    }

    public void setDriving_licence(String driving_licence) {
        this.driving_licence = driving_licence;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getCan_live_with_family() {
        return can_live_with_family;
    }

    public void setCan_live_with_family(String can_live_with_family) {
        this.can_live_with_family = can_live_with_family;
    }

    public String getTravel_situation() {
        return travel_situation;
    }

    public void setTravel_situation(String travel_situation) {
        this.travel_situation = travel_situation;
    }

    public String getExpected_fees() {
        return expected_fees;
    }

    public void setExpected_fees(String expected_fees) {
        this.expected_fees = expected_fees;
    }

    public String getMaid_education() {
        return maid_education;
    }

    public void setMaid_education(String maid_education) {
        this.maid_education = maid_education;
    }

    public String getMaid_certificate() {
        return maid_certificate;
    }

    public void setMaid_certificate(String maid_certificate) {
        this.maid_certificate = maid_certificate;
    }

    public String getMaid_about_me() {
        return maid_about_me;
    }

    public void setMaid_about_me(String maid_about_me) {
        this.maid_about_me = maid_about_me;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public String getLocation_text() {
        return location_text;
    }

    public void setLocation_text(String location_text) {
        this.location_text = location_text;
    }

  /*  public double getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }*/

   /* public double getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }*/

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public ArrayList<UserImageModel> getUserImageModel() {
        return userImageModel;
    }

    public ArrayList<UserLanguageModel> getUserLanguageModel() {
        return userLanguageModel;
    }

    public ArrayList<UserEducationModel> getUserEducationModel() {
        return userEducationModel;
    }

    public ArrayList<UserPetProblemModel> getUserPetProblemModel() {
        return userPetProblemModel;
    }

    public ArrayList<MaidWorkingStatesModel> getMaidWorkingStatesModels() {
        return maidWorkingStatesModels;
    }

    public ArrayList<MaidJobChoiceModel> getMaidJobChoiceModels() {
        return maidJobChoiceModels;
    }

    public ArrayList<MaidWorkingStyleModel> getMaidWorkingStyleModels() {
        return maidWorkingStyleModels;
    }

    public ArrayList<MaidSkillsModel> getMaidSkillsModels() {
        return maidSkillsModels;
    }

    public ArrayList<UserImageModel> getMaidImageModel() {
        return maidImageModel;
    }

    public class MaidWorkingCityModel implements Serializable {

        @SerializedName("user_id")
        private int user_id;

        @SerializedName("id")
        private int id;

        public int getId() {
            return id;
        }

        @SerializedName("city_id")
        private int city_id;

        @SerializedName("city_detail")
        private UserDetailModel city_detail;

        public int getUser_id() {
            return user_id;
        }

        public int getCity_id() {
            return city_id;
        }

        public UserDetailModel getCity_detail() {
            return city_detail;
        }
    }

    public class MaidWorkingDistrictModel implements Serializable {


        @SerializedName("user_id")
        private int user_id;

        @SerializedName("id")
        private int id;

        @SerializedName("district_id")
        private int district_id;

        @SerializedName("district_detail")
        private UserDetailModel district_detail;

        public int getUser_id() {
            return user_id;
        }

        public int getId() {
            return id;
        }

        public int getDistrict_id() {
            return district_id;
        }

        public UserDetailModel getDistrict_detail() {
            return district_detail;
        }
    }


}
