package com.housemaid.activities;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.housemaid.R;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
import com.housemaid.activities.agency.fromHome.OtherMaidsProfileActivity;
import com.housemaid.activities.dataList.CitylistSingleActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.EducationListActivity;
import com.housemaid.activities.dataList.JobChoiceListActivity;
import com.housemaid.activities.dataList.LanguageListActivity;
import com.housemaid.activities.dataList.NationalityListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.activities.maid.fromHome.activity.HomeForMaidActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.databinding.ActivityFilterBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.RegisterApiForMaidList;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFilterBinding binding;
    private SharedPreference sharedPreference;
    private String cityName;
    private String cityid = "";
    private String districtid = "";
    private String districtName;
    private String workingChoiceName;
    private ArrayList<String> jobChoiceList = new ArrayList<>();
    private ArrayList<String> joChoiceIdList = new ArrayList<>();
    private ArrayList<String> languageNameList = new ArrayList<>();
    private ArrayList<String> languageIdList = new ArrayList<>();
    private ArrayList<String> educationNameList = new ArrayList<>();
    private ArrayList<String> educationIdList = new ArrayList<>();
    private String nationality;
    private int nationalityID;
    private String accessToken;

    private String amount[];
    private String amountStatus;
    private String experience[];
    private String maritalStatus[];
    private String kidStatus[];
    private String hijab[];
    private String liveWithFamily[];
    private ArrayList<String> fee;

    private String familyStatus = "";
    private String totalExperience = "";
    private String mariStatus = "";
    private String kidsStatus = "";
    private String hijabStatus = "";
    private String workStatus[];
    private String drivingLicence[];
    private String smoke[];
    private String alcohol[];
    private String drivingStatus = "";
    private String work = "";
    private String smokeStatus = "";
    private String alcoholStatus = "";
    private String education = "";
    private String name;
    private String name1;
    private String name2;
    private String countryName;
    private String stateName;
    private ArrayList<UserDetailModel> maidListingList;
    private ArrayList<SignUpModel> jobListingList;

    private String country_id = "";
    private String state_id = "";
    private String nationality_id = "";

    private String language = "";
    private String jobchoice = "";
    private String minAge = "";
    private String maxAge = "";
    private ArrayList<String> ageList = new ArrayList<>();
    private String age = "";
    private String work_id = "";
    private int filterfor = 0;
    private String ratingMaid = "";
    private ArrayList<SignUpModel> agencyDetailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.search);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
        float rating = binding.ratingMaid.getRating();
        ratingMaid = String.valueOf(rating);
        fee = new ArrayList<>();

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            binding.btnSearchAgency.setVisibility(View.VISIBLE);
            binding.btnSearchUser.setVisibility(View.VISIBLE);
            binding.rlExperience.setVisibility(View.VISIBLE);
            binding.viewExperience.setVisibility(View.VISIBLE);
            binding.rlRating.setVisibility(View.GONE);
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            binding.btnSearchMaid.setVisibility(View.VISIBLE);
            binding.btnSearchAgency.setVisibility(View.VISIBLE);
            binding.rlRating.setVisibility(View.VISIBLE);
        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            binding.btnSearchUser.setVisibility(View.VISIBLE);
            binding.btnSearchMaid.setVisibility(View.VISIBLE);
            binding.rlRating.setVisibility(View.GONE);
            binding.btnSearchUser.setBackground(getResources().getDrawable(R.drawable.btn_blue));
            binding.btnSearchMaid.setBackground(getResources().getDrawable(R.drawable.btn_white));
            binding.btnSearchUser.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            binding.btnSearchMaid.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        maritalStatus = getResources().getStringArray(R.array.maritalStatusFilter);
        showMaritalStatus();
        kidStatus = getResources().getStringArray(R.array.kidStatusFilter);
        showkidStatus();
        hijab = getResources().getStringArray(R.array.hijabFilter);
        showHijabStatus();
        workStatus = getResources().getStringArray(R.array.workStatusFilter);
        showWorkStatus();
        drivingLicence = getResources().getStringArray(R.array.drivingLicenceFilter);
        showLicenceStatus();
        smoke = getResources().getStringArray(R.array.smokeFilter);
        showSmokeStatus();
        alcohol = getResources().getStringArray(R.array.alcoholFilter);
        showAlcoholStatus();
        experience = getResources().getStringArray(R.array.experienceFilter);
        showExperience();
        amount = getResources().getStringArray(R.array.amount);
        showAmountSituation();
        liveWithFamily = getResources().getStringArray(R.array.FamilyFilter);
        showLiveFamily();

    }

    @Override
    public void initControls() {
        super.initControls();

        binding.btnSearchAgency.setOnClickListener(this);
        binding.btnSearchMaid.setOnClickListener(this);
        binding.btnSearchUser.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);
        binding.rlDistrict.setOnClickListener(this);
        binding.rlCity.setOnClickListener(this);
        binding.rlNationality.setOnClickListener(this);
        binding.rlEducation.setOnClickListener(this);
        binding.rlWorkType.setOnClickListener(this);
        binding.rlLanguages.setOnClickListener(this);
        binding.rlJobChoice.setOnClickListener(this);
        binding.rlWorkStatus.setOnClickListener(this);
        binding.rlSmoke.setOnClickListener(this);
        binding.rlAlcohol.setOnClickListener(this);
        binding.rlMaritalStatus.setOnClickListener(this);
        binding.rlKidsStatus.setOnClickListener(this);
        binding.rlHijob.setOnClickListener(this);
        binding.rlExperience.setOnClickListener(this);
        binding.rlLiveFamily.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);
        binding.btnClearForm.setOnClickListener(this);

        binding.rangeSeekbar1.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                binding.tvMinAge.setText(String.valueOf(minValue) + "yrs");
                binding.tvMaxAge.setText(String.valueOf(maxValue) + "yrs");

                minAge = String.valueOf(minValue);
                maxAge = String.valueOf(maxValue);
                ageList.add(minAge);
                ageList.add(maxAge);
                age = ageList.toString();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivBack:

                onBackPressed();
                break;

            case R.id.btnSearchMaid:

                binding.scrollView.smoothScrollTo(0, 0);
                if (sharedPreference.getInteger("entry_key", 0) == 2) {
                    binding.btnSearchAgency.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    binding.btnSearchMaid.setBackground(getResources().getDrawable(R.drawable.btn_blue));
                    binding.btnSearchAgency.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.btnSearchMaid.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                if (sharedPreference.getInteger("entry_key", 0) == 3) {
                    binding.btnSearchMaid.setBackground(getResources().getDrawable(R.drawable.btn_blue));
                    binding.btnSearchMaid.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    binding.rlExperience.setVisibility(View.GONE);
                    binding.viewExperience.setVisibility(View.GONE);
                }
                binding.btnSearchUser.setBackground(getResources().getDrawable(R.drawable.btn_white));
                binding.btnSearchUser.setTextColor(getResources().getColor(R.color.colorPrimary));
                binding.rlRating.setVisibility(View.VISIBLE);
                binding.rlAgeRange.setVisibility(View.VISIBLE);
                binding.llMaidFilter.setVisibility(View.VISIBLE);
                filterfor = 1;
                break;

            case R.id.btnSearchUser:

                binding.scrollView.smoothScrollTo(0, 0);
                if (sharedPreference.getInteger("entry_key", 0) == 1) {
                    binding.btnSearchAgency.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    binding.btnSearchUser.setBackground(getResources().getDrawable(R.drawable.btn_blue));
                    binding.btnSearchAgency.setTextColor(getResources().getColor(R.color.colorPrimary));
                    binding.btnSearchUser.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                }
                if (sharedPreference.getInteger("entry_key", 0) == 3) {
                    binding.btnSearchUser.setBackground(getResources().getDrawable(R.drawable.btn_blue));
                    binding.btnSearchUser.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    binding.btnSearchMaid.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    binding.btnSearchMaid.setTextColor(getResources().getColor(R.color.colorPrimary));
                }

                binding.rlRating.setVisibility(View.GONE);
                binding.rlAgeRange.setVisibility(View.VISIBLE);
                binding.llMaidFilter.setVisibility(View.VISIBLE);
                binding.rlExperience.setVisibility(View.VISIBLE);
                binding.viewExperience.setVisibility(View.VISIBLE);
                filterfor = 2;
                break;

            case R.id.btnSearchAgency:

                if (sharedPreference.getInteger("entry_key", 0) == 1) {
                    binding.btnSearchUser.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    binding.btnSearchUser.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                if (sharedPreference.getInteger("entry_key", 0) == 2) {
                    binding.btnSearchMaid.setBackground(getResources().getDrawable(R.drawable.btn_white));
                    binding.btnSearchMaid.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                binding.btnSearchAgency.setBackground(getResources().getDrawable(R.drawable.btn_blue));
                binding.btnSearchAgency.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                binding.llMaidFilter.setVisibility(View.GONE);
                binding.rlAgeRange.setVisibility(View.GONE);
                binding.rlRating.setVisibility(View.GONE);
                filterfor = 3;
                break;

            case R.id.rlNationality:
                startActivityForResult(new Intent(this, NationalityListActivity.class),
                        522);
                break;

            case R.id.rlCountry:
                startActivityForResult(new Intent(this, CountryListActivity.class),
                        512);
                break;

            case R.id.rlState:
                startActivityForResult(new Intent(this, StateListActivity.class),
                        520);
                break;

            case R.id.rlDistrict:
                startActivityForResult(new Intent(this, DistrictSingleListActivity.class),
                        550);
                break;

            case R.id.rlCity:
                startActivityForResult(new Intent(this, CitylistSingleActivity.class),
                        552);
                break;

            case R.id.rlEducation:
                Intent educationIntent = new Intent(this, EducationListActivity.class);
                educationIntent.putExtra("educationIdList", educationIdList);
                educationIntent.putExtra("educationNameList", educationNameList);
                startActivityForResult(educationIntent, 524);
                break;

            case R.id.rlLanguages:
                Intent languageIntent = new Intent(this, LanguageListActivity.class);
                languageIntent.putExtra("languageIdList", languageIdList);
                languageIntent.putExtra("languageNameList", languageNameList);
                startActivityForResult(languageIntent, 526);
                break;

            case R.id.rlWorkType:
                startActivityForResult(new Intent(this, WorkingchoiceSingleListActivity
                                .class),
                        554);
                break;

            case R.id.rlJobChoice:
                Intent jobIntent = new Intent(this, JobChoiceListActivity.class);
                jobIntent.putExtra("joChoiceIdList", joChoiceIdList);
                jobIntent.putExtra("jobChoiceList", jobChoiceList);
                startActivityForResult(jobIntent, 538);
                break;


            case R.id.rlMaritalStatus:
                binding.spinnerMaritalStatus.performClick();
                break;

            case R.id.rlDrivingLicence:
                binding.spinnerDrivingLicence.performClick();
                break;

            case R.id.rlKidsStatus:
                binding.spinnerKidStatus.performClick();
                break;

            case R.id.rlHijob:
                binding.spinnerHijab.performClick();
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

            case R.id.etCurrency:
                binding.etCurrency.performClick();
                break;

            case R.id.rlLiveFamily:
                binding.spinnerFamily.performClick();
                break;

            case R.id.rlExperience:
                binding.spinnerExperience.performClick();
                break;

            case R.id.btnFilter:

                String minFee = binding.etMinRate.getText().toString().trim();
                String maxFee = binding.etMaxRate.getText().toString().trim();

                fee.add(minFee);
                fee.add(maxFee);

                if (sharedPreference.getInteger("entry_key", 0) == 1) {
                    if (filterfor == 0 || filterfor == 2) {
                        getFilterJobData();
                    } else {
                        getFilterAgencyData();
                    }

                }
                if (sharedPreference.getInteger("entry_key", 0) == 2) {
                    if (filterfor == 0 || filterfor == 1) {
                        getFilterMaidData();
                    } else {
                        getFilterAgencyData();
                    }

                }
                if (sharedPreference.getInteger("entry_key", 0) == 3) {
                    if (filterfor == 0 || filterfor == 2) {
                        getFilterJobData();
                    } else getFilterMaidData();

                }
                break;

            case R.id.btnClearForm:
                clearAllFilter();
                break;

        }
    }

    private void clearAllFilter() {
        binding.ratingMaid.setRating(0f);
        binding.tvMinAge.setText("16yrs");
        binding.tvMaxAge.setText("65yrs");
        binding.rangeSeekbar1.setMinStartValue(0f);
        binding.rangeSeekbar1.setMaxStartValue(100f);
        binding.rangeSeekbar1.apply();

        country_id = "";
        cityid = "";
        state_id = "";
        districtid = "";
        nationality_id = "";
        age = "";
        language = "";
        jobchoice = "";
        work = "";
        education = "";
        work_id = "";
        ratingMaid = "";
        name = "";
        name1 = "";
        name2 = "";
        districtName = "";
        countryName = "";
        stateName = "";
        cityName = "";
        workingChoiceName = "";
        binding.etMinRate.setText("");
        binding.etMaxRate.setText("");
        fee = new ArrayList<>();

        binding.tvCountry.setText("");
        binding.tvState.setText("");
        binding.tvNationality.setText("");
        binding.tvEducation.setText(name);
        binding.tvLanguages.setText(name1);
        binding.tvDistrict.setText(districtName);
        binding.tvCity.setText(cityName);
        binding.tvWorkType.setText(workingChoiceName);
        binding.tvJobChoice.setText(name2);
        binding.etMinRate.setText("");
        binding.etMaxRate.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 512 && resultCode == Activity.RESULT_OK) {
            countryName = data.getStringExtra("country");
            int id = data.getIntExtra("id", 0);
            country_id = String.valueOf(id);

            cityid = "";
            state_id = "";
            districtid = "";
            stateName = "";
            districtName = "";
            cityName = "";
            binding.tvState.setText(stateName);
            binding.tvDistrict.setText(districtName);
            binding.tvCity.setText(cityName);


            sharedPreference.putInteger("Country_id", id);
            binding.tvCountry.setText(countryName);
        }
        if (requestCode == 520 && resultCode == Activity.RESULT_OK) {
            stateName = data.getStringExtra("state");
            binding.tvState.setText(stateName);
            int stateID = data.getIntExtra("state_id", 0);
            state_id = String.valueOf(stateID);
            binding.tvState.setText(stateName);
        }
        if (requestCode == 522 && resultCode == Activity.RESULT_OK) {
            nationality = data.getStringExtra("nationality");
            binding.tvNationality.setText(nationality);
            nationalityID = data.getIntExtra("nationality_id", 0);
            nationality_id = String.valueOf(nationalityID);
        }
        if (requestCode == 524 && resultCode == Activity.RESULT_OK) {
            educationNameList = data.getStringArrayListExtra("nameList");
            educationIdList = data.getStringArrayListExtra("idList");
            education = educationIdList.toString();
            for (int i = 0; i < educationNameList.size(); i++) {
                if (i == 0) {
                    name = educationNameList.get(i);
                } else {
                    name = name + ", " + educationNameList.get(i);
                }
            }
            if (educationNameList.size() > 0) binding.tvEducation.setText(name);
            else binding.tvEducation.setText("");
        }
        if (requestCode == 526 && resultCode == Activity.RESULT_OK) {
            languageNameList = data.getStringArrayListExtra("nameList");
            languageIdList = data.getStringArrayListExtra("idList");
            language = languageIdList.toString();
            for (int i = 0; i < languageNameList.size(); i++) {
                if (i == 0) {
                    name1 = languageNameList.get(i);
                } else {
                    name1 = name1 + ", " + languageNameList.get(i);
                }
            }
            if (languageNameList.size() > 0) binding.tvLanguages.setText(name1);
            else binding.tvLanguages.setText("");
        }

        if (requestCode == 550 && resultCode == Activity.RESULT_OK) {
            districtName = data.getStringExtra("district");
            binding.tvDistrict.setText(districtName);
            int districtID = data.getIntExtra("id", 0);
            districtid = String.valueOf(districtID);
        }
        if (requestCode == 552 && resultCode == Activity.RESULT_OK) {
            cityName = data.getStringExtra("city");
            binding.tvCity.setText(cityName);
            int cityID = data.getIntExtra("id", 0);
            cityid = String.valueOf(cityID);
        }
        if (requestCode == 554 && resultCode == Activity.RESULT_OK) {
            workingChoiceName = data.getStringExtra("working");
            binding.tvWorkType.setText(workingChoiceName);
            int workingID = data.getIntExtra("id", 0);
            String workingid = String.valueOf(workingID);
            ArrayList<String> workingList = new ArrayList<>();
            workingList.add(workingid);
            work_id = workingList.toString();
        }
        if (requestCode == 538 && resultCode == Activity.RESULT_OK) {
            jobChoiceList = data.getStringArrayListExtra("nameList");
            joChoiceIdList = data.getStringArrayListExtra("idList");
            jobchoice = joChoiceIdList.toString();
            for (int i = 0; i < jobChoiceList.size(); i++) {
                if (i == 0) {
                    name2 = jobChoiceList.get(i);
                } else {
                    name2 = name2 + ", " + jobChoiceList.get(i);
                }
            }
            if (jobChoiceList.size() > 0)
                binding.tvJobChoice.setText(name2);
            else binding.tvJobChoice.setText("");
        }
    }

    private void showLiveFamily() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, liveWithFamily);
        binding.spinnerFamily.setAdapter(adapter);
        binding.spinnerFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else familyStatus = binding.spinnerFamily.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showAmountSituation() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, amount);
        binding.etCurrency.setAdapter(adapter);
        binding.etCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else amountStatus = binding.etCurrency.getSelectedItem().toString();

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

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else mariStatus = binding.spinnerMaritalStatus.getSelectedItem().toString();
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

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else kidsStatus = binding.spinnerKidStatus.getSelectedItem().toString();
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

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else hijabStatus = binding.spinnerHijab.getSelectedItem().toString();
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
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else alcoholStatus = binding.spinnerAlcohol.getSelectedItem().toString();

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
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else smokeStatus = binding.spinnerSmoke.getSelectedItem().toString();

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
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else drivingStatus = binding.spinnerDrivingLicence.getSelectedItem().toString();

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

                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else work = binding.spinnerWorkStatus.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showExperience() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, experience);
        binding.spinnerExperience.setAdapter(adapter);
        binding.spinnerExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else totalExperience = binding.spinnerExperience.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getFilterMaidData() {
        binding.progress.setVisibility(View.VISIBLE);
        if (ValidationUtils.isOnline(binding.linearlayout, this)) {

            sendDataOfMaid(accessToken, country_id, cityid, state_id, districtid, nationality_id, fee.toString(),
                    age, language, jobchoice, work, mariStatus, kidsStatus, hijabStatus, education,
                    drivingStatus, smokeStatus, alcoholStatus, work_id, familyStatus, ratingMaid, amountStatus);
        }
    }

    public void getFilterJobData() {
        binding.progress.setVisibility(View.VISIBLE);
        if (ValidationUtils.isOnline(binding.linearlayout, this)) {
            sendDataOfJob(accessToken, country_id, cityid, state_id, districtid, nationality_id, fee.toString(),
                    age, language, jobchoice, work, mariStatus, kidsStatus, hijabStatus, education,
                    drivingStatus, smokeStatus, alcoholStatus, work_id, familyStatus, totalExperience);

        }
    }

    public void getFilterAgencyData() {

        binding.progress.setVisibility(View.VISIBLE);
        if (ValidationUtils.isOnline(binding.linearlayout, this)) {
            sendDataOfAgency(accessToken, country_id, state_id);
        }
    }

    private void sendDataOfMaid(String access_token,
                                String country_id,
                                String city_id,
                                String state_id,
                                String district_id,
                                String nationality_id,
                                String fees,
                                String age,
                                String languages_ids,
                                String jobChoice_ids,
                                String work_status,
                                String marital_status,
                                String kids,
                                String hi_job,
                                String education_ids,
                                String driving_licence,
                                String smoke,
                                String alcohol,
                                String work_type,
                                String family,
                                String rating,
                                String fees_currency) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApiForMaidList> call = apiService.getMaidListByFilter(
                access_token,
                country_id,
                city_id,
                state_id,
                district_id,
                nationality_id,
                fees,
                age,
                languages_ids,
                jobChoice_ids,
                work_status,
                marital_status,
                kids,
                hi_job,
                education_ids,
                driving_licence,
                smoke,
                alcohol,
                work_type,
                family,
                rating,
                fees_currency);
        call.enqueue(new Callback<RegisterApiForMaidList>() {

            @Override
            public void onResponse(Call<RegisterApiForMaidList> call,
                                   Response<RegisterApiForMaidList> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnFilter.setClickable(true);

                if (response.isSuccessful()) {

                    RegisterApiForMaidList registerApi = response.body();
                    maidListingList = registerApi.getMaidDetailModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("entry_key", 0) == 2) {
                            Intent intent = new Intent(FilterActivity.this,
                                    HomeUserActivity.class);
                            intent.putExtra("filterMaidList", maidListingList);
                            intent.putExtra("filterKey", 1);
                            startActivity(intent);
                            finish();
                        }
                        if (sharedPreference.getInteger("entry_key", 0) == 3) {

                            Intent intent = new Intent(FilterActivity.this,
                                    OtherMaidsProfileActivity.class);
                            intent.putExtra("filterMaidList", maidListingList);
                            intent.putExtra("filterKey", 1);
                            startActivity(intent);
                            finish();
                        }
                    } else

                        Toast.makeText(FilterActivity.this, "Fails", Toast.LENGTH_LONG)
                                .show();

                } else {

                    try {

                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FilterActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(FilterActivity.this, "" + response.errorBody(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApiForMaidList> call, Throwable t) {

                Toast.makeText(FilterActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendDataOfJob(String access_token,
                               String country_id,
                               String city_id,
                               String state_id,
                               String district_id,
                               String nationality_id,
                               String fees,
                               String age,
                               String languages_ids,
                               String jobChoice_ids,
                               String work_status,
                               String marital_status,
                               String kids,
                               String hi_job,
                               String education_ids,
                               String driving_licence,
                               String smoke,
                               String alcohol,
                               String work_type,
                               String family,
                               String experience) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApiList> call = apiService.getJobListByFilter(
                access_token,
                country_id,
                city_id,
                state_id,
                district_id,
                nationality_id,
                fees,
                age,
                languages_ids,
                jobChoice_ids,
                work_status,
                marital_status,
                kids,
                hi_job,
                education_ids,
                driving_licence,
                smoke,
                alcohol,
                work_type,
                family,
                experience);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnFilter.setClickable(true);

                if (response.isSuccessful()) {

                    RegisterApiList registerApi = response.body();
                    jobListingList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("entry_key", 0) == 1) {
                            Intent intent = new Intent(FilterActivity.this,
                                    HomeForMaidActivity.class);
                            intent.putExtra("filterJobList", jobListingList);
                            intent.putExtra("filterKey", 1);
                            startActivity(intent);
                            finish();
                        }
                        if (sharedPreference.getInteger("entry_key", 0) == 3) {
                            Intent intent = new Intent(FilterActivity.this,
                                    HomeAgencyActivity.class);
                            intent.putExtra("filterJobList", jobListingList);
                            intent.putExtra("filterKey", 1);
                            startActivity(intent);
                            finish();
                        }
                    } else

                        Toast.makeText(FilterActivity.this, "Fails", Toast.LENGTH_LONG)
                                .show();

                } else {

                    try {

                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FilterActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(FilterActivity.this, "" + response.errorBody(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApiList> call, Throwable t) {

                Toast.makeText(FilterActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendDataOfAgency(String access_token,
                                  String country_id,
                                  String state_id) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApiList> call = apiService.getAgencyListByFilter(
                access_token,
                country_id,
                state_id);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnFilter.setClickable(true);

                if (response.isSuccessful()) {

                    RegisterApiList registerApi = response.body();
                    agencyDetailList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (agencyDetailList.size() > 0) {
                            Intent intent = new Intent(FilterActivity.this,
                                    AgencyActivity.class);
                            intent.putExtra("filterAgencyList", agencyDetailList);
                            intent.putExtra("filterKey", 1);
                            startActivity(intent);
                            finish();
                        } else
                            Toast.makeText(FilterActivity.this, "No List Found! Try Again"
                                    , Toast.LENGTH_SHORT).show();

                    } else

                        Toast.makeText(FilterActivity.this, "Fails", Toast.LENGTH_LONG)
                                .show();

                } else {

                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(FilterActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(FilterActivity.this, "" + response.errorBody(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApiList> call, Throwable t) {

                Toast.makeText(FilterActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}