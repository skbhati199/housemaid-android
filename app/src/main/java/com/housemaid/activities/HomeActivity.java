package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.HomeListingAdapter;
import com.housemaid.adapter.MaidHomeListAdapter;
import com.housemaid.databinding.ActivityHomeBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApiForMaidList;
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

public class HomeActivity extends BaseActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, callMethod {

    ActivityHomeBinding binding;
    SharedPreference sharedPreference;
    ArrayList<SignUpModel> jobListingList;
    ArrayList<UserDetailModel> maidListingList;
    MaidHomeListAdapter maidHomeListAdapter;
    HomeListingAdapter homeListingAdapter;
    String accessToken;
    String location;
    double lati;
    double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signIn_token", "0");
        location = getIntent().getStringExtra("location");
        lati = getIntent().getDoubleExtra("latitude", 27.343);
        longi = getIntent().getDoubleExtra("longitude", 84.343);

        binding.toolbar.ivBack.setVisibility(View.GONE);
        binding.toolbar.ivMenu.setVisibility(View.VISIBLE);

        if (location != null) {
            binding.toolbar.tvTitle.setText(location);
        } else binding.toolbar.tvTitle.setText(R.string.no_location);

        binding.progress.setVisibility(View.VISIBLE);

        if ((sharedPreference.getInteger("entry_key", 0) == 1) ||
                (sharedPreference.getInteger("entry_key", 0) == 3)) {
            getJobListing(lati, longi);

        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            getMaidList(lati, longi);
        }

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        //Toolbar Clicks
        binding.toolbar.ivMenu.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);
        //navigationView Clicks
        binding.sidebarContent.tvLogin.setOnClickListener(this);
        binding.sidebarContent.tvSignUp.setOnClickListener(this);
        binding.sidebarContent.civProfilePic.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);

        if ((sharedPreference.getInteger("entry_key", 0) == 1) ||
                (sharedPreference.getInteger("entry_key", 0) == 3)) {
            getJobListing(lati, longi);

        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            getMaidList(lati, longi);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMenu:
                openDrawerLayout();
                break;

            case R.id.ivBack:
                super.onBackPressed();
                break;

            case R.id.tvSignUp:
                startActivity(new Intent(this, SignUpActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.tvLogin:
                startActivity(new Intent(this, SignInActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

        }
    }

    private void openDrawerLayout() {
        ValidationUtils.hideSoftKeyboard(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                binding.drawerLayout, (Toolbar) binding.toolbar.getRoot(),
                R.string.open_drawer, R.string.close_drawer);

        binding.drawerLayout.addDrawerListener(drawerToggle);
        binding.drawerLayout.openDrawer(Gravity.START);
    }

    private void getJobListing(Double lati, Double longi) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.getJobListAtHome(lati, longi);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                binding.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    jobListingList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (jobListingList.size() > 0) {

                            calls();
                        } else binding.tvNoData.setVisibility(View.VISIBLE);

                    } else {

                        Toast.makeText(HomeActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(HomeActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string() +
                                "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calls() {
        binding.swipeRefreshLayout.setRefreshing(false);
        String accessFrom = "withoutSignUp";
        maidHomeListAdapter = new MaidHomeListAdapter(HomeActivity.this, jobListingList,
                this, accessFrom);
        binding.rvHomeListing.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        binding.rvHomeListing.setAdapter(maidHomeListAdapter);
    }

    private void getMaidList(Double lati, Double longi) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForMaidList> call = apiService.getMaidListAtHome(lati, longi);
        call.enqueue(new Callback<RegisterApiForMaidList>() {

            @Override
            public void onResponse(Call<RegisterApiForMaidList> call,
                                   Response<RegisterApiForMaidList> response) {
                binding.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiForMaidList registerApi = response.body();
                    maidListingList = registerApi.getMaidDetailModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (maidListingList.size() > 0) {
                            sharedPreference.putInteger("page_selection", 0);
                            call();
                        } else binding.tvNoData.setVisibility(View.VISIBLE);

                    } else {

                        Toast.makeText(HomeActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        Toast.makeText(HomeActivity.this, new Gson().fromJson
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
            public void onFailure(retrofit2.Call<RegisterApiForMaidList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void call() {
        ArrayList<UserDetailModel> filterList = new ArrayList<>();

        for (int i = 0; i < maidListingList.size(); i++) {
            if (!maidListingList.get(i).getIs_hired()) {
                filterList.add(maidListingList.get(i));
            }
        }
        binding.swipeRefreshLayout.setRefreshing(false);
        String accessFrom = "withoutSignUp";
        homeListingAdapter = new HomeListingAdapter(HomeActivity.this,
                filterList, this, accessFrom);
        binding.rvHomeListing.setLayoutManager(
                new LinearLayoutManager(HomeActivity.this));
        binding.rvHomeListing.setAdapter(homeListingAdapter);

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