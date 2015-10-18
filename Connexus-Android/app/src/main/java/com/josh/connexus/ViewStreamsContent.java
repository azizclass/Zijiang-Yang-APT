package com.josh.connexus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.josh.connexus.elements.Stream;

public class ViewStreamsContent extends ViewContent {
    private Iterable<Stream> streams;

    public ViewStreamsContent(Context context, ViewGroup parentLayout, Iterable<Stream> streams){
        super(context, parentLayout);
        this.streams = streams;
    }

    public void show(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        inflater.inflate(R.layout.stream_overview, parentLayout, true);
    }

    public void clear(){

    }
}
