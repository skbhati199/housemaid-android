package com.housemaid.activities.agency.fromHome;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.MyListingsActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.dataList.EducationListActivity;
import com.housemaid.activities.dataList.LanguageListActivity;
import com.housemaid.activities.dataList.NationalityListActivity;
import com.housemaid.activities.dataList.PetProblemListActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.AddListNextStepActivityBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddListNextStepActivity extends BaseActivity implements View.OnClickListener {

    private AddListNextStepActivityBinding binding;
    private SharedPreference sharedPreference;
    private String maritalStatus[];
    private String kidStatus[];
    private String hijab[];
    private String workStatus[];
    private String experience[];
    private String mariStatus;
    private String kidsStatus;
    private String hijabStatus;
    private int nationalityID;
    private String drivingLicence[];
    private String smoke[];
    private String alcohol[];
    private String drivingStatus;
    private String work = "";
    private String workExperience = "";
    private String smokeStatus;
    private String alcoholStatus;
    private ArrayList<String> educationNameList = new ArrayList<>();
    private ArrayList<String> educationIdList = new ArrayList<>();
    private ArrayList<String> languageNameList = new ArrayList<>();
    private ArrayList<String> languageIdList = new ArrayList<>();
    private ArrayList<String> petProblemList = new ArrayList<>();
    private ArrayList<String> petProblemIdList = new ArrayList<>();
    private String name;
    private String name1;
    private String name2;
    private Map<String, RequestBody> userDetailMap;
    private String educationNameListid;
    private String languageNameListid;
    private String petProblemListid;
    private String accessToken;
    private String imagePath;
    private MultipartBody.Part profileImage;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.add_list_next_step_activity);
        init();
        initControls();
    }

    public void init() {
        super.init();
        sharedPreference = SharedPreference.getInstance(this);
        if (Objects.equals(sharedPreference.getString("add", "0"), "maid")) {
            binding.toolbar.tvTitle.setText(R.string.add_maids );
            binding.layoutAgeRange.setVisibility(View.GONE);
        }
        if (Objects.equals(sharedPreference.getString("add", "0"), "listing")) {
            binding.toolbar.tvTitle.setText(R.string.add_listing );
            binding.layoutAgeRange.setVisibility(View.VISIBLE);
        }


        if (Objects.equals(sharedPreference.getString("start_key", "0"), "1")) {
            accessToken = sharedPreference.getString("signUp_token", "0");
        }
        if (Objects.equals(sharedPreference.getString("start_key", "0"), "2")) {
            accessToken = sharedPreference.getString("signIn_token", "0");
        }
        if (Objects.equals(sharedPreference.getString("start_key", "0"), "3")) {
            accessToken = sharedPreference.getString("signUp_token", "0");
        }

        maritalStatus = getResources().getStringArray(R.array.maritalStatus);
        showMaritalStatus();
        kidStatus = getResources().getStringArray(R.array.kidStatus);
        showkidStatus();
        hijab = getResources().getStringArray(R.array.hijab);
        showHijabStatus();
        drivingLicence = getResources().getStringArray(R.array.drivingLicence);
        showLicenceStatus();
        smoke = getResources().getStringArray(R.array.smoke);
        showSmokeStatus();
        alcohol = getResources().getStringArray(R.array.alcohol);
        showAlcoholStatus();
        workStatus = getResources().getStringArray(R.array.workStatus);
        showWorkStatus();
        experience = getResources().getStringArray(R.array.experience);
        showExperience();
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.toolbar.ivBack.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.rlNationality.setOnClickListener(this);
        binding.rlMaritalStatus.setOnClickListener(this);
        binding.rlKidsStatus.setOnClickListener(this);
        binding.rlHijob.setOnClickListener(this);
        binding.rlAlcohol.setOnClickListener(this);
        binding.rlEducation.setOnClickListener(this);
        binding.rlLanguage.setOnClickListener(this);
        binding.rlDrivingLicence.setOnClickListener(this);
        binding.rlSmoke.setOnClickListener(this);
        binding.rlExperience.setOnClickListener(this);
        binding.rlWorkStatus.setOnClickListener(this);
        binding.rlPetProblem.setOnClickListener(this);

        binding.rangeSeekbar1.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                binding.tvMinAge.setText(String.valueOf(minValue) + "yrs");
                binding.tvMaxAge.setText(String.valueOf(maxValue) + "yrs");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                finish();
                

            
} else if (v.getId() == R.id.rlNationality) {

                startActivityForResult(new Intent(this, NationalityListActivity.class),
                        522);
                

            
} else if (v.getId() == R.id.rlMaritalStatus) {

                binding.spinnerMaritalStatus.performClick();
                

            
} else if (v.getId() == R.id.rlKidsStatus) {

                binding.spinnerKidStatus.performClick();
                

            
} else if (v.getId() == R.id.rlHijob) {

                binding.spinnerHijab.performClick();
                

            
} else if (v.getId() == R.id.rlDrivingLicence) {

                binding.spinnerDrivingLicence.performClick();
                

            
} else if (v.getId() == R.id.rlExperience) {

                binding.spinnerExperience.performClick();
                

            
} else if (v.getId() == R.id.rlEducation) {

                Intent educationIntent = new Intent ( this, EducationListActivity.class );
                educationIntent.putExtra("educationIdList", educationIdList);
                educationIntent.putExtra("educationNameList", educationNameList);
                startActivityForResult (educationIntent , 524 );
                

            
} else if (v.getId() == R.id.rlLanguage) {

                Intent languageIntent = new Intent ( this, LanguageListActivity.class );
                languageIntent.putExtra("languageIdList", languageIdList);
                languageIntent.putExtra("languageNameList", languageNameList);
                startActivityForResult (languageIntent , 526 );
                

            
} else if (v.getId() == R.id.rlPetProblem) {

                Intent petIntent = new Intent ( this, PetProblemListActivity.class );
                petIntent.putExtra("petProblemList", petProblemList);
                petIntent.putExtra("petProblemIdList", petProblemIdList);
                startActivityForResult (petIntent , 528 );
                

            
} else if (v.getId() == R.id.rlAlcohol) {

                binding.spinnerAlcohol.performClick();
                

            
} else if (v.getId() == R.id.rlSmoke) {

                binding.spinnerSmoke.performClick();
                

            
} else if (v.getId() == R.id.rlWorkStatus) {

                binding.spinnerWorkStatus.performClick();
                

            
} else if (v.getId() == R.id.btnSubmit) {


                if (binding.tvNationality.getText ().toString ().trim ().isEmpty ()) {
                    Toast.makeText(this, R.string.please_enter_nationality, Toast.LENGTH_SHORT).show();
                }else if (binding.tvEducation.getText ().toString ().trim ().isEmpty ()){
                    Toast.makeText ( this, getString(R.string.please_enter_education), Toast.LENGTH_SHORT ).show ();
                }else if (binding.tvLanguage.getText ().toString ().trim ().isEmpty ()){
                    Toast.makeText ( this, getString(R.string.please_enter_language), Toast.LENGTH_SHORT ).show ();
                }else if (binding.tvPetProblem.getText ().toString ().trim ().isEmpty ()) {
                    Toast.makeText(this, R.string.please_enter_pet_problem, Toast.LENGTH_SHORT).show();
                }else {

                if (Objects.equals(sharedPreference.getString("add", "0"), "maid")) {
                    startActivity(new Intent(this, AddListingActivity.class));
                }
                if (Objects.equals(sharedPreference.getString("add", "0"), "listing")) {
                    binding.progress.setVisibility(View.VISIBLE);
                    setValueInHashMap();
                }
        }
                
        
}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 522 && resultCode == Activity.RESULT_OK) {
            String nationality = data.getStringExtra("nationality");
            binding.tvNationality.setText(nationality);
            nationalityID = data.getIntExtra("nationality_id", 0);
        }
        if (requestCode == 524 && resultCode == Activity.RESULT_OK) {
            educationNameList = data.getStringArrayListExtra("nameList");
            educationIdList = data.getStringArrayListExtra("idList");
            educationNameListid = educationIdList.toString();
            for (int i = 0; i < educationNameList.size(); i++) {
                if (i == 0) {
                    name = educationNameList.get(i);
                } else {
                    name = name + ", " + educationNameList.get(i);
                }
            }
            if (educationNameList.size()>0) binding.tvEducation.setText ( name );
            else  binding.tvEducation.setText ( "" );
        }
        if (requestCode == 526 && resultCode == Activity.RESULT_OK) {
            languageNameList = data.getStringArrayListExtra("nameList");
            languageIdList = data.getStringArrayListExtra("idList");
            languageNameListid = languageIdList.toString();
            for (int i = 0; i < languageNameList.size(); i++) {
                if (i == 0) {
                    name1  = languageNameList.get(i);
                } else {
                    name1  = name1  + ", " + languageNameList.get(i);
                }
            }
            if (languageNameList.size()>0) binding.tvLanguage.setText ( name1 );
            else  binding.tvLanguage.setText ( "" );
        }
        if (requestCode == 528 && resultCode == Activity.RESULT_OK) {
            petProblemList = data.getStringArrayListExtra("nameList");
            petProblemIdList = data.getStringArrayListExtra("idList");
            petProblemListid = petProblemIdList.toString();
            for (int i = 0; i < petProblemList.size(); i++) {
                if (i == 0) {
                    name2 = petProblemList.get(i);
                } else {
                    name2 = name2 + ", " + petProblemList.get(i);
                }
            }
            if (petProblemList.size()>0) binding.tvPetProblem.setText ( name2 );
            else  binding.tvPetProblem.setText ( "" );
        }

    }

    private void showMaritalStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, maritalStatus);
        binding.spinnerMaritalStatus.setAdapter(adapter);
        binding.spinnerMaritalStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               mariStatus = binding.spinnerMaritalStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showkidStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, kidStatus);
        binding.spinnerKidStatus.setAdapter(adapter);
        binding.spinnerKidStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kidsStatus = binding.spinnerKidStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showHijabStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, hijab);
        binding.spinnerHijab.setAdapter(adapter);
        binding.spinnerHijab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 hijabStatus = binding.spinnerHijab.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showWorkStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, workStatus);
        binding.spinnerWorkStatus.setAdapter(adapter);
        binding.spinnerWorkStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                work = binding.spinnerWorkStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showAlcoholStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, alcohol);
        binding.spinnerAlcohol.setAdapter(adapter);
        binding.spinnerAlcohol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                alcoholStatus = binding.spinnerAlcohol.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showSmokeStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, smoke);
        binding.spinnerSmoke.setAdapter(adapter);
        binding.spinnerSmoke.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               smokeStatus = binding.spinnerSmoke.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void showExperience() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, experience);
        binding.spinnerExperience.setAdapter(adapter);
        binding.spinnerExperience.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                workExperience = binding.spinnerExperience.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showLicenceStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.single_spinner_maid_layout, drivingLicence);
        binding.spinnerDrivingLicence.setAdapter(adapter);
        binding.spinnerDrivingLicence.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drivingStatus = binding.spinnerDrivingLicence.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setValueInHashMap() {

        //createPartFromString(String.valueOf(imageIdList.get(position)))
        userDetailMap = new HashMap<>();
        userDetailMap.put("job_listing_title_id", createPartFromString(String.valueOf(getIntent().getStringExtra("listingID"))));
        userDetailMap.put("country_id", createPartFromString(String.valueOf(getIntent().getStringExtra("country"))));
        userDetailMap.put("state_id", createPartFromString(String.valueOf(getIntent().getStringExtra("state"))));
        userDetailMap.put("district_id", createPartFromString(String.valueOf(getIntent().getStringExtra("district"))));
        userDetailMap.put("city_id", createPartFromString(String.valueOf(getIntent().getStringExtra("city"))));
        userDetailMap.put("working_style_id", createPartFromString(String.valueOf(getIntent().getStringExtra("workingType"))));
        userDetailMap.put("expected_min_fees", createPartFromString(String.valueOf(getIntent().getStringExtra("minfee"))));
        userDetailMap.put("expected_max_fees", createPartFromString(String.valueOf(getIntent().getStringExtra("maxfee"))));
        userDetailMap.put("job_choice_id", createPartFromString(String.valueOf(getIntent().getStringExtra("jobCategory"))));
        userDetailMap.put("live_with_family", createPartFromString(String.valueOf(getIntent().getStringExtra("family"))));
        userDetailMap.put("travel", createPartFromString(String.valueOf(getIntent().getStringExtra("travel"))));
        userDetailMap.put("description", createPartFromString(String.valueOf(getIntent().getStringExtra("description"))));
        userDetailMap.put("nationality", createPartFromString(String.valueOf(String.valueOf(nationalityID))));
        userDetailMap.put("marital_status", createPartFromString(String.valueOf(mariStatus)));
        userDetailMap.put("kid_status", createPartFromString(String.valueOf(kidsStatus)));
        userDetailMap.put("hijab", createPartFromString(String.valueOf(hijabStatus)));
        userDetailMap.put("education_id", createPartFromString(String.valueOf(educationNameListid)));
        userDetailMap.put("known_language_id", createPartFromString(String.valueOf(languageNameListid)));
        userDetailMap.put("driving_licence", createPartFromString(String.valueOf(drivingStatus)));
        userDetailMap.put("smoking", createPartFromString(String.valueOf(smokeStatus)));
        userDetailMap.put("alcohol", createPartFromString(String.valueOf(alcoholStatus)));
        userDetailMap.put("pet_problem", createPartFromString(String.valueOf(petProblemListid)));
        userDetailMap.put("min_age", createPartFromString(String.valueOf(binding.tvMinAge.getText().toString())));
        userDetailMap.put("max_age", createPartFromString(String.valueOf(binding.tvMaxAge.getText().toString())));
        userDetailMap.put("fees_currency", createPartFromString(String.valueOf(getIntent().getStringExtra("fees_currency"))));
        userDetailMap.put("work_status", createPartFromString(String.valueOf(work)));
        userDetailMap.put("experience", createPartFromString(String.valueOf(workExperience)));

        imagePath = getIntent().getStringExtra("imagePath");

        //intent.putExtra("imagePath", imagePath);
        setImageInPart();
        //postJobByUser();
    }

    public void setImageInPart() {

        try {
            file = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            // MultipartBody.Part is used to send also the actual file name
            profileImage = MultipartBody.Part.createFormData("image", file.getName(),
                    requestFile);
           /* userDetailMap = new HashMap<>();
            userDetailMap.put("user_type", createPartFromString(Constants.userType(this)));
            userDetailMap.put("step_for_maid_profile", createPartFromString("1"));*/
            postJobByUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
            /*binding.progress.setIndeterminate(true);
            binding.progress.setVisibility(View.VISIBLE);
            binding.btnNext.setClickable(false);

            registerProfileToServer(Constants.TIMEZONE, Constants.LOCALE, accessToken,
                    profileImage);*/
        }
    }

    private void postJobByUser() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        retrofit2.Call<RegisterApi> call = apiService.postJobByUser(
                Constants.TIMEZONE,
                Constants.LOCALE,
                accessToken,
                userDetailMap,
                profileImage);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {
                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        showPopUp();

                    } else {
                        binding.progress.setVisibility(View.GONE);
                        binding.btnSubmit.setClickable(true);
                        Toast.makeText(AddListNextStepActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.btnSubmit.setClickable(true);
                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddListNextStepActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {

                            Toast.makeText(AddListNextStepActivity.this,
                                    ""+response.errorBody().toString(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d("TEST", R.string.error+" " + response.errorBody().string()
                                    +R.string.message+" " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                binding.btnSubmit.setClickable(true);
                Toast.makeText(AddListNextStepActivity.this, R.string.error+" "+ t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    public void showPopUp() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_listing_done);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
        }
        // Hide after some seconds
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    Intent intent = new Intent(AddListNextStepActivity.this,
                            MyListingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 2000);
    }
}