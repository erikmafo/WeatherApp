package com.erikmafo.weatherapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * Created by erik on 10/18/15.
 */


public class LocationProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {


    interface Callback {

        void handleNewLocation(Location location);

    }

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String TAG = LocationProvider.class.getSimpleName();
    private final Activity mHostingActivity;
    private final GoogleApiClient mGoogleApiClient;

    private volatile Location mCurrentLocation;
    private volatile boolean mLocationReceived;


    public LocationProvider(Activity hostingActivity) {

        mHostingActivity = hostingActivity;

        mGoogleApiClient = new GoogleApiClient.Builder(mHostingActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API)
                .build();

        /*
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds */

    }


    public void connect() {

        int googleServiceStatus = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mHostingActivity);

        if (googleServiceStatus == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        } else {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    mHostingActivity, googleServiceStatus, 10);
            if (dialog != null) {
                dialog.show();
            }
        }
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    public void requestLocation(final Callback callback) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                synchronized (this) {

                    while (!mLocationReceived) {

                        Log.i(TAG, "waiting for location");

                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    callback.handleNewLocation(mCurrentLocation);
                }

            }
        }).start();


    }


    private void handleNewLocation(final Location location) {

        Log.i(TAG, "handleNewLocation");

        new Thread(new Runnable() {

            @Override
            public void run() {

                synchronized (this) {

                    mCurrentLocation = location;
                    mLocationReceived = true;
                    notifyAll();

                }

            }

        }).start();

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (lastLocation != null) {
            handleNewLocation(lastLocation);
        } else {

            // get
            //requestNewLocation();
            //LocationServices.FusedLocationApi.requestLocationUpdates()
            //LocationServices.FusedLocationApi.
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        mHostingActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }




}
