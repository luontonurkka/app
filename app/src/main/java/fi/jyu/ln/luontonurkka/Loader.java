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

        DatabaseHelper myDbHelper;
        SQLiteDatabase myDb = null;

        myDbHelper = new DatabaseHelper(this);
        /*
         * Database must be initialized before it can be used. This will ensure
         * that the database exists and is the current version.
         */
        myDbHelper.initializeDataBase();

        try {
            // A reference to the database can be obtained after initialization.
            myDb = myDbHelper.getWritableDatabase();
            /*
             * Place code to use database here.
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                myDbHelper.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                myDb.close();
            }
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