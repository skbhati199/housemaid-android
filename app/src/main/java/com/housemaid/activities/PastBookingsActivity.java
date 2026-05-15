package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.PastBookingsAdapter;
import com.housemaid.databinding.ActivityPastBookingsBinding;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.RegisterApiForMaidList;
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

public class PastBookingsActivity extends BaseActivity implements View.OnClickListener {

    ActivityPastBookingsBinding binding;
    PastBookingsAdapter pastBookingsAdapter;
    ArrayList<UserDetailModel> userDetailModel;
    SharedPreference sharedPreference;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_past_bookings);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        binding.toolbar.tvTitle.setText(R.string.past_bookings);
        binding.progress.setVisibility(View.VISIBLE);
        if (ValidationUtils.isOnline(getWindow().getDecorView().getRootView(), this)) {
            getPastMaidList(accessToken, 1);
        }
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                
        
}
    }

    private void getPastMaidList(String access_token, int position) {
        try {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            retrofit2.Call<RegisterApiForMaidList> call;

            if (sharedPreference.getInteger("entry_key", 0) == 3) {
                call = apiService.getAgencyPastBooking(access_token);
            } else call = apiService.getPastMaidList(access_token);

            call.enqueue(new Callback<RegisterApiForMaidList>() {

                @Override
                public void onResponse(Call<RegisterApiForMaidList> call,
                                       Response<RegisterApiForMaidList> response) {
                    if (response.isSuccessful()) {
                        binding.progress.setVisibility(View.GONE);
                        RegisterApiForMaidList registerApi = response.body();
                        userDetailModel = registerApi.getMaidDetailModel();
                        String message = registerApi.message;
                        if (message != null) {

                            if (userDetailModel.size() > 0) {
                                maidCalls();
                            } else binding.tvNoData.setVisibility(View.VISIBLE);

                        } else {
                            Toast.makeText(PastBookingsActivity.this,
                                    response.errorBody().toString(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        binding.progress.setVisibility(View.GONE);
                        try {

                            if (response.code() == 401) {
                                sharedPreference.deletePreference();
                                Intent signInIntent = new Intent(PastBookingsActivity.this,
                                        SelectionActivity.class);
                                startActivity(signInIntent);
                                finishAffinity();
                            } else {

                                Toast.makeText(PastBookingsActivity.this,
                                        "" + response.errorBody(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PastBookingsActivity.this, "error " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();

                    if (t instanceof ConnectException) {

                        Toast.makeText(PastBookingsActivity.this, "Network Error",
                                Toast.LENGTH_SHORT).show();

                    } else if (t instanceof SocketTimeoutException) {

                        Toast.makeText(PastBookingsActivity.this, "Connection Lost",
                                Toast.LENGTH_SHORT).show();
                    } else if (t instanceof UnknownHostException) {

                        Toast.makeText(PastBookingsActivity.this, "Server Error",
                                Toast.LENGTH_SHORT).show();
                    } else if (t instanceof InternalError) {

                        Toast.makeText(PastBookingsActivity.this, "Server Error",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void maidCalls() {
        pastBookingsAdapter = new PastBookingsAdapter(this, userDetailModel, binding);
        binding.rvPastBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPastBookings.setAdapter(pastBookingsAdapter);
    }
}