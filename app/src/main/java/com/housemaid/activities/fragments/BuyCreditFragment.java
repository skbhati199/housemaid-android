package com.housemaid.activities.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.agency.fromHome.AddMaidActivity2;
import com.housemaid.adapter.UpgradeMembershipAdapter;
import com.housemaid.databinding.FragmentBuyCreditBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
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
public class BuyCreditFragment extends Fragment {

    FragmentBuyCreditBinding binding;
    UpgradeMembershipAdapter upgradeMembershipAdapter;
    ArrayList<SignUpModel> offersList;
    SharedPreference sharedPreference;
    String accessToken;
    int userType;

    public BuyCreditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_credit, container,
                false);
        View view = binding.getRoot();
        sharedPreference = SharedPreference.getInstance(getContext());
        String accessToken = sharedPreference.getString("signUp_token", "");
        binding.progress.setVisibility(View.VISIBLE);
        userType = sharedPreference.getInteger("entry_key",0);
        if (ValidationUtils.isOnline(binding.frameLayout, getContext())) {
            if (userType == 1) {
                getPaymentGateway(accessToken, 2);
            } else if (userType == 2) {
                getPaymentGateway(accessToken, 1);
            } else getPaymentGateway(accessToken, 3);
        }
        return view;
    }

    private void getPaymentGateway(String access_token, int userType) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.getOffersList(access_token, userType);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    offersList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {

                        upgradeMembershipAdapter = new UpgradeMembershipAdapter(getContext(),offersList);
                        binding.rvUpgradeMembership.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        binding.rvUpgradeMembership.setAdapter(upgradeMembershipAdapter);

                    } else {

                        Toast.makeText(getContext(), "Registration Fails"
                                , Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}