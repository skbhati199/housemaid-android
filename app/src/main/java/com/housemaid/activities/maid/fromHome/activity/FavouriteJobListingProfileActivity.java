package com.housemaid.activities.maid.fromHome.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.LiveConversationActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.activities.UserJobListingActivity;
import com.housemaid.activities.agency.fromHome.MyMaidsActivity;
import com.housemaid.databinding.ActivityFavouriteJobListingProfileBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.StatusModel;
import com.housemaid.model.UserEducationModel;
import com.housemaid.model.UserImageModel;
import com.housemaid.model.UserLanguageModel;
import com.housemaid.model.UserPetProblemModel;
import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouriteJobListingProfileActivity extends BaseActivity implements View.OnClickListener{

    ActivityFavouriteJobListingProfileBinding binding;
    private SharedPreference sharedPreference;
    SignUpModel userDetailModel;
    ArrayList<UserEducationModel> educationModelArrayList;
    ArrayList<UserLanguageModel> languageModelArrayList;
    ArrayList<UserPetProblemModel> petProblemModelArrayList;
    ArrayList<UserImageModel> imageModelArrayList;
    ArrayList<SignUpModel> jobListing;
    String educationName = "";
    String languageName = "";
    String petProblemName = "";
    PopupWindow pw;
    String callerName;
    private String reasonToApply;
    String senderId;
    String firstCallerName;
    String key;
    String accessToken;
    private String credits;
    private int totalCredits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_favourite_job_listing_profile);
        sharedPreference = SharedPreference.getInstance(this);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");

        binding.toolbar.ivAdd.setVisibility(View.VISIBLE);
        userDetailModel = (SignUpModel) getIntent().getSerializableExtra("favouriteListingDetail");

        educationModelArrayList= userDetailModel.getEducationModels();
        languageModelArrayList = userDetailModel.getLanguageModels();
        petProblemModelArrayList = userDetailModel.getPetProblemModels();
        imageModelArrayList = userDetailModel.getUserDetailModel().getUserImagesModel();

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            callerName = sharedPreference.getString("maid_name", "");
            senderId = sharedPreference.getString("maid_id", "");
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            callerName = sharedPreference.getString("user_name", "");
            senderId = sharedPreference.getString("user_id", "");

        } if (sharedPreference.getInteger("entry_key", 0) == 3) {
            callerName = sharedPreference.getString("agency_name", "");
            senderId = sharedPreference.getString("agency_Id", "");
        }

        String[] a = callerName.split(" ");
        firstCallerName = a[0];

            if (userDetailModel.getUserDetailModel().getPhoto_email_status()==1){
                binding.btnViewEmail.setVisibility(View.GONE);
                binding.btnViewPhoneNumber.setVisibility(View.GONE);
            }else {
                binding.btnViewPhoneNumber.setVisibility(View.VISIBLE);
                binding.btnViewEmail.setVisibility(View.VISIBLE);
            }
            checkPaidStatus();


        setData();

    }

    private void checkPaidStatus() {
        if (userDetailModel.getPaidStatusModel().getMobile_status().equals("1")) {
            binding.btnViewPhoneNumber.setVisibility(View.GONE);
            binding.tvPhoneNumber.setText(userDetailModel.getUserDetailModel().getMobile());
        }
        if (userDetailModel.getPaidStatusModel().getEmail_status().equals("1")) {
            binding.btnViewEmail.setVisibility(View.GONE);
            binding.tvEmail.setText(userDetailModel.getUserDetailModel().getEmail());
        }
    }
    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.ivAdd.setOnClickListener(this);
        binding.btnViewEmail.setOnClickListener(this);
        binding.btnViewPhoneNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                
            
} else if (v.getId() == R.id.ivAdd) {

                callPopUpNoti(binding.toolbar.ivAdd);
                
            
} else if (v.getId() == R.id.tvSuggestMaid) {

                openCreditDialog();
                pw.dismiss();
                

            
} else if (v.getId() == R.id.btnViewPhoneNumber) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "3", "1", "");
                
            
} else if (v.getId() == R.id.btnViewEmail) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "3", "2", "");
                

            
} else if (v.getId() == R.id.tvApplyListing) {

                applyToJobDialog();
                

            
} else if (v.getId() == R.id.tvLiveConversation) {

                if (userDetailModel.getPaidStatusModel().getCall_status().equals("1")){
                    sendNotification(accessToken, String.valueOf(userDetailModel.getUserDetailModel()
                                    .getId()),
                            firstCallerName + "_" + senderId, "0");
                    binding.progress.setVisibility(View.VISIBLE);
                    pw.dismiss();
                }else getCreditListing(accessToken,"4", "7","live");
                

            
} else if (v.getId() == R.id.tvViewUserListing) {

                Intent intent1 = new Intent(FavouriteJobListingProfileActivity.this,
                        UserJobListingActivity.class);
                intent1.putExtra("user_id", userDetailModel.getUser_id());
                startActivity(intent1);
                

        
}
    }

    private void callPopUpNoti(AppCompatImageView ivAdd) {

        LinearLayout delete_layout, edit_layout;
        TextView tvApply, tvRequestLive ,tvViewLisitng, tvSuggestMaid;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_popup, null, false);

        pw = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // display popup
        pw.showAsDropDown(ivAdd, 100, 0, Gravity.BOTTOM);
        tvApply = layout.findViewById(R.id.tvApplyListing);
        tvRequestLive = layout.findViewById(R.id.tvLiveConversation);
        tvViewLisitng = layout.findViewById(R.id.tvViewUserListing);
        tvSuggestMaid = layout.findViewById(R.id.tvSuggestMaid);

        tvApply.setOnClickListener(this);
        tvRequestLive.setOnClickListener(this);
        tvViewLisitng.setOnClickListener(this);
        tvSuggestMaid.setOnClickListener(this);



        if (sharedPreference.getInteger ( "entry_key", 0 ) == 1) {
            tvRequestLive.setVisibility(View.VISIBLE);
            tvApply.setVisibility(View.VISIBLE);
        }        if (sharedPreference.getInteger ( "entry_key", 0 ) == 2) {
            tvRequestLive.setVisibility(View.VISIBLE);
        }        if (sharedPreference.getInteger ( "entry_key", 0 ) == 3) {
            tvRequestLive.setVisibility(View.VISIBLE);
            tvViewLisitng.setVisibility(View.VISIBLE);
            tvSuggestMaid.setVisibility(View.VISIBLE);

        }
        }

    private void openCreditDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_credit_suggest_maid);
        dialog.findViewById(R.id.btnBuyCredit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(FavouriteJobListingProfileActivity.this,
                        UpgradeMemberShipActivity.class));
            }
        });
        dialog.findViewById(R.id.btnSuggest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(FavouriteJobListingProfileActivity.this,
                        MyMaidsActivity.class);
                intent.putExtra("user_Id", String.valueOf(userDetailModel.getUser_id()));
                intent.putExtra("job_Id", String.valueOf(userDetailModel.getId()));
                intent.putExtra("select_maid",1);
                startActivity(intent);

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void applyToJobDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_apply_job_reason);

        final EditText etReason = dialog.findViewById(R.id.etReason);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress);


        dialog.findViewById(R.id.btnApply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reasonToApply = etReason.getText().toString();
                if (!reasonToApply.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    getCreditListing(accessToken,"8", "0","toJob");
                }else Toast.makeText(FavouriteJobListingProfileActivity.this, "" +
                        getString(R.string.please_write_why_you_want_to_apply_for_this_job), Toast.LENGTH_SHORT).show();

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @SuppressLint("DefaultLocale")
    private void setData() {

        //Education Name from ArraListModel
        for (int position=0; position<educationModelArrayList.size() ; position++){

            String  name =educationName + educationModelArrayList.get(position).getEducation_name();
            educationName =name+ ", ";
        }
        educationName = educationName.substring(0,educationName.length()-2);

        //Language Name Name from ArraListModel
        for (int position=0; position<languageModelArrayList.size() ; position++){

            String  name =languageName + languageModelArrayList.get(position).getLanguage_name();
            languageName =name+ ", ";
        }
        languageName = languageName.substring(0,languageName.length()-2);

        //PetProblem Name Name from ArraListModel
        for (int position=0; position<petProblemModelArrayList.size() ; position++){

            String  name =petProblemName + petProblemModelArrayList.get(position).getPet_problem_name();
            petProblemName =name+ ", ";
        }
        petProblemName = petProblemName.substring(0,petProblemName.length()-2);



        binding.toolbar.tvTitle.setText(userDetailModel.getUserDetailModel().getName());
        binding.tvName.setText(userDetailModel.getUserDetailModel().getName());
        binding.tvUserCountry.setText(userDetailModel.getUserDetailModel().getCountry_name());
        binding.tvUserState.setText(userDetailModel.getUserDetailModel().getState_name());
        binding.tvGender.setText(userDetailModel.getUserDetailModel().getGender());
        binding.tvUserMaritalStatus.setText(userDetailModel.getUserDetailModel().getMarital_status());
        binding.tvDob.setText(userDetailModel.getUserDetailModel().getDob());

        Glide.with(this).load(imageModelArrayList.get(0).getImageModel().getBig()).error(R.drawable.avatar)
                .fit().centerCrop().into(binding.ivProfilePicBig);

        binding.tvCity.setText(userDetailModel.getCity_name());
        binding.tvDistrict.setText(userDetailModel.getDistrict_name());
        binding.tvWorkingType.setText(userDetailModel.getWorking_style_name());
        binding.tvExpectedFee.setText(String.format("%d-%d", userDetailModel.getExpected_min_fees(),
                userDetailModel.getExpected_max_fees()));
        binding.tvJobCategory.setText(userDetailModel.getJob_choice_name());
        binding.tvLivesWithFamily.setText(userDetailModel.getLive_with_family());
        binding.tvNationality.setText(userDetailModel.getNationality());
        binding.tvMaritalStatus.setText(userDetailModel.getMarital_status());
        binding.tvkidsStatus.setText(userDetailModel.getKid_status());
        binding.tvHizabStatus.setText(userDetailModel.getHijab());
        binding.tvEducation.setText(educationName);
        binding.tvKnownLaunguage.setText(languageName);
        binding.tvDrivingLicense.setText(userDetailModel.getDriving_licence());
        binding.tvSmoking.setText(userDetailModel.getSmoking());
        binding.tvAlcohol.setText(userDetailModel.getAlcohol());
        binding.tvPetProblem.setText(petProblemName);
        binding.tvDescription.setText(userDetailModel.getDescription());

    }

    private void getCreditListing(final String accessToken, final String key, final String from,
                                  final String fromWhere) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditListingApi> call = apiService.getCreditListing(accessToken, key);
        call.enqueue(new Callback<CreditListingApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditListingApi> call, Response<CreditListingApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    CreditListingApi registerApi = response.body();
                    CreditListingApi.CreditListingModel creditListingModel =
                            registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        credits = creditListingModel.getCredit();
                        callDialogForMoreThenImages(from, fromWhere);

                    } else {

                        Toast.makeText(FavouriteJobListingProfileActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FavouriteJobListingProfileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(FavouriteJobListingProfileActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditListingApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(FavouriteJobListingProfileActivity.this, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void callDialogForMoreThenImages(final String key, final String fromWhere) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_pay_credits);
        dialog.setCancelable(true);
        TextView tvCurrentCredits = dialog.findViewById(R.id.tvCurrentCredits);
        TextView tvPayCredit = dialog.findViewById(R.id.tvPayCredit);
        TextView btnPay = dialog.findViewById(R.id.btnPay);
        TextView btnByCredit = dialog.findViewById(R.id.btnByCredit);
        tvPayCredit.setText(credits);
        tvCurrentCredits.setText(String.valueOf(totalCredits));


        dialog.show();
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                binding.progress.setVisibility(View.VISIBLE);
                payCredit(accessToken, credits, String.valueOf(userDetailModel.getUserDetailModel()
                        .getId()), key, fromWhere);
            }
        });
        btnByCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FavouriteJobListingProfileActivity.this,
                        UpgradeMemberShipActivity.class));
                dialog.dismiss();
            }
        });
    }


    private void payCredit(final String accessToken, String credit, String user_id, final String key,
                           final String fromWhere) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditStatusApi> call = apiService.payCredit(accessToken, credit, user_id, key);
        call.enqueue(new Callback<CreditStatusApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditStatusApi> call, Response<CreditStatusApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    CreditStatusApi registerApi = response.body();
                    StatusModel creditSatusModel = registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        getTotalCredits(accessToken);

                        if (key.equals("1")) {
                            binding.tvPhoneNumber.setText(userDetailModel.getUserDetailModel().getMobile());
                            binding.btnViewPhoneNumber.setVisibility(View.GONE);
                            userDetailModel.getPaidStatusModel().setMobile_status("1");
                            checkPaidStatus();
                        }
                        if (key.equals("2")) {
                            binding.tvEmail.setText(userDetailModel.getUserDetailModel().getEmail());
                            binding.btnViewEmail.setVisibility(View.GONE);
                            userDetailModel.getPaidStatusModel().setEmail_status("1");
                            checkPaidStatus();
                        }
                        if (fromWhere.equals("toJob")){
                            applyToJob(accessToken, String.valueOf(userDetailModel.getId()),
                                    reasonToApply);
                        }
                        if (fromWhere.equals("live")) {

                            sendNotification(accessToken, String.valueOf(userDetailModel.getUserDetailModel()
                                            .getId()),
                                    firstCallerName + "_" + senderId, "0");
                            binding.progress.setVisibility(View.VISIBLE);
                            pw.dismiss();
                        }

                    } else {

                        Toast.makeText(FavouriteJobListingProfileActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FavouriteJobListingProfileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(FavouriteJobListingProfileActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditStatusApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(FavouriteJobListingProfileActivity.this, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }private void applyToJob(String access_token, String job_id, String reasonToApply) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.applyToJob(access_token, job_id,
                reasonToApply);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                pw.dismiss();
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        Toast.makeText(FavouriteJobListingProfileActivity.this, R.string.applied_successfully,
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FavouriteJobListingProfileActivity.this, HomeForMaidActivity.class));
                        finish();

                    } else {

                        Toast.makeText(FavouriteJobListingProfileActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FavouriteJobListingProfileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(FavouriteJobListingProfileActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                pw.dismiss();
                Toast.makeText(FavouriteJobListingProfileActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotalCredits(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getTotalCredit(accessToken);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    SignUpModel signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", signUpModel.getTotal_credit());
                        totalCredits = signUpModel.getTotal_credit();

                    } else {

                        Toast.makeText(FavouriteJobListingProfileActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FavouriteJobListingProfileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(FavouriteJobListingProfileActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                Toast.makeText(FavouriteJobListingProfileActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void sendNotification(String access_token, String user_id, String call_text,
                                  final String key) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.videoCall(access_token, user_id, call_text, key);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    SignUpModel signUpModel = response.body().getSignUpModel();
                    String message = response.body().getMessage();
                    if (message != null) {
                        if (key.equals("0")){
                            userDetailModel.getPaidStatusModel().setCall_status("1");
                        }
                        if (signUpModel.getCall_status().equals("0")) {
                            Toast.makeText(FavouriteJobListingProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else if (signUpModel.getCall_status().equals("1")) {
                            Intent intent1 = new Intent(FavouriteJobListingProfileActivity.this,
                                    LiveConversationActivity.class);
                            intent1.putExtra("caller_id", userDetailModel.getUserDetailModel()
                                    .getId());
                            intent1.putExtra("to_caller_name", userDetailModel
                                    .getUserDetailModel().getName());
                            intent1.putExtra("profileToCaller",
                                    userDetailModel.getUserDetailModel().getUserImagesModel().get(0)
                                            .getImageModel().getSmall());
                            intent1.putExtra("calling_key", 1);
                            intent1.putExtra("channel_name", firstCallerName + "_" +
                                    senderId);
                            startActivity(intent1);
                            pw.dismiss();

                        } else
                            Toast.makeText(FavouriteJobListingProfileActivity.this,
                                    R.string.your_call_request_has_been_rejected,
                                    Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(FavouriteJobListingProfileActivity.this, "Fails"
                                , Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FavouriteJobListingProfileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(FavouriteJobListingProfileActivity.this, response.errorBody().string()
                                    , Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(FavouriteJobListingProfileActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

}
