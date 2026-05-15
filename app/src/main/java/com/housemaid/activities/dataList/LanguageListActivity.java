package com.housemaid.activities.dataList;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.agency.fromHome.AddMaidActivity2;
import com.housemaid.adapter.EducationAdapter;

import com.housemaid.databinding.ActivityLanguageListBinding;
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

public class LanguageListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLanguageListBinding binding;
    private EducationAdapter languageAdapter;
    private ArrayList<CountryListModel> listLanguage;
    private ArrayList<String> itemNameList;
    private ArrayList<String> itemIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText(R.string.select_language);
        listLanguage = new ArrayList<>();
        itemNameList = getIntent().getStringArrayListExtra("languageNameList");
        itemIdList = getIntent().getStringArrayListExtra("languageIdList");
        binding.progress.setVisibility(View.VISIBLE);
        getLanguage();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {

                sendLanguageList();
                
        
}

    }

    private void sendLanguageList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getLanguage() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getLanguageList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    listLanguage = registerApi.countryListModels;
                    ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("languageIdList");
                    if (selectedIdList != null) {

                        for (int j = 0; j < selectedIdList.size(); j++) {
                            String selectedId = selectedIdList.get(j);

                            for (int k = 0; k < listLanguage.size(); k++) {
                                if ((listLanguage.get(k).getId() + "").equals(selectedId)) {
                                    listLanguage.get(k).checked = true;
                                }
                            }
                        }
                    }

                    binding.rvLanguage.setLayoutManager(new LinearLayoutManager(
                            LanguageListActivity.this));
                    languageAdapter = new EducationAdapter(listLanguage, new EducationAdapter
                            .OnItemCheckListener() {
                        @Override
                        public void onItemCheck(String name, String id) {

                            itemNameList.add(name);
                            itemIdList.add(id);
                            languageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onItemUncheck(String name, String id) {
                            itemNameList.remove(name);
                            itemIdList.remove(id);
                            languageAdapter.notifyDataSetChanged();
                        }
                    });
                    binding.rvLanguage.setAdapter(languageAdapter);
                    binding.progress.setVisibility(View.GONE);

                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (LanguageListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(LanguageListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(LanguageListActivity.this, ""
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
                Toast.makeText(LanguageListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}