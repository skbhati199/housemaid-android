package com.housemaid.activities.user.fromHome;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.housemaid.adapter.HomeListingAdapter;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.HomeUserActivityBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.bean.UserDetailModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.model.response.RegisterApiForMaidList;
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
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.housemaid.constants.Constants.LOCALE;

public class HomeUserActivity extends BaseActivity implements View.OnClickListener, callMethod,
        SwipeRefreshLayout.OnRefreshListener {

    private HomeUserActivityBinding binding;
    private SharedPreference sharedPreference;
    private String accessToken;
    private ArrayList<UserDetailModel> maidListingList;
    private SignUpModel signUpModel;
    private HomeListingAdapter homeListingAdapter;
    private int filterKey;
    private String accessFrom = "";
    private FirebaseDatabase firebaseDatabase;
    private int messageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.home_user_activity);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        sharedPreference.putBoolean("session", true);
        sharedPreference.putInteger("page_selection", 0);
        accessToken = sharedPreference.getString("signUp_token", "");
        maidListingList = (ArrayList<UserDetailModel>) getIntent().getSerializableExtra("filterMaidList");
        filterKey = getIntent().getIntExtra("filterKey", 0);
        int disableKey = sharedPreference.getInteger("disable_key", 0);
        binding.rvHomeUserListing.setLayoutManager(
                new LinearLayoutManager(HomeUserActivity.this));
        firebaseDatabase = FirebaseDatabase.getInstance();





        if (disableKey == 1) {
            Toast.makeText(this, R.string.disable_quote,
                    Toast.LENGTH_LONG).show();
            binding.sidebarContent.tvPastBookings.setEnabled(false);
            //binding.sidebarContent.tvAgenciess.setEnabled(false);
            binding.sidebarContent.tvHome.setEnabled(false);
            binding.sidebarContent.tvBuyCredit.setEnabled(false);
            binding.sidebarContent.tvMyFavorites.setEnabled(false);
            binding.sidebarContent.tvMyListings.setEnabled(false);
            binding.sidebarContent.layoutMessages.setEnabled(false);
            binding.toolbar.ivFilter.setEnabled(false);
            binding.toolbar.ivNotification.setEnabled(false);
            binding.sidebarContent.ivEdit.setEnabled(false);

        } else {
            binding.toolbar.ivMenu.setEnabled(false);
            binding.toolbar.ivNotification.setVisibility(View.VISIBLE);
        }

        binding.toolbar.tvTitle.setText(sharedPreference.getString("location", ""));
        binding.swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        uiChanges();

        if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
            binding.progress.setVisibility(View.VISIBLE);
            getUserProfile(LOCALE, accessToken);
        }

        if (filterKey == 1) {
            binding.toolbar.ivFilter.setEnabled(true);
            if (maidListingList.size() > 0) {
                calls();

            } else binding.tvNoData.setVisibility(View.VISIBLE);
        } else {
            if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
                getMaidList(accessToken);
            }
        }

        binding.tvSearchMaid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

    }

    private void uiChanges() {
        binding.toolbar.ivBack.setVisibility(View.GONE);
        binding.toolbar.ivMenu.setVisibility(View.VISIBLE);
        binding.toolbar.ivFilter.setVisibility(View.VISIBLE);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        binding.toolbar.ivMenu.setEnabled(false);
        binding.tvSearchMaid.setEnabled(false);
        binding.sidebarContent.tvMyMaids.setVisibility(View.GONE);
        binding.sidebarContent.tvMaidProfile.setVisibility(View.GONE);
        binding.sidebarContent.tvOtherAgencies.setVisibility(View.GONE);
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
        binding.sidebarContent.tvMyListings.setOnClickListener(this);
        binding.sidebarContent.tvAgenciess.setOnClickListener(this);
        binding.sidebarContent.tvPastBookings.setOnClickListener(this);
        binding.sidebarContent.tvSettings.setOnClickListener(this);
        binding.sidebarContent.tvBuyCredit.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public void checkMessageCount() {
        messageCount = 0;
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
    public void onRefresh() {
        binding.swipeRefreshLayout.setRefreshing(true);
        if (ValidationUtils.isOnline(binding.drawerLayout, this)) {

            getUserProfile(Constants.TIMEZONE, accessToken);
        }
        if (filterKey == 1) {
            if (maidListingList.size() > 0) {

                calls();
            } else {
                binding.tvNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(R.string.sorry);
            }
        } else if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
            getMaidList(accessToken);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivMenu) {

                openDrawerLayout();
                

            
} else if (v.getId() == R.id.ivFilter) {

                Intent intentFilter = new Intent(this, FilterActivity.class);
                intentFilter.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentFilter);
                

            
} else if (v.getId() == R.id.ivNotification) {

                startActivity(new Intent(this, NotificationActivity.class));
                

            
} else if (v.getId() == R.id.ivBack) {

                super.onBackPressed();
                

            
} else if (v.getId() == R.id.ivEdit) {

                Intent intent1 = new Intent(this, EditUserProfileActivity.class);
                intent1.putExtra("userProfile", signUpModel);
                startActivity(intent1);
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.civProfilePic) {

                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("userProfile", signUpModel);
                startActivity(intent);
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvTitle) {

                startActivity(new Intent(this, EnterLocationActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvHome) {

                binding.progress.setVisibility(View.VISIBLE);
                getMaidList(accessToken);
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvMyFavorites) {

                startActivity(new Intent(this, MyFavoritesListingActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.layoutMessages) {

                startActivity(new Intent(this, ChatActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvMyListings) {

                binding.drawerLayout.closeDrawers();
                startActivity(new Intent(this, MyListingsActivity.class));
                

            
} else if (v.getId() == R.id.tvAgenciess) {

                startActivity(new Intent(this, AgencyActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvPastBookings) {

                startActivity(new Intent(this, PastBookingsActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvSettings) {

                Intent intent2 = new Intent(this, SettingsActivity.class);
                intent2.putExtra("userProfile", signUpModel);
                startActivity(intent2);
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvBuyCredit) {

                startActivity(new Intent(this, UpgradeMemberShipActivity.class));
                binding.drawerLayout.closeDrawers();
                
        
}
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (signUpModel != null) checkMessageCount();

        if (homeListingAdapter != null) {
            if (filterKey != 1) {
                binding.swipeRefreshLayout.setRefreshing(true);
                getUserProfile(LOCALE, accessToken);
                getMaidList(accessToken);
            }
        }
    }

    private void filter(String newText) {
        ArrayList<UserDetailModel> filterList = new ArrayList<>();

        for (int i = 0; i < maidListingList.size(); i++) {
            if (!maidListingList.get(i).getIs_hired()) {
                filterList.add(maidListingList.get(i));
            }
        }

        newText = newText.toLowerCase();
        ArrayList<UserDetailModel> newList = new ArrayList<>();
        for (UserDetailModel maidList : filterList) {

            String maidName = maidList.getName().toLowerCase();
            String country = maidList.getCountry_name().toLowerCase();
            String city = maidList.getState_name().toLowerCase();
            String state = maidList.getState_name().toLowerCase();
            String language = maidList.getUserLanguageModel().get(0).getLanguage_detail().getName();

            if (maidName.contains(newText) || country.contains(newText) || city.contains(newText) ||
                    state.contains(newText) || language.contains(newText))
                newList.add(maidList);

        }
        if (newList.isEmpty()) {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.tvNoData.setText("No maid found.");
        } else binding.tvNoData.setVisibility(View.GONE);
        HomeListingAdapter homeListingAdapter1 = new HomeListingAdapter(HomeUserActivity.this,
                newList, this, accessFrom);
        binding.rvHomeUserListing.setAdapter(homeListingAdapter1);


        //homeListingAdapter.setfilter(newList);
    }

    private void openDrawerLayout() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                binding.drawerLayout, (Toolbar) binding.toolbar.getRoot(),
                R.string.open_drawer, R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(drawerToggle);

        binding.sidebarContent.btnMessageCount.setText("" + messageCount);
        binding.drawerLayout.openDrawer(Gravity.START);
    }

    private void getMaidList(String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForMaidList> call = apiService.getMaidList(access_token);
        call.enqueue(new Callback<RegisterApiForMaidList>() {
            @Override
            public void onResponse(Call<RegisterApiForMaidList> call,
                                   Response<RegisterApiForMaidList> response) {
                if (response.isSuccessful()) {
                    binding.relativeLayout.setClickable(true);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);

                    RegisterApiForMaidList registerApi = response.body();
                    maidListingList = registerApi.getMaidDetailModel();
                    String message = registerApi.message;
                    if (message != null) {

                        binding.toolbar.ivFilter.setEnabled(true);
                        if (maidListingList.size() > 0) {

                            binding.tvSearchMaid.setEnabled(true);
                            sharedPreference.putInteger("count", 1);
                            calls();
                            binding.tvNoData.setVisibility(View.GONE);
                        } else binding.tvNoData.setVisibility(View.VISIBLE);

                    } else {

                        Toast.makeText(HomeUserActivity.this, "Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeUserActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(HomeUserActivity.this, "" + response.errorBody()
                                    .string(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiForMaidList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(HomeUserActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calls() {
        ArrayList<UserDetailModel> filterList = new ArrayList<>();

       /* for (int i = 0; i < maidListingList.size(); i++) {
            if (!maidListingList.get(i).getIs_hired()) {
                filterList.add(maidListingList.get(i));
            }
        }*/
        binding.swipeRefreshLayout.setRefreshing(false);

        homeListingAdapter = new HomeListingAdapter(HomeUserActivity.this,
                maidListingList, this, accessFrom);
        binding.rvHomeUserListing.setAdapter(homeListingAdapter);

    }

    private void makeFavourite(String access_token, String key, String jobListingId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.makeMaidFavourite(access_token, key,
                jobListingId);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(HomeUserActivity.this, "Removed from favourites!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeUserActivity.this, "Added to favourites!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(HomeUserActivity.this, "Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(HomeUserActivity.this, "" + response.errorBody(),
                                Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(HomeUserActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserProfile(String locale, String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getProfileDetail(Constants.TIMEZONE, locale, access_token);
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

                        getTotalCredits(accessToken);
                        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        binding.toolbar.ivMenu.setEnabled(true);
                        sharedPreference.putString("user_id", String.valueOf(signUpModel.getId()));
                        sharedPreference.putString("user_name", String.valueOf(signUpModel.getName()));
                        if (!signUpModel.getUserImagesModel().isEmpty()) {
                            sharedPreference.putString("user_pic", String.valueOf(signUpModel
                                    .getUserImagesModel().get(0).getImageModel().getBig()));

                            Glide.with(HomeUserActivity.this).load(signUpModel.getUserImagesModel().get(0).getImageModel()
                                    .getSmall())
                                    .centerCrop()
                                    .error(R.drawable.avatar)
                                    .into(binding.sidebarContent.civProfilePic);
                        } else
                            binding.sidebarContent.civProfilePic.setImageResource(R.drawable.user_c);
                        binding.sidebarContent.tvName.setText(signUpModel.getName());
                        binding.sidebarContent.tvEmail.setText(signUpModel.getEmail());
                        checkMessageCount();

                    } else {
                        Toast.makeText(HomeUserActivity.this, "Registration Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        Toast.makeText(HomeUserActivity.this, "" + response.errorBody().string(),
                                Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeUserActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(HomeUserActivity.this,
                                "No Response! Refresh!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeUserActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(HomeUserActivity.this, "" + response.errorBody(),
                                    Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeUserActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

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