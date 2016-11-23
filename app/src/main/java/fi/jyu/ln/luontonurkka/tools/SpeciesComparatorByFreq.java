package fi.jyu.ln.luontonurkka.tools;

import java.util.Comparator;

import fi.jyu.ln.luontonurkka.Species;

/**
 * Created by sinikka on 23.11.2016.
 */

public class SpeciesComparatorByFreq implements Comparator<Species> {
    @Override
    public int compare(Species s1, Species s2) {
        return (s2.getFreq() - s1.getFreq());
    }
}