package com.housemaid.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.housemaid.BuildConfig;
import com.housemaid.R;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
import com.housemaid.activities.maid.fromHome.activity.HomeForMaidActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.databinding.ActivitySettingsBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    SharedPreference  sharedPreference;
    ActivitySettingsBinding binding;
    String hidePhoto;
    String notification;
    String accessToken;
    SignUpModel userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        userProfile = (SignUpModel) getIntent().getSerializableExtra("userProfile");
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.settings);
        setData();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.tvLogout.setOnClickListener(this);
        binding.tvAboutUs.setOnClickListener(this);
        binding.tvHelp.setOnClickListener(this);
        binding.tvContactUs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvAboutUs:
                Intent intent = new Intent(this, SettingWebViewActivity.class);
                intent.putExtra("key_for_page", "about");
                startActivity(intent);
                break;
            case R.id.tvHelp:
                Intent intent1 = new Intent(this, SettingWebViewActivity.class);
                intent1.putExtra("key_for_page", "help");
                startActivity(intent1);
                break;
            case R.id.tvContactUs:
                Intent intent2 = new Intent(this, SettingWebViewActivity.class);
                intent2.putExtra("key_for_page", "contact");
                startActivity(intent2);
                break;

            case R.id.tvLogout:
                sharedPreference.deletePreference();
                binding.progress.setVisibility(View.VISIBLE);
                logoutFromServer(accessToken);

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            startActivity(new Intent(this, HomeForMaidActivity.class));
            finish();
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            startActivity(new Intent(this, HomeUserActivity.class));
            finish();

        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            startActivity(new Intent(this, HomeAgencyActivity.class));
            finish();
        }
    }

    private void setData() {
        String versionName = BuildConfig.VERSION_NAME;
        binding.tvVersion.setText(versionName);

        if (userProfile.getNotification_status() == 1) {
            binding.switchNotification.setChecked(true);
        } else binding.switchNotification.setChecked(false);
        if (userProfile.getPhoto_email_status() == 1) {
            binding.switchHidePhoto.setChecked(true);
        } else binding.switchHidePhoto.setChecked(false);

        binding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notification = "1";
                } else {
                    notification = "0";
                }
                setNotification(accessToken, "1", notification);
            }
        });
        binding.switchHidePhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hidePhoto = "1";

                } else {
                    hidePhoto = "0";
                }
                setHidePhoto(accessToken, "2", hidePhoto);
            }
        });
    }

    private void logoutFromServer(String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.logoutFromServer(access_token);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();

                    String message = registerApi.message;
                    if (message != null) {
                        Intent signInIntent = new Intent(SettingsActivity.this,
                                SelectionActivity.class);
                        startActivity(signInIntent);
                        finishAffinity();

                    } else {

                        Toast.makeText(SettingsActivity.this, "Registration Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(SettingsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {

                            Toast.makeText(SettingsActivity.this, new Gson().fromJson
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(SettingsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setNotification(String access_token, String key, String notification) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.setNotification(access_token, key, notification);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                    } else {

                        Toast.makeText(SettingsActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {

                        Toast.makeText(SettingsActivity.this, ""
                                + response.errorBody().string(), Toast.LENGTH_LONG).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void setHidePhoto(String access_token, String key, String hidePhoto) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.setPhotoEmail(access_token, key, hidePhoto);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                    } else {

                        Toast.makeText(SettingsActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {

                        Toast.makeText(SettingsActivity.this, ""
                                + response.errorBody().string(), Toast.LENGTH_LONG).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                Toast.makeText(SettingsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}