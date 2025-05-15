package com.housemaid.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.databinding.ActivityChangeMobileNumberBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeMobileNumberActivity extends BaseActivity implements View.OnClickListener {

    private ActivityChangeMobileNumberBinding binding;
    private SharedPreference sharedPreference;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_mobile_number);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.change_mobile_number);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnNext.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                openOTP();
                break;
            case R.id.ivBack:
                onBackPressed();

                break;
        }
    }

    private void openOTP() {
        if (ValidationUtils.mobileMatch(binding.etMobileNumber.getText().toString().trim(), this)) {

            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
                binding.progress.setIndeterminate(true);
                binding.progress.setVisibility(View.VISIBLE);
                binding.relativeLayout.setClickable(false);
                binding.btnNext.setClickable(false);

                changeMobileNumber(accessToken,
                        binding.countryCodePicker.getSelectedCountryCodeWithPlus().toString(),
                        binding.etMobileNumber.getText().toString().trim());

            }
        }
    }

    private void changeMobileNumber(String accessToken, String country_code, String mobile) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.changeMobileNumber(accessToken, country_code,
                mobile);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;

                    if (message != null) {
                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("signUp_token", signUpModel.getRemember_token());
                        sharedPreference.putString("otp", signUpModel.getOtp());
                        sharedPreference.putString("start_key", "2");
                        Intent intent = new Intent(ChangeMobileNumberActivity.this,
                                ChangePasswordOTPActivity.class);
                        intent.putExtra("key", 1);
                        startActivity(intent);
                        finishAffinity();

                    } else
                        Toast.makeText(ChangeMobileNumberActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    binding.progress.setVisibility(View.GONE);
                    binding.relativeLayout.setClickable(true);
                    binding.btnNext.setClickable(true);
                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        binding.relativeLayout.setClickable(true);
                        binding.btnNext.setClickable(true);

                        Toast.makeText(ChangeMobileNumberActivity.this, new Gson().fromJson
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
                binding.relativeLayout.setClickable(true);
                binding.btnNext.setClickable(true);
                Toast.makeText(ChangeMobileNumberActivity.this, "Error :", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}