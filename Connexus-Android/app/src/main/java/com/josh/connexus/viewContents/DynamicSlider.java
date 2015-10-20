package com.josh.connexus.viewContents;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

public class DynamicSlider extends SlidableHorizontalSchrollView{
    private final static String TAG = "DynamicSlider";

    private ViewGroup inner;
    private int preloadNumber;
    private SliderAdapter adapter;
    private int startIndex;
    private int stopIndex;

    public DynamicSlider(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        if(getChildCount() > 0)
            inner = (ViewGroup) getChildAt(0);
    }

    public void configure(int preloadNumber, SliderAdapter adapter){
        if(inner == null) return;
        if(adapter == null) return;
        if(preloadNumber < 0) return;
        this.preloadNumber = preloadNumber;
        this.adapter = adapter;
        startIndex = 0;
        stopIndex = -1;
        for(int i=0; i<Math.min(1+preloadNumber, inner.getChildCount()); i++) {
            adapter.loadResource(i, inner.getChildAt(i));
            stopIndex = i;
        }
    }

    @Override
    protected void onSlideChange(int slideIndex, boolean left){
        super.onSlideChange(slideIndex, left);
//        Log.d(TAG, "SliderIndex="+slideIndex+" left="+left);
        if(inner == null) return;
        if(adapter == null) return;
        int cur_left = Math.max(0, slideIndex-preloadNumber);
        int cur_right = Math.min(getMaxSlide(), slideIndex+preloadNumber);
        for(int i=startIndex; i<Math.min(stopIndex+1, cur_left); i++)
            adapter.releaseResource(i, inner.getChildAt(i));
        for(int i=stopIndex; i>Math.max(startIndex - 1, cur_right); i--)
            adapter.releaseResource(i, inner.getChildAt(i));
        for(int i=cur_left; i<Math.min(cur_right + 1, startIndex); i++)
            adapter.loadResource(i, inner.getChildAt(i));
        for(int i=cur_right; i>Math.max(cur_left-1, stopIndex); i--)
            adapter.loadResource(i, inner.getChildAt(i));
        startIndex = cur_left;
        stopIndex = cur_right;
    }

}
