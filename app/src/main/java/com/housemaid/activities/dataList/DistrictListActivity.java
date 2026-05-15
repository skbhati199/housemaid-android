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
import com.housemaid.databinding.ActivityDistrictListBinding;
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

public class DistrictListActivity extends BaseActivity implements View.OnClickListener {


    private ActivityDistrictListBinding binding;
    private EducationAdapter districtAdapter;
    private ArrayList<CountryListModel> listDistrict;
    private SharedPreference sharedPreference;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_district_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        int stateId = sharedPreference.getInteger("state_ID", 0);

        listDistrict = new ArrayList<>();
        itemNameList = getIntent().getStringArrayListExtra("districtList");
        itemIdList = getIntent().getStringArrayListExtra("districtIDList");

        binding.progress.setVisibility(View.VISIBLE);
        getDistrictList(stateId);

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
                sendCountryList();
                break;
        }

    }

    private void sendCountryList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getDistrictList(int state_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getDistrictList(state_id);

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);

                    ServerResponseCountryList registerApi = response.body();

                    listDistrict = registerApi.countryListModels;

                    if (listDistrict.size() > 0) {

                        ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("districtIDList");
                        if (selectedIdList != null) {

                            for (int j = 0; j < selectedIdList.size(); j++) {
                                String selectedId = selectedIdList.get(j);

                                for (int k = 0; k < listDistrict.size(); k++) {
                                    if ((listDistrict.get(k).getId() + "").equals(selectedId)) {
                                        listDistrict.get(k).checked = true;
                                    }
                                }
                            }
                        }
                        binding.rvDistrict.setLayoutManager(new LinearLayoutManager(
                                DistrictListActivity.this));
                        districtAdapter = new EducationAdapter(listDistrict, new EducationAdapter
                                .OnItemCheckListener() {
                            @Override
                            public void onItemCheck(String name, String id) {
                                itemNameList.add(name);
                                itemIdList.add(id);
                                districtAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemUncheck(String name, String id) {
                                itemNameList.remove(name);
                                itemIdList.remove(id);
                                districtAdapter.notifyDataSetChanged();
                            }
                        });
                        binding.rvDistrict.setHasFixedSize(true);
                        binding.rvDistrict.setAdapter(districtAdapter);
                    } else binding.tvNoData.setVisibility(View.VISIBLE);
                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(DistrictListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(DistrictListActivity.this, ""
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
                Toast.makeText(DistrictListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
