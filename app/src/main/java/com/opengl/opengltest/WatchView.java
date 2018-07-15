package com.opengl.opengltest;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sohu.auto.base.R;
import com.sohu.auto.base.utils.DeviceInfo;
import com.sohu.auto.base.widget.imagespickers.Image;

/**
 * Created by legao005426 on 2018/5/25.
 */

public class WatchView extends RelativeLayout {
    private Context mContext;
    private ImageView ivAdd;
    private TextView tvUnwatch;
    private TextView tvWatched;
    private boolean mState = false; // mState为true表示已关注， mState为false表示未关注

    public WatchView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WatchView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WatchView(Context context, AttributeSet attrs, int defStyle){
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

    private void updateView(){
        if (mState){
            tvUnwatch.setVisibility(GONE);
            ivAdd.setVisibility(GONE);
            tvWatched.setVisibility(VISIBLE);
            setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_unwatch_button));
        }else {
            tvUnwatch.setVisibility(VISIBLE);
            ivAdd.setVisibility(VISIBLE);
            tvWatched.setVisibility(GONE);
            setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_watched_button));
        }
    }

    public void changeState(boolean state){
        mState = state;
        updateView();
    }

    public boolean getState(){
        return mState;
    }

    private void init(Context context, AttributeSet attrs, int defStyle){
        mContext = context;
        /* 获取自定义属性 */
        if(attrs == null){
            mState = false;
        }else {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WatchView, defStyle, 0);
            int n = a.getIndexCount();
            for(int i=0; i < n; i++){
                int attr = a.getIndex(i);
                if (attr == R.styleable.WatchView_watchViewState) {
                    mState = a.getBoolean(attr, false);
                }
            }
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_watch_button, null, false);
        //View view = View.inflate(context, R.layout.item_watch_button, null);
        addView(view);
        ivAdd = view.findViewById(R.id.iv_add_btn);
        tvUnwatch = view.findViewById(R.id.tv_watch);
        tvWatched = view.findViewById(R.id.tv_watched);
        updateView();
    }
}
