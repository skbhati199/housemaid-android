package com.housemaid.activities.user.fromHome;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.databinding.ActivityMyListingDetailsBinding;
import com.housemaid.model.SignUpModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyListingDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMyListingDetailsBinding binding;
    private String address, skillName, languages, imgUrl;
    private SignUpModel jobListing;
    private int fromScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_listing_details);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_listing_details);

        initUI();

        Intent intent = getIntent();
        if (intent != null) {
            this.jobListing = (SignUpModel) intent.getExtras().getSerializable("ListingDetails");
            /*address = intent.getStringExtra("listingAddress");
            skillName = intent.getStringExtra("listingSkillName");
            imgUrl = intent.getStringExtra("listingImgUrl");*/

            languages = intent.getStringExtra("listingLanguages");

            address =   jobListing.getCity_name().concat(", ")
                        .concat(jobListing.getDistrict_name().concat(", "))
                        .concat(jobListing.getCountry_name());

            skillName = jobListing.getJob_listing_title_name();

            imgUrl = jobListing.getImage().getBig();

            fromScreen = intent.getIntExtra("fromScreen",0);
        }

        if(fromScreen == 1){
            binding.tvApproval.setVisibility(View.GONE);
            binding.view1.setVisibility(View.GONE);
        }
        //LOAD IMAGE HERE
        if (imgUrl != null) {
            /*Glide.with(itemView.getContext()).load(imgUrl)
                    .error(R.drawable.user).into(binding.profilePic);*/
            Glide.with(this).load(imgUrl).error(R.drawable.user).into(binding.profilePic);

        } else {
            binding.profilePic.setImageResource(R.drawable.user);
        }

        binding.tvskills.setText(skillName);
        binding.tvAddress.setText(address);
        binding.tvlanguages.setText(languages);
        binding.tvExpectedFee.setText(jobListing.getExpected_min_fees()+" - "+
                jobListing.getExpected_max_fees()+" "+jobListing.getFees_currency());
        binding.tvMarried.setText(jobListing.getMarital_status());
        binding.tvworkStatus.setText(jobListing.getWork_status());
        binding.tvHijab.setText(jobListing.getHijab());
        if(jobListing.getDescription()==null){
            binding.tvWorkDescription.setText("NO DESCRIPTION");
        }else {
            binding.tvWorkDescription.setText(jobListing.getDescription());
        }

        binding.tvalcoholStatus.setText(jobListing.getAlcohol());
        //jobListing.getEducationModels().get(jobListing.getEducationModels().size()).getEducation_name();
//        binding.tvTotalExperience.setText(jobListing.getTotal_experience());
        binding.tvMinMaxAge.setText("Min : "+jobListing.getMin_age() + " - Max : "+jobListing.getMax_age());

    }

    private void initUI() {
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.toolbar.tvTitle.setText(getString(R.string.listing_details));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
        }
    }
}
