package com.yushan.slidingmenu;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by beiyong on 2017-1-16.
 */

public class SlideMenu extends FrameLayout {
    private View menuView;
    private View mainView;
    private ViewDragHelper viewDragHelper;
    private int dragRange;
    private FloatEvaluator floatEvaluator;
    // 当前的state,默认是关闭状态
    private DragState mState = DragState.Close;

    //定义状态常量
    public enum DragState {
        Open, Close;
    }

    public DragState getDragState() {
        return mState;
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        // 浮点类型的计算器
        floatEvaluator = new FloatEvaluator();
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 方法功能:获取子控件
     */
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 简单的异常处理
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlideMenu only support 2 children!");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChild(menuView, widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 一般是在当前的view执行完onMeasure方法之后调用,所以在该方法中肯定可以获取到宽高
     *
     * @param w:
     * @param h:
     * @param oldw:
     * @param oldh:
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        dragRange = (int) (getMeasuredWidth() * 0.8f);
        dragRange = menuView.getMeasuredWidth();
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 方法功能:捕获所有子控件
         * @param child:
         * @param pointerId:
         * @return :
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mainView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                if (left < 0) {
                    left = 0;//限制左边
                }
                if (left > dragRange) {
                    left = dragRange;//限制右边
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == menuView) {
                //让menuView固定住，不要动
                menuView.layout(0, 0, menuView.getMeasuredWidth(), menuView.getMeasuredHeight());
                //同时让mainView伴随移动
                int newLeft = mainView.getLeft() + dx;
                //对newLeft进行限制
                if (newLeft < 0) newLeft = 0;//限制左边
                if (newLeft > dragRange) newLeft = dragRange;//限制右边
                mainView.layout(newLeft, 0, newLeft + mainView.getMeasuredWidth(), mainView.getMeasuredHeight());
            }
            //1.计算移动的百分比
            float fraction = mainView.getLeft() * 1f / dragRange;
            //2.根据移动的百分比去执行伴随动画
            executeAnim(fraction);
            //3.回调监听器的方法
            if (fraction == 1f && mState != DragState.Open) {
                //说明是打开，应该回调onOpen
                mState = DragState.Open;
                if (listener != null) {
                    listener.onOpen();
                }
            } else if (fraction == 0f && mState != DragState.Close) {
                //说明是关闭，应该回调onClose
                mState = DragState.Close;
                if (listener != null) {
                    listener.onClose();
                }
            }
            //回调onDragging
            if (listener != null) {
                listener.onDragging(fraction);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (mainView.getLeft() < dragRange / 2) {
                //在左半边，
                close();
            } else {
                //在右半边
                open();
            }
            //提高滑动的敏感度
            if (xvel > 200) {
                open();
            } else if (xvel < -200) {
                close();
            }
        }
    };

    /**
     * 方法功能:关闭动画
     */
    public void close() {
        mState = DragState.Close;
        viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);//刷新整个ViewGroup
    }

    /**
     * 方法功能:打开动画
     */
    public void open() {
        mState = DragState.Open;
        viewDragHelper.smoothSlideViewTo(mainView, dragRange, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);//刷新整个ViewGroup
    }

    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    private void executeAnim(float fraction) {
        //fraction:0 - 1
        //先缩放mainView
//		float scaleVaule = 0.8f+(1-fraction)*0.2f;//1-0.8f
//        ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
//        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
        //平移menuView
        ViewHelper.setTranslationX(menuView, floatEvaluator.evaluate(fraction, -menuView.getMeasuredWidth() / 2, 0));
        //缩放menuView
//        ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 1f, 0.5f));
//        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
        //透明menuView
//        ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
        //给背景图片添加颜色的遮罩效果
//        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT),
//                PorterDuff.Mode.SRC_OVER);
//		getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction,Color.RED,Color.YELLOW),Mode.SRC_OVER);
    }

    private OnSlideStateChangeListener listener;

    public void setOnSlideStateChangeListener(OnSlideStateChangeListener listener) {
        this.listener = listener;
    }

    /**
     * 拖拽状态改变的监听器
     *
     * @author Administrator
     */
    public interface OnSlideStateChangeListener {
        void onOpen();

        void onClose();

        void onDragging(float fraction);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }
}
