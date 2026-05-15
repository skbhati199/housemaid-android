package com.housemaid.activities.user.fromHome;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.MyListingsActivity;
import com.housemaid.activities.UpgradeMemberShipActivity;
import com.housemaid.databinding.ActivityListingSuccessfulBinding;

public class ListingSuccessfulActivity extends BaseActivity implements View.OnClickListener {

    ActivityListingSuccessfulBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_listing_successful);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void initControls() {
        binding.toolbar.tvTitle.setText("Added Successfully");
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.btnBuyCredit.setOnClickListener(this);
        binding.btnReturnToMain.setOnClickListener(this);
        binding.btnNewListing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                
            
} else if (v.getId() == R.id.btnBuyCredit) {

                startActivity(new Intent(this, UpgradeMemberShipActivity.class));
                finish();
                

                
} else if (v.getId() == R.id.btnReturnToMain) {

                startActivity(new Intent(this, MyListingsActivity.class));
                finish();
                

                
} else if (v.getId() == R.id.btnNewListing) {

                startActivity(new Intent(this, AddListingFirstPageActivity.class));
                
        
}

    }
}
