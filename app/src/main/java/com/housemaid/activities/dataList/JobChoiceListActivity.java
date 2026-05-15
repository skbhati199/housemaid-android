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
import com.housemaid.databinding.ActivityJobChoiceListBinding;
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

public class JobChoiceListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityJobChoiceListBinding binding;
    private EducationAdapter JobChoiceAdapter;
    private ArrayList<CountryListModel> listJobChoice;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_choice_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText(R.string.select_job);
        listJobChoice = new ArrayList<>();
        if (getIntent().getStringArrayListExtra("joChoiceList") != null) {
            itemNameList = getIntent().getStringArrayListExtra("joChoiceList");
            itemIdList = getIntent().getStringArrayListExtra("joChoiceIdList");
        } else {
            itemNameList = new ArrayList<>();
            itemIdList = new ArrayList<>();
        }

        binding.progress.setVisibility(View.VISIBLE);
        binding.btnSubmit.setClickable(false);

        getJobChoice();
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
                sendJobList();
                break;
        }

    }

    private void sendJobList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getJobChoice() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getJobChoiceList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnSubmit.setClickable(true);
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    ServerResponseCountryList registerApi = response.body();

                    listJobChoice = registerApi.countryListModels;
                    if (listJobChoice.size() > 0) {

                        ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("joChoiceIdList");
                        if (selectedIdList != null) {

                            for (int j = 0; j < selectedIdList.size(); j++) {
                                String selectedId = selectedIdList.get(j);

                                for (int k = 0; k < listJobChoice.size(); k++) {
                                    if ((listJobChoice.get(k).getId() + "").equals(selectedId)) {
                                        listJobChoice.get(k).checked = true;
                                    }
                                }
                            }
                        }
                        binding.rvJobChoice.setLayoutManager(new LinearLayoutManager(
                                JobChoiceListActivity.this));
                        JobChoiceAdapter = new EducationAdapter(listJobChoice, new EducationAdapter
                                .OnItemCheckListener() {
                            @Override
                            public void onItemCheck(String name, String id) {
                                itemNameList.add(name);
                                itemIdList.add(id);
                                JobChoiceAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemUncheck(String name, String id) {
                                itemNameList.remove(name);
                                itemIdList.remove(id);
                                JobChoiceAdapter.notifyDataSetChanged();

                            }
                        });
                        binding.rvJobChoice.setAdapter(JobChoiceAdapter);
                    } else binding.tvNoData.setVisibility(View.VISIBLE);
                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (JobChoiceListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(JobChoiceListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(JobChoiceListActivity.this, ""
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
                Toast.makeText(JobChoiceListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
