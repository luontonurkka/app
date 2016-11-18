package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
        species = (Species)getIntent().getSerializableExtra("Species");

        // set title to species name
        CollapsingToolbarLayout layout = (CollapsingToolbarLayout)this.findViewById(species_toolbar_layout);
        layout.setTitle(species.getName());
        layout.setExpandedTitleColor(Color.WHITE);
        layout.setCollapsedTitleTextColor(Color.WHITE);

        // TODO ids wrongway around
        pageId = species.getIdEng();
        lang = "fi";
        if(pageId.length() < 1) {
            pageId = species.getIdFin();
            lang = "en";
        }

        OnTaskCompleted task = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if(result != null)
                    setTextComplete(result);
            }

            @Override
            public void onTaskCompleted(Bitmap result) {
                if(result != null)
                    setImgComplete(result);
            }
        };
        WikiFetcher.getWikiDescription(pageId, task, lang);

        if (species.getImgUrl().length() > 0)
            new DownloadImageTask(task).execute(species.getImgUrl());
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
    }

    private void setImgComplete(Bitmap img) {
        speciesImg = img;
        ImageView imgView = (ImageView)findViewById(R.id.species_toolbar_img);
        imgView.setImageBitmap(speciesImg);
        imgView.setVisibility(View.VISIBLE);
    }
}
