package com.josh.connexus.viewContents;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class SlidableHorizontalSchrollView extends HorizontalScrollView {

    private int slideWidth = Integer.MAX_VALUE;
    private int x;

    public SlidableHorizontalSchrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSlideWidth(int slideWidth){
        this.slideWidth = slideWidth;
    }

    @Override
    public void fling(int velocityY){

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN)
            x = getScrollX();
        if(ev.getAction() == MotionEvent.ACTION_UP) {
            int delta = getScrollX() - x;
            if(delta == 0) return super.onTouchEvent(ev);
            double cur_slide = (double) getScrollX() / slideWidth;
            int next_slide = (int)(delta > 0 ? Math.ceil(cur_slide) : Math.floor(cur_slide));
            int target = next_slide* slideWidth;
            onSlideChange(next_slide, delta<0);
            ObjectAnimator animator=ObjectAnimator.ofInt(this, "scrollX", target );
            animator.setDuration(200*Math.abs(target-getScrollX())*2/slideWidth);
            animator.start();
        }
        return super.onTouchEvent(ev);
    }

    protected void onSlideChange(int slideIndex, boolean left){

    }

}
