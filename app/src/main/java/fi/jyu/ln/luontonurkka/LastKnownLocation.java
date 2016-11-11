package fi.jyu.ln.luontonurkka;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * LastKnownLocation creates an instance of GoogleApiClient, connects and
 * disconnects the client and fetches the device's last known location. To use
 * in Activity, create LastKnownLocation object in onCreate() with parameters
 * this.getApplicationContext() and (Activity) this. Then, use connectAPI() in
 * onStart() and disconnectAPI() in onStop().
 *
 * TODO The above instructions need verifying.
 *
 * Created by Sinikka Siironen on 18.10.2016.
 */

public class LastKnownLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient apiClient;
    private android.content.Context context;
    private android.app.Activity activity;
    private android.location.Location lastLocation;
    private Runnable locationChanged;

    /* LastKnownLocation Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 22;

    private static final int GPS_MIN_UPDATE_TIME_MILLIS = 5000;
    private static final float GPS_MIN_DIST_METERS = 100;

    /**
     * Constructor
     * @param context Activity's context
     * @param activity Activity which creates LastKnownLocation instance
     */
    public LastKnownLocation(Context context, Activity activity, Runnable locationChanged) {
        this.context = context;
        this.activity = activity;
        this.locationChanged = locationChanged;
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

        // Check for the permission to access fine location
        if ( ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( activity, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSION_ACCESS_FINE_LOCATION );
        }

        // Get last known location
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        LocationManager lm = (LocationManager)this.activity.getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_UPDATE_TIME_MILLIS, GPS_MIN_DIST_METERS, this);
        locationChanged.run();
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

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        locationChanged.run();
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
}