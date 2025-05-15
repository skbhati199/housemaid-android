package com.housemaid.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.AgencyListingAdapter;
import com.housemaid.databinding.ActivityAgencyBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgencyActivity extends BaseActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ActivityAgencyBinding binding;
    private AgencyListingAdapter agencyListingAdapter;
    private SharedPreference sharedPreference;
    private String accessToken;
    private ArrayList<SignUpModel> agencyDetailList;
    private int filterKey;
    private String agencyID;
    private int entry_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency);
        init();
        initControls();
    }

    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
        agencyID = sharedPreference.getString("agency_Id", "");
        entry_key = sharedPreference.getInteger("entry_key", 0);

        filterKey = getIntent().getIntExtra("filterKey", 0);
        agencyDetailList = (ArrayList<SignUpModel>) getIntent().getSerializableExtra(
                "filterAgencyList");
        binding.toolbar.tvTitle.setText(R.string.agencies);
        binding.progress.setVisibility(View.VISIBLE);

        if (filterKey == 1) {
            if (agencyDetailList.size() > 0) {

                calls();
            } else {
                binding.tvNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(R.string.sorry);
            }
        } else {
            if (ValidationUtils.isOnline(getWindow().getDecorView().getRootView(), this)) {
                getAgencyListing(accessToken);
            }

        }
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.swipeRefreshLayout.setOnRefreshListener(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (agencyListingAdapter != null) {
            onRefresh();
        }
    }

    private void getAgencyListing(String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.getAgencyListing(access_token);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {

                binding.progress.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {

                    RegisterApiList registerApi = response.body();
                    agencyDetailList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {


                        if (agencyDetailList.size() > 0) {
                            if (entry_key == 3) {
                                for (int i = 0; i < agencyDetailList.size(); i++) {
                                    if (agencyDetailList.get(i).getId() == Integer.parseInt(agencyID))
                                        agencyDetailList.remove(i);
                                }
                            }
                            calls();

                        } else binding.tvNoData.setVisibility(View.VISIBLE);


                    } else {

                        Toast.makeText(AgencyActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AgencyActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(AgencyActivity.this, new Gson().fromJson
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
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AgencyActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void calls() {
        binding.progress.setVisibility(View.GONE);
        agencyListingAdapter = new AgencyListingAdapter(AgencyActivity.this,
                agencyDetailList, binding);
        binding.rvAgencyListing.setLayoutManager(new LinearLayoutManager(
                AgencyActivity.this));
        binding.rvAgencyListing.setAdapter(agencyListingAdapter);

    }

    @Override
    public void onRefresh() {
        if (filterKey != 1) {
            binding.swipeRefreshLayout.setRefreshing(true);
            getAgencyListing(accessToken);
        }
    }
}