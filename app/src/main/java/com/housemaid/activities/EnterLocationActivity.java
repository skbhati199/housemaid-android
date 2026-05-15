package com.housemaid.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
/*import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;*/
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.housemaid.R;
import com.housemaid.activities.agency.fromHome.HomeAgencyActivity;
import com.housemaid.activities.agency.fromHome.MyMaidsActivity;
import com.housemaid.activities.maid.fromHome.activity.HomeForMaidActivity;
import com.housemaid.activities.user.fromHome.HomeUserActivity;
import com.housemaid.constants.Constants;
import com.housemaid.databinding.ActivityEnterLocationBinding;
import com.housemaid.model.response.RegisterApi;
import com.housemaid.rest.ApiClient;
import com.housemaid.rest.ApiInterface;
import com.housemaid.utils.GPSTracker;
import com.housemaid.utils.SharedPreference;
import com.housemaid.utils.ValidationUtils;
import com.imagepicker.pdfpicker.Constant;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// AQuery removed

public class EnterLocationActivity extends BaseActivity implements View.OnClickListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private ActivityEnterLocationBinding binding;
    private GPSTracker gpsTracker;
    private double lati;
    private double longi;
    private String currentLocation = "";
    private String accessToken;
    private SharedPreference sharedPreference;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 10;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int noLogin;
    private GoogleMap myMap;
    private LatLng originLatLng;
    private PlacesClient placesClient;
    private GoogleApiClient mGoogleApiClient;
    private Geocoder geocoder;
    private List<Address> addresses;
    private int maid_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enter_location);

        String key = "AIzaSyCu4hfpELKpWjG-AQR-LrmQYK3ytiW9pXs";
        // Initialize Places.
        //Places.initialize(getApplicationContext(), key);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        // Create a new Places client instance.
        //placesClient = Places.createClient(this);


        init();
        initControls();
    }

    @Override
    public void init() {
        super.init();
        gpsTracker = new GPSTracker(this);
        sharedPreference = SharedPreference.getInstance(this);
        accessToken = sharedPreference.getString("signUp_token", "0");
        noLogin = getIntent().getIntExtra("nologin", 0);
        maid_id = getIntent().getIntExtra("maid_id", 0);

        binding.toolbar.ivBack.setVisibility(View.GONE);
        binding.toolbar.tvTitle.setText(R.string.set_location);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                .ACCESS_FINE_LOCATION}, 1);
        geocoder = new Geocoder(this, Locale.getDefault());

        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        /*myLocation();*/
    }

    @Override
    public void initControls() {
        super.initControls();

        binding.tvSearchLocation.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvSearchLocation) {

                openAutoComplePicker();
                
            
} else if (v.getId() == R.id.btnSubmit) {



                if (!currentLocation.isEmpty()) {
                    sharedPreference.putString("location", currentLocation);
                    binding.progress.setVisibility(View.VISIBLE);
                    binding.btnSubmit.setClickable(false);
                    if (noLogin == 1) {
                        Intent intent = new Intent(EnterLocationActivity.this, HomeActivity.class);
                        intent.putExtra("latitude", lati);
                        intent.putExtra("longitude", longi);
                        intent.putExtra("location", currentLocation);
                        startActivity(intent);
                    } else {
                        updateLocation();

                    }
                } else
                    Toast.makeText(this, R.string.please_enter_location, Toast.LENGTH_SHORT).show();
                
        
}

    }

    private void openAutoComplePicker() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        /*try {
            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setFilter(autocompleteFilter)
                    .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                String address = (String) place.getAddress();
                if (address != null && address.contains(",")) {
                    String[] array = address.split(",", 3);
                    try {
                        address = array[0] + "," + array[1] + "," + array[2];
                    } catch (Exception ignored) {

                    }
                }
                ValidationUtils.hideSoftKeyboard(this);
                binding.tvSearchLocation.setText(address);
                //latLngToAddress(this, lati, longi);
                currentLocation = address;

                LatLng latLng = place.getLatLng();
                lati = latLng.latitude;
                longi = latLng.longitude;
                //  latLngToAddress(this, lati, longi);
                originLatLng = new LatLng(lati, longi);
                currentLocation = address;
                sharedPreference.putString("location", currentLocation);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                myMap.setMyLocationEnabled(true);

                myMap.clear();
                //Move the camera instantly to hamburg with a zoom of 15.
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 15));
                // Zoom in, animating the camera.
                //        myMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                myMap.animateCamera(CameraUpdateFactory.newLatLng(originLatLng));
                myMap.addMarker(new MarkerOptions().position(originLatLng).title("Current Location"));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        /*if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                *//*isChekPickup = 1;*//*
                Place place = PlaceAutocomplete.getPlace(this, data);

                String address = (String) place.getAddress();
                if (address.contains(",")) {
                    String[] array = address.split(",", 3);
                    try {
                        address = array[0] + "," + array[1] + "," + array[2];
                    } catch (Exception ignored) {

                    }
                }

                ValidationUtils.hideSoftKeyboard(this);
                binding.tvSearchLocation.setText(address);
                //latLngToAddress(this, lati, longi);
                currentLocation = address;
                LatLng latLng = place.getLatLng();
                lati = latLng.latitude;
                longi = latLng.longitude;
              //  latLngToAddress(this, lati, longi);
                originLatLng = new LatLng(lati, longi);
                currentLocation = address;
                sharedPreference.putString("location", currentLocation);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                myMap.setMyLocationEnabled(true);

                myMap.clear();
                //Move the camera instantly to hamburg with a zoom of 15.
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 15));
                // Zoom in, animating the camera.
                //        myMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
                myMap.animateCamera(CameraUpdateFactory.newLatLng(originLatLng));
                myMap.addMarker(new MarkerOptions().position(originLatLng).title("Current Location"));


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            } else if (resultCode == RESULT_CANCELED) {
            }
        }*/
    }

   /* public static String latLngToAddress(Context context, double latitude, double longitude) {
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
                String city = locality;
                address = featureName + ", " + subLocality + ", " + locality + "- " + postalCode;
                Log.d("house", "Address : " + address);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("house", "Exception while converting LatLng to address : " + e.getMessage());
        }
        return address;
    }*/

    private void updateLocation() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<RegisterApi> call;
        if (noLogin == 2) call = apiService.agencyAddMaid6(Constants.TIMEZONE, accessToken,
                String.valueOf(maid_id), "6", currentLocation, lati, longi);
        else call = apiService.updateLocation(Constants.TIMEZONE,
                Constants.LOCALE, accessToken, currentLocation, lati, longi);

        call.enqueue(new Callback<RegisterApi>() {

            @Override
            public void onResponse(Call<RegisterApi> call, Response<RegisterApi> response) {
                if (response.isSuccessful()) {

                    binding.progress.setVisibility(View.GONE);
                    binding.btnSubmit.setClickable(true);

                    RegisterApi registerApi = response.body();
                    String message = registerApi.message;

                    if (noLogin == 2) {
                        Intent intent = new Intent(EnterLocationActivity.this, MyMaidsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        if (sharedPreference.getInteger("entry_key", 0) == 1) {
                            startActivity(new Intent(EnterLocationActivity.this, HomeForMaidActivity.class));
                            finishAffinity();
                        }
                        if (sharedPreference.getInteger("entry_key", 0) == 2) {
                            startActivity(new Intent(EnterLocationActivity.this, HomeUserActivity.class));
                            finishAffinity();
                        }
                        if (sharedPreference.getInteger("entry_key", 0) == 3) {
                            startActivity(new Intent(EnterLocationActivity.this, HomeAgencyActivity.class));
                            finishAffinity();
                        }
                    }

                } else {
                    try {
                        binding.progress.setVisibility(View.GONE);

                        binding.btnSubmit.setClickable(true);

                        Toast.makeText(EnterLocationActivity.this,
                                "" + response.errorBody(), Toast.LENGTH_SHORT).show();
                        Log.d("TEST", "Error : " + response.errorBody().string() +
                                "message : " + response.message());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegisterApi> call, Throwable t) {

                binding.progress.setVisibility(View.GONE);

                binding.btnSubmit.setClickable(true);
                Toast.makeText(EnterLocationActivity.this, "Error: ", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.myMap = googleMap;
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        Log.d("TAG", "" + gpsTracker.getLatitude() + " " + gpsTracker.getLongitude());

        originLatLng = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        lati = gpsTracker.getLatitude();
        longi = gpsTracker.getLongitude();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);
/*
        final BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.color.colorPrimary);
*/
        myMap.clear();
        //Move the camera instantly to hamburg with a zoom of 15.
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 15));
        // Zoom in, animating the camera.
        //        myMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(originLatLng));
        myMap.addMarker(new MarkerOptions().position(originLatLng).title("Current Location"));


        myMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                LatLng mCenterLatLong = cameraPosition.target;

                myMap.clear();
                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    lati = mCenterLatLong.latitude;
                    longi = mCenterLatLong.longitude;
//                    startIntentService(mLocation);


                    try {
                        addresses = geocoder.getFromLocation(mCenterLatLong.latitude, mCenterLatLong.longitude, 1);
                        String address = addresses.get(0).getAddressLine(0);
                        binding.Address.setText(address);
                        sharedPreference.putString("location", address);
                        currentLocation = address;
                        binding.tvSearchLocation.setText("");
                    } catch (Exception e) {
                        e.getStackTrace();
                    }


                } catch (Exception e)

                {
                    e.printStackTrace();
                }

            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(EnterLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EnterLocationActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            changeMap(mLastLocation);
            Log.d("TAG", "ON connected");


        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null)
                changeMap(location);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(EnterLocationActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(EnterLocationActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    private void changeMap(Location location) {

        Log.d("TAG", "Reaching map" + myMap);


        if (ActivityCompat.checkSelfPermission(EnterLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EnterLocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (myMap != null) {
            myMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).tilt(70).build();

            myMap.setMyLocationEnabled(true);
            myMap.getUiSettings().setMyLocationButtonEnabled(true);
            myMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            //startIntentService(location);

        } else {
            Toast.makeText(EnterLocationActivity.this,
                    R.string.sorry_unable_to_create_map, Toast.LENGTH_SHORT)
                    .show();
        }

    }
}