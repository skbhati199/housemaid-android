package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.MaidHomeListAdapter;
import com.housemaid.databinding.ActivityUserJobListingBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserJobListingActivity extends BaseActivity implements View.OnClickListener, callMethod {

    ActivityUserJobListingBinding binding;
    MaidHomeListAdapter maidHomeListAdapter;
    ArrayList<SignUpModel> jobListingList;
    private SharedPreference sharedPreference;
    String accessToken;
    SignUpModel signUpModel;
    int user_id;
    String key = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_job_listing);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        user_id = getIntent().getIntExtra("user_id", 0);

        binding.toolbar.tvTitle.setText(R.string.job_listing);
        binding.progress.setVisibility(View.VISIBLE);
        getJobListing(accessToken, key, String.valueOf(user_id));

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void getJobListing(String access_token, String key, String user_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.getUserJobList(access_token, key, user_id);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    jobListingList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {
                        if (jobListingList.size() > 0) {
                            calls();
                        } else binding.tvNoData.setVisibility(View.VISIBLE);
                    } else {

                        Toast.makeText(UserJobListingActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(UserJobListingActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(UserJobListingActivity.this, new Gson().fromJson
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
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(UserJobListingActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void makeFavourites(String access_token, String key, String jobListingId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.makeFavourite(access_token, key, jobListingId);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    jobListingList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(UserJobListingActivity.this, R.string.removed_from_favorites,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserJobListingActivity.this, R.string.added_to_favorites,
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(UserJobListingActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(UserJobListingActivity.this, "" + response.errorBody(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(UserJobListingActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void calls() {
        String accessFrom = "";
        maidHomeListAdapter = new MaidHomeListAdapter(UserJobListingActivity.this,
                jobListingList, this, accessFrom);
        binding.rvUserJobListing.setLayoutManager(new LinearLayoutManager
                (UserJobListingActivity.this));
        binding.rvUserJobListing.setAdapter(maidHomeListAdapter);
    }

    @Override
    public void makeFavourite(int key, int jobListId) {
        makeFavourites(accessToken, String.valueOf(key), String.valueOf(jobListId));

    }

    @Override
    public void makeUnfavourite(int key, int jobListId, int position) {

    }

    @Override
    public void removeMaid(int position) {

    }
}