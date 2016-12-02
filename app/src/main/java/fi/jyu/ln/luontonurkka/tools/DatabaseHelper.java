package fi.jyu.ln.luontonurkka.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.jyu.ln.luontonurkka.R;
import fi.jyu.ln.luontonurkka.Species;
import fi.jyu.ln.luontonurkka.SpeciesLists;

/**
 * Created by sinikka on 7.11.2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    /*
     * The Android's default system path of the application database in internal
     * storage. The package of the application is part of the path of the
     * directory.
     */
    private static String DB_DIR = "/data/data/fi.jyu.ln.luontonurkka/databases/";
    private static String DB_NAME = "LuontonurkkaDB.db";
    private static String DB_PATH = DB_DIR + DB_NAME;
    private static String OLD_DB_PATH = DB_DIR + "old_" + DB_NAME;

    //Database element names:
    //Tables
    private static String TABLE_SPECIES = "species";
    private static String TABLE_GRID = "grid";
    private static String TABLE_SPEC_IN_SQ = "species_in_square";

    //Table columns
    private static String KEY_ID = "id";
    private static String KEY_SQ_ID = "gid";
    private static String KEY_SPEC_ID = "sid";
    private static String KEY_NORTH = "N";
    private static String KEY_EAST = "E";
    private static String KEY_NAME_LATIN = "namelatin";
    private static String KEY_NAME_FIN = "namefin";
    private static String KEY_TYPE = "type";
    private static String KEY_PIC = "picture";
    private static String KEY_WIKI_EN = "idEN";
    private static String KEY_WIKI_FI = "idFI";
    private static String KEY_FREQ = "freq";


    private final Context myContext;

    private boolean createDatabase = false;
    private boolean upgradeDatabase = false;

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, context.getResources().getInteger(
                R.integer.databaseVersion));
        myContext = context;
        // Get the path of the database that is based on the context.
        DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
    }

    /**
     * Upgrade the database in internal storage if it exists but is not current.
     * Create a new empty database in internal storage if it does not exist.
     */
    public void initializeDataBase() {
        /*
         * Creates or updates the database in internal storage if it is needed
         * before opening the database. In all cases opening the database copies
         * the database in internal storage to the cache.
         */
        getWritableDatabase();

        if (createDatabase) {
            /*
             * If the database is created by the copy method, then the creation
             * code needs to go here. This method consists of copying the new
             * database from assets into internal storage and then caching it.
             */
            try {
                /*
                 * Write over the empty data that was created in internal
                 * storage with the one in assets and then cache it.
                 */
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        } else if (upgradeDatabase) {
            /*
             * If the database is upgraded by the copy and reload method, then
             * the upgrade code needs to go here. This method consists of
             * renaming the old database in internal storage, create an empty
             * new database in internal storage, copying the database from
             * assets to the new database in internal storage, caching the new
             * database from internal storage, loading the data from the old
             * database into the new database in the cache and then deleting the
             * old database from internal storage.
             */
            try {
                FileHelper.copyFile(DB_PATH, OLD_DB_PATH);
                copyDataBase();
                SQLiteDatabase old_db = SQLiteDatabase.openDatabase(OLD_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
                SQLiteDatabase new_db = SQLiteDatabase.openDatabase(DB_PATH,null, SQLiteDatabase.OPEN_READWRITE);
                /*
                 * Add code to load data into the new database from the old
                 * database and then delete the old database from internal
                 * storage after all data has been transferred.
                 */
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {
        /*
         * Close SQLiteOpenHelper so it will commit the created empty database
         * to internal storage.
         */
        close();

        /*
         * Open the database in the assets folder as the input stream.
         */
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        /*
         * Open the empty db in interal storage as the output stream.
         */
        OutputStream myOutput = new FileOutputStream(DB_PATH);

        /*
         * Copy over the empty db in internal storage with the database in the
         * assets folder.
         */
        FileHelper.copyFile(myInput, myOutput);

        /*
         * Access the copied database so SQLiteHelper will cache it and mark it
         * as created.
         */
        getWritableDatabase().close();
    }

    /*
     * This is where the creation of tables and the initial population of the
     * tables should happen, if a database is being created from scratch instead
     * of being copied from the application package assets. Copying a database
     * from the application package assets to internal storage inside this
     * method will result in a corrupted database.
     * <P>
     * NOTE: This method is normally only called when a database has not already
     * been created. When the database has been copied, then this method is
     * called the first time a reference to the database is retrieved after the
     * database is copied since the database last cached by SQLiteOpenHelper is
     * different than the database in internal storage.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         * Signal that a new database needs to be copied. The copy process must
         * be performed after the database in the cache has been closed causing
         * it to be committed to internal storage. Otherwise the database in
         * internal storage will not have the same creation timestamp as the one
         * in the cache causing the database in internal storage to be marked as
         * corrupted.
         */
        createDatabase = true;

        /*
         * This will create by reading a sql file and executing the commands in
         * it.
         */
        // try {
        // InputStream is = myContext.getResources().getAssets().open(
        // "create_database.sql");
        //
        // String[] statements = FileHelper.parseSqlFile(is);
        //
        // for (String statement : statements) {
        // db.execSQL(statement);
        // }
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
    }

    /**
     * Called only if version number was changed and the database has already
     * been created. Copying a database from the application package assets to
     * the internal data system inside this method will result in a corrupted
     * database in the internal data system.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*
         * Signal that the database needs to be upgraded for the copy method of
         * creation. The copy process must be performed after the database has
         * been opened or the database will be corrupted.
         */
        upgradeDatabase = true;

        /*
         * Code to update the database via execution of sql statements goes
         * here.
         */

        /*
         * This will upgrade by reading a sql file and executing the commands in
         * it.
         */
        // try {
        // InputStream is = myContext.getResources().getAssets().open(
        // "upgrade_database.sql");
        //
        // String[] statements = FileHelper.parseSqlFile(is);
        //
        // for (String statement : statements) {
        // db.execSQL(statement);
        // }
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // }
    }

    /**
     * Called everytime the database is opened by getReadableDatabase or
     * getWritableDatabase. This is called after onCreate or onUpgrade is
     * called.
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    /**
     * Get square id from database according to coordinates.
     * @param n North YKJ coordinate
     * @param e East YKJ coordinate
     * @return square id in database
      */
    private int getSquare(int n, int e) {
        String[] tableColumns = new String[] {KEY_ID, KEY_NORTH, KEY_EAST};
        String whereClause = KEY_NORTH + " = ? AND " + KEY_EAST + " = ?";
        String[] whereArgs = new String[] {Integer.toString(n), Integer.toString(e)};

        int squareId = 0;
        try (SQLiteDatabase db = getWritableDatabase(); Cursor c = db.query(TABLE_GRID, tableColumns, whereClause, whereArgs, null, null, null);) {
            c.moveToFirst();
            squareId = c.getInt(c.getColumnIndex(KEY_ID));
        }
        return squareId;
    }

    /**
     * Get all species in a square
     * @param n Squares north YKJ coordinate
     * @param e Squares east YKJ coordinate
     * @return SpeciesLists object containing separate lists
     */
    public SpeciesLists getSpeciesInSquare(int n, int e) {
        String[] tableColumns = new String[] {KEY_ID, KEY_SPEC_ID, KEY_SQ_ID, KEY_FREQ};
        String whereClause = KEY_SQ_ID + " = ?";
        String[] whereArgs = new String[] {Integer.toString(getSquare(n, e))};

        List<Species> birdsInSquare = new ArrayList<Species>();
        List<Species> plantsInSquare = new ArrayList<Species>();
        try (SQLiteDatabase db = getWritableDatabase(); Cursor c = db.query(TABLE_SPEC_IN_SQ, tableColumns, whereClause, whereArgs, null, null, null);) {
            Species s;

            while (c.moveToNext()) {
                s = getSpeciesById(c.getInt(c.getColumnIndex(KEY_SPEC_ID)), c.getInt(c.getColumnIndex(KEY_FREQ)));
                if (s != null) {
                    if (s.getType() == Species.BIRD) {
                        birdsInSquare.add(s);
                    } else {
                        plantsInSquare.add(s);
                    }
                }
            }
        }

        //sort species lists by frequency
        SpeciesComparatorByFreq speciesComparatorByFreq = new SpeciesComparatorByFreq();
        Collections.sort(birdsInSquare, speciesComparatorByFreq);
        Collections.sort(plantsInSquare, speciesComparatorByFreq);

        return new SpeciesLists(birdsInSquare, plantsInSquare);
    }

    /**
     * Get species information from database according to id
     * @param speciesId Id of the species
     * @return A species object
     */
    private Species getSpeciesById(int speciesId, int freq) {
        String[] tableColumns = new String[] {KEY_ID, KEY_NAME_LATIN, KEY_NAME_FIN, KEY_TYPE, KEY_WIKI_EN, KEY_WIKI_FI, KEY_PIC};
        String whereClause = KEY_ID + " = ?";
        String[] whereArgs = new String[] {Integer.toString(speciesId)};

        Species s = null;
        try (SQLiteDatabase db = getWritableDatabase(); Cursor c = db.query(TABLE_SPECIES, tableColumns, whereClause, whereArgs, null, null, null);) {
            c.moveToNext();
            if (c.getCount() > 0) {
                String name = c.getString(c.getColumnIndex(KEY_NAME_FIN));
                if (name.isEmpty() || name == null) {
                    name = c.getString(c.getColumnIndex(KEY_NAME_LATIN));
                }
                s = new Species.SpeciesBuilder(
                        name,
//                    c.getString(c.getColumnIndex(KEY_NAME_LATIN)),
                        c.getInt(c.getColumnIndex(KEY_TYPE)))
                        .setWikiIdFin(Integer.toString(c.getInt(c.getColumnIndex(KEY_WIKI_FI))))
                        .setWikiIdEng(Integer.toString(c.getInt(c.getColumnIndex(KEY_WIKI_EN))))
                        .setImageUrl(c.getString(c.getColumnIndex(KEY_PIC)))
                        .setFreq(freq)
                        .build();
            }
        }

        return s;
    }
}