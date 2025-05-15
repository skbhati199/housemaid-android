package com.housemaid.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.housemaid.R;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityChangePasswordOtpBinding;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordOTPActivity extends BaseActivity implements View.OnClickListener {

    private ActivityChangePasswordOtpBinding binding;
    private String keycode = "1";
    private String locale;
    private String timezone;
    private SharedPreference sharedPreference;
    private String user_id;
    private int count = 0;
    private IntentFilter intentFilter;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password_otp);
        init();
        initControls();
        otpText();

        startSmsRetriever();
    }


    public void startSmsRetriever() {
        registerReceiver();
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();
        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("SMSRE", "success");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("SMSRE", "failed");
            }
        });

    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);

        binding.toolbar.tvTitle.setText(R.string.verifiaction);
        if ((getIntent().getIntExtra("key", 0) == 2)
                || getIntent().getIntExtra("key", 0) == 3) {
            binding.toolbar.ivBack.setVisibility(View.GONE);
            binding.btnChangeMobile.setVisibility(View.GONE);
        }
        if (getIntent().getIntExtra("key", 0) == 1) {
            binding.toolbar.ivBack.setVisibility(View.GONE);
        }
        locale = "en";
        timezone = "Asia/calcutta";

        if (getIntent().getIntExtra("key", 0) == 1) {
            user_id = String.valueOf(sharedPreference.getString("user_id", "0"));
        } else if (getIntent().getIntExtra("key", 0) == 2) {
            user_id = String.valueOf(sharedPreference.getString("forget_user_id", "0"));
        } else {
            user_id = String.valueOf(sharedPreference.getString("user_id", "0"));

        }
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.btnChangeMobile.setOnClickListener(this);
        binding.btnResend.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.btnSubmit:
                openChangePassword();
                break;
            case R.id.btnChangeMobile:
                openChangeNumber();
                break;
            case R.id.btnResend:
                resendOTP();
                startSmsRetriever();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        count++;
        if (count % 2 == 1) {
            Toast.makeText(this, R.string.tap_again_to_exit, Toast.LENGTH_SHORT).show();
        } else super.onBackPressed();
    }

    private void openChangeNumber() {
        Intent changeNumberIntent = new Intent(this, ChangeMobileNumberActivity.class);
        startActivity(changeNumberIntent);
    }

    private void resendOTP() {
        binding.etOTP1.setText("");
        binding.etOTP2.setText("");
        binding.etOTP3.setText("");
        binding.etOTP4.setText("");

        binding.etOTP1.requestFocus();

        binding.progress.setIndeterminate(true);
        binding.progress.setVisibility(View.VISIBLE);
        binding.relativeLayout.setClickable(false);
        binding.btnResend.setClickable(false);
        binding.btnSubmit.setClickable(false);
        binding.btnChangeMobile.setClickable(false);
        resenOtp(user_id, keycode);
    }

    private boolean validation() {
        return ValidationUtils.otpEmpty(binding.etOTP1.getText().toString().trim(), this)
                && ValidationUtils.otpEmpty(binding.etOTP2.getText().toString().trim(), this)
                && ValidationUtils.otpEmpty(binding.etOTP3.getText().toString().trim(), this)
                && ValidationUtils.otpEmpty(binding.etOTP4.getText().toString().trim(), this);
    }

    private void openChangePassword() {
        if (validation()) {
            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {

                String otp = "" + binding.etOTP1.getText().toString().trim() + "" +
                        binding.etOTP2.getText().toString().trim() + "" +
                        binding.etOTP3.getText().toString().trim() + "" +
                        binding.etOTP4.getText().toString().trim();

                if (getIntent().getIntExtra("key", 0) == 1 ||
                        getIntent().getIntExtra("key", 0) == 2) {
                    binding.progress.setIndeterminate(true);
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.relativeLayout.setClickable(false);
                    binding.btnSubmit.setClickable(false);
                    binding.btnResend.setClickable(false);
                    binding.btnChangeMobile.setClickable(false);
                    verifyOTP(timezone, locale, user_id, keycode, otp);

                } else {
                    binding.progress.setIndeterminate(true);
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.relativeLayout.setClickable(false);
                    binding.btnSubmit.setClickable(false);
                    binding.btnResend.setClickable(false);
                    binding.btnChangeMobile.setClickable(false);
                    verifyOTP(timezone, locale, user_id, keycode, otp);
                }
            }
        }
    }

    private void verifyOTP(String timezone, String locale, String user_id, String key,
                           String otp) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.otpVerifyUser(timezone, locale, user_id, key,
                otp);

        call.enqueue(new Callback<RegisterApi>() {
            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {
                        if (getIntent().getIntExtra("key", 0) == 1 ||
                                getIntent().getIntExtra("key", 0) == 3) {
                            sharedPreference.putString("otp_verified", "1");
                            Intent completeProfileIntent = new Intent(
                                    ChangePasswordOTPActivity.this
                                    , CompleteProfileActivity.class);
                            startActivity(completeProfileIntent);
                            finish();
                        } else {
                            startActivity(new Intent(ChangePasswordOTPActivity.this,
                                    ChangePasswordActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(ChangePasswordOTPActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                        binding.progress.setVisibility(View.GONE);
                        binding.relativeLayout.setClickable(true);
                        binding.btnSubmit.setClickable(true);
                        binding.btnChangeMobile.setClickable(true);
                        binding.btnResend.setClickable(true);
                    }
                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        binding.relativeLayout.setClickable(true);
                        binding.btnSubmit.setClickable(true);
                        binding.btnChangeMobile.setClickable(true);
                        binding.btnResend.setClickable(true);

                        Toast.makeText(ChangePasswordOTPActivity.this,
                                new Gson().fromJson(response.errorBody().string(),
                                        ErrorResponse.class).getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string() +
                                "message : " + response.message());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {

                binding.progress.setVisibility(View.GONE);
                binding.relativeLayout.setClickable(true);
                binding.btnSubmit.setClickable(true);
                binding.btnChangeMobile.setClickable(true);
                binding.btnResend.setClickable(true);
                Toast.makeText(ChangePasswordOTPActivity.this, "Error: ",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resenOtp(String user_id, String key) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.resendOtp(Constants.TIMEZONE, user_id, key);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;

                    if (message != null) {
                        binding.btnResend.setClickable(true);
                        binding.btnSubmit.setClickable(true);
                        binding.btnChangeMobile.setClickable(true);
                        Toast.makeText(ChangePasswordOTPActivity.this, R.string.otp_sent_successfully,
                                Toast.LENGTH_SHORT).show();

                    } else
                        Toast.makeText(ChangePasswordOTPActivity.this,
                                "Fails!", Toast.LENGTH_LONG).show();
                    binding.progress.setVisibility(View.GONE);
                    binding.relativeLayout.setClickable(true);
                    binding.btnResend.setClickable(true);
                    binding.btnChangeMobile.setClickable(true);
                    binding.btnSubmit.setClickable(true);
                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        binding.relativeLayout.setClickable(true);
                        binding.btnResend.setClickable(true);
                        binding.btnChangeMobile.setClickable(true);
                        binding.btnSubmit.setClickable(true);

                        Toast.makeText(ChangePasswordOTPActivity.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string() +
                                "message : " + response.message());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {

                binding.progress.setVisibility(View.GONE);
                binding.relativeLayout.setClickable(true);
                binding.btnResend.setClickable(true);
                binding.btnSubmit.setClickable(true);
                binding.btnChangeMobile.setClickable(true);
                Toast.makeText(ChangePasswordOTPActivity.this, "error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    //TO CONTROL THE REQUEST FOCUS AND CURSOR.
    private void otpText() {


        binding.etOTP1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.etOTP1.getText().toString().length() == 1) //size as per your requirement
                {
                    binding.etOTP2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        binding.etOTP2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.etOTP2.getText().toString().length() == 1)     //size as per your requirement
                {
                    binding.etOTP3.requestFocus();
                }
                if (binding.etOTP2.getText().toString().length() == 0) {
                    binding.etOTP1.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        //onKeyListener use for pressing delete button
        binding.etOTP2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (binding.etOTP2.getText().toString().length() == 0) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        // this is for backspace
                        binding.etOTP1.requestFocus();
                    }
                }
                return false;
            }
        });

        binding.etOTP3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.etOTP3.getText().toString().length() == 1)     //size as per your requirement
                {
                    binding.etOTP4.requestFocus();
                }
                if (binding.etOTP3.getText().toString().length() == 0) {
                    binding.etOTP2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }

        });

        binding.etOTP3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (binding.etOTP3.getText().toString().length() == 0) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        // this is for backspace
                        binding.etOTP2.requestFocus();
                    }
                }
                return false;
            }
        });

        binding.etOTP4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.etOTP4.getText().toString().length() == 1)     //size as per your requirement
                {
                    binding.btnSubmit.requestFocus();
                }
                if (binding.etOTP4.getText().toString().length() == 0) {
                    binding.etOTP3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        binding.etOTP4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // You can identify which key pressed buy checking keyCode value
                // with KeyEvent.KEYCODE_
                if (binding.etOTP4.getText().toString().length() == 0) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        // this is for backspace
                        binding.etOTP3.requestFocus();
                    }
                }
                return false;
            }
        });

    }

    public void backtoPage(View view) {
        super.onBackPressed();
    }

    /**
     * Check if we have SMS permission
     */
    public boolean isSmsPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request runtime SMS permission
     */
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            // You may display a non-blocking explanation here, read more in the documentation:
            // https://developer.android.com/training/permissions/requesting.html
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 133);
    }

    private void registerReceiver() {
        // filter to receive SMS
        intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);

        // receiver to receive and to get otp from SMS
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                    Bundle extras = intent.getExtras();
                    Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            // Get SMS message contents
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server for SMS authenticity.
                            Log.e("OTP check", "message : " + message);

                            if (message != null) {
                                String otp = message.substring(4, 8);
                                Log.e("OTP check", "OTP : " + otp);
                                binding.etOTP1.setText(String.valueOf(otp.charAt(0)));
                                binding.etOTP2.setText(String.valueOf(otp.charAt(1)));
                                binding.etOTP3.setText(String.valueOf(otp.charAt(2)));
                                binding.etOTP4.setText(String.valueOf(otp.charAt(3)));


                                verifyOTP(timezone, locale, user_id, keycode, otp);
                            }

                            stopSmsReceiver();
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out (5 minutes)
                            Log.e("OTP check", "Timeout");
                            stopSmsReceiver();
                            break;
                    }
                }
            }
        };
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public void stopSmsReceiver() {
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}