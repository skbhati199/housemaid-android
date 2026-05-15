package com.housemaid.activities.user.fromHome;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.adapter.ImageAdapter;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityEditUserProfileBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;
import com.imagepicker.FilePickUtils;
import com.imagepicker.LifeCycleCallBackManager;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.imagepicker.FilePickUtils.CAMERA_PERMISSION;
import static com.imagepicker.FilePickUtils.STORAGE_PERMISSION_IMAGE;

public class EditUserProfileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityEditUserProfileBinding binding;
    private ImageAdapter imageAdapter;
    private SharedPreference sharedPreference;
    private String accessToken;
    private Dialog dialog;
    private MultipartBody.Part profileImage;
    private Map<String, RequestBody> userDetailMap;
    private int id;
    private int stateID;
    private boolean setDob = false;
    private boolean setGender = false;
    private int count = 0;
    private int count2 = 0;
    private String gender;
    private String status[];
    private String maritalStatus;
    private String userId;
    private SignUpModel signUpModel;
    private ArrayList<String> imageList;
    private ArrayList<String> bigImageList;
    private ArrayList<Integer> imageIdList;
    private int position;
    private FilePickUtils filePickUtils;
    private LifeCycleCallBackManager lifeCycleCallBackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_user_profile);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        filePickUtils = new FilePickUtils(this, onFileChoose);
        lifeCycleCallBackManager = filePickUtils.getCallBackManager();
        binding.toolbar.tvTitle.setText(R.string.edit_profile);
        binding.toolbar.ivBack.setVisibility(View.VISIBLE);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
        signUpModel = (SignUpModel) getIntent().getSerializableExtra("userProfile");
        imageIdList = new ArrayList<>();
        bigImageList = new ArrayList<>();
        imageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            imageIdList.add(0);
        }
        if (signUpModel != null) {

            if (signUpModel.getUserImagesModel().isEmpty()) {
                binding.ivProfilePicBig.setImageResource(R.drawable.user);
                bigImageList.add(getResources().getDrawable(R.drawable.user).toString());
                imageList.add(getResources().getDrawable(R.drawable.user).toString());

            } else {
                if ((signUpModel.getUserImagesModel().size() > 0)) {
                    for (int i = 1; i <= signUpModel.getUserImagesModel().size(); i++) {
                        imageIdList.set(i - 1, signUpModel.getUserImagesModel().get(i - 1).getId());

                    }
                }
            }
            setData();
        } else Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();

        userId = sharedPreference.getString("user_id", "");
        status = getResources().getStringArray(R.array.items);

        showStatusList();

    }

    private void setData() {
        id = signUpModel.getCountry_id();
        stateID = signUpModel.getState_id();
        gender = signUpModel.getGender();
        maritalStatus = signUpModel.getMarital_status();


        if (!(signUpModel.getUserImagesModel().isEmpty()) && (signUpModel.getUserImagesModel().size() > 0)
                && !signUpModel.getUserImagesModel().get(0).getImageModel().getBig().isEmpty()) {
            for (int i = 1; i <= signUpModel.getUserImagesModel().size(); i++) {
                imageList.add(signUpModel.getUserImagesModel().get(i - 1).getImageModel().getSmall());
                bigImageList.add(signUpModel.getUserImagesModel().get(i - 1).getImageModel()
                        .getBig());
            }

            Glide.with(itemView.getContext()).load(signUpModel.getUserImagesModel().get(0).getImageModel().getBig())
                    .error(R.drawable.user).into(binding.ivProfilePicBig);
        }
        imageAdapter = new ImageAdapter(EditUserProfileActivity.this, imageList, bigImageList
                , binding, imageIdList);
        binding.rvImageList.setLayoutManager(new LinearLayoutManager(EditUserProfileActivity.this,
                RecyclerView.HORIZONTAL, false));
        binding.rvImageList.setAdapter(imageAdapter);


        binding.tvDob.setText(signUpModel.getDob());
        setDob = true;

        int position;
        if (signUpModel.getMarital_status().equals("Married")) {
            position = 1;
        } else position = 2;

        binding.spinnerMaritalStatus.setSelection(position);
        binding.tvCountryName.setText(signUpModel.getCountry_name());
        binding.tvStateName.setText(signUpModel.getState_name());

        if (Objects.equals(signUpModel.getGender(), "male")) {
            setGender = true;
            binding.tvMaleColor.setVisibility(View.VISIBLE);
        } else {
            binding.tvFemaleColor.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void initControls() {
        super.initControls();
        binding.ivChoosePhoto.setOnClickListener(this);
        binding.ivProfilePicBig.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);

        /*onClick of multiple profile pictures*/
        binding.tvDob.setOnClickListener(this);
        binding.tvFemale.setOnClickListener(this);
        binding.tvMale.setOnClickListener(this);
        binding.tvFemaleColor.setOnClickListener(this);
        binding.tvMaleColor.setOnClickListener(this);
        binding.rlMaritalStatus.setOnClickListener(this);
        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivChoosePhoto:
                if (position <= bigImageList.size() - 1) {
                    openDialog(onFileChoose);
                }
                break;

            case R.id.ivProfilePicBig:
                if (position <= bigImageList.size() - 1) {
                    openDialog(onFileChoose);
                }
                break;

            case R.id.btnSubmit:
                setImageInPart();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;

            //Choose Image Dialog Clicks
            case R.id.btnGallery:
                openGallery();
                break;
            case R.id.btnCamera:
                openCamera();
                break;
            case R.id.btnAvatar:
                openAvatars();
                break;

            case R.id.rlCountry:
                if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
                    startActivityForResult(new Intent(this,
                            CountryListActivity.class), 512);
                }
                break;

            case R.id.rlState:
                if (binding.tvCountryName.getText().length() == 0) {
                    Toast.makeText(this, "Please select Country!", Toast.LENGTH_SHORT).show();
                } else startActivityForResult(new Intent(this,
                        StateListActivity.class), 520);
                break;

            case R.id.rlMaritalStatus:
                binding.spinnerMaritalStatus.performClick();
                break;

            case R.id.tv_dob:
                onSelectDate();
                break;

            case R.id.tv_male:
                count++;
                if (count % 2 == 1) {
                    binding.tvFemaleColor.setVisibility(View.GONE);
                    binding.tvMaleColor.setVisibility(View.VISIBLE);
                    gender = "male";
                    setGender = true;
                    if (count2 % 2 == 1)
                        count2++;
                    binding.tvFemaleColor.setVisibility(View.GONE);
                    break;
                }

            case R.id.tv_male_color:
                count++;
                if (count % 2 == 0) {
                    binding.tvMaleColor.setVisibility(View.GONE);
                    break;
                }

            case R.id.tv_female:
                count2++;
                if (count2 % 2 == 1) {
                    binding.tvMaleColor.setVisibility(View.GONE);
                    binding.tvFemaleColor.setVisibility(View.VISIBLE);
                    gender = "female";
                    setGender = true;
                    if (count % 2 == 1) {
                        count++;
                        binding.tvMaleColor.setVisibility(View.GONE);
                    }
                    break;
                }

            case R.id.tv_female_color:
                count2++;
                if (count2 % 2 == 0) {
                    count++;
                    binding.tvFemaleColor.setVisibility(View.GONE);
                    break;
                }
        }

    }

    public void updateList(int position, ArrayList<Integer> imageIdList) {
        this.position = position;
        this.imageIdList = imageIdList;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 512 && resultCode == Activity.RESULT_OK) {
            String countryName = data.getStringExtra("country");
            id = data.getIntExtra("id", 0);
            sharedPreference.putInteger("Country_id", id);
            binding.tvCountryName.setText(countryName);
        }
        if (requestCode == 520 && resultCode == Activity.RESULT_OK) {
            String stateName = data.getStringExtra("state");
            binding.tvState.setText(stateName);
            stateID = data.getIntExtra("state_id", 0);
            binding.tvStateName.setText(stateName);
        }
    }
    /*private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }*/

    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, "title",
                null);
        return Uri.parse(path);
    }

    private void showStatusList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.singlerow_spinner_layout, status);
        binding.spinnerMaritalStatus.setAdapter(adapter);
        binding.spinnerMaritalStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maritalStatus = binding.spinnerMaritalStatus.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onSelectDate() {
        final Calendar mDate = Calendar.getInstance();
        int date = mDate.get(Calendar.DAY_OF_MONTH);
        int month = mDate.get(Calendar.MONTH);
        int year = mDate.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this
                , new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if (year >= mDate.get(Calendar.YEAR)) {
                    Toast.makeText(EditUserProfileActivity.this, "Please choose a Valid Date!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    binding.tvDob.setText(year + "-" +
                            ((month + 1) < 10 ? ("0" + (month + 1)) : (month + 1)) + "-" +
                            ((dayOfMonth) < 10 ? ("0" + dayOfMonth) : (dayOfMonth)));
                    setDob = true;
                }
            }
        }, year, month, date);
        datePickerDialog.getDatePicker().setCalendarViewShown(true);
        datePickerDialog.show();
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


    private void openDialog(FilePickUtils.OnFileChoose onFileChoose) {

        if (checkPermissionForReadExtertalStorage()) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.choose_image_layout);
            dialog.show();
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }

            dialog.findViewById(R.id.btnGallery).setOnClickListener(this);
            dialog.findViewById(R.id.btnCamera).setOnClickListener(this);
            dialog.findViewById(R.id.btnAvatar).setOnClickListener(this);
        } else {
            try {
                requestPermissionForReadExtertalStorage();
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.WRITE_STORAGE_PERMISSION_REQUEST_CODE);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void openGallery() {
        dialog.dismiss();
        filePickUtils.requestImageGallery(STORAGE_PERMISSION_IMAGE, true, false);

    }

    private void openCamera() {
        filePickUtils.requestImageCamera(CAMERA_PERMISSION, true, false);
        dialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private FilePickUtils.OnFileChoose onFileChoose = new FilePickUtils.OnFileChoose() {
        @Override
        public void onFileChoose(String fileUri, int requestCode, int size) {
            lifeCycleCallBackManager = filePickUtils.getCallBackManager();
            binding.ivProfilePicBig.setImageURI(Uri.fromFile(new File(fileUri)));
            setImagePart(fileUri);
        }
    };

    private void openAvatars() {
        binding.ivProfilePicBig.setImageResource(R.drawable.men_icon);
        binding.ivProfilePicBig.setScaleType(ImageView.ScaleType.FIT_XY);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, "avatar.PNG");
        setImagePart(file.getAbsolutePath());
        dialog.dismiss();
    }

    public void setImagePart(String fileUri) {

        try {
            File file = new File(fileUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            // MultipartBody.Part is used to send also the actual file name
            profileImage = MultipartBody.Part.createFormData("profile_image", file.getName(),
                    requestFile);
            userDetailMap = new HashMap<>();
            userDetailMap.put("image_id", createPartFromString(String.valueOf(imageIdList.get(position))));
            userDetailMap.put("user_id", createPartFromString(userId));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
            binding.progress.setIndeterminate(true);
            binding.progress.setVisibility(View.VISIBLE);


            updateProfilePicture(accessToken, profileImage, userDetailMap);
        }
    }

    public void setImageInPart() {

        if (setGender) {
            if (setDob) {
                if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
                    binding.progress.setIndeterminate(true);
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.btnSubmit.setClickable(false);
                    binding.rlCountry.setClickable(false);

                    updateUserProfile(accessToken,
                            Constants.userType(this),
                            String.valueOf(id), binding.tvDob.getText().toString(), gender,
                            maritalStatus, String.valueOf(stateID));
                }

            } else
                Toast.makeText(this, "Please Enter Date of Birth!", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Please Fill All Details!", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    private void updateProfilePicture(String accessToken, MultipartBody.Part profileImage,
                                      Map<String, RequestBody> userDetailMap) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.updateProfilePicture(accessToken, profileImage,
                userDetailMap);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call,
                                   Response<RegisterApi> response) {

                if (response.isSuccessful()) {
                    binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    signUpModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (message.equals("Profile updated successfully.")) {

                            if (signUpModel.getUserImagesModel().size() > 0) {
                                for (int i = 1; i <= signUpModel.getUserImagesModel().size(); i++) {
                                    imageList.set(i - 1, signUpModel.getUserImagesModel().get(i - 1)
                                            .getImageModel().getSmall());
                                    bigImageList.set(i - 1, signUpModel.getUserImagesModel().get(i - 1)
                                            .getImageModel().getBig());
                                }
                            }
                            imageAdapter.notifyDataSetChanged();
                            Toast.makeText(EditUserProfileActivity.this, "Profile picture updated successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(EditUserProfileActivity.this, "Upload Fails!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {
                        Toast.makeText(EditUserProfileActivity.this,
                                "" + response.errorBody(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                binding.progress.setVisibility(View.GONE);
                Toast.makeText(EditUserProfileActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateUserProfile(String access_token,
                                   String user_type, String country_id, String dob,
                                   String gender, String marital_status, String state_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.updateUserProfile(Constants.TIMEZONE, Constants.LOCALE,
                access_token, user_type, country_id, dob, gender, marital_status, state_id);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                binding.progress.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;

                        Toast.makeText(EditUserProfileActivity.this, "Profile Updated Successfully!",
                                Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditUserProfileActivity.this, HomeUserActivity.class));
                        finish();

                    } else

                        Toast.makeText(EditUserProfileActivity.this, "Registration Fails",
                                Toast.LENGTH_LONG).show();

                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.btnSubmit.setClickable(true);
                    binding.rlCountry.setClickable(true);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(EditUserProfileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(EditUserProfileActivity.this, new Gson().fromJson
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
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
                binding.rlCountry.setClickable(true);

                Toast.makeText(EditUserProfileActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}