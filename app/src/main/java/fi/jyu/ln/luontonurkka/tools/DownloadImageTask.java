package fi.jyu.ln.luontonurkka.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by jarno on 10/11/16.
 */

/**
 * Class that loads and image from url and sets it in the ImageView provided.
 *
 * Usage:
 * new DownloadImageTask(imageView).execute("http://url.com/img.png");
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imgView;

    public DownloadImageTask(ImageView imgView) {
        this.imgView = imgView;
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
        imgView.setImageBitmap(result);
    }
}
