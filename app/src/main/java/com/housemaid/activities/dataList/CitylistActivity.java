package com.housemaid.activities.dataList;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.agency.fromHome.AddMaidActivity2;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
import com.housemaid.activities.maid.fromHome.activity.EditMaidPofileActivity;
import com.housemaid.adapter.EducationAdapter;
import com.housemaid.databinding.ActivityCitylistBinding;
import com.housemaid.model.CountryListModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.ServerResponseCountryList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitylistActivity extends BaseActivity implements View.OnClickListener {

ActivityCitylistBinding binding;

    private EducationAdapter cityAdapter;
    private ArrayList<CountryListModel> listCity;
    private SharedPreference sharedPreference;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemIdList;
    private ArrayList<String> district_id;

    int stateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_citylist);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        stateId = sharedPreference.getInteger("state_ID", 0);

        listCity = new ArrayList<>();
        district_id = getIntent().getStringArrayListExtra("districtIDList");
        itemNameList = getIntent().getStringArrayListExtra("cityList");
        itemIdList = getIntent().getStringArrayListExtra("cityIdList");

        if(district_id.isEmpty() || district_id == null){
            Toast.makeText(this, "Please select district first", Toast.LENGTH_SHORT).show();
            finish();
        }
        binding.progress.setVisibility(View.VISIBLE);
        getAllCityList();

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSubmit:
                sendCityList();
                break;
        }
    }
    private void sendCityList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
    private void getCityList(int state_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getCityList(state_id);

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    ServerResponseCountryList registerApi = response.body();

                    listCity = registerApi.countryListModels;
                    if (listCity.size()>0) {


                        ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("cityIdList");
                        if (selectedIdList != null) {

                            for (int j = 0; j < selectedIdList.size(); j++) {
                                String selectedId = selectedIdList.get(j);

                                for (int k = 0; k < listCity.size(); k++) {
                                    if ((listCity.get(k).getId() + "").equals(selectedId)) {
                                        listCity.get(k).checked = true;
                                    }
                                }
                            }
                        }
                        binding.rvCity.setLayoutManager(new LinearLayoutManager
                                (CitylistActivity.this));
                        cityAdapter = new EducationAdapter(listCity, new EducationAdapter.OnItemCheckListener() {
                            @Override
                            public void onItemCheck(String name, String id) {
                                itemNameList.add(name);
                                itemIdList.add(id);
                                cityAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemUncheck(String name, String id) {
                                itemNameList.remove(name);
                                itemIdList.remove(id);
                                cityAdapter.notifyDataSetChanged();
                            }
                        });
                        binding.rvCity.setHasFixedSize(true);
                        binding.rvCity.setAdapter(cityAdapter);

                    }else binding.tvNoData.setVisibility(View.VISIBLE);
                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {  if (response.code() == 401) {
                        sharedPreference.deletePreference();
                        Intent signInIntent = new Intent(CitylistActivity.this,
                                SelectionActivity.class);
                        startActivity(signInIntent);

                    } else {

                        Toast.makeText(CitylistActivity.this, ""
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
                Toast.makeText(CitylistActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllCityList() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.get_multicity_list(district_id);

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    ServerResponseCountryList registerApi = response.body();

                    listCity = registerApi.countryListModels;
                    if (listCity.size()>0) {


                        ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("cityIdList");
                        if (selectedIdList != null) {

                            for (int j = 0; j < selectedIdList.size(); j++) {
                                String selectedId = selectedIdList.get(j);

                                for (int k = 0; k < listCity.size(); k++) {
                                    if ((listCity.get(k).getId() + "").equals(selectedId)) {
                                        listCity.get(k).checked = true;
                                    }
                                }
                            }
                        }
                        binding.rvCity.setLayoutManager(new LinearLayoutManager
                                (CitylistActivity.this));
                        cityAdapter = new EducationAdapter(listCity, new EducationAdapter.OnItemCheckListener() {
                            @Override
                            public void onItemCheck(String name, String id) {
                                itemNameList.add(name);
                                itemIdList.add(id);
                                cityAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemUncheck(String name, String id) {
                                itemNameList.remove(name);
                                itemIdList.remove(id);
                                cityAdapter.notifyDataSetChanged();
                            }
                        });
                        binding.rvCity.setHasFixedSize(true);
                        binding.rvCity.setAdapter(cityAdapter);

                    }else binding.tvNoData.setVisibility(View.VISIBLE);
                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {  if (response.code() == 401) {
                        sharedPreference.deletePreference();
                        Intent signInIntent = new Intent(CitylistActivity.this,
                                SelectionActivity.class);
                        startActivity(signInIntent);

                    } else {

                        Toast.makeText(CitylistActivity.this, ""
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
                Toast.makeText(CitylistActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
