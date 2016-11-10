package fi.jyu.ln.luontonurkka;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import fi.jyu.ln.luontonurkka.tools.GridParser;
import fi.jyu.ln.luontonurkka.tools.SettingsManager;

public class Loader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

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

        SettingsManager sm = new SettingsManager(this);
        Log.d(getClass().toString(), sm.getBool(getString(R.string.setting_map_default)) + "");
        final Intent intent;
        if(sm.getBool(getString(R.string.setting_map_default))) {
            intent = new Intent(this, MapsActivity.class);
        } else {
            intent = new Intent(this, TabbedListActivity.class);
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}