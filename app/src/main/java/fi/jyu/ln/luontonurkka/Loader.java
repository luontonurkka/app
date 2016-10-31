package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import fi.jyu.ln.luontonurkka.tools.GridParser;

public class Loader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        //not working as intented, take a closer look
        LastKnownLocation asd = new LastKnownLocation(this,this);
        final Location proop = asd.getLocation();


        //reading grid csv to hashmap
        GridParser p = new GridParser();
        InputStream is = getResources().openRawResource(R.raw.grid_sorted);
        p.openFile(is);
        try {
            HashMap<String, String> grid = p.parseFile();
            p.closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Intent intent1 = new Intent(this, TabbedListActivity.class);
        //intent1.putExtra("species_list",);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //remember to .putextra all stuff for a working listview
                //presumably a hashmap of grids.
                startActivity(intent1);
                finish();
            }
        }, 10000);


    }
}