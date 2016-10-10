package fi.jyu.ln.luontonurkka;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import static fi.jyu.ln.luontonurkka.R.id.species_content_text;
import static fi.jyu.ln.luontonurkka.R.id.toolbar_layout;

public class SpeciesActivity extends AppCompatActivity {

    Species species;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        species = (Species)getIntent().getSerializableExtra("Species");

        CollapsingToolbarLayout layout = (CollapsingToolbarLayout)this.findViewById(toolbar_layout);
        layout.setTitle(species.getName());

        TextView contentTextView = (TextView)this.findViewById(species_content_text);
        contentTextView.setText(species.getDescr());
    }
}
