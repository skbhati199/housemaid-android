package com.housemaid.activities.user.fromHome;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.CompleteProfileActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.agency.beforeHome.CompleteProfileAgency;
import com.housemaid.activities.maid.beforeHome.CompleteProfileMaid1;
import com.housemaid.activities.maid.fromHome.activity.EditMaidPofileActivity;
import com.housemaid.activities.user.beforeHome.CompleteProfileNamesActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityAddListingPhotoBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.imagepicker.FilePickUtils;
import com.imagepicker.LifeCycleCallBackManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

public class AddListingPhotoActivity extends BaseActivity implements View.OnClickListener  {

    ActivityAddListingPhotoBinding binding ;
    private SharedPreference sharedPreference;
    private Dialog dialog;
    private String imagePath = "";
    private static String nopath = "Select Video Only";
    private  boolean state = false;
    private File file;
    private FilePickUtils filePickUtils;
    private LifeCycleCallBackManager lifeCycleCallBackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_add_listing_photo);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_listing_photo);
        init();
        initControls();
    }
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return nopath;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return nopath;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    @Override
    public void init() {
        super.init();

        filePickUtils = new FilePickUtils(this, onFileChoose);
        lifeCycleCallBackManager = filePickUtils.getCallBackManager();

        //binding.toolbar.tvTitle.setText(R.string.complete_your_profile);
        //binding.toolbar.ivBack.setVisibility(View.GONE);
        sharedPreference = SharedPreference.getInstance(this);
        //accessToken = sharedPreference.getString("signUp_token", "0");
        //userId = sharedPreference.getString("maid_id", "");
        int fromPage = getIntent().getIntExtra("edit_maid_pic", 0);

        /*if (fromPage == 1) {
            binding.btnNext.setVisibility(View.GONE);
            binding.btnNext.setVisibility(View.VISIBLE);
            maidDetailModel = (SignUpModel) getIntent().getSerializableExtra("maidDetail");
            imageId = maidDetailModel.getUserImagesModel().get(0).getId();
        }*/
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.ivChoosePhoto.setOnClickListener(this);
        binding.ivProfilePic.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        //binding.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivChoosePhoto) {

                openDialog();
                //Toast.makeText ( this, "fdgdfgfgfdgfdg", Toast.LENGTH_SHORT ).show ();
                

            
} else if (v.getId() == R.id.ivProfilePic) {

                openDialog();
                

            
} else if (v.getId() == R.id.btnNext) {

                openNextActivity();
                

           /* 
} else if (v.getId() == R.id.btnSubmit) {

                if (imagePath != null) {
                    setImagePart();
                } else Toast.makeText(this, R.string.please_select_profile_picture,
                        Toast.LENGTH_SHORT).show();
                */
            //Choose Image Dialog Clicks
            
} else if (v.getId() == R.id.btnGallery) {

                openGallery();
                

            
} else if (v.getId() == R.id.btnCamera) {

                if (checkPermissionForCamera()) {
                    openCamera();
                } else {
                    try {
                        requestPermissionForCamera();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                

            
} else if (v.getId() == R.id.btnAvatar) {

                openAvatars();
                
        
}
    }

    private void openNextActivity() {
        if (state) {
            //setImageInPart();
            Intent intent = new Intent(AddListingPhotoActivity.this, AddListingFirstPageActivity.class);
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
        } else
            Toast.makeText(this, "Please select profile picture.", Toast.LENGTH_SHORT).show();
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        //CHECKING FOR LOLIPOP
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return true;
        }
        return false;
    }

    public boolean checkPermissionForCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.CAMERA);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return true;
        }
        return false;
    }

    private void openDialog() {
/*
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_image_layout);*/
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
            binding.ivProfilePic.setImageURI(Uri.fromFile(new File(fileUri)));
            imagePath = fileUri;
            state = true;
        }
    };

    private void openAvatars() {
        binding.ivProfilePic.setImageResource(R.drawable.men_icon);
        binding.ivProfilePic.setScaleType(ImageView.ScaleType.FIT_XY);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.men_icon);
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        File file = new File(extStorageDirectory, "avatar.PNG");

        try {
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public Uri getImageUri(Bitmap src) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.PNG, 50, os);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), src, "title",
                null);
        return Uri.parse(path);
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }


    public void setImageInPart() {

        try {
            file = new File(imagePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part profileImage = MultipartBody.Part.createFormData("profile_image[]", file.getName(),
                    requestFile);
           /* userDetailMap = new HashMap<>();
            userDetailMap.put("user_type", createPartFromString(Constants.userType(this)));
            userDetailMap.put("step_for_maid_profile", createPartFromString("1"));*/
            registerProfileToServer(Constants.TIMEZONE, Constants.LOCALE,sharedPreference.getString("signUp_token", ""),profileImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ValidationUtils.isOnline(binding.constraintLayout, this)) {
            /*binding.progress.setIndeterminate(true);
            binding.progress.setVisibility(View.VISIBLE);
            binding.btnNext.setClickable(false);

            registerProfileToServer(Constants.TIMEZONE, Constants.LOCALE, accessToken,
                    profileImage);*/
        }
    }

    private void registerProfileToServer(String timezone, String locale, String access_token,
                                         MultipartBody.Part profileImage) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.postJobByUserImage(timezone, locale, access_token,
                profileImage);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                /*binding.progress.setVisibility(View.GONE);
                binding.btnNext.setClickable(true);
                binding.btnSubmit.setClickable(true);
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("complete_profile",
                                signUpModel.getComplete_profile());
                        sharedPreference.putInteger("step_for_maid_profile",
                                signUpModel.getStep_for_maid_profile());
                        startActivity(new Intent(CompleteProfileActivity.this,
                                CompleteProfileMaid1.class));

                    } else

                        Toast.makeText(CompleteProfileActivity.this, response.errorBody().toString(),
                                Toast.LENGTH_LONG).show();

                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.btnSubmit.setClickable(true);
                    binding.btnNext.setClickable(true);
                    try {
                        if (response.code() == 401) {
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(CompleteProfileActivity.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(CompleteProfileActivity.this, new Gson().fromJson
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("TEST", "Error : " + response.errorBody().string() +
                                    "message : " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/
                if (response.isSuccessful()){
                    startActivity(new Intent(AddListingPhotoActivity.this, AddListingFirstPageActivity.class));
                }else{
                    Toast.makeText(AddListingPhotoActivity.this, response.errorBody().toString(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                /*binding.progress.setVisibility(View.GONE);
                binding.btnSubmit.setClickable(true);
                binding.btnNext.setClickable(true);

                Toast.makeText(CompleteProfileActivity.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();*/

            }
        });
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
                    /*binding.progress.setVisibility(View.GONE);
                    RegisterApi registerApi = response.body();
                    maidDetailModel = registerApi.getSignUpModel();
                    String message = registerApi.message;
                    if (message != null) {

                        if (message.equals(R.string.profile_updated_successfully)) {
                            binding.progress.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(CompleteProfileActivity.this,
                                    EditMaidPofileActivity.class);
                            intent.putExtra("maidDetail", maidDetailModel);
                            startActivity(intent);
                        }
                    } else {

                        Toast.makeText(CompleteProfileActivity.this, "Upload Fails!",
                                Toast.LENGTH_LONG).show();
                    }*/
                } else {
                    /*binding.progress.setVisibility(View.GONE);
                    try {

                        Toast.makeText(CompleteProfileActivity.this, new Gson().fromJson
                                (response.errorBody().string(), ErrorResponse.class)
                                .getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string()
                                + "message : " + response.message());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {
                /*binding.progress.setVisibility(View.GONE);
                Toast.makeText(CompleteProfileActivity.this, "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();*/

            }
        });
    }
}
