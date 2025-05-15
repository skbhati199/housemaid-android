package com.housemaid.activities;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.housemaid.R;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
import com.housemaid.activities.maid.beforeHome.CompleteProfileMaid1;
import com.housemaid.activities.maid.beforeHome.CompleteProfileMaid2;
import com.housemaid.activities.maid.beforeHome.CompleteProfileMaid3;
import com.housemaid.activities.maid.beforeHome.CompleteProfileMaid4;
import com.housemaid.activities.maid.fromHome.activity.HomeForMaidActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivitySignInBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySignInBinding binding;
    private String refreshedToken;
    private SharedPreference sharedPreference;
    private SignUpModel signUpModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();

        FirebaseApp.initializeApp(this);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            refreshedToken = instanceIdResult.getToken();
            Log.d("TOKEN: ", ""+refreshedToken);
        }).addOnFailureListener(this,
                ex->ex.printStackTrace());


        sharedPreference = SharedPreference.getInstance(this);

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnLogin.setOnClickListener(this);
        binding.tvSignUp.setOnClickListener(this);
        binding.tvForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvForgotPassword:
                openForgotPassword();
                break;
            case R.id.btnLogin:
                openCompleteProfile();
                break;
            case R.id.tvSignUp:
                openSignUp();
                break;
        }
    }


   /* private boolean CheckGpsStatus() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }*/

    private void openSignUp() {
        Intent signupIntent = new Intent(this, SignUpActivity.class);
        signupIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(signupIntent);
        finish();
    }

    private void openCompleteProfile() {
        if (validation()) {
            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {

                binding.progress.setIndeterminate(true);
                binding.progress.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
                binding.tvSignUp.setEnabled(false);
                binding.tvForgotPassword.setEnabled(false);

                registerUsertoServer(binding.etMobileNumber.getText().toString().trim(),
                        binding.etPassword.getText().toString(),
                        refreshedToken,
                        Constants.userType(this));
            }
        }
    }

    private void openForgotPassword() {
        Intent forgotIntent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(forgotIntent);
    }

    private boolean validation() {

        return ValidationUtils.mobileMatch(binding.etMobileNumber.getText().toString().trim(),
                this)
                && ValidationUtils.passwordEmpty(binding.etPassword.getText().toString().trim(),
                this);
    }

    private void registerUsertoServer(String mobile, String password, String device_token,
                                      String user_type) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.loginUser(mobile, password, device_token,
                Constants.DEVICE_TYPE, user_type);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    assert registerApi != null;
                    String message = registerApi.message;

                    if (message != null) {
                        RegisterApi registerApi1 = response.body();
                        assert registerApi1 != null;
                        signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("signUp_token", signUpModel.getRemember_token());
                        sharedPreference.putString("start_key", "3");
                        sharedPreference.putString("login_otp", signUpModel.getOtp());
                        sharedPreference.putString("user_id", String.valueOf(signUpModel.getId()));

                        if (signUpModel.getDisable_enable_status().equals("0")) {
                            sharedPreference.putInteger("disable_key", 1);
                        } else sharedPreference.putInteger("disable_key", 0);


                        if (signUpModel.getOtp_verified().equals("0")) {
                            startActivity(new Intent(SignInActivity.this,
                                    ChangePasswordOTPActivity.class)
                                    .putExtra("key", 3)
                            .putExtra("user_id", signUpModel.getId()));
                            finish();

                        } else if (signUpModel.getComplete_profile().equals("0")) {
                            if (sharedPreference.getInteger("entry_key", 0) == 1) {
                                if (signUpModel.getStep_for_maid_profile() == 0) {
                                    startActivity(new Intent(SignInActivity.this,
                                            CompleteProfileActivity.class));
                                    finishAffinity();
                                } else if (signUpModel.getStep_for_maid_profile() == 1) {
                                    startActivity(new Intent(SignInActivity.this,
                                            CompleteProfileMaid1.class));
                                    finishAffinity();
                                } else if (signUpModel.getStep_for_maid_profile() == 2) {
                                    startActivity(new Intent(SignInActivity.this,
                                            CompleteProfileMaid2.class));
                                    finishAffinity();
                                } else if (signUpModel.getStep_for_maid_profile() == 3) {
                                    startActivity(new Intent(SignInActivity.this,
                                            CompleteProfileMaid3.class));
                                    finishAffinity();
                                } else if (signUpModel.getStep_for_maid_profile() == 4) {
                                    startActivity(new Intent(SignInActivity.this,
                                            CompleteProfileMaid4.class));
                                    finishAffinity();
                                }

                            } else {
                                startActivity(new Intent(SignInActivity.this,
                                        CompleteProfileActivity.class));
                                finishAffinity();
                            }

                        } else if (signUpModel.getComplete_profile().equals("1") &&
                                (signUpModel.getOtp_verified().equals("1"))) {

                            if (signUpModel.getLocation_text() != null) {

                                if (sharedPreference.getInteger("entry_key", 0) == 1) {
                                    sharedPreference.putString("location", signUpModel.getLocation_text());
                                    Intent intent = new Intent(SignInActivity.this,
                                            HomeForMaidActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else if (sharedPreference.getInteger("entry_key", 0) == 2) {
                                    sharedPreference.putString("location", signUpModel.getLocation_text());
                                    Intent intent = new Intent(SignInActivity.this,
                                            HomeUserActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    sharedPreference.putString("location", signUpModel.getLocation_text());
                                    Intent intent = new Intent(SignInActivity.this,
                                            HomeAgencyActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                }
                            } else {
                                Intent intent = new Intent(SignInActivity.this,
                                        EnterLocationActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        }
                    } else
                        Toast.makeText(SignInActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    binding.progress.setVisibility(View.GONE);
                    binding.tvForgotPassword.setEnabled(true);
                    binding.btnLogin.setEnabled(true);
                    binding.tvSignUp.setEnabled(true);
                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.tvForgotPassword.setEnabled(true);
                    binding.btnLogin.setEnabled(true);
                    binding.tvSignUp.setEnabled(true);


                    try {
                        Toast.makeText(SignInActivity.this,
                                new Gson().fromJson(response.errorBody().string(), ErrorResponse.class).getMessage(),
                                Toast.LENGTH_SHORT).show();
                        /*Log.d("TEST", "Error : " + response.errorBody().string() +
                                "message : " + response.message());*/


                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {

                binding.progress.setVisibility(View.GONE);
                binding.tvForgotPassword.setEnabled(true);
                binding.btnLogin.setEnabled(true);
                binding.tvSignUp.setEnabled(true);
                Toast.makeText(SignInActivity.this, "Error: ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}