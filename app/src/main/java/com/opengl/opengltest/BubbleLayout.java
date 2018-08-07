package com.opengl.opengltest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by zhipengyang on 2018/7/26.
 */

public class BubbleLayout extends RelativeLayout {
    private Paint mPaint;
    private Path mBubblePath;
    private Path mPath;
    private int mCornerRadius;
    private int mStrokeWidth;
    private int mBubbleHeight;
    private int mBubbleWidth;
    private int mBubbleHOffset;
    private int mWidth;
    private int mHeight;
    private int mColor;
    private BubbleLegOrientation mBubbleOrientation = BubbleLegOrientation.LEFT;

    /**
     * 气泡尖角方向
     */
    public enum BubbleLegOrientation {
        TOP, LEFT, RIGHT, BOTTOM, NONE
    }

    public BubbleLayout(Context context) {
        this(context, null);
    }

    public BubbleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleLayout);
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.BubbleLayout_bubble_strokeWidth, 2);
            mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.BubbleLayout_bubble_cornerRadius, 8);
            mBubbleWidth = typedArray.getDimensionPixelSize(R.styleable.BubbleLayout_bubble_bubbleWidth, 20);
            mBubbleHeight = typedArray.getDimensionPixelSize(R.styleable.BubbleLayout_bubble_bubbleHeight, 10);
            mBubbleHOffset = typedArray.getDimensionPixelSize(R.styleable.BubbleLayout_bubble_bubbleHOffset, 10);
            mColor = typedArray.getColor(R.styleable.BubbleLayout_bubble_color, context.getResources().getColor(R.color.colorPrimaryDark));
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        mBubblePath = new Path();
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    /**
     * 尖角path
     */
    private Path renderBubbleType() {
        mBubblePath.moveTo(mBubbleHOffset, mBubbleHeight);
        mBubblePath.lineTo(mBubbleHOffset + mBubbleWidth / 2, 0);
        mBubblePath.lineTo(mBubbleHOffset + mBubbleWidth, mBubbleHeight);
        mBubblePath.close();
        return mBubblePath;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        mPath.rewind();
        mPath.addRoundRect(new RectF(0, mBubbleHeight, width, height), mCornerRadius, mCornerRadius, Path.Direction.CW);
        mPath.addPath(renderBubbleType());
        canvas.drawPath(mPath, mPaint);
    }


}
