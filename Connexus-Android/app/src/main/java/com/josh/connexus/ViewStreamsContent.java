package com.josh.connexus;

import android.content.Context;
import android.view.ViewGroup;

import com.josh.connexus.elements.Stream;

public class ViewStreamsContent extends ViewContent {
    private Iterable<Stream> streams;

    public ViewStreamsContent(Context context, ViewGroup parentLayout, Iterable<Stream> streams){
        super(context, parentLayout);
        this.streams = streams;
    }

    public void show(){

    }

    public void clear(){

    }
}
