package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.maid.fromHome.activity.HomeForMaidActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.adapter.MyFavoritesAdapter;
import com.housemaid.adapter.MyFavoritesMaidAdapter;
import com.housemaid.databinding.ActivityMyFavoritesListingBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.FavouriteListModel;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApiForList;
import com.housemaid.model.response.RegisterApiForMaidList;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavoritesListingActivity extends BaseActivity implements View.OnClickListener,
        callMethod, SwipeRefreshLayout.OnRefreshListener {

    ActivityMyFavoritesListingBinding binding;
    MyFavoritesAdapter myFavoritesAdapter;
    MyFavoritesMaidAdapter myFavouritesMaidAdapter;
    private SharedPreference sharedPreference;
    ArrayList<FavouriteListModel> favouriteListing;
    ArrayList<UserDetailModel> favouriteMaidListing;
    String accessToken;
    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,
                R.layout.activity_my_favorites_listing);
        sharedPreference = SharedPreference.getInstance(this);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.favourites);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
        binding.progress.setVisibility(View.VISIBLE);

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
                getFavouriteJobListing(accessToken, a);
            }

        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
                getFavouriteMaidListing(accessToken, a);

            }

        }
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
                if (sharedPreference.getInteger("entry_key", 0) == 1) {
                    startActivity(new Intent(this, HomeForMaidActivity.class));
                }
                if (sharedPreference.getInteger("entry_key", 0) == 2) {
                    startActivity(new Intent(this, HomeUserActivity.class));
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            if (myFavoritesAdapter != null) {
                binding.swipeRefreshLayout.setRefreshing(true);
                getFavouriteJobListing(accessToken, a);
            }
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            if (myFavouritesMaidAdapter != null) {
                binding.swipeRefreshLayout.setRefreshing(true);
                getFavouriteMaidListing(accessToken, a);
            }
        }
    }

    private void getFavouriteJobListing(String access_token, final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForList> call = apiService.getFavouriteJobListing(access_token);
        call.enqueue(new Callback<RegisterApiForList>() {

            @Override
            public void onResponse(Call<RegisterApiForList> call,
                                   Response<RegisterApiForList> response) {
                binding.swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiForList registerApi = response.body();
                    favouriteListing = registerApi.getFavouriteListModelArrayList();
                    String message = registerApi.message;
                    if (message != null) {

                        if (favouriteListing.size() > 0) {

                            calls();
                        } else binding.tvNoData.setVisibility(View.VISIBLE);

                    } else {

                        Toast.makeText(MyFavoritesListingActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MyFavoritesListingActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(MyFavoritesListingActivity.this, new Gson().fromJson
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
            public void onFailure(retrofit2.Call<RegisterApiForList> call, Throwable t) {
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(MyFavoritesListingActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calls() {
        myFavoritesAdapter = new MyFavoritesAdapter(MyFavoritesListingActivity.this,
                favouriteListing, this);
        binding.rvFavoritesListing.setLayoutManager(new LinearLayoutManager(
                MyFavoritesListingActivity.this));
        binding.rvFavoritesListing.setAdapter(myFavoritesAdapter);
    }

    private void getFavouriteMaidListing(String access_token, int position) {
        try {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            retrofit2.Call<RegisterApiForMaidList> call = apiService.getFavouriteMaidListing(access_token);
            call.enqueue(new Callback<RegisterApiForMaidList>() {

                @Override
                public void onResponse(Call<RegisterApiForMaidList> call,
                                       Response<RegisterApiForMaidList> response) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    if (response.isSuccessful()) {
                        binding.progress.setVisibility(View.GONE);
                        RegisterApiForMaidList registerApi = response.body();
                        favouriteMaidListing = registerApi.getMaidDetailModel();
                        String message = registerApi.message;
                        if (message != null) {

                            if (favouriteMaidListing.size() > 0) {
                                maidCalls();
                            } else binding.tvNoData.setVisibility(View.VISIBLE);

                        } else {

                            Toast.makeText(MyFavoritesListingActivity.this,
                                    response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        binding.progress.setVisibility(View.GONE);
                        try {
                            if (response.code() == 401) {
                                sharedPreference.deletePreference();
                                Intent signInIntent = new Intent(
                                        MyFavoritesListingActivity.this,
                                        SelectionActivity.class);
                                startActivity(signInIntent);
                                finishAffinity();
                            } else {
                                Toast.makeText(MyFavoritesListingActivity.this, new Gson().fromJson
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
                public void onFailure(retrofit2.Call<RegisterApiForMaidList> call, Throwable t) {
                    binding.progress.setVisibility(View.GONE);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    if (t instanceof ConnectException) {

                        Toast.makeText(MyFavoritesListingActivity.this, "Network Error",
                                Toast.LENGTH_SHORT).show();

                    } else if (t instanceof SocketTimeoutException) {

                        Toast.makeText(MyFavoritesListingActivity.this, "Connection Lost",
                                Toast.LENGTH_SHORT).show();
                    } else if (t instanceof UnknownHostException) {

                        Toast.makeText(MyFavoritesListingActivity.this, "Server Error",
                                Toast.LENGTH_SHORT).show();
                    } else if (t instanceof InternalError) {

                        Toast.makeText(MyFavoritesListingActivity.this, "Server Error",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void maidCalls() {
        myFavouritesMaidAdapter = new MyFavoritesMaidAdapter(MyFavoritesListingActivity.this
                , favouriteMaidListing, this, "");
        binding.rvFavoritesListing.setLayoutManager(new LinearLayoutManager
                (MyFavoritesListingActivity.this));
        binding.rvFavoritesListing.setAdapter(myFavouritesMaidAdapter);

    }

    private void makeFavourite(String access_token, String key, String jobListingId,
                               final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call;
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            call = apiService.makeMaidFavourite(access_token, key, jobListingId);
        } else call = apiService.makeFavourite(access_token, key, jobListingId);


        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call,
                                   Response<RegisterApiList> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("entry_key", 0) == 2) {
                            favouriteMaidListing.remove(position);
                            myFavouritesMaidAdapter.notifyDataSetChanged();
                            if (favouriteMaidListing.size() == 0) {
                                binding.tvNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            favouriteListing.remove(position);
                            myFavoritesAdapter.notifyDataSetChanged();
                            if (favouriteListing.size() == 0) {
                                binding.tvNoData.setVisibility(View.VISIBLE);
                            }
                        }

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(MyFavoritesListingActivity.this,
                                    R.string.removed_from_favorites,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MyFavoritesListingActivity.this,
                                    R.string.added_to_favorites,
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(MyFavoritesListingActivity.this,
                                response.errorBody().toString(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MyFavoritesListingActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(MyFavoritesListingActivity.this, new Gson().fromJson
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
            public void onFailure(Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(MyFavoritesListingActivity.this, "error "
                        + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void makeFavourite(int key, int jobListId) {
    }

    @Override
    public void makeUnfavourite(int key, int jobListId, int position) {
        makeFavourite(accessToken, String.valueOf(key), String.valueOf(jobListId), position);
    }

    @Override
    public void removeMaid(int position) {
    }

    @Override
    public void onRefresh() {

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            getFavouriteJobListing(accessToken, a);

        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            getFavouriteMaidListing(accessToken, a);

        }


    }
}