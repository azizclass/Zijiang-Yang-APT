package com.josh.connexus.elements;

import java.util.HashMap;

public class Image {
    private static HashMap<String, byte[]> imageCache = new HashMap<String, byte[]>();

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
