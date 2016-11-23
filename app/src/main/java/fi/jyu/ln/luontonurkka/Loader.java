package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import fi.jyu.ln.luontonurkka.tools.DatabaseHelper;
import fi.jyu.ln.luontonurkka.tools.SettingsManager;

public class Loader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        final LoaderTask lt = new LoaderTask();
        final Loader l = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Loader[] list = {l};
                lt.execute(list);
            }
        });
    }

    public void startNextActivity(Intent intent) {
        startActivity(intent);

        finish();
    }
}

class LoaderTask extends AsyncTask<Loader, Void, Intent> {

    private Loader loader = null;

    @Override
    protected Intent doInBackground(Loader... params) {
        loader = params[0];

        /*
         * create database helper and initialize the database
         */
        DatabaseHelper myDbHelper;
        myDbHelper = DatabaseHelper.getInstance(loader);
        Log.d(getClass().toString(), "DB helper opened");
        /*
         * Database must be initialized before it can be used. This will ensure
         * that the database exists and is the current version.
         */
        myDbHelper.initializeDataBase();
        Log.d(getClass().toString(), "DB initialized");
        myDbHelper.close();
        Log.d(getClass().toString(), "DB helper closed");

        // get default view from preferences
        SettingsManager sm = new SettingsManager(loader);
        final Intent intent;
        if(sm.getBool(loader.getString(R.string.setting_map_default))) {
            intent = new Intent(loader, MapsActivity.class);
        } else {
            intent = new Intent(loader, TabbedListActivity.class);
        }

        return intent;
    }

    @Override
    protected void onPostExecute(Intent intent) {
        loader.startNextActivity(intent);
    }
}
