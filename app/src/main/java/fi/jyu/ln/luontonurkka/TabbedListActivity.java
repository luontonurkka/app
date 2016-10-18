package fi.jyu.ln.luontonurkka;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TabbedListActivity extends AppCompatActivity {

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

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final ActionBar actionBar = getActionBar();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
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
                View row = inflater.inflate(R.layout.specieslistobject_layout, null, true);
                TextView textView = (TextView)row.findViewById(R.id.list_name);
                textView.setText(species.getName());
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TabbedListActivity)v.getContext()).openSpeciesView(species);
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

            ArrayList<Species> testiLista = new ArrayList<Species>(10);
            for (int i = 0;i < 10; i++) {
                if(i > 5) {
                    Species.SpeciesBuilder sb = new Species.SpeciesBuilder("Koira", 1);
                    sb.descr("Koira on myös kovis");
                    Species s = sb.build();
                    testiLista.add(i,s);
                } else {
                    Species.SpeciesBuilder sb = new Species.SpeciesBuilder("Kissa", 1);
                    sb.descr("Kissa on kovis");
                    Species s = sb.build();
                    testiLista.add(i, s);
                }
            }

            return ListFragment.newInstance(position + 1, testiLista);
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

    protected void openSpeciesView(Species species) {

        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra("Species", species);

        startActivity(intent);
    }
}
