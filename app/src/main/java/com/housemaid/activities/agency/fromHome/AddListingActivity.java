package com.housemaid.activities.agency.fromHome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.databinding.ActivityAddListingBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.CreditListingApi;
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

public class AddListingActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddListingBinding binding;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        binding = DataBindingUtil.setContentView ( this, R.layout.activity_add_listing );
        init ();
        initControls ();
    }

    @Override
    public void init() {
        super.init ();
        binding.toolbar.tvTitle.setText ( getString(R.string.add_maids) );
        sharedPreference = SharedPreference.getInstance(this);
        String accessToken = sharedPreference.getString("signUp_token", "");
        getTotalCredits(accessToken);
        getCreditListing(accessToken);
    }

    @Override
    public void initControls() {
        super.initControls ();
        binding.toolbar.ivBack.setOnClickListener ( this );
        binding.btnAddMaid.setOnClickListener ( this );
        binding.btnBuyCredit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {
            case R.id.btnAddMaid:
                startActivity ( new Intent ( getApplicationContext (), AddMaidFirstActivity.class ) );
                finish ();
                break;
                case R.id.btnBuyCredit:
                startActivity ( new Intent ( getApplicationContext (), UpgradeMemberShipActivity.class ) );
                finish ();
                break;
            case R.id.ivBack:
                onBackPressed ();
                finish ();
                break;
        }

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
                    SignUpModel totalCredits = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", totalCredits.getTotal_credit());
                        binding.tvCurrentCredits.setText(getString(R.string.your_current_credits)+" "+totalCredits.getTotal_credit());
                    } else {

                        Toast.makeText(AddListingActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddListingActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AddListingActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", getString(R.string.error)  + response.errorBody().string()
                                    + getString(R.string.message) + response.message());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                Toast.makeText(AddListingActivity.this, getString(R.string.error)   + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getCreditListing(final String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditListingApi> call = apiService.getCreditListing(accessToken, "10");
        call.enqueue(new Callback<CreditListingApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditListingApi> call, Response<CreditListingApi> response) {

                if (response.isSuccessful()) {

                    CreditListingApi registerApi = response.body();
                    CreditListingApi.CreditListingModel creditListingModel =
                            registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        String credits = creditListingModel.getCredit();
                        binding.tvCreditPrice.setText(getString(R.string.add_an_maid_is)+" "+
                                credits+" "+getString(R.string.credit));

                    } else {

                        Toast.makeText(AddListingActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddListingActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AddListingActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", getString(R.string.error) +" " + response.errorBody().string()
                                    + getString(R.string.message) +" " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CreditListingApi> call, Throwable t) {
                Toast.makeText(AddListingActivity.this, getString(R.string.error)  +" "+ t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
