package com.housemaid.activities.maid.beforeHome;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.dataList.EducationListActivity;
import com.housemaid.activities.dataList.LanguageListActivity;
import com.housemaid.activities.dataList.PetProblemListActivity;
import com.housemaid.activities.dataList.WorkingchoicesListActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityCompleteProfileMaid2Binding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteProfileMaid2 extends BaseActivity implements View.OnClickListener {

    ActivityCompleteProfileMaid2Binding binding;
    String workStatus[];
    String drivingLicence[];
    String smoke[];
    String alcohol[];
    String drivingStatus;
    String work;
    String smokeStatus;
    String alcoholStatus;
    ArrayList <String> educationNameList= new ArrayList<>();
    ArrayList <String> educationIdList= new ArrayList<>();
    ArrayList <String> languageNameList= new ArrayList<>();
    ArrayList <String> languageIdList= new ArrayList<>();
    ArrayList <String> petProblemList= new ArrayList<>();
    ArrayList <String> petProblemIdList= new ArrayList<>();
    String name;
    String name1;
    String name2;
    SharedPreference sharedPreference;
    String accessToken;
    String stepStatus = "3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        binding = DataBindingUtil.setContentView ( this, R.layout.activity_complete_profile_maid2 );
        init ();
        initControls ();
    }

    @Override
    public void init() {
        super.init ();
        binding.toolbar.tvTitle.setText ( getString(R.string.complete_your_profile) );
        sharedPreference = SharedPreference.getInstance ( this );

        accessToken = sharedPreference.getString ( "signUp_token", "0" );

        workStatus = getResources ().getStringArray ( R.array.workStatus );
        showWorkStatus ();
        drivingLicence = getResources ().getStringArray ( R.array.drivingLicence );
        showLicenceStatus ();
        smoke = getResources ().getStringArray ( R.array.smoke );
        showSmokeStatus ();
        alcohol = getResources ().getStringArray ( R.array.alcohol );
        showAlcoholStatus ();
    }

    @Override
    public void initControls() {
        super.initControls ();
        binding.btnNext.setOnClickListener ( this );
        binding.rlEducation.setOnClickListener ( this );
        binding.rlLanguage.setOnClickListener ( this );
        binding.rlPetProblem.setOnClickListener ( this );
        binding.rlWorkStatus.setOnClickListener ( this );
        binding.rlSmoke.setOnClickListener ( this );
        binding.rlDrivingLicence.setOnClickListener(this);
        binding.rlAlcohol.setOnClickListener ( this );
        binding.toolbar.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId ()) {

            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.btnNext:
                if (binding.tvEducation.getText ().toString ().trim ().isEmpty ()) {
                    Toast.makeText ( this, getString(R.string.please_select_education),
                            Toast.LENGTH_SHORT ).show ();
                } else if (binding.tvLanguage.getText ().toString ().trim ().isEmpty ()) {
                    Toast.makeText ( this, getString(R.string.please_select_your_language),
                            Toast.LENGTH_SHORT ).show ();
                }else if (work.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_your_work_status),
                            Toast.LENGTH_SHORT ).show ();
                }else if (drivingStatus.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_your_driving_licence),
                            Toast.LENGTH_SHORT ).show ();
                }else if (smokeStatus.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_your_smoke_status),
                            Toast.LENGTH_SHORT ).show ();
                }else if (alcoholStatus.equals ( "" )){
                    Toast.makeText ( this, getString(R.string.please_select_your_alcohol_status),
                            Toast.LENGTH_SHORT ).show ();
                }else if (binding.tvPetProblem.getText ().toString ().trim ().isEmpty ()) {
                    Toast.makeText ( this, getString(R.string.please_select_your_pet_problem),
                            Toast.LENGTH_SHORT ).show ();
                }else {
                    openNextActivity ();
                }
                break;

            case R.id.rlEducation:
                Intent educationIntent = new Intent ( this, EducationListActivity.class );
                educationIntent.putExtra("educationIdList", educationIdList);
                educationIntent.putExtra("educationNameList", educationNameList);
                startActivityForResult (educationIntent , 524 );
                break;

            case R.id.rlLanguage:
                Intent languageIntent = new Intent ( this, LanguageListActivity.class );
                languageIntent.putExtra("languageIdList", languageIdList);
                languageIntent.putExtra("languageNameList", languageNameList);
                startActivityForResult (languageIntent , 526 );
                break;

            case R.id.rlPetProblem:
                Intent petIntent = new Intent ( this, PetProblemListActivity.class );
                petIntent.putExtra("petProblemList", petProblemList);
                petIntent.putExtra("petProblemIdList", petProblemIdList);
                startActivityForResult (petIntent , 528 );
                break;
            case R.id.rlWorkStatus:
                binding.spinnerWorkStatus.performClick ();
                break;

            case R.id.rlAlcohol:
                binding.spinnerAlcohol.performClick ();
                break;

            case R.id.rlSmoke:
                binding.spinnerSmoke.performClick ();
                break;

            case R.id.rlDrivingLicence:
                binding.spinnerDrivingLicence.performClick();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        if (requestCode == 524 && resultCode == Activity.RESULT_OK) {
            educationNameList = data.getStringArrayListExtra ( "nameList" );
            educationIdList = data.getStringArrayListExtra ( "idList" );
            for (int i = 0; i < educationNameList.size (); i++) {
                if (i == 0) {
                    name = educationNameList.get ( i );
                } else {
                    name = name + ", " + educationNameList.get ( i );
                }
            }
            if (educationNameList.size()>0) binding.tvEducation.setText ( name );
            else  binding.tvEducation.setText ( "" );
        }
        if (requestCode == 526 && resultCode == Activity.RESULT_OK) {
            languageNameList = data.getStringArrayListExtra ( "nameList" );
            languageIdList = data.getStringArrayListExtra ( "idList" );
            for (int i = 0; i < languageNameList.size (); i++) {
                if (i == 0) {
                    name1 = languageNameList.get ( i );
                } else {
                    name1 = name1 + ", " + languageNameList.get ( i );
                }
            }
            if (languageNameList.size()>0) binding.tvLanguage.setText ( name1 );
            else  binding.tvLanguage.setText ( "" );
        }
        if (requestCode == 528 && resultCode == Activity.RESULT_OK) {
            petProblemList = data.getStringArrayListExtra ( "nameList" );
            petProblemIdList = data.getStringArrayListExtra ( "idList" );
            for (int i = 0; i < petProblemList.size (); i++) {
                if (i == 0) {
                    name2 = petProblemList.get ( i );
                } else {
                    name2 = name2 + ", " + petProblemList.get ( i );
                }
            }
            if (petProblemList.size()>0) binding.tvPetProblem.setText ( name2 );
            else  binding.tvPetProblem.setText ( "" );
        }
    }

    private void showAlcoholStatus() {
        ArrayAdapter <String> adapter = new ArrayAdapter <String> ( this,
                R.layout.single_spinner_maid_layout, alcohol );
        binding.spinnerAlcohol.setAdapter ( adapter );
        binding.spinnerAlcohol.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                alcoholStatus = binding.spinnerAlcohol.getSelectedItem ().toString ();
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        } );
    }

    private void showSmokeStatus() {
        ArrayAdapter <String> adapter = new ArrayAdapter <String> ( this,
                R.layout.single_spinner_maid_layout, smoke );
        binding.spinnerSmoke.setAdapter ( adapter );
        binding.spinnerSmoke.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                smokeStatus = binding.spinnerSmoke.getSelectedItem ().toString ();
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        } );

    }

    private void showLicenceStatus() {
        ArrayAdapter <String> adapter = new ArrayAdapter <String> ( this,
                R.layout.single_spinner_maid_layout, drivingLicence );
        binding.spinnerDrivingLicence.setAdapter ( adapter );
        binding.spinnerDrivingLicence.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
               drivingStatus = binding.spinnerDrivingLicence.getSelectedItem ().toString ();
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        } );

    }

    private void showWorkStatus() {
        ArrayAdapter <String> adapter = new ArrayAdapter <String> ( this,
                R.layout.single_spinner_maid_layout, workStatus );
        binding.spinnerWorkStatus.setAdapter ( adapter );
        binding.spinnerWorkStatus.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                work = binding.spinnerWorkStatus.getSelectedItem ().toString ();
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        } );
    }

    private void openNextActivity() {
        binding.progress.setVisibility ( View.VISIBLE );
        binding.btnNext.setClickable ( false );
        binding.rlEducation.setClickable ( false );
        binding.rlLanguage.setClickable ( false );
        binding.rlPetProblem.setClickable ( false );
        sendDataOfMaid ( Constants.TIMEZONE, Constants.LOCALE, accessToken, Constants.userType ( this ),
                educationIdList.toString (), languageIdList.toString (), work, drivingStatus, stepStatus,
                smokeStatus, alcoholStatus, petProblemIdList.toString () );

    }

    private void sendDataOfMaid(String timezone, String locale, String access_token,
                                String user_type,
                                String education_ids,
                                String languages_ids,
                                String work_status,
                                String driving_licence,
                                String step_for_maid_profile,
                                String smoke,
                                String alcohol,
                                String pet_problem_ids) {

        ApiInterface apiService = ApiClient.getClient ().create ( ApiInterface.class );
        Call <RegisterApi> call = apiService.completeProfileMaid2 ( timezone, locale, access_token,
                user_type, education_ids, languages_ids, work_status, driving_licence, step_for_maid_profile,
                smoke, alcohol, pet_problem_ids );
        call.enqueue ( new Callback <RegisterApi> () {

            @Override
            public void onResponse(Call <RegisterApi> call, Response <RegisterApi> response) {
                binding.progress.setVisibility ( View.GONE );
                binding.btnNext.setClickable ( true );
                binding.rlEducation.setClickable ( true );
                binding.rlLanguage.setClickable ( true );
                binding.rlPetProblem.setClickable ( true );
                if (response.isSuccessful ()) {

                    RegisterApi registerApi = response.body ();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body ();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString ( "complete_profile", signUpModel.getComplete_profile () );
                        sharedPreference.putInteger ( "step_for_maid_profile", signUpModel.getStep_for_maid_profile () );
                        Intent intent = new Intent ( CompleteProfileMaid2.this, CompleteProfileMaid3.class );
                        startActivity ( intent );
                    } else

                        Toast.makeText ( CompleteProfileMaid2.this, "Fails",
                                Toast.LENGTH_LONG ).show ();

                } else {

                    try {

                        Toast.makeText(CompleteProfileMaid2.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());  } catch (IOException e) {
                        e.printStackTrace ();
                    }
                }
            }

            @Override
            public void onFailure(Call <RegisterApi> call, Throwable t) {
                binding.progress.setVisibility ( View.GONE );
                binding.btnNext.setClickable ( true );
                binding.rlEducation.setClickable ( true );
                binding.rlLanguage.setClickable ( true );
                binding.rlPetProblem.setClickable ( true );
                Toast.makeText ( CompleteProfileMaid2.this, "Error " + t.getMessage (),
                        Toast.LENGTH_SHORT ).show ();

            }
        } );
    }
}