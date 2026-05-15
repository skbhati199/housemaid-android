package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
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
import com.google.firebase.messaging.FirebaseMessaging;
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
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            refreshedToken = token;
        }).addOnFailureListener(ex -> {
            refreshedToken = "dummy_token_fallback";
            Log.w("TOKEN", "Firebase failed, using fallback: " + ex.getMessage());
        });
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
        if (v.getId() == R.id.btnCreateAccount) {

                openVerification();
                
            
} else if (v.getId() == R.id.tvLogin) {

                opensignIn();
                finish();
                
            
} else if (v.getId() == R.id.tvShow) {

                showPassword();
                
            
} else if (v.getId() == R.id.tvConfirmShow) {

                showConfirmPassword();
                
            
} else if (v.getId() == R.id.tvTerms) {

                Intent intent = new Intent(this, SettingWebViewActivity.class);
                intent.putExtra("key_for_page", "terms");
                startActivity(intent);
                
        
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
                    assert registerApi != null;
                    String message = registerApi.message;

                    if (message != null && "1".equals(registerApi.getStatus())
                            && registerApi.signUpModel != null) {

                        SignUpModel signUpModel = registerApi.signUpModel;
                        sharedPreference.putString("user_id", String.valueOf(signUpModel.getId()));
                        sharedPreference.putString("opt_verified", signUpModel.getOtp_verified());
                        sharedPreference.putString("otp", signUpModel.getOtp());
                        sharedPreference.putString("signUp_token", signUpModel.getRemember_token());
                        sharedPreference.putString("start_key", "1");

                        if ("0".equals(signUpModel.getDisable_enable_status())) {
                            sharedPreference.putInteger("disable_key", 1);
                        } else sharedPreference.putInteger("disable_key", 0);

                        startActivity(new Intent(SignUpActivity.this,
                                ChangePasswordOTPActivity.class).putExtra("key", 1));

                        finish();

                    } else {
                        Toast.makeText(SignUpActivity.this,
                                message != null ? message : "Registration failed",
                                Toast.LENGTH_LONG).show();
                    }

                    binding.progress.setVisibility(View.GONE);
                    binding.btnCreateAccount.setClickable(true);
                    binding.tvLogin.setClickable(true);

                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.btnCreateAccount.setClickable(true);
                    binding.tvLogin.setClickable(true);

                    try {
                        String errorBody = response.errorBody().string();
                        try {
                            ErrorResponse err = new Gson().fromJson(errorBody, ErrorResponse.class);
                            Toast.makeText(SignUpActivity.this,
                                    err != null && err.getMessage() != null ? err.getMessage() : "Registration failed",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception jsonEx) {
                            Toast.makeText(SignUpActivity.this,
                                    "Registration failed: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
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