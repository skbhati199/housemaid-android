package com.housemaid.activities.fragments;


import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.MyFavoritesMaidAdapter;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.databinding.FragmentFavouriteMaidsBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.RegisterApiForMaidList;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteMaidsFragment extends Fragment implements callMethod {

    FragmentFavouriteMaidsBinding binding;
    private Activity activity;
    SharedPreference sharedPreference = SharedPreference.getInstance(getActivity());
    ArrayList<UserDetailModel> favouriteMaidListing;
    MyFavoritesMaidAdapter myFavouritesMaidAdapter;

    String accessToken = sharedPreference.getString("signUp_token", "0");

    public FavouriteMaidsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_maids,
                container, false);
        View view = binding.getRoot();
        activity = getActivity();
        if (ValidationUtils.isOnline(binding.relativeLayout, activity)) {
            binding.progress.setVisibility(View.VISIBLE);
            getFavouriteMaidListing(accessToken);
        }
        return view;
    }

    private void getFavouriteMaidListing(String access_token) {
        try {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            retrofit2.Call<RegisterApiForMaidList> call = apiService.getFavouriteMaidListing(access_token);
            call.enqueue(new Callback<RegisterApiForMaidList>() {

                @Override
                public void onResponse(Call<RegisterApiForMaidList> call, Response<RegisterApiForMaidList> response) {
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

                            Toast.makeText(activity, response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        binding.progress.setVisibility(View.GONE);
                        try {
                            if (response.code() == 401) {
                                SharedPreference sharedPreference = SharedPreference.getInstance
                                        (getContext());
                                sharedPreference.deletePreference();
                                Intent signInIntent = new Intent(getContext(), SelectionActivity.class);
                                startActivity(signInIntent);

                            } else {

                                Toast.makeText(getContext(), ""
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
                    Toast.makeText(activity, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    if (t instanceof ConnectException) {

                        Toast.makeText(activity, R.string.network_error, Toast.LENGTH_SHORT).show();

                    } else if (t instanceof SocketTimeoutException) {

                        Toast.makeText(activity, R.string.connection_lost, Toast.LENGTH_SHORT).show();
                    } else if (t instanceof UnknownHostException) {

                        Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
                    } else if (t instanceof InternalError) {

                        Toast.makeText(activity, R.string.server_error, Toast.LENGTH_SHORT).show();
                    }


                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void maidCalls() {
        myFavouritesMaidAdapter = new MyFavoritesMaidAdapter(activity, favouriteMaidListing,
                this,"");
        binding.rvFavoritesMaidListing.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvFavoritesMaidListing.setAdapter(myFavouritesMaidAdapter);

    }

    private void makeFavourite(String access_token, String key, String jobListingId, final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call;
        call = apiService.makeMaidFavourite(access_token, key, jobListingId);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        favouriteMaidListing.remove(position);
                        myFavouritesMaidAdapter.notifyDataSetChanged();
                        if (favouriteMaidListing.size() == 0) {
                            binding.tvNoData.setVisibility(View.VISIBLE);
                        }

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(activity, "Removed from favourites!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, "Added to favourites!",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(activity, "Registration Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(activity, "" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                        Log.d("TEST", "Error : " + response.errorBody().string() + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(activity, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();

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
}
