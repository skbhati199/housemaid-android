package com.housemaid.activities.maid.beforeHome;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.NationalityListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.activities.dataList.WorkingchoicesListActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityCompleteProfileMaid1Binding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteProfileMaid1 extends BaseActivity implements View.OnClickListener {

    ActivityCompleteProfileMaid1Binding binding;
    private int count = 0;
    private int count2 = 0;
    String maritalStatus[];
    String kidStatus[];
    String hijab[];
    String mariStatus;
    String kidsStatus;
    String hijabStatus;
    SharedPreference sharedPreference;
    String countryName;
    int id;
    String stateName;
    int stateID;
    String nationality;
    int nationalityID;
    String accessToken;
    String gender="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_profile_maid1);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.complete_your_profile);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");

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
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivBack:
                onBackPressed();
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

            case R.id.btnNext:
                if (binding.tvCountry.getText ().toString ().trim ().isEmpty ()){
                    Toast.makeText ( this, R.string.please_select_country_name, Toast.LENGTH_SHORT ).show ();
                }else if (binding.tvState.getText ().toString ().trim ().isEmpty ()){
                    Toast.makeText ( this,  R.string.please_select_state, Toast.LENGTH_SHORT ).show ();
                }else if (binding.tvDob.getText ().toString ().trim ().isEmpty ()){
                    Toast.makeText ( this, getString(R.string.please_select_your_dob), Toast.LENGTH_SHORT ).show ();
                }else if (binding.tvNationality.getText ().toString ().trim ().isEmpty ()){
                    Toast.makeText ( this, getString(R.string.please_select_nationality), Toast.LENGTH_SHORT ).show ();
                }else if (gender.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_gender), Toast.LENGTH_SHORT ).show ();
                }else if (mariStatus.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_your_marital_status), Toast.LENGTH_SHORT ).show ();
                }else if (kidsStatus.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_your_kid_status), Toast.LENGTH_SHORT ).show ();
                }else if (hijabStatus.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_hijab), Toast.LENGTH_SHORT ).show ();
                }else {
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
    protected void onResume() {
        super.onResume();
       hideKeyboard(this);

    }
    public static void hideKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = context.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(context);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 512 && resultCode == Activity.RESULT_OK) {
            countryName = data.getStringExtra("country");
            id = data.getIntExtra("id", 0);
            binding.tvCountry.setText(countryName);
        }
        if (requestCode == 520 && resultCode == Activity.RESULT_OK) {
            stateName = data.getStringExtra("state");
            binding.tvState.setText(stateName);
            stateID = data.getIntExtra("state_id", 0);
        }
        if (requestCode == 522 && resultCode == Activity.RESULT_OK) {
            nationality = data.getStringExtra("nationality");
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

        sendDataOfMaid(Constants.TIMEZONE, Constants.LOCALE, accessToken, Constants.userType(this),
                String.valueOf(id), binding.tvDob.getText().toString(), gender, mariStatus, "2",
                String.valueOf(nationalityID), kidsStatus, hijabStatus, String.valueOf(stateID));

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

                if (year >= mDate.get(Calendar.YEAR)-16) {
                    Toast.makeText( CompleteProfileMaid1.this, R.string.atleast_16_years_old,
                            Toast.LENGTH_SHORT).show();
                } else {
                    String str = year + "-" +
                            ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                            ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                    binding.tvDob.setText(str);
                }
            }
        }, year, month, date);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void sendDataOfMaid(String timezone, String locale, String access_token,
                                String user_type, String country_id, String dob,
                                String gender, String marital_status,
                                String step_for_maid_profile, String nationality_id,
                                String kids, String hi_job, String state_id) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApi> call = apiService.completeProfileMaid1(timezone, locale, access_token,
                user_type, country_id, dob, gender, marital_status, step_for_maid_profile,
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
                        sharedPreference.putInteger("step_for_maid_profile", signUpModel.getStep_for_maid_profile());
                        startActivity(new Intent(getApplicationContext (), CompleteProfileMaid2.class));

                    } else

                        Toast.makeText( CompleteProfileMaid1.this, "Fails", Toast.LENGTH_LONG).show();

                } else {

                    try {

                        Toast.makeText(CompleteProfileMaid1.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());  } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApi> call, Throwable t) {

                Toast.makeText( CompleteProfileMaid1.this, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}
