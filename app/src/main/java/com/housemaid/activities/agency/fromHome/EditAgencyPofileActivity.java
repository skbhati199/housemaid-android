package com.housemaid.activities.agency.fromHome;

import android.Manifest;
import android.app.Activity;
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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.adapter.ImageAdapter1;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityEditAgencyPofileBinding;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

public class EditAgencyPofileActivity extends BaseActivity implements View.OnClickListener {

    private ActivityEditAgencyPofileBinding binding;
    private SharedPreference sharedPreference;
    private int id;
    private String accessToken;
    private int stateID;
    private Dialog dialog;
    private SignUpModel signUpModel;
    private String userId;
    private ArrayList<Integer> imageIdList;
    private ArrayList<String> imageList;
    private ArrayList<String> bigImageList;
    private MultipartBody.Part profileImage;
    private Map<String, RequestBody> userDetailMap;
    private ImageAdapter1 imageAdapter;
    private int position;
    private FilePickUtils filePickUtils;
    private LifeCycleCallBackManager lifeCycleCallBackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_agency_pofile);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        filePickUtils = new FilePickUtils(this, onFileChoose);
        lifeCycleCallBackManager = filePickUtils.getCallBackManager();
        binding.toolbar.tvTitle.setText(R.string.edit_profile);
        sharedPreference = SharedPreference.getInstance(this);

        accessToken = sharedPreference.getString("signUp_token", "0");
        signUpModel = (SignUpModel) getIntent().getSerializableExtra("agencyProfile");
        userId = sharedPreference.getString("agency_Id", "");
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
                if (signUpModel.getUserImagesModel().size() > 0) {
                    for (int i = 1; i <= signUpModel.getUserImagesModel().size(); i++) {
                        imageIdList.set(i - 1, signUpModel.getUserImagesModel().get(i - 1).getId());
                    }
                }
            }
            setData();
        } else Toast.makeText(this, R.string.please_refresh, Toast.LENGTH_SHORT).show();

    }

    private void setData() {

        binding.etCompanyName.setText(signUpModel.getCompany_name());
        binding.etAutherizedPerson.setText(signUpModel.getAuthorised_person());
        binding.tvCountryName.setText(signUpModel.getCountry_name());
        id = signUpModel.getCountry_id();
        stateID = signUpModel.getState_id();
        binding.tvStateName.setText(signUpModel.getState_name());
        binding.etAddress.setText(signUpModel.getAddress());
        binding.etTaxAdministration.setText(signUpModel.getTax_administration());
        binding.etTaxNumber.setText(signUpModel.getTax_no());
        binding.etCompanyPhone.setText(signUpModel.getCompany_phone());
        if (!(signUpModel.getUserImagesModel().isEmpty()) && (signUpModel.getUserImagesModel().size() > 0)
                && !signUpModel.getUserImagesModel().get(0).getImageModel().getBig().isEmpty()) {
            for (int i = 1; i <= signUpModel.getUserImagesModel().size(); i++) {
                imageList.add(signUpModel.getUserImagesModel().get(i - 1).getImageModel().getSmall());
                bigImageList.add(signUpModel.getUserImagesModel().get(i - 1).getImageModel().getBig());
            }
            Picasso.get().load(signUpModel.getUserImagesModel().get(0).getImageModel().getBig())
                    .fit().error(R.drawable.avatar).into(binding.ivProfilePicBig);

        } else binding.ivProfilePicBig.setImageResource(R.drawable.user);

        imageAdapter = new ImageAdapter1(EditAgencyPofileActivity.this, imageList, bigImageList
                , binding, imageIdList);
        binding.rvImageList.setLayoutManager(new LinearLayoutManager(EditAgencyPofileActivity.this,
                RecyclerView.HORIZONTAL, false));
        binding.rvImageList.setAdapter(imageAdapter);


    }

    @Override
    public void initControls() {
        super.initControls();
        binding.ivChoosePhoto.setOnClickListener(this);
        binding.ivProfilePicBig.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.toolbar.ivBack.setOnClickListener(this);

        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivChoosePhoto:
                if (position <= bigImageList.size() - 1) {
                    openDialog();
                }
                break;
            case R.id.ivProfilePicBig:
                if (position <= bigImageList.size() - 1) {
                    openDialog();
                }
                break;

            case R.id.rlCountry:
                startActivityForResult(new Intent(this, CountryListActivity.class),
                        512);
                break;
            case R.id.rlState:
                if (binding.tvCountryName.getText().length() == 0) {
                    Toast.makeText(this, R.string.please_select_country, Toast.LENGTH_SHORT)
                            .show();
                } else startActivityForResult(new Intent(this, StateListActivity.class)
                        , 520);
                break;

            case R.id.btnGallery:
                openGallery();
                break;
            case R.id.btnCamera:
                openCamera();
                break;
            case R.id.btnAvatar:
                openAvatars();
                break;

            case R.id.btnSubmit:
                setImageInPart();
                break;

            case R.id.ivBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeAgencyActivity.class));
        finish();
        super.onBackPressed();
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return true;
        }
        return false;
    }

    public void updateList(int position, ArrayList<Integer> imageIdList) {
        this.position = position;
        this.imageIdList = imageIdList;
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
        dialog.dismiss();
        filePickUtils.requestImageCamera(CAMERA_PERMISSION, true, false);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (lifeCycleCallBackManager != null) {
            lifeCycleCallBackManager.onActivityResult(requestCode, resultCode, data);
        }

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

    public Uri getImageUri(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);

        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, "title",
                null);
        return Uri.parse(path);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    public void setImagePart(String fileUri) {

        try {
            File file = new File(fileUri);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            // MultipartBody.Part is used to send also the actual file name
            profileImage = MultipartBody.Part.createFormData("profile_image", file.getName(),
                    requestFile);
            userDetailMap = new HashMap<>();
            userDetailMap.put("image_id", createPartFromString(String
                    .valueOf(imageIdList.get(position))));
            userDetailMap.put("user_id", createPartFromString(userId));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
            binding.progress.setIndeterminate(true);
            binding.progress.setVisibility(View.VISIBLE);
            binding.btnSubmit.setClickable(false);

            updateProfilePicture(accessToken, profileImage, userDetailMap);
        }
    }

    public void setImageInPart() {

        if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
            binding.progress.setIndeterminate(true);
            binding.progress.setVisibility(View.VISIBLE);

            updateAgencyProfile(accessToken,
                    Constants.userType(this), String.valueOf(id),
                    binding.etCompanyName.getText().toString(),
                    binding.etAutherizedPerson.getText().toString(),
                    binding.etTaxAdministration.getText().toString().trim(),
                    binding.etTaxNumber.getText().toString().trim(),
                    binding.etCompanyPhone.getText().toString().trim(),
                    binding.etAddress.getText().toString().trim(),
                    String.valueOf(stateID));
        }
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

                        if (message.equals(getString(R.string.profile_updated_successfully))) {
                            if (signUpModel.getUserImagesModel().size() > 0) {
                                for (int i = 1; i <= signUpModel.getUserImagesModel().size(); i++) {
                                    imageList.set(i - 1, signUpModel.getUserImagesModel()
                                            .get(i - 1).getImageModel().getSmall());
                                    bigImageList.set(i - 1, signUpModel.getUserImagesModel().
                                            get(i - 1).getImageModel().getBig());
                                }
                            }
                            imageAdapter.notifyDataSetChanged();

                            Toast.makeText(EditAgencyPofileActivity.this, "Profile picture updated successfully",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(EditAgencyPofileActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(EditAgencyPofileActivity.this,
                                new Gson().fromJson(response.errorBody().string(),
                                        ErrorResponse.class).getMessage()
                                , Toast.LENGTH_LONG).show();
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
                Toast.makeText(EditAgencyPofileActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateAgencyProfile(String access_token,
                                     String user_type, String country_id, String company_name,
                                     String authorised_person, String tax_administration,
                                     String tax_no, String company_phone, String address,
                                     String state_id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.updateAgencyProfile(Constants.TIMEZONE,
                Constants.LOCALE, access_token, user_type, country_id, company_name, authorised_person,
                tax_administration, tax_no, company_phone, address, state_id);
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

                        Toast.makeText(EditAgencyPofileActivity.this,
                                getString(R.string.profile_updated_successfully), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditAgencyPofileActivity.this,
                                HomeAgencyActivity.class));
                        finish();

                    } else

                        Toast.makeText(EditAgencyPofileActivity.this, response.errorBody().toString()
                                , Toast.LENGTH_LONG).show();

                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.btnSubmit.setClickable(true);
                    binding.rlCountry.setClickable(true);
                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(EditAgencyPofileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(EditAgencyPofileActivity.this, ""
                                    + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            Log.d("TEST", "Error : " + response.errorBody().string()
                                    + "message : " + response.message());
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

                Toast.makeText(EditAgencyPofileActivity.this, "error "
                        + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}