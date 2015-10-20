package com.josh.connexus.elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.io.IOException;
import java.net.URL;

public class BitmapFetcher {
    private static LruCache<String, Bitmap> cache;
    private static final String TAG = "BitmapFetcher";
    private static final int imageHeight = 800;
    private static final int imageWidth = 480;

    private static void init(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static Bitmap fetchBitmap(String url){
        if(cache == null) init();
        if(url == null) return null;
        Bitmap bitmap = cache.get(url);
        if(bitmap != null) return bitmap;
        try {
            bitmap = decodeSampledBitmapFromURL(url, imageWidth, imageHeight);
            if(bitmap == null) throw new Exception("Unable to decode bitmap!");
            cache.put(url, bitmap);
        } catch (Exception e) {
            Log.e(TAG, "There is an error when fetch image " + url);
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap decodeSampledBitmapFromURL(String url, int reqWidth, int reqHeight) throws IOException {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream(), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream(), null, options);
    }

    //from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
