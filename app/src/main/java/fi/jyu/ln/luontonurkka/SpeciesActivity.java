package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import fi.jyu.ln.luontonurkka.tools.DownloadImageTask;
import fi.jyu.ln.luontonurkka.tools.DownloadTextTask;
import fi.jyu.ln.luontonurkka.tools.OnTaskCompleted;

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

    private static final int DESCRIPTION_LENGTH = 1000;

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

        // get img
        final ImageView imgView = (ImageView)this.findViewById(species_toolbar_img);
        imgView.setImageResource(R.drawable.kissa);

        // get text from wikipage
        final TextView contentTextView = (TextView)this.findViewById(species_content_text);
        contentTextView.setText("Lataa...");
        OnTaskCompleted task = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                try {
                    final JSONObject obj = new JSONObject(result);
                    Iterator<String> keys = obj.getJSONObject("query").getJSONObject("pages").keys();
                    if (keys.hasNext()) {
                        final String firstKey = (String)keys.next();
                        String desc = obj.getJSONObject("query").getJSONObject("pages").getJSONObject(firstKey).getString("extract");
                        if(desc.length() > DESCRIPTION_LENGTH) {
                            desc = desc.substring(0,DESCRIPTION_LENGTH) + "...";
                        }
                        contentTextView.setText(desc);
                        Button wikiButton = (Button)findViewById(R.id.species_content_button_wiki);
                        wikiButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openWikiPage(firstKey);
                            }
                        });
                        wikiButton.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException je) {
                    Log.w(getClass().toString(), je.getMessage());
                }
            }

            @Override
            public void onTaskCompleted(Bitmap result) {

            }
        };
        new DownloadTextTask(task).execute("https://fi.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + species.getName());
    }

    protected void openWikiPage(String pageId) {
        Intent wikiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://fi.wikipedia.org/?curid=" + pageId));
        startActivity(wikiIntent);
    }
}
