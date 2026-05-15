package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.adapter.NotificationAdapter;
import com.housemaid.databinding.ActivityNotificationBinding;
import com.housemaid.model.NotificationListModel;
import com.housemaid.model.bean.NotificationKeyModel;
import com.housemaid.model.response.RegisterApiForNotification;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends BaseActivity implements View.OnClickListener {

    ActivityNotificationBinding binding;
    String accessToken;
    SharedPreference sharedPreference;
    NotificationKeyModel notificationKeyModels;
    ArrayList<NotificationListModel> notificationListModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.notification);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");

        notificationListModelArrayList = new ArrayList<>();
        binding.progress.setVisibility(View.VISIBLE);
        if (ValidationUtils.isOnline(binding.layout, this)) {
            getNotificationList(accessToken);
        }
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    private void getNotificationList(String accessToken) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApiForNotification> call = apiService.getNotificationList(accessToken);

        call.enqueue(new Callback<RegisterApiForNotification>() {

            @Override
            public void onResponse(Call<RegisterApiForNotification> call,
                                   Response<RegisterApiForNotification> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApiForNotification registerApi = response.body();
                    notificationKeyModels = registerApi.getNotificationKeyModels();
                    String message = registerApi.message;
                    if (message != null) {
                        if (notificationKeyModels != null) {

                            if (notificationKeyModels.getDailyNotifiaction().size() > 0) {
                                notificationListModelArrayList.addAll(notificationKeyModels
                                        .getDailyNotifiaction());
                            }

                            if (sharedPreference.getInteger("entry_key", 0) == 1) {
                                if (notificationKeyModels.getHireMaid().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getHireMaid());
                                }
                                if (notificationKeyModels.getInvitationRegistration().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getInvitationRegistration());
                                }
                                if (notificationKeyModels.getSuggestUser().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getSuggestUser());
                                }
                                if (notificationKeyModels.getApplyJobListing().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getApplyJobListing());
                                }
                                if (notificationKeyModels.getLiveConversation().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getLiveConversation());
                                }
                                if (notificationKeyModels.getApplyAgency().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getApplyAgency());
                                }

                            }
                            if (sharedPreference.getInteger("entry_key", 0) == 2) {
                                if (notificationKeyModels.getApplyJobListing().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getApplyJobListing());
                                }
                                if (notificationKeyModels.getSuggestMaid().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getSuggestMaid());
                                }
                                if (notificationKeyModels.getHireMaid().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getHireMaid());
                                }
                                if (notificationKeyModels.getHighlightJob().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getHighlightJob());
                                }
                                if (notificationKeyModels.getLiveConversation().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getLiveConversation());
                                }
                            }
                            if (sharedPreference.getInteger("entry_key", 0) == 3) {
                                if (notificationKeyModels.getMaidApplyAgency().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getMaidApplyAgency());
                                }
                                if (notificationKeyModels.getRequestForMaid().size() > 0) {
                                    notificationListModelArrayList.addAll(notificationKeyModels
                                            .getRequestForMaid());
                                }
                            }

                            if (notificationListModelArrayList.size() > 0) {
                                NotificationAdapter notificationListAdapter = new NotificationAdapter(
                                        NotificationActivity.this, notificationListModelArrayList, binding);
                                binding.rvNotification.setLayoutManager(new LinearLayoutManager(
                                        NotificationActivity.this));
                                binding.rvNotification.setAdapter(notificationListAdapter);
                                binding.tvNoData.setVisibility(View.GONE);
                            } else binding.tvNoData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(NotificationActivity.this, response.errorBody().toString()
                                , Toast.LENGTH_LONG).show();
                    }

                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(NotificationActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(NotificationActivity.this, response.errorBody().string()
                                    , Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApiForNotification> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(NotificationActivity.this, "error" + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}