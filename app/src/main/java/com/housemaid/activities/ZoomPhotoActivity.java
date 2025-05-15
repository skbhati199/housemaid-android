package com.housemaid.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.housemaid.R;
import com.housemaid.adapter.ViewPageAdapter;
import com.housemaid.databinding.ActivityZoomPhotoBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ZoomPhotoActivity extends BaseActivity {

    ActivityZoomPhotoBinding binding;
    private ViewPager viewPager;
    private String images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zoom_photo);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();

        images = getIntent().getStringExtra("images");

        Picasso
                .get()
                .load(images)
                .error(R.drawable.avatar)
                .into(binding.imageView);

    }

    @Override
    public void initControls() {
        super.initControls();
    }
}
