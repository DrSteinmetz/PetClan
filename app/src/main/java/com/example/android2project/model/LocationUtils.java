package com.example.android2project.model;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.android2project.view.MainActivity;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Locale;

public class LocationUtils extends BroadcastReceiver {
    private static LocationUtils locationUtils;


    private Activity mActivity;


    private Handler mHandler;
    private Geocoder mGeoCoder;
    private LocationCallback mLocationCallback;

    private static Address mAddress;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public interface LocationDetected {
        void onLocationChange(Address address, Advertisement advertisement);
    }

    private LocationDetected locationListener;

    public void setLocationListener(LocationDetected locationListener) {
        this.locationListener = locationListener;
    }


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
        mHandler = new Handler();
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

    public boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) mActivity.getSystemService(mActivity.LOCATION_SERVICE);
            Log.d(TAG, "isLocationEnabled: " + lm.isLocationEnabled());
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(mActivity.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            Log.d(TAG, "isLocationEnabled: " + mode);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    public void getGeoPointFromCity(final Advertisement advertisement) {

        final Address[] address = new Address[1];
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    address[0] = mGeoCoder.getFromLocationName(advertisement.getLocation(), 1).get(0);
                    if (address[0] != null) {

                        if (locationListener != null) {

                            locationListener.onLocationChange(address[0], advertisement);
                        }
                        mHandler.removeCallbacks(this);
                    } else {
                        mHandler.postDelayed(this, 100);
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        });

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsEnabled && networkEnabled) {
                startLocation();
                Log.d(TAG, "onReceive: gps enabled");
            } else {
                Snackbar.make(mActivity.findViewById(android.R.id.content), "Location is disabled", Snackbar.LENGTH_LONG).show();
                Log.d(TAG, "GPS is disabled");
            }
        }
    }

    public static int getDistance(GeoPoint other) {
        if(mAddress!=null) {
            double theDistance = (Math.sin(Math.toRadians(mAddress.getLatitude())) *
                    Math.sin(Math.toRadians(other.getLatitude())) +
                    Math.cos(Math.toRadians(mAddress.getLatitude())) *
                            Math.cos(Math.toRadians(other.getLatitude())) *
                            Math.cos(Math.toRadians(mAddress.getLongitude() - other.getLongitude())));
            return (int) (Math.toDegrees(Math.acos(theDistance) * 69.09) * 1.6);
        }
        return 0;
    }

    public static Address getAddress(){
        return mAddress;
    }
}
