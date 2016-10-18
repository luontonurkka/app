package fi.jyu.ln.luontonurkka;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    protected ArrayList<Species> testiLista = new ArrayList<Species>(10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        for (int i = 0;i < 10; i++) {
            Species.SpeciesBuilder sb = new Species.SpeciesBuilder("Kissa", 1);
            sb.descr("Kissa on kovis");
            Species s = sb.build();
            testiLista.add(i,s);
        }

        SpeciesListAdapter adapteri = new SpeciesListAdapter(this, testiLista);

        ListView lista = (ListView)findViewById(R.id.species_list);
        lista.setAdapter(adapteri);

    }

    private class SpeciesListAdapter implements ListAdapter {

        ListActivity listActivity;
        ArrayList<Species> speciesList;

        public SpeciesListAdapter(ListActivity listActivity, ArrayList<Species> speciesList) {
            this.listActivity = listActivity;
            this.speciesList = speciesList;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return speciesList.toArray().length;
        }

        @Override
        public Object getItem(int position) {
            return speciesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = new TextView(listActivity);

            textView.setText(((Species)getItem(position)).getName());

            return textView;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
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
