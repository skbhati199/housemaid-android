package com.housemaid.activities.agency.fromHome;

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
import com.housemaid.activities.SelectionActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityAddMaid5Binding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMaidActivity5 extends BaseActivity implements View.OnClickListener {

    private ActivityAddMaid5Binding binding;
    private String notification;
    private String hidePhoto;
    private SharedPreference sharedPreference;
    private String accessToken;
    private int startYear;
    private int startMonth;
    private String start1;
    private String end1;
    private String start2;
    private String end2;
    private String start3;
    private String end3;
    private ArrayList<String> startDate;
    private ArrayList<String> endDate;
    private ArrayList<String> workExperiences;
    private ArrayList<String> stillWork;
    private int checkState = 1;
    private boolean nextStep = false;
    private int maid_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_maid5);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.ivBack.setVisibility(View.GONE);
        binding.toolbar.tvTitle.setText(getString(R.string.complete_your_profile));

        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
        maid_id = getIntent().getIntExtra("maid_id", 0);

        Date today = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String todayDate = simpleDateFormat.format(today);

        if (binding.switchHidePhoto.isActivated()) {
            hidePhoto = "1";
        } else hidePhoto = "0";

        if (binding.switchNotification.isActivated()) {
            notification = "1";
        } else notification = "0";

        stillWork = new ArrayList<>();
        workExperiences = new ArrayList<>();
        startDate = new ArrayList<>();
        endDate = new ArrayList<>();

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSubmit.setOnClickListener(this);
        binding.tvStartDate.setOnClickListener(this);
        binding.tvEndDate.setOnClickListener(this);
        binding.tvStartDate2.setOnClickListener(this);
        binding.tvEndDate2.setOnClickListener(this);
        binding.tvStartDate3.setOnClickListener(this);
        binding.tvEndDate3.setOnClickListener(this);
        binding.btnAdd1.setOnClickListener(this);
        binding.btnAdd2.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (binding.etEducationDetail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_fill_education_details, Toast.LENGTH_SHORT).show();
                } else if (binding.etCertificateDetail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_fill_certificate_details, Toast.LENGTH_SHORT).show();
                } else if (binding.etAboutDetail.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.Please_fill_about_yourself, Toast.LENGTH_SHORT).show();
                } else if (binding.etWorkExperience.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_fill_work_experience, Toast.LENGTH_SHORT).show();
                } else {
                    openNextActivity();
                }

                break;


            case R.id.btnAdd1:
                binding.llAdd1.setVisibility(View.GONE);
                binding.llExperience2.setVisibility(View.VISIBLE);
                checkState = 2;
                break;

            case R.id.btnAdd2:
                binding.llAdd2.setVisibility(View.GONE);
                binding.llExperience3.setVisibility(View.VISIBLE);
                checkState = 3;
                break;


            case R.id.tvStartDate:
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
                break;

            case R.id.tvEndDate:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end1 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        if (year > startYear) {
                            binding.tvEndDate.setText(end1);
                        } else
                            Toast.makeText(AddMaidActivity5.this,
                                    R.string.choose_corect_date, Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.tvStartDate2:
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
                break;

            case R.id.tvEndDate2:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end2 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        if (year > startYear) {
                            binding.tvEndDate2.setText(end2);
                        } else
                            Toast.makeText(AddMaidActivity5.this,
                                    R.string.choose_corect_date, Toast.LENGTH_SHORT).show();

                    }
                });
                break;
            case R.id.tvStartDate3:
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
                break;

            case R.id.tvEndDate3:
                getSelectedDate(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        end3 = year + "-" +
                                ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                                ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth));
                        if ((year > startYear)) {
                            binding.tvEndDate3.setText(end3);
                        } else
                            Toast.makeText(AddMaidActivity5.this,
                                    R.string.choose_corect_date, Toast.LENGTH_SHORT).show();

                    }
                });
                break;
        }
    }


    private void openNextActivity() {

        int stillWorking;
        if (checkState == 1) {
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
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }


            } else nextStep = true;
        } else if (checkState == 2) {
            startDate = new ArrayList<>();
            endDate = new ArrayList<>();
            stillWork = new ArrayList<>();
            workExperiences = new ArrayList<>();
            if (!binding.etWorkExperience2.getText().toString().isEmpty() &&
                    !binding.etWorkExperience.getText().toString().isEmpty()) {
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
                        nextStep = true;
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                } else {
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
                        nextStep = true;
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }


            } else Toast.makeText(this, R.string.fill_all_the_details, Toast.LENGTH_SHORT).show();
        } else if (checkState == 3) {
            startDate = new ArrayList<>();
            endDate = new ArrayList<>();
            stillWork = new ArrayList<>();
            workExperiences = new ArrayList<>();
            if (!binding.etWorkExperience2.getText().toString().isEmpty() &&
                    !binding.etWorkExperience.getText().toString().isEmpty()
                    && !binding.etWorkExperience3.getText().toString().isEmpty()) {

                if (binding.checkbox3.isChecked()) {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvEndDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvEndDate2.getText().toString().isEmpty() &&
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
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();


                } else {
                    if (!binding.tvStartDate.getText().toString().isEmpty() &&
                            !binding.tvStartDate2.getText().toString().isEmpty() &&
                            !binding.tvStartDate3.getText().toString().isEmpty()) {
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
                    } else
                        Toast.makeText(this, R.string.select_start_end_date, Toast.LENGTH_LONG).show();
                }


            } else Toast.makeText(this, R.string.fill_all_the_details, Toast.LENGTH_SHORT).show();

        }

        if (nextStep) {

            binding.progress.setVisibility(View.VISIBLE);
            binding.btnSubmit.setClickable(false);

            if (binding.switchHidePhoto.isChecked()) {
                hidePhoto = "1";
            } else hidePhoto = "0";

            if (binding.switchNotification.isChecked()) {
                notification = "1";
            } else notification = "0";

            sendDataOfMaid();
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

    private void sendDataOfMaid() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String step = "5";
        Call<RegisterApi> call = apiService.agencyAddMaid5(
                Constants.TIMEZONE,
                accessToken,
                String.valueOf(maid_id),
                binding.etEducationDetail.getText().toString().trim(),
                binding.etCertificateDetail.getText().toString().trim(),
                binding.etAboutDetail.getText().toString().trim(),
                notification, step, hidePhoto, workExperiences, startDate, endDate, stillWork);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                binding.btnSubmit.setClickable(true);

                if (response.isSuccessful()) {

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        /*sharedPreference.putString("complete_profile", signUpModel.getComplete_profile());*/
                        sharedPreference.putString("complete_profile", signUpModel
                                .getComplete_profile());

                        Intent intent = new Intent(AddMaidActivity5.this, EnterLocationActivity.class);
                        intent.putExtra("nologin", 2);
                        intent.putExtra("maid_id", signUpModel.getId());
                        startActivity(intent);
                        finish();

                    } else

                        Toast.makeText(AddMaidActivity5.this, getString(R.string.fails_),
                                Toast.LENGTH_LONG).show();

                } else {

                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddMaidActivity5.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {

                            Toast.makeText(AddMaidActivity5.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", getString(R.string.error) + response.errorBody().string()
                                    + getString(R.string.message_) + response.message());

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterApi> call, Throwable t) {

                Toast.makeText(AddMaidActivity5.this, getString(R.string.error) + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}