package com.housemaid.activities.dataList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.adapter.CountryAdapter;
import com.housemaid.databinding.ActivityCountryListBinding;
import com.housemaid.model.CountryListModel;
import com.housemaid.model.response.ServerResponseCountryList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryListActivity extends BaseActivity implements View.OnClickListener {


    private ActivityCountryListBinding binding;
    private CountryAdapter countryAdapter;
    private ArrayList<CountryListModel> listCountry;
    private SharedPreference sharedPreference;
    private String accessToken;
    private ArrayList<CountryListModel> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_country_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        newList = new ArrayList<>();
        sharedPreference = SharedPreference.getInstance(this);
        listCountry = new ArrayList<>();

        switch (sharedPreference.getString("start_key", "0")) {
            case "1":
                accessToken = sharedPreference.getString("signUp_token", "0");
                break;
            case "2":
                accessToken = sharedPreference.getString("signIn_token", "0");
                break;
            case "3":
                accessToken = sharedPreference.getString("changed_token", "0");
                break;
        }
        binding.progress.setVisibility(View.VISIBLE);
        binding.layout.etSearchCountry.setEnabled(false);
        getCountryList(accessToken);
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
        for (CountryListModel countryList : listCountry) {

            String countryName = countryList.getName().toLowerCase();
            if (countryName.contains(newText))
                newList.add(countryList);
        }
        countryAdapter.setfilter(newList);
    }

    @Override
    public void onClick(View v) {
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
                    binding.layout.rvCountry.setLayoutManager(new LinearLayoutManager(
                            CountryListActivity.this));
                    countryAdapter = new CountryAdapter(CountryListActivity.this, listCountry);
                    binding.layout.rvCountry.setAdapter(countryAdapter);
                    binding.progress.setVisibility(View.GONE);
                    countryAdapter.notifyDataSetChanged();
                    binding.layout.etSearchCountry.setEnabled(true);
                    newList = listCountry;
                    onRecyclerViewClick();
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(CountryListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(CountryListActivity.this, ""
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
                Toast.makeText(CountryListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRecyclerViewClick() {
        binding.layout.rvCountry.addOnItemTouchListener(new RecyclerTouchListener(
                CountryListActivity.this, binding.layout.rvCountry, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ValidationUtils.hideSoftKeyboard(CountryListActivity.this);
                String countryName = newList.get(position).getName();
                int id = newList.get(position).getId();
                Intent resultIntent = new Intent();
                sharedPreference.putInteger("country_id", id);
                resultIntent.putExtra("country", countryName);
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