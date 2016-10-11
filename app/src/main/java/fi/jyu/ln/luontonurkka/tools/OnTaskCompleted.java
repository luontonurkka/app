package fi.jyu.ln.luontonurkka.tools;

import android.graphics.Bitmap;

/**
 * Interface for DownloadTextTask
 *
 * Created by jarno on 10/11/16.
 */
public interface OnTaskCompleted {
    void onTaskCompleted(String result);
    void onTaskCompleted(Bitmap result);
}
