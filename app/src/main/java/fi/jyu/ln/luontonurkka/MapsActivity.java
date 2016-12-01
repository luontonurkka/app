package fi.jyu.ln.luontonurkka;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, GoogleMap.OnMarkerClickListener {
    private GoogleMap map;
    private Marker locationMarker;
    private GoogleApiClient apiClient;

    private static LatLng clickedPoint;
    private static boolean myLocationEnabled;

    /**
     * Intent extras
     */
    protected static final String ARG_NORTH_COORD = "north_coord";
    protected static final String ARG_EAST_COORD = "earth_coord";

    protected static final String FROM_MAP_VIEW = "from_map_view";

    /* Location Permission Constant */
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 22;

    /* Constant used in the location settings dialog. */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * Resolving error in connecting to Google API Client state
     */
    private boolean resolvingError;
    private static final int REQUEST_RESOLVE_ERROR = 77;

    /* Stores parameters for requests to the FusedLocationProviderApi. */
    protected LocationRequest locationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest locationSettingsRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //disable my location on default
        myLocationEnabled = false;

        //build and connect Google API client
        buildGoogleApiClient();

        if (!(apiClient.isConnected())) {
            apiClient.connect();
        }

        //build location settings request with city level accuracy (with PRIORITY_LOW_POWER)
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        //build LocationSettingsRequest
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Connect Google API Client when starting activity.
     */
    @Override
    protected void onStart() {
//        if (!(apiClient.isConnected())) {
//            apiClient.connect();
//        }
        super.onStart();
    }

    /**
     * Disconnect Google API Client when stopping activity.
     */
    @Override
    protected void onStop() {
        if (!(apiClient.isConnected())) {
            apiClient.disconnect();
        }
        super.onStop();
    }

    /**
     * Connect to Google API Client when resuming activity.
     */
    @Override
    public void onResume() {
//        if (!(apiClient.isConnected())) {
//            apiClient.connect();
//        }
        super.onResume();
    }

    /**
     * Disconnect Google API Client when pausing activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(this.getLocalClassName(), "Map ready.");

        map = googleMap;

        //Luontonurkka hq
//        loc = new LatLng(62.232436, 25.737582);
//        locationMarker = map.addMarker(new MarkerOptions().position(loc));

        //center map on Finland
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(65.551806, 26.305592), 5));
        map.setOnMapClickListener(this);
//        map.setOnInfoWindowClickListener(this);
        map.setOnMarkerClickListener(this);

        Log.i(this.getLocalClassName(), "Check the permission to use location.");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getLocalClassName(), "Request a permission to use location.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            Log.i(this.getLocalClassName(), "Permission to use location already granted.");
            //check location settings
            checkLocationSettings();
        }

        if (myLocationEnabled) {
            Log.i(this.getLocalClassName(), "My Location enabled.");
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        clickedPoint = point;

        map.clear();
        locationMarker = map.addMarker(new MarkerOptions()
                .position(point)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_show_species)));
//        locationMarker.showInfoWindow();
    }

    /**
     * Called when user clicks info window
     * @param marker Marker of the info window
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, TabbedListActivity.class);
        intent.putExtra(ARG_NORTH_COORD, clickedPoint.latitude);
        intent.putExtra(ARG_EAST_COORD, clickedPoint.longitude);
        intent.putExtra(FROM_MAP_VIEW, true);
        startActivity(intent);
    }

    /**
     * Called when user clicks marker
     * @param marker Marker clicked
     * @return Suppress the default behaviour (centering the map and opening an info window)
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(this, TabbedListActivity.class);
        intent.putExtra(ARG_NORTH_COORD, clickedPoint.latitude);
        intent.putExtra(ARG_EAST_COORD, clickedPoint.longitude);
        intent.putExtra(FROM_MAP_VIEW, true);
        startActivity(intent);
        return true;
    }

    /**
     * Builds a GoogleAPIClient.
     */
    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this.getApplicationContext())
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
        Log.i(this.getLocalClassName(), "Connected to Google API Client.");
    }

    /**
     * Connection to Google API Client was lost, try to connect again.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(this.getLocalClassName(), "Connection suspended");
        if (!(apiClient.isConnected())) {
            apiClient.connect();
        }
    }

    /**
     * If connection to Google API Client fails, try to resolve the error
     * and show user an error dialog.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(this.getLocalClassName(), "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

        if (resolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                resolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                if (!(apiClient.isConnected())) {
                    apiClient.connect();
                }
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            resolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        TabbedListActivity.ErrorDialogFragment dialogFragment = new TabbedListActivity.ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt("dialog_error", errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(dialogFragment.getFragmentManager(), "dialog_error");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        resolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt("dialog_error");
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((TabbedListActivity) getActivity()).onDialogDismissed();
        }
    }


    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        Log.i(this.getLocalClassName(), "Checking location settings.");
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient,
                        locationSettingsRequest
                );
        result.setResultCallback(this);
    }

    /**
     * The callback invoked when
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} is called. Examines the
     * {@link com.google.android.gms.location.LocationSettingsResult} object and determines if
     * location settings are adequate. If they are not, begins the process of presenting a location
     * settings dialog to the user.
     */
    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        Log.i(this.getLocalClassName(), "Location settings result received.");
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(this.getLocalClassName(), "All location settings are satisfied.");
//                enableMyLocation();
//                myLocationEnabled = true;

                Log.i(this.getLocalClassName(), "Check the permission to use location.");
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.i(this.getLocalClassName(), "Request a permission to use location.");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                } else {
                    Log.i(this.getLocalClassName(), "Permission to use location already granted.");
                    myLocationEnabled = true;
                }

                if (myLocationEnabled) {
                    Log.i(this.getLocalClassName(), "My Location enabled.");
                    map.setMyLocationEnabled(true);
                }

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(this.getLocalClassName(), "Location settings are not satisfied. Show the user a dialog to " +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(this.getLocalClassName(), "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(this.getLocalClassName(), "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    /**
     * Request user to change the device settings as requested.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(this.getLocalClassName(), "User agreed to make required location settings changes.");
//                        enableMyLocation();
//                        myLocationEnabled = true;

                        Log.i(this.getLocalClassName(), "Check the permission to use location.");
                        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.i(this.getLocalClassName(), "Request a permission to use location.");
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                        } else {
                            Log.i(this.getLocalClassName(), "Permission to use location already granted.");
                            myLocationEnabled = true;
                        }

                        if (myLocationEnabled) {
                            Log.i(this.getLocalClassName(), "My Location enabled.");
                            map.setMyLocationEnabled(true);
                        }

                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(this.getLocalClassName(), "User chose not to make required location settings changes.");
                        myLocationEnabled = false;
                        break;
                }
                break;
        }
    }


    /**
     * Act on users decision to allow or deny location.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //user allowed app to use location
                    myLocationEnabled = true;
                    //check location settings
                    checkLocationSettings();
                } else {
                    //user denied app to use location
                    myLocationEnabled = false;
                }
                return;
            }
        }
    }

    /**
     * Show device location on map.
     */
    public void enableMyLocation() {
        Log.i(this.getLocalClassName(), "Check the permission to use location.");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getLocalClassName(), "Request a permission to use location.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }

        if (myLocationEnabled) {
            map.setMyLocationEnabled(true);
        }
    }
}
