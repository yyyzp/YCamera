///*
// *
// * CameraView.java
// *
// * Created by Wuwang on 2016/11/14
// * Copyright © 2016年 深圳哎吖科技. All rights reserved.
// */
//package com.opengl.opengltest;
//
//import android.content.Context;
//import android.graphics.Point;
//import android.hardware.Camera;
//import android.util.AttributeSet;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import com.opengl.opengltest.encode.YMediaMuxer;
//
///**
// * Description:
// */
//public class YSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {
//
//    private LowCamera mCamera2;
//    private int cameraId = 0;
//    YMediaMuxer yMediaMuxer;
//
//    public YSurfaceView(Context context) {
//        this(context, null);
//    }
//
//    public YSurfaceView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    private void init() {
//        mCamera2 = new LowCamera();
//        getHolder().addCallback(this);
//        yMediaMuxer = new YMediaMuxer();
//    }
//
//
//    @Override
//    public void surfaceRedrawNeeded(SurfaceHolder holder) {
//
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        mCamera2.open(cameraId);
//        Point point = mCamera2.getPreviewSize();
//        mCamera2.setPreviewSurface(holder);
//        mCamera2.preview();
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        if (mCamera2 != null) {
//            mCamera2.stopPreview();
//        }
//    }
//
//    public LowCamera getCamera() {
//        return mCamera2;
//    }
//
//}
