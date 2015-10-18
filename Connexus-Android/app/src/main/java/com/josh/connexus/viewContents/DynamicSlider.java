package com.josh.connexus.viewContents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class DynamicSlider extends SlidableHorizontalSchrollView{

    private ViewGroup inner;
    private int preloadNumber;
    private SliderAdapter adapter;
    private int startIndex;
    private int stopIndex;
    private int totalNumber;

    public DynamicSlider(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        if(getChildCount() > 0)
            inner = (ViewGroup) getChildAt(0);
    }

    public void configure(int preloadNumber, int totalNumber, SliderAdapter adapter){
        if(inner == null) return;
        if(adapter == null) return;
        if(preloadNumber < 0 || totalNumber < 0) return;
        this.preloadNumber = preloadNumber;
        this.totalNumber = totalNumber;
        this.adapter = adapter;
        startIndex = 0;
        stopIndex = -1;
        for(int i=0; i<Math.min(preloadNumber, totalNumber); i++) {
            inner.addView(adapter.getView(i, inner));
            stopIndex = i;
        }
    }

    @Override
    protected void onSlideChange(int slideIndex, boolean left){
        if(inner == null) return;
        if(adapter == null) return;
        if(slideIndex-preloadNumber > startIndex) {
            for (int i = startIndex; i < slideIndex - preloadNumber; i++)
                adapter.onRemoveView(inner.getChildAt(i-startIndex));
            inner.removeViews(startIndex, slideIndex - preloadNumber-startIndex);
            startIndex = slideIndex - preloadNumber;
        }
        if(slideIndex+preloadNumber < stopIndex){
            for(int i=stopIndex; i>slideIndex+preloadNumber; i++)
                adapter.onRemoveView(inner.getChildAt(inner.getChildCount()-1-(stopIndex-i)));
            inner.removeViews(slideIndex+preloadNumber+1, stopIndex-(slideIndex+preloadNumber));
            stopIndex = slideIndex+preloadNumber;
        }
        if(slideIndex-preloadNumber < startIndex) {
            for (int i = startIndex - 1; i >= Math.max(0, slideIndex - preloadNumber); i--)
                inner.addView(adapter.getView(i, inner), 0);
            startIndex = Math.max(0, slideIndex - preloadNumber);
        }

        if(slideIndex+preloadNumber > stopIndex) {
            for (int i = stopIndex + 1; i <= Math.min(totalNumber - 1, slideIndex + preloadNumber); i++)
                inner.addView(adapter.getView(i, inner));
            stopIndex = Math.min(totalNumber - 1, slideIndex + preloadNumber);
        }

    }

}
