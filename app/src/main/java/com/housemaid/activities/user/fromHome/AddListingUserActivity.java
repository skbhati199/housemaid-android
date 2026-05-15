package com.housemaid.activities.user.fromHome;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.databinding.ActivityAddListingUserBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.StatusModel;
import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddListingUserActivity extends BaseActivity implements View.OnClickListener {

    ActivityAddListingUserBinding binding;
    private String credits;
    String accessToken;
    SharedPreference sharedPreference;
    private int totalCredits ;
    private String userId;
    private float alphaVal = (float) 0.5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_listing_user);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(getString(R.string.add_listing));
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);
        userId = sharedPreference.getString("user_id","");
        getCreditListing(accessToken, "6");
        getTotalCredits(accessToken);
        binding.progress.setVisibility(View.VISIBLE);


        //IF USER HAS CREDIT LESS THAN 3 THEN MAKE THE ADD_LISTING_BTN NOT CLICKABLE(USER REQUIREMENT).
        if(totalCredits < 3){
            binding.btnAddListing.setAlpha(alphaVal);
            binding.btnAddListing.setEnabled(false);
        }else {
            binding.btnAddListing.setAlpha(1);
            binding.btnAddListing.setEnabled(true);
        }
    }

    @Override
    public void initControls() {
        super.initControls();

        binding.btnBuyCredit.setOnClickListener(this);
        binding.btnAddListing.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBuyCredit:
                startActivity(new Intent(this, UpgradeMemberShipActivity.class));
                finish();
                break;
            case R.id.btnAddListing:
                if (totalCredits < 3){
                    Toast.makeText(this, getString(R.string.not_enough_credit), Toast.LENGTH_LONG).show();
                }else {
                    startActivity(new Intent(AddListingUserActivity.this, AddListingPhotoActivity.class));
                }
                break;
            case R.id.btnCancel:
                onBackPressed();
                finish();
                break;
            case R.id.ivBack:
                onBackPressed();
                finish();
                break;

        }
    }

    private void getCreditListing(final String accessToken, final String key) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditListingApi> call = apiService.getCreditListing(accessToken, key);
        call.enqueue(new Callback<CreditListingApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditListingApi> call, Response<CreditListingApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    CreditListingApi registerApi = response.body();
                    CreditListingApi.CreditListingModel creditListingModel =
                            registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {
                        credits = creditListingModel.getCredit();
                        binding.tvAddListingCost.setText(getString(R.string.add_an) +" "+ credits + " "+getString(R.string.credits));
                    } else {
                        Toast.makeText(AddListingUserActivity.this,
                                "No response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddListingUserActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AddListingUserActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditListingApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(AddListingUserActivity.this, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTotalCredits(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getTotalCredit(accessToken);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    SignUpModel signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", signUpModel.getTotal_credit());
                        totalCredits = signUpModel.getTotal_credit();
                        binding.tvCurrentCredits.setText("Your Current Credit "+totalCredits);

                    } else {
                        binding.progress.setVisibility(View.GONE);
                        Toast.makeText(AddListingUserActivity.this,
                                "No response", Toast.LENGTH_LONG).show();
                    }
                } else {binding.progress.setVisibility(View.GONE);

                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddListingUserActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AddListingUserActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(AddListingUserActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void payCredit(final String accessToken, String credit, String user_id, final String key) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditStatusApi> call = apiService.payCredit(accessToken,credit,user_id,key);
        call.enqueue(new Callback<CreditStatusApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditStatusApi> call, Response<CreditStatusApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    CreditStatusApi registerApi = response.body();
                    StatusModel creditSatusModel = registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        getTotalCredits(accessToken);
                        startActivity(new Intent(AddListingUserActivity.this,
                                HomeUserActivity.class));

                    } else {

                        Toast.makeText(AddListingUserActivity.this,
                                "No response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddListingUserActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AddListingUserActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditStatusApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(AddListingUserActivity.this, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
