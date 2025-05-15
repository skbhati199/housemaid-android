package com.housemaid.activities.dataList;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.adapter.EducationAdapter;
import com.housemaid.databinding.ActivitySkillsListBinding;
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

public class SkillsListActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySkillsListBinding binding;
    private EducationAdapter skillsAdapter;
    private ArrayList<CountryListModel> listSkills;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_skills_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText(R.string.select_skills);
        listSkills = new ArrayList<>();
        itemNameList = getIntent().getStringArrayListExtra("skillsList");
        itemIdList = getIntent().getStringArrayListExtra("skillsIdList");
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
                senSkillsList();
                break;
        }
    }

    private void senSkillsList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getJobChoice() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getSkillsList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    ServerResponseCountryList registerApi = response.body();

                    listSkills = registerApi.countryListModels;
                    if (listSkills.size() > 0) {

                        ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("skillsIdList");
                        if (selectedIdList != null) {

                            for (int j = 0; j < selectedIdList.size(); j++) {
                                String selectedId = selectedIdList.get(j);

                                for (int k = 0; k < listSkills.size(); k++) {
                                    if ((listSkills.get(k).getId() + "").equals(selectedId)) {
                                        listSkills.get(k).checked = true;
                                    }
                                }
                            }
                        }
                        binding.rvSkills.setLayoutManager(new LinearLayoutManager(
                                SkillsListActivity.this));
                        skillsAdapter = new EducationAdapter(listSkills, new EducationAdapter
                                .OnItemCheckListener() {
                            @Override
                            public void onItemCheck(String name, String id) {
                                itemNameList.add(name);
                                itemIdList.add(id);
                                skillsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onItemUncheck(String name, String id) {
                                itemNameList.remove(name);
                                itemIdList.remove(id);
                                skillsAdapter.notifyDataSetChanged();
                            }
                        });
                        binding.rvSkills.setAdapter(skillsAdapter);
                    } else binding.tvNoData.setVisibility(View.VISIBLE);
                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (SkillsListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(SkillsListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(SkillsListActivity.this, ""
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
                Toast.makeText(SkillsListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
