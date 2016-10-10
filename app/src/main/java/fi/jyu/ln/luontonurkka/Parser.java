package fi.jyu.ln.luontonurkka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Abstract class for handling a file and creating a hash map from it.
 * @author Sinikka Siironen on 10.10.2016.
 */
public abstract class Parser {

    protected static BufferedReader buffer = null;

    protected abstract HashMap parseFile() throws IOException;

    private BufferedReader openFile(InputStream is) throws FileNotFoundException {
        buffer = new BufferedReader(new InputStreamReader(is));
        return buffer;
    }

    private void closeFile() throws IOException {
        if (buffer != null) {
            buffer.close();
        }
    }
}