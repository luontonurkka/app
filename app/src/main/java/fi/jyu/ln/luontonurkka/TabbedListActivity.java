package fi.jyu.ln.luontonurkka;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import fi.jyu.ln.luontonurkka.tools.CoordinateConverter;

public class TabbedListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static SpeciesLists speciesInSquare;
    private int[] lastLocation = {0,0};

    private LastKnownLocation lkl;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        updateLocation();

        //get intent
        Intent intent = getIntent();

        //if list view was opened from map activity
        if (intent.getBooleanExtra(MapsActivity.FROM_MAP_VIEW, false)) {
            lastLocation = CoordinateConverter.WGSToYKJ(intent.getDoubleExtra(MapsActivity.ARG_NORTH_COORD, 62.2141), intent.getDoubleExtra(MapsActivity.ARG_EAST_COORD, 25.7126));
            //TODO Decide on default coordinates
            speciesInSquare = getSpeciesList(intent.getDoubleExtra(MapsActivity.ARG_NORTH_COORD, 62.2141), intent.getDoubleExtra(MapsActivity.ARG_EAST_COORD, 25.7126));
        }

        // If species in square is null, create an example list
        if (speciesInSquare == null) {
            ArrayList<Species> testiLista = new ArrayList<Species>(10);
            for (int i = 0;i < 10; i++) {
                if(i > 5) {
                    Species s = new Species.SpeciesBuilder("Koira", 1).setWikiIdFin("612").build();
                    testiLista.add(i,s);
                } else {
                    Species s = new Species.SpeciesBuilder("Kissa", 1).setWikiIdFin("7064").build();
                    testiLista.add(i, s);
                }
            }
//            speciesInSquare = testiLista;
        }

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
        public static ListFragment newInstance(int sectionNumber, ArrayList<Species> speciesList) {
            ListFragment fragment = new ListFragment();
            Bundle args = new Bundle();
            args.putSerializable(ARG_SPECIES_LIST, speciesList);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_list, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            ArrayList<Species> speciesList = (ArrayList<Species>) getArguments().getSerializable(ARG_SPECIES_LIST);

            SpeciesListAdapter sla = new SpeciesListAdapter(container.getContext(), speciesList);
            //SpeciesListAdapter sla = new SpeciesListAdapter(container.getContext(), speciesInSquare);

            ListView listView = (ListView)rootView.findViewById(R.id.species_list);
            listView.setAdapter(sla);

            return rootView;
        }

        private class SpeciesListAdapter extends ArrayAdapter {

            ArrayList<Species> speciesList;
            Context context;

            public SpeciesListAdapter(Context context, ArrayList<Species> speciesList) {
                super(context,R.layout.specieslistobject_layout,speciesList);
                this.speciesList = speciesList;
                this.context = context;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final Species species = (Species)getItem(position);
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                final View row = inflater.inflate(R.layout.specieslistobject_layout, null, true);
                TextView textView = (TextView)row.findViewById(R.id.list_name);
                textView.setText(species.getName());
                Button button = (Button)row.findViewById(R.id.list_button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TabbedListActivity)row.getContext()).openSpeciesView(species);
                    }
                });
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
                return ListFragment.newInstance(position, speciesInSquare.getBirds());
            }
            else if (position == 2) {
                return ListFragment.newInstance(position, speciesInSquare.getPlants());
            }
            else {
                return ListFragment.newInstance(position, speciesInSquare.getAll());
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
     * @param species   Species object on which more info will be shown
     */
    protected void openSpeciesView(Species species) {

        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra("Species", species);

        startActivity(intent);
    }

    /**
     * Opens the map view activity and finishes the current activity.
     */
    protected void openMapView(View view) {
        finish();
        if (!(getIntent().getBooleanExtra(MapsActivity.FROM_MAP_VIEW, false))) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Shows settings drawer. Called from three vertical lines button.
     * @param view  View that calling the method
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
        ViewPager pager = (ViewPager)findViewById(R.id.list_pager);
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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateLocation() {
        lkl = new LastKnownLocation(this.getApplicationContext(), this, new Runnable() {
            // get called when location changes
            @Override
            public void run() {
                Log.d(getClass().toString(), "location changed");
                // get new location
                Location loc = lkl.getLocation();
                // convert coordinate
                int[] ykj = CoordinateConverter.WGSToYKJ(loc.getLatitude(), loc.getLongitude());
                // TODO remove debug text
                ((TextView)findViewById(R.id.testi_text)).setText(ykj[0] + "," + ykj[1] + " " + loc.getLatitude() + "," + loc.getLongitude());
                // only update list if in different grid square
                if(lastLocation[0] != ykj[0] && lastLocation[1] != ykj[1]) {
                    speciesInSquare = getSpeciesList(loc.getLatitude(), loc.getLongitude());
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                    mViewPager = (ViewPager) findViewById(R.id.list_pager);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    lastLocation = ykj;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        lkl.connectAPI();
        super.onStart();
    }

    /**
     * Get list of species in square
     * @param n North WGS coordinate
     * @param e East WGS coordinate
     * @return List of species in square
     */
    public SpeciesLists getSpeciesList(double n, double e) {
        //create database helper and initialize database if needed
        DatabaseHelper myDbHelper = DatabaseHelper.getInstance(this);
        myDbHelper.initializeDataBase();
        try {
            int[] ykjCoord = CoordinateConverter.WGSToYKJ(n, e);
            speciesInSquare = myDbHelper.getSpeciesInSquare(ykjCoord[0]/10000, ykjCoord[1]/10000);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                myDbHelper.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return speciesInSquare;
    }
}
