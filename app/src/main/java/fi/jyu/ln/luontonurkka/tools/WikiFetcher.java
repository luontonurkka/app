package fi.jyu.ln.luontonurkka.tools;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by jaenkies on 10/31/16.
 */

public class WikiFetcher {

    public static void getWikiDescription(String pageId, OnTaskCompleted task) {
        final OnTaskCompleted tt = task;
        OnTaskCompleted t = new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                tt.onTaskCompleted(getDescriptionFromJson(result));
            }

            @Override
            public void onTaskCompleted(Bitmap result) {

            }
        };

        new DownloadTextTask(t).execute("https://fi.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&pageids=" + pageId);
    }

    public static String getDescriptionFromJson(String json) {
        try {
            final JSONObject obj = new JSONObject(json);
            Iterator<String> keys = obj.getJSONObject("query").getJSONObject("pages").keys();
            if (keys.hasNext()) {
                final String firstKey = (String) keys.next();
                String desc = obj.getJSONObject("query").getJSONObject("pages").getJSONObject(firstKey).getString("extract");
                return desc;
            }
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return ""; // TODO return something smarter?
    }
}
