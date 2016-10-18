package fi.jyu.ln.luontonurkka;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by sinikka on 18.10.2016.'
 *
 */

public class LastKnownLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient apiClient;
    private android.content.Context context;
    private android.app.Activity activity;
    private android.location.Location lastLocation;

    /* LastKnownLocation Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    /**
     * Constructor
     * @param context Context
     * @param activity Activity which creates LastKnownLocation instance
     */
    public LastKnownLocation(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        // Create an instance of GoogleAPIClient to get device location.
        buildGoogleApiClient();
    }

    /**
     * Returns device's location
     * @return Last known location or null
     */
    public Location getLocation() {
        return lastLocation;
    }

    /**
     * Connects Google API Client
     */
    public void connectAPI() {
        apiClient.connect();
    }

    /**
     * Disconnects Google API Client
     */
    public void disconnectAPI() {
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    /**
     * Builds a GoogleAPIClient.
     */
    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when Google API Client is connected.
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Check for the permission to access coarse location
        if ( ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( activity, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }

        // Get last known location
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
    }

    /**
     * Connection to Google API Client was lost.
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        apiClient.connect();
    }

    /**
     * If connection to Google API Client fails (can't get location)
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
    }
}
