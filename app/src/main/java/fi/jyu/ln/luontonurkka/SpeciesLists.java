package fi.jyu.ln.luontonurkka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.jyu.ln.luontonurkka.tools.SpeciesComparatorByFreq;

/**
 * Contains array lists for different species types.
 * Created by sinikka on 14.11.2016.
 */

public class SpeciesLists {

    private List<Species> birds;
    private List<Species> plants;

    public SpeciesLists(List<Species> birds, List<Species> plants) {
        this.birds = birds;
        this.plants = plants;
    }

    public List<Species> getPlants() {
        return plants;
    }

    public List<Species> getBirds() {
        return birds;
    }

    public List<Species> getAll() {
        List<Species> all = new ArrayList<Species>();
        all.addAll(birds);
        all.addAll(plants);
        SpeciesComparatorByFreq speciesComparatorByFreq = new SpeciesComparatorByFreq();
        Collections.sort(all, speciesComparatorByFreq);
        return all;
    }
}
