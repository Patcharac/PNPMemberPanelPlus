package com.panelplus.pnpmember.activity;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.graphics.Bitmap;
import android.widget.ImageView;


import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;


/**
 * Created by FSC on 13/4/2563.
 */

public class GenQrCode extends AsyncTask<String, Void, Bitmap> {
//    private final WeakReference<ImageView> imageViewReference;

    public GenQrCode(ImageView imageView){
//        imageViewReference = new WeakReference<>(imageView);
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        return GenQrCode(strings[0]);
    }

     @Override
     protected void onPostExecute(Bitmap bitmap) {
          if(isCancelled()) {
              bitmap = null;
          }

//          ImageView imageView = imageViewReference.get();
//          if(imageView != null) {
//              if(bitmap != null) {
//                  imageView.setImageBitmap(bitmap);
//              }
//          }
     }

    private Bitmap GenQrCode(String url) {
        HttpURLConnection urlConnection = null;
        try{
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
            if(statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if(inputStream != null){
                 return BitmapFactory.decodeStream(inputStream);
            }
        }   catch (Exception e) {
            urlConnection.disconnect();
        } finally {
           if(urlConnection != null ) {
               urlConnection.disconnect();
           }
        }
        return null;
    }
}
