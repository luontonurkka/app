package fi.jyu.ln.luontonurkka.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

    protected abstract HashMap<String, String> parseFile() throws IOException;

    public BufferedReader openFile(InputStream is) {
        buffer = new BufferedReader(new InputStreamReader(is));
        return buffer;
    }

    public void closeFile() throws IOException {
        if (buffer != null) {
            buffer.close();
        }
    }
}