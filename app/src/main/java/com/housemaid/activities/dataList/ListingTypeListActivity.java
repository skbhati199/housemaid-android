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
import com.housemaid.databinding.ActivityListingTypeListBinding;
import com.housemaid.model.CountryListModel;
import com.housemaid.model.response.ServerResponseCountryList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.FilterListUtils;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingTypeListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityListingTypeListBinding binding;
    private CountryAdapter listingTypeAdapter;
    private ArrayList<CountryListModel> listingTypeList;
    private ArrayList<CountryListModel> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_listing_type_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        listingTypeList = new ArrayList<>();
        SharedPreference sharedPreference = SharedPreference.getInstance(this);


        String accessToken = sharedPreference.getString("signUp_token", "0");

        binding.progress.setVisibility(View.VISIBLE);
        binding.etSearchListing.setEnabled(false);
        getListing(accessToken);
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.etSearchListing.addTextChangedListener(new TextWatcher() {
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
        for (CountryListModel countryList : listingTypeList) {

            String countryName = countryList.getName().toLowerCase();
            if (countryName.contains(newText))
                newList.add(countryList);
        }
        listingTypeAdapter.setfilter(newList);
    }

    @Override
    public void onClick(View v) {

    }

    private void getListing(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getListingType(accessToken);

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    ServerResponseCountryList registerApi = response.body();

                    listingTypeList = registerApi.countryListModels;
                    if (listingTypeList.size() > 0) {
                        binding.etSearchListing.setEnabled(false);
                        binding.rvListingType.setLayoutManager(new LinearLayoutManager(
                                ListingTypeListActivity.this));
                        listingTypeAdapter = new CountryAdapter(ListingTypeListActivity.this,
                                listingTypeList);
                        binding.rvListingType.setAdapter(listingTypeAdapter);

                        listingTypeAdapter.notifyDataSetChanged();
                        newList = listingTypeList;
                        onRecyclerViewClick();

                    } else binding.tvNoData.setVisibility(View.VISIBLE);
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (ListingTypeListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(ListingTypeListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(ListingTypeListActivity.this, ""
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
                Toast.makeText(ListingTypeListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onRecyclerViewClick() {
        binding.rvListingType.addOnItemTouchListener(new RecyclerTouchListener(this,
                binding.rvListingType,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String stateName = newList.get(position).getName();
                        int stateId = newList.get(position).getId();
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("listingType", stateName);
                        resultIntent.putExtra("listingTypeId", stateId);
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