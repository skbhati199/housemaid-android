package com.housemaid.activities.agency.fromHome;


import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.adapter.HomeListingAdapter;
import com.housemaid.databinding.ActivityMyMaidsBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.RegisterApiForMaidList;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyMaidsActivity extends BaseActivity implements View.OnClickListener, callMethod {

    private ActivityMyMaidsBinding binding;
    private ArrayList<UserDetailModel> maidListingList;
    private SharedPreference sharedPreference;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_maids);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();

        sharedPreference = SharedPreference.getInstance(this);
        String user_id = getIntent().getStringExtra("user_Id");
        String job_id = getIntent().getStringExtra("job_Id");
        sharedPreference.putString("user_ID", user_id);
        sharedPreference.putString("job_ID", job_id);

        int select_maid = getIntent().getIntExtra("select_maid", 0);
        sharedPreference.putInteger("page_selection", select_maid);
        String agency_id = sharedPreference.getString("agency_Id", "");
        accessToken = sharedPreference.getString("signUp_token", "");
        binding.toolbar.tvTitle.setText(R.string.my_maids);

        if (select_maid == 0) {
            binding.toolbar.ivAdd.setVisibility(View.VISIBLE);
            binding.tvSelectionText.setVisibility(View.GONE);
        }
        if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
            getMaidList(accessToken, agency_id);
            binding.progress.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivAdd.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivAdd) {

                startActivity(new Intent(this, AddListingActivity.class));
                
            
} else if (v.getId() == R.id.ivBack) {

                onBackPressed();
                finish();
                
        
}
    }

    private void getMaidList(String access_token, String agency_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForMaidList> call = apiService.maidListUnderAgency(access_token,
                agency_id);
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

                        Toast.makeText(MyMaidsActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MyMaidsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(MyMaidsActivity.this, ""
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
            public void onFailure(retrofit2.Call<RegisterApiForMaidList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(MyMaidsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void calls() {
        String accessFrom = "myMaids";
        HomeListingAdapter homeListingAdapter = new HomeListingAdapter(MyMaidsActivity.this,
                maidListingList, this, accessFrom);
        binding.rvMyMaidsListing.setLayoutManager(
                new LinearLayoutManager(MyMaidsActivity.this));
        binding.rvMyMaidsListing.setAdapter(homeListingAdapter);

    }

    private void makeFavourite(String access_token, String key, String jobListingId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.makeMaidFavourite(access_token, key,
                jobListingId);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(MyMaidsActivity.this, "Removed from favourites!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MyMaidsActivity.this, "Added to favourites!",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(MyMaidsActivity.this, "Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(MyMaidsActivity.this, ""
                                + response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(MyMaidsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void makeFavourite(int key, int jobListId) {
        makeFavourite(accessToken, String.valueOf(key), String.valueOf(jobListId));

    }

    @Override
    public void makeUnfavourite(int key, int jobListId, int position) {
    }

    @Override
    public void removeMaid(int position) {
    }
}