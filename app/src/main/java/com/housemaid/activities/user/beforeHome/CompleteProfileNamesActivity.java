package com.housemaid.activities.user.beforeHome;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.EnterLocationActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityCompleteProfileNamesBinding;
import com.housemaid.model.CountryListModel;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.GPSTracker;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteProfileNamesActivity extends BaseActivity implements View.OnClickListener {

    ActivityCompleteProfileNamesBinding binding;
    String status[];
    String imagePath;
    Double lati, longi;
    String currentLocation;
    SharedPreference sharedPreference;
    String accessToken;
    String gender;
    File file;
    MultipartBody.Part profileImage;
    Map <String, RequestBody> userDetailMap;
    String maritalStatus;
    String notification;
    String hidePhoto;
    String countryName;
    String stateName;
    int id;
    int stateID;
    boolean setDob = false;
    boolean setGender = false;
    private int count = 0;
    private int count2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        binding = DataBindingUtil.setContentView ( this, R.layout.activity_complete_profile_names );
        init ();
        initControls ();
    }

    @Override
    public void init() {
        super.init ();
        binding.toolbar.ivBack.setVisibility ( View.GONE );
        binding.toolbar.tvTitle.setText ( "Complete Your Profile" );

        status = getResources ().getStringArray ( R.array.items );
        showStatusList ();

        imagePath = getIntent ().getStringExtra ( "image_path" );

        sharedPreference = SharedPreference.getInstance ( this );

        accessToken = sharedPreference.getString ( "signUp_token", "0" );

        if (binding.switchHidePhoto.isChecked()) {
            hidePhoto = "1";
        } else hidePhoto = "0";

        if (binding.switchNotification.isChecked()) {
            notification = "1";
        } else notification = "0";

        binding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notification = "1";
                } else {
                    notification = "0";
                }
            }
        });
        binding.switchHidePhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    hidePhoto = "1";
                } else {
                    hidePhoto = "0";
                }
            }
        });

    }

    @Override
    public void initControls() {
        super.initControls ();
        binding.tvDob.setOnClickListener ( this );
        binding.tvFemale.setOnClickListener ( this );
        binding.tvMale.setOnClickListener ( this );
        binding.tvFemaleColor.setOnClickListener ( this );
        binding.tvMaleColor.setOnClickListener ( this );
        binding.rlMaritalStatus.setOnClickListener ( this );
        binding.rlCountry.setOnClickListener ( this );
        binding.rlState.setOnClickListener ( this );
        binding.btnSubmit.setOnClickListener ( this );
    }

    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.btnSubmit) {

                setImageInPart ();
                

            
} else if (v.getId () == R.id.rlCountry) {

                if (ValidationUtils.isOnline ( binding.relativeLayout, this )) {
                    startActivityForResult ( new Intent ( this, CountryListActivity.class ), 512 );
                }
                

            
} else if (v.getId () == R.id.rlState) {

                if (binding.tvCountryName.getText().length()==0){
                    Toast.makeText(this, "Please enter country first", Toast.LENGTH_SHORT).show();
                }else startActivityForResult ( new Intent ( this, StateListActivity.class ), 520 );
                

            
} else if (v.getId () == R.id.rlMaritalStatus) {

                binding.spinnerMaritalStatus.performClick ();
                

            
} else if (v.getId () == R.id.tv_dob) {

                onSelectDate ();
                

            
} else if (v.getId () == R.id.tv_male) {

                count++;
                if (count % 2 == 1) {
                    binding.tvMaleColor.setVisibility ( View.VISIBLE );
                    gender = "male";
                    setGender = true;
                    if (count2 % 2 == 1)
                        count2++;
                    binding.tvFemaleColor.setVisibility ( View.GONE );
                    
                }

            
} else if (v.getId () == R.id.tv_male_color) {

                count++;
                if (count % 2 == 0) {
                    binding.tvMaleColor.setVisibility ( View.GONE );
                    
                }

            
} else if (v.getId () == R.id.tv_female) {

                count2++;
                if (count2 % 2 == 1) {
                    binding.tvFemaleColor.setVisibility ( View.VISIBLE );
                    gender = "female";
                    setGender = true;
                    if (count % 2 == 1) {
                        count++;
                        binding.tvMaleColor.setVisibility ( View.GONE );
                    }
                    
                }

            
} else if (v.getId () == R.id.tv_female_color) {

                count2++;
                if (count2 % 2 == 0) {
                    count++;
                    binding.tvFemaleColor.setVisibility ( View.GONE );
                    
                }
        
}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        if (requestCode == 512 && resultCode == Activity.RESULT_OK) {
            countryName = data.getStringExtra ( "country" );
            id = data.getIntExtra ( "id", 0 );
            sharedPreference.putInteger ( "Country_id", id );
            binding.tvCountryName.setText ( countryName );
        }
        if (requestCode == 520 && resultCode == Activity.RESULT_OK) {
            stateName = data.getStringExtra ( "state" );
            binding.tvState.setText ( stateName );
            stateID = data.getIntExtra ( "state_id", 0 );
            binding.tvStateName.setText ( stateName );
        }
    }

    private void showStatusList() {
        ArrayAdapter <String> adapter = new ArrayAdapter <String> ( this,
                R.layout.singlerow_spinner_layout, status );
        binding.spinnerMaritalStatus.setAdapter ( adapter );
        binding.spinnerMaritalStatus.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
                maritalStatus = binding.spinnerMaritalStatus.getSelectedItem ().toString ();
            }

            @Override
            public void onNothingSelected(AdapterView <?> parent) {

            }
        } );
    }

    public  void onSelectDate() {
        final Calendar mDate = Calendar.getInstance ();
        int date = mDate.get ( Calendar.DAY_OF_MONTH );
        int month = mDate.get ( Calendar.MONTH );
        int year = mDate.get ( Calendar.YEAR );
        DatePickerDialog datePickerDialog = new DatePickerDialog ( this
                , new DatePickerDialog.OnDateSetListener () {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (year >= mDate.get ( Calendar.YEAR )-16) {
                    Toast.makeText ( CompleteProfileNamesActivity.this, R.string.atleast_16_years_old,
                            Toast.LENGTH_SHORT ).show ();

                } else {
                    binding.tvDob.setText ( year + "-" +
                            ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                            ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)) );
                    setDob = true;
                }
            }
        }, year, month, date );
        datePickerDialog.getDatePicker ().setCalendarViewShown ( true );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show ();
    }

    public void setImageInPart() {

        try {
            file = new File ( imagePath );

            RequestBody requestFile = RequestBody.create ( MediaType.parse ( "image/*" ), file );

            // MultipartBody.Part is used to send also the actual file name
            profileImage = MultipartBody.Part.createFormData ( "profile_image[]", file.getName (),
                    requestFile );

            userDetailMap = new HashMap <> ();
            userDetailMap.put ( "user_type", createPartFromString ( Constants.userType ( this ) ) );
            userDetailMap.put ( "country_id", createPartFromString ( String.valueOf ( id ) ) );
            userDetailMap.put ( "dob", createPartFromString ( binding.tvDob.getText ().toString () ) );
            userDetailMap.put ( "gender", createPartFromString ( gender ) );
            userDetailMap.put ( "marital_status", createPartFromString ( maritalStatus ) );
            userDetailMap.put ( "notification_status", createPartFromString ( notification ) );
            userDetailMap.put ( "photo_email_status", createPartFromString ( hidePhoto ) );
            userDetailMap.put ( "state_id", createPartFromString ( String.valueOf ( stateID ) ) );

        } catch (Exception e) {
            e.printStackTrace ();
        }
        if (setGender) {
            if (setDob) {
                if (ValidationUtils.isOnline ( binding.relativeLayout, this )) {
                    binding.progress.setIndeterminate ( true );
                    binding.progress.setVisibility ( View.VISIBLE );
                    binding.btnSubmit.setClickable ( false );
                    binding.rlCountry.setClickable ( false );
                  /*  final Handler handler = new Handler ();
                    handler.postDelayed ( new Runnable () {
                        @Override
                        public void run() {
                            binding.progress.setVisibility ( View.GONE );
                            // Do something after 5s = 5000ms
                            startActivity ( new Intent ( CompleteProfileNamesActivity.this, EnterLocationActivity.class ) );

                        }
                    }, 5000 );*/
                        registerProfileToServer(Constants.TIMEZONE, Constants.LOCALE, accessToken,
                                profileImage, userDetailMap);
                }

            } else
                Toast.makeText ( this, "Please Enter Date of Birth!", Toast.LENGTH_SHORT ).show ();
        } else Toast.makeText ( this, "Please Fill All Details!", Toast.LENGTH_SHORT ).show ();
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create (
                okhttp3.MultipartBody.FORM, descriptionString );
    }

    private void registerProfileToServer(String timezone, String locale, String access_token,
                                         MultipartBody.Part profileImage,
                                         Map <String, RequestBody> userDetailMap) {
        ApiInterface apiService = ApiClient.getClient ().create ( ApiInterface.class );
        retrofit2.Call <RegisterApi> call = apiService.completeProfileDetails( timezone, locale, access_token,
                profileImage, userDetailMap );
        call.enqueue ( new Callback <RegisterApi> () {

            @Override
            public void onResponse(Call <RegisterApi> call, Response <RegisterApi> response) {
                binding.progress.setVisibility ( View.GONE );
                if (response.isSuccessful ()) {
                    RegisterApi registerApi = response.body ();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body ();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString ( "complete_profile", signUpModel.getComplete_profile () );
                        sharedPreference.putBoolean ( "session", true );

                        /*startActivity ( new Intent ( CompleteProfileNamesActivity.this, HomeActivity.class ) );*/
                        // Intent intent = new Intent(ExplorerSignUpActivity.this, IntroSlideActivity.class);
                        // intent.putExtra("STATUS", value);
                        // startActivity(intent);
                        Intent intent = new Intent(CompleteProfileNamesActivity.this, EnterLocationActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    } else

                        Toast.makeText ( CompleteProfileNamesActivity.this, "Registration Fails", Toast.LENGTH_LONG ).show ();

                } else {
                    binding.progress.setVisibility ( View.GONE );
                    binding.btnSubmit.setClickable ( true );
                    binding.rlCountry.setClickable ( true );
                    try {
                        if (response.code()==401){
                            sharedPreference.deletePreference ();
                            Intent signInIntent = new Intent ( CompleteProfileNamesActivity.this,
                                    SelectionActivity.class );
                            startActivity ( signInIntent );
                            finishAffinity ();
                        }else {
                            Toast.makeText(CompleteProfileNamesActivity.this, "" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() + "message : " + response.message());
                        }
                        } catch (IOException e) {
                        e.printStackTrace ();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call <RegisterApi> call, Throwable t) {
                binding.progress.setVisibility ( View.GONE );
                binding.btnSubmit.setClickable ( true );
                binding.rlCountry.setClickable ( true );

                Toast.makeText ( CompleteProfileNamesActivity.this, "error " + t.getMessage (), Toast.LENGTH_SHORT ).show ();

            }
        } );
    }
    /*private void openDialog() {
        final Dialog dialog = new Dialog(this);
        final GPSTracker gpsTracker = new GPSTracker(this);
        lati = gpsTracker.getLatitude();
        longi = gpsTracker.getLongitude();
         currentLocation = latLngToAddress(this, lati, longi);

        dialog.setContentView(R.layout.popup_enter_location_layout);

        dialog.findViewById(R.id.btnEnterManually).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
                Intent intent = new Intent(CompleteProfileNamesActivity.this, EnterLocationActivity.class);
                startActivity(intent);
            }
        });

        dialog.findViewById(R.id.btnEnableLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                else {
                  *//*  dialog.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                    if (currentLocation!=null) {
                        updateLocation(Constants.TIMEZONE, Constants.LOCALE, accessToken, currentLocation,
                                lati, longi);

                        sharedPreference.putString("location", currentLocation);
                    }else {
                        dialog.findViewById(R.id.progress).setVisibility(View.GONE);
                        Toast.makeText(gpsTracker, "Try again!", Toast.LENGTH_SHORT).show();
                    }*//*
                    Toast.makeText(CompleteProfileNamesActivity.this,
                            "Location is already enabled.", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }*/


    private void updateLocation(String timezone, String locale, String accessToken,
                                String location_text, Double latitude, Double longitude) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.updateLocation(timezone, locale, accessToken,
                location_text, latitude, longitude);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    startActivity(new Intent(CompleteProfileNamesActivity.this, HomeUserActivity.class));
                    finishAffinity();
                    binding.btnSubmit.setClickable(true);
                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);
                        binding.btnSubmit.setClickable(true);
                        if (response.code() == 400) {
                            Toast.makeText(CompleteProfileNamesActivity.this, "Invalid Credentials!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CompleteProfileNamesActivity.this, "" + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() + "message : " + response.message());
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
                Toast.makeText(CompleteProfileNamesActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

