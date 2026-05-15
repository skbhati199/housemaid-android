package com.housemaid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.CountryAdapter;
import com.housemaid.databinding.ActivityJobCategorySingleBinding;
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

public class JobCategorySingleActivity extends BaseActivity implements View.OnClickListener {

    ActivityJobCategorySingleBinding binding;
    RelativeLayout layout;
    CountryAdapter jobAdapter;
    ArrayList<CountryListModel> listJob;
    ArrayList<CountryListModel> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_category_single);
        init();
        initControls();

    }

    @Override
    public void init() {
        super.init();
        newList = new ArrayList<>();
        listJob = new ArrayList<>();
        binding.layout.tvHeader.setText(R.string.select_job_category);
        binding.progress.setVisibility(View.VISIBLE);
        binding.layout.etSearchCountry.setEnabled(false);
        getJobChoice();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.layout.etSearchCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }
    private void filter(String newText) {
        newText = newText.toLowerCase();
        newList = new ArrayList<>();
        for (CountryListModel countryList : listJob) {

            String countryName = countryList.getName().toLowerCase();
            if (countryName.contains(newText))
                newList.add(countryList);
        }
        jobAdapter.setfilter(newList);
    }



    @Override
    public void onClick(View v) {

    }

    private void getJobChoice() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getJobChoiceList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    listJob = registerApi.countryListModels;
                    binding.layout.rvCountry.setLayoutManager(new LinearLayoutManager(
                            JobCategorySingleActivity.this));
                    jobAdapter = new CountryAdapter(JobCategorySingleActivity.this, listJob);
                    binding.layout.rvCountry.setAdapter(jobAdapter);
                    binding.progress.setVisibility(View.GONE);
                    jobAdapter.notifyDataSetChanged();
                    binding.layout.etSearchCountry.setEnabled(true);
                    newList = listJob;
                    onRecyclerViewClick();

                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (JobCategorySingleActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(JobCategorySingleActivity
                                    .this, SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(JobCategorySingleActivity.this, new Gson().fromJson
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ServerResponseCountryList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(JobCategorySingleActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRecyclerViewClick() {
        binding.layout.rvCountry.addOnItemTouchListener(new RecyclerTouchListener(
                JobCategorySingleActivity.this, binding.layout.rvCountry, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String countryName = newList.get(position).getName();
                int id = newList.get(position).getId();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("jobchoice", countryName);
                resultIntent.putExtra("id", id);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        RecyclerTouchListener(Context context, final RecyclerView recycleView,
                              final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}