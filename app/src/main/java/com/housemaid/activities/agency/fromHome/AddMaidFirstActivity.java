package com.housemaid.activities.agency.fromHome;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityAddMaidFirstBinding;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.imagepicker.FilePickUtils.CAMERA_PERMISSION;
import static com.imagepicker.FilePickUtils.STORAGE_PERMISSION_IMAGE;

public class AddMaidFirstActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddMaidFirstBinding binding;
    private SharedPreference sharedPreference;
    private Dialog dialog;
    private String imagePath;
    private boolean state = false;
    private MultipartBody.Part profileImage;
    private Map<String, RequestBody> userDetailMap;
    private String accessToken;
    private FilePickUtils filePickUtils;
    private LifeCycleCallBackManager lifeCycleCallBackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_maid_first);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        filePickUtils = new FilePickUtils(this, onFileChoose);
        lifeCycleCallBackManager = filePickUtils.getCallBackManager();
        binding.toolbar.tvTitle.setText(R.string.add_maid_profile);
        binding.toolbar.ivBack.setVisibility(View.GONE);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.ivChoosePhoto.setOnClickListener(this);
        binding.ivProfilePicBig.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivChoosePhoto:
                openDialog();
                break;

            case R.id.btnCancel:
                onBackPressed();
                break;

            case R.id.ivProfilePicBig:
                openDialog();
                break;

            case R.id.btnNext:
                if (imagePath != null) {
                    if (ValidationUtils.userNameEmpty(binding.etUserName.getText().toString().trim(), this)
                            && ValidationUtils.mobileMatch(binding.etMobileNumber.getText().toString().trim(), this)
                            && ValidationUtils.emailMatch(binding.etEmailID.getText().toString(), this)) {
                        setImageInPart();

                    }

                } else Toast.makeText(this, R.string.please_select_profile_picture,
                        Toast.LENGTH_SHORT).show();
                break;
            //Choose Image Dialog Clicks
            case R.id.btnGallery:
                openGallery();
                break;

            case R.id.btnCamera:
                if (checkPermissionForCamera()) {
                    openCamera();
                } else {
                    try {
                        requestPermissionForCamera();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnAvatar:
                openAvatars();
                break;
        }
    }

    public void requestPermissionForCamera() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    Constants.CAMERA_PERMISSION_REQUEST_CODE);

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public boolean checkPermissionForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.CAMERA);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void openDialog() {
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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    Constants.CAMERA_PERMISSION_REQUEST_CODE);

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void openGallery() {
        dialog.dismiss();
        filePickUtils.requestImageGallery(STORAGE_PERMISSION_IMAGE, true, true);

    }

    private void openCamera() {
        filePickUtils.requestImageCamera(CAMERA_PERMISSION, true, true);
        dialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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
            binding.tvUpload.setVisibility(View.GONE);
            imagePath = fileUri;
        }

    };

    private void openAvatars() {
        binding.ivProfilePicBig.setImageResource(R.drawable.men_icon);
        binding.ivProfilePicBig.setScaleType(ImageView.ScaleType.FIT_XY);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.men_icon);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, "avatar.PNG");

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.tvUpload.setVisibility(View.GONE);
        imagePath = file.getAbsolutePath();
        state = true;
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    public void setImageInPart() {

        try {
            File file = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

            // MultipartBody.Part is used to send also the actual file name
            profileImage = MultipartBody.Part.createFormData("profile_image[]", file.getName(),
                    requestFile);

            userDetailMap = new HashMap<>();
            userDetailMap.put("user_name", createPartFromString(binding.etUserName.getText()
                    .toString().trim()));
            userDetailMap.put("country_code", createPartFromString(binding.countryCodePicker
                    .getDefaultCountryCodeWithPlus()));
            userDetailMap.put("mobile", createPartFromString(binding.etMobileNumber.getText()
                    .toString().trim()));
            userDetailMap.put("device_token", createPartFromString(Constants.REFRESHTOKEN));
            userDetailMap.put("device_type", createPartFromString(Constants.userType(this)));
            userDetailMap.put("email", createPartFromString(binding.etEmailID.getText().toString()
                    .trim()));
            userDetailMap.put("step_for_maid_profile", createPartFromString("1"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
            binding.progress.setIndeterminate(true);
            binding.progress.setVisibility(View.VISIBLE);
            binding.btnNext.setClickable(false);

            registerProfileToServer(accessToken,
                    profileImage, userDetailMap);
        }
    }

    private void registerProfileToServer(String access_token,
                                         MultipartBody.Part profileImage,
                                         Map<String, RequestBody> userDetailMap) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.agencyAddMaidFirst(Constants.TIMEZONE, access_token,
                profileImage, userDetailMap);
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


                        startActivity(new Intent(AddMaidFirstActivity.this,
                                AddMaidActivity2.class).putExtra("add_maid_id", signUpModel
                                .getId()));
                        finish();

                    } else

                        Toast.makeText(AddMaidFirstActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();

                } else {
                    binding.progress.setVisibility(View.GONE);

                    binding.btnNext.setClickable(true);
                    try {
                        if (response.code() == 400) {
                            binding.progress.setVisibility(View.GONE);

                            Toast.makeText(AddMaidFirstActivity.this,
                                    new Gson().fromJson(response.errorBody().string(),
                                            ErrorResponse.class).getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(AddMaidFirstActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(AddMaidFirstActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
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
                binding.btnNext.setClickable(true);

                Toast.makeText(AddMaidFirstActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}