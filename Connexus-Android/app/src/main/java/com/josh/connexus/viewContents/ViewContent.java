package com.josh.connexus.viewContents;

import android.content.Context;
import android.view.ViewGroup;

public abstract class ViewContent {
    protected   Context context;
    protected ViewGroup parentLayout;

    public ViewContent(Context context, ViewGroup parentLayout){
        this.context = context;
        this.parentLayout = parentLayout;
    }

    public abstract void show();

    public abstract void clear();

}
