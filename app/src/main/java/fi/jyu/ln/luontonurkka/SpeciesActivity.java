package fi.jyu.ln.luontonurkka;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import fi.jyu.ln.luontonurkka.tools.DownloadImageTask;
import fi.jyu.ln.luontonurkka.tools.OnTaskCompleted;
import fi.jyu.ln.luontonurkka.tools.WikiFetcher;

import static fi.jyu.ln.luontonurkka.R.id.species_toolbar_layout;

/**
 * Activity that display species specific info
 *
 * Created by Jarno on 10.10.2016.
 */
public class SpeciesActivity extends AppCompatActivity {

    Species species;

    private static final int DESCRIPTION_LENGTH = 1000;

    private String speciesDesc;
    private Bitmap speciesImg;
    private String pageId;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get species from intent
        species = (Species) getIntent().getSerializableExtra("Species");

        // set title to species name
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout) this.findViewById(species_toolbar_layout);
        layout.setTitle(species.getName());
        layout.setExpandedTitleColor(Color.WHITE);
        layout.setCollapsedTitleTextColor(Color.WHITE);

        pageId = species.getIdEng();
        lang = "fi";
        if (pageId.length() < 1) {
            pageId = species.getIdFin();
            lang = "en";
        }

        if (!isNetworkAvailable()) {
            (findViewById(R.id.image_progress)).setVisibility(View.INVISIBLE);
            (findViewById(R.id.species_loading)).setVisibility(View.INVISIBLE);
            setFrequencyVisible(species);
            Snackbar.make(findViewById(android.R.id.content), "Yhdistä laite internetiin nähdäksesi lajitiedot.", Snackbar.LENGTH_INDEFINITE)
                    .show();
        } else {

            OnTaskCompleted task = new OnTaskCompleted() {
                @Override
                public void onTaskCompleted(String result) {
                    if (result != null)
                        setTextComplete(result);
                }

                @Override
                public void onTaskCompleted(Bitmap result) {
                    if (result != null)
                        setImgComplete(result);
                }
            };
            WikiFetcher.getWikiDescription(pageId, task, lang);

            if (species.getImgUrl().length() > 0) {
                new DownloadImageTask(task).execute(species.getImgUrl());
            } else {
                ((ProgressBar) findViewById(R.id.image_progress)).setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void openWikiPage(View view) {
        Intent wikiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + lang + ".wikipedia.org/?curid=" + pageId));
        startActivity(wikiIntent);
    }

    private void setTextComplete(String text) {
        speciesDesc = text;
        TextView textView = (TextView)findViewById(R.id.species_content_text);
        if(speciesDesc.length() > DESCRIPTION_LENGTH)
            speciesDesc = speciesDesc.substring(0,DESCRIPTION_LENGTH) + "...";
        textView.setText(speciesDesc);

        ProgressBar loadingBar = (ProgressBar)findViewById(R.id.species_loading);
        Button wikiButton = (Button)findViewById(R.id.species_content_button_wiki);

        loadingBar.setVisibility(View.INVISIBLE);

        wikiButton.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);

        setFrequencyVisible(species);
    }

    private void setImgComplete(Bitmap img) {
        speciesImg = img;
        ImageView imgView = (ImageView)findViewById(R.id.species_toolbar_img);
        ImageView bigView = (ImageView)findViewById(R.id.big_image);
        imgView.setImageBitmap(speciesImg);
        bigView.setImageBitmap(speciesImg);
        imgView.setVisibility(View.VISIBLE);
        findViewById(R.id.species_maximize_img).setVisibility(View.VISIBLE);
        Log.d(getClass().toString(), findViewById(R.id.image_progress) == null ? "null" : "not null");
        ((ProgressBar)findViewById(R.id.image_progress)).setVisibility(View.INVISIBLE);
        findViewById(R.id.species_img_gradient).setVisibility(View.VISIBLE);
    }

    private void setFrequencyVisible(Species s) {
        // find indicator circles
        ImageView[] freqs = {
                (ImageView)findViewById(R.id.freq_1),
                (ImageView)findViewById(R.id.freq_2),
                (ImageView)findViewById(R.id.freq_3),
                (ImageView)findViewById(R.id.freq_4)
        };
        // normalize freq to [0,...,4]
        int f = Math.round(s.getFreq() / 25f);
        // change indicator icons and show them
        for(int i = 0; i < f; i++) {
            freqs[i].setImageResource(R.drawable.circle_filled);
        }
        findViewById(R.id.freq_layout).setVisibility(View.VISIBLE);
    }

    public void openImage(View view) {
        findViewById(R.id.big_image).setVisibility(View.VISIBLE);
    }

    public void closeImage(View view) {
        findViewById(R.id.big_image).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        // if zoomed image is visible back button closes image
        if(findViewById(R.id.big_image).getVisibility() == View.VISIBLE)
            closeImage(null);
        else
            super.onBackPressed();
    }

    /**
     * Checks if network is available.
     * @return Is network available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
