package com.housemaid.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivitySignUpBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.housemaid.constants.Constants.TIMEZONE;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySignUpBinding binding;
    private SharedPreference sharedPreference;
    private String refreshedToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnCreateAccount.setOnClickListener(this);
        binding.tvLogin.setOnClickListener(this);
        binding.tvShow.setOnClickListener(this);
        binding.tvConfirmShow.setOnClickListener(this);
        binding.tvTerms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateAccount:
                openVerification();
                break;
            case R.id.tvLogin:
                opensignIn();
                finish();
                break;
            case R.id.tvShow:
                showPassword();
                break;
            case R.id.tvConfirmShow:
                showConfirmPassword();
                break;
            case R.id.tvTerms:
                Intent intent = new Intent(this, SettingWebViewActivity.class);
                intent.putExtra("key_for_page", "terms");
                startActivity(intent);
                break;
        }
    }

    private void opensignIn() {
        Intent signUpIntent = new Intent(this, SignInActivity.class);
        signUpIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(signUpIntent);
        finish();
    }

    private void openVerification() {
        if (validation()) {
            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {

                binding.progress.setIndeterminate(true);
                binding.progress.setVisibility(View.VISIBLE);
                binding.btnCreateAccount.setClickable(false);
                binding.tvLogin.setClickable(false);

                registerUsertoServer(binding.etUserName.getText().toString().trim(),
                        binding.countryCodePicker.getSelectedCountryCodeWithPlus(),
                        binding.etMobileNumber.getText().toString().trim(),
                        binding.etPassword.getText().toString().trim(),
                        refreshedToken,
                        Constants.userType(this),
                        binding.etEmailID.getText().toString());
            }
        }
    }

    private void showPassword() {
        if (binding.tvShow.getText().toString().trim().equalsIgnoreCase("Show")) {
            binding.tvShow.setText(R.string.hide);
            binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (binding.tvShow.getText().toString().trim().equalsIgnoreCase("hide")) {
            binding.tvShow.setText(R.string.show);
            binding.etPassword.setInputType(129);
        }
    }

    private void showConfirmPassword() {
        if (binding.tvConfirmShow.getText().toString().trim().equalsIgnoreCase("Show")) {
            binding.tvConfirmShow.setText(R.string.hide);
            binding.etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (binding.tvConfirmShow.getText().toString().trim().equalsIgnoreCase("hide")) {
            binding.tvConfirmShow.setText(R.string.show);
            binding.etConfirmPassword.setInputType(129);
        }
    }

    private boolean validation() {

        if (ValidationUtils.userNameEmpty(binding.etUserName.getText().toString().trim(),
                this)
                && ValidationUtils.mobileMatch(binding.etMobileNumber.getText().toString().trim(),
                this)
                && ValidationUtils.emailMatch(binding.etEmailID.getText().toString().trim(),
                this)
                && ValidationUtils.passwordEmpty(binding.etPassword.getText().toString().trim(),
                this)
                && ValidationUtils.conformPasswordEmpty(binding.etConfirmPassword.getText().toString().trim(),
                this)
                && ValidationUtils.passwordMatch(binding.etPassword.getText().toString().trim(),
                binding.etConfirmPassword.getText().toString().trim(), this)
                && ValidationUtils.check(binding.checkbox, this)) {
            return true;
        }
        return false;
    }

    private void registerUsertoServer(String username, String countryCode,
                                      String mobile, String password, String device_token,
                                      String user_type, String email) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.registerUser(TIMEZONE, username, countryCode,
                mobile, password, device_token, Constants.DEVICE_TYPE, user_type, email);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("user_id", String.valueOf(signUpModel.getId()));
                        sharedPreference.putString("opt_verified", signUpModel.getOtp_verified());
                        sharedPreference.putString("otp", signUpModel.getOtp());
                        sharedPreference.putString("signUp_token", signUpModel.getRemember_token());
                        sharedPreference.putString("start_key", "1");

                        if (signUpModel.getDisable_enable_status().equals("0")) {
                            sharedPreference.putInteger("disable_key", 1);
                        } else sharedPreference.putInteger("disable_key", 0);

                        startActivity(new Intent(SignUpActivity.this,
                                ChangePasswordOTPActivity.class).putExtra("key", 1));

                        finish();

                    } else
                        Toast.makeText(SignUpActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();

                    binding.progress.setVisibility(View.GONE);
                    binding.btnCreateAccount.setClickable(true);
                    binding.tvLogin.setClickable(true);

                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        binding.btnCreateAccount.setClickable(true);
                        binding.tvLogin.setClickable(true);

                        if (response.code() == 400) {
                            binding.progress.setVisibility(View.GONE);

                            Toast.makeText(SignUpActivity.this, new Gson().fromJson(response.errorBody().string(),
                                    ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, response.errorBody().toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() + "message : "
                                    + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {

                binding.progress.setVisibility(View.GONE);
                binding.btnCreateAccount.setClickable(true);
                binding.tvLogin.setClickable(true);
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}