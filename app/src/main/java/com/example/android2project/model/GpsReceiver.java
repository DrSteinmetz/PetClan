package com.example.android2project.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.LocationCallback;
public class GpsReceiver extends BroadcastReceiver {
    private static final String TAG = "GpsRecevier";

    private LocationCallBack locationCallBack;

    public interface LocationCallBack{
        void onLocationTriggered(boolean isLocationOn);
    }

    private static GpsReceiver gpsReceiver;


    public static GpsReceiver getInstance(LocationCallBack locationCallBack){
        if(gpsReceiver==null){
            gpsReceiver=new GpsReceiver(locationCallBack);
        }
        return gpsReceiver;
    }

    /**
     * initializes receiver with callback
     * @param iLocationCallBack Location callback
     */
    private  GpsReceiver(LocationCallBack iLocationCallBack){
        this.locationCallBack = iLocationCallBack;
    }

    /**
     * triggers on receiving external broadcast
     * @param context Context
     * @param intent Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(gpsEnabled && networkEnabled) {
//                if (snackbar != null) {
////                    snackbar.dismiss();
                if(locationCallBack!=null) {
                    locationCallBack.onLocationTriggered(true);
                }
//               }
                Log.d(TAG, "onReceive: gps enabled");
            } else {
                if (locationCallBack!=null) {
                    locationCallBack.onLocationTriggered(false);
                }
                //snackbar.show();
                Log.d(TAG, "GPS is disabled");
            }
        }
    }

            //TODO: method calls more than once a click happens.
}

