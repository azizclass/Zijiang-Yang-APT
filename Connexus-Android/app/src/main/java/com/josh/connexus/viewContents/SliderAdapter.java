package com.josh.connexus.viewContents;

import android.view.View;

public interface SliderAdapter {
    public abstract void loadResource(int position, View view);

    public abstract void releaseResource(int position, View view);

}
