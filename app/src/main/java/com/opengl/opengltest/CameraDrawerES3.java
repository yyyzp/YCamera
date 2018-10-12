/*
 *
 * CameraDrawer.java
 */
package com.opengl.opengltest;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.opengl.opengltest.filter.AFilter;
import com.opengl.opengltest.filter.Beauty;
import com.opengl.opengltest.filter.CameraFilter;
import com.opengl.opengltest.filter.DouyinHuanjue;
import com.opengl.opengltest.filter.NoFilter;
import com.opengl.opengltest.filter.WaterMarkFilter;
import com.opengl.opengltest.glfilter.base.GLImageOESInputFilter;
import com.opengl.opengltest.rencoder.video.TextureMovieEncoder;
import com.opengl.opengltest.utils.Gl2Utils;
import com.opengl.opengltest.utils.MatrixUtils;
import com.opengl.opengltest.utils.OpenGLUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Description:
 */
public class CameraDrawerES3 implements GLSurfaceView.Renderer {

    private GLImageOESInputFilter glImageOESInputFilter;
    private SurfaceTexture mSurfaceTextrue;
    /**
     * 预览数据的宽高
     */
    private int mPreviewWidth = 0, mPreviewHeight = 0;
    private float[] mMatrix = new float[16];

    private TextureMovieEncoder videoEncoder;
    private boolean recordingEnabled;
    private int recordingStatus;
    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;
    private static final int RECORDING_PAUSE = 3;
    private static final int RECORDING_RESUME = 4;
    private static final int RECORDING_PAUSED = 5;
    private String savePath;
    private int textureID;
    /**
     * 切换摄像头的时候
     * 会出现画面颠倒的情况
     * 通过跳帧来解决
     */
    boolean switchCamera = false;
    int skipFrame;

    public void switchCamera() {
        switchCamera = true;
    }

    public CameraDrawerES3() {
        glImageOESInputFilter=new GLImageOESInputFilter();
        recordingEnabled = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        textureID = createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTextrue = new SurfaceTexture(textureID);

        if (recordingEnabled) {
            recordingStatus = RECORDING_RESUMED;
        } else {
            recordingStatus = RECORDING_OFF;
        }
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        mSurfaceTextrue.getTransformMatrix(mMatrix);
        glImageOESInputFilter.setTextureTransformMatirx(mMatrix);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        /**更新界面中的数据*/
        mSurfaceTextrue.updateTexImage();

        //切换摄像头时跳两帧
        if (switchCamera) {
            skipFrame++;
            if (skipFrame > 1) {
                skipFrame = 0;
                switchCamera = false;
            }
            return;
        }
        glImageOESInputFilter.drawFrame(textureID);

//        if (recordingEnabled) {
//            /**说明是录制状态*/
//            switch (recordingStatus) {
//                case RECORDING_OFF:
//                    videoEncoder = new TextureMovieEncoder();
//                    videoEncoder.setPreviewSize(mPreviewWidth, mPreviewHeight);
//                    videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
//                            savePath, mPreviewWidth, mPreviewHeight,
//                            3500000, EGL14.eglGetCurrentContext(),
//                            null));
//                    recordingStatus = RECORDING_ON;
//                    break;
//                case RECORDING_ON:
//                    break;
//                case RECORDING_PAUSE:
//                    videoEncoder.pauseRecording();
//                    recordingStatus = RECORDING_PAUSED;
//                    break;
//                case RECORDING_RESUMED:
//                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
//                    videoEncoder.resumeRecording();
//                    recordingStatus = RECORDING_ON;
//                    break;
//                default:
//                    throw new RuntimeException("unknown recording status " + recordingStatus);
//            }
//
//        } else {
//            switch (recordingStatus) {
//                case RECORDING_ON:
//                case RECORDING_RESUMED:
//                case RECORDING_PAUSE:
//                case RECORDING_RESUME:
//                case RECORDING_PAUSED:
//                    videoEncoder.stopRecording();
//                    recordingStatus = RECORDING_OFF;
//                    break;
//                case RECORDING_OFF:
//                    break;
//                default:
//                    throw new RuntimeException("unknown recording status " + recordingStatus);
//            }
//        }
//
//        if (videoEncoder != null && recordingEnabled && recordingStatus == RECORDING_ON) {
//            videoEncoder.setTextureId(textureID);
//            videoEncoder.frameAvailable(mSurfaceTextrue);
//        }



    }

    /**
     * 设置预览效果的size
     */
    public void setPreviewSize(int width, int height) {
        if (mPreviewWidth != width || mPreviewHeight != height) {
            mPreviewWidth = width;
            mPreviewHeight = height;
            glImageOESInputFilter.onDisplaySizeChanged(mPreviewWidth,mPreviewHeight);
        }
        Log.e("hero", "--setPreviewSize-==" + width + "---" + height);
    }

    //根据摄像头设置纹理映射坐标
    public void setCameraId(int id) {
    }

    public void startRecord() {
        Log.d("thread", android.os.Process.myPid() + "   start");
        recordingEnabled = true;
    }

    public void stopRecord() {
        Log.d("thread", android.os.Process.myPid() + "   stop");
        recordingEnabled = false;
    }

    public void setSavePath(String path) {
        savePath = path;
    }

    private void addFilter(AFilter filter) {
        /**抵消本身的颠倒操作*/
//        filter.setMatrix(OM);
    }

    public SurfaceTexture getTexture() {
        return mSurfaceTextrue;
    }

    public void onPause(boolean auto) {
        if (auto) {
            videoEncoder.pauseRecording();
            if (recordingStatus == RECORDING_ON) {
                recordingStatus = RECORDING_PAUSED;
            }
            return;
        }
        if (recordingStatus == RECORDING_ON) {
            recordingStatus = RECORDING_PAUSE;
        }
    }

    public void onResume(boolean auto) {
        if (auto) {
            if (recordingStatus == RECORDING_PAUSED) {
                recordingStatus = RECORDING_RESUME;
            }
            return;
        }
        if (recordingStatus == RECORDING_PAUSED) {
            recordingStatus = RECORDING_RESUME;
        }
    }

    /**
     * GLES 3.0
     * 创建Texture对象
     *
     * @param textureType
     * @return
     */
    public static int createTexture(int textureType) {
        int[] textures = new int[1];
        GLES30.glGenTextures(1, textures, 0);
        OpenGLUtils.checkGlError("glGenTextures");
        int textureId = textures[0];
        GLES30.glBindTexture(textureType, textureId);
        OpenGLUtils.checkGlError("glBindTexture " + textureId);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(textureType, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        OpenGLUtils.checkGlError("glTexParameter");
        return textureId;
    }



    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTextrue;
    }

}