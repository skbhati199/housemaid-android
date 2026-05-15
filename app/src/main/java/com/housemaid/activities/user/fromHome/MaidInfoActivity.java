package com.housemaid.activities.user.fromHome;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
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

import com.google.gson.Gson;
import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.LiveConversationActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.activities.ZoomPhotoActivity;
import com.housemaid.activities.agency.fromHome.MyMaidsActivity;
import com.housemaid.activities.agency.fromHome.OtherMaidsProfileActivity;
import com.housemaid.adapter.ViewPageAdapter;
import com.housemaid.databinding.ActivityMaidInfoBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.StatusModel;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.DateConvertUtils;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MaidInfoActivity extends BaseActivity implements View.OnClickListener {

    ActivityMaidInfoBinding binding;
    private SharedPreference sharedPreference;
    private UserDetailModel maidInformation;
    private SignUpModel signUpModel;
    private StatusModel creditSatusModel;
    private String educationName = "";
    private String languageName = "";
    private String petProblemName = "";
    private String jobChoiceName = "";
    private String workingStyleName = "";
    private String workSkillsName = "";
    private String workingDistrict = "";
    private PopupWindow pw;
    private String accessToken;
    private String reasonToFire;
    private ProgressBar progressBar;
    private Dialog dialog;
    private String reasonToHire;
    private String firstCallerName;
    private String callerName;
    private String senderId;
    private int totalCredits;
    private ArrayList<String> images;
    private ViewPager viewPager;
    private String credits;
    private int disableKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maid_info);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);
        disableKey = sharedPreference.getInteger("disable_key", 1);

        if (disableKey == 1) {
            Toast.makeText(this, R.string.disable_quote, Toast.LENGTH_LONG).show();
            binding.btnViewPhoneNumber.setEnabled(false);
            binding.btnViewEmail.setEnabled(false);
            binding.toolbar.ivAdd.setEnabled(false);
        }

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            callerName = sharedPreference.getString("maid_name", "");
            senderId = sharedPreference.getString("maid_id", "");
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            callerName = sharedPreference.getString("user_name", "");
            senderId = sharedPreference.getString("user_id", "");

        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            callerName = sharedPreference.getString("agency_name", "");
            senderId = sharedPreference.getString("agency_Id", "");
        }

        String[] a = callerName.split(" ");
        firstCallerName = a[0];

        if (Objects.equals(sharedPreference.getString("list_type", ""), "normal")) {
            maidInformation = (UserDetailModel) getIntent().getSerializableExtra("maidDetail");
        }
        if (Objects.equals(sharedPreference.getString("list_type", ""), "favourite")) {
            maidInformation = (UserDetailModel) getIntent().getSerializableExtra("favouriteListingDetail");
        }

        binding.toolbar.tvTitle.setText(maidInformation.getName());

        String accessFrom = getIntent().getStringExtra("accessFrom");
        if (accessFrom.equals("")) {
            binding.toolbar.ivAdd.setVisibility(View.VISIBLE);

            if (maidInformation.getPhoto_email_status() == 1) {
                binding.btnViewEmail.setVisibility(View.GONE);
                binding.btnViewPhoneNumber.setVisibility(View.GONE);

            } else {
                binding.btnViewEmail.setVisibility(View.VISIBLE);
                binding.btnViewPhoneNumber.setVisibility(View.VISIBLE);
            }
            checkPaidStatus();
        } else {
            binding.tvPhoneNumber.setText(maidInformation.getMobile());
            binding.tvEmail.setText(maidInformation.getEmail());
        }
        setData();
    }

    private void checkPaidStatus() {
        if (maidInformation.getPaidStatusModel().getMobile_status().equals("1")) {
            binding.btnViewPhoneNumber.setVisibility(View.GONE);
            binding.tvPhoneNumber.setText(maidInformation.getMobile());
            binding.btnViewEmail.setVisibility(View.GONE);
            binding.tvEmail.setText(maidInformation.getEmail());

        }
        if (maidInformation.getPaidStatusModel().getEmail_status().equals("1")) {
            binding.btnViewEmail.setVisibility(View.GONE);
            binding.tvEmail.setText(maidInformation.getEmail());
            binding.btnViewPhoneNumber.setVisibility(View.GONE);
            binding.tvPhoneNumber.setText(maidInformation.getMobile());
        }
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.ivAdd.setOnClickListener(this);
        binding.btnViewPhoneNumber.setOnClickListener(this);
        binding.btnViewEmail.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                
            
} else if (v.getId() == R.id.ivAdd) {

                callPopUpNoti(binding.toolbar.ivAdd);
                
            
} else if (v.getId() == R.id.btnViewPhoneNumber) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "3", "1", "");
                
            
} else if (v.getId() == R.id.btnViewEmail) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "3", "2", "");
                

            
} else if (v.getId() == R.id.tvInvite) {

                binding.progress.setVisibility(View.VISIBLE);
                popupWindowMenuClick(accessToken, String.valueOf(maidInformation.getId()),
                        reasonToFire, progressBar, dialog);
                

            
} else if (v.getId() == R.id.tvFire) {

                openFireDialog();
                pw.dismiss();
                

            
} else if (v.getId() == R.id.tvHire) {

                if (maidInformation.getIs_hired())
                    Toast.makeText(this, R.string.hire_request_already_sent,
                            Toast.LENGTH_SHORT).show();
                else openHireDialog();

                pw.dismiss();
                

            
} else if (v.getId() == R.id.tvLiveConversation) {

                if (maidInformation.getPaidStatusModel().getCall_status().equals("1")) {

                    sendNotification(accessToken, String.valueOf(maidInformation.getId()),
                            firstCallerName + "_" + senderId, "0");
                    binding.progress.setVisibility(View.VISIBLE);
                } else {
                    binding.progress.setVisibility(View.VISIBLE);
                    getCreditListing(accessToken, "4", "7", "live");
                    pw.dismiss();
                }

                

            
} else if (v.getId() == R.id.viewPager) {

                startActivity(new Intent(this, ZoomPhotoActivity.class)
                                .putStringArrayListExtra("images", images));
                

        
}
    }

    private void callDialogForMoreThenImages(final String key, final String fromWhere) {
        binding.progress.setVisibility(View.GONE);
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
                payCredit(accessToken, credits, String.valueOf(maidInformation.getId()), key, fromWhere);
            }
        });
        btnByCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MaidInfoActivity.this,
                        UpgradeMemberShipActivity.class));
                dialog.dismiss();
            }
        });
    }

    private void callPopUpNoti(AppCompatImageView ivAdd) {
        LinearLayout delete_layout, edit_layout;
        TextView tvLiveConversation, tvInvite, tvFire, tvHire;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_popup, null, false);

        pw = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // display popup
        pw.showAsDropDown(ivAdd, 100, 0, Gravity.BOTTOM);
        tvLiveConversation = layout.findViewById(R.id.tvLiveConversation);
        tvInvite = layout.findViewById(R.id.tvInvite);
        tvFire = layout.findViewById(R.id.tvFire);
        tvHire = layout.findViewById(R.id.tvHire);

        tvLiveConversation.setVisibility(View.VISIBLE);


        tvFire.setOnClickListener(this);
        tvInvite.setOnClickListener(this);
        tvLiveConversation.setOnClickListener(this);
        tvHire.setOnClickListener(this);

        if (sharedPreference.getInteger("entry_key", 0) == 3) {

            if (sharedPreference.getInteger("popup_key", 0) == 1) {
                tvFire.setVisibility(View.VISIBLE);
            }
            if (sharedPreference.getInteger("popup_key", 0) == 2) {

                if (maidInformation.getIs_invited_by_agency() == 0) {
                    tvInvite.setVisibility(View.VISIBLE);
                }

            }
        } else tvHire.setVisibility(View.VISIBLE);

    }

    private void openFireDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_fire_maid_reason);

        final EditText etReason = dialog.findViewById(R.id.etReason);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress);


        dialog.findViewById(R.id.btnFire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonToFire = etReason.getText().toString();
                if (!reasonToFire.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    popupWindowMenuClick(accessToken, String.valueOf(maidInformation.getId()),
                            reasonToFire, progressBar, dialog);
                    dialog.dismiss();
                } else Toast.makeText(MaidInfoActivity.this, "" +
                        getString(R.string.please_write_why_you_fire_this_maid), Toast.LENGTH_SHORT).show();


            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    private void openHireDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_hire_maid_reason);

        final EditText etReason = dialog.findViewById(R.id.etReason);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress);

        dialog.findViewById(R.id.btnHire).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonToHire = etReason.getText().toString();
                if (!reasonToHire.isEmpty()) {
                    binding.progress.setVisibility(View.VISIBLE);
                    getCreditListing(accessToken, "5", "0", "hire");
                    dialog.dismiss();
                } else Toast.makeText(MaidInfoActivity.this, "" +
                        getString(R.string.please_write_why_you_hire_this_maid), Toast.LENGTH_SHORT).show();

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }

    }

    private void setData() {

        binding.ratingMaid.setRating(maidInformation.getRating());

        if (maidInformation.getAgency_name() == null) {
            binding.tvAgency.setText(R.string.no_agency);
        } else binding.tvAgency.setText(maidInformation.getAgency_name());

        images = new ArrayList<>();

        for (int i = 0; i < maidInformation.getMaidImageModel().size(); i++) {
            images.add(maidInformation.getMaidImageModel().get(i).getImageModel().getBig());
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        ViewPageAdapter viewPagerAdapter = new ViewPageAdapter(this, images);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnClickListener(this);

        binding.tvName.setText(maidInformation.getName());
        binding.tvCountry.setText(maidInformation.getCountry_name());
        binding.tvState.setText(maidInformation.getState_name());
        binding.tvDob.setText(maidInformation.getDob());
        binding.tvGender.setText(maidInformation.getGender());
        binding.tvNationality.setText(maidInformation.getNationality());
        binding.tvKidsStatus.setText(maidInformation.getKids());
        binding.tvHijab.setText(maidInformation.getHi_job());
        binding.tvTotalExperience.setText(maidInformation.getTotal_experience() == 0 ? "No experience" :
                DateConvertUtils.daysToYear(maidInformation.getTotal_experience()));
        //Education Name from ArrayListModel
        if (maidInformation.getUserEducationModel().size() > 0) {
            for (int position = 0; position < maidInformation.getUserEducationModel().size(); position++) {

                String name = educationName + maidInformation.getUserEducationModel().get(position).getEducationDetailModel().getName();
                educationName = name + ", ";
            }
            educationName = educationName.substring(0, educationName.length() - 2);
        }
        //Language Name from ArrayListModel
        if (maidInformation.getUserLanguageModel().size() > 0) {
            for (int position = 0; position < maidInformation.getUserLanguageModel().size(); position++) {

                String name = languageName + maidInformation.getUserLanguageModel().get(position).getLanguage_detail().getName();
                languageName = name + ", ";
            }
            languageName = languageName.substring(0, languageName.length() - 2);
        }
        //PetProblem Name from ArrayListModel
        if (maidInformation.getUserPetProblemModel().size() > 0) {

            for (int position = 0; position < maidInformation.getUserPetProblemModel().size(); position++) {

                String name = petProblemName + maidInformation.getUserPetProblemModel().get(position).getPetProblemDetailModel().getName();
                petProblemName = name + ", ";
            }
            petProblemName = petProblemName.substring(0, petProblemName.length() - 2);
        }
        //JobChoice Name from ArraListModel
        if (maidInformation.getMaidJobChoiceModels().size() > 0) {

            for (int position = 0; position < maidInformation.getMaidJobChoiceModels().size(); position++) {

                String name = jobChoiceName + maidInformation.getMaidJobChoiceModels().get(position)
                        .getJob_choice_detail().getName();
                jobChoiceName = name + ", ";
            }
            jobChoiceName = jobChoiceName.substring(0, jobChoiceName.length() - 2);
        }
        //workingStyleName  from ArrayListModel
        if (maidInformation.getMaidWorkingStyleModels().size() > 0) {

            for (int position = 0; position < maidInformation.getMaidWorkingStyleModels().size(); position++) {

                String name = workingStyleName + maidInformation.getMaidWorkingStyleModels().get(position).getWorking_style_detail().getName();
                workingStyleName = name + ", ";
            }
            workingStyleName = workingStyleName.substring(0, workingStyleName.length() - 2);
        }
        //workSkillsName  from ArrayListModel
        if (maidInformation.getMaidSkillsModels().size() > 0) {

            for (int position = 0; position < maidInformation.getMaidSkillsModels().size(); position++) {

                String name = workSkillsName + maidInformation.getMaidSkillsModels().get(position).getSkill_detail().getName();
                workSkillsName = name + ", ";
            }
            workSkillsName = workSkillsName.substring(0, workSkillsName.length() - 2);
        }
        //WorkingDistrictName  from ArrayListModel
        if (maidInformation.getMaidWorkingDistrictModels().size() > 0) {

            for (int position = 0; position < maidInformation.getMaidWorkingDistrictModels().size(); position++) {

                String name = workingDistrict + maidInformation.getMaidWorkingDistrictModels().get(position).getDistrict_detail().getName();
                workingDistrict = name + ", ";
            }
            workingDistrict = workingDistrict.substring(0, workingDistrict.length() - 2);
        }

        binding.tvEducation.setText(educationName);
        binding.tvKnownLaunguage.setText(languageName);
        binding.tvWorkStatus.setText(maidInformation.getWork_status());
        binding.tvDrivingLicence.setText(maidInformation.getDriving_licence());
        binding.tvSmoking.setText(maidInformation.getSmoke());
        binding.tvAlcohol.setText(maidInformation.getAlcohol());
        binding.tvPetProblem.setText(petProblemName);
        binding.tvCountryWork.setText(maidInformation.getMaid_can_work_country_name());

        if (maidInformation.getMaidWorkingStatesModels() == null &&
                maidInformation.getMaidWorkingStatesModels().get(0).getState_detail().getName() == null) {
            binding.tvStateWork.setText("No state");
        } else
            binding.tvStateWork.setText(maidInformation.getMaidWorkingStatesModels().isEmpty() ? "No States" :
                    maidInformation.getMaidWorkingStatesModels().get(0).getState_detail().getName());
        binding.tvDistrictWork.setText(maidInformation.getMaidWorkingDistrictModels().isEmpty() ? "No District" :
                workingDistrict);

        if (maidInformation.getMaidWorkingCityModels() != null
                && !maidInformation.getMaidWorkingCityModels().isEmpty()) {
            binding.tvCityWork.setText(maidInformation.getMaidWorkingCityModels().get(0).getCity_detail() == null ? "No City" :
                    maidInformation.getMaidWorkingCityModels().get(0).getCity_detail().getName());
        }
        binding.tvJobChoice.setText(maidInformation.getMaidJobChoiceModels().isEmpty() ? "No Job Choice" :
                jobChoiceName);

        binding.tvWorkingType.setText(maidInformation.getMaidWorkingStyleModels().isEmpty() ? "No Style" :
                workingStyleName);

        binding.tvLiveFamily.setText(maidInformation.getCan_live_with_family());
        binding.tvTravelSituation.setText(maidInformation.getTravel_situation());
        binding.tvExpectedFee.setText(maidInformation.getExpected_fees() + "" + maidInformation.getFees_currency());
        binding.tvDescriptionEducation.setText(maidInformation.getMaid_education() == null ? "No data" :
                maidInformation.getMaid_education());
        binding.tvDescriptionCertificate.setText(maidInformation.getMaid_certificate() == null ? "No data" :
                maidInformation.getMaid_certificate());
        binding.tvAboutMe.setText(maidInformation.getMaid_about_me() == null ? "No data" :
                maidInformation.getMaid_about_me());
        binding.tvSkills.setText(maidInformation.getMaidSkillsModels().isEmpty() ? "No Skill" :
                workSkillsName);

        if (maidInformation.getMaidWorkExperiencesModel() != null &&
                !maidInformation.getMaidWorkExperiencesModel().isEmpty()) {

            if (maidInformation.getMaidWorkExperiencesModel().size() == 1) {
                binding.tvDescriptionWorkExperience1.setText(maidInformation.getMaidWorkExperiencesModel()
                        .get(0).getDetail() + ", from " +
                        maidInformation.getMaidWorkExperiencesModel().get(0).getStart_date() + " to " +
                        (maidInformation.getMaidWorkExperiencesModel().get(0).getEnd_date() == null
                                ? "still working" : maidInformation.getMaidWorkExperiencesModel().get(0).getEnd_date()));
            }
            if (maidInformation.getMaidWorkExperiencesModel().size() == 2) {
                binding.tvDescriptionWorkExperience1.setText(maidInformation.getMaidWorkExperiencesModel()
                        .get(0).getDetail() + ", from " +
                        maidInformation.getMaidWorkExperiencesModel().get(0).getStart_date() + " to " +
                        (maidInformation.getMaidWorkExperiencesModel().get(0).getEnd_date() == null
                                ? "still working" : maidInformation.getMaidWorkExperiencesModel().get(0).getEnd_date()));

                binding.tvDescriptionWorkExperience2.setText(maidInformation.getMaidWorkExperiencesModel()
                        .get(1).getDetail() + ", from " +
                        maidInformation.getMaidWorkExperiencesModel().get(1).getStart_date() + " to " +
                        (maidInformation.getMaidWorkExperiencesModel().get(1).getEnd_date() == null
                                ? "still working" : maidInformation.getMaidWorkExperiencesModel().get(1).getEnd_date()));
            }
            if (maidInformation.getMaidWorkExperiencesModel().size() > 2) {
                binding.tvDescriptionWorkExperience1.setText(maidInformation.getMaidWorkExperiencesModel()
                        .get(0).getDetail() + ", from " +
                        maidInformation.getMaidWorkExperiencesModel().get(0).getStart_date() + " to " +
                        (maidInformation.getMaidWorkExperiencesModel().get(0).getEnd_date() == null
                                ? "still working" : maidInformation.getMaidWorkExperiencesModel().get(0).getEnd_date()));

                binding.tvDescriptionWorkExperience2.setText(maidInformation.getMaidWorkExperiencesModel()
                        .get(1).getDetail() + ", from " +
                        maidInformation.getMaidWorkExperiencesModel().get(1).getStart_date() + " to " +
                        (maidInformation.getMaidWorkExperiencesModel().get(1).getEnd_date() == null
                                ? "still working" : maidInformation.getMaidWorkExperiencesModel().get(1).getEnd_date()));

                binding.tvDescriptionWorkExperience3.setText(maidInformation.getMaidWorkExperiencesModel()
                        .get(2).getDetail() + ", from "
                        + maidInformation.getMaidWorkExperiencesModel().get(2).getStart_date() + " to " +
                        (maidInformation.getMaidWorkExperiencesModel().get(2).getEnd_date() == null
                                ? "still working" : maidInformation.getMaidWorkExperiencesModel().get(2).getEnd_date()));
            }

        } else binding.tvDescriptionWorkExperience1.setText("No Experience");


    }

    private void getMaidHired(String access_token, String maid_id, String reason_to_hire, final Dialog dialog) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.getMaidHired(access_token, maid_id, reason_to_hire);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    SignUpModel registerApi = response.body().getSignUpModel();

                    String message = registerApi.getMessage();
                    if (message != null) {

                        Toast.makeText(MaidInfoActivity.this, R.string.the_requent_has_been_sent, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MaidInfoActivity.this, HomeUserActivity.class));

                    } else {
                        dialog.dismiss();
                        Toast.makeText(MaidInfoActivity.this, R.string.registration_fails, Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MaidInfoActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(MaidInfoActivity.this, "" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(MaidInfoActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void popupWindowMenuClick(String access_token, String maid_id, String reasonToFire,
                                      final ProgressBar progressBar, final Dialog dialog) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        if (sharedPreference.getInteger("popup_key", 0) == 1) {
            call = apiService.fireMaid(access_token, maid_id, reasonToFire);
        } else call = apiService.sendInvitationToMaid(access_token, maid_id);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                pw.dismiss();
                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.getMessage();
                    if (message != null) {

                        if (sharedPreference.getInteger("popup_key", 0) == 1) {

                            Toast.makeText(MaidInfoActivity.this,
                                    "Maid Fired", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            dialog.dismiss();
                            startActivity(new Intent(MaidInfoActivity.this, MyMaidsActivity.class));
                            finish();


                        } else {
                            Toast.makeText(MaidInfoActivity.this,
                                    R.string.invitation_sent_successfully, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MaidInfoActivity.this, OtherMaidsProfileActivity.class));
                            finish();
                        }

                    } else {

                        Toast.makeText(MaidInfoActivity.this, getString(R.string.registration_fails)
                                , Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {

                        Toast.makeText(MaidInfoActivity.this, "" + response.errorBody().string(),
                                Toast.LENGTH_LONG).show();
                        Log.d("TEST", R.string.error + response.errorBody().string() +
                                R.string.message_ + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                pw.dismiss();
                Toast.makeText(MaidInfoActivity.this, R.string.error + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

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
                    creditSatusModel = registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        getTotalCredits(accessToken);

                        if (key.equals("1")) {
                            binding.tvPhoneNumber.setText(maidInformation.getMobile());
                            binding.btnViewPhoneNumber.setVisibility(View.GONE);
                            binding.tvEmail.setText(maidInformation.getEmail());
                            binding.btnViewEmail.setVisibility(View.GONE);
                            checkPaidStatus();
                        }
                        if (key.equals("2")) {
                            binding.tvEmail.setText(maidInformation.getEmail());
                            binding.btnViewEmail.setVisibility(View.GONE);
                            binding.tvPhoneNumber.setText(maidInformation.getMobile());
                            binding.btnViewPhoneNumber.setVisibility(View.GONE);
                            checkPaidStatus();
                        }
                        if (fromWhere.equals("hire")) {
                            getMaidHired(accessToken, String.valueOf(maidInformation.getId()),
                                    reasonToHire, dialog);
                        }
                        if (fromWhere.equals("live")) {
                            sendNotification(accessToken, String.valueOf(maidInformation.getId()),
                                    firstCallerName + "_" + senderId, "0");
                            binding.progress.setVisibility(View.VISIBLE);
                            pw.dismiss();
                        }


                    } else {

                        Toast.makeText(MaidInfoActivity.this,
                                R.string.no_response, Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MaidInfoActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        }
                        else if (response.code() == 400){

                            Toast.makeText(MaidInfoActivity.this,"You have insufficient balance",
                                    Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(MaidInfoActivity.this, response.message(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", getText(R.string.error) + response.errorBody().string()
                                    + getText(R.string.message_) + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditStatusApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(MaidInfoActivity.this, getText(R.string.error)
                        + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

                        Toast.makeText(MaidInfoActivity.this, getText(R.string.no_response),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MaidInfoActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(MaidInfoActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", R.string.error + response.errorBody().string()
                                    + R.string.message_ + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditListingApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(MaidInfoActivity.this, R.string.error + t.getMessage(),
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
                    signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", signUpModel.getTotal_credit());
                        totalCredits = signUpModel.getTotal_credit();

                    } else {

                        Toast.makeText(MaidInfoActivity.this,
                                R.string.no_response, Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MaidInfoActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(MaidInfoActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", R.string.error + response.errorBody().string()
                                    + R.string.message_ + response.message());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                Toast.makeText(MaidInfoActivity.this, R.string.error + t.getMessage(),
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
                        if (key.equals("0")) {
                            maidInformation.getPaidStatusModel().setCall_status("1");
                        }

                        if (signUpModel.getCall_status().equals("0")) {
                            Toast.makeText(MaidInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else if (signUpModel.getCall_status().equals("1")) {
                            Intent intent = new Intent(MaidInfoActivity.this,
                                    LiveConversationActivity.class);
                            intent.putExtra("caller_id", maidInformation.getId());
                            intent.putExtra("to_caller_name", maidInformation.getName());
                            intent.putExtra("calling_key", 1);
                            intent.putExtra("profileToCaller",
                                    maidInformation.getMaidImageModel().get(0).getImageModel().getSmall());
                            intent.putExtra("channel_name", firstCallerName + "_" + senderId);
                            startActivity(intent);
                            pw.dismiss();
                        } else
                            Toast.makeText(MaidInfoActivity.this, "Your Call request has been rejected",
                                    Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(MaidInfoActivity.this, "Fails"
                                , Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MaidInfoActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(MaidInfoActivity.this, response.errorBody().string()
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
                Toast.makeText(MaidInfoActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}