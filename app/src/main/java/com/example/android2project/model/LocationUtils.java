package com.example.android2project.model;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Locale;

public class LocationUtils {
    private static LocationUtils locationUtils;

    private Activity mActivity;

    private Address mAddress;

    private Handler mHandler;
    private Geocoder mGeoCoder;
    private LocationCallback mLocationCallback;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private MutableLiveData<Address> mLocationLiveData;

    private final int LOCATION_REQUEST_CODE = 1;
    private final int REQUEST_CHECK_SETTINGS = 2;

    private final String TAG = "LocationUtils";

    public static LocationUtils getInstance(final Activity activity) {
        if (locationUtils == null) {
            locationUtils = new LocationUtils(activity);
        }
        return locationUtils;
    }

    private LocationUtils(final Activity activity) {
        this.mActivity = activity;
        mGeoCoder = new Geocoder(activity, Locale.getDefault());
    }

    public MutableLiveData<Address> getLocationLiveData() {
        if (mLocationLiveData == null) {
            mLocationLiveData = new MutableLiveData<>();
        }
        return mLocationLiveData;
    }

    public void requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasLocationPermission = mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                mActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            } else {
                startLocation();
            }
        } else {
            startLocation();
        }
    }

    public void startLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(final LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mAddress = mGeoCoder.getFromLocation(locationResult.getLastLocation().getLatitude(),
                                                locationResult.getLastLocation().getLongitude(), 1).get(0);
                                if (mAddress != null) {
                                    if (mLocationLiveData != null) {
                                        mLocationLiveData.setValue(mAddress);
                                        mHandler.removeCallbacks(this);
                                        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };
            final LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(500);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            SettingsClient client = LocationServices.getSettingsClient(mActivity);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

            task.addOnSuccessListener(mActivity, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    if (mActivity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                    }
                }
            });

            task.addOnFailureListener(mActivity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location preferences are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            //TODO:make a custom window.
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(mActivity,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }
    }
    public boolean isLocationEnabled()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
            Log.d(TAG, "isLocationEnabled: "+ lm.isLocationEnabled());
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(mActivity.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            Log.d(TAG, "isLocationEnabled: "+ mode);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }
}
