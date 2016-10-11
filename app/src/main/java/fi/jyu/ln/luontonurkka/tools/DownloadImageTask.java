package fi.jyu.ln.luontonurkka.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * Loads image and calls onTaskComplete when image is loaded.
 *
 * Usage:
 * new DownloadImageTask(onTaskComplete).execute("http://url.com/img.png");
 *
 * Created by jarno on 11.10.16.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private OnTaskCompleted taskCompleted;

    public DownloadImageTask(OnTaskCompleted taskCompleted) {
        this.taskCompleted = taskCompleted;
    }

    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmap = null;
        String url = urls[0];
        try {
            InputStream in = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitmap;
    }


    protected void onPostExecute(Bitmap result) {
        taskCompleted.onTaskCompleted(result);
    }
}
