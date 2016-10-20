package fi.jyu.ln.luontonurkka;

import java.io.IOException;
import java.util.HashMap;

/**
 * A parser to parse grid data from a CSV file.
 * Created by sinikka on 10.10.2016.
 */

public class GridParser extends Parser {

    /**
     * Parses a CSV file of the following form:
     * coordinates, species, species, species, ...
     * Creates a hash map with coordinates as key (string, "366:490") and all species in the square as value (comma separated string).
     * @return HashMap with grid coordinates as key and species in square as value
     * @throws IOException
     */
    @Override
    protected HashMap<String, String> parseFile() throws IOException {
        HashMap<String, String> grid = new HashMap<>();
        String csvLine;
        while ((csvLine = buffer.readLine()) != null) {
            if (csvLine.length() >= 7) {
                grid.put(csvLine.substring(0, 7), csvLine.substring(8));
            }
        }
        return grid;
    }
}