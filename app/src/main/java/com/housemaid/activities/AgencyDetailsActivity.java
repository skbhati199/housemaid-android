package com.housemaid.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.databinding.ActivityAgencyDetailsBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.CreditListingApi;
import com.housemaid.model.response.CreditStatusApi;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.MyBoldTextView;
import com.housemaid.utils.MyButton;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgencyDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAgencyDetailsBinding binding;
    private SharedPreference sharedPreference;
    private SignUpModel agencyDetailModel;
    private SignUpModel creditModel;
    private String accessToken;
    private PopupWindow pw;
    private String callerName;
    private String firstCallerName;
    private String senderId;
    private int totalCredits;
    private String credits;
    private String reasonToApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_details);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "");
        agencyDetailModel = (SignUpModel) getIntent().getSerializableExtra("agencyDetail");
        totalCredits = sharedPreference.getInteger("TotalCredits", 0);

        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            binding.toolbar.ivAdd.setVisibility(View.GONE);
        } else binding.toolbar.ivAdd.setVisibility(View.VISIBLE);

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            callerName = sharedPreference.getString("maid_name", "");
            senderId = sharedPreference.getString("maid_id", "");
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            callerName = sharedPreference.getString("user_name", "");
            senderId = sharedPreference.getString("user_id", "");

        }
        if (sharedPreference.getInteger("entry_key", 0) == 3) {
            callerName = sharedPreference.getString("agency_name", "");
            senderId = sharedPreference.getString("agency_Id", "");
        }

        String[] a = callerName.split(" ");
        firstCallerName = a[0];

        if (agencyDetailModel.getPhoto_email_status() == 1) {
            binding.btnViewPhoneNumber.setVisibility(View.GONE);
            binding.btnViewEmail.setVisibility(View.GONE);
            binding.btnViewAddress.setVisibility(View.GONE);
            binding.btnViewCompanyPhone.setVisibility(View.GONE);
        } else checkPaidStatus();

        setData();
    }

    private void checkPaidStatus() {
        if (agencyDetailModel.getPaidStatusModel().getMobile_status().equals("1")) {
            showAllData();
        }

    }

    public void showAllData() {
        binding.btnViewPhoneNumber.setVisibility(View.GONE);
        binding.tvPhoneNumber.setText(agencyDetailModel.getMobile());
        binding.btnViewEmail.setVisibility(View.GONE);
        binding.tvEmail.setText(agencyDetailModel.getEmail());
        binding.btnViewAddress.setVisibility(View.GONE);
        binding.tvAddress.setText(agencyDetailModel.getAddress());
        binding.btnViewCompanyPhone.setVisibility(View.GONE);
        binding.tvCompanyPhone.setText(agencyDetailModel.getCompany_phone());
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.ivAdd.setOnClickListener(this);
        binding.btnViewCompanyPhone.setOnClickListener(this);
        binding.btnViewAddress.setOnClickListener(this);
        binding.btnViewEmail.setOnClickListener(this);
        binding.btnViewPhoneNumber.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                finish();
                
            
} else if (v.getId() == R.id.btnViewPhoneNumber) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "2", "1", "");
                
            
} else if (v.getId() == R.id.btnViewEmail) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "2", "2", "");
                
            
} else if (v.getId() == R.id.btnViewAddress) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "2", "4", "");
                
            
} else if (v.getId() == R.id.btnViewCompanyPhone) {

                binding.progress.setVisibility(View.VISIBLE);
                getCreditListing(accessToken, "2", "5", "");
                

            
} else if (v.getId() == R.id.ivAdd) {

                callPopUpNoti(binding.toolbar.ivAdd);
                
            
} else if (v.getId() == R.id.tvApplyThisAgency) {

                applyAgencyDialog();
                pw.dismiss();
                

            
} else if (v.getId() == R.id.tvRequestMaid) {

                if (agencyDetailModel.getPaidStatusModel().getRequest_maid_status().equals("1")) {
                    applyToAgency(accessToken, String.valueOf(agencyDetailModel.getId()));
                    binding.progress.setVisibility(View.VISIBLE);
                    pw.dismiss();

                } else {
                    binding.progress.setVisibility(View.VISIBLE);
                    getCreditListing(accessToken, "7", "10", "");
                    pw.dismiss();
                }

                

            
} else if (v.getId() == R.id.tvAgencyMaid) {

                Intent intent = new Intent(this, AgencyMaidsActivity.class);
                intent.putExtra("agency_Id", agencyDetailModel.getId());
                startActivity(intent);
                pw.dismiss();
                

            
} else if (v.getId() == R.id.tvLiveConversation) {

                if (agencyDetailModel.getPaidStatusModel().getCall_status().equals("1")) {
                    sendNotification(accessToken, String.valueOf(agencyDetailModel.getId()),
                            firstCallerName + "_" + senderId, "0");
                    binding.progress.setVisibility(View.VISIBLE);

                } else {
                    binding.progress.setVisibility(View.VISIBLE);
                    getCreditListing(accessToken, "4", "7", "live");
                    pw.dismiss();
                }
                
        
}
    }


    private void callDialogForMoreThenImages(final String key, final String fromWhere) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_pay_credits);
        dialog.setCancelable(true);
        TextView tvCurrentCredits = dialog.findViewById(R.id.tvCurrentCredits);
        TextView tvPayCredit = dialog.findViewById(R.id.tvPayCredit);
        TextView btnPay = dialog.findViewById(R.id.btnPay);
        TextView btnByCredit = dialog.findViewById(R.id.btnByCredit);
        tvPayCredit.setText(credits);
        tvCurrentCredits.setText(String.valueOf(totalCredits));

        dialog.show();
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                binding.progress.setVisibility(View.VISIBLE);
                payCredit(accessToken, credits, String.valueOf(agencyDetailModel.getId()), key,
                        fromWhere);
            }
        });
        btnByCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AgencyDetailsActivity.this,
                        UpgradeMemberShipActivity.class));
                dialog.dismiss();
            }
        });
    }

    private void callPopUpNoti(AppCompatImageView ivAdd) {

        TextView tvRequestMaid, tvAgencyMaid, tvApplyAgency, tvLive, tvSendMessage, tvSuggestMaid;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.dialog_popup,
                null, false);

        pw = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        // display popup
        pw.showAsDropDown(ivAdd, 100, 0, Gravity.BOTTOM);
        tvRequestMaid = layout.findViewById(R.id.tvRequestMaid);

        tvAgencyMaid = layout.findViewById(R.id.tvAgencyMaid);
        tvApplyAgency = layout.findViewById(R.id.tvApplyThisAgency);
        tvLive = layout.findViewById(R.id.tvLiveConversation);
        tvSendMessage = layout.findViewById(R.id.tvSendMessage);

        tvSendMessage.setOnClickListener(this);
        tvApplyAgency.setOnClickListener(this);
        tvRequestMaid.setOnClickListener(this);
        tvAgencyMaid.setOnClickListener(this);
        tvLive.setOnClickListener(this);

        if (sharedPreference.getInteger("entry_key", 0) == 1) {

            if (agencyDetailModel.getIs_applied() == 0) {
                tvApplyAgency.setVisibility(View.VISIBLE);
            }
            tvLive.setVisibility(View.VISIBLE);
        }
        if (sharedPreference.getInteger("entry_key", 0) == 2) {
            tvRequestMaid.setVisibility(View.VISIBLE);
            tvAgencyMaid.setVisibility(View.VISIBLE);

        }
    }

    private void setData() {
        binding.toolbar.tvTitle.setText(agencyDetailModel.getName());
        binding.tvAuthorisedPerson.setText(agencyDetailModel.getAuthorised_person());
        binding.tvTaxAdministration.setText(agencyDetailModel.getTax_administration());
        binding.tvTaxNumber.setText(agencyDetailModel.getTax_no());
        binding.tvCountry.setText(agencyDetailModel.getCountry_name());
        binding.tvState.setText(agencyDetailModel.getState_name());
        binding.tvAgency.setText(agencyDetailModel.getName());

        if (agencyDetailModel.getUserImagesModel().size() > 0 &&
                !agencyDetailModel.getUserImagesModel().get(0).getImageModel().getBig().isEmpty()) {
            Glide.with(this).load(agencyDetailModel.getUserImagesModel().get(0).getImageModel().getBig())
                    .error(R.drawable.user).into(binding.ivProfilePicBig);

        } else {
            binding.ivProfilePicBig.setImageResource(R.drawable.user);
        }
    }

    private void applyToAgency(String access_token, String agency_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;

        if (sharedPreference.getInteger("entry_key", 0) == 1) {
            call = apiService.applyToAgency(access_token, agency_id, reasonToApply);
        } else call = apiService.requestForMaid(access_token, agency_id);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                pw.dismiss();
                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.getMessage();
                    if (message != null) {

                        if (sharedPreference.getInteger("entry_key", 0) == 1) {

                            Intent intent = new Intent(AgencyDetailsActivity.this,
                                    AgencyActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(AgencyDetailsActivity.this, "Your request has" +
                                    " been sent successfully", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(AgencyDetailsActivity.this, R.string.applied_successfully
                                , Toast.LENGTH_SHORT).show();
                    } else {

                        Toast.makeText(AgencyDetailsActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {

                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AgencyDetailsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {

                            Toast.makeText(AgencyDetailsActivity.this, new Gson().fromJson
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
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                pw.dismiss();
                Toast.makeText(AgencyDetailsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

                if (t instanceof ConnectException) {

                    Toast.makeText(AgencyDetailsActivity.this, "Network Error",
                            Toast.LENGTH_SHORT).show();

                } else if (t instanceof SocketTimeoutException) {

                    Toast.makeText(AgencyDetailsActivity.this, "Connection Lost",
                            Toast.LENGTH_SHORT).show();
                } else if (t instanceof UnknownHostException) {

                    Toast.makeText(AgencyDetailsActivity.this, "Server Error",
                            Toast.LENGTH_SHORT).show();
                } else if (t instanceof InternalError) {

                    Toast.makeText(AgencyDetailsActivity.this, "Server Error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void payCredit(final String accessToken, String credit, String user_id, final String key,
                           final String fromWhere) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditStatusApi> call = apiService.payCredit(accessToken, credit, user_id, key);
        call.enqueue(new Callback<CreditStatusApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditStatusApi> call, Response<CreditStatusApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    CreditStatusApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        getTotalCredits(accessToken);

                        if (key.equals("1")) {
                            agencyDetailModel.getPaidStatusModel().setMobile_status("1");
                            showAllData();
                        }
                        if (key.equals("2")) {
                            agencyDetailModel.getPaidStatusModel().setEmail_status("1");
                            showAllData();
                        }
                        if (key.equals("4")) {
                            agencyDetailModel.getPaidStatusModel().setAddress_status("1");
                            showAllData();
                        }
                        if (key.equals("5")) {
                            agencyDetailModel.getPaidStatusModel().setCompany_phone_status("1");
                            showAllData();
                        }
                        if (key.equals("7")) {
                            binding.progress.setVisibility(View.VISIBLE);
                            applyToAgency(accessToken, String.valueOf(agencyDetailModel.getId()));

                        }
                        if (key.equals("10")) {
                            binding.progress.setVisibility(View.VISIBLE);
                            applyToAgency(accessToken, String.valueOf(agencyDetailModel.getId()));

                        }
                        if (fromWhere.equals("live")) {
                            sendNotification(accessToken, String.valueOf(agencyDetailModel.getId()),
                                    firstCallerName + "_" + senderId, "0");
                            binding.progress.setVisibility(View.VISIBLE);
                            pw.dismiss();
                        }

                    } else {

                        Toast.makeText(AgencyDetailsActivity.this,
                                "No Response! Refresh!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AgencyDetailsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AgencyDetailsActivity.this, new Gson().fromJson
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
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(AgencyDetailsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyAgencyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_suggest_maid);

        final EditText etReason = dialog.findViewById(R.id.etReason);
        final MyBoldTextView tvHeader = dialog.findViewById(R.id.tvHeader);
        final ProgressBar progressBar = dialog.findViewById(R.id.progress);
        final MyButton btnSuggest = dialog.findViewById(R.id.btnSuggest);

        tvHeader.setText("Please write why you want to apply to this agency");
        btnSuggest.setText("Apply To This Agency");

        dialog.findViewById(R.id.btnSuggest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reasonToApply = etReason.getText().toString();
                if (!reasonToApply.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    binding.progress.setVisibility(View.VISIBLE);
                    getCreditListing(accessToken, "8", "7", "");
                    dialog.dismiss();
                } else{
                    Toast.makeText(AgencyDetailsActivity.this, "" +
                            getString(R.string.please_write_why_you_want_to_suggest), Toast.LENGTH_SHORT).show();
                }


            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }


    private void getCreditListing(final String accessToken, final String key, final String from,
                                  final String fromWhere) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<CreditListingApi> call = apiService.getCreditListing(accessToken, key);
        call.enqueue(new Callback<CreditListingApi>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CreditListingApi> call, Response<CreditListingApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    CreditListingApi registerApi = response.body();
                    CreditListingApi.CreditListingModel creditListingModel =
                            registerApi.getStatusModel();
                    String message = registerApi.message;
                    if (message != null) {

                        credits = creditListingModel.getCredit();

                        callDialogForMoreThenImages(from, fromWhere);

                    } else {

                        Toast.makeText(AgencyDetailsActivity.this,
                                "No response", Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AgencyDetailsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AgencyDetailsActivity.this, new Gson().fromJson
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
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(AgencyDetailsActivity.this, "Error " + t.getMessage(),
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
                    creditModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        sharedPreference.putInteger("TotalCredits", creditModel.getTotal_credit());
                        totalCredits = creditModel.getTotal_credit();

                    } else {

                        Toast.makeText(AgencyDetailsActivity.this,
                                "No Response! Refresh!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AgencyDetailsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);

                        } else {
                            Toast.makeText(AgencyDetailsActivity.this, ""
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
                Toast.makeText(AgencyDetailsActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void sendNotification(String access_token, String user_id, String call_text,
                                  final String key) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        call = apiService.videoCall(access_token, user_id, call_text, key);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {


                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    SignUpModel signUpModel = response.body().getSignUpModel();
                    String message = response.body().getMessage();
                    if (message != null) {
                        if (key.equals("0")) {
                            agencyDetailModel.getPaidStatusModel().setCall_status("1");
                        }
                        if (signUpModel.getCall_status().equals("0")) {
                            Toast.makeText(AgencyDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else if (signUpModel.getCall_status().equals("1")) {
                            Intent intent1 = new Intent(AgencyDetailsActivity.this,
                                    LiveConversationActivity.class);
                            intent1.putExtra("caller_id", agencyDetailModel.getId());
                            intent1.putExtra("to_caller_name", agencyDetailModel.getName());
                            intent1.putExtra("calling_key", 1);
                            intent1.putExtra("profileToCaller",
                                    agencyDetailModel.getUserImagesModel().get(0).getImageModel().getSmall());
                            intent1.putExtra("channel_name", firstCallerName + "_" +
                                    senderId);
                            startActivity(intent1);
                            pw.dismiss();

                        } else
                            Toast.makeText(AgencyDetailsActivity.this, "Your Call request has been rejected",
                                    Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(AgencyDetailsActivity.this, "Fails"
                                , Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AgencyDetailsActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(AgencyDetailsActivity.this, response.errorBody().string()
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
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(AgencyDetailsActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

}