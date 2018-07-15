package com.opengl.opengltest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by legao005426 on 2018/5/25.
 */

public class ImgTextLayout extends RelativeLayout {
    private Context mContext;
    private ImageView ivAdd;
    private TextView tvWatched;
    private boolean mState = false; // mState为true表示已关注， mState为false表示未关注
    private int mBgNormal; // mState为true表示已关注， mState为false表示未关注
    private int mBgPress; // mState为true表示已关注， mState为false表示未关注
    private String mTextNormal; // mState为true表示已关注， mState为false表示未关注
    private String mTextPress; // mState为true表示已关注， mState为false表示未关注
    private int mImgNormal; // mState为true表示已关注， mState为false表示未关注
    private int mImgPress; // mState为true表示已关注， mState为false表示未关注
    private int mSpacing; // mState为true表示已关注， mState为false表示未关注
    private int tvLength;
    private int ivLenght;

    public ImgTextLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ImgTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ImgTextLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int width;
//        int height ;
//        if (widthMode == MeasureSpec.EXACTLY) {
//            width = widthSize;
//            height = heightSize;
//        }else{
//            width = DeviceInfo.dip2Px(mContext, 60);
//            height = DeviceInfo.dip2Px(mContext, 25);
//        }
//        setMeasuredDimension(width, height);
//    }

    private void updateView() {
        if (mState) {
            ivAdd.setVisibility(GONE);
            tvWatched.setVisibility(VISIBLE);
            setBackgroundDrawable(ContextCompat.getDrawable(mContext, mBgPress));
        } else {
            ivAdd.setVisibility(VISIBLE);
            tvWatched.setVisibility(GONE);
            setBackgroundDrawable(ContextCompat.getDrawable(mContext, mBgNormal));
        }
    }

    public void changeState(boolean state) {
        mState = state;
        updateView();
    }

    public boolean getState() {
        return mState;
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        mContext = context;
        /* 获取自定义属性 */
        if (attrs == null) {
            mState = false;
        } else {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WatchView, defStyle, 0);
            mState = a.getBoolean(R.styleable.WatchView_watchViewState, false);
            mSpacing = a.getDimensionPixelSize(R.styleable.WatchView_watchViewSpacing, 12);
            mBgNormal = a.getResourceId(R.styleable.WatchView_watchViewBgNormal, R.mipmap.ic_launcher);
            mBgPress = a.getResourceId(R.styleable.WatchView_watchViewBgPress, R.mipmap.ic_launcher);
            mImgNormal = a.getResourceId(R.styleable.WatchView_watchViewImgResNormal, R.mipmap.ic_launcher);
            mImgPress = a.getResourceId(R.styleable.WatchView_watchViewImgResPress, R.mipmap.ic_launcher);
            mTextNormal = a.getString(R.styleable.WatchView_watchViewTextNormal);
            mTextNormal = a.getString(R.styleable.WatchView_watchViewTextPress);
        }

        View view = LayoutInflater.from(context).inflate(R.layout.item_img_text_btn, null, false);

        //View view = View.inflate(context, R.layout.item_watch_button, null);
        addView(view);

        ivAdd = view.findViewById(R.id.iv_img);
        tvWatched = view.findViewById(R.id.tv_text);
        ivAdd.setImageResource(mImgNormal);
        tvWatched.setText(mTextNormal);
        updateView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof TextView) {
                tvLength = getChildAt(i).getMeasuredWidth();
            } else if (getChildAt(i) instanceof ImageView) {
                ivLenght = getChildAt(i).getMeasuredWidth();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int chilidCount = getChildCount();
        int left = ((int) getWidth() - (tvLength + ivLenght + mSpacing)) / 2;
        int len = tvLength;
        for (int i = 0; i < chilidCount; i++) {
            getChildAt(i).layout(left, getChildAt(i).getTop(), left + len, getChildAt(i).getBottom());
            left = left + len + mSpacing;
            len = ivLenght;
        }
    }
}
