package fi.jyu.ln.luontonurkka;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.android.gms.nearby.messages.internal.Update;

import java.util.ArrayList;
import java.util.List;

import fi.jyu.ln.luontonurkka.tools.CoordinateConverter;
import fi.jyu.ln.luontonurkka.tools.DatabaseHelper;

public class TabbedListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult>, com.google.android.gms.location.LocationListener {

    // TODO remove
    private long startTime;

    private static SpeciesLists speciesInSquare;
    private static final int LIST_LENGTH = 30;
    private int[] lastLocationYKJ = {0, 0};
    private GoogleApiClient apiClient;

    private Location lastLocation;
    /* Location Constant Permissions */
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;

    /* Location update parameters */
    private static final int GPS_MIN_UPDATE_TIME_MILLIS = 5000;

    private static final float GPS_MIN_DIST_METERS = 100;

    /* Stores parameters for requests to the FusedLocationProviderApi. */
    protected LocationRequest locationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    protected LocationSettingsRequest locationSettingsRequest;

    /* Tracks the status of the location updates request. */
    protected Boolean requestingLocationUpdates;

    /* Constant used in the location settings dialog. */
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * Resolving error in connecting to Google API Client state
     */
    private boolean resolvingError;
    private static final int REQUEST_RESOLVE_ERROR = 77;

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        time("api build");

        //Build the Google API Client, LocationRequest and LocationSettingsRequest
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();

        time("intent");

        //get intent
        Intent intent = getIntent();

        //if list view was opened from map activity
        if (intent.getBooleanExtra(MapsActivity.FROM_MAP_VIEW, false)) {
            //if came to list view from map view, don't update location
            requestingLocationUpdates = false;
            lastLocationYKJ = CoordinateConverter.WGSToYKJ(intent.getDoubleExtra(MapsActivity.ARG_NORTH_COORD, 62.2141), intent.getDoubleExtra(MapsActivity.ARG_EAST_COORD, 25.7126));
            //TODO Decide on default coordinates
            speciesInSquare = getSpeciesList(intent.getDoubleExtra(MapsActivity.ARG_NORTH_COORD, 62.2141), intent.getDoubleExtra(MapsActivity.ARG_EAST_COORD, 25.7126));
        } else {
            requestingLocationUpdates = true;
        }

        time("adapter");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        if (speciesInSquare != null) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        }

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.list_pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        time("on create done");
    }

    /**
     * Fragment creating species list
     */
    public static class ListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SPECIES_LIST = "species_list";

        public ListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ListFragment newInstance(int sectionNumber, List<Species> speciesList) {
            ListFragment fragment = new ListFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_SPECIES_LIST, new ArrayList<Species>(speciesList));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_list, container, false);

            ArrayList<Species> speciesList = (ArrayList<Species>) getArguments().getSerializable(ARG_SPECIES_LIST);

            SpeciesListAdapter sla = new SpeciesListAdapter(container.getContext(), speciesList);

            ListView listView = (ListView) rootView.findViewById(R.id.species_list);
            listView.setAdapter(sla);

            return rootView;
        }

        private class SpeciesListAdapter extends ArrayAdapter {

            ArrayList<Species> speciesList;
            Context context;

            public SpeciesListAdapter(Context context, ArrayList<Species> speciesList) {
                super(context, R.layout.specieslistobject_layout, speciesList);
                this.speciesList = speciesList;
                this.context = context;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final Species species = (Species) getItem(position);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                final View row = inflater.inflate(R.layout.specieslistobject_layout, null, true);
                TextView textView = (TextView) row.findViewById(R.id.list_name);
                textView.setText(species.getName());
                Button button = (Button) row.findViewById(R.id.list_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TabbedListActivity) row.getContext()).openSpeciesView(species);
                    }
                });
                ImageView icon = (ImageView) row.findViewById(R.id.list_image);
                if(species.getType() == Species.BIRD) {
                    icon.setImageResource(R.drawable.ic_bird);
                } else if (species.getType() == Species.PLANT) {
                    icon.setImageResource(R.drawable.ic_leaf);
                }
                return row;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ListFragment (defined as a static inner class below).

            if (position == 1) {
                return ListFragment.newInstance(position, new ArrayList<Species>(speciesInSquare.getBirds().subList(0, LIST_LENGTH)));
            } else if (position == 2) {
                return ListFragment.newInstance(position, new ArrayList<Species>(speciesInSquare.getPlants().subList(0, LIST_LENGTH)));
            } else {
                return ListFragment.newInstance(position, new ArrayList<Species>(speciesInSquare.getAll().subList(0, LIST_LENGTH)));
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Kaikki";
                case 1:
                    return "Linnut";
                case 2:
                    return "Kasvit";
            }
            return null;
        }
    }

    /**
     * Opens the species view activity.
     *
     * @param species Species object on which more info will be shown
     */
    protected void openSpeciesView(Species species) {

        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra("Species", species);

        startActivity(intent);
    }

    /**
     * Clicking on the map button opens the map view.
     */
    protected void onMapButtonClick(View view) {
        openMapView();
    }

    /**
     * Opens the map view activity and finishes the current activity.
     */
    protected void openMapView() {
        finish();
        if (!(getIntent().getBooleanExtra(MapsActivity.FROM_MAP_VIEW, false))) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Shows settings drawer. Called from three vertical lines button.
     *
     * @param view View that calling the method
     */
    public void showDrawer(View view) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void showAllTab(View view) {
        showTab(0);
    }

    public void showBirdsTab(View view) {
        showTab(1);
    }

    public void showPlantsTab(View view) {
        showTab(2);
    }

    public void showTab(int index) {
        ViewPager pager = (ViewPager) findViewById(R.id.list_pager);
        pager.setCurrentItem(index);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            //
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     *
     * Location updating
     *
     */

    /**
     * Builds a GoogleAPIClient.
     */
    protected synchronized void buildGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(GPS_MIN_UPDATE_TIME_MILLIS);

        locationRequest.setSmallestDisplacement(GPS_MIN_DIST_METERS);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
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
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(this.getLocalClassName(), "All location settings are satisfied.");
//                startLocationUpdates();
                requestingLocationUpdates = true;
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(this.getLocalClassName(), "Location settings are not satisfied. Show the user a dialog to " +
                        "upgrade location settings ");
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    status.startResolutionForResult(TabbedListActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //TODO
                    Log.i(this.getLocalClassName(), "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                //TODO
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
                        requestingLocationUpdates = true;
//                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(this.getLocalClassName(), "User chose not to make required location settings changes.");
                        requestingLocationUpdates = false;
                        openMapView();
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
            case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //user allowed app to use location
                    requestingLocationUpdates = true;
                    //check location settings
                    checkLocationSettings();
                } else {
                    //user denied app to use location
                    openMapView();
                }
                return;
            }
        }
    }

    /**
     * Connect Google API Client when starting activity.
     */
    @Override
    protected void onStart() {
        apiClient.connect();
        super.onStart();
    }

    /**
     * Disconnect Google API Client when stopping activity.
     */
    @Override
    protected void onStop() {
        apiClient.disconnect();
        super.onStop();
    }

    /**
     * Start location updates again when resuming activity.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Resume receiving location updates
        if (apiClient.isConnected() && requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Stop location updates when pausing activity.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (apiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        Log.i(this.getLocalClassName(), "Start location updates.");
        time("start location updates");

        Log.i(this.getLocalClassName(), "Check the permission to use location.");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(this.getLocalClassName(), "Request a permission to use location.");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            Log.i(this.getLocalClassName(), "Permission to use location already granted.");
            //check location settings
//            checkLocationSettings();

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient,
                    locationRequest,
                    this
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    requestingLocationUpdates = true;
                }
            });
        }

        Log.i(this.getLocalClassName(), "Location check done.");
        time("location check done");
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                apiClient,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                requestingLocationUpdates = false;
            }
        });
    }

    /**
     * Runs when Google API Client is connected.
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(this.getLocalClassName(), "Connected to GoogleApiClient");
        time("connected to api");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it.

        final TabbedListActivity activity = this;

        Thread thread = new Thread() {
            @Override
            public void run() {
                if (lastLocation == null) {
                    // Ask user for permission to use coarse location
                    Log.i(activity.getLocalClassName(), "Check the permission to use location.");
                    if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.i(activity.getLocalClassName(), "Request a permission to use location.");
                        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
                    } else {
                        Log.i(activity.getLocalClassName(), "Permission to use location already granted.");
                        //check location settings
                        checkLocationSettings();

                        // Get last location

                        lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                        UpdateListTask task = new UpdateListTask();
                        task.execute(new Void[0]);

                        // Start location updates
                        if (requestingLocationUpdates) {
                            startLocationUpdates();
                        }
                    }
                }
                else {
                    // Start location updates
                    if (requestingLocationUpdates) {
                        startLocationUpdates();
                    }
                }
            }
        };
        thread.run();

        time("on connected done");
    }

    /**
     * Connection to Google API Client was lost, try to connect again.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(this.getLocalClassName(), "Connection suspended");
        apiClient.connect();
    }

    /**
     * If connection to Google API Client fails (can't get location), try to resolve the error
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
                apiClient.connect();
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
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt("dialog_error", errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        resolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

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



    /*
     * On location changed, set new location and update
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(getClass().toString(), "location changed");
        if (location != null) {
            lastLocation = location;
        }
        UpdateListTask task = new UpdateListTask();
        task.execute(new Void[0]);
    }

    private class UpdateListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            time("updating list");
            if (lastLocation != null) {
                // convert coordinate
                int[] ykj = CoordinateConverter.WGSToYKJ(lastLocation.getLatitude(), lastLocation.getLongitude());
                Log.d(getClass().toString(), (lastLocationYKJ[0] - ykj[0]) + " " + (lastLocationYKJ[1] - ykj[1]));
                // only update list if in different grid square
                if (lastLocationYKJ[0] != ykj[0] && lastLocationYKJ[1] != ykj[1]) {
                    time("getting species list");
                    speciesInSquare = getSpeciesList(lastLocation.getLatitude(), lastLocation.getLongitude());
                    time("done species list");
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    mViewPager = (ViewPager) findViewById(R.id.list_pager);
                    // adapter has to be set in ui thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setAdapter(mSectionsPagerAdapter);
                            findViewById(R.id.list_loading).setVisibility(View.INVISIBLE);
                        }
                    });
                    lastLocationYKJ = ykj;
                }
            }
            time("list updated");

            return null;
        }
    }

    private void time(String text) {
        String time = System.currentTimeMillis() - startTime + "";
        Log.d(getClass().toString(), time + " " + text);
    }


    /**
     * Get list of species in square
     *
     * @param n North WGS coordinate
     * @param e East WGS coordinate
     * @return List of species in square
     */
    public SpeciesLists getSpeciesList(double n, double e) {
        //create database helper and initialize database if needed
        DatabaseHelper myDbHelper = DatabaseHelper.getInstance(this);
        myDbHelper.initializeDataBase();
        try {
            //fetch list from database according to YKJ coordinates
            int[] ykjCoord = CoordinateConverter.WGSToYKJ(n, e);
            speciesInSquare = myDbHelper.getSpeciesInSquare(ykjCoord[0] / 10000, ykjCoord[1] / 10000);
        } catch (Exception ex) {
            //TODO
            ex.printStackTrace();
        } finally {
            try {
                myDbHelper.close();
            } catch (Exception ex) {
                //TODO
                ex.printStackTrace();
            }
        }

        return speciesInSquare;
    }
}
