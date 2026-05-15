package com.housemaid.activities.agency.fromHome;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.AgencyActivity;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.ChatActivity;
import com.housemaid.activities.EnterLocationActivity;
import com.housemaid.activities.FilterActivity;
import com.housemaid.activities.NotificationActivity;
import com.housemaid.activities.PastBookingsActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.SettingsActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.adapter.MaidHomeListAdapter;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.HomeAgencyActivityBinding;
import com.housemaid.interfaces.callMethod;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.StatusModel;
import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.ErrorResponse;
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
import com.google.gson.Gson;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAgencyActivity extends BaseActivity implements View.OnClickListener, callMethod,
        SwipeRefreshLayout.OnRefreshListener {

    private HomeAgencyActivityBinding binding;
    private SharedPreference sharedPreference;
    private String accessToken;
    private MaidHomeListAdapter maidHomeListAdapter;
    private ArrayList<SignUpModel> jobListingList;
    private SignUpModel signUpModel;
    private int filterKey;
    private String credits;
    private int totalCredits;
    private Dialog dialog;
    private int disableKey;
    private String accessFrom = "";
    private FirebaseDatabase firebaseDatabase;
    private int messageCount;
    private DatabaseReference root,unreadConversationSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.home_agency_activity);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        sharedPreference.putBoolean("session", true);
        sharedPreference.putInteger("page_selection", 0);
        disableKey = sharedPreference.getInteger("disable_key", 1);
        jobListingList = (ArrayList<SignUpModel>) getIntent().getSerializableExtra("filterJobList");
        filterKey = getIntent().getIntExtra("filterKey", 0);
        binding.toolbar.tvTitle.setText(sharedPreference.getString("location", ""));

        binding.rvAgencyListing.setLayoutManager(new LinearLayoutManager(HomeAgencyActivity.this));
        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference();

        uiChanges();

        if (disableKey == 1) {
            Toast.makeText(this, R.string.disable_quote, Toast.LENGTH_LONG).show();
            binding.sidebarContent.ivEdit.setEnabled(false);
            binding.sidebarContent.tvHome.setEnabled(false);
            //binding.sidebarContent.tvAgenciess.setEnabled(false);
            binding.sidebarContent.tvBuyCredit.setEnabled(false);
            binding.sidebarContent.tvMyFavorites.setEnabled(false);
            binding.sidebarContent.tvMyMessages.setEnabled(false);
            binding.sidebarContent.tvPastBookings.setEnabled(false);
            binding.sidebarContent.tvHighlight.setEnabled(false);
            binding.sidebarContent.tvMyMaids.setEnabled(false);
            binding.toolbar.ivFilter.setEnabled(false);
            binding.toolbar.ivNotification.setEnabled(false);
        } else {
            binding.toolbar.ivMenu.setEnabled(false);
            binding.toolbar.ivNotification.setVisibility(View.VISIBLE);
        }

        if (filterKey == 1) {
            if (jobListingList.size() > 0) {
                binding.tvNoData.setVisibility(View.GONE);
                calls();
                binding.toolbar.ivFilter.setEnabled(true);
            } else {
                binding.tvNoData.setVisibility(View.VISIBLE);
                binding.tvNoData.setText(R.string.sorry);
                binding.toolbar.ivFilter.setEnabled(true);
            }

        } else {
            if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
                binding.progress.setVisibility(View.VISIBLE);
                getJobListing(accessToken);
            }

        }
        if (ValidationUtils.isOnline(binding.drawerLayout, this)) {
            binding.progress.setVisibility(View.VISIBLE);
            getTotalCredits(accessToken);
            getUserProfile(Constants.TIMEZONE, Constants.LOCALE, accessToken);
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
        binding.sidebarContent.tvMyListings.setVisibility(View.GONE);
        binding.sidebarContent.tvAgenciess.setVisibility(View.GONE);
        binding.sidebarContent.tvAgenciess.setVisibility(View.GONE);
        binding.sidebarContent.tvAgenciess.setText("Other Agencies");
        //binding.sidebarContent.tvOtherAgencies.setVisibility(View.GONE);
        binding.toolbar.ivMenu.setEnabled(false);
        binding.tvSearchMaid.setEnabled(false);
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
        binding.sidebarContent.ivEdit.setOnClickListener(this);
        binding.sidebarContent.civProfilePic.setOnClickListener(this);
        //navigationView Clicks

        binding.sidebarContent.tvHome.setOnClickListener(this);
        binding.sidebarContent.tvMyFavorites.setOnClickListener(this);
        binding.sidebarContent.layoutMessages.setOnClickListener(this);
        binding.sidebarContent.tvMyMaids.setOnClickListener(this);
        binding.sidebarContent.tvMaidProfile.setOnClickListener(this);
        binding.sidebarContent.tvAgenciess.setOnClickListener(this);
        binding.sidebarContent.tvHighlight.setOnClickListener(this);
        binding.sidebarContent.tvPastBookings.setOnClickListener(this);
        binding.sidebarContent.tvSettings.setOnClickListener(this);
        binding.sidebarContent.tvBuyCredit.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivMenu) {

                openDrawerLayout();
                

            
} else if (v.getId() == R.id.ivFilter) {

                startActivity(new Intent(this, FilterActivity.class));
                

            
} else if (v.getId() == R.id.ivNotification) {

                startActivity(new Intent(this, NotificationActivity.class));
                

            
} else if (v.getId() == R.id.ivBack) {

                super.onBackPressed();
                

            
} else if (v.getId() == R.id.tvTitle) {

                startActivity(new Intent(this, EnterLocationActivity.class));
                


            
} else if (v.getId() == R.id.ivEdit) {

                Intent intent1 = new Intent(this, EditAgencyPofileActivity.class);
                intent1.putExtra("agencyProfile", signUpModel);
                startActivity(intent1);
                

            
} else if (v.getId() == R.id.civProfilePic) {

                Intent intent = new Intent(this, AgencyProfileActivity.class);
                intent.putExtra("agencyProfile", signUpModel);
                startActivity(intent);
                

            
} else if (v.getId() == R.id.tvHome) {

                binding.progress.setVisibility(View.VISIBLE);
                getJobListing(accessToken);
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvMyFavorites) {

                startActivity(new Intent(this, AgencyFavouritesActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.layoutMessages) {

                startActivity(new Intent(this, ChatActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvMyMaids) {

                startActivity(new Intent(this, MyMaidsActivity.class));
                sharedPreference.putInteger("popup_key", 1);
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvMaidProfile) {

                Intent intent3 = new Intent(this, OtherMaidsProfileActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3);
                sharedPreference.putInteger("popup_key", 2);
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvAgenciess) {

                startActivity(new Intent(this, AgencyActivity.class));
                binding.drawerLayout.closeDrawers();
                

            
} else if (v.getId() == R.id.tvHighlight) {

                if (signUpModel.getHighlight_profile().equals("1")) {
                    Toast.makeText(HomeAgencyActivity.this, R.string.your_agency_has_been_already_highlighted,
                            Toast.LENGTH_SHORT).show();
                } else getCreditListing(accessToken, "12", "8");
                

            
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
        if (signUpModel!=null) checkMessageCount();
    }

    public void checkMessageCount() {
        messageCount = 0;
        unreadConversationSender = root.child("Users").child("Unread_Conversation")
                .child("User_"+signUpModel.getId());
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users")
                .child("Unread_Conversation").child("User_" + signUpModel.getId());
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (dataSnapshot.getValue().toString().equals("0")) {
                        messageCount++;
                        Log.d("count", String.valueOf(messageCount));
                    }else {
                        if (messageCount>=1) messageCount--;
                        Log.d("count", String.valueOf(messageCount));
                    }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.getValue().toString().equals("0")) {
                    messageCount++;
                    Log.d("count", String.valueOf(messageCount));
                }else {
                    if (messageCount>=1) messageCount--;
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
        if (filterKey != 1) {
            binding.swipeRefreshLayout.setRefreshing(true);

            getTotalCredits(accessToken);
            getUserProfile(Constants.TIMEZONE, Constants.TIMEZONE, accessToken);

            if (filterKey == 1) {
                if (jobListingList.size() > 0) {

                    calls();
                } else {
                    binding.tvNoData.setVisibility(View.VISIBLE);
                    binding.tvNoData.setText(R.string.sorry);
                }
            } else getJobListing(accessToken);

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
            if (jobTitle.contains(newText) || country.contains(newText) || city.contains(newText)
                    || state.contains(newText))
                newList.add(jobList);
        }
        if (newList.isEmpty()) {
            binding.tvNoData.setVisibility(View.VISIBLE);
            binding.tvNoData.setText("No job listing found.");
        } else binding.tvNoData.setVisibility(View.GONE);
        MaidHomeListAdapter maidHomeListAdapter1 = new MaidHomeListAdapter(HomeAgencyActivity.this,
                newList, this, accessFrom);
        binding.rvAgencyListing.setAdapter(maidHomeListAdapter1);
    }

    @SuppressLint("SetTextI18n")
    private void openDrawerLayout() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                binding.drawerLayout, (Toolbar) binding.toolbar.getRoot(),
                R.string.open_drawer, R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(drawerToggle);
        binding.drawerLayout.openDrawer(Gravity.START);
        binding.sidebarContent.btnMessageCount.setText(""+messageCount);
    }

    private void getJobListing(String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.getJobListing(access_token);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    jobListingList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {
                        binding.toolbar.ivFilter.setEnabled(true);
                        if (jobListingList.size() > 0) {
                            binding.tvSearchMaid.setEnabled(true);
                            sharedPreference.putInteger("count", 1);
                            calls();
                            binding.tvNoData.setVisibility(View.GONE);
                        } else binding.tvNoData.setVisibility(View.VISIBLE);


                    } else {

                        Toast.makeText(HomeAgencyActivity.this, "Registration Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeAgencyActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(HomeAgencyActivity.this, new Gson().fromJson
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiList> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(HomeAgencyActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void calls() {
        binding.swipeRefreshLayout.setRefreshing(false);
        maidHomeListAdapter = new MaidHomeListAdapter(HomeAgencyActivity.this, jobListingList,
                this, accessFrom);
        binding.rvAgencyListing.setAdapter(maidHomeListAdapter);
    }

    private void makeFavourites(String access_token, String key, String jobListingId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiList> call = apiService.makeFavourite(access_token, key, jobListingId);
        call.enqueue(new Callback<RegisterApiList>() {

            @Override
            public void onResponse(Call<RegisterApiList> call, Response<RegisterApiList> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiList registerApi = response.body();
                    jobListingList = registerApi.getUserJobListingModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (sharedPreference.getInteger("favouritekey", 10) == 0) {
                            Toast.makeText(HomeAgencyActivity.this, "Removed from favourites!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeAgencyActivity.this, "Added to favourites!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        Toast.makeText(HomeAgencyActivity.this, "Registration Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(HomeAgencyActivity.this, "" + response.errorBody(),
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
                Toast.makeText(HomeAgencyActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void getUserProfile(String timezone, String locale, String access_token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.getProfileDetail(timezone, locale, access_token);
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
                        sharedPreference.putString("agency_Id", String.valueOf(signUpModel.getId()));
                        sharedPreference.putString("agency_name", String.valueOf(signUpModel.getName()));
                        if (!signUpModel.getUserImagesModel().isEmpty() &&
                                !signUpModel.getUserImagesModel().get(0).getImageModel().getSmall().isEmpty()) {
                            sharedPreference.putString("agency_pic", String.valueOf(signUpModel
                                    .getUserImagesModel().get(0).getImageModel().getBig()));
                            Glide.with(HomeAgencyActivity.this).load(signUpModel.getUserImagesModel().get(0).getImageModel()
                                    .getSmall())
                                    .fit()
                                    .error(R.drawable.avatar)
                                    .into(binding.sidebarContent.civProfilePic);
                            binding.sidebarContent.tvEmail.setText(signUpModel.getEmail());
                            checkMessageCount();
                        } else
                            binding.sidebarContent.civProfilePic.setImageResource(R.drawable.user_c);
                        binding.sidebarContent.tvName.setText(signUpModel.getName());
                        binding.sidebarContent.tvEmail.setText(signUpModel.getEmail());


                    } else {

                        Toast.makeText(HomeAgencyActivity.this, "Registration Fails",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(HomeAgencyActivity.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(HomeAgencyActivity.this, "error " + t.getMessage(),
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

                        Toast.makeText(HomeAgencyActivity.this,
                                "No Response! Refresh!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeAgencyActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(HomeAgencyActivity.this, ""
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
                Toast.makeText(HomeAgencyActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getCreditListing(final String accessToken, final String key, final String from) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditListingApi> call = apiService.getCreditListing(accessToken, key);
        call.enqueue(new Callback<CreditListingApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditListingApi> call, Response<CreditListingApi> response) {

                if (response.isSuccessful()) {

                    CreditListingApi registerApi = response.body();
                    CreditListingApi.CreditListingModel creditListingModel =
                            registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        credits = creditListingModel.getCredit();
                        callDialogForMoreThenImages(from);

                    } else {

                        Toast.makeText(HomeAgencyActivity.this,
                                "No response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeAgencyActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(HomeAgencyActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
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
            public void onFailure(retrofit2.Call<CreditListingApi> call, Throwable t) {
                Toast.makeText(HomeAgencyActivity.this, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callDialogForMoreThenImages(final String key) {
        dialog = new Dialog(HomeAgencyActivity.this);
        dialog.setContentView(R.layout.popup_pay_credits);
        dialog.setCancelable(true);
        TextView tvCurrentCredits = dialog.findViewById(R.id.tvCurrentCredits);
        TextView tvPayCredit = dialog.findViewById(R.id.tvPayCredit);
        TextView btnPay = dialog.findViewById(R.id.btnPay);
        TextView btnByCredit = dialog.findViewById(R.id.btnByCredit);
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);
        tvPayCredit.setText(credits);
        tvCurrentCredits.setText(String.valueOf(totalCredits));

        dialog.show();
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String user_id = sharedPreference.getString("agency_Id", "");
                payCredit(accessToken, credits, user_id, key);
            }
        });
        btnByCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeAgencyActivity.this.startActivity(new Intent(HomeAgencyActivity.this,
                        UpgradeMemberShipActivity.class));
                dialog.dismiss();
            }
        });
    }

    private void payCredit(final String accessToken, String credit, final String user_id,
                           final String key) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditStatusApi> call = apiService.payCredit(accessToken, credit, user_id, key);
        call.enqueue(new Callback<CreditStatusApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditStatusApi> call, Response<CreditStatusApi> response) {

                if (response.isSuccessful()) {

                    CreditStatusApi registerApi = response.body();
                    StatusModel creditSatusModel = registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        getTotalCredits(accessToken);
                        Toast.makeText(HomeAgencyActivity.this, "Your agency has been" +
                                " highlighted.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        binding.drawerLayout.closeDrawers();
                        onRefresh();

                    } else {

                        Toast.makeText(HomeAgencyActivity.this,
                                "No response", Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(HomeAgencyActivity.this,
                                    SelectionActivity.class);
                            HomeAgencyActivity.this.startActivity(signInIntent);

                        } else {
                            Toast.makeText(HomeAgencyActivity.this, new Gson().fromJson
                                            (response.errorBody().string(), ErrorResponse.class).getMessage(),
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
            public void onFailure(retrofit2.Call<CreditStatusApi> call, Throwable t) {
                Toast.makeText(HomeAgencyActivity.this, "Error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void makeFavourite(int key, int jobListId) {
        makeFavourites(accessToken, String.valueOf(key), String.valueOf(jobListId));
    }

    @Override
    public void makeUnfavourite(int key, int jobListId, int position) {

    }

    @Override
    public void removeMaid(int position) {

    }
}