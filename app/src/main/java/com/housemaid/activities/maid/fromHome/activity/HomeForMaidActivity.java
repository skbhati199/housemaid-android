package com.housemaid.activities.maid.fromHome.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.AgencyActivity;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.ChatActivity;
import com.housemaid.activities.EnterLocationActivity;
import com.housemaid.activities.FilterActivity;
import com.housemaid.activities.MyFavoritesListingActivity;
import com.housemaid.activities.MyListingsActivity;
import com.housemaid.activities.NotificationActivity;
import com.housemaid.activities.PastBookingsActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.SettingsActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.adapter.MaidHomeListAdapter;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.HomeForMaidActivityBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.model.response.RegisterApiList;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeForMaidActivity extends BaseActivity implements View.OnClickListener, callMethod,
        SwipeRefreshLayout.OnRefreshListener {

    private HomeForMaidActivityBinding binding;
    private SharedPreference sharedPreference;
    private String accessToken;
    private ArrayList<SignUpModel> jobListingList;
    private SignUpModel signUpModel;
    private int filterKey = 0;
    private FirebaseDatabase firebaseDatabase;
    private int messageCount;
    private DatabaseReference root, unreadConversationSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.home_for_maid_activity);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        sharedPreference.putBoolean("session", true);
        accessToken = sharedPreference.getString("signUp_token", "");
        int disableKey = sharedPreference.getInteger("disable_key", 1);
        jobListingList = (ArrayList<SignUpModel>) getIntent()
                .getSerializableExtra("filterJobList");
        filterKey = getIntent().getIntExtra("filterKey", 0);
        binding.toolbar.tvTitle.setText(sharedPreference.getString("location", ""));
        binding.rvHomeMaidListing.setLayoutManager(new LinearLayoutManager(HomeForMaidActivity.this));
        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference();

        uiChanges();

        if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
            binding.progress.setVisibility(View.VISIBLE);
            getUserProfile(accessToken);
            getTotalCredits(accessToken);
        }
        if (disableKey == 1) {
            Toast.makeText(this, R.string.disable_quote,
                    Toast.LENGTH_LONG).show();
            binding.sidebarContent.ivEdit.setEnabled(false);
            binding.sidebarContent.tvHome.setEnabled(false);
            //binding.sidebarContent.tvAgenciess.setEnabled(false);
            binding.sidebarContent.tvBuyCredit.setEnabled(false);
            binding.sidebarContent.tvMyFavorites.setEnabled(false);
            binding.sidebarContent.tvMyMessages.setEnabled(false);
            binding.sidebarContent.tvPastBookings.setEnabled(false);
            binding.toolbar.ivFilter.setEnabled(false);
            binding.toolbar.ivNotification.setEnabled(false);
        } else {
            binding.toolbar.ivMenu.setEnabled(false);
            binding.toolbar.ivNotification.setVisibility(View.VISIBLE);
        }

        if (filterKey == 1) {
            binding.toolbar.ivFilter.setEnabled(true);
            if (jobListingList.size() > 0) {
                calls();
            } else {
                binding.tvNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(R.string.sorry);
            }
        } else {
            if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
                binding.progress.setVisibility(View.VISIBLE);
                getJobListing(accessToken);
            }
        }

        binding.tvSearchMaid.addTextChangedListener(new TextWatcher() {
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

    private void uiChanges() {
        binding.toolbar.ivBack.setVisibility(View.GONE);
        binding.toolbar.ivMenu.setVisibility(View.VISIBLE);
        binding.toolbar.ivFilter.setVisibility(View.VISIBLE);
        binding.toolbar.ivNotification.setVisibility(View.VISIBLE);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.tvSearchMaid.setEnabled(false);
        binding.toolbar.ivMenu.setEnabled(false);
        binding.sidebarContent.tvMyMaids.setVisibility(View.GONE);
        binding.sidebarContent.tvMaidProfile.setVisibility(View.GONE);
        binding.sidebarContent.tvOtherAgencies.setVisibility(View.GONE);
        binding.sidebarContent.tvMyListings.setVisibility(View.GONE);
        binding.sidebarContent.tvHighlight.setVisibility(View.GONE);
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.swipeRefreshLayout.setOnRefreshListener(this);
        //Toolbar Clicks
        binding.toolbar.ivMenu.setOnClickListener(this);
        binding.toolbar.ivFilter.setOnClickListener(this);
        binding.toolbar.ivNotification.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.tvTitle.setOnClickListener(this);
        binding.sidebarContent.civProfilePic.setOnClickListener(this);
        binding.sidebarContent.ivEdit.setOnClickListener(this);
        //navigationView Clicks
        binding.sidebarContent.tvHome.setOnClickListener(this);
        binding.sidebarContent.tvMyFavorites.setOnClickListener(this);
        binding.sidebarContent.layoutMessages.setOnClickListener(this);
        binding.sidebarContent.tvAgenciess.setOnClickListener(this);
        binding.sidebarContent.tvBuyCredit.setOnClickListener(this);
        binding.sidebarContent.tvPastBookings.setOnClickListener(this);
        binding.sidebarContent.tvSettings.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMenu:
                openDrawerLayout();
                ValidationUtils.hideSoftKeyboard(this);
                break;

            case R.id.ivFilter:
                startActivity(new Intent(this, FilterActivity.class));
                break;

            case R.id.ivNotification:
                startActivity(new Intent(this, NotificationActivity.class));
                break;

            case R.id.ivEdit:
                Intent intent1 = new Intent(this, EditMaidPofileActivity.class);
                intent1.putExtra("maidDetail", signUpModel);
                intent1.putExtra("edit_maid_pic", 1);
                startActivity(intent1);
                break;

            case R.id.civProfilePic:
                Intent intent = new Intent(this, MaidHomeProfileActivity.class);
                intent.putExtra("maidDetail", signUpModel);
                startActivity(intent);
                break;

            case R.id.tvTitle:
                startActivity(new Intent(this, EnterLocationActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.ivBack:
                super.onBackPressed();
                break;

            case R.id.tvHome:
                if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
                    binding.progress.setVisibility(View.VISIBLE);
                    getJobListing(accessToken);
                    binding.drawerLayout.closeDrawers();
                }
                break;

            case R.id.tvMyFavorites:
                startActivity(new Intent(this, MyFavoritesListingActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.layoutMessages:
                startActivity(new Intent(this, ChatActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.tvMyListings:
                startActivity(new Intent(this, MyListingsActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.tvAgenciess:
                startActivity(new Intent(this, AgencyActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.tvPastBookings:
                startActivity(new Intent(this, PastBookingsActivity.class));
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.tvSettings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                intent2.putExtra("userProfile", signUpModel);
                startActivity(intent2);
                binding.drawerLayout.closeDrawers();
                break;

            case R.id.tvBuyCredit:
                startActivity(new Intent(this, UpgradeMemberShipActivity.class));
                binding.drawerLayout.closeDrawers();
                break;
        }
    }

    @Override
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        if (ValidationUtils.isOnline(binding.drawerLayout, this)) {

            getUserProfile(accessToken);
            getTotalCredits(accessToken);
        }
        if (filterKey == 1) {
            if (jobListingList.size() > 0) {
                calls();
            } else {
                binding.tvNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(R.string.sorry);
            }
        } else getJobListing(accessToken);

    }

    public void checkMessageCount() {
        messageCount = 0;
        unreadConversationSender = root.child("Users").child("Unread_Conversation")
                .child("User_" + signUpModel.getId());
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users")
                .child("Unread_Conversation").child("User_" + signUpModel.getId());
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.getValue().toString().equals("0")) {
                    messageCount++;
                    Log.d("count", String.valueOf(messageCount));
                } else {
                    if (messageCount >= 1) messageCount--;
                    Log.d("count", String.valueOf(messageCount));
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue().toString().equals("0")) {
                    messageCount++;
                    Log.d("count", String.valueOf(messageCount));
                } else {
                    if (messageCount >= 1) messageCount--;
                    Log.d("count", String.valueOf(messageCount));
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (signUpModel != null) checkMessageCount();
        if (filterKey != 1) {
            if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
                binding.swipeRefreshLayout.setRefreshing(true);
                getJobListing(accessToken);
            }
        }

    }

    private void filter(String newText) {

        newText = newText.toLowerCase();
        ArrayList<SignUpModel> newList = new ArrayList<>();
        for (SignUpModel jobList : jobListingList) {

            String jobTitle = jobList.getJob_listing_title_name().toLowerCase();
            String country = jobList.getCountry_name().toLowerCase();
            String city = jobList.getCity_name().toLowerCase();
            String state = jobList.getState_name().toLowerCase();
            String district = jobList.getDistrict_name().toLowerCase();
            String language = jobList.getLanguageModels().get(0).getLanguage_name();
            if (jobTitle.contains(newText) || country.contains(newText) || city.contains(newText)
                    || state.contains(newText) || language.contains(newText) || district.contains(newText))
                newList.add(jobList);
        }
        if (newList.isEmpty()) {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.tvNoData.setText(R.string.no_job_listing_found);
        } else binding.tvNoData.setVisibility(View.GONE);
        String accessFrom = "";
        MaidHomeListAdapter maidHomeListAdapter1 = new MaidHomeListAdapter(HomeForMaidActivity.this,
                newList, this, accessFrom);
        binding.rvHomeMaidListing.setAdapter(maidHomeListAdapter1);
    }

    private void openDrawerLayout() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                binding.drawerLayout, (Toolbar) binding.toolbar.getRoot(),
                R.string.open_drawer, R.string.close_drawer);

        binding.drawerLayout.addDrawerListener(drawerToggle);
        binding.sidebarContent.btnMessageCount.setText("" + messageCount);
        binding.drawerLayout.openDrawer(Gravity.START);
    }

    private void getJobListing(String access_token) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.getJobListing(access_token);
        call.enqueue(new Callback<RegisterApiList>() {
            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                binding.swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    jobListingList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {
                        binding.toolbar.ivFilter.setEnabled(true);
                        if (jobListingList.size() > 0) {

                            binding.tvSearchMaid.setEnabled(true);
                            calls();
                            binding.tvNoData.setVisibility(View.GONE);

                        } else binding.tvNoData.setVisibility(View.VISIBLE);

                    } else {

                        Toast.makeText(HomeForMaidActivity.this, "Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeForMaidActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(HomeForMaidActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.progress.setVisibility(View.GONE);
                if (t instanceof ConnectException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();

                } else if (t instanceof SocketTimeoutException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
                } else if (t instanceof UnknownHostException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                } else if (t instanceof InternalError) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calls() {
        String accessFrom = "";
        MaidHomeListAdapter maidHomeListAdapter = new MaidHomeListAdapter(HomeForMaidActivity.this, jobListingList,
                this, accessFrom);
        binding.rvHomeMaidListing.setLayoutManager(new LinearLayoutManager(HomeForMaidActivity.this));
        binding.rvHomeMaidListing.setAdapter(maidHomeListAdapter);
    }

    private void makeFavourite(String access_token, String key, String jobListingId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.makeFavourite(access_token, key, jobListingId);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(HomeForMaidActivity.this, R.string.removed_from_favorites,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeForMaidActivity.this, R.string.added_to_favorites,
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(HomeForMaidActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                if (response.code() == 401) {
                    binding.progress.setVisibility(View.GONE);
                    sharedPreference.deletePreference();
                    Intent signInIntent = new Intent(HomeForMaidActivity.this,
                            SelectionActivity.class);
                    startActivity(signInIntent);
                    finishAffinity();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(HomeForMaidActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getUserProfile(String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getProfileDetail(Constants.TIMEZONE, Constants.LOCALE, access_token);
        call.enqueue(new Callback<RegisterApi>() {
            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        binding.toolbar.ivMenu.setEnabled(true);
                        sharedPreference.putString("maid_id", String.valueOf(signUpModel.getId()));
                        sharedPreference.putString("maid_name", String.valueOf(signUpModel.getName()));
                        if (!signUpModel.getUserImagesModel().isEmpty()) {
                            sharedPreference.putString("maid_pic", String.valueOf(signUpModel
                                    .getUserImagesModel().get(0).getImageModel().getBig()));
                        }
                        binding.sidebarContent.tvName.setText(signUpModel.getName());
                        binding.sidebarContent.tvEmail.setText(signUpModel.getEmail());
                        if (!signUpModel.getUserImagesModel().isEmpty() && !signUpModel.getUserImagesModel().get(0).getImageModel()
                                .getSmall().isEmpty()) {
                            Glide.with(itemView.getContext()).load(signUpModel.getUserImagesModel().get(0).getImageModel()
                                    .getSmall())
                                    .fit().centerCrop()
                                    .error(R.drawable.avatar)
                                    .into(binding.sidebarContent.civProfilePic);
                        } else
                            binding.sidebarContent.civProfilePic.setImageResource(R.drawable.user_c);

                        checkMessageCount();
                    } else {

                        Toast.makeText(HomeForMaidActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(HomeForMaidActivity.this, ""
                                + response.errorBody().string(), Toast.LENGTH_LONG).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                if (t instanceof ConnectException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();

                } else if (t instanceof SocketTimeoutException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
                } else if (t instanceof UnknownHostException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                } else if (t instanceof InternalError) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getTotalCredits(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getTotalCredit(accessToken);
        call.enqueue(new Callback<RegisterApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    SignUpModel totalCredits = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", totalCredits.getTotal_credit());

                    } else {

                        Toast.makeText(HomeForMaidActivity.this,
                                "No response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeForMaidActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(HomeForMaidActivity.this, ""
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
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                if (t instanceof ConnectException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();

                } else if (t instanceof SocketTimeoutException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
                } else if (t instanceof UnknownHostException) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                } else if (t instanceof InternalError) {

                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeForMaidActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void makeFavourite(int key, int jobListId) {
        makeFavourite(accessToken, String.valueOf(key), String.valueOf(jobListId));
    }

    @Override
    public void makeUnfavourite(int key, int jobListId, int position) {

    }

    @Override
    public void removeMaid(int position) {

    }

}