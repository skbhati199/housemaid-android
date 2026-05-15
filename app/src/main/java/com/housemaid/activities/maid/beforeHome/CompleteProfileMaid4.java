package com.housemaid.activities.maid.beforeHome;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.EnterLocationActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityCompleteProfileMaid4Binding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteProfileMaid4 extends BaseActivity implements View.OnClickListener {

    ActivityCompleteProfileMaid4Binding binding;
    String notification = "1";
    String hidePhoto = "0";
    SharedPreference sharedPreference;
    String accessToken;
    String step = "5";
    int stillWorking;
    int startYear;
    int startMonth;
    String start1;
    String end1;
    String start2;
    String end2;
    String start3;
    String end3;
    ArrayList<String> startDate;
    ArrayList<String> endDate;
    ArrayList<String> workExperiences;
    ArrayList<String> stillWork;
    int checkState=1;
    boolean nextStep =false;
    String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_profile_maid4);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.tvTitle.setText(R.string.complete_your_profile);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");

        Date today = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        todayDate = simpleDateFormat.format(today);

        stillWork = new ArrayList<>();
        workExperiences = new ArrayList<>();
        startDate = new ArrayList<>();
        endDate = new ArrayList<>();

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnNext.setOnClickListener(this);
        binding.btnPrevious.setOnClickListener(this);
        binding.tvStartDate.setOnClickListener(this);
        binding.tvEndDate.setOnClickListener(this);
        binding.tvStartDate2.setOnClickListener(this);
        binding.tvEndDate2.setOnClickListener(this);
        binding.tvStartDate3.setOnClickListener(this);
        binding.tvEndDate3.setOnClickListener(this);
        binding.btnAdd1.setOnClickListener(this);
        binding.btnAdd2.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);

        binding.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvEndDate.setText("");
                    binding.tvEndDate.setEnabled(false);
                } else {
                    binding.tvEndDate.setText("");
                    binding.tvEndDate.setEnabled(true);
                }
            }
        });
        binding.checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvEndDate2.setText("");
                    binding.tvEndDate2.setEnabled(false);
                } else {
                    binding.tvEndDate2.setText("");
                    binding.tvEndDate2.setEnabled(true);
                }
            }
        });
        binding.checkbox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvEndDate3.setText("");
                    binding.tvEndDate3.setEnabled(false);
                } else {
                    binding.tvEndDate3.setText("");
                    binding.tvEndDate3.setEnabled(true);
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivBack) {

                onBackPressed();
                

            
} else if (v.getId() == R.id.btnNext) {

                openNextActivity();
                

            
} else if (v.getId() == R.id.btnPrevious) {

                onBackPressed();
                

            
} else if (v.getId() == R.id.btnAdd1) {

                binding.llAdd1.setVisibility(View.GONE);
                binding.llExperience2.setVisibility(View.VISIBLE);
                checkState=2;
                

            
} else if (v.getId() == R.id.btnAdd2) {

                binding.llAdd2.setVisibility(View.GONE);
                binding.llExperience3.setVisibility(View.VISIBLE);
                checkState=3;
                


            
} else if (v.getId() == R.id.tvStartDate) {

                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start1 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        binding.tvStartDate.setText(start1);
                        startMonth = month + 1;
                        startYear = year;
                    }
                });
                

            
} else if (v.getId() == R.id.tvEndDate) {

                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end1 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        if (year > startYear ) {
                            binding.tvEndDate.setText(end1);
                        } else
                            Toast.makeText(CompleteProfileMaid4.this,
                                    R.string.choose_corect_date, Toast.LENGTH_SHORT).show();

                    }
                });
                
            
} else if (v.getId() == R.id.tvStartDate2) {

                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start2 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        binding.tvStartDate2.setText(start2);
                        startMonth = month + 1;
                        startYear = year;
                    }
                });
                

            
} else if (v.getId() == R.id.tvEndDate2) {

                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end2 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        if (year > startYear) {
                            binding.tvEndDate2.setText(end2);
                        } else
                            Toast.makeText(CompleteProfileMaid4.this,
                                    R.string.choose_corect_date, Toast.LENGTH_SHORT).show();

                    }
                });
                
            
} else if (v.getId() == R.id.tvStartDate3) {

                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        start3 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        binding.tvStartDate3.setText(start3);
                        startMonth = month + 1;
                        startYear = year;

                    }
                });
                

            
} else if (v.getId() == R.id.tvEndDate3) {

                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end3 =year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        if ((year > startYear)) {
                            binding.tvEndDate3.setText(end3);
                        } else
                            Toast.makeText(CompleteProfileMaid4.this,
                                    R.string.choose_corect_date, Toast.LENGTH_SHORT).show();
                    }
                });
                
        
}
    }

    private void openNextActivity() {

        if (checkState==1) {
            if (!binding.etWorkExperience.getText().toString().isEmpty()) {
                if (binding.checkbox.isChecked()) {
                    if (!binding.tvStartDate.getText().toString().isEmpty()) {
                        startDate.add(start1);
                        endDate.add(binding.tvEndDate.getText().toString());
                        stillWorking = 1;
                        workExperiences.add(binding.etWorkExperience.getText().toString());

                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();

                } else {
                if (!binding.tvStartDate.getText().toString().isEmpty() &&
                        !binding.tvEndDate.getText().toString().isEmpty()) {
                    stillWorking = 0;
                    startDate.add(start1);
                    endDate.add(end1);
                    workExperiences.add(binding.etWorkExperience.getText().toString());

                    stillWork.add(String.valueOf(stillWorking));
                    nextStep = true;
                }else Toast.makeText(this,  R.string.select_start_end_date, Toast.LENGTH_LONG).show();
            }

            }else nextStep=true;
        }else if (checkState==2){
            startDate = new ArrayList<>();
            endDate = new ArrayList<>();
            stillWork = new ArrayList<>();
            workExperiences = new ArrayList<>();
            if (!binding.etWorkExperience2.getText().toString().isEmpty() &&
                    !binding.etWorkExperience.getText().toString().isEmpty()){
                if (binding.checkbox2.isChecked()) {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty()
                           ) {
                        startDate.add(start1);
                        startDate.add(start2);
                        endDate.add(binding.tvEndDate.getText().toString());
                        endDate.add(binding.tvEndDate2.getText().toString());
                        stillWorking = 1;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());

                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep= true;
                    } else Toast.makeText(this,  R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }else {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvEndDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvEndDate2.getText().toString().isEmpty()) {
                        startDate.add(start1);
                        startDate.add(start2);
                        endDate.add(end1);
                        endDate.add(end2);

                        stillWorking = 0;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());

                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep= true;
                    } else Toast.makeText(this,  R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }

            }else Toast.makeText(this, R.string.fill_all_the_details, Toast.LENGTH_SHORT).show();
        }else if (checkState==3){
            startDate = new ArrayList<>();
            endDate = new ArrayList<>();
            stillWork = new ArrayList<>();
            workExperiences = new ArrayList<>();
            if (!binding.etWorkExperience2.getText().toString().isEmpty() &&
                    !binding.etWorkExperience.getText().toString().isEmpty()
                    && !binding.etWorkExperience3.getText().toString().isEmpty()){

                if (binding.checkbox3.isChecked()) {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvEndDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvEndDate2.getText().toString().isEmpty()&&
                            !binding.tvStartDate3.getText().toString().isEmpty() &&
                            !binding.tvEndDate3.getText().toString().isEmpty()) {
                        stillWorking = 1;
                        startDate.add(start1);
                        startDate.add(start2);
                        startDate.add(start3);
                        endDate.add(end1);
                        endDate.add(end2);
                        endDate.add(end3);
                        stillWork.add("0");
                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());
                        workExperiences.add(binding.etWorkExperience3.getText().toString());
                    }else Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();

                } else {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvStartDate3.getText().toString().isEmpty() ) {
                        startDate.add(start1);
                        startDate.add(start2);
                        startDate.add(start3);
                        endDate.add(binding.tvEndDate.getText().toString());
                        endDate.add(binding.tvEndDate2.getText().toString());
                        endDate.add(binding.tvEndDate3.getText().toString());
                        stillWorking = 0;
                        stillWork.add("0");
                        stillWork.add("0");
                        stillWork.add(String.valueOf(stillWorking));
                        nextStep = true;
                        workExperiences.add(binding.etWorkExperience.getText().toString());
                        workExperiences.add(binding.etWorkExperience2.getText().toString());
                        workExperiences.add(binding.etWorkExperience3.getText().toString());
                    }else Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }

            }else Toast.makeText(this, R.string.fill_all_the_details, Toast.LENGTH_SHORT).show();

        }
            if (nextStep) {
                binding.progress.setVisibility(View.VISIBLE);
                binding.btnNext.setEnabled(false);

                if (binding.switchHidePhoto.isChecked()) {
                    hidePhoto = "1";
                } else hidePhoto = "0";

                if (binding.switchNotification.isChecked()) {
                    notification = "1";
                } else notification = "0";

                sendDataOfMaid(accessToken, Constants.userType(this),
                        binding.etEducationDetail.getText().toString().trim(),
                        binding.etCertificateDetail.getText().toString().trim(),
                        binding.etAboutDetail.getText().toString().trim(),
                        notification, step, hidePhoto, workExperiences,
                        startDate, endDate,
                        stillWork);
            }

    }

    private void getSelectedDate(DatePickerDialog.OnDateSetListener listener) {
        Calendar mDate = Calendar.getInstance();
        int date = mDate.get(Calendar.DAY_OF_MONTH);
        int month = mDate.get(Calendar.MONTH);
        int year = mDate.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this
                , listener, year, month, date);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

  /*  public static String latLngToAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            //address = addresses.get(0).getAddressLine(0);
            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            if (addresses != null) {
                String locality = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String featureName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                String premises = addresses.get(0).getPremises();
                String subAdminArea = addresses.get(0).getSubAdminArea();
                String subLocality = addresses.get(0).getSubLocality();

                Log.d("SIY", "locality - city : " + locality);
                Log.d("SIY", "admin area - state : " + state);
                Log.d("SIY", "Country : " + country);
                Log.d("SIY", "Postal Code : " + postalCode);
                Log.d("SIY", "Featured Name : " + featureName);
                Log.d("SIY", "Premises : " + premises);
                Log.d("SIY", "Sub Admin Area : " + subAdminArea);
                Log.d("SIY", "Sub Locality  : " + subLocality); // Sector

                //  c-152, Sector-63, Noida

                if (subLocality == null) {
                    subLocality = state;
                    locality = country;
                }
                if (locality == null) {
                    subLocality = subAdminArea;
                }

                address = featureName + ", " + subLocality + ", " + locality + "- " + postalCode;
                Log.d("house", "Address : " + address);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("house", "Exception while converting LatLng to address : " + e.getMessage());
        }
        return address;
    }
*/
    private void sendDataOfMaid(String access_token,
                                String user_type,
                                String maid_education,
                                String maid_certificate,
                                String maid_about_me,
                                String notification_status,
                                String step_for_maid_profile,
                                String photo_email_status,
                                ArrayList<String> maid_work_experiences,
                                ArrayList<String> start_date,
                                ArrayList<String> end_date,
                                ArrayList<String> still_working) {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterApi> call = apiService.completeProfileMaid4(Constants.TIMEZONE, Constants.LOCALE, access_token,
                user_type, maid_education, maid_certificate, maid_about_me, notification_status,
                step_for_maid_profile,
                photo_email_status, maid_work_experiences, start_date, end_date, still_working);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnNext.setEnabled(true);

                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        /*sharedPreference.putString("complete_profile", signUpModel.getComplete_profile());*/
                        sharedPreference.putString("complete_profile", signUpModel.getComplete_profile());
                        sharedPreference.putInteger("step_for_maid_profile", signUpModel.getStep_for_maid_profile());

                        /*startActivity ( new Intent ( CompleteProfileMaid4.this, HomeUserActivity.class ) );*/
                        Intent intent = new Intent(CompleteProfileMaid4.this, EnterLocationActivity.class);
                        startActivity(intent);
                       finishAffinity();
                    } else

                        Toast.makeText(CompleteProfileMaid4.this, R.string.fails_, Toast.LENGTH_LONG).show();

                } else {

                    try {

                        Toast.makeText(CompleteProfileMaid4.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", getString(R.string.error) + response.errorBody().string()
                                + getString(R.string.message_) + response.message());  } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApi> call, Throwable t) {

                Toast.makeText(CompleteProfileMaid4.this, getString(R.string.error) +
                        t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}

