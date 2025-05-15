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
import com.housemaid.activities.agency.fromHome.AddMaidActivity2;
import com.housemaid.adapter.CountryAdapter;
import com.housemaid.databinding.ActivityStateListBinding;
import com.housemaid.model.CountryListModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.ServerResponseCountryList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StateListActivity extends BaseActivity implements View.OnClickListener {

    ActivityStateListBinding binding;
    int countryID;
    private SharedPreference sharedPreference;
    private CountryAdapter countryAdapter;
    private ArrayList<CountryListModel> listState;
    private ArrayList<CountryListModel> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_state_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        countryID = sharedPreference.getInteger("country_id", 0);
        listState = new ArrayList<>();
        binding.progress.setVisibility(View.VISIBLE);
        binding.etSearchSate.setEnabled(false);
        getStateList(countryID);
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.etSearchSate.addTextChangedListener(new TextWatcher() {
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
        for (CountryListModel countryList : listState) {

            String countryName = countryList.getName().toLowerCase();
            if (countryName.contains(newText))
                newList.add(countryList);
        }
        countryAdapter.setfilter(newList);
    }


    @Override
    public void onClick(View v) {

    }
    private void getStateList(int country_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getSateList(country_id);

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                binding.progress.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    listState = registerApi.countryListModels;

                    if (listState.size()>0) {
                        binding.etSearchSate.setEnabled(true);
                        binding.rvState.setLayoutManager(new LinearLayoutManager(StateListActivity.this));
                        countryAdapter = new CountryAdapter(StateListActivity.this, listState);
                        binding.rvState.setAdapter(countryAdapter);
                        binding.progress.setVisibility(View.GONE);
                        countryAdapter.notifyDataSetChanged();
                        newList = listState;
                        onRecyclerViewClick();
                    }else binding.tvNoData.setVisibility(View.VISIBLE);

                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (StateListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(StateListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(StateListActivity.this, ""
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
                binding.etSearchSate.setEnabled(true);
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(StateListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void onRecyclerViewClick() {
        binding.rvState.addOnItemTouchListener(new RecyclerTouchListener(this,
                binding.rvState, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ValidationUtils.hideSoftKeyboard(StateListActivity.this);
                String stateName = newList.get(position).getName();
                int stateId = newList.get(position).getId();
                Intent resultIntent = new Intent();
                sharedPreference.putInteger("state_ID", stateId);
                resultIntent.putExtra("state", stateName);
                resultIntent.putExtra("state_id", stateId);
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
