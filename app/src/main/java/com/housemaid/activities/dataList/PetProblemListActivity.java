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
import com.housemaid.databinding.ActivityPetProblemListBinding;
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

public class PetProblemListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPetProblemListBinding binding;
    private EducationAdapter petProblemAdapter;
    private ArrayList<CountryListModel> listPetProblem;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_problem_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText(R.string.select_pet_problem);
        listPetProblem = new ArrayList<>();
        itemNameList = getIntent().getStringArrayListExtra("petProblemList");
        itemIdList = getIntent().getStringArrayListExtra("petProblemIdList");
        binding.progress.setVisibility(View.VISIBLE);
        getPetProblem();
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
                sendLanguageList();
                break;
        }

    }

    private void sendLanguageList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getPetProblem() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getPetProblemsList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    listPetProblem = registerApi.countryListModels;
                    ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("petProblemIdList");
                    if (selectedIdList != null) {

                        for (int j = 0; j < selectedIdList.size(); j++) {
                            String selectedId = selectedIdList.get(j);

                            for (int k = 0; k < listPetProblem.size(); k++) {
                                if ((listPetProblem.get(k).getId() + "").equals(selectedId)) {
                                    listPetProblem.get(k).checked = true;
                                }
                            }
                        }
                    }
                    binding.rvPetProblem.setLayoutManager(new LinearLayoutManager(
                            PetProblemListActivity.this));
                    petProblemAdapter = new EducationAdapter(listPetProblem, new EducationAdapter
                            .OnItemCheckListener() {
                        @Override
                        public void onItemCheck(String name, String id) {
                            itemNameList.add(name);
                            itemIdList.add(id);
                            petProblemAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onItemUncheck(String name, String id) {
                            itemNameList.remove(name);
                            itemIdList.remove(id);
                            petProblemAdapter.notifyDataSetChanged();
                        }
                    });
                    binding.rvPetProblem.setAdapter(petProblemAdapter);
                    binding.progress.setVisibility(View.GONE);

                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (PetProblemListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(PetProblemListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(PetProblemListActivity.this, ""
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
                Toast.makeText(PetProblemListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}