package com.josh.connexus.elements;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.HashMap;

public class Image implements Serializable{

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
