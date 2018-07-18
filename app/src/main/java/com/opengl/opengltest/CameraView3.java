/*
 *
 * CameraView.java
 * 
 * Created by Wuwang on 2016/11/14
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.opengl.opengltest;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

/**
 * Description:
 */
public class CameraView3 extends TextureView implements TextureView.SurfaceTextureListener {

    private LowCamera mCamera2;
    private int cameraId = 0;

    public CameraView3(Context context) {
        this(context, null);
    }

    public CameraView3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
        mCamera2 = new LowCamera();

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera2.open(cameraId);
        mCamera2.setPreviewTexture(surface);
        mCamera2.preview();
        mCamera2.takePhoto(new ICamera.TakePhotoCallback() {
            @Override
            public void onTakePhoto(byte[] bytes, int width, int height) {

            }
        });
        mCamera2.setDataCallback(new LowCamera.DataCallback() {
            @Override
            public void callback(byte[] data) {
                Log.d("data", data.toString());
            }
        });

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
