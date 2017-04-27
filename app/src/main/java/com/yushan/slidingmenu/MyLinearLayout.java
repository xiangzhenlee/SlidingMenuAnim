package com.yushan.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by beiyong on 2017-1-16.
 */

public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context) {
        super(context);
    }

    private SlideMenu slideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        this.slideMenu = slideMenu;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 如果slideMenu处于关闭，则拦截并消费掉
        if (slideMenu.getDragState() == SlideMenu.DragState.Close) {
            return true;// 拦截
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果slideMenu处于关闭，则消费掉
        if (slideMenu.getDragState() == SlideMenu.DragState.Close) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 则让SlideMenu关闭掉
                slideMenu.close();
            }
            return true;// 消费掉
        }
        return super.onTouchEvent(event);
    }
}
