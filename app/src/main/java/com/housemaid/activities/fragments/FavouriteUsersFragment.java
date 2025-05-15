package com.housemaid.activities.fragments;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.MyFavoritesListingActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.agency.fromHome.AddMaidActivity2;
import com.housemaid.adapter.MyFavoritesAdapter;
import com.housemaid.databinding.FragmentFavouriteMaidsBinding;
import com.housemaid.databinding.FragmentFavouriteUsersBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.FavouriteListModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApiForList;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteUsersFragment extends Fragment implements callMethod {

    FragmentFavouriteUsersBinding binding;
    private Activity activity;
    MyFavoritesAdapter myFavoritesAdapter;
    ArrayList<FavouriteListModel> favouriteListing;
    SharedPreference sharedPreference = SharedPreference.getInstance(getActivity()) ;
    String accessToken;

    public FavouriteUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_favourite_users,
                container, false);
        View view = binding.getRoot();
        activity=getActivity();
        accessToken = sharedPreference.getString("signUp_token", "0");
        if (ValidationUtils.isOnline(binding.relativeLayout, activity)) {
            getFavouriteJobListing(accessToken, 1);
        }
        return view;
    }
    private void getFavouriteJobListing(String access_token, final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForList> call = apiService.getFavouriteJobListing(access_token);
        call.enqueue(new Callback<RegisterApiForList>() {

            @Override
            public void onResponse(Call<RegisterApiForList> call, Response<RegisterApiForList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiForList registerApi = response.body();
                    favouriteListing = registerApi.getFavouriteListModelArrayList();
                    String message = registerApi.message;
                    if (message != null) {

                        if (favouriteListing.size()>0) {

                            calls();
                        }else binding.tvNoData.setVisibility(View.VISIBLE);

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
            public void onFailure(retrofit2.Call<RegisterApiForList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(activity, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void calls() {
        myFavoritesAdapter = new MyFavoritesAdapter(activity, favouriteListing, this);
        binding.rvFavoritesJobListing.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvFavoritesJobListing.setAdapter(myFavoritesAdapter);
    }


    private void makeFavourite(String access_token, String key, String jobListingId, final int position) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call;
        call = apiService.makeFavourite(access_token, key, jobListingId);

        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {


                            favouriteListing.remove(position);
                            myFavoritesAdapter.notifyDataSetChanged();
                            if (favouriteListing.size() == 0) {
                                binding.tvNoData.setVisibility(View.VISIBLE);
                        }

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(activity, R.string.removed_from_favorites,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity,  R.string.added_to_favorites,
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {

                        Toast.makeText(activity, response.errorBody().toString(),
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
        makeFavourite(accessToken, String.valueOf(key), String.valueOf(jobListId),position);

    }

    @Override
    public void removeMaid(int position) {

    }
}
