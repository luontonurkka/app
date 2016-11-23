package fi.jyu.ln.luontonurkka;

import java.util.ArrayList;
import java.util.Collections;

import fi.jyu.ln.luontonurkka.tools.SpeciesComparatorByFreq;

/**
 * Contains array lists for different species types.
 * Created by sinikka on 14.11.2016.
 */

public class SpeciesLists {

    private ArrayList<Species> birds;
    private ArrayList<Species> plants;

    public SpeciesLists(ArrayList<Species> birds, ArrayList<Species> plants) {
        this.birds = birds;
        this.plants = plants;
    }

    public ArrayList<Species> getPlants() {
        return plants;
    }

    public ArrayList<Species> getBirds() {
        return birds;
    }

    public ArrayList<Species> getAll() {
        ArrayList<Species> all = new ArrayList<Species>();
        all.addAll(birds);
        all.addAll(plants);
        SpeciesComparatorByFreq speciesComparatorByFreq = new SpeciesComparatorByFreq();
        Collections.sort(all, speciesComparatorByFreq);
        return all;
    }
}
