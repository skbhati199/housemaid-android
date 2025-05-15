package com.housemaid.activities.agency.beforeHome;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.housemaid.R;
import com.housemaid.activities.BaseActivity;
import com.housemaid.activities.EnterLocationActivity;
import com.housemaid.activities.SelectionActivity;
import com.housemaid.activities.dataList.CountryListActivity;
import com.housemaid.activities.dataList.StateListActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityCompleteProfileAgencyBinding;
import com.housemaid.model.SignUpModel;
import com.housemaid.model.response.ErrorResponse;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteProfileAgency extends BaseActivity implements View.OnClickListener {

    private ActivityCompleteProfileAgencyBinding binding;
    private SharedPreference sharedPreference;
    private String imagePath;
    private int id;
    private MultipartBody.Part profileImage;
    private Map<String, RequestBody> userDetailMap;
    private String accessToken;
    private int stateID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_profile_agency);
        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        binding.toolbar.ivBack.setVisibility(View.GONE);
        binding.toolbar.tvTitle.setText(R.string.complete_your_profile);

        sharedPreference = SharedPreference.getInstance(this);
        imagePath = getIntent().getStringExtra("image_path");
        accessToken = sharedPreference.getString("signUp_token", "0");
    }

    @Override
    public void initControls() {
        super.initControls();
        binding.btnSubmit.setOnClickListener(this);
        binding.rlCountry.setOnClickListener(this);
        binding.rlState.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlCountry:
                startActivityForResult(new Intent(this, CountryListActivity.class),
                        512);
                break;
            case R.id.rlState:
                if (binding.tvCountryName.getText().length() == 0) {
                    Toast.makeText(this, R.string.please_select_country, Toast.LENGTH_SHORT).show();
                } else startActivityForResult(new Intent(this, StateListActivity.class),
                        520);
                break;
            case R.id.btnSubmit:
                if (binding.etCompanyName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_company_name, Toast.LENGTH_SHORT).show();
                } else if (binding.etAutherizedPerson.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_authorized_person_name, Toast.LENGTH_SHORT).show();
                } else if (binding.tvCountry.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_select_country_name, Toast.LENGTH_SHORT).show();
                } else if (binding.tvState.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_select_state, Toast.LENGTH_SHORT).show();
                } else if (binding.etAddress.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_address, Toast.LENGTH_SHORT).show();
                } else if (binding.etTaxAdministration.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_tax_administration, Toast.LENGTH_SHORT).show();
                } else if (binding.etTaxNumber.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_tax_number, Toast.LENGTH_SHORT).show();
                } else if (binding.etCompanyPhone.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, R.string.please_enter_company_phone, Toast.LENGTH_SHORT).show();
                } else {
                    setImageInPart();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 512 && resultCode == Activity.RESULT_OK) {
            String countryName = data.getStringExtra("country");
            id = data.getIntExtra("id", 0);
            binding.tvCountryName.setText(countryName);
        }
        if (requestCode == 520 && resultCode == Activity.RESULT_OK) {
            String stateName = data.getStringExtra("state");
            binding.tvStateName.setText(stateName);
            stateID = data.getIntExtra("state_id", 0);
        }
    }

    public void setImageInPart() {

        try {
            File file = new File(imagePath);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            profileImage = MultipartBody.Part.createFormData("profile_image[]", file.getName(),
                    requestFile);

            userDetailMap = new HashMap<>();
            userDetailMap.put("user_type", createPartFromString(Constants.userType(this)));
            userDetailMap.put("country_id", createPartFromString(String.valueOf(id)));
            userDetailMap.put("company_name", createPartFromString(binding.etCompanyName.getText()
                    .toString()));
            userDetailMap.put("authorised_person", createPartFromString(binding.etAutherizedPerson
                    .getText().toString()));
            userDetailMap.put("tax_administration", createPartFromString(binding.etTaxAdministration
                    .getText().toString().trim()));
            userDetailMap.put("tax_no", createPartFromString(binding.etTaxNumber.getText().toString()
                    .trim()));

            userDetailMap.put("company_phone", createPartFromString(binding.etCompanyPhone.getText()
                    .toString().trim()));
            userDetailMap.put("state_id", createPartFromString(String.valueOf(stateID)));
            userDetailMap.put("address", createPartFromString(binding.etAddress.getText().toString()
                    .trim()));
        } catch (Exception e) {
            Toast.makeText(this, R.string.please_choose_your_profile_picture,
                    Toast.LENGTH_SHORT).show();
        }
        if (ValidationUtils.isOnline(binding.relativeLayout, this)) {
            binding.progress.setIndeterminate(true);
            binding.progress.setVisibility(View.VISIBLE);
            registerProfileToServer(accessToken,
                    profileImage, userDetailMap);
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    private void registerProfileToServer(String access_token,
                                         MultipartBody.Part profileImage,
                                         Map<String, RequestBody> userDetailMap) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call = apiService.completeProfileDetails(Constants.TIMEZONE, Constants.LOCALE,
                access_token, profileImage, userDetailMap);
        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {
                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;
                    if (message != null) {

                        RegisterApi registerApi1 = response.body();
                        SignUpModel signUpModel = registerApi1.signUpModel;
                        sharedPreference.putString("complete_profile", signUpModel.getComplete_profile());
                        sharedPreference.putBoolean("session", true);
                        Intent intent = new Intent(CompleteProfileAgency.this,
                                EnterLocationActivity.class);
                        startActivity(intent);

                    } else {
                        binding.progress.setVisibility(View.GONE);
                        binding.btnSubmit.setClickable(true);
                        Toast.makeText(CompleteProfileAgency.this, response.errorBody().toString()
                                , Toast.LENGTH_LONG).show();
                    }
                } else {
                    binding.progress.setVisibility(View.GONE);
                    binding.btnSubmit.setClickable(true);
                    try {
                        if (response.code() == 401) {
                            binding.progress.setVisibility(View.GONE);
                            sharedPreference.deletePreference();
                            Intent signInIntent = new Intent(CompleteProfileAgency.this,
                                    SelectionActivity.class);
                            startActivity(signInIntent);
                            finishAffinity();
                        } else {
                            Toast.makeText(CompleteProfileAgency.this, new Gson().fromJson
                                    (response.errorBody().string(), ErrorResponse.class)
                                    .getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CompleteProfileAgency.this, "error " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }


   /* public static String latLngToAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String address = null;
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            //address = addresses.get(0).getAddressLine(0);
            // If any additional address line present than only, check with max available address
            // lines by getMaxAddressLineIndex()
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
    }*/

}
