package com.josh.connexus.viewContents;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class SlidableHorizontalSchrollView extends HorizontalScrollView {

    private final static String TAG = "SlidableSchrollView";

    private int x;
    private int slideWidth = Integer.MAX_VALUE;
    private OnSlideChangeListener listener;
    private int cur_slide = 0;

    public SlidableHorizontalSchrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnSlideChangeListener(OnSlideChangeListener listener){
        this.listener = listener;
    }

    public void setSlideWidth(int slideWidth){
        this.slideWidth = Math.max(1, slideWidth);
    }

    @Override
    public void fling(int velocityY){

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            onTouchEvent(ev);
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN)
            x = getScrollX();
        if(ev.getAction() == MotionEvent.ACTION_UP) {
            int delta = getScrollX() - x;
            if(delta == 0) return super.onTouchEvent(ev);
            int next_slide = delta > 0 ? cur_slide+1 : cur_slide-1;
            slideTo(next_slide);
        }
        return super.onTouchEvent(ev);
    }

    public void slideTo(int index){
        index = Math.max(0, Math.min(index, getMaxSlide()));
        onSlideChange(index, index < cur_slide);
        int target = index * slideWidth;
        ObjectAnimator animator=ObjectAnimator.ofInt(this, "scrollX",  target);
        animator.setDuration(Math.min(200, 200 * Math.abs(target - getScrollX()) * 2 / slideWidth));
        animator.start();
        cur_slide = index;
    }

    public int getCurentSlideIndex(){
        if(getChildCount() < 1) return -1;
        return cur_slide;
    }

    public int getMaxSlide(){
        if(getChildCount() < 1) return -1;
        return getChildAt(0).getWidth()/slideWidth-1;
    }

    protected void onSlideChange(int slideIndex, boolean left){
        if(listener!=null)
            listener.onSlideChange(slideIndex, left);
    }

}
