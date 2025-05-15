package com.housemaid.model;

import com.housemaid.model.bean.UserDetailModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Fluper on 14/5/18.
 */

public class SignUpModel implements Serializable {
    //public static int id;
    @SerializedName("id")
    private int id;

    @SerializedName("call_status")
    private String call_status;


    private boolean isFav = false;

    @SerializedName("is_applied")
    private int is_applied;

    public int getIs_applied() {
        return is_applied;
    }

    @SerializedName("is_favourite")
    private int is_favourite;

    public int getIs_favourite() {
        return is_favourite;
    }

    public void setIs_favourite(int is_favourite) {
        this.is_favourite = is_favourite;
    }

    @SerializedName("image")
    private UserImageModel.ImageModel image;

    public UserImageModel.ImageModel getImage() {
        return image;
    }

    public void setImage(UserImageModel.ImageModel image) {
        this.image = image;
    }

    @SerializedName("name")
    private String name;

    public String getText() {
        return text;
    }

    @SerializedName("text")
    private String text;

    public String getMessage() {
        return message;
    }

    @SerializedName("message")
    private String message;

    @SerializedName("country_code")
    private String country_code;

    @SerializedName("dob")
    private String dob;

    public String getDob() {
        return dob;
    }

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("user_type")
    private String user_type;

    @SerializedName("job_listing_title_id")
    private int job_listing_title_id;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    public String getGender() {
        return gender;
    }

    public String getCall_status() {
        return call_status;
    }

    @SerializedName("gender")
    private String gender;

    @SerializedName("disable_enable_status")
    private String disable_enable_status;

    @SerializedName("fees_currency")
    private String fees_currency;

    public String getFees_currency() {
        return fees_currency;
    }

    @SerializedName("otp_verified")
    private String otp_verified;

    @SerializedName("complete_profile")
    private String complete_profile;

    @SerializedName("total_credit")
    private int total_credit;

    @SerializedName("credit")
    private int credit;

    @SerializedName("credit_key")
    private String credit_key;

    @SerializedName("device_token")
    private String device_token;

    @SerializedName("device_type")
    private int device_type;

    @SerializedName("notification_status")
    private int notification_status;

    @SerializedName("photo_email_status")
    private int photo_email_status;

    @SerializedName("country_id")
    private int country_id;

    @SerializedName("city_id")
    private int city_id;

    @SerializedName("state_id")
    private int state_id;

    @SerializedName("district_id")
    private int district_id;

    @SerializedName("district")
    private String district;

    @SerializedName("nationality_id")
    private int nationality_id;

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

    @SerializedName("rating")
    private float rating;

    public float getRating() {
        return rating;
    }

    @SerializedName("address")
    private String address;

    public String getAddress() {
        return address;
    }

    @SerializedName("working_style_id")
    private int working_style_id;

    @SerializedName("total_experience")
    private int total_experience;

    @SerializedName("expected_min_fees")
    private int expected_min_fees;

    @SerializedName("expected_max_fees")
    private int expected_max_fees;

    @SerializedName("job_choice_id")
    private int job_choice_id;

    @SerializedName("live_with_family")
    private String live_with_family;

    @SerializedName("travel")
    private String travel;

    @SerializedName("description")
    private String description;

    @SerializedName("kid_status")
    private String kid_status;

    @SerializedName("hijab")
    private String hijab;

    @SerializedName("education_id")
    private int education_id;

    @SerializedName("smoking")
    private String smoking;

    @SerializedName("min_age")
    private String min_age;

    @SerializedName("max_age")
    private String max_age;

    @SerializedName("status_by_admin")
    private int status_by_admin;

    @SerializedName("driving_licence")
    private String driving_licence;

    @SerializedName("smoke")
    private String smoke;

    @SerializedName("alcohol")
    private String alcohol;

    @SerializedName("deleted_at")
    private String deleted_at;

    @SerializedName("remember_token")
    private String remember_token;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("job_listing_title_name")
    private String job_listing_title_name;

    @SerializedName("country_name")
    private String country_name;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("district_name")
    private String district_name;

    @SerializedName("state_name")
    private String state_name;

    @SerializedName("location_text")
    private String location_text;

    @SerializedName("latitude")
    private double latitude;

    /* @SerializedName("longitude")
     private double longitude;
 */
    @SerializedName("maid_work_experience")
    private String maid_work_experience;

    public String getMaid_work_experience() {
        return maid_work_experience;
    }

    public int getJob_listing_title_id() {
        return job_listing_title_id;
    }

    public String getLocation_text() {
        return location_text;
    }

    public double getLatitude() {
        return latitude;
    }

    /*
        public double getLongitude() {
            return longitude;
        }
    */
    public int getCredit() {
        return credit;
    }

    public String getCredit_key() {
        return credit_key;
    }

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

    @SerializedName("city_name")
    private String city_name;

    @SerializedName("working_style_name")
    private String working_style_name;

    @SerializedName("job_choice_name")
    private String job_choice_name;

    @SerializedName("nationality_name")
    private String nationality_name;

    public String getNationality() {
        return nationality;
    }

    @SerializedName("nationality")
    private String nationality;

    @SerializedName("result")
    private String result;

    @SerializedName("response_code")
    private String response_code;

    @SerializedName("payment_url")
    private String payment_url;

    @SerializedName("p_id")
    private int p_id;

    @SerializedName("credit_value")
    private int credit_value;

    @SerializedName("job_listing")
    private String job_listing;

    @SerializedName("apply_listing")
    private String apply_listing;

    @SerializedName("highlight_profile_status")
    private String highlight_profile;


 @SerializedName("highlight_profile")
    private String highlight_profile_status;

    public String getHighlight_profile_status() {
        return highlight_profile_status;
    }

    @SerializedName("highlight_job_status")
    private String highlight_job_status;

    public String getHighlight_job_status() {
        return highlight_job_status;
    }

    @SerializedName("add_maid")
    private String add_maid;

    public String getApply_listing() {
        return apply_listing;
    }

    public String getHighlight_profile() {
        return highlight_profile;
    }

    public String getAdd_maid() {
        return add_maid;
    }

    public int getCredit_value() {
        return credit_value;
    }

    public String getJob_listing() {
        return job_listing;
    }

    @SerializedName("price")
    private String price;

    public int getTotal_experience() {
        return total_experience;
    }

    @SerializedName("user_languages")
    private ArrayList<UserLanguageModel> userLanguageModel;

    @SerializedName("maid_work_experiences")
    private ArrayList<MaidWorkExperiencesModel> MaidWorkExperiencesModel;

    public ArrayList<MaidWorkExperiencesModel> getMaidWorkExperiencesModel() {
        return MaidWorkExperiencesModel;
    }

    @SerializedName("languages")
    private ArrayList<UserLanguageModel> languageModels;

    @SerializedName("nationalities")
    private ArrayList<NationalityModel> nationalityModel;

    @SerializedName("education_name")
    private ArrayList<UserEducationModel> educationModels;

    public ArrayList<UserEducationModel> getEducationModels() {
        return educationModels;
    }

    @SerializedName("user_educations")
    private ArrayList<UserEducationModel> userEducationModel;

    @SerializedName("pet_problems")
    private ArrayList<UserPetProblemModel> petProblemModels;

    @SerializedName("user_pet_problems")
    private ArrayList<UserPetProblemModel> userPetProblemModel;

/*
    @SerializedName("user_educations")
    private ArrayList<MaidJobChoiceModel> userJobChoiceModel;*/

    @SerializedName("user_images")
    private ArrayList<UserImageModel> userImagesModel;

    @SerializedName("user_image")
    private UserImageModel userImageModel;

    public UserImageModel getUserImageModel() {
        return userImageModel;
    }
  /*    @SerializedName("user_image" )
    private ArrayList<UserImageModel> userImageModel;*/

    @SerializedName("otp_response")
    private OTPResponseModel otpResponseModel;

    public ArrayList<MaidWorkingCountryModel> getMaidWorkingCountryModels() {
        return maidWorkingCountryModels;
    }

    @SerializedName("maid_working_countries")
    private ArrayList<MaidWorkingCountryModel> maidWorkingCountryModels;

    @SerializedName("maid_working_states")
    private ArrayList<MaidWorkingStatesModel> maidWorkingStatesModels;

    @SerializedName("maid_working_city")
    private ArrayList<UserDetailModel.MaidWorkingCityModel> maidWorkingCityModels;

    public ArrayList<UserDetailModel.MaidWorkingDistrictModel> getMaidWorkingDistrictModels() {
        return maidWorkingDistrictModels;
    }

    @SerializedName("maid_working_district")
    private ArrayList<UserDetailModel.MaidWorkingDistrictModel> maidWorkingDistrictModels;

    public ArrayList<UserDetailModel.MaidWorkingCityModel> getMaidWorkingCityModels() {
        return maidWorkingCityModels;
    }

    @SerializedName("maid_job_choices")
    private ArrayList<MaidJobChoiceModel> maidJobChoiceModels;

    @SerializedName("maid_working_style")
    private ArrayList<MaidWorkingStyleModel> maidWorkingStyleModels;

    @SerializedName("maid_skills")
    private ArrayList<MaidSkillsModel> maidSkillsModels;

    @SerializedName("status_paid")
    private StatusModel paidStatusModel;

    public StatusModel getPaidStatusModel() {
        return paidStatusModel;
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


    public void setDriving_licence(String driving_licence) {
        this.driving_licence = driving_licence;
    }

    public String getDisable_enable_status() {
        return disable_enable_status;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
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

    @SerializedName("user_detail")
    private UserDetailModel userDetailModel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public SignUpModel(int id) {
        this.id = id;
    }

    public int getTotal_credit() {
        return total_credit;
    }

    /* public int getId() {
        return id;

    }*/

    public String getPrice() {
        return price;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getPayment_url() {
        return payment_url;
    }

    public void setPayment_url(String payment_url) {
        this.payment_url = payment_url;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getDistrict_id() {
        return district_id;
    }

    public int getWorking_style_id() {
        return working_style_id;
    }

    public int getExpected_min_fees() {
        return expected_min_fees;
    }

    public int getExpected_max_fees() {
        return expected_max_fees;
    }

    public int getJob_choice_id() {
        return job_choice_id;
    }

    public String getLive_with_family() {
        return live_with_family;
    }

    public String getTravel() {
        return travel;
    }

    public String getDescription() {
        return description;
    }

    public String getKid_status() {
        return kid_status;
    }

    public String getHijab() {
        return hijab;
    }

    public int getEducation_id() {
        return education_id;
    }

    public String getSmoking() {
        return smoking;
    }

    public String getMin_age() {
        return min_age;
    }

    public String getMax_age() {
        return max_age;
    }

    public int getStatus_by_admin() {
        return status_by_admin;
    }

    public String getJob_listing_title_name() {
        return job_listing_title_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public String getDistrict_name() {
        return district_name;
    }

    public String getState_name() {
        return state_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getWorking_style_name() {
        return working_style_name;
    }

    public String getJob_choice_name() {
        return job_choice_name;
    }

    public String getNationality_name() {
        return nationality_name;
    }

    public ArrayList<UserLanguageModel> getUserLanguageModel() {
        return userLanguageModel;
    }

    public ArrayList<UserLanguageModel> getLanguageModels() {
        return languageModels;
    }

    public ArrayList<NationalityModel> getNationalityModel() {
        return nationalityModel;
    }

    public ArrayList<UserEducationModel> getUserEducationModel() {
        return userEducationModel;
    }

    public ArrayList<UserPetProblemModel> getPetProblemModels() {
        return petProblemModels;
    }

    public ArrayList<UserPetProblemModel> getUserPetProblemModel() {
        return userPetProblemModel;
    }

    public UserDetailModel getUserDetailModel() {
        return userDetailModel;
    }


    public String getUser_type() {
        return user_type;
    }

    public String getName() {
        return name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getOtp_verified() {
        return otp_verified;
    }

    public String getComplete_profile() {
        return complete_profile;
    }

    public String getDevice_token() {
        return device_token;
    }

    public int getDevice_type() {
        return device_type;
    }

    public int getNotification_status() {
        return notification_status;
    }

    public int getPhoto_email_status() {
        return photo_email_status;
    }

    public int getCountry_id() {
        return country_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public int getState_id() {
        return state_id;
    }

    public String getDistrict() {
        return district;
    }

    public int getNationality_id() {
        return nationality_id;
    }

    public String getMarital_status() {
        return marital_status;
    }

    public String getKids() {
        return kids;
    }

    public String getHi_job() {
        return hi_job;
    }

    public String getCompany_name() {
        return company_name;
    }

    public String getAuthorised_person() {
        return authorised_person;
    }

    public String getTax_no() {
        return tax_no;
    }

    public String getCompany_phone() {
        return company_phone;
    }

    public String getTax_administration() {
        return tax_administration;
    }

    public int getStep_for_maid_profile() {
        return step_for_maid_profile;
    }

    public String getWork_status() {
        return work_status;
    }

    public String getDriving_licence() {
        return driving_licence;
    }

    public String getSmoke() {
        return smoke;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public ArrayList<UserImageModel> getUserImagesModel() {
        return userImagesModel;
    }

    public OTPResponseModel getOtpResponseModel() {
        return otpResponseModel;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

}