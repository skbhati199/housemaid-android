package com.housemaid.activities;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.housemaid.R;
import com.housemaid.databinding.ActivitySelectionEntryBinding;
import com.housemaid.utils.SharedPreference;

public class SelectionEntryActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySelectionEntryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_selection_entry);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        FirebaseApp.initializeApp(this);
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSignIn.setOnClickListener(this);
        binding.btnSignUp.setOnClickListener(this);
        binding.btnWithoutSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSignIn) {

                openSignIn();
                

            
} else if (v.getId() == R.id.btnSignUp) {

                openSignUp();
                

            
} else if (v.getId() == R.id.btnWithoutSignUp) {

                Intent intent = new Intent(SelectionEntryActivity.this,
                        EnterLocationActivity.class);
                intent.putExtra("nologin", 1);
                startActivity(intent);
                
        
}
    }

    private void openSignUp() {
        binding.progress.setVisibility(View.GONE);
        Intent signUpIntent = new Intent(this, SignUpActivity.class);
        startActivity(signUpIntent);
    }

    private void openSignIn() {
        binding.progress.setVisibility(View.GONE);
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
    }
}