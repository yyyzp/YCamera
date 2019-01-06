package com.opengl.opengltest.widget;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhipengyang on 2018/12/29.
 * 右滑关闭Layout
 */

public class SHSlideLayout extends FrameLayout {

    private static final int DEFAULT_SCRIM_COLOR = 0x60000000;
    private int mScrollPointerId = -1;

    private Activity mActivity;
    private Map<Integer, Integer> mTouchDownXMap;
    private int mLastX;
    private int mLastTouchX;
    private int mLastY;
    private int mLastTouchY;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private int deltaX;
    private int deltaY;
    private RectF rectF;
    private Paint paint;
    private PointF[] mPonitF;
    boolean intercept = false;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mMinFlingVelocity;
    private int mScrimColor = DEFAULT_SCRIM_COLOR;
    private boolean isConsumed = false;


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
        mVelocityTracker = VelocityTracker.obtain();
        mMinFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity() * 2;
        paint = new Paint();
        rectF = new RectF();
        mTouchDownXMap = new HashMap();
    }

    /**
     * 绑定Activity
     */
    public void bindActivity(Activity activity) {
        mActivity = activity;
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View child = decorView.getChildAt(0);//linearLayout
        decorView.removeView(child);
        addView(child);//add linearLayout to slideLayout
        decorView.addView(this);//add slideLayout to decorView
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = ev.getPointerId(0);//down事件触发时，索引一定是0,当前接触屏幕的手指也只有一根
                intercept = false;
                Log.d("yzp", "onInterceptTouchEvent ACTION_DOWN");
                mLastTouchX = (int) ev.getX();
                mLastTouchY = (int) ev.getY();
                mTouchDownXMap.put(mScrollPointerId, mLastTouchX);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("yzp", "onInterceptTouchEvent ACTION_MOVE");
//                int mCurrX = (int) ev.getX();
//                int mCurrY = (int) ev.getY();
                deltaX = (int) ev.getX() - mLastTouchX;
                deltaY = (int) ev.getY() - mLastTouchY;
                //deltaX > 0 表示右滑 ，这里不拦截左滑
                if (deltaX > 0 && Math.abs(deltaX) > Math.abs(deltaY)) {
                    intercept = true;
//                    mLastX = mCurrX;
//                    mLastY = mCurrY;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("yzp", "onInterceptTouchEvent ACTION_UP");
                intercept = false;
                break;
        }
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionIndex = event.getActionIndex();//获取当前事件的索引
        mVelocityTracker.addMovement(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mScrollPointerId = event.getPointerId(0);//down事件触发时，索引一定是0,当前接触屏幕的手指也只有一根
                mInitialTouchX = (int) event.getX();
                mInitialTouchY = (int) event.getY();
                mTouchDownXMap.put(mScrollPointerId, mInitialTouchX);
                Log.d("yzp", "onTouchEvent ACTION_DOWN");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mScrollPointerId = event.getPointerId(actionIndex);//获取当前需要追踪手指的Id
                mInitialTouchX = (int) event.getX(actionIndex);//缓存当前需要追踪手指的X轴坐标
                mInitialTouchY = (int) event.getY(actionIndex);//缓存当前需要追踪手指的Y轴坐标
                mTouchDownXMap.put(mScrollPointerId, mInitialTouchX);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerId(actionIndex) == mScrollPointerId) {//判断离开屏幕的手指是不是当前追踪的手指
                    final int newIndex = actionIndex == 0 ? 1 : 0;//如果当前的索引是最小的那么缓存索引为1的Y轴坐标
                    mScrollPointerId = event.getPointerId(newIndex);
                    mInitialTouchX = (int) event.getX(newIndex);
                    mInitialTouchY = (int) event.getY(newIndex);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                final int index = event.findPointerIndex(event.getPointerId(actionIndex));//根据缓存的Id获取索引
                if (index < 0) {
                    return false;//没找到就return 不处理事件
                }
                int nowX = (int) event.getX(index);
                int nowY = (int) event.getY(index);
//                int mCurrX = (int) event.getX();
                deltaX = nowX - mTouchDownXMap.get(index);
                Log.d("yzp", "onTouchEvent ACTION_MOVE   " + mScrollPointerId + "   " + "nowX " + "    " +
                        nowX + " mTouchDownXMap    " + mTouchDownXMap.get(index));

//                if (!isConsumed &&/* mTouchDownX < (getWidth() / 10) && */Math.abs(deltaX) > Math.abs(deltaY)) {
//                    isConsumed = true;
//                }
//
//                if (isConsumed) {
//                    int rightMovedX = mLastTouchX - nowX;
//                    // 左侧即将滑出屏幕
//                    scrollBy(rightMovedX, 0);
//                    Log.d("yzp", "rightMoveX   " + rightMovedX + "  " + mLastTouchX + "   " + event.getX());
//                }
                if (deltaX > 0) {
                    scrollTo(-deltaX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("yzp", "onTouchEvent ACTION_UP");
                mVelocityTracker.computeCurrentVelocity(1000);
                //判断抬手之后是向左滑 还是向右滑动关闭
                if (-getScrollX() > getWidth() / 5 * 2 || mVelocityTracker.getXVelocity() > mMinFlingVelocity) {
                    scrollClose();
                } else {
                    scrollBack();
                }
                break;
        }
        return true;
    }

    private void onPointerUp(MotionEvent e) {
        final int actionIndex = e.getActionIndex();
        if (e.getPointerId(actionIndex) == mScrollPointerId) {
            // Pick a new pointer to pick up the slack.
            final int newIndex = actionIndex == 0 ? 1 : 0;
            mScrollPointerId = e.getPointerId(newIndex);
            mInitialTouchX = mLastTouchX = (int) (e.getX(newIndex) + 0.5f);
            mInitialTouchY = mLastTouchY = (int) (e.getY(newIndex) + 0.5f);
        }
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

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawShadow(canvas);
    }

    private void drawShadow(Canvas canvas) {
        float scrimOpacity = 1 - (float) Math.abs(getScrollX()) / getWidth();
        final int baseAlpha = (mScrimColor & 0xff000000) >>> 24;
        final int alpha = (int) (baseAlpha * scrimOpacity);
        final int color = alpha << 24;
        paint.setColor(color);
        canvas.clipRect(getScrollX(), 0, 0, getHeight());
        canvas.drawColor(color);
//        rectF.left = getScrollX();
//        rectF.right = 0;
//        rectF.top = 0;
//        rectF.bottom = getHeight();
//        float ratio = (float) Math.abs(getScrollX()) / getWidth();
//        String sAlpha = Integer.toHexString(alpha);
//        if (alpha <= 0xF) {
//            sAlpha = 0 + sAlpha;
//        }
//        Log.d("yzp", sAlpha);
//        paint.setColor(Color.parseColor("#" + sAlpha + "000000"));
//        canvas.save();
//        canvas.drawRect(rectF, paint);
//        canvas.restore();
    }
}
