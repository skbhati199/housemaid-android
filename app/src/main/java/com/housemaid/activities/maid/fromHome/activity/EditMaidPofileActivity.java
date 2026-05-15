package com.housemaid.activities.maid.fromHome.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.agency.fromHome.EditAgencyPofileActivity;
import com.housemaid.activities.dataList.CitylistActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.DistrictListActivity;
import com.housemaid.activities.dataList.EducationListActivity;
import com.housemaid.activities.dataList.JobChoiceListActivity;
import com.housemaid.activities.dataList.LanguageListActivity;
import com.housemaid.activities.dataList.NationalityListActivity;
import com.housemaid.activities.dataList.PetProblemListActivity;
import com.housemaid.activities.dataList.SkillsListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.activities.dataList.WorkingchoicesListActivity;
import com.housemaid.adapter.ImageAdapter3;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityEditMaidPofileBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;
import com.imagepicker.FilePickUtils;
import com.imagepicker.LifeCycleCallBackManager;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.imagepicker.FilePickUtils.CAMERA_PERMISSION;
import static com.imagepicker.FilePickUtils.STORAGE_PERMISSION_IMAGE;

public class EditMaidPofileActivity extends BaseActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ActivityEditMaidPofileBinding binding;
    private int count = 0;
    private int count2 = 0;
    private Dialog dialog;
    private MultipartBody.Part profileImage;
    private Map<String, RequestBody> userDetailMap;
    private SignUpModel maidDetailModel;
    private SharedPreference sharedPreference;
    private String accessToken;
    private int id;
    private int stateID;
    private int nationalityID;
    private String gender;
    boolean setGender = false;
    private String name;
    private String mariStatus;
    private String kidsStatus;
    private String hijabStatus;
    private String maritalStatus[];
    private String kidStatus[];
    private String hijab[];
    private String liveWithFamily[];
    private String travel[];
    private String amount[];
    private String workStatus[];
    private String drivingLicence[];
    private String smoke[];
    private String alcohol[];
    private String travelStatus;
    private String amountStatus;
    private String familyStatus;
    private String drivingStatus;
    private String work;
    private String smokeStatus;
    private String alcoholStatus;

    private ArrayList<String> educationNameList;
    private ArrayList<String> educationIdList = new ArrayList<>();
    private ArrayList<String> languageNameList;
    private ArrayList<String> languageIdList = new ArrayList<>();
    private ArrayList<String> petProblemList;
    private ArrayList<String> petProblemIdList = new ArrayList<>();
    private ArrayList<String> countryIdList = new ArrayList<>();
    private ArrayList<String> districtList;
    private ArrayList<String> districtIDList = new ArrayList<>();
    private ArrayList<String> cityList;
    private ArrayList<String> cityIdList = new ArrayList<>();
    private ArrayList<String> stateIdList = new ArrayList<>();
    private ArrayList<String> jobChoiceList;
    private ArrayList<String> joChoiceIdList = new ArrayList<>();
    private ArrayList<String> skillsList;
    private ArrayList<String> skillsIdList = new ArrayList<>();
    private ArrayList<String> workingList;
    private ArrayList<String> workingIdList = new ArrayList<>();
    private int startYear;
    private int startMonth;
    private String start1;
    private String end1;
    private String start2;
    private String end2;
    private String start3;
    private String end3;
    private ArrayList<String> startDate;
    private ArrayList<String> endDate;
    private ArrayList<String> workExperiences;
    private ArrayList<String> stillWork;

    private String educationName = "";
    private String petProblemName = "";
    private String languageName = "";
    private String workingStyleName = "";
    private String workSkillsName = "";
    private String workingDistrict = "";
    private String workingCountry = "";
    private String jobChoiceName = "";
    private String cityName = "";
    private String userId;
    private ArrayList<String> imageList;
    private ArrayList<String> bigImageList;
    private ArrayList<Integer> imageIdList;
    private int position;
    private ImageAdapter3 imageAdapter;
    private FilePickUtils filePickUtils;
    private LifeCycleCallBackManager lifeCycleCallBackManager;
    private int checkState = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_maid_pofile);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        filePickUtils = new FilePickUtils(this, onFileChoose);
        lifeCycleCallBackManager = filePickUtils.getCallBackManager();
        binding.toolbar.ivBack.setVisibility(View.VISIBLE);
        binding.toolbar.tvTitle.setText(R.string.edit_profile);
        sharedPreference = SharedPreference.getInstance(this);
        maidDetailModel = (SignUpModel) getIntent().getSerializableExtra("maidDetail");
        accessToken = sharedPreference.getString("signUp_token", "0");
        gender = maidDetailModel.getGender();

        maritalStatus = getResources().getStringArray(R.array.maritalStatus);
        showMaritalStatus();
        kidStatus = getResources().getStringArray(R.array.kidStatus);
        showkidStatus();
        hijab = getResources().getStringArray(R.array.hijab);
        showHijabStatus();
        workStatus = getResources().getStringArray(R.array.workStatus);
        showWorkStatus();
        drivingLicence = getResources().getStringArray(R.array.drivingLicence);
        showLicenceStatus();
        smoke = getResources().getStringArray(R.array.smoke);
        showSmokeStatus();
        alcohol = getResources().getStringArray(R.array.smoke);
        showAlcoholStatus();
        workStatus = getResources().getStringArray(R.array.workStatus);
        showWorkStatus();
        drivingLicence = getResources().getStringArray(R.array.drivingLicence);
        showLicenceStatus();
        smoke = getResources().getStringArray(R.array.smoke);
        showSmokeStatus();
        alcohol = getResources().getStringArray(R.array.smoke);
        showAlcoholStatus();

        liveWithFamily = getResources().getStringArray(R.array.Family);
        showLiveFamily();
        travel = getResources().getStringArray(R.array.TravelSituation);
        showTravelSituation();
        amount = getResources().getStringArray(R.array.amount);
        showamountSituation();

        stillWork = new ArrayList<>();
        workExperiences = new ArrayList<>();
        startDate = new ArrayList<>();
        endDate = new ArrayList<>();


        imageList = new ArrayList<>();
        imageIdList = new ArrayList<>();
        bigImageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            imageIdList.add(0);
        }
        if (maidDetailModel != null) {
            if (maidDetailModel.getUserImagesModel().isEmpty()) {
                binding.ivProfilePicBig.setImageResource(R.drawable.user);
                bigImageList.add(getResources().getDrawable(R.drawable.user).toString());
                imageList.add(getResources().getDrawable(R.drawable.user).toString());

            } else {
                if (maidDetailModel.getUserImagesModel().size() > 0) {
                    for (int i = 1; i <= maidDetailModel.getUserImagesModel().size(); i++) {
                        imageIdList.set(i - 1, maidDetailModel.getUserImagesModel().get(i - 1).getId());
                    }
                }
            }
            setData();
        } else Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
        userId = sharedPreference.getString("maid_id", "");

    }

    private void setData() {

        if (Objects.equals(maidDetailModel.getGender(), "male")) {
            setGender = true;
            binding.tvMaleColor.setVisibility(View.VISIBLE);
        } else {
            binding.tvFemaleColor.setVisibility(View.VISIBLE);

        }

        if (!(maidDetailModel.getUserImagesModel().isEmpty()) && (maidDetailModel.getUserImagesModel().size() > 0)
                && !maidDetailModel.getUserImagesModel().get(0).getImageModel().getBig().isEmpty()) {
            for (int i = 1; i <= maidDetailModel.getUserImagesModel().size(); i++) {
                imageList.add(maidDetailModel.getUserImagesModel().get(i - 1).getImageModel().getSmall());
                bigImageList.add(maidDetailModel.getUserImagesModel().get(i - 1).getImageModel()
                        .getBig());
            }
            Glide.with(itemView.getContext()).load(maidDetailModel.getUserImagesModel().get(0).getImageModel().getBig())
                    .error(R.drawable.user).into(binding.ivProfilePicBig);

        }
        imageAdapter = new ImageAdapter3(EditMaidPofileActivity.this, imageList, bigImageList
                , binding, imageIdList);
        binding.rvImageList.setLayoutManager(new LinearLayoutManager(EditMaidPofileActivity.this,
                RecyclerView.HORIZONTAL, false));
        binding.rvImageList.setAdapter(imageAdapter);


        if (maidDetailModel.getUserEducationModel().size() > 0) {


            for (int position = 0; position < maidDetailModel.getUserEducationModel().size(); position++) {

                educationIdList.add(String.valueOf(maidDetailModel.getUserEducationModel().get(position).getEducationDetailModel().getId()));

                String name = educationName + maidDetailModel.getUserEducationModel().get(position).getEducationDetailModel().getName();
                educationName = name + ", ";
            }
            educationName = educationName.substring(0, educationName.length() - 2);
        }
        //Language Name from ArrayListModel
        if (maidDetailModel.getUserLanguageModel().size() > 0) {
            for (int position = 0; position < maidDetailModel.getUserLanguageModel().size(); position++) {

                languageIdList.add(String.valueOf(maidDetailModel.getUserLanguageModel().get(position).getLanguage_detail().getId()));

                String name = languageName + maidDetailModel.getUserLanguageModel().get(position).getLanguage_detail().getName();
                languageName = name + ", ";
            }
            languageName = languageName.substring(0, languageName.length() - 2);
        }
        //PetProblem Name from ArrayListModel
        if (maidDetailModel.getUserPetProblemModel().size() > 0) {

            for (int position = 0; position < maidDetailModel.getUserPetProblemModel().size(); position++) {

                petProblemIdList.add(String.valueOf(maidDetailModel.getUserPetProblemModel().get(position).getPetProblemDetailModel().getId()));

                String name = petProblemName + maidDetailModel.getUserPetProblemModel().get(position).getPetProblemDetailModel().getName();
                petProblemName = name + ", ";
            }
            petProblemName = petProblemName.substring(0, petProblemName.length() - 2);
        }
        //workingStyleName  from ArrayListModel
        if (maidDetailModel.getMaidWorkingStyleModels().size() > 0) {

            for (int position = 0; position < maidDetailModel.getMaidWorkingStyleModels().size(); position++) {

                workingIdList.add(String.valueOf(maidDetailModel.getMaidWorkingStyleModels().get(position).getWorking_style_detail().getId()));

                String name = workingStyleName + maidDetailModel.getMaidWorkingStyleModels().get(position).getWorking_style_detail().getName();
                workingStyleName = name + ", ";
            }
            workingStyleName = workingStyleName.substring(0, workingStyleName.length() - 2);
        }
        //workSkillsName  from ArrayListModel
        if (maidDetailModel.getMaidSkillsModels().size() > 0) {

            for (int position = 0; position < maidDetailModel.getMaidSkillsModels().size(); position++) {

                skillsIdList.add(String.valueOf(maidDetailModel.getMaidSkillsModels().get(position).getSkill_detail().getId()));

                String name = workSkillsName + maidDetailModel.getMaidSkillsModels().get(position).getSkill_detail().getName();
                workSkillsName = name + ", ";
            }
            workSkillsName = workSkillsName.substring(0, workSkillsName.length() - 2);
        }
        //WorkingDistrictName  from ArrayListModel
        if (maidDetailModel.getMaidWorkingDistrictModels().size() > 0) {

            for (int position = 0; position < maidDetailModel.getMaidWorkingDistrictModels().size(); position++) {

                districtIDList.add(String.valueOf(maidDetailModel.getMaidWorkingDistrictModels().get(position).getDistrict_detail().getId()));

                String name = workingDistrict + maidDetailModel.getMaidWorkingDistrictModels().get(position).getDistrict_detail().getName();
                workingDistrict = name + ", ";
            }
            workingDistrict = workingDistrict.substring(0, workingDistrict.length() - 2);
        }
        //WorkingCountries  from ArrayListModel
        if (maidDetailModel.getMaidWorkingCountryModels().size() > 0) {

            for (int position = 0; position < maidDetailModel.getMaidWorkingCountryModels().size(); position++) {

                countryIdList.add(String.valueOf(maidDetailModel.getMaidWorkingCountryModels().get(position).getCountry_detail().getId()));

                String name = workingCountry + maidDetailModel.getMaidWorkingCountryModels().get(position).getCountry_detail().getName();
                workingCountry = name + ", ";
            }
            workingCountry = workingCountry.substring(0, workingCountry.length() - 2);
        }
        //JobChoice Name from ArraListModel
        if (maidDetailModel.getMaidJobChoiceModels().size() > 0) {

            for (int position = 0; position < maidDetailModel.getMaidJobChoiceModels().size(); position++) {

                joChoiceIdList.add(String.valueOf(maidDetailModel.getMaidJobChoiceModels().get(position)
                        .getJob_choice_detail().getId()));

                String name = jobChoiceName + maidDetailModel.getMaidJobChoiceModels().get(position)
                        .getJob_choice_detail().getName();
                jobChoiceName = name + ", ";
            }
            jobChoiceName = jobChoiceName.substring(0, jobChoiceName.length() - 2);
        }    //JobChoice Name from ArraListModel

        if (maidDetailModel.getMaidWorkingCityModels().size() > 0) {

            for (int position = 0; position < maidDetailModel.getMaidWorkingCityModels().size(); position++) {

                cityIdList.add(String.valueOf(maidDetailModel.getMaidWorkingCityModels().get(position)
                        .getCity_detail().getId()));

                String name = cityName + maidDetailModel.getMaidWorkingCityModels().get(position)
                        .getCity_detail().getName();
                cityName = name + ", ";
            }
            cityName = cityName.substring(0, cityName.length() - 2);
        }


        id = maidDetailModel.getCountry_id();
        stateID = maidDetailModel.getState_id();
        nationalityID = maidDetailModel.getNationality_id();

        binding.tvCountry.setText(maidDetailModel.getCountry_name());
        binding.tvState.setText(maidDetailModel.getState_name());
        binding.tvDob.setText(maidDetailModel.getDob());
        binding.tvNationality.setText(maidDetailModel.getNationality_name());

        binding.spinnerMaritalStatus.setSelection(Arrays.asList(maritalStatus)
                .indexOf(maidDetailModel.getMarital_status()));

        binding.spinnerKidStatus.setSelection(Arrays.asList(kidStatus)
                .indexOf(maidDetailModel.getKid_status()));

        binding.spinnerHijab.setSelection(Arrays.asList(hijab)
                .indexOf(maidDetailModel.getHijab()));

        binding.spinnerWorkStatus.setSelection(Arrays.asList(workStatus)
                .indexOf(maidDetailModel.getWork_status()));

        binding.spinnerDrivingLicence.setSelection(Arrays.asList(drivingLicence)
                .indexOf(maidDetailModel.getDriving_licence()));

        binding.spinnerSmoke.setSelection(Arrays.asList(smoke)
                .indexOf(maidDetailModel.getSmoke()));

        binding.spinnerAlcohol.setSelection(Arrays.asList(alcohol)
                .indexOf(maidDetailModel.getAlcohol()));

        binding.spinnerFamily.setSelection(Arrays.asList(liveWithFamily)
                .indexOf(maidDetailModel.getCan_live_with_family()));

        binding.spinnerTravelSituation.setSelection(Arrays.asList(travel)
                .indexOf(maidDetailModel.getTravel_situation()));

        binding.tvEducation.setText(educationName);
        binding.tvLanguage.setText(languageName);
        binding.tvPetProblem.setText(petProblemName);
        binding.tvCountryWork.setText(maidDetailModel.getMaidWorkingCountryModels().isEmpty() ? "No States" :
                maidDetailModel.getMaidWorkingCountryModels().get(0).getCountry_detail().getName());
        binding.tvStateWork.setText(maidDetailModel.getMaidWorkingStatesModels().isEmpty() ? "No States" :
                maidDetailModel.getMaidWorkingStatesModels().get(0).getState_detail().getName());
        binding.tvDistrict.setText(maidDetailModel.getMaidWorkingDistrictModels().isEmpty() ? "No District" :
                workingDistrict);

        binding.tvCity.setText(maidDetailModel.getMaidWorkingCityModels().isEmpty() ? "No City" :
                cityName);

        binding.tvJobChoice.setText(maidDetailModel.getMaidJobChoiceModels().isEmpty() ? "No Job Choice" :
                jobChoiceName);

        binding.tvworkingChoice.setText(maidDetailModel.getMaidWorkingStyleModels().isEmpty() ? "No Style" :
                workingStyleName);

        binding.tvSkill.setText(workSkillsName);
        binding.etEducationDetail.setText(maidDetailModel.getMaid_education());
        binding.etCertificateDetail.setText(maidDetailModel.getMaid_certificate());
        binding.etAboutDetail.setText(maidDetailModel.getMaid_about_me());

        int i = maidDetailModel.getMaidWorkExperiencesModel().size();
        if (i == 1) {
            if (maidDetailModel.getMaidWorkExperiencesModel().get(0).getStill_working().equals("1")) {
                binding.checkbox.setChecked(true);
                binding.checkbox.setActivated(true);
            }
            binding.etWorkExperience.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getDetail());
            binding.tvStartDate.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getStart_date());
            binding.tvEndDate.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getEnd_date());

        }
        if (i == 2) {

            binding.llAdd1.setVisibility(View.GONE);
            binding.llExperience2.setVisibility(View.VISIBLE);
            binding.etWorkExperience.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getDetail());
            binding.tvStartDate.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getStart_date());
            binding.tvEndDate.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getEnd_date());
            binding.etWorkExperience2.setText(maidDetailModel.getMaidWorkExperiencesModel().get(1).getDetail());
            binding.tvStartDate2.setText(maidDetailModel.getMaidWorkExperiencesModel().get(1).getStart_date());
            binding.tvEndDate2.setText(maidDetailModel.getMaidWorkExperiencesModel().get(1).getEnd_date());

            if (maidDetailModel.getMaidWorkExperiencesModel().get(1).getStill_working().equals("1")) {
                binding.checkbox2.setChecked(true);
                binding.checkbox2.setActivated(true);
            }

        }
        if (i == 3) {
            binding.btnAdd2.setVisibility(View.GONE);
            binding.llAdd2.setVisibility(View.GONE);
            binding.llExperience3.setVisibility(View.VISIBLE);
            binding.etWorkExperience.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getDetail());
            binding.tvStartDate.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getStart_date());
            binding.tvEndDate.setText(maidDetailModel.getMaidWorkExperiencesModel().get(0).getEnd_date());
            binding.etWorkExperience2.setText(maidDetailModel.getMaidWorkExperiencesModel().get(1).getDetail());
            binding.tvStartDate2.setText(maidDetailModel.getMaidWorkExperiencesModel().get(1).getStart_date());
            binding.tvEndDate2.setText(maidDetailModel.getMaidWorkExperiencesModel().get(1).getEnd_date());
            binding.etWorkExperience3.setText(maidDetailModel.getMaidWorkExperiencesModel().get(2).getDetail());
            binding.tvStartDate3.setText(maidDetailModel.getMaidWorkExperiencesModel().get(2).getStart_date());
            binding.tvEndDate3.setText(maidDetailModel.getMaidWorkExperiencesModel().get(2).getEnd_date());

            if (maidDetailModel.getMaidWorkExperiencesModel().get(1).getStill_working().equals("1")) {
                binding.checkbox3.setChecked(true);
                binding.checkbox3.setActivated(true);
            }
        }
    }

    @Override
    public void initControls() {
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.ivChoosePhoto.setOnClickListener(this);
        binding.ivProfilePicBig.setOnClickListener(this);
        binding.tvDob.setOnClickListener(this);
        binding.tvFemale.setOnClickListener(this);
        binding.tvMale.setOnClickListener(this);
        binding.tvFemaleColor.setOnClickListener(this);
        binding.tvMaleColor.setOnClickListener(this);
        binding.rlMaritalStatus.setOnClickListener(this);
        binding.rlKidsStatus.setOnClickListener(this);
        binding.rlHijob.setOnClickListener(this);
        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);
        binding.rlNationality.setOnClickListener(this);
        binding.rlEducation.setOnClickListener(this);
        binding.rlLanguage.setOnClickListener(this);
        binding.rlPetProblem.setOnClickListener(this);
        binding.rlWorkStatus.setOnClickListener(this);
        binding.rlSmoke.setOnClickListener(this);
        binding.rlAlcohol.setOnClickListener(this);
        binding.rlEducation.setOnClickListener(this);
        binding.rlLanguage.setOnClickListener(this);
        binding.rlPetProblem.setOnClickListener(this);
        binding.rlWorkStatus.setOnClickListener(this);
        binding.rlSmoke.setOnClickListener(this);
        binding.rlAlcohol.setOnClickListener(this);
        binding.tvStartDate.setOnClickListener(this);
        binding.tvEndDate.setOnClickListener(this);
        binding.tvStartDate2.setOnClickListener(this);
        binding.tvEndDate2.setOnClickListener(this);
        binding.tvStartDate3.setOnClickListener(this);
        binding.tvEndDate3.setOnClickListener(this);
        binding.btnAdd1.setOnClickListener(this);
        binding.btnAdd2.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.rlCountryWork.setOnClickListener(this);
        binding.rlStateWork.setOnClickListener(this);
        binding.rlDistrict.setOnClickListener(this);
        binding.rlCity.setOnClickListener(this);
        binding.rlJobChoice.setOnClickListener(this);
        binding.rlSkills.setOnClickListener(this);
        binding.rlWorkingChoices.setOnClickListener(this);
        binding.rlLiveFamily.setOnClickListener(this);
        binding.rlTravelSituation.setOnClickListener(this);

        if (binding.checkbox.isChecked()) {
            binding.tvEndDate.setEnabled(false);
        }
        if (binding.checkbox2.isChecked()) {
            binding.tvEndDate2.setEnabled(false);
        }
        if (binding.checkbox3.isChecked()) {
            binding.tvEndDate3.setEnabled(false);
        }


        binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvEndDate.setText("");
                    binding.tvEndDate.setEnabled(false);
                } else {
                    binding.tvEndDate.setText("");
                    binding.tvEndDate.setEnabled(true);
                }
            }
        });
        binding.checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvEndDate2.setText("");
                    binding.tvEndDate2.setEnabled(false);
                } else {
                    binding.tvEndDate2.setText("");
                    binding.tvEndDate2.setEnabled(true);
                }
            }
        });
        binding.checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvEndDate3.setText("");
                    binding.tvEndDate3.setEnabled(false);
                } else {
                    binding.tvEndDate3.setText("");
                    binding.tvEndDate3.setEnabled(true);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivChoosePhoto:
                if (position <= bigImageList.size() - 1) {
                    openDialog();
                }
                break;
            case R.id.ivProfilePicBig:
                if (position <= bigImageList.size() - 1) {
                    openDialog();
                }
                break;
            //Choose Image Dialog Clicks
            case R.id.btnGallery:
                openGallery();
                break;
            case R.id.btnCamera:
                openCamera();
                break;
            case R.id.btnAvatar:
                openAvatars();
                break;

            case R.id.btnAdd1:
                checkState = 2;
                binding.llAdd1.setVisibility(View.GONE);
                binding.llExperience2.setVisibility(View.VISIBLE);
                break;

            case R.id.btnAdd2:
                checkState = 3;
                binding.btnAdd2.setVisibility(View.GONE);
                binding.llAdd2.setVisibility(View.GONE);
                binding.llExperience3.setVisibility(View.VISIBLE);
                break;

            case R.id.rlNationality:
                startActivityForResult(new Intent(this, NationalityListActivity.class), 522);
                break;

            case R.id.rlCountry:
                startActivityForResult(new Intent(this, CountryListActivity.class), 512);
                break;

            case R.id.rlState:
                startActivityForResult(new Intent(this, StateListActivity.class), 520);
                break;

            case R.id.rlMaritalStatus:
                binding.spinnerMaritalStatus.performClick();
                break;

            case R.id.rlKidsStatus:
                binding.spinnerKidStatus.performClick();
                break;

            case R.id.rlHijob:
                binding.spinnerHijab.performClick();
                break;

            case R.id.tv_dob:
                onSelectDate();
                break;

            case R.id.tv_male:
                count++;
                if (count % 2 == 1) {
                    binding.tvFemaleColor.setVisibility(View.GONE);
                    binding.tvMaleColor.setVisibility(View.VISIBLE);
                    gender = "male";
                    setGender = true;
                    if (count2 % 2 == 1)
                        count2++;
                    binding.tvFemaleColor.setVisibility(View.GONE);
                    break;
                }

            case R.id.tv_male_color:
                count++;
                if (count % 2 == 0) {
                    binding.tvMaleColor.setVisibility(View.GONE);
                    break;
                }

            case R.id.tv_female:
                count2++;
                if (count2 % 2 == 1) {
                    binding.tvMaleColor.setVisibility(View.GONE);
                    binding.tvFemaleColor.setVisibility(View.VISIBLE);
                    gender = "female";
                    setGender = true;
                    if (count % 2 == 1) {
                        count++;
                        binding.tvMaleColor.setVisibility(View.GONE);
                    }
                    break;
                }

            case R.id.tv_female_color:
                count2++;
                if (count2 % 2 == 0) {
                    count++;
                    binding.tvFemaleColor.setVisibility(View.GONE);
                    break;
                }


            case R.id.rlEducation:
                startActivityForResult(new Intent(this, EducationListActivity.class), 524);
                break;

            case R.id.btnSubmit:
                updateProfile();
                break;

            case R.id.rlLanguage:
                startActivityForResult(new Intent(this, LanguageListActivity.class), 526);
                break;

            case R.id.rlPetProblem:
                startActivityForResult(new Intent(this, PetProblemListActivity.class), 528);
                break;

            case R.id.rlWorkStatus:
                binding.spinnerWorkStatus.performClick();
                break;

            case R.id.rlAlcohol:
                binding.spinnerAlcohol.performClick();
                break;

            case R.id.rlSmoke:
                binding.spinnerSmoke.performClick();
                break;
            case R.id.rlCountryWork:
                startActivityForResult(new Intent(this, CountryListActivity.class), 530);
                break;

            case R.id.rlStateWork:
                startActivityForResult(new Intent(this, StateListActivity.class), 532);
                break;

            case R.id.rlDistrict:
                startActivityForResult(new Intent(this, DistrictListActivity.class), 534);
                break;

            case R.id.rlCity:
                startActivityForResult(new Intent(this, CitylistActivity.class), 536);
                break;

            case R.id.rlJobChoice:
                startActivityForResult(new Intent(this, JobChoiceListActivity.class), 538);

                break;
            case R.id.rlSkills:
                startActivityForResult(new Intent(this, SkillsListActivity.class), 540);
                break;
            case R.id.rlWorkingChoices:
                startActivityForResult(new Intent(this, WorkingchoicesListActivity.class), 542);
                break;

            case R.id.rlLiveFamily:
                binding.spinnerFamily.performClick();
                break;

            case R.id.rlTravelSituation:
                binding.spinnerTravelSituation.performClick();
                break;

            case R.id.tvStartDate:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start1 = dayOfMonth + "/" + (month + 1) + "/" + year;
                        binding.tvStartDate.setText(start1);
                        startMonth = month + 1;
                        startYear = year;

                    }
                });
                break;

            case R.id.tvEndDate:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end1 = dayOfMonth + "/" + (month + 1) + "/" + year;
                        if ((year > startYear) || (year == startYear) || (month + 1 > startMonth)) {
                            binding.tvEndDate.setText(end1);
                        } else
                            Toast.makeText(EditMaidPofileActivity.this,
                                    R.string.please_choose_correct_date, Toast.LENGTH_SHORT).show();

                    }
                });
                break;

            case R.id.tvStartDate2:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start2 = dayOfMonth + "/" + (month + 1) + "/" + year;
                        binding.tvStartDate2.setText(start2);
                        startMonth = month + 1;
                        startYear = year;

                    }
                });
                break;

            case R.id.tvEndDate2:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end2 = dayOfMonth + "/" + (month + 1) + "/" + year;
                        if ((year > startYear) || (year == startYear) || (month + 1 > startMonth)) {
                            binding.tvEndDate2.setText(end2);
                        } else
                            Toast.makeText(EditMaidPofileActivity.this,
                                    R.string.please_choose_correct_date, Toast.LENGTH_SHORT).show();

                    }
                });
                break;

            case R.id.tvStartDate3:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start3 = dayOfMonth + "/" + (month + 1) + "/" + year;
                        binding.tvStartDate3.setText(start3);
                        startMonth = month + 1;
                        startYear = year;

                    }
                });
                break;

            case R.id.tvEndDate3:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end3 = dayOfMonth + "/" + (month + 1) + "/" + year;
                        if ((year > startYear) || (year == startYear) || (month + 1 > startMonth)) {
                            binding.tvEndDate3.setText(end3);
                        } else
                            Toast.makeText(EditMaidPofileActivity.this,
                                    R.string.please_choose_correct_date, Toast.LENGTH_SHORT).show();

                    }
                });
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == 512 && resultCode == Activity.RESULT_OK) {
            String countryName = data.getStringExtra("country");
            id = data.getIntExtra("id", 0);
            binding.tvCountry.setText(countryName);
        }
        if (requestCode == 520 && resultCode == Activity.RESULT_OK) {
            String stateName = data.getStringExtra("state");
            binding.tvState.setText(stateName);
            stateID = data.getIntExtra("state_id", 0);
        }
        if (requestCode == 522 && resultCode == Activity.RESULT_OK) {
            String nationality = data.getStringExtra("nationality");
            binding.tvNationality.setText(nationality);
            nationalityID = data.getIntExtra("nationality_id", 0);
        }
        if (requestCode == 524 && resultCode == Activity.RESULT_OK) {
            educationNameList = data.getStringArrayListExtra("nameList");
            educationIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < educationNameList.size(); i++) {
                if (i == 0) {
                    name = educationNameList.get(i);
                } else {
                    name = name + ", " + educationNameList.get(i);
                }
            }
            binding.tvEducation.setText(name);
        }
        if (requestCode == 526 && resultCode == Activity.RESULT_OK) {
            languageNameList = data.getStringArrayListExtra("nameList");
            languageIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < languageNameList.size(); i++) {
                if (i == 0) {
                    name = languageNameList.get(i);
                } else name = name + ", " + languageNameList.get(i);
            }
            binding.tvLanguage.setText(name);
        }
        if (requestCode == 528 && resultCode == Activity.RESULT_OK) {
            petProblemList = data.getStringArrayListExtra("nameList");
            petProblemIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < petProblemList.size(); i++) {
                if (i == 0) {
                    name = petProblemList.get(i);
                } else name = name + ", " + petProblemList.get(i);
            }
            binding.tvPetProblem.setText(name);
        }
        if (requestCode == 530 && resultCode == Activity.RESULT_OK) {
            String workCountryName = data.getStringExtra("country");
            String countryID = String.valueOf(id);
            countryIdList.add(countryID);
            binding.tvCountryWork.setText(workCountryName);
        }
        if (requestCode == 532 && resultCode == Activity.RESULT_OK) {
            String workStateName = data.getStringExtra("state");
            binding.tvStateWork.setText(workStateName);
            String stateid = String.valueOf(stateID);
            stateIdList.add(stateid);
        }
        if (requestCode == 534 && resultCode == Activity.RESULT_OK) {
            districtList = data.getStringArrayListExtra("nameList");
            districtIDList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < districtList.size(); i++) {
                if (i == 0) {
                    name = districtList.get(i);
                } else name = name + ", " + districtList.get(i);
            }
            binding.tvDistrict.setText(name);
        }
        if (requestCode == 536 && resultCode == Activity.RESULT_OK) {
            cityList = data.getStringArrayListExtra("nameList");
            cityIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < cityList.size(); i++) {
                if (i == 0) {
                    name = cityList.get(i);
                } else name = name + ", " + cityList.get(i);
            }
            binding.tvCity.setText(name);
        }
        if (requestCode == 538 && resultCode == Activity.RESULT_OK) {
            jobChoiceList = data.getStringArrayListExtra("nameList");
            joChoiceIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < jobChoiceList.size(); i++) {
                if (i == 0) {
                    name = jobChoiceList.get(i);
                } else name = name + ", " + jobChoiceList.get(i);
            }
            binding.tvJobChoice.setText(name);
        }
        if (requestCode == 540 && resultCode == Activity.RESULT_OK) {
            skillsList = data.getStringArrayListExtra("nameList");
            skillsIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < skillsList.size(); i++) {
                if (i == 0) {
                    name = skillsList.get(i);
                } else name = name + ", " + skillsList.get(i);
            }
            binding.tvSkill.setText(name);
        }
        if (requestCode == 542 && resultCode == Activity.RESULT_OK) {
            workingList = data.getStringArrayListExtra("nameList");
            workingIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < workingList.size(); i++) {
                if (i == 0) {
                    name = workingList.get(i);
                } else name = name + ", " + workingList.get(i);
            }
            binding.tvworkingChoice.setText(name);
        }
    }

    private void showamountSituation() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, amount);
        binding.etCurrency.setAdapter(adapter);
        binding.etCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                amountStatus = binding.etCurrency.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showTravelSituation() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, travel);
        binding.spinnerTravelSituation.setAdapter(adapter);
        binding.spinnerTravelSituation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                travelStatus = binding.spinnerTravelSituation.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showLiveFamily() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, liveWithFamily);
        binding.spinnerFamily.setAdapter(adapter);
        binding.spinnerFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                familyStatus = binding.spinnerFamily.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showMaritalStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, maritalStatus);
        binding.spinnerMaritalStatus.setAdapter(adapter);
        binding.spinnerMaritalStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mariStatus = binding.spinnerMaritalStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showkidStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, kidStatus);
        binding.spinnerKidStatus.setAdapter(adapter);
        binding.spinnerKidStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kidsStatus = binding.spinnerKidStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showHijabStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, hijab);
        binding.spinnerHijab.setAdapter(adapter);
        binding.spinnerHijab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hijabStatus = binding.spinnerHijab.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void showAlcoholStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, alcohol);
        binding.spinnerAlcohol.setAdapter(adapter);
        binding.spinnerAlcohol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alcoholStatus = binding.spinnerAlcohol.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showSmokeStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, smoke);
        binding.spinnerSmoke.setAdapter(adapter);
        binding.spinnerSmoke.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                smokeStatus = binding.spinnerSmoke.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showLicenceStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, drivingLicence);
        binding.spinnerDrivingLicence.setAdapter(adapter);
        binding.spinnerDrivingLicence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drivingStatus = binding.spinnerDrivingLicence.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showWorkStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, workStatus);
        binding.spinnerWorkStatus.setAdapter(adapter);
        binding.spinnerWorkStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                work = binding.spinnerWorkStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSelectedDate(DatePickerDialog.OnDateSetListener listener) {
        Calendar mDate = Calendar.getInstance();
        int date = mDate.get(Calendar.DAY_OF_MONTH);
        int month = mDate.get(Calendar.MONTH);
        int year = mDate.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this
                , listener, year, month, date);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void onSelectDate() {
        final Calendar mDate = Calendar.getInstance();
        int date = mDate.get(Calendar.DAY_OF_MONTH);
        int month = mDate.get(Calendar.MONTH);
        int year = mDate.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (year >= mDate.get(Calendar.YEAR)) {
                    Toast.makeText(EditMaidPofileActivity.this, R.string.please_choose_a_valid_date,
                            Toast.LENGTH_SHORT).show();
                } else {
                    binding.tvDob.setText(year + "-" +
                            ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                            ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)));
                }
            }
        }, year, month, date);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void updateList(int position, ArrayList<Integer> imageIdList) {
        this.position = position;
        this.imageIdList = imageIdList;
    }

    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, "title",
                null);
        return Uri.parse(path);
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void openDialog() {
        if (checkPermissionForReadExtertalStorage()) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.choose_image_layout);
            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }
            dialog.findViewById(R.id.btnGallery).setOnClickListener(this);
            dialog.findViewById(R.id.btnCamera).setOnClickListener(this);
            dialog.findViewById(R.id.btnAvatar).setOnClickListener(this);
        } else {
            try {
                requestPermissionForReadExtertalStorage();
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.WRITE_STORAGE_PERMISSION_REQUEST_CODE);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void openGallery() {
        dialog.dismiss();
        filePickUtils.requestImageGallery(STORAGE_PERMISSION_IMAGE, true, true);

    }

    private void openCamera() {
        dialog.dismiss();
        filePickUtils.requestImageCamera(CAMERA_PERMISSION, true, true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private FilePickUtils.OnFileChoose onFileChoose = new FilePickUtils.OnFileChoose() {
        @Override
        public void onFileChoose(String fileUri, int requestCode, int size) {
            lifeCycleCallBackManager = filePickUtils.getCallBackManager();
            binding.ivProfilePicBig.setImageURI(Uri.fromFile(new File(fileUri)));
            setImagePart(fileUri);
        }
    };

    private void openAvatars() {
        binding.ivProfilePicBig.setImageResource(R.drawable.men_icon);
        binding.ivProfilePicBig.setScaleType(ImageView.ScaleType.FIT_XY);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, "avatar.PNG");
        setImagePart(file.getAbsolutePath());
        dialog.dismiss();
    }

    public void setImagePart(String fileUri) {

        try {
            File file = new File(fileUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            // MultipartBody.Part is used to send also the actual file name
            profileImage = MultipartBody.Part.createFormData("profile_image", file.getName(),
                    requestFile);
            userDetailMap = new HashMap<>();
            userDetailMap.put("image_id", createPartFromString(String.valueOf(imageIdList.get(position))));
            userDetailMap.put("user_id", createPartFromString(userId));

        } catch (Exception e) {
            e.printStackTrace();
        }


        binding.progress.setVisibility(View.VISIBLE);
        updateProfilePicture(accessToken, profileImage, userDetailMap);

    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }


    private void updateProfile() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.btnSubmit.setEnabled(false);

        int stillWorking;
        boolean nextStep = false;
        if (checkState == 1) {
            if (!binding.etWorkExperience.getText().toString().isEmpty()) {
                if (binding.checkbox.isChecked()) {
                    if (!binding.tvStartDate.getText().toString().isEmpty()) {
                        startDate.add(start1);
                        endDate.add(binding.tvEndDate.getText().toString());
                        stillWorking = 1;
                        workExperiences.add(binding.etWorkExperience.getText().toString());

                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();

                } else {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvEndDate.getText().toString().isEmpty()) {
                        stillWorking = 0;
                        startDate.add(start1);
                        endDate.add(end1);
                        workExperiences.add(binding.etWorkExperience.getText().toString());

                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }


            } else nextStep = true;
        } else if (checkState == 2) {
            startDate = new ArrayList<>();
            endDate = new ArrayList<>();
            stillWork = new ArrayList<>();
            workExperiences = new ArrayList<>();
            if (!binding.etWorkExperience2.getText().toString().isEmpty() &&
                    !binding.etWorkExperience.getText().toString().isEmpty()) {
                if (binding.checkbox2.isChecked()) {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty()
                            ) {
                        startDate.add(start1);
                        startDate.add(start2);
                        endDate.add(binding.tvEndDate.getText().toString());
                        endDate.add(binding.tvEndDate2.getText().toString());
                        stillWorking = 1;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());

                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                } else {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvEndDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvEndDate2.getText().toString().isEmpty()) {
                        startDate.add(start1);
                        startDate.add(start2);
                        endDate.add(end1);
                        endDate.add(end2);

                        stillWorking = 0;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());

                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }


            } else Toast.makeText(this, R.string.fill_all_the_details, Toast.LENGTH_SHORT).show();
        } else if (checkState == 3) {
            startDate = new ArrayList<>();
            endDate = new ArrayList<>();
            stillWork = new ArrayList<>();
            workExperiences = new ArrayList<>();
            if (!binding.etWorkExperience2.getText().toString().isEmpty() &&
                    !binding.etWorkExperience.getText().toString().isEmpty()
                    && !binding.etWorkExperience3.getText().toString().isEmpty()) {

                if (binding.checkbox3.isChecked()) {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvEndDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvEndDate2.getText().toString().isEmpty() &&
                            !binding.tvStartDate3.getText().toString().isEmpty() &&
                            !binding.tvEndDate3.getText().toString().isEmpty()) {
                        stillWorking = 1;
                        startDate.add(start1);
                        startDate.add(start2);
                        startDate.add(start3);
                        endDate.add(end1);
                        endDate.add(end2);
                        endDate.add(end3);
                        stillWork.add("0");
                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());
                        workExperiences.add(binding.etWorkExperience3.getText().toString());
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();


                } else {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvStartDate3.getText().toString().isEmpty()) {
                        startDate.add(start1);
                        startDate.add(start2);
                        startDate.add(start3);
                        endDate.add(binding.tvEndDate.getText().toString());
                        endDate.add(binding.tvEndDate2.getText().toString());
                        endDate.add(binding.tvEndDate3.getText().toString());
                        stillWorking = 0;
                        stillWork.add("0");
                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());
                        workExperiences.add(binding.etWorkExperience3.getText().toString());
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }


            } else Toast.makeText(this, R.string.fill_all_the_details, Toast.LENGTH_SHORT).show();

        }

        if (binding.etWorkExperience.getText().toString().isEmpty()) {
            if (ValidationUtils.isOnline(binding.layout, this)) {
                sendDataOfMaid(
                        accessToken,
                        Constants.userType(this),
                        String.valueOf(id),
                        binding.tvDob.getText().toString(),
                        gender, mariStatus,
                        String.valueOf(nationalityID),
                        kidsStatus,
                        hijabStatus,
                        String.valueOf(stateID),
                        educationIdList.toString(),
                        languageIdList.toString(),
                        work,
                        drivingStatus,
                        smokeStatus,
                        alcoholStatus,
                        petProblemIdList.toString(),
                        stateIdList.toString(),
                        countryIdList.toString(),
                        cityIdList.toString(),
                        districtIDList.toString(),
                        joChoiceIdList.toString(),
                        workingIdList.toString(),
                        familyStatus,
                        travelStatus,
                        binding.etRate.getText().toString().trim() + " " + "$",
                        skillsIdList.toString(),
                        binding.etEducationDetail.getText().toString().trim(),
                        binding.etCertificateDetail.getText().toString().trim(),
                        binding.etAboutDetail.getText().toString().trim(),
                        workExperiences,
                        startDate, endDate,
                        stillWork,
                        amountStatus
                );
            }
        } else {
            if (!startDate.isEmpty() || !endDate.isEmpty()) {
                if (ValidationUtils.isOnline(binding.layout, this)) {
                    sendDataOfMaid(
                            accessToken,
                            Constants.userType(this),
                            String.valueOf(id),
                            binding.tvDob.getText().toString(),
                            gender, mariStatus,
                            String.valueOf(nationalityID),
                            kidsStatus,
                            hijabStatus,
                            String.valueOf(stateID),
                            educationIdList.toString(),
                            languageIdList.toString(),
                            work,
                            drivingStatus,
                            smokeStatus,
                            alcoholStatus,
                            petProblemIdList.toString(),
                            stateIdList.toString(),
                            countryIdList.toString(),
                            cityIdList.toString(),
                            districtIDList.toString(),
                            joChoiceIdList.toString(),
                            workingIdList.toString(),
                            familyStatus,
                            travelStatus,
                            binding.etRate.getText().toString().trim(),
                            skillsIdList.toString(),
                            binding.etEducationDetail.getText().toString().trim(),
                            binding.etCertificateDetail.getText().toString().trim(),
                            binding.etAboutDetail.getText().toString().trim(),
                            workExperiences,
                            startDate, endDate,
                            stillWork,
                            amountStatus
                    );
                }
            } else
                Toast.makeText(this, R.string.please_select_start_and_end_date, Toast.LENGTH_LONG).show();


        }
    }

    private void sendDataOfMaid(String access_token,
                                String user_type,
                                String country_id,
                                String dob,
                                String gender,
                                String marital_status,
                                String nationality_id,
                                String kids,
                                String hi_job,
                                String state_id,
                                String education_ids,
                                String languages_ids,
                                String work_status,
                                String driving_licence,
                                String smoke,
                                String alcohol,
                                String pet_problem_ids,
                                String working_states,
                                String maid_can_work_country_id,
                                String city,
                                String district,
                                String maid_job_choice_ids,
                                String maid_working_style_ids,
                                String can_live_with_family,
                                String travel_situation,
                                String expected_fees,
                                String maid_skill_ids,
                                String maid_education,
                                String maid_certificate,
                                String maid_about_me,
                                ArrayList<String> maid_work_experiences,
                                ArrayList<String> start_date,
                                ArrayList<String> end_date,
                                ArrayList<String> still_working,
                                String fees_currency) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApi> call = apiService.updateMaidProfile(
                Constants.TIMEZONE,
                Constants.LOCALE,
                access_token,
                user_type,
                country_id,
                dob,
                gender,
                marital_status,
                nationality_id,
                kids,
                hi_job,
                state_id,
                education_ids,
                languages_ids,
                work_status,
                driving_licence,
                smoke, alcohol,
                pet_problem_ids,
                working_states,
                maid_can_work_country_id,
                city, district,
                maid_job_choice_ids,
                maid_working_style_ids,
                can_live_with_family,
                travel_situation,
                expected_fees,
                maid_skill_ids,
                maid_education,
                maid_certificate,
                maid_about_me,
                maid_work_experiences,
                start_date,
                end_date,
                still_working,
                fees_currency);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnSubmit.setClickable(true);

                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;

                        Toast.makeText(EditMaidPofileActivity.this, R.string.profile_updated_successfully,
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditMaidPofileActivity.this,
                                HomeForMaidActivity.class));
                        finish();

                    } else

                        Toast.makeText(EditMaidPofileActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();

                } else {

                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(EditMaidPofileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else if (response.code() == 500) {
                            Toast.makeText(EditMaidPofileActivity.this, response.errorBody().toString(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EditMaidPofileActivity.this, ""
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
            public void onFailure(Call<RegisterApi> call, Throwable t) {

                Toast.makeText(EditMaidPofileActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateProfilePicture(String accessToken, MultipartBody.Part profileImage,
                                      Map<String, RequestBody> userDetailMap) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.updateProfilePicture(accessToken, profileImage,
                userDetailMap);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    SignUpModel signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (message.equals("Profile updated successfully.")) {

                            if (signUpModel.getUserImagesModel().size() > 0) {
                                for (int i = 1; i <= signUpModel.getUserImagesModel().size(); i++) {
                                    imageList.set(i - 1, signUpModel.getUserImagesModel().get(i - 1)
                                            .getImageModel().getSmall());
                                    bigImageList.set(i - 1, signUpModel.getUserImagesModel().get(i - 1)
                                            .getImageModel().getBig());

                                }
                            }
                            imageAdapter.notifyDataSetChanged();
                            Toast.makeText(EditMaidPofileActivity.this, "Profile picture updated successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(EditMaidPofileActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        Toast.makeText(EditMaidPofileActivity.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(EditMaidPofileActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {

    }
}
