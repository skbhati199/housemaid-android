package com.housemaid.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.fragments.BuyCreditFragment;
import com.housemaid.activities.fragments.EarnCreditFragment;
import com.housemaid.adapter.ViewPagerAdapter;
import com.housemaid.databinding.ActivityUpgradeMemberShipBinding;
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

public class UpgradeMemberShipActivity extends BaseActivity implements View.OnClickListener {

    ActivityUpgradeMemberShipBinding binding;
    ViewPagerAdapter viewPagerAdapter;
    SharedPreference sharedPreference;
    String accessToken;
    SignUpModel totalCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upgrade_member_ship);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.subscription);
        binding.toolbar.tvTotalCredit.setVisibility(View.VISIBLE);
        binding.toolbar.tvTotalCredit.setText(R.string.balance);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
        getTotalCredits(accessToken);
        setFragmentOnViewPager();
        binding.tabLayout.getTabAt(1).select();
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

    private void setFragmentOnViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addfragment(new EarnCreditFragment(), "Earn Credit");
        viewPagerAdapter.addfragment(new BuyCreditFragment(), "Buy Credit");
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    private void getTotalCredits(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getTotalCredit(accessToken);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    totalCredits = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", totalCredits.getTotal_credit());
                        binding.toolbar.tvTotalCredit.setText("Balance : " + totalCredits.getTotal_credit());

                    } else {

                        Toast.makeText(UpgradeMemberShipActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(UpgradeMemberShipActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(UpgradeMemberShipActivity.this, new Gson().fromJson
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
                Toast.makeText(UpgradeMemberShipActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}
