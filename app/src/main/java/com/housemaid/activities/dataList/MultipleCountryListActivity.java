package com.housemaid.activities.dataList;

import android.app.Activity;
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
import com.housemaid.adapter.EducationAdapter;
import com.housemaid.databinding.ActivityMultipleCountryListBinding;
import com.housemaid.model.CountryListModel;
import com.housemaid.model.response.ServerResponseCountryList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MultipleCountryListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMultipleCountryListBinding binding;
    private EducationAdapter countryAdapter;
    private ArrayList<CountryListModel> listCountry;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemIdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_multiple_country_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText(R.string.select_country);
        listCountry = new ArrayList<>();
        itemNameList = new ArrayList<>();
        itemIdList = new ArrayList<>();

        SharedPreference sharedPreference = SharedPreference.getInstance(this);
        String accessToken = sharedPreference.getString("signUp_token", "0");

        binding.progress.setVisibility(View.VISIBLE);
        binding.btnSubmit.setClickable(false);
        getCountryList(accessToken);
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {

                sendCountryList();
                
        
}

    }

    private void sendCountryList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getCountryList(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getCountryList(accessToken);

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    listCountry = registerApi.countryListModels;
                    binding.rvCountry.setLayoutManager(new LinearLayoutManager(
                            MultipleCountryListActivity.this));
                    countryAdapter = new EducationAdapter(listCountry, new EducationAdapter
                            .OnItemCheckListener() {
                        @Override
                        public void onItemCheck(String name, String id) {
                            itemNameList.add(name);
                            itemIdList.add(id);
                        }

                        @Override
                        public void onItemUncheck(String name, String id) {

                            itemNameList.remove(name);
                            itemIdList.remove(id);
                        }
                    });
                    binding.rvCountry.setHasFixedSize(true);
                    binding.rvCountry.setAdapter(countryAdapter);
                    binding.progress.setVisibility(View.GONE);
                    countryAdapter.notifyDataSetChanged();
                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (MultipleCountryListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(MultipleCountryListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(MultipleCountryListActivity.this, ""
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
            public void onFailure(retrofit2.Call<ServerResponseCountryList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(MultipleCountryListActivity.this, "error" + t.getMessage()
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }
}