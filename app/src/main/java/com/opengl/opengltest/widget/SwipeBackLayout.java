package com.opengl.opengltest.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.opengl.opengltest.R;

public class SwipeBackLayout extends FrameLayout {

    /**
     * 滑动的方向
     */
    private int mEdgeFlag;
    private ViewDragHelper mDragHelper;
    private Drawable mShadowLeft;
    private float mScrollPercent;
    private View mContentView;
    private boolean mInLayout;
    private int mScrimColor = 0x60000000;
    private float mScrimOpacity;
    private boolean isInterceptEnable = true;

    private int mMinFlingVelocity;

    /**
     * Default threshold of scroll
     */
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.3f;

    private static final int OVERSCROLL_DISTANCE = 10;

    /**
     * Threshold of scroll, we will close the activity, when scrollPercent over
     * this value;
     */
    private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;
    private int mContentLeft;
    private int mContentTop;
    private Activity mActivity;

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, new MyCallBack());
        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity() * 2;

    }

    public void setInterceptEnable(boolean isInterceptEnable) {
        this.isInterceptEnable = isInterceptEnable;
    }

    public void attachToActivity(Activity activity) {
        mActivity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    private void setContentView(View view) {
        mContentView = view;
    }

    /**
     * ViewDragHelper回调
     */
    private class MyCallBack extends ViewDragHelper.Callback {
        // 指定当前传入的这个视图是否可拖动
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //是边缘滑动并且满足可以滑动的时候，contentview才可以拖动
            boolean a = child == mContentView;
            boolean b = !mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL, pointerId);
            boolean c = mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL, pointerId);
            Log.d("yzp", "  "+pointerId+"   " + a + "   " + b + "     " + c);
            return isInterceptEnable && child == mContentView && !mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_VERTICAL, pointerId)
                  /*  && mDragHelper.checkTouchSlop(ViewDragHelper.DIRECTION_HORIZONTAL, pointerId)*/;

//        return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return ViewDragHelper.EDGE_LEFT;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 0;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            //获取一个滑动位置的百分比参数
            //如果是左边滑动的情况，mScrollPercent=拖动过后的left/（contentView的宽度+左边阴影的宽度）
            mScrollPercent = Math.abs((float) left
                    / (mContentView.getWidth()/* + mShadowLeft.getIntrinsicWidth()*/));
            //获取拖动过后的left跟top，然后调用invalidate会重新绘制ViewGroup，在onLayout方法中重新摆放子控件位置
            mContentLeft = left;
            mContentTop = top;
            invalidate();
            /**
             * 当滑动的距离百分比》=1的时候，结束掉当前Activity
             */
            if (mScrollPercent >= 1) {
                if (!mActivity.isFinishing()) {
                    mActivity.finish();
                    mActivity.overridePendingTransition(0, 0);
                }
            }
        }

        //当手指离开屏幕后实现的操作
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();
            final int childHeight = releasedChild.getHeight();

            int left = 0, top = 0;
            //当为左边滑动的时候
            //x轴上滑动的速度>=0并且滑动的距离到达总距离自定的0.3f的时候，left即为（content的宽度+左阴影的宽度+超出滑动距离的offset）
            left = xvel > mMinFlingVelocity || mScrollPercent > mScrollThreshold ? childWidth : 0;
            //自动滚动到指定的left跟top位置
            mDragHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        // 处理水平滑动
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int ret = 0;
            //边界限制为（最小值为0，最大值为contentview的宽度）
            ret = Math.min(child.getWidth(), Math.max(left, 0));
            return ret;
        }


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mInLayout = true;
        if (mContentView != null)
            mContentView.layout(mContentLeft, mContentTop,
                    mContentLeft + mContentView.getMeasuredWidth(),
                    mContentTop + mContentView.getMeasuredHeight());
        mInLayout = false;
    }

    //    @Override
//    public void requestLayout() {
//        if (!mInLayout) {
//            super.requestLayout();
//        }
//    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean drawContent = child == mContentView;

        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (/*mScrimOpacity > 0 &&*/ drawContent
                && mDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            drawScrim(canvas, child);
        }
        return ret;
    }

    private void drawScrim(Canvas canvas, View child) {
        float scrimOpacity = 1 - mScrollPercent;
        final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * scrimOpacity);
        final int color = alpha << 24 | (mScrimColor & 0xffffff);
        canvas.clipRect(0, 0, child.getLeft(), getHeight());
        canvas.drawColor(color);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        try {
            return mDragHelper.shouldInterceptTouchEvent(event);
        } catch (ArrayIndexOutOfBoundsException e) {
            // FIXME: handle exception
            // issues #9
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDragHelper.processTouchEvent(event);
        return true;
    }

}