package com.josh.connexus.viewContents;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class SlidableHorizontalSchrollView extends HorizontalScrollView {

    private int slideWidth;

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
        if(ev.getAction() == MotionEvent.ACTION_UP) {
            final int target = (int) (Math.round(((double) getScrollX()) / slideWidth) * slideWidth);
            ObjectAnimator animator=ObjectAnimator.ofInt(this, "scrollX", target );
            animator.setDuration(300*Math.abs(target-getScrollX())*2/slideWidth);
            animator.start();
        }
        return super.onTouchEvent(ev);
    }

}
