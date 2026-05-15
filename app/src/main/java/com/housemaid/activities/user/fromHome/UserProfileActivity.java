package com.housemaid.activities.user.fromHome;

import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

// AQuery removed - use Glide for image loading
import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.adapter.ViewPageAdapter;
import com.housemaid.adapter.ViewPagerAdapter;
import com.housemaid.databinding.ActivityUserProfileBinding;
import com.housemaid.model.SignUpModel;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Target;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class UserProfileActivity extends BaseActivity implements View.OnClickListener {

    ActivityUserProfileBinding binding;
    SignUpModel signUpModel;
    ViewPager viewPager;
    private ArrayList<String> images;
    String fromPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_profile);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        signUpModel = (SignUpModel) getIntent().getSerializableExtra("userProfile");
        fromPage = getIntent().getStringExtra("fromPage");

        if (fromPage!=null){
            binding.toolbar.tvTitle.setText(signUpModel.getName());
        }else binding.toolbar.tvTitle.setText("My Profile");

        binding.tvName.setText(signUpModel.getName());
        binding.tvAuthorisedPerson.setText(signUpModel.getEmail());
        binding.tvPhoneNumber.setText(signUpModel.getMobile());
        binding.tvCountry.setText(signUpModel.getCountry_name());
        binding.tvState.setText(signUpModel.getState_name());
        binding.tvMaritalStatus.setText(signUpModel.getMarital_status());

        images = new ArrayList<>();
        if (!signUpModel.getUserImagesModel().isEmpty()) {
            for (int i = 0; i < signUpModel.getUserImagesModel().size(); i++) {
                images.add(signUpModel.getUserImagesModel().get(i).getImageModel().getBig());

            }
        }else images.add(getResources().getDrawable(R.drawable.user).toString());


        binding.tvBirthDate.setText(signUpModel.getDob());

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        ViewPageAdapter viewPagerAdapter = new ViewPageAdapter(this, images);
        viewPager.setAdapter(viewPagerAdapter);

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                onBackPressed();
                finish();
                break;
        }

    }
}
