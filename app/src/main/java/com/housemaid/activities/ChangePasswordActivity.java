package com.housemaid.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityChangePasswordBinding;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private ActivityChangePasswordBinding binding;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.reset_password);
        binding.toolbar.ivBack.setVisibility(View.GONE);
        SharedPreference sharedPreference = SharedPreference.getInstance(this);
        user_id = String.valueOf(sharedPreference.getInteger("forget_user_id", 0));
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {

                openPopup();
                
        
}
    }

    private void openPopup() {
        if (validation()) {
            if (ValidationUtils.isOnline(binding.relativeLayout, this)) {

                binding.progress.setIndeterminate(true);
                binding.progress.setVisibility(View.VISIBLE);
                binding.relativeLayout.setClickable(false);
                binding.btnSubmit.setClickable(false);

                //for reset password key=2
                String key = "2";
                changePassword(
                        key, binding.etNewPassword.getText().toString(),
                        binding.etConfirmNewPassword.getText().toString(), user_id);
            }
        }
    }

    private void changePassword(String key, String old_password, String new_password, String user_id) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.changePassword(Constants.TIMEZONE, Constants.LOCALE, key,
                old_password, new_password, user_id);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;

                    if (message != null) {
                        showPopUp();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Fails"
                                , Toast.LENGTH_LONG).show();
                        binding.progress.setVisibility(View.GONE);
                        binding.relativeLayout.setClickable(true);
                        binding.btnSubmit.setClickable(true);
                    }
                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        binding.relativeLayout.setClickable(true);
                        binding.btnSubmit.setClickable(true);

                        Toast.makeText(ChangePasswordActivity.this, new Gson().fromJson
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
                binding.relativeLayout.setClickable(true);
                binding.btnSubmit.setClickable(true);
                Toast.makeText(ChangePasswordActivity.this, "Error: ", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public void showPopUp() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_password_changed);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    startActivity(new Intent(ChangePasswordActivity.this,
                            SignInActivity.class));
                    finishAffinity();
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 2000);
    }

    public boolean validation() {
        if (ValidationUtils.passwordEmpty(binding.etNewPassword.getText().toString().trim(), this)
                && ValidationUtils.passwordMatch(binding.etNewPassword.getText().toString().trim(),
                binding.etConfirmNewPassword.getText().toString().trim(), this)) {
            return true;
        }
        return false;
    }
}