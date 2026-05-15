package com.housemaid.activities.agency.fromHome;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import com.google.android.material.tabs.TabLayout;
import android.os.Bundle;
import android.view.View;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.fragments.FavouriteMaidsFragment;
import com.housemaid.activities.fragments.FavouriteUsersFragment;
import com.housemaid.adapter.ViewPagerAdapter;
import com.housemaid.databinding.ActivityAgencyFavouritesBinding;

public class AgencyFavouritesActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAgencyFavouritesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_favourites);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.my_favorites);
        setFragmentOnViewPager();
        setSelectedTab();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                
        
}

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeAgencyActivity.class));
        finish();
    }

    private void setFragmentOnViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addfragment(new FavouriteMaidsFragment(), "Favourite Maids");
        viewPagerAdapter.addfragment(new FavouriteUsersFragment(), "Favourite Users");
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    private void setSelectedTab() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        //open fragment at position 0 here
                    case 1:
                        //open fragment at position 1 here

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}