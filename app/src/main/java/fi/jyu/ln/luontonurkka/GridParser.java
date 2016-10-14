package fi.jyu.ln.luontonurkka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A parser to parse grid data from a CSV file.
 * Created by sinikka on 10.10.2016.
 */

public class GridParser extends Parser {

    /**
     * Parses a CSV file of the following form:
     * coordinates, species, species, species, ...
     * Creates a hash map with coordinates as key (two integers) and all species in the square as value (list of strings).
     * @return HashMap with grid coordinates as key and species in square as value
     * @throws IOException
     */
    @Override
    protected HashMap parseFile() throws IOException {
        HashMap<ArrayList<Integer>, ArrayList<String>> grid = new HashMap<>();
        String csvLine;
        while ((csvLine = buffer.readLine()) != null) {
            String[] splitLine = csvLine.split("\\s*,\\s*");

            String[] coordsAsString = splitLine[0].split(":");
            int n = Integer.parseInt(coordsAsString[0]);
            int e = Integer.parseInt(coordsAsString[1]);
            ArrayList<Integer> coords = new ArrayList<Integer>();
            coords.add(n);
            coords.add(e);

            ArrayList<String> allSpeciesInSquare = new ArrayList<String>();
            if (splitLine.length > 1) {
                for (int i = 1; i < splitLine.length; i++) {
                    allSpeciesInSquare.add(splitLine[i]);
                }
            }
            grid.put(coords, allSpeciesInSquare);
        }
        return grid;
    }
}
