package com.erikmafo.weatherapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationFragment.class.getSimpleName();
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private Geocoder mGeocoder;
    private TextView mLocationView;

    public interface OnLocationUpdateListener {
        void onLocationUpdate(Location location);
    }

    private OnLocationUpdateListener mListener;
    private GoogleApiClient mGoogleApiClient;


    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API)
                .build();

        mGeocoder = new Geocoder(getActivity(), Locale.getDefault());

    }


    @Override
    public void onResume() {
        super.onResume();

        int googleServiceStatus = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());

        if (googleServiceStatus == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        } else {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                    getActivity(), googleServiceStatus, 10);
            if (dialog != null) {
                dialog.show();
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_location, container, false);
        mLocationView = (TextView) view.findViewById(R.id.locationLabel);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLocationUpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLocationUpdateListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    private void handleNewLocation(Location location) {
        mLocationView.setText(getLocality(location));
        ((OnLocationUpdateListener) getActivity()).onLocationUpdate(location);
    }

    private String getLocality(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        List<Address> addresses = null;

        try {
            addresses = mGeocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            return addresses.get(0).getLocality();
        }

        return "Unknown Location Name";

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
                        getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
}
