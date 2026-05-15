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
import com.housemaid.databinding.ActivityEducationListBinding;
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

public class EducationListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityEducationListBinding binding;
    private EducationAdapter educationAdapter;
    private ArrayList<CountryListModel> listEducation;
    private ArrayList<String> itemNameList = new ArrayList<>();
    private ArrayList<String> itemIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_education_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText(R.string.select_education);
        listEducation = new ArrayList<>();
        itemNameList = getIntent().getStringArrayListExtra("educationNameList");
        itemIdList = getIntent().getStringArrayListExtra("educationIdList");
        binding.progress.setVisibility(View.VISIBLE);
        getEducation();
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
                sendEducationList();
                break;
        }
    }

    private void sendEducationList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }

    private void getEducation() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getEducationList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    listEducation = registerApi.countryListModels;

                    ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("educationIdList");
                    if (selectedIdList != null) {

                        for (int j = 0; j < selectedIdList.size(); j++) {
                            String selectedId = selectedIdList.get(j);

                            for (int k = 0; k < listEducation.size(); k++) {
                                if ((listEducation.get(k).getId() + "").equals(selectedId)) {
                                    listEducation.get(k).checked = true;
                                }
                            }
                        }
                    }
                    binding.rvEducation.setLayoutManager(new LinearLayoutManager(
                            EducationListActivity.this));
                    educationAdapter = new EducationAdapter(listEducation, new EducationAdapter
                            .OnItemCheckListener() {
                        @Override
                        public void onItemCheck(String name, String id) {
                            itemNameList.add(name);
                            itemIdList.add(id);
                            educationAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onItemUncheck(String name, String id) {
                            itemNameList.remove(name);
                            itemIdList.remove(id);
                            educationAdapter.notifyDataSetChanged();
                        }

                    });

                    binding.rvEducation.setAdapter(educationAdapter);
                    binding.progress.setVisibility(View.GONE);

                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (EducationListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(EducationListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(EducationListActivity.this, ""
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
                Toast.makeText(EducationListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}