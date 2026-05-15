package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.HomeListingAdapter;
import com.housemaid.databinding.ActivityAgencyMaidsBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApiForMaidList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgencyMaidsActivity extends BaseActivity implements View.OnClickListener, callMethod {

    private ActivityAgencyMaidsBinding binding;
    private ArrayList<UserDetailModel> maidListingList;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_maids);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.agency_maids);
        sharedPreference = SharedPreference.getInstance(this);
        binding.toolbar.ivBack.setVisibility(View.VISIBLE);
        binding.progress.setVisibility(View.VISIBLE);
        String accessToken = sharedPreference.getString("signUp_token", "");
        int agency_id = getIntent().getIntExtra("agency_Id", 0);
        getMaidList(accessToken, String.valueOf(agency_id));

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
                finish();
                break;

        }

    }

    private void getMaidList(String access_token, String agency_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForMaidList> call = apiService.maidListUnderAgency(access_token, agency_id);
        call.enqueue(new Callback<RegisterApiForMaidList>() {

            @Override
            public void onResponse(Call<RegisterApiForMaidList> call,
                                   Response<RegisterApiForMaidList> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiForMaidList registerApi = response.body();
                    maidListingList = registerApi.getMaidDetailModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (maidListingList.size() > 0) {
                            calls();
                        } else binding.tvNoData.setVisibility(View.VISIBLE);
                    } else {

                        Toast.makeText(AgencyMaidsActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AgencyMaidsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {

                            Toast.makeText(AgencyMaidsActivity.this, new Gson().fromJson
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiForMaidList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(AgencyMaidsActivity.this, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void calls() {
        String accessFrom = "";
        HomeListingAdapter homeListingAdapter = new HomeListingAdapter(AgencyMaidsActivity.this,
                maidListingList, this, accessFrom);
        binding.rvAgencymaidsListing.setLayoutManager(
                new LinearLayoutManager(AgencyMaidsActivity.this));
        binding.rvAgencymaidsListing.setAdapter(homeListingAdapter);

    }

    @Override
    public void makeFavourite(int key, int jobListId) {

    }

    @Override
    public void makeUnfavourite(int key, int jobListId, int position) {

    }

    @Override
    public void removeMaid(int position) {

    }
}
