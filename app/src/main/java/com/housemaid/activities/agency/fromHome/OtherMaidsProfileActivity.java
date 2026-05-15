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
import com.housemaid.databinding.ActivityOtherMaidsProfileBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.RegisterApiForMaidList;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherMaidsProfileActivity extends BaseActivity implements View.OnClickListener, callMethod {

    private ActivityOtherMaidsProfileBinding binding;
    private SharedPreference sharedPreference;
    private String accessToken;
    private ArrayList<UserDetailModel> maidListingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_other_maids_profile);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.maid_profiles);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        getMaidList(accessToken);
        binding.progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void initControls() {

        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                
        
}
    }

    private void getMaidList(String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForMaidList> call = apiService.getMaidList(access_token);
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
                        if (maidListingList != null && !maidListingList.isEmpty()) calls();
                        else binding.tvNoData.setVisibility(View.VISIBLE);
                    } else {

                        Toast.makeText(OtherMaidsProfileActivity.this, "Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(OtherMaidsProfileActivity
                                    .this, SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(OtherMaidsProfileActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(OtherMaidsProfileActivity.this, "error " + t.getMessage()
                        , Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void calls() {
        ArrayList<UserDetailModel> filterList = new ArrayList<>();

        for (int i = 0; i < maidListingList.size(); i++) {
            if (!maidListingList.get(i).getIs_hired()) {
                filterList.add(maidListingList.get(i));
            }
        }
        String accessFrom = "";
        HomeListingAdapter homeListingAdapter = new HomeListingAdapter(this,
                filterList, this, accessFrom);
        binding.rvMaidListing.setLayoutManager(
                new LinearLayoutManager(this));
        binding.rvMaidListing.setAdapter(homeListingAdapter);

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
                            Toast.makeText(OtherMaidsProfileActivity.this,
                                    R.string.removed_from_favorites,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(OtherMaidsProfileActivity.this,
                                    R.string.added_to_favorites,
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(OtherMaidsProfileActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(OtherMaidsProfileActivity.this, ""
                                + response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(OtherMaidsProfileActivity.this, "error "
                        + t.getMessage(), Toast.LENGTH_SHORT).show();

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