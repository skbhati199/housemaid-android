package com.housemaid.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.housemaid.R;
import com.housemaid.adapter.SlidingImageAdapter;
import com.housemaid.databinding.ActivityFullScreenImageSliderBinding;

import java.util.ArrayList;

public class FullScreenImageSlider extends AppCompatActivity {

    ActivityFullScreenImageSliderBinding binding;
    int position;
    ArrayList<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this,R.layout.activity_full_screen_image_slider);

        Intent intent = getIntent();
        images = intent.getStringArrayListExtra("images");
        position = intent.getIntExtra("imagePosition",0);

        binding.imageSlidingVp.setOffscreenPageLimit(3);
        binding.imageSlidingVp.setAdapter(new SlidingImageAdapter(this,images));
        binding.imageSlidingVp.setCurrentItem(position);
    }
}
