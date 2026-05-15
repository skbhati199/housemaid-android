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
import com.housemaid.databinding.ActivityWorkingchoicesListBinding;
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

public class WorkingchoicesListActivity extends BaseActivity implements View.OnClickListener {


    ActivityWorkingchoicesListBinding binding;
    EducationAdapter workingAdapter;
    ArrayList<CountryListModel> listWorking;
    ArrayList<String> itemNameList;
    ArrayList<String> itemIdList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_workingchoices_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText("Select Working Choice");
        listWorking = new ArrayList<>();
        itemNameList = getIntent().getStringArrayListExtra("workingList");
        itemIdList = getIntent().getStringArrayListExtra("workingIdList");
        binding.progress.setVisibility(View.VISIBLE);
        binding.btnSubmit.setClickable(false);
        getWorkingList();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {

                sendWorkingList();
                
        
}
    }

    private void sendWorkingList() {
        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("nameList", itemNameList);
        resultIntent.putStringArrayListExtra("idList", itemIdList);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getWorkingList() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getworkingChoiceList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnSubmit.setClickable(true);
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    ServerResponseCountryList registerApi = response.body();

                    listWorking = registerApi.countryListModels;
                    if (listWorking.size() > 0) {

                        ArrayList<String> selectedIdList = getIntent().getStringArrayListExtra("workingIdList");
                        if (selectedIdList != null) {

                            for (int j = 0; j < selectedIdList.size(); j++) {
                                String selectedId = selectedIdList.get(j);

                                for (int k = 0; k < listWorking.size(); k++) {
                                    if ((listWorking.get(k).getId() + "").equals(selectedId)) {
                                        listWorking.get(k).checked = true;
                                    }
                                }
                            }
                        }
                        binding.rvWorkingChoice.setLayoutManager(new LinearLayoutManager(
                                WorkingchoicesListActivity.this));
                        workingAdapter = new EducationAdapter(listWorking, new EducationAdapter
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
                        binding.rvWorkingChoice.setAdapter(workingAdapter);
                        workingAdapter.notifyDataSetChanged();
                    } else binding.tvNoData.setVisibility(View.VISIBLE);
                    //onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (WorkingchoicesListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(WorkingchoicesListActivity
                                    .this, SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(WorkingchoicesListActivity.this, ""
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
                binding.btnSubmit.setClickable(true);
                Toast.makeText(WorkingchoicesListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}