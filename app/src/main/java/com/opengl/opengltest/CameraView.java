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
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Description:
 */
public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private KitkatCamera mCamera2;
    private CameraDrawer mCameraDrawer;
    private CameraDrawerES3 mCameraDrawer3;
    private int cameraId = 0;

    private Runnable mRunnable;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        setPreserveEGLContextOnPause(true);//保存Context当pause时
        mCamera2 = new KitkatCamera();
        mCameraDrawer = new CameraDrawer(getResources());
        mCameraDrawer3 = new CameraDrawerES3();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraDrawer3.onSurfaceCreated(gl, config);
        if (mRunnable != null) {
            mRunnable.run();
            mRunnable = null;
        }
        mCamera2.open(cameraId);
        mCameraDrawer3.setCameraId(cameraId);
        Point point = mCamera2.getPreviewSize();
        mCameraDrawer3.setPreviewSize(point.x, point.y);
//        mCameraDrawer.setDataSize(point.x,point.y);
        mCamera2.setPreviewTexture(mCameraDrawer3.getSurfaceTexture());
        mCameraDrawer3.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });
        mCamera2.preview();

    }

    private void open(int cameraId) {
        mCamera2.close();
        mCamera2.open(cameraId);
        mCameraDrawer3.setCameraId(cameraId);
        Point point = mCamera2.getPreviewSize();
        mCameraDrawer3.setPreviewSize(point.x, point.y);
        mCamera2.setPreviewTexture(mCameraDrawer3.getSurfaceTexture());
        mCamera2.preview();
    }

    public void switchCamera() {
        cameraId = cameraId == 0 ? 1 : 0;
        mCameraDrawer3.switchCamera();
        open(cameraId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraDrawer3.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCameraDrawer3.onDrawFrame(gl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera2.close();
    }

    public void startRecord() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer3.startRecord();
            }
        });
    }

    public void stopRecord() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer3.stopRecord();
            }
        });
    }

    public void resume(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer3.onResume(auto);
            }
        });
    }

    public void pause(final boolean auto) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraDrawer3.onPause(auto);
            }
        });
    }

    public void setSavePath(String path) {
        mCameraDrawer3.setSavePath(path);
    }


}
