package fi.jyu.ln.luontonurkka.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Jarno 22.10.16
 */

public class SettingsManager {

    private SharedPreferences sp;

    public SettingsManager(Activity activity) {
        PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        sp = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void setBool(String preference, boolean bool) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(preference, bool);
        editor.commit();
    }

    public boolean getBool(String preference) {
        return sp.getBoolean(preference, false);
    }

}
