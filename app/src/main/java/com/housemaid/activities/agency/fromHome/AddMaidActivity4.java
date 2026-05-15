package com.housemaid.activities.agency.fromHome;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.dataList.CitylistActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.DistrictListActivity;
import com.housemaid.activities.dataList.JobChoiceListActivity;
import com.housemaid.activities.dataList.SkillsListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.activities.dataList.WorkingchoicesListActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityAddMaid4Binding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMaidActivity4 extends BaseActivity implements View.OnClickListener {

    private ActivityAddMaid4Binding binding;
    private String liveWithFamily[];
    private String travel[];
    private String amount[];
    private String travelStatus;
    private String amountStatus;
    private String familyStatus;
    private ArrayList<String> countryIdList;
    private ArrayList<String> stateIdList;
    private ArrayList<String> districtList = new ArrayList<>();
    private ArrayList<String> districtIDList = new ArrayList<>();
    private ArrayList<String> cityList = new ArrayList<>();
    private ArrayList<String> cityIdList = new ArrayList<>();
    private ArrayList<String> jobChoiceList = new ArrayList<>();
    private ArrayList<String> joChoiceIdList = new ArrayList<>();
    private ArrayList<String> skillsList = new ArrayList<>();
    private ArrayList<String> skillsIdList = new ArrayList<>();
    private ArrayList<String> workingList = new ArrayList<>();
    private ArrayList<String> workingIdList = new ArrayList<>();
    private String name;
    private String name1;
    private String name2;
    private String name3;
    private String name4;
    private String accessToken;
    private SharedPreference sharedPreference;
    private int maid_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_maid4);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.ivBack.setVisibility(View.GONE);
        binding.toolbar.tvTitle.setText(R.string.complete_maid_profile);
        sharedPreference = SharedPreference.getInstance(this);

        accessToken = sharedPreference.getString("signUp_token", "0");
        maid_id = getIntent().getIntExtra("maid_id", 0);

        liveWithFamily = getResources().getStringArray(R.array.Family);
        showLiveFamily();
        travel = getResources().getStringArray(R.array.TravelSituation);
        showTravelSituation();
        amount = getResources().getStringArray(R.array.amount);
        showamountSituation();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnNext.setOnClickListener(this);
        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);
        binding.rlDistrict.setOnClickListener(this);
        binding.rlCity.setOnClickListener(this);
        binding.rlJobChoice.setOnClickListener(this);
        binding.rlSkills.setOnClickListener(this);
        binding.rlWorkingChoices.setOnClickListener(this);
        binding.rlLiveFamily.setOnClickListener(this);
        binding.rlTravelSituation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnNext:
                if (binding.tvCountry.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_select_country),
                            Toast.LENGTH_SHORT).show();
                } else if (binding.tvState.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_select_state),
                            Toast.LENGTH_SHORT).show();
                } else if (binding.tvJobChoice.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_select_job_choice),
                            Toast.LENGTH_SHORT).show();
                } else if (binding.tvworkingChoice.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_select_working_choice),
                            Toast.LENGTH_SHORT).show();
                } else if (travelStatus.equals("")) {
                    Toast.makeText(this, getString(R.string.please_select_travel_status),
                            Toast.LENGTH_SHORT).show();
                } else if (familyStatus.equals("")) {
                    Toast.makeText(this, getString(R.string.please_select_live_with_family),
                            Toast.LENGTH_SHORT).show();
                } else if (binding.etRate.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_select_amount),
                            Toast.LENGTH_SHORT).show();
                } else if (amountStatus.equals("Currency")) {
                    Toast.makeText(this, getString(R.string.please_select_currency_type),
                            Toast.LENGTH_SHORT).show();
                } else if (binding.tvSkill.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, getString(R.string.please_select_working_skills),
                            Toast.LENGTH_SHORT).show();
                } else {
                    openNextActivity();
                }

                break;

            case R.id.rlCountry:
                startActivityForResult(new Intent(this, CountryListActivity.class),
                        530);
                break;

            case R.id.rlState:
                startActivityForResult(new Intent(this, StateListActivity.class),
                        532);
                break;
            case R.id.rlDistrict:
                Intent districtIntent = new Intent(this, DistrictListActivity.class);
                districtIntent.putExtra("districtIDList", districtIDList);
                districtIntent.putExtra("districtList", districtList);
                startActivityForResult(districtIntent, 534);
                break;

            case R.id.rlCity:
                Intent cityIntent = new Intent(this, CitylistActivity.class);
                cityIntent.putExtra("cityIdList", cityIdList);
                cityIntent.putExtra("cityList", cityList);
                startActivityForResult(cityIntent, 536);
                break;

            case R.id.rlJobChoice:
                Intent jobIntent = new Intent(this, JobChoiceListActivity.class);
                jobIntent.putExtra("joChoiceIdList", joChoiceIdList);
                jobIntent.putExtra("jobChoiceList", jobChoiceList);
                startActivityForResult(jobIntent, 538);
                break;

            case R.id.rlSkills:
                Intent skillsIntent = new Intent(this, SkillsListActivity.class);
                skillsIntent.putExtra("skillsIdList", skillsIdList);
                skillsIntent.putExtra("skillsList", skillsList);
                startActivityForResult(skillsIntent, 540);
                break;

            case R.id.rlWorkingChoices:
                Intent workingIntent = new Intent(this, WorkingchoicesListActivity.class);
                workingIntent.putExtra("workingIdList", workingIdList);
                workingIntent.putExtra("workingList", workingList);
                startActivityForResult(workingIntent, 542);
                break;
            case R.id.rlLiveFamily:
                binding.spinnerFamily.performClick();
                break;

            case R.id.rlTravelSituation:
                binding.spinnerTravelSituation.performClick();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 530 && resultCode == Activity.RESULT_OK) {
            String countryName = data.getStringExtra("country");
            int id = data.getIntExtra("id", 0);
            String countryID = String.valueOf(id);
            countryIdList = new ArrayList<>();
            countryIdList.add(countryID);
            binding.tvCountry.setText(countryName);
        }
        if (requestCode == 532 && resultCode == Activity.RESULT_OK) {
            String stateName = data.getStringExtra("state");
            binding.tvState.setText(stateName);
            int stateID = data.getIntExtra("state_id", 0);
            String stateid = String.valueOf(stateID);
            stateIdList = new ArrayList<>();
            stateIdList.add(stateid);
        }
        if (requestCode == 534 && resultCode == Activity.RESULT_OK) {
            districtList = data.getStringArrayListExtra("nameList");
            districtIDList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < districtList.size(); i++) {
                if (i == 0) {
                    name = districtList.get(i);
                } else {
                    name = name + ", " + districtList.get(i);
                }
            }
            binding.tvDistrict.setText(name);
        }
        if (requestCode == 536 && resultCode == Activity.RESULT_OK) {
            cityList = data.getStringArrayListExtra("nameList");
            cityIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < cityList.size(); i++) {
                if (i == 0) {
                    name1 = cityList.get(i);
                } else {
                    name1 = name1 + ", " + cityList.get(i);
                }
            }
            binding.tvCity.setText(name1);
        }
        if (requestCode == 538 && resultCode == Activity.RESULT_OK) {
            jobChoiceList = data.getStringArrayListExtra("nameList");
            joChoiceIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < jobChoiceList.size(); i++) {
                if (i == 0) {
                    name2 = jobChoiceList.get(i);
                } else {
                    name2 = name2 + ", " + jobChoiceList.get(i);
                }
            }
            binding.tvJobChoice.setText(name2);
        }
        if (requestCode == 540 && resultCode == Activity.RESULT_OK) {
            skillsList = data.getStringArrayListExtra("nameList");
            skillsIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < skillsList.size(); i++) {
                if (i == 0) {
                    name3 = skillsList.get(i);
                } else {
                    name3 = name3 + ", " + skillsList.get(i);
                }
            }
            binding.tvSkill.setText(name3);
        }
        if (requestCode == 542 && resultCode == Activity.RESULT_OK) {
            workingList = data.getStringArrayListExtra("nameList");
            workingIdList = data.getStringArrayListExtra("idList");
            for (int i = 0; i < workingList.size(); i++) {
                if (i == 0) {
                    name4 = workingList.get(i);
                } else {
                    name4 = name4 + ", " + workingList.get(i);
                }
            }
            binding.tvworkingChoice.setText(name4);
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

    private void openNextActivity() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.btnNext.setClickable(false);
        binding.rlSkills.setClickable(false);
        binding.rlWorkingChoices.setClickable(false);
        binding.rlJobChoice.setClickable(false);
        String step = "4";
        sendDataOfMaid(accessToken,
                String.valueOf(maid_id),
                stateIdList.toString(),
                countryIdList.toString(),
                cityIdList.toString(),
                districtIDList.toString(),
                joChoiceIdList.toString(),
                workingIdList.toString(), step, familyStatus, travelStatus,
                binding.etRate.getText().toString().trim(),
                skillsIdList.toString());

    }

    private void sendDataOfMaid(String access_token,
                                String maid_id,
                                String working_states,
                                String maid_can_work_country_id,
                                String city,
                                String district,
                                String maid_job_choice_ids,
                                String maid_working_style_ids,
                                String step_for_maid_profile,
                                String can_live_with_family,
                                String travel_situation,
                                String expected_fees,
                                String maid_skill_ids) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApi> call = apiService.agencyAddMaid4(Constants.TIMEZONE, access_token,
                maid_id, working_states, maid_can_work_country_id,
                city, district, maid_job_choice_ids, maid_working_style_ids,
                step_for_maid_profile, can_live_with_family, travel_situation,
                expected_fees, maid_skill_ids, amountStatus);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnNext.setClickable(true);
                binding.rlSkills.setClickable(true);
                binding.rlWorkingChoices.setClickable(true);
                binding.rlJobChoice.setClickable(true);
                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("complete_profile",
                                signUpModel.getComplete_profile());
                        sharedPreference.putInteger("step_for_maid_profile",
                                signUpModel.getStep_for_maid_profile());
                        Intent intent = new Intent(AddMaidActivity4.this,
                                AddMaidActivity5.class);
                        intent.putExtra("maid_id", signUpModel.getId());
                        startActivity(intent);
                        finish();
                    } else

                        Toast.makeText(AddMaidActivity4.this, "Fails",
                                Toast.LENGTH_LONG).show();

                } else {

                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddMaidActivity4.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(AddMaidActivity4.this, ""
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

                Toast.makeText(AddMaidActivity4.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
