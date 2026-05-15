package com.housemaid.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.housemaid.R;
import com.housemaid.databinding.ActivitySelectionBinding;
import com.housemaid.utils.SharedPreference;

public class SelectionActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySelectionBinding binding;
    private SharedPreference sharedPreference;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_selection);
        init();
        initControls();

    }

    @Override
    public void init() {
        super.init();
        FirebaseApp.initializeApp(this);
        sharedPreference = SharedPreference.getInstance(this);
        checkPermission();

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnAgency.setOnClickListener(this);
        binding.btnJob.setOnClickListener(this);
        binding.btnMaid.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnJob) {

                openMaidPanel();
                
            
} else if (v.getId() == R.id.btnMaid) {

                openUserPanel();
                
            
} else if (v.getId() == R.id.btnAgency) {

                openAgencyPanel();
                
        
}
    }

    private void openMaidPanel() {
        Intent MaidIntent = new Intent(this, SelectionEntryActivity.class);
        sharedPreference.putInteger("entry_key", 1);
        startActivity(MaidIntent);
    }

    private void openUserPanel() {
        Intent UserIntent = new Intent(this, SelectionEntryActivity.class);
        sharedPreference.putInteger("entry_key", 2);
        startActivity(UserIntent);
    }

    private void openAgencyPanel() {
        Intent agencyIntent = new Intent(this, SelectionEntryActivity.class);
        sharedPreference.putInteger("entry_key", 3);
        startActivity(agencyIntent);
    }


    private void checkPermission() {
        if (ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.CAMERA) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.RECORD_AUDIO)) {


            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission
                                    .ACCESS_FINE_LOCATION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.RECORD_AUDIO},
                            PERMISSIONS_MULTIPLE_REQUEST);
                }
            }
        } else {
            // write your logic code if permission already granted
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean fineLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean recordAudio = grantResults[3] == PackageManager.PERMISSION_GRANTED;


                    if (fineLocation && readExternalFile && cameraPermission && recordAudio) {
                        // write your logic here
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                R.string.please_grant_permission,
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                            requestPermissions(
                                                    new String[]{Manifest.permission
                                                            .ACCESS_FINE_LOCATION,
                                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                                            Manifest.permission.CAMERA,
                                                            Manifest.permission.RECORD_AUDIO},
                                                    PERMISSIONS_MULTIPLE_REQUEST);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }
}