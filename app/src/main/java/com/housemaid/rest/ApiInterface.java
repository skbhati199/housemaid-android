package com.housemaid.rest;

import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.model.response.RegisterApiForList;
import com.housemaid.model.response.RegisterApiForMaidList;
import com.housemaid.model.response.RegisterApiForNotification;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.model.response.ServerResponseCountryList;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by fluper on 14/5/18.
 */
public interface ApiInterface {
    @FormUrlEncoded
    @POST("sign_up")
    Call<RegisterApi> registerUser(@Header("timezone") String timezone,
                                   @Field("user_name") String name,
                                   @Field("country_code") String country_code,
                                   @Field("mobile") String mobile,
                                   @Field("password") String password,
                                   @Field("device_token") String device_token,
                                   @Field("device_type") String device_type,
                                   @Field("user_type") String user_type,
                                   @Field("email") String email);

    @FormUrlEncoded
    @POST("login")
    Call<RegisterApi> loginUser(@Field("mobile") String mobile,
                                @Field("password") String password,
                                @Field("device_token") String device_token,
                                @Field("device_type") String device_type,
                                @Field("user_type") String user_type);

    @FormUrlEncoded
    @POST("otpVerify")
    Call<RegisterApi> otpVerifyUser(@Header("timezone") String timezone,
                                    @Header("locale") String locale,
                                    @Field("user_id") String user_id,
                                    @Field("key") String key,
                                    @Field("otp") String otp);

    @FormUrlEncoded
    @POST("forgetPassword")
    Call<RegisterApi> forgetPassword(@Header("timezone") String timezone,
                                     @Field("country_code") String country_code,
                                     @Field("mobile") String mobile,
                                     @Field("key") String key,
                                     @Field("user_type") String user_type);

    @FormUrlEncoded
    @POST("change_password")
    Call<RegisterApi> changePassword(@Header("timezone") String timezone,
                                     @Header("locale") String locale,
                                     @Field("key") String key,
                                     @Field("old_password") String old_password,
                                     @Field("new_password") String new_password,
                                     @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("changeMobileNumber")
    Call<RegisterApi> changeMobileNumber(@Header("accessToken") String accessToken,
                                         @Field("country_code") String country_code,
                                         @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("resendOtp")
    Call<RegisterApi> resendOtp(@Header("timezone") String timezone,
                                @Field("user_id") String user_id,
                                @Field("key") String key);

    @Multipart
    @POST("update_profilepic")
    Call<RegisterApi> updateProfilePicture(@Header("accessToken") String accessToken,
                                           @Part MultipartBody.Part profileImage,
                                           @PartMap Map<String, RequestBody> map);

    @GET("get_country_list")
    Call<ServerResponseCountryList> getCountryList(@Header("accessToken") String accessToken);


    @Multipart
    @POST("complete_profile")
    Call<RegisterApi> completeProfileDetails(@Header("timezone") String timezone,
                                             @Header("locale") String locale,
                                             @Header("accessToken") String accessToken,
                                             @Part MultipartBody.Part profileImage,
                                             @PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST("update_profile")
    Call<RegisterApi> updateUserProfile(@Header("timezone") String timezone,
                                        @Header("locale") String locale,
                                        @Header("accessToken") String accessToken,
                                        @Field("user_type") String user_type,
                                        @Field("country_id") String country_id,
                                        @Field("dob") String dob,
                                        @Field("gender") String gender,
                                        @Field("marital_status") String marital_status,
                                        @Field("state_id") String state_id);

    @FormUrlEncoded
    @POST("update_profile")
    Call<RegisterApi> updateAgencyProfile(@Header("timezone") String timezone,
                                          @Header("locale") String locale,
                                          @Header("accessToken") String accessToken,
                                          @Field("user_type") String user_type,
                                          @Field("country_id") String country_id,
                                          @Field("company_name") String company_name,
                                          @Field("authorised_person") String authorised_person,
                                          @Field("tax_administration") String tax_administration,
                                          @Field("tax_no") String tax_no,
                                          @Field("company_phone") String company_phone,
                                          @Field("address") String address,
                                          @Field("state_id") String state_id);


    @POST("logout")
    Call<RegisterApi> logoutFromServer(@Header("accessToken") String accessToken);

    @FormUrlEncoded
    @POST("get_state_under_country")
    Call<ServerResponseCountryList> getSateList(@Field("country_id") int country_id);

    @Multipart
    @POST("complete_profile/maid")
    Call<RegisterApi> completeProfileMaid(@Header("timezone") String timezone,
                                          @Header("locale") String locale,
                                          @Header("accessToken") String accessToken,
                                          @Part MultipartBody.Part profileImage,
                                          @PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST("complete_profile/maid")
    Call<RegisterApi> completeProfileMaid1(@Header("timezone") String timezone,
                                           @Header("locale") String locale,
                                           @Header("accessToken") String accessToken,
                                           @Field("user_type") String user_type,
                                           @Field("country_id") String country_id,
                                           @Field("dob") String dob,
                                           @Field("gender") String gender,
                                           @Field("marital_status") String marital_status,
                                           @Field("step_for_maid_profile") String step_for_maid_profile,
                                           @Field("nationality_id") String nationality_id,
                                           @Field("kids") String kids,
                                           @Field("hi_job") String hi_job,
                                           @Field("state_id") String state_id);


    @FormUrlEncoded
    @POST("complete_profile/maid")
    Call<RegisterApi> completeProfileMaid2(@Header("timezone") String timezone,
                                           @Header("locale") String locale,
                                           @Header("accessToken") String accessToken,
                                           @Field("user_type") String user_type,
                                           @Field("education_ids") String education_ids,
                                           @Field("languages_ids") String languages_ids,
                                           @Field("work_status") String work_status,
                                           @Field("driving_licence") String driving_licence,
                                           @Field("step_for_maid_profile") String step_for_maid_profile,
                                           @Field("smoke") String smoke,
                                           @Field("alcohol") String alcohol,
                                           @Field("pet_problem_ids") String pet_problem_ids);

    @FormUrlEncoded
    @POST("complete_profile/maid")
    Call<RegisterApi> completeProfileMaid3(@Header("timezone") String timezone,
                                           @Header("locale") String locale,
                                           @Header("accessToken") String accessToken,
                                           @Field("user_type") String user_type,
                                           @Field("working_states") String working_states,
                                           @Field("maid_can_work_country_id") String maid_can_work_country_id,
                                           @Field("city") String city,
                                           @Field("district") String district,
                                           @Field("maid_job_choice_ids") String maid_job_choice_ids,
                                           @Field("maid_working_style_ids") String maid_working_style_ids,
                                           @Field("step_for_maid_profile") String step_for_maid_profile,
                                           @Field("can_live_with_family") String can_live_with_family,
                                           @Field("travel_situation") String travel_situation,
                                           @Field("expected_fees") String expected_fees,
                                           @Field("maid_skill_ids") String maid_skill_ids,
                                           @Field("fees_currency") String fees_currency);

    @FormUrlEncoded
    @POST("complete_profile/maid")
    Call<RegisterApi> completeProfileMaid4(@Header("timezone") String timezone,
                                           @Header("locale") String locale,
                                           @Header("accessToken") String accessToken,
                                           @Field("user_type") String user_type,
                                           @Field("maid_education") String maid_education,
                                           @Field("maid_certificate") String maid_certificate,
                                           @Field("maid_about_me") String maid_about_me,
                                           @Field("notification_status") String notification_status,
                                           @Field("step_for_maid_profile") String step_for_maid_profile,
                                           @Field("photo_email_status") String photo_email_status,
                                           @Field("maid_work_experiences[]") ArrayList<String> maid_work_experiences,
                                           @Field("start_date[]") ArrayList<String> start_date,
                                           @Field("end_date[]") ArrayList<String> end_date,
                                           @Field("still_working[]") ArrayList<String> still_working);

    @FormUrlEncoded
    @POST("update_profile/maid")
    Call<RegisterApi> updateMaidProfile(@Header("timezone") String timezone,
                                        @Header("locale") String locale,
                                        @Header("accessToken") String accessToken,
                                        @Field("user_type") String user_type,
                                        @Field("country_id") String country_id,
                                        @Field("dob") String dob,
                                        @Field("gender") String gender,
                                        @Field("marital_status") String marital_status,
                                        @Field("nationality_id") String nationality_id,
                                        @Field("kids") String kids,
                                        @Field("hi_job") String hi_job,
                                        @Field("state_id") String state_id,
                                        @Field("education_ids") String education_ids,
                                        @Field("languages_ids") String languages_ids,
                                        @Field("work_status") String work_status,
                                        @Field("driving_licence") String driving_licence,
                                        @Field("smoke") String smoke,
                                        @Field("alcohol") String alcohol,
                                        @Field("pet_problem_ids") String pet_problem_ids,
                                        @Field("working_states") String working_states,
                                        @Field("maid_can_work_country_id") String maid_can_work_country_id,
                                        @Field("city") String city,
                                        @Field("district") String district,
                                        @Field("maid_job_choice_ids") String maid_job_choice_ids,
                                        @Field("maid_working_style_ids") String maid_working_style_ids,
                                        @Field("can_live_with_family") String can_live_with_family,
                                        @Field("travel_situation") String travel_situation,
                                        @Field("expected_fees") String expected_fees,
                                        @Field("maid_skill_ids") String maid_skill_ids,
                                        @Field("maid_education") String maid_education,
                                        @Field("maid_certificate") String maid_certificate,
                                        @Field("maid_about_me") String maid_about_me,
                                        @Field("maid_work_experiences[]") ArrayList<String> maid_work_experiences,
                                        @Field("start_date[]") ArrayList<String> start_date,
                                        @Field("end_date[]") ArrayList<String> end_date,
                                        @Field("still_working[]") ArrayList<String> still_working,
                                        @Field("fees_currency") String fees_currency);


    @FormUrlEncoded
    @POST("maid_list_datafilter")
    Call<RegisterApiForMaidList> getMaidListByFilter(
            @Header("accessToken") String accessToken,
            @Field("country_id") String country_id,
            @Field("city_id") String city_id,
            @Field("state_id") String state_id,
            @Field("district_id") String district_id,
            @Field("nationality_id") String nationality_id,
            @Field("fees") String fees,
            @Field("age") String age,
            @Field("language") String language,
            @Field("jobchoice") String jobchoice,
            @Field("work_status") String work_status,
            @Field("marital_status") String marital_status,
            @Field("kids") String kids,
            @Field("hijab") String hijab,
            @Field("education") String education,
            @Field("driving_licence") String driving_licence,
            @Field("smoke") String smoke,
            @Field("alcohol") String alcohol,
            @Field("work_type") String work_type,
            @Field("family_status") String family_status,
            @Field("rating") String rating,
            @Field("fees_currency") String fees_currency);


    @FormUrlEncoded
    @POST("get_job_filterlist")
    Call<RegisterApiList> getJobListByFilter(
            @Header("accessToken") String accessToken,
            @Field("country_id") String country_id,
            @Field("city_id") String city_id,
            @Field("state_id") String state_id,
            @Field("district_id") String district_id,
            @Field("nationality_id") String nationality_id,
            @Field("fees") String fees,
            @Field("age") String age,
            @Field("language") String language,
            @Field("jobchoice") String jobchoice,
            @Field("work_status") String work_status,
            @Field("marital_status") String marital_status,
            @Field("kids") String kids,
            @Field("hijab") String hijab,
            @Field("education") String education,
            @Field("driving_licence") String driving_licence,
            @Field("smoke") String smoke,
            @Field("alcohol") String alcohol,
            @Field("work_type") String work_type,
            @Field("family_status") String family_status,
            @Field("experience") String experience);

    @FormUrlEncoded
    @POST("agency_listfilter")
    Call<RegisterApiList> getAgencyListByFilter(@Header("accessToken") String accessToken,
                                                @Field("country_id") String country_id,
                                                @Field("state_id") String state_id);

    @Multipart
    @POST("add_maid_by_agency")
    Call<RegisterApi> agencyAddMaidFirst(@Header("timezone") String timezone,
                                         @Header("accessToken") String accessToken,
                                         @Part MultipartBody.Part profileImage,
                                         @PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST("add_maid_by_agency")
    Call<RegisterApi> agencyAddMaid2(@Header("timezone") String timezone,
                                     @Header("accessToken") String accessToken,
                                     @Field("country_id") String country_id,
                                     @Field("maid_id") String maid_id,
                                     @Field("dob") String dob,
                                     @Field("gender") String gender,
                                     @Field("marital_status") String marital_status,
                                     @Field("step_for_maid_profile") String step_for_maid_profile,
                                     @Field("nationality_id") String nationality_id,
                                     @Field("kids") String kids,
                                     @Field("hi_job") String hi_job,
                                     @Field("state_id") String state_id);


    @FormUrlEncoded
    @POST("add_maid_by_agency")
    Call<RegisterApi> agencyAddMaid3(@Header("timezone") String timezone,
                                     @Header("accessToken") String accessToken,
                                     @Field("maid_id") String maid_id,
                                     @Field("education_ids") String education_ids,
                                     @Field("languages_ids") String languages_ids,
                                     @Field("work_status") String work_status,
                                     @Field("driving_licence") String driving_licence,
                                     @Field("step_for_maid_profile") String step_for_maid_profile,
                                     @Field("smoke") String smoke,
                                     @Field("alcohol") String alcohol,
                                     @Field("pet_problem_ids") String pet_problem_ids);

    @FormUrlEncoded
    @POST("add_maid_by_agency")
    Call<RegisterApi> agencyAddMaid4(@Header("timezone") String timezone,
                                     @Header("accessToken") String accessToken,
                                     @Field("maid_id") String maid_id,
                                     @Field("working_states") String working_states,
                                     @Field("maid_can_work_country_id") String maid_can_work_country_id,
                                     @Field("city") String city,
                                     @Field("district") String district,
                                     @Field("maid_job_choice_ids") String maid_job_choice_ids,
                                     @Field("maid_working_style_ids") String maid_working_style_ids,
                                     @Field("step_for_maid_profile") String step_for_maid_profile,
                                     @Field("can_live_with_family") String can_live_with_family,
                                     @Field("travel_situation") String travel_situation,
                                     @Field("expected_fees") String expected_fees,
                                     @Field("maid_skill_ids") String maid_skill_ids,
                                     @Field("fees_currency") String fees_currency);


    @FormUrlEncoded
    @POST("add_maid_by_agency")
    Call<RegisterApi> agencyAddMaid5(@Header("timezone") String timezone,
                                     @Header("accessToken") String accessToken,
                                     @Field("maid_id") String maid_id,
                                     @Field("maid_education") String maid_education,
                                     @Field("maid_certificate") String maid_certificate,
                                     @Field("maid_about_me") String maid_about_me,
                                     @Field("notification_status") String notification_status,
                                     @Field("step_for_maid_profile") String step_for_maid_profile,
                                     @Field("photo_email_status") String photo_email_status,
                                     @Field("maid_work_experiences[]") ArrayList<String> maid_work_experiences,
                                     @Field("start_date[]") ArrayList<String> start_date,
                                     @Field("end_date[]") ArrayList<String> end_date,
                                     @Field("still_working[]") ArrayList<String> still_working);

    @FormUrlEncoded
    @POST("add_maid_by_agency")
    Call<RegisterApi> agencyAddMaid6(@Header("timezone") String timezone,
                                     @Header("accessToken") String accessToken,
                                     @Field("maid_id") String maid_id,
                                     @Field("step_for_maid_profile") String step_for_maid_profile,
                                     @Field("location_text") String location_text,
                                     @Field("latitude") Double latitude,
                                     @Field("longitude") Double longitude);


    @POST("get_nationality")
    Call<ServerResponseCountryList> getNationalityList();

    @GET("get_education")
    Call<ServerResponseCountryList> getEducationList();

    @GET("job_listing_title")
    Call<ServerResponseCountryList> getListingType(@Header("accessToken") String accessToken);

    @GET("get_language")
    Call<ServerResponseCountryList> getLanguageList();

    @GET("get_pet_problems")
    Call<ServerResponseCountryList> getPetProblemsList();

    @GET("get_job_choices")
    Call<ServerResponseCountryList> getJobChoiceList();

    @GET("get_working_choices")
    Call<ServerResponseCountryList> getworkingChoiceList();

    @GET("get_skills")
    Call<ServerResponseCountryList> getSkillsList();

    @GET("notificationlist")
    Call<RegisterApiForNotification> getNotificationList(@Header("accessToken") String accessToken);

    @FormUrlEncoded
    @POST("get_district_list")
    Call<ServerResponseCountryList> getDistrictList(@Field("state_id") int state_id);

    @FormUrlEncoded
    @POST("get_city_list")
    Call<ServerResponseCountryList> getCityList(@Field("district_id") int district_id);

    @FormUrlEncoded
    @POST("get_multicity_list")
    Call<ServerResponseCountryList> get_multicity_list(@Field("district_id[]") ArrayList<String> district_id);

    @FormUrlEncoded
    @POST("update_user_location")
    Call<RegisterApi> updateLocation(@Header("timezone") String timezone,
                                     @Header("locale") String locale,
                                     @Header("accessToken") String accessToken,
                                     @Field("location_text") String location_text,
                                     @Field("latitude") Double latitude,
                                     @Field("longitude") Double longitude);

    @Multipart
    @POST("post_job_by_user")
    Call<RegisterApi> postJobByUser(@Header("timezone") String timezone,
                                    @Header("locale") String locale,
                                    @Header("accessToken") String accessToken,
                                    @PartMap Map<String, RequestBody> map,
                                    @Part MultipartBody.Part image);

    @Multipart
    @POST("post_job_by_user")
    Call<RegisterApi> postJobByUserImage(@Header("timezone") String timezone,
                                         @Header("locale") String locale,
                                         @Header("accessToken") String accessToken,
                                         @Part MultipartBody.Part profileImage);

    @POST("get_profile")
    Call<RegisterApi> getProfileDetail(@Header("timezone") String timezone,
                                       @Header("locale") String locale,
                                       @Header("accessToken") String accessToken);

    @FormUrlEncoded
    @POST("job_listing")
    Call<RegisterApiList> getActivePassiveList(@Header("timezone") String timezone,
                                               @Header("locale") String locale,
                                               @Header("accessToken") String accessToken,
                                               @Field("key") String key);

    @FormUrlEncoded
    @POST("job_list_of_user")
    Call<RegisterApiList> getUserJobList(@Header("accessToken") String accessToken,
                                         @Field("key") String key,
                                         @Field("user_id") String user_id);

    @POST("get_job_list")
    Call<RegisterApiList> getJobListing(@Header("accessToken") String accessToken);

    @POST("get_maid_list_at_user_home")
    Call<RegisterApiForMaidList> getMaidList(@Header("accessToken") String accessToken);

    @POST("agency_list")
    Call<RegisterApiList> getAgencyListing(@Header("accessToken") String accessToken);

    @FormUrlEncoded
    @POST("make_favourite_unfavourite_job_by_maid")
    Call<RegisterApiList> makeFavourite(@Header("accessToken") String accessToken,
                                        @Field("key") String key,
                                        @Field("job_list_id") String job_list_id);

    @FormUrlEncoded
    @POST("complete_past_bookings")
    Call<RegisterApi> completePastBooking(@Header("accessToken") String accessToken,
                                          @Field("maid_id") String maid_id,
                                          @Field("status") String status);

    @FormUrlEncoded
    @POST("make_favourite_unfavourite_to_maid_by_user")
    Call<RegisterApiList> makeMaidFavourite(@Header("accessToken") String accessToken,
                                            @Field("key") String key,
                                            @Field("to_maid_id") String to_maid_id);

    @POST("favourite_list_of_maid")
    Call<RegisterApiForList> getFavouriteJobListing(@Header("accessToken") String accessToken);

    @POST("favourite_list_of_user")
    Call<RegisterApiForMaidList> getFavouriteMaidListing(@Header("accessToken") String accessToken);

    @POST("past_maid")
    Call<RegisterApiForMaidList> getPastMaidList(@Header("accessToken") String accessToken);

    @POST("agency_past_booking")
    Call<RegisterApiForMaidList> getAgencyPastBooking(@Header("accessToken") String accessToken);

    @FormUrlEncoded
    @POST("hire_maid")
    Call<RegisterApi> getMaidHired(@Header("accessToken") String accessToken,
                                   @Field("maid_id") String maid_id,
                                   @Field("reason_to_hire") String reason_to_hire);

    @FormUrlEncoded
    @POST("setting")
    Call<RegisterApi> setNotification(@Header("accessToken") String accessToken,
                                      @Field("key") String key,
                                      @Field("notification_status") String notification_status);

    @FormUrlEncoded
    @POST("setting")
    Call<RegisterApi> setPhotoEmail(@Header("accessToken") String accessToken,
                                    @Field("key") String key,
                                    @Field("photo_email_status") String photo_email_status);

    @FormUrlEncoded
    @POST("rating_review")
    Call<RegisterApi> giveRatingsToMaid(@Header("accessToken") String accessToken,
                                        @Field("rating") String rating,
                                        @Field("key") String key,
                                        @Field("rating_to_id") String rating_to_id,
                                        @Field("review") String review);


    @FormUrlEncoded
    @POST("rating_review")
    Call<RegisterApi> giveRatingsToJobPost(@Header("accessToken") String accessToken,
                                           @Field("rating") String rating,
                                           @Field("key") String key,
                                           @Field("rating_to_job_id") String rating_to_job_id,
                                           @Field("review") String review);


    @FormUrlEncoded
    @POST("apply_to_job")
    Call<RegisterApi> applyToJob(@Header("accessToken") String accessToken,
                                 @Field("job_id") String job_id,
                                 @Field("reason_to_apply") String reason_to_apply);

    @FormUrlEncoded
    @POST("apply_to_agency")
    Call<RegisterApi> applyToAgency(@Header("accessToken") String accessToken,
                                    @Field("agency_id") String agency_id,
                                    @Field("reason_to_apply") String reason_to_apply);

    @FormUrlEncoded
    @POST("send_invitaion_to_maid")
    Call<RegisterApi> sendInvitationToMaid(@Header("accessToken") String accessToken,
                                           @Field("maid_id") String maid_id);

    @FormUrlEncoded
    @POST("maid_list_under_agency")
    Call<RegisterApiForMaidList> maidListUnderAgency(@Header("accessToken") String accessToken,
                                                     @Field("agency_id") String agency_id);

    @FormUrlEncoded
    @POST("suggest_maid")
    Call<RegisterApi> suggestMaid(@Header("accessToken") String accessToken,
                                  @Field("user_id") String user_id,
                                  @Field("maid_id") String maid_id,
                                  @Field("reason_to_suggest") String reason_to_suggest,
                                  @Field("job_id") String job_id);

    @FormUrlEncoded
    @POST("videocall_api")
    Call<RegisterApi> videoCall(@Header("accessToken") String accessToken,
                                @Field("user_id") String user_id,
                                @Field("call_text") String call_text,
                                @Field("key") String key);

    @FormUrlEncoded
    @POST("livechat_api")
    Call<RegisterApi> liveChatNotification(@Header("accessToken") String accessToken,
                                           @Field("user_id") String user_id,
                                           @Field("message") String message);

    @FormUrlEncoded
    @POST("fire_maid")
    Call<RegisterApi> fireMaid(@Header("accessToken") String accessToken,
                               @Field("maid_id") String maid_id,
                               @Field("reason_to_fire") String reason_to_fire);


    @FormUrlEncoded
    @POST("request_for_maid_to_agency")
    Call<RegisterApi> requestForMaid(@Header("accessToken") String accessToken,
                                     @Field("agency_id") String agency_id);

    @FormUrlEncoded
    @POST("buyNow")
    Call<RegisterApi> getpaymentGateway(@Header("accessToken") String accessToken,
                                        @Field("offer_id") int offer_id);

    @FormUrlEncoded
    @POST("maidlist-without-signup")
    Call<RegisterApiForMaidList> getMaidListAtHome(@Field("latitude") Double latitude,
                                                   @Field("longitude") Double longitude);

    @FormUrlEncoded
    @POST("joblist-without-signup")
    Call<RegisterApiList> getJobListAtHome(@Field("latitude") Double latitude,
                                           @Field("longitude") Double longitude);

    @FormUrlEncoded
    @POST("get-offer-list")
    Call<RegisterApiList> getOffersList(@Header("accessToken") String accessToken,
                                        @Field("user_type") int user_type);

    @FormUrlEncoded
    @POST("user-details")
    Call<RegisterApi> getUserDetails(@Header("accessToken") String accessToken,
                                     @Field("sender_id") int sender_id);

    @GET("get-wallet")
    Call<RegisterApi> getTotalCredit(@Header("accessToken") String accessToken);


    @FormUrlEncoded
    @POST("pay-credit")
    Call<CreditStatusApi> payCredit(@Header("accessToken") String accessToken,
                                    @Field("credit") String credit,
                                    @Field("user_id") String user_id,
                                    @Field("credit_key") String credit_key);

    @FormUrlEncoded
    @POST("pay-credit")
    Call<CreditStatusApi> payCreditForListing(@Header("accessToken") String accessToken,
                                              @Field("credit") String credit,
                                              @Field("user_id") String user_id,
                                              @Field("credit_key") String credit_key,
                                              @Field("job_id") String job_id);

    @FormUrlEncoded
    @POST("upload-image-by-credit")
    Call<RegisterApi> payCreditForImage(@Header("accessToken") String accessToken,
                                        @Field("credit") int credit,
                                        @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("credit-listing")
    Call<CreditListingApi> getCreditListing(@Header("accessToken") String accessToken,
                                            @Field("key") String key);

    @FormUrlEncoded
    @POST("acceptReject_conversation")
    Call<RegisterApi> sendAcceptRejectRequest(@Header("accessToken") String accessToken,
                                              @Field("user_id") String user_id,
                                              @Field("key") int key);

    @FormUrlEncoded
    @POST("delete_jobpost_by_user")
    Call<RegisterApi> deleteJobPost(@Header("accessToken") String accessToken,
                                    @Field("job_post_id") String job_post_id);

    @FormUrlEncoded
    @POST("complete_job_by_user")
    Call<RegisterApi> completeJobPost(@Header("accessToken") String accessToken,
                                      @Field("job_post_id") String job_post_id);

}
