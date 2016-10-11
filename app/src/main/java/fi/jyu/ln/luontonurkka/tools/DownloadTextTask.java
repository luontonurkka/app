package fi.jyu.ln.luontonurkka.tools;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 *
 * Created by Jarno on 11.10.16.
 */
public class DownloadTextTask extends AsyncTask<String, Void, String> {

    private OnTextTaskCompleted taskCompleted;

    public DownloadTextTask(OnTextTaskCompleted taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    protected String doInBackground(String... urls) {
        String s = "";
        try {
            URL url = new URL(urls[0]);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while((str = in.readLine()) != null) {
                s += str + "\n";
            }
            in.close();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return s;
    }

    protected void onPostExecute(String result) {
        taskCompleted.onTaskCompleted(result);
    }
}
