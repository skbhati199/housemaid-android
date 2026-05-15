package com.housemaid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.housemaid.R;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
import com.housemaid.activities.maid.fromHome.activity.HomeForMaidActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.utils.AppSignatureHelper;
import com.housemaid.utils.SharedPreference;

public class SplashActivity extends AppCompatActivity {

    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Firebase Crashlytics auto-initializes — no Fabric.with() needed
        setContentView(R.layout.activity_splash);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreference = SharedPreference.getInstance(this);


        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
        Log.e("key hash", appSignatureHelper.getAppSignatures().get(0));
        final boolean isSessioMantained = sharedPreference.getBoolean("session", false);


        // Using handler with postDelayed called runnable run method
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isSessioMantained) {

                if (sharedPreference.getInteger("entry_key", 0) == 1) {
                    startActivity(new Intent(SplashActivity.this,
                            HomeForMaidActivity.class));
                }
                if (sharedPreference.getInteger("entry_key", 0) == 2) {
                    startActivity(new Intent(SplashActivity.this,
                            HomeUserActivity.class));
                }
                if (sharedPreference.getInteger("entry_key", 0) == 3) {
                    startActivity(new Intent(SplashActivity.this,
                            HomeAgencyActivity.class));
                }
            } else
                startActivity(new Intent(SplashActivity.this, SelectionActivity.class));
            // close this activity
            finish();
        }, 3 * 1000);
    }
}