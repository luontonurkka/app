package fi.jyu.ln.luontonurkka.tools;

import java.io.IOException;
import java.util.HashMap;

import fi.jyu.ln.luontonurkka.Species;

/**
 * A parser to parse grid data from a CSV file.
 * Created by sinikka on 10.10.2016.
 */

public class SpeciesParser extends Parser {

    /**
     * Parses a CSV file of the following form:
     * species name, type, description
     * Creates a hash map with species name as key (string) and species information as value (list of strings).
     * @return HashMap with specis name as key and species information as value
     * @throws IOException
     */
    @Override
    protected HashMap parseFile() throws IOException {
        HashMap<String, Species> allSpecies = new HashMap<>();
        String csvLine;
        while ((csvLine = buffer.readLine()) != null) {
            String[] splitLine = csvLine.split("\\s*,\\s*");
            Species newSpecies = new Species.SpeciesBuilder(splitLine[0], Integer.parseInt(splitLine[1]))
                    //.descr(splitLine[2])
                    .build();
            allSpecies.put(splitLine[0], newSpecies);
        }
        return allSpecies;
    }
}