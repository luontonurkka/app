package fi.jyu.ln.luontonurkka;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import static fi.jyu.ln.luontonurkka.R.id.species_content_text;
import static fi.jyu.ln.luontonurkka.R.id.species_toolbar_img;
import static fi.jyu.ln.luontonurkka.R.id.species_toolbar_layout;

/**
 * Activity that display species specific info
 *
 * Created by Jarno on 10.10.2016.
 */
public class SpeciesActivity extends AppCompatActivity {

    Species species;

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

        // get text from desc
        TextView contentTextView = (TextView)this.findViewById(species_content_text);
        contentTextView.setText(species.getDescr());

        // get img
        ImageView imgView = (ImageView)this.findViewById(species_toolbar_img);
        imgView.setImageResource(R.drawable.kissa);
    }

    private static Drawable LoadImageFromUrl(String url) {
        try {
            InputStream is = (InputStream)new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "img");
            return d;
        } catch(Exception e) {
            Log.w("SpeciesAcitivty", "Image " + url + " load failed");
            return null;
        }
    }
}
