package com.housemaid.activities.dataList;

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
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.adapter.CountryAdapter;
import com.housemaid.databinding.ActivityCitylistSingleBinding;
import com.housemaid.model.CountryListModel;
import com.housemaid.model.response.ServerResponseCountryList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.FilterListUtils;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CitylistSingleActivity extends BaseActivity {

    private ActivityCitylistSingleBinding binding;
    private CountryAdapter cityAdapter;
    private ArrayList<CountryListModel> listCity;
    private SharedPreference sharedPreference;
    private ArrayList<CountryListModel> newList;
    private String districtId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_citylist_single);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();

        districtId = getIntent().getStringExtra("districtId");
        sharedPreference = SharedPreference.getInstance(this);
        newList = new ArrayList<>();
        listCity = new ArrayList<>();
        int stateId = sharedPreference.getInteger("state_ID", 0);
        binding.layout.tvHeader.setText(R.string.select_city);
        binding.progress.setVisibility(View.VISIBLE);
        binding.layout.etSearchCountry.setEnabled(false);
        getCityList(stateId);
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
        for (CountryListModel countryList : listCity) {

            String countryName = countryList.getName().toLowerCase();
            if (countryName.contains(newText))
                newList.add(countryList);
        }
        cityAdapter.setfilter(newList);
    }


    private void getCityList(int state_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getCityList(Integer.parseInt(districtId));

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call, Response<ServerResponseCountryList> response) {

                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    listCity = registerApi.countryListModels;
                    if (listCity.size() > 0) {
                        binding.layout.rvCountry.setLayoutManager(new LinearLayoutManager(CitylistSingleActivity.this));
                        cityAdapter = new CountryAdapter(CitylistSingleActivity.this, listCity);
                        binding.layout.rvCountry.setAdapter(cityAdapter);
                        binding.progress.setVisibility(View.GONE);
                        cityAdapter.notifyDataSetChanged();
                        binding.layout.etSearchCountry.setEnabled(true);
                        newList = listCity;
                        onRecyclerViewClick();
                    } else binding.tvNoData.setVisibility(View.VISIBLE);

                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(CitylistSingleActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(CitylistSingleActivity.this, ""
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
                Toast.makeText(CitylistSingleActivity.this, "error" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRecyclerViewClick() {
        binding.layout.rvCountry.addOnItemTouchListener(new RecyclerTouchListener(
                CitylistSingleActivity.this, binding.layout.rvCountry, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String countryName = newList.get(position).getName();
                int id = newList.get(position).getId();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("city", countryName);
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