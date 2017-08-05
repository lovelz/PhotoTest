package com.lz.selectphoto.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 适配PhotoPreview的使用
 * Created by liuzhu
 * on 2017/7/25.
 */

public class PreviewPager extends ViewPager {

    private int mScrollState = SCROLL_STATE_IDLE;
    private boolean isTransition = false;
    private boolean isInterceptable = false;

    public PreviewPager(Context context) {
        this(context, null);
    }

    public PreviewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        addOnPageChangeListener(new PageChangeListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScrollState != SCROLL_STATE_IDLE) {
            return super.onInterceptTouchEvent(ev);
        }

        if (isTransition){
            int action = ev.getAction();
            ev.setAction(MotionEvent.ACTION_DOWN);
            super.onInterceptTouchEvent(ev);
            ev.setAction(action);
            isTransition = false;
        }

        boolean b  = false;
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN){
            isInterceptable = false;
        }

        if (action != MotionEvent.ACTION_MOVE || isInterceptable){
            b = super.onInterceptTouchEvent(ev);
        }

        return isInterceptable && b;
    }

    public void setInterceptable(boolean interceptable) {
        isInterceptable = interceptable;
    }

    private class PageChangeListener extends SimpleOnPageChangeListener{
        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;
        }
    }
}
