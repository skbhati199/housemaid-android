package com.housemaid.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.housemaid.R;
import com.housemaid.activities.fragments.ActiveListingFragment;
import com.housemaid.activities.fragments.PassiveListingFragment;
import com.housemaid.activities.user.fromHome.AddListingUserActivity;
import com.housemaid.adapter.ViewPagerAdapter;
import com.housemaid.databinding.ActivityMyListingsBinding;

public class MyListingsActivity extends BaseActivity implements View.OnClickListener {

    ActivityMyListingsBinding binding;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_listings);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.listings);
        binding.toolbar.ivAdd.setVisibility(View.VISIBLE);
        setFragmentOnViewPager();
        setSelectedTab();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.ivAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivAdd:
                startActivity(new Intent(this, AddListingUserActivity.class));
                break;
        }
    }

    private void setFragmentOnViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addfragment(new ActiveListingFragment(), "Active");
        viewPagerAdapter.addfragment(new PassiveListingFragment(), "Passive");
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setCurrentItem(0, false);
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