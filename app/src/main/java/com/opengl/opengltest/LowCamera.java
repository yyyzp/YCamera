//package com.opengl.opengltest;
//
//import android.graphics.ImageFormat;
//import android.graphics.Point;
//import android.graphics.SurfaceTexture;
//import android.hardware.Camera;
//import android.view.SurfaceHolder;
//
//import com.opengl.opengltest.utils.CameraSizeComparator;
//
//import java.io.IOException;
//import java.security.PublicKey;
//import java.util.Collections;
//import java.util.List;
//
///**
// * Created by zhipengyang on 2018/7/16.
// */
//
//public class LowCamera implements ICamera {
//    private Config mConfig;
//    private Camera mCamera;
//    private CameraSizeComparator sizeComparator;
//    private Camera.Size preSize;
//    private Point mPreSize;
//
//    public LowCamera() {
//        this.mConfig = new Config();
//        mConfig.minPreviewWidth = 720;
//        mConfig.rate = 1.778f;
//        sizeComparator = new CameraSizeComparator();
//
//    }
//
//    @Override
//    public boolean open(int cameraId) {
//        if (mCamera == null) {
//            mCamera = Camera.open(cameraId);
//        } else {
//            return false;
//        }
//        Camera.Parameters parameters = mCamera.getParameters();
//        preSize = getPropPreviewSize(parameters.getSupportedPreviewSizes(), mConfig.rate, mConfig.minPreviewWidth);
//        parameters.setPreviewSize(preSize.width, preSize.height);
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//        //设置预览格式
//        parameters.setPreviewFormat(ImageFormat.NV21);
//        mPreSize = new Point(preSize.width, preSize.height);
//        mCamera.setParameters(parameters);
//
//        mCamera.setDisplayOrientation(90);
//        return true;
//    }
//
//    @Override
//    public void setConfig(Config config) {
//        this.mConfig = config;
//    }
//
//    @Override
//    public boolean preview() {
//        if (mCamera != null) {
//            mCamera.startPreview();
//        }
//        return false;
//    }
//
//    public void stopPreview() {
//        if (mCamera != null) {
//            mCamera.stopPreview();
//            mCamera.release();
//        }
//    }
//
//    @Override
//    public boolean switchTo(int cameraId) {
//        close();
//        open(cameraId);
//        return false;
//    }
//
//    @Override
//    public void takePhoto(TakePhotoCallback callback) {
//
//    }
//
//    @Override
//    public boolean close() {
//        if (mCamera != null) {
//            mCamera.stopPreview();
//            mCamera.release();
//        }
//        return false;
//    }
//
//    @Override
//    public void setPreviewTexture(SurfaceTexture texture) {
//        if (mCamera != null) {
//            try {
//                mCamera.setPreviewTexture(texture);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public void setDataCallback(final DataCallback dataCallback) {
//        if (mCamera != null) {
//            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
//                @Override
//                public void onPreviewFrame(byte[] data, Camera camera) {
//                    dataCallback.callback(data);
//                }
//            });
//        }
//    }
//
//    public void setPreviewSurface(SurfaceHolder holder) {
//        if (mCamera != null) {
//            try {
//                mCamera.setPreviewDisplay(holder);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public Point getPreviewSize() {
//        return null;
//    }
//
//    @Override
//    public Point getPictureSize() {
//        return null;
//    }
//
//    @Override
//    public void setOnPreviewFrameCallback(final PreviewFrameCallback callback) {
//        if (mCamera != null) {
//            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
//                @Override
//                public void onPreviewFrame(byte[] data, Camera camera) {
//                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
//                }
//            });
//        }
//    }
//
//    public void setOnPreviewBufferCallback(final PreviewFrameCallback callback) {
//        if (mCamera != null) {
//            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
//                @Override
//                public void onPreviewFrame(byte[] data, Camera camera) {
//                    callback.onPreviewFrame(data, mPreSize.x, mPreSize.y);
//                }
//            });
//        }
//    }
//    private Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
//        Collections.sort(list, sizeComparator);
//
//        int i = 0;
//        for (Camera.Size s : list) {
//            if ((s.height >= minWidth) && equalRate(s, th)) {
//                break;
//            }
//            i++;
//        }
//        if (i == list.size()) {
//            i = 0;
//        }
//        return list.get(i);
//    }
//
//    private boolean equalRate(Camera.Size s, float rate) {
//        float r = (float) (s.width) / (float) (s.height);
//        if (Math.abs(r - rate) <= 0.03) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//    public Camera getmCamera(){
//        return mCamera;
//    }
//
//    public interface DataCallback {
//        void callback(byte[] data);
//    }
//}
