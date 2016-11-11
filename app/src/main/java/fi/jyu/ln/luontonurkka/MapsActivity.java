package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import fi.jyu.ln.luontonurkka.tools.CoordinateConverter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private Marker locationMarker;
    private LatLng loc;
    private GoogleApiClient apiClient;

    private static LatLng clickedPoint;
    protected static final String ARG_NORTH_COORD = "north_coord";
    protected static final String ARG_EAST_COORD = "earth_coord";
    protected static final String FROM_MAP_VIEW = "from_map_view";
    /* LastKnownLocation Constant Permission */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Check for the permission to access coarse location
        if ( ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }
        map.setMyLocationEnabled(true);

        //Luontonurkka hq
//        loc = new LatLng(62.232436, 25.737582);

        buildGoogleApiClient();
        apiClient.connect();

//        locationMarker = map.addMarker(new MarkerOptions().position(loc));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
        map.setOnMapClickListener(this);
        map.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        clickedPoint = point;

        map.clear();
        locationMarker = map.addMarker(new MarkerOptions()
                .position(point)
                .title("Title")
                .snippet("Snippet")
                .infoWindowAnchor(0.5f, 0.5f));
        locationMarker.showInfoWindow();
    }

    /**
     * Called when user clicks infowindow
     * @param marker Marker of the infowindow
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
        // Check for the permission to access coarse location
        if ( ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }

        // Get last known location
        Location deviceLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        loc = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());

//        map.clear();
//        locationMarker = map.addMarker(new MarkerOptions()
//                .position(loc));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
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
