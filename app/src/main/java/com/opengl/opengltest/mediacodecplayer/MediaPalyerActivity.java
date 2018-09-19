package com.opengl.opengltest.mediacodecplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.opengl.opengltest.R;

import java.util.List;

/**
 * Created by zhipengyang on 2018/9/12.
 */

public class MediaPalyerActivity  extends AppCompatActivity implements SurfaceHolder.Callback,IPlayStateListener{
    private static final String TAG = "MediaPlayerActivity";

    public static final String PATH = "path";
    public static final String ORIENTATION = "orientation";

    private String mVideoPath= "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4";
    private int mOrientation;

    private SurfaceView mVideoSurfaceView;

    private AspectFrameLayout mLayoutPlayer;
    private SimplePlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        mLayoutPlayer=findViewById(R.id.framlayout);
        mVideoSurfaceView=findViewById(R.id.surfaceview);
        mVideoSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mVideoPath != null) {
            mPlayer = new SimplePlayer(holder.getSurface(), mVideoPath);
            mPlayer.setPlayStateListener(MediaPalyerActivity.this);
            mPlayer.play();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mPlayer != null) {
            mPlayer.destroy();
        }
    }

    @Override
    public void videoAspect(final int width, final int height, float time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 判断旋转角度，有些视频拍出来的orientation是90/270度的
                if (mOrientation % 180 == 0) {
                    mLayoutPlayer.setAspectRatio((float) width / height);
                } else {
                    mLayoutPlayer.setAspectRatio((float) height / width);
                }
            }
        });
    }
}
