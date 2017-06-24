package Downloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by root on 24/6/17.
 */

public class ImageLoader {
    String url;

    public ImageLoader(String url) {
        this.url = url;
    }

    public Bitmap loadImage() throws IOException {

        URL urlConnection = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) urlConnection
                .openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        return myBitmap;

    }
}
