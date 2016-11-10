package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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

public class Loader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        /*
         * create database helper and initialize the database
         */
        DatabaseHelper myDbHelper;
        myDbHelper = DatabaseHelper.getInstance(this);
        Log.d(getClass().toString(), "DB helper opened");
        /*
         * Database must be initialized before it can be used. This will ensure
         * that the database exists and is the current version.
         */
        myDbHelper.initializeDataBase();
        Log.d(getClass().toString(), "DB initialized");
        myDbHelper.close();
        Log.d(getClass().toString(), "DB helper closed");

        final Intent intent = new Intent(this, TabbedListActivity.class);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 10000);


    }
}