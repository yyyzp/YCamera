package com.opengl.opengltest.glfilter.render;

import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;


import com.opengl.opengltest.glfilter.utils.GLImageFilterType;

import java.lang.ref.WeakReference;

/**
 * 预览渲染Handler
 * Created by cain.huang on 2017/11/3.
 */

class RenderHandler extends Handler {

    // Surface创建
    public static final int MSG_SURFACE_CREATED = 0x001;
    // Surface改变
    public static final int MSG_SURFACE_CHANGED = 0x002;
    // Surface销毁
    public static final int MSG_SURFACE_DESTROYED = 0x003;
    // 渲染
    public static final int MSG_RENDER = 0x004;
    // 改变滤镜
    public static final int MSG_FILTER_TYPE = 0x005;
    // 开始录制
    public static final int MSG_START_RECORDING = 0x006;
    // 停止录制
    public static final int MSG_STOP_RECORDING = 0x008;
    // 重新打开相机
    public static final int MSG_REOPEN_CAMERA = 0x009;
    // 切换相机
    public static final int MSG_SWITCH_CAMERA = 0x010;
    // 预览帧回调
    public static final int MSG_PREVIEW_CALLBACK = 0x011;
    // 拍照
    public static final int MSG_TAKE_PICTURE = 0x012;
    // 计算fps
    public static final int MSG_CALCULATE_FPS = 0x013;

    private WeakReference<RenderThread> mWeakRenderThread;

    public RenderHandler(RenderThread thread) {
        super(thread.getLooper());
        mWeakRenderThread = new WeakReference<RenderThread>(thread);
    }

    @Override
    public void handleMessage(Message msg) {
        if (mWeakRenderThread == null || mWeakRenderThread.get() == null) {
            return;
        }
        RenderThread thread = mWeakRenderThread.get();
        switch (msg.what) {

            // surfaceCreated
            case MSG_SURFACE_CREATED:
                thread.surfaceCreated((SurfaceHolder)msg.obj);
                break;

            // surfaceChanged
            case MSG_SURFACE_CHANGED:
                thread.surfaceChanged(msg.arg1, msg.arg2);
                break;

            // surfaceDestroyed;
            case MSG_SURFACE_DESTROYED:
                thread.surfaceDestroyed();
                break;

            // 帧可用（考虑同步的问题）
            case MSG_RENDER:
                thread.drawFrame();
                break;

            // 切换滤镜
            case MSG_FILTER_TYPE:
                thread.changeFilter((GLImageFilterType) msg.obj);
                break;

            // 开始录制
            case MSG_START_RECORDING:
                thread.startRecording();
                break;

            // 停止录制
            case MSG_STOP_RECORDING:
                thread.stopRecording();
                break;

            // 重新打开相机
            case MSG_REOPEN_CAMERA:
                thread.openCamera();
                break;

            // 切换相机
            case MSG_SWITCH_CAMERA:
                thread.switchCamera();
                break;

            // 预览帧回调
            case MSG_PREVIEW_CALLBACK:
                thread.onPreviewCallback((byte[])msg.obj);
                break;

            // 拍照
            case MSG_TAKE_PICTURE:
                thread.takePicture();
                break;

            // 计算fps
            case MSG_CALCULATE_FPS:
                thread.calculateFps();
                break;

            default:
                throw new IllegalStateException("Can not handle message what is: " + msg.what);
        }
    }
}
