package com.josh.connexus.viewContents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.josh.connexus.R;
import com.josh.connexus.elements.Stream;

public class ViewPicturesContent extends ViewContent {

    private Stream stream;
    private boolean active;

    public ViewPicturesContent(Context context, ViewGroup parentLayout, Stream stream){
        super(context, parentLayout);
        this.stream = stream;
    }

    @Override
    public void show(){
        if(active) return;
        active = true;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_pictures, parentLayout, true);
    }

    @Override
    public void clear(){

    }
}
