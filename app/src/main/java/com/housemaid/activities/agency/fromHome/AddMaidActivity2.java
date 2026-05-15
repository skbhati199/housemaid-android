package com.housemaid.activities.agency.fromHome;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.NationalityListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityAddMaid2Binding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMaidActivity2 extends BaseActivity implements View.OnClickListener {

    ActivityAddMaid2Binding binding;
    private int count = 0;
    private int count2 = 0;
    private String maritalStatus[];
    private String kidStatus[];
    private String hijab[];
    private String mariStatus;
    private String kidsStatus;
    private String hijabStatus;
    private SharedPreference sharedPreference;
    private int id;
    private int stateID;
    private int nationalityID;
    private String accessToken;
    private String gender;
    private int maid_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_maid2);
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
        maid_id = getIntent().getIntExtra("add_maid_id", 0);

        maritalStatus = getResources().getStringArray(R.array.maritalStatus);
        showMaritalStatus();
        kidStatus = getResources().getStringArray(R.array.kidStatus);
        showkidStatus();
        hijab = getResources().getStringArray(R.array.hijab);
        showHijabStatus();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.tvDob.setOnClickListener(this);
        binding.tvFemale.setOnClickListener(this);
        binding.tvMale.setOnClickListener(this);
        binding.tvFemaleColor.setOnClickListener(this);
        binding.tvMaleColor.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.rlMaritalStatus.setOnClickListener(this);
        binding.rlKidsStatus.setOnClickListener(this);
        binding.rlHijob.setOnClickListener(this);
        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);
        binding.rlNationality.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

            case R.id.rlMaritalStatus:
                binding.spinnerMaritalStatus.performClick();
                break;

            case R.id.rlKidsStatus:
                binding.spinnerKidStatus.performClick();
                break;

            case R.id.rlHijob:
                binding.spinnerHijab.performClick();
                break;

            case R.id.btnNext:
                if (binding.tvCountry.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_country_name, Toast.LENGTH_SHORT)
                            .show();
                } else if (binding.tvState.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_state_name, Toast.LENGTH_SHORT)
                            .show();
                } else if (binding.tvDob.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_dob, Toast.LENGTH_SHORT)
                            .show();
                } else if (gender.equals("")) {
                    Toast.makeText(this, R.string.please_select_your_gender, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    openNextActivity();
                }

                break;

            case R.id.tv_dob:
                onSelectDate();
                break;

            case R.id.tv_male:
                count++;
                if (count % 2 == 1) {
                    binding.tvMaleColor.setVisibility(View.VISIBLE);
                    gender = "male";
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
                    binding.tvFemaleColor.setVisibility(View.VISIBLE);
                    gender = "female";
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
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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

    private void openNextActivity() {

        binding.progress.setVisibility(View.VISIBLE);
        binding.btnNext.setClickable(false);
        binding.rlCountry.setClickable(false);
        binding.rlNationality.setClickable(false);
        binding.rlState.setClickable(false);

        sendDataOfMaid(
                accessToken,
                String.valueOf(id),
                String.valueOf(maid_id),
                binding.tvDob.getText().toString(),
                gender,
                mariStatus,
                String.valueOf(nationalityID),
                kidsStatus,
                hijabStatus,
                String.valueOf(stateID));
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

                if (year >= mDate.get(Calendar.YEAR) - 16) {
                    Toast.makeText(AddMaidActivity2.this, R.string.atleast_16_years_old,
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

    private void sendDataOfMaid(String access_token, String country_id,
                                String maid_id, String dob, String gender, String marital_status,
                                String nationality_id,
                                String kids, String hi_job, String state_id) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApi> call = apiService.agencyAddMaid2(Constants.TIMEZONE, access_token, country_id, maid_id,
                dob, gender, marital_status, "2",
                nationality_id, kids, hi_job, state_id);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnNext.setClickable(true);
                binding.rlCountry.setClickable(true);
                binding.rlNationality.setClickable(true);
                binding.rlState.setClickable(true);
                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("complete_profile", signUpModel.getComplete_profile());
                        Intent intent = new Intent(getApplicationContext(), AddMaidActivity3.class);
                        intent.putExtra("maid_id", signUpModel.getId());
                        startActivity(intent);
                        finish();

                    } else

                        Toast.makeText(AddMaidActivity2.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG)
                                .show();

                } else {

                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddMaidActivity2.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(AddMaidActivity2.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", R.string.error + response.errorBody().string()
                                    + R.string.message + response.message());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApi> call, Throwable t) {

                Toast.makeText(AddMaidActivity2.this, R.string.error + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}