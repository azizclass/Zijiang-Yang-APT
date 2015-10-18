package com.josh.connexus.elements;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.net.URL;

public class BitmapFetcher {
    private static LruCache<String, Bitmap> cache;
    private static final String TAG = "BitmapFetcher";

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
            bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
            if(bitmap == null) throw new Exception("Unable to decode bitmap!");
            cache.put(url, bitmap);
        } catch (Exception e) {
            Log.e(TAG, "There is an error when fetch image " + url);
            e.printStackTrace();
        }
        return bitmap;
    }

}
