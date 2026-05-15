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
import com.housemaid.databinding.ActivityNationalityListBinding;
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

public class NationalityListActivity extends BaseActivity implements View.OnClickListener {
    private ActivityNationalityListBinding binding;
    private CountryAdapter nationalityAdapter;
    private ArrayList<CountryListModel> newList;
    private ArrayList<CountryListModel> nationalityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nationality_list);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.tvHeader.setText(R.string.select_nationality);
        nationalityList = new ArrayList<>();
        binding.progress.setVisibility(View.VISIBLE);
        binding.etSearchNationality.setEnabled(false);
        getNationality();

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.etSearchNationality.addTextChangedListener(new TextWatcher() {
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
        for (CountryListModel countryList : nationalityList) {

            String countryName = countryList.getName().toLowerCase();
            if (countryName.contains(newText))
                newList.add(countryList);
        }
        nationalityAdapter.setfilter(newList);
    }
    @Override
    public void onClick(View v) {

    }

    private void getNationality() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ServerResponseCountryList> call = apiService.getNationalityList();

        call.enqueue(new Callback<ServerResponseCountryList>() {

            @Override
            public void onResponse(Call<ServerResponseCountryList> call,
                                   Response<ServerResponseCountryList> response) {
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    ServerResponseCountryList registerApi = response.body();

                    nationalityList = registerApi.countryListModels;
                    if (nationalityList.size() > 0) {

                        binding.etSearchNationality.setEnabled(true);
                        binding.rvNationality.setLayoutManager(new LinearLayoutManager(
                                NationalityListActivity.this));
                        nationalityAdapter = new CountryAdapter(NationalityListActivity.this,
                                nationalityList);
                        binding.rvNationality.setAdapter(nationalityAdapter);
                        binding.progress.setVisibility(View.GONE);
                        nationalityAdapter.notifyDataSetChanged();
                        newList = nationalityList;
                        onRecyclerViewClick();

                    }else binding.tvNoData.setVisibility(View.VISIBLE);
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            SharedPreference sharedPreference = SharedPreference.getInstance
                                    (NationalityListActivity.this);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(NationalityListActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {

                            Toast.makeText(NationalityListActivity.this, ""
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
                binding.etSearchNationality.setEnabled(true);
                Toast.makeText(NationalityListActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRecyclerViewClick() {
        binding.rvNationality.addOnItemTouchListener(new RecyclerTouchListener(
                this, binding.rvNationality, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ValidationUtils.hideSoftKeyboard(NationalityListActivity.this);
                String stateName = newList.get(position).getName();
                int stateId = newList.get(position).getId();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("nationality", stateName);
                resultIntent.putExtra("nationality_id", stateId);
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

        private NationalityListActivity.ClickListener clicklistener;
        private GestureDetector gestureDetector;

        RecyclerTouchListener(Context context, final RecyclerView recycleView,
                              final NationalityListActivity.ClickListener clicklistener) {

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
