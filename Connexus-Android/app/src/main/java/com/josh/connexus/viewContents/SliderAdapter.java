package com.josh.connexus.viewContents;

import android.view.View;
import android.view.ViewGroup;

public interface SliderAdapter {
    public abstract View getView(int position, ViewGroup parent);

    public abstract void onRemoveView(View view);
}
