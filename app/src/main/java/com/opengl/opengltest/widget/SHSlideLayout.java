package com.opengl.opengltest.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.Toast;

/**
 * Created by zhipengyang on 2018/12/29.
 */

public class SHSlideLayout extends FrameLayout {
    private Activity mActivity;
    private ScrollFinish mScrollFinish;
    private int mLastX;
    private int mLastY;
    int deltaX;
    int deltaY;
    Scroller mScroller;
    RectF rectF;
    Paint paint;
    boolean intercept = false;
    VelocityTracker mVelocityTracker;
    int maxScrollSpeed;
//    private int mLastTouchX;
//    private int mLastTouchY;

    public SHSlideLayout(@NonNull Context context) {
        this(context, null);
    }

    public SHSlideLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SHSlideLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        mScroller = new Scroller(context);
        paint = new Paint();
        paint.setColor(Color.parseColor("#30000000"));
        mVelocityTracker=VelocityTracker.obtain();
        maxScrollSpeed=ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    /**
     * 绑定Activity
     */
    public void bindActivity(Activity activity) {
        mActivity = activity;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View child = decorView.getChildAt(0);
        decorView.removeView(child);
        addView(child);
        decorView.addView(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) ev.getX();
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int mCurrX = (int) ev.getX();
                int mCurrY = (int) ev.getY();
                deltaX = mCurrX - mLastX;
                deltaY = mCurrY - mLastY;
                //deltaX > 0 表示右滑 ，这里不拦截左滑
                if (deltaX > 0 && Math.abs(deltaX) > Math.abs(deltaY * 1.5)) {
                    intercept = true;
                }
                mLastX=mCurrX;
                mLastY=mCurrY;
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int mCurrX = (int) event.getX();
                int mCurrY = (int) event.getY();
                deltaX = mCurrX - mLastX;
                deltaY = mCurrX - mLastY;
                if (deltaX > 0) {
                    scrollTo(-deltaX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000);
                if (mVelocityTracker.getXVelocity()>maxScrollSpeed){
                    scrollClose();
                    break;
                }
                //判断抬手之后是向左滑 还是向右滑动关闭
                if (-getScrollX() < getWidth() / 5 * 2) {
                    scrollBack();
                } else {
                    scrollClose();
                }
                break;
        }

        return true;

    }

    private void scrollBack() {
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 300);
        invalidate();
    }

    private void scrollClose() {
        //scrollX为负数是往右滑
        mScroller.startScroll(getScrollX(), 0, -getWidth() - getScrollX(), 0, 300);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();
        } else if (-getScrollX() >= getWidth()) {//如果滑动结束了 并且是向右滑动到屏幕边缘 则关闭页面
          mActivity.finish();
        }
    }

    public void setScrollFinish(ScrollFinish scrollFinish) {
        this.mScrollFinish = scrollFinish;
    }

    public interface ScrollFinish {
        void finish();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        rectF = new RectF(getScrollX(), 0, 0, getHeight());
        float ratio = (float) Math.abs(getScrollX()) / getWidth();
        int alpha = 0xFF - (int) (ratio * 0XFF);
//        if(alpha>=100){
//            alpha-=100;
//        }
        String sAlpha = Integer.toHexString(alpha);
        if (alpha < 16) {
            sAlpha = "0" + sAlpha;
        }

        String colorString = "#" + sAlpha + "000000";
        Log.d("yzp", colorString);
        paint.setColor(Color.parseColor(colorString));
        canvas.save();
        canvas.drawRect(rectF, paint);
        canvas.restore();
    }
}
