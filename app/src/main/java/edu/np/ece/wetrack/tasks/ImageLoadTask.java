package edu.np.ece.wetrack.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.np.ece.wetrack.R;

/**
 * Created by hoanglong on 18-Jan-17.
 */

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;
    private Context context;

    public ImageLoadTask(String url, ImageView imageView, Context context) {
        this.url = url;
        this.imageView = imageView;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avt);
            return myBitmap;
//            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }

}