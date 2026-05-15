package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.databinding.ActivityForgotPasswordBinding;
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

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    ActivityForgotPasswordBinding binding;
    String timezone;
    String keycode = "1";
    SharedPreference sharedPreference;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.forgot_password);
        timezone = "Asia/calcutta";
        binding.countryCodePicker.setDefaultCountryUsingNameCode("+90");
        sharedPreference = SharedPreference.getInstance(this);
        if (sharedPreference.getInteger("entry_key", 0) == 1)
            userType = "2";
        if (sharedPreference.getInteger("entry_key", 0) == 2)
            userType = "1";
        if (sharedPreference.getInteger("entry_key", 0) == 3)
            userType = "3";
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                openForgotOTP();
                ValidationUtils.hideSoftKeyboard(this);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void openForgotOTP() {
        if (ValidationUtils.mobileMatch(binding.etMobileNumber.getText().toString().trim(),
                this)) {
            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {

                binding.progress.setIndeterminate(true);
                binding.progress.setVisibility(View.VISIBLE);
                binding.btnNext.setEnabled(false);

                forgetPassword(timezone, binding.countryCodePicker.getSelectedCountryCodeWithPlus(),
                        binding.etMobileNumber.getText().toString().trim(), keycode, userType);
            }
        }
    }

    private void forgetPassword(String timezone, String country_code, String mobile, String key,
                                String user_type) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.forgetPassword(timezone, country_code, mobile,
                key, user_type);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;

                    if (message != null) {
                        binding.btnNext.setEnabled(true);
                        binding.progress.setVisibility(View.GONE);
                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("forget_otp", signUpModel.getOtp());
                        sharedPreference.putInteger("forget_user_id", signUpModel.getId());
                        Intent otpIntent = new Intent(ForgotPasswordActivity.this,
                                ChangePasswordOTPActivity.class);
                        otpIntent.putExtra("key", 2);
                        startActivity(otpIntent);

                    } else {
                        binding.progress.setVisibility(View.GONE);
                        binding.btnNext.setEnabled(true);
                        Toast.makeText(ForgotPasswordActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        binding.btnNext.setEnabled(true);

                        Toast.makeText(ForgotPasswordActivity.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string() +
                                "message : " + response.message());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                binding.btnNext.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Error: ", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}