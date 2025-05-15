package com.housemaid.activities.user.fromHome;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.dataList.CitylistSingleActivity;
import com.housemaid.activities.DistrictSingleListActivity;
import com.housemaid.activities.JobCategorySingleActivity;
import com.housemaid.activities.WorkingchoiceSingleListActivity;
import com.housemaid.activities.agency.fromHome.AddListNextStepActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.ListingTypeListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.databinding.ActivityAddListingFirstPageBinding;
import com.housemaid.utils.SharedPreference;

import java.util.ArrayList;

public class AddListingFirstPageActivity extends BaseActivity implements View.OnClickListener {

    ActivityAddListingFirstPageBinding binding;

    String liveWithFamily[];
    String travel[];
    String travelStatus;
    String familyStatus;
    String countryName;
    String stateName;
    String amount[];
    String amountStatus;
    int stateID;
    int id;
    ArrayList<String> countryIdList;
    String cityName;
    String cityid;
    String districtid;
    String districtName;
    ArrayList<String> stateIdList;
    String listingType;
    int listingTypeID;
    String listingTypeid;
    String countryID;
    String stateid;
    String workingChoiceName;
    String workingid;
    String jobCategoryName;
    String jobid;
    String imagePath;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_listing_first_page);
        init();
        initControls();

    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.add_listing);
        sharedPreference = SharedPreference.getInstance(this);

        liveWithFamily = getResources().getStringArray(R.array.Family);
        showLiveFamily();
        travel = getResources().getStringArray(R.array.TravelSituation);
        showTravelSituation();
        amount = getResources().getStringArray(R.array.amount);
        showAmountSituation();

        Intent intent = getIntent();
        if(intent != null){
            imagePath = intent.getStringExtra("imagePath");
        }
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.rlListingType.setOnClickListener(this);
        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);
        binding.rlDistrict.setOnClickListener(this);
        binding.rlCity.setOnClickListener(this);
        binding.rlWorkingType.setOnClickListener(this);
        binding.rlJobCategory.setOnClickListener(this);
        binding.rlLiveFamily.setOnClickListener(this);
        binding.rlTravelSituation.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rlListingType:
                startActivityForResult(new Intent(this, ListingTypeListActivity.class), 546);
                break;

            case R.id.rlCountry:
                startActivityForResult(new Intent(this, CountryListActivity.class), 530);
                break;

            case R.id.rlState:
                if (binding.tvCountry.getText().length() == 0) {
                    Toast.makeText(this, "Please select Country!", Toast.LENGTH_SHORT).show();
                } else startActivityForResult(new Intent(this, StateListActivity.class), 532);

                break;

            case R.id.rlDistrict:

                if (binding.tvState.getText().length() == 0) {
                    Toast.makeText(this, "Please select State! ", Toast.LENGTH_SHORT).show();
                } else
                    startActivityForResult(new Intent(this, DistrictSingleListActivity.class), 550);
                break;

            case R.id.rlCity:

                if (binding.tvState.getText().length() == 0) {
                    Toast.makeText(this, "Please select State! ", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(new Intent(this, CitylistSingleActivity.class));
                    intent.putExtra("districtId",districtid);
                    startActivityForResult(intent, 552);
                }
                break;

            case R.id.rlWorkingType:
                startActivityForResult(new Intent(this, WorkingchoiceSingleListActivity.class), 554);
                break;

            case R.id.rlJobCategory:
                startActivityForResult(new Intent(this, JobCategorySingleActivity.class), 556);
                break;

            case R.id.rlLiveFamily:
                binding.spinnerFamily.performClick();
                break;

            case R.id.rlTravelSituation:
                binding.spinnerTravelSituation.performClick();
                break;

            case R.id.etCurrency:
                binding.etCurrency.performClick();
                break;

            case R.id.ivBack:
                onBackPressed();
                finish();
                break;

            case R.id.btnNext:

                if (binding.tvListingType.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter listing type", Toast.LENGTH_SHORT).show();
                } else if (binding.tvCountry.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter country name", Toast.LENGTH_SHORT).show();
                } else if (binding.tvState.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter state name", Toast.LENGTH_SHORT).show();
                } else if (binding.tvDistrict.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter district name", Toast.LENGTH_SHORT).show();
                } else if (binding.tvCity.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show();
                } else if (binding.tvWorkingType.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter working type", Toast.LENGTH_SHORT).show();
                } else if (binding.tvJobCategory.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please enter job category", Toast.LENGTH_SHORT).show();
                } else if (binding.etMinRate.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter minimum fee", Toast.LENGTH_SHORT).show();
                } else if (binding.etMaxRate.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter maximum fee", Toast.LENGTH_SHORT).show();
                } else if (binding.etDescription.getText().toString().equals("")) {
                    Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
                } else {

                    int min = Integer.parseInt(binding.etMinRate.getText().toString());
                    int max = Integer.parseInt(binding.etMaxRate.getText().toString());

                    if ((max <= min) && (min > max)) {
                        Toast.makeText(this, "Please enter correct values of Fees!", Toast.LENGTH_SHORT).show();
                    } else {

                        Intent intent = new Intent(this, AddListNextStepActivity.class);
                        intent.putExtra("family", familyStatus);
                        intent.putExtra("travel", travelStatus);
                        intent.putExtra("listingID", listingTypeid);
                        intent.putExtra("country", countryID);
                        intent.putExtra("state", stateid);
                        intent.putExtra("district", districtid);
                        intent.putExtra("city", cityid);
                        intent.putExtra("workingType", workingid);
                        intent.putExtra("jobCategory", jobid);
                        intent.putExtra("minfee", binding.etMinRate.getText().toString().trim());
                        intent.putExtra("maxfee", binding.etMaxRate.getText().toString().trim());
                        intent.putExtra("fees_currency", amountStatus);
                        intent.putExtra("description", binding.etDescription.getText().toString().trim());
                        intent.putExtra("imagePath", imagePath);
                        startActivity(intent);

                        sharedPreference.putString("add", "listing");
                        finish();
                    }
                }
                break;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 546 && resultCode == Activity.RESULT_OK) {

            listingType = data.getStringExtra("listingType");
            listingTypeID = data.getIntExtra("listingTypeId", 0);
            listingTypeid = String.valueOf(listingTypeID);
            binding.tvListingType.setText(listingType);
        }
        if (requestCode == 530 && resultCode == Activity.RESULT_OK) {
            countryName = data.getStringExtra("country");
            id = data.getIntExtra("id", 0);
            countryID = String.valueOf(id);
            countryIdList = new ArrayList<>();
            countryIdList.add(countryID);
            binding.tvCountry.setText(countryName);
        }
        if (requestCode == 532 && resultCode == Activity.RESULT_OK) {
            stateName = data.getStringExtra("state");
            binding.tvState.setText(stateName);
            stateID = data.getIntExtra("state_id", 0);
            stateid = String.valueOf(stateID);
            stateIdList = new ArrayList<>();
            stateIdList.add(stateid);
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
        if (requestCode == 556 && resultCode == Activity.RESULT_OK) {
            jobCategoryName = data.getStringExtra("jobchoice");
            binding.tvJobCategory.setText(jobCategoryName);
            int jobID = data.getIntExtra("id", 0);
            jobid = String.valueOf(jobID);

        }
        if (requestCode == 554 && resultCode == Activity.RESULT_OK) {
            workingChoiceName = data.getStringExtra("working");
            binding.tvWorkingType.setText(workingChoiceName);
            int workingID = data.getIntExtra("id", 0);
            workingid = String.valueOf(workingID);
        }
    }
}
