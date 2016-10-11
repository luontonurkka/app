package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
    }

    protected void openSpeciesView(View view) {

        Species.SpeciesBuilder sb = new Species.SpeciesBuilder("Kissa", 1);
        sb.descr("Kissa on kovis");
        Species s = sb.build();


        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra("Species", s);

        startActivity(intent);
    }
}
