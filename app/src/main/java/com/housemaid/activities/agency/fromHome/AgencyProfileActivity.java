package com.housemaid.activities.agency.fromHome;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.adapter.ViewPageAdapter;
import com.housemaid.databinding.ActivityAgencyProfileBinding;
import com.housemaid.model.SignUpModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AgencyProfileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAgencyProfileBinding binding;
    private SignUpModel signUpModel;
    private ViewPager viewPager;
    private String fromPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agency_profile);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        signUpModel = (SignUpModel) getIntent().getSerializableExtra("agencyProfile");
        fromPage = getIntent().getStringExtra("fromPage");

        if (fromPage!=null){
            binding.toolbar.tvTitle.setText(signUpModel.getName());
        }else binding.toolbar.tvTitle.setText(R.string.my_profile);

        binding.tvAgency.setText(signUpModel.getCompany_name());
        binding.tvAuthorisedPerson.setText(signUpModel.getAuthorised_person());
        binding.tvTaxAdministration.setText(signUpModel.getTax_administration());
        binding.tvTaxNumber.setText(signUpModel.getTax_no());
        binding.tvCountry.setText(signUpModel.getCountry_name());
        binding.tvState.setText(signUpModel.getState_name());

        binding.tvPhoneNumber.setText(signUpModel.getMobile());
        binding.tvEmail.setText(signUpModel.getEmail());

        binding.tvCompanyPhone.setText(signUpModel.getCompany_phone());
        binding.tvAddress.setText(signUpModel.getAddress());
        ArrayList<String> images = new ArrayList<>();
        if (!signUpModel.getUserImagesModel().isEmpty()) {
            for (int i = 0; i < signUpModel.getUserImagesModel().size(); i++) {
                images.add(signUpModel.getUserImagesModel().get(i).getImageModel().getBig());

            }
        }else images.add(getResources().getDrawable(R.drawable.user).toString());


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
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                
        
}

    }
}
