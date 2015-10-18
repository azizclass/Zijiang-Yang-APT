package com.josh.connexus.elements;

import android.graphics.Bitmap;

import java.util.HashMap;

public class Image {
    private static HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();

    public final String url;

    public Image(String url){
        this.url = url;
    }

    public void load(){

    }

    @Override
    public String toString(){
        return url;
    }
}
