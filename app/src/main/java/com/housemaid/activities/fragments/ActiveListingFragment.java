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
import com.housemaid.activities.SelectionActivity;
import com.housemaid.adapter.MyActiveListingAdapter;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.FragmentActiveListingBinding;
import com.housemaid.model.SignUpModel;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveListingFragment extends Fragment {

    FragmentActiveListingBinding binding;
    MyActiveListingAdapter activeListingAdapter;
    private Activity activity;
    SharedPreference sharedPreference;
    String accessToken;
    ArrayList<SignUpModel> jobListing;
    public ActiveListingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_active_listing,
                container, false);
        View view = binding.getRoot();
        activity = getActivity();
        sharedPreference = SharedPreference.getInstance(activity);
        accessToken = sharedPreference.getString("signUp_token", "0");
        if (ValidationUtils.isOnline(binding.relativeLayout, activity)) {
            binding.progress.setVisibility(View.VISIBLE);
            getFavouriteJobListing(accessToken);
        }
        return view;
    }

    private void getFavouriteJobListing(String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.getActivePassiveList(Constants.TIMEZONE,
                Constants.LOCALE,access_token, "1");
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    jobListing = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (jobListing.size()>0) {

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
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(activity, "error " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void calls() {
        activeListingAdapter = new MyActiveListingAdapter(activity, jobListing, binding);
        binding.rvActiveListing.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvActiveListing.setAdapter(activeListingAdapter);
    }



}