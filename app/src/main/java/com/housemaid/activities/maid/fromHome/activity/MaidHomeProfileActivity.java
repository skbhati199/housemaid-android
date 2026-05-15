package com.housemaid.activities.maid.fromHome.activity;

import android.annotation.SuppressLint;
import androidx.databinding.DataBindingUtil;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.adapter.ViewPageAdapter;

import com.housemaid.databinding.ActivityMaidHomeProfileBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.utils.DateConvertUtils;
import com.housemaid.utils.SharedPreference;

import java.util.ArrayList;

public class MaidHomeProfileActivity extends BaseActivity implements View.OnClickListener {

    ActivityMaidHomeProfileBinding binding;
    SharedPreference sharedPreference;
    private SignUpModel signUpModel;
    String educationName = "";
    String petProblemName = "";
    String languageName = "";
    String workingStyleName = "";
    String workSkillsName = "";
    String workingDistrict = "";
    String workingCountry = "";
    String jobChoiceName = "";
    ArrayList<String> images;
    ViewPager viewPager;
    String fromPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maid_home_profile);
        init();
        initControls();
        setData();
    }

    @Override
    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        signUpModel = (SignUpModel) getIntent().getSerializableExtra("maidDetail");
        fromPage = getIntent().getStringExtra("fromPage");
        if (fromPage != null) {
            binding.toolbar.tvTitle.setText(signUpModel.getName());
        } else binding.toolbar.tvTitle.setText(R.string.my_profile);

        images = new ArrayList<>();
        if (!signUpModel.getUserImagesModel().isEmpty()) {
            for (int i = 0; i < signUpModel.getUserImagesModel().size(); i++) {

                images.add(signUpModel.getUserImagesModel().get(i).getImageModel().getBig());

            }
        } else images.add(getResources().getDrawable(R.drawable.user).toString());


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

    @SuppressLint("SetTextI18n")
    private void setData() {

        binding.ratingMaid.setRating(signUpModel.getRating());
        binding.ratingMaid.setIsIndicator(true);

        if (signUpModel.getUserEducationModel().size() > 0) {
            for (int position = 0; position < signUpModel.getUserEducationModel().size(); position++) {

                String name = educationName + signUpModel.getUserEducationModel().get(position).getEducationDetailModel().getName();
                educationName = name + ", ";
            }
            educationName = educationName.substring(0, educationName.length() - 2);
        }
        //Language Name from ArrayListModel
        if (signUpModel.getUserLanguageModel().size() > 0) {
            for (int position = 0; position < signUpModel.getUserLanguageModel().size(); position++) {

                String name = languageName + signUpModel.getUserLanguageModel().get(position).getLanguage_detail().getName();
                languageName = name + ", ";
            }
            languageName = languageName.substring(0, languageName.length() - 2);
        }
        //PetProblem Name from ArrayListModel
        if (signUpModel.getUserPetProblemModel().size() > 0) {

            for (int position = 0; position < signUpModel.getUserPetProblemModel().size(); position++) {

                String name = petProblemName + signUpModel.getUserPetProblemModel().get(position).getPetProblemDetailModel().getName();
                petProblemName = name + ", ";
            }
            petProblemName = petProblemName.substring(0, petProblemName.length() - 2);
        }
        //workingStyleName  from ArrayListModel
        if (signUpModel.getMaidWorkingStyleModels().size() > 0) {

            for (int position = 0; position < signUpModel.getMaidWorkingStyleModels().size(); position++) {

                String name = workingStyleName + signUpModel.getMaidWorkingStyleModels().get(position).getWorking_style_detail().getName();
                workingStyleName = name + ", ";
            }
            workingStyleName = workingStyleName.substring(0, workingStyleName.length() - 2);
        }
        //workSkillsName  from ArrayListModel
        if (signUpModel.getMaidSkillsModels().size() > 0) {

            for (int position = 0; position < signUpModel.getMaidSkillsModels().size(); position++) {

                String name = workSkillsName + signUpModel.getMaidSkillsModels().get(position).getSkill_detail().getName();
                workSkillsName = name + ", ";
            }
            workSkillsName = workSkillsName.substring(0, workSkillsName.length() - 2);
        }
        //WorkingDistrictName  from ArrayListModel
        if (signUpModel.getMaidWorkingDistrictModels().size() > 0) {

            for (int position = 0; position < signUpModel.getMaidWorkingDistrictModels().size(); position++) {

                String name = workingDistrict + signUpModel.getMaidWorkingDistrictModels().get(position).getDistrict_detail().getName();
                workingDistrict = name + ", ";
            }
            workingDistrict = workingDistrict.substring(0, workingDistrict.length() - 2);
        }
        //WorkingCountries  from ArrayListModel
        if (signUpModel.getMaidWorkingCountryModels().size() > 0) {

            for (int position = 0; position < signUpModel.getMaidWorkingCountryModels().size(); position++) {

                String name = workingCountry + signUpModel.getMaidWorkingCountryModels().get(position).getCountry_detail().getName();
                workingCountry = name + ", ";
            }
            workingCountry = workingCountry.substring(0, workingCountry.length() - 2);
        }
        //JobChoice Name from ArraListModel
        if (signUpModel.getMaidJobChoiceModels().size() > 0) {

            for (int position = 0; position < signUpModel.getMaidJobChoiceModels().size(); position++) {

                String name = jobChoiceName + signUpModel.getMaidJobChoiceModels().get(position)
                        .getJob_choice_detail().getName();
                jobChoiceName = name + ", ";
            }
            jobChoiceName = jobChoiceName.substring(0, jobChoiceName.length() - 2);
        }

        //Basic Information
        binding.tvName.setText(signUpModel.getName());
        binding.tvPhoneNumber.setText(signUpModel.getMobile());
        binding.tvEmail.setText(signUpModel.getEmail());
        binding.tvCountry.setText(signUpModel.getCountry_name());
        binding.tvState.setText(signUpModel.getState_name());
        binding.tvDob.setText(signUpModel.getDob());
        binding.tvGender.setText(signUpModel.getGender());
        binding.tvNationality.setText(signUpModel.getNationality_name());
        binding.tvKidsStatus.setText(signUpModel.getKids());
        binding.tvHijab.setText(signUpModel.getHi_job());
        binding.tvEducation.setText(educationName);
        binding.tvKnownLaunguage.setText(languageName);
        binding.tvTotalExperience.setText(signUpModel.getTotal_experience()==0 ? "No experience" :
                DateConvertUtils.daysToYear(signUpModel.getTotal_experience()));
        binding.tvWorkStatus.setText(signUpModel.getWork_status());
        binding.tvDrivingLicence.setText(signUpModel.getDriving_licence());
        binding.tvSmoking.setText(signUpModel.getSmoke());
        binding.tvAlcohol.setText(signUpModel.getAlcohol());
        binding.tvPetProblem.setText(petProblemName);

        binding.tvCountryWork.setText(signUpModel.getMaidWorkingCountryModels().isEmpty() ? "No States" :
                signUpModel.getMaidWorkingCountryModels().get(0).getCountry_detail().getName());
        binding.tvStateWork.setText(signUpModel.getMaidWorkingStatesModels().isEmpty() ? "No States" :
                signUpModel.getMaidWorkingStatesModels().get(0).getState_detail().getName());
        binding.tvDistrictWork.setText(signUpModel.getMaidWorkingDistrictModels().isEmpty() ? "No District" :
                workingDistrict);

        binding.tvCityWork.setText(signUpModel.getMaidWorkingCityModels().isEmpty() ? "No City" :
                signUpModel.getMaidWorkingCityModels().get(0).getCity_detail().getName());

        binding.tvJobChoice.setText(signUpModel.getMaidJobChoiceModels().isEmpty() ? "No Job Choice" :
                jobChoiceName);

        binding.tvWorkingType.setText(signUpModel.getMaidWorkingStyleModels().isEmpty() ? "No Style" :
                workingStyleName);

        binding.tvLiveFamily.setText(signUpModel.getCan_live_with_family());
        binding.tvTravelSituation.setText(signUpModel.getTravel_situation());
        binding.tvExpectedFee.setText(signUpModel.getExpected_fees());
        binding.tvDescriptionEducation.setText(signUpModel.getMaid_education() == null ? "No data" :
                signUpModel.getMaid_education());
        binding.tvDescriptionCertificate.setText(signUpModel.getMaid_certificate() == null ? "No data" :
                signUpModel.getMaid_certificate());
        binding.tvAboutMe.setText(signUpModel.getMaid_about_me() == null ? "No data" :
                signUpModel.getMaid_about_me());

        if (signUpModel.getMaidWorkExperiencesModel() != null &&
                !signUpModel.getMaidWorkExperiencesModel().isEmpty()) {

            if (signUpModel.getMaidWorkExperiencesModel().size() == 1) {
                binding.tvDescriptionWorkExperience1.setText(signUpModel.getMaidWorkExperiencesModel()
                        .get(0).getDetail() + ", from " +
                        signUpModel.getMaidWorkExperiencesModel().get(0).getStart_date() + " to " +
                        (signUpModel.getMaidWorkExperiencesModel().get(0).getEnd_date() == null
                                ? "still working" : signUpModel.getMaidWorkExperiencesModel().get(0).getEnd_date()));
            }
            if (signUpModel.getMaidWorkExperiencesModel().size() == 2) {
                binding.tvDescriptionWorkExperience1.setText(signUpModel.getMaidWorkExperiencesModel()
                        .get(0).getDetail() + ", from " +
                        signUpModel.getMaidWorkExperiencesModel().get(0).getStart_date() + " to " +
                        (signUpModel.getMaidWorkExperiencesModel().get(0).getEnd_date().isEmpty()
                                ? "still working" : signUpModel.getMaidWorkExperiencesModel().get(0).getEnd_date()));

                binding.tvDescriptionWorkExperience2.setText(signUpModel.getMaidWorkExperiencesModel()
                        .get(1).getDetail() + ", from " +
                        signUpModel.getMaidWorkExperiencesModel().get(1).getStart_date() + " to " +
                        (signUpModel.getMaidWorkExperiencesModel().get(1).getEnd_date().isEmpty()
                                ? "still working" : signUpModel.getMaidWorkExperiencesModel().get(1).getEnd_date()));
            }
            if (signUpModel.getMaidWorkExperiencesModel().size() > 2) {
                binding.tvDescriptionWorkExperience1.setText(signUpModel.getMaidWorkExperiencesModel()
                        .get(0).getDetail() + ", from " +
                        signUpModel.getMaidWorkExperiencesModel().get(0).getStart_date() + " to " +
                        (signUpModel.getMaidWorkExperiencesModel().get(0).getEnd_date().isEmpty()
                                ? "still working" : signUpModel.getMaidWorkExperiencesModel().get(0).getEnd_date()));

                binding.tvDescriptionWorkExperience2.setText(signUpModel.getMaidWorkExperiencesModel()
                        .get(1).getDetail() + ", from " +
                        signUpModel.getMaidWorkExperiencesModel().get(1).getStart_date() + " to " +
                        (signUpModel.getMaidWorkExperiencesModel().get(1).getEnd_date().isEmpty()
                                ? "still working" : signUpModel.getMaidWorkExperiencesModel().get(1).getEnd_date()));

                binding.tvDescriptionWorkExperience3.setText(signUpModel.getMaidWorkExperiencesModel()
                        .get(2).getDetail() + ", from "
                        + signUpModel.getMaidWorkExperiencesModel().get(2).getStart_date() + " to " +
                        (signUpModel.getMaidWorkExperiencesModel().get(2).getEnd_date().isEmpty()
                                ? "still working" : signUpModel.getMaidWorkExperiencesModel().get(2).getEnd_date()));
            }

        } else binding.tvDescriptionWorkExperience1.setText("No Experience");

        binding.tvSkills.setText(signUpModel.getMaidSkillsModels().isEmpty() ? "No Skill" :
                workSkillsName);

    }

}
