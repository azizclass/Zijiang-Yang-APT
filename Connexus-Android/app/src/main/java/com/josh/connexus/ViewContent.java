package com.josh.connexus;

import android.content.Context;
import android.view.ViewGroup;

public abstract class ViewContent {
    private  Context context;
    private ViewGroup parentLayout;

    public ViewContent(Context context, ViewGroup parentLayout){
        this.context = context;
        this.parentLayout = parentLayout;
    }

    public abstract void show();

    public abstract void clear();

}
