/*
 *
 * CameraDrawer.java
 */
package com.opengl.opengltest;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.opengl.opengltest.filter.AFilter;
import com.opengl.opengltest.filter.Beauty;
import com.opengl.opengltest.filter.CameraFilter;
import com.opengl.opengltest.filter.DouYin1;
import com.opengl.opengltest.filter.GroupFilter;
import com.opengl.opengltest.filter.NoFilter;
import com.opengl.opengltest.filter.OesFilter;
import com.opengl.opengltest.filter.ProcessFilter;
import com.opengl.opengltest.filter.WaterMarkFilter;
import com.opengl.opengltest.filter.WaterMarkFilter2;
import com.opengl.opengltest.filter.gpufilter.SlideGpuFilterGroup;
import com.opengl.opengltest.filter.gpufilter.filter.MagicBeautyFilter;
import com.opengl.opengltest.glfilter.advanced.GLImageEffectIllusionFilter;
import com.opengl.opengltest.glfilter.base.GLImageFilter;
import com.opengl.opengltest.glfilter.base.GLImageOESInputFilter;
import com.opengl.opengltest.rencoder.video.TextureMovieEncoder;
import com.opengl.opengltest.utils.EasyGlUtils;
import com.opengl.opengltest.utils.Gl2Utils;
import com.opengl.opengltest.utils.MatrixUtils;
import com.opengl.opengltest.utils.OpenGLUtils;
import com.opengl.opengltest.waterfiltercamera.FrameRect;
import com.opengl.opengltest.waterfiltercamera.FrameRectSProgram;
import com.opengl.opengltest.waterfiltercamera.GlUtil;
import com.opengl.opengltest.waterfiltercamera.TextureHelper;
import com.opengl.opengltest.waterfiltercamera.WaterSignSProgram;
import com.opengl.opengltest.waterfiltercamera.WaterSignature;

import java.io.WriteAbortedException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Description:
 */
public class CameraDrawer implements GLSurfaceView.Renderer {
    /**
     * 显示画面的filter
     */
//    private final AFilter showFilter;
    /**
     * 后台绘制的filter
     */
//    private final AFilter drawFilter;
    private OesFilter douyinFilter;
    private DouYin1 douYin1;
    /**
     * 后台绘制的filter
     */
//    private final AFilter drawFilter;
//    private final WaterMarkFilter waterMarkFilter;
    /*
    * 相机预览 美颜滤镜
    */
    private Beauty beautyFilter;

    private GLImageFilter displayFilter;
    private GLImageEffectIllusionFilter glImageEffectIllusionFilter;
    private GLImageOESInputFilter glImageOESInputFilter;
    private SurfaceTexture mSurfaceTextrue;
    /**
     * 预览数据的宽高
     */
    private int mPreviewWidth = 0, mPreviewHeight = 0;
    /**
     * 控件的宽高
     */
    private int width = 0, height = 0;

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
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private float[] OM;
    private float[] SM = new float[16];     //用于显示的变换矩阵
    private Resources res;
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

    public CameraDrawer(Resources resources) {
        this.res = resources;
        //初始化一个滤镜 也可以叫控制器
//        showFilter = new NoFilter(resources);
//        drawFilter = new CameraFilter(resources);
//        beautyFilter = new Beauty(resources);
//        douYin1=new DouYin1(resources);
//        douyinFilter = new OesFilter(resources);
//        waterMarkFilter = new WaterMarkFilter(resources);
//        waterMarkFilter.setWaterMark(BitmapFactory.decodeResource(resources, R.mipmap.fei));
//        waterMarkFilter.setPosition(30, 50, 100, 100);
//        addFilter(waterMarkFilter);
        glImageEffectIllusionFilter=new GLImageEffectIllusionFilter();
        glImageOESInputFilter=new GLImageOESInputFilter();
        displayFilter=new GLImageFilter();
        recordingEnabled = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        textureID = createTextureID();
        textureID =createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTextrue = new SurfaceTexture(textureID);
//        drawFilter.create();
//        drawFilter.setTextureId(textureID);
//        douyinFilter.create();
//        douyinFilter.setTextureId(textureID);
//        beautyFilter.create();
//        beautyFilter.setTextureId(textureID);
//        douYin1.create();
//        douYin1.setTextureId(textureID);
//        showFilter.create();
//        waterMarkFilter.create();


        if (recordingEnabled) {
            recordingStatus = RECORDING_RESUMED;
        } else {
            recordingStatus = RECORDING_OFF;
        }
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        width = i;
        height = i1;
        //清除遗留的
       /* GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
        *//**创建一个帧染缓冲区对象*//*
        GLES20.glGenFramebuffers(1, fFrame, 0);
        *//**根据纹理数量 返回的纹理索引*//*
        GLES20.glGenTextures(1, fTexture, 0);
       *//* GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                height);*//*
        *//**将生产的纹理名称和对应纹理进行绑定*//*
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[0]);
        *//**根据指定的参数 生产一个2D的纹理 调用该函数前  必须调用glBindTexture以指定要操作的纹理*//*
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mPreviewWidth, mPreviewHeight,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        useTexParameter();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);*/
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

        textureID=glImageOESInputFilter.drawFrameBuffer(textureID);
//        textureID=glImageEffectIllusionFilter.drawFrameBuffer(textureID);
        displayFilter.drawFrame(textureID);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
//        GLES20.glViewport(0, 0, mPreviewWidth, mPreviewHeight);
//        drawFilter.draw();
//        beautyFilter.draw();
//        douyinFilter.draw();
//        douYin1.draw();
//        waterMarkFilter.draw();
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        if (recordingEnabled) {
            /**说明是录制状态*/
            switch (recordingStatus) {
                case RECORDING_OFF:
                    videoEncoder = new TextureMovieEncoder();
                    videoEncoder.setPreviewSize(mPreviewWidth, mPreviewHeight);
                    videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                            savePath, mPreviewWidth, mPreviewHeight,
                            3500000, EGL14.eglGetCurrentContext(),
                            null));
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_ON:
                    break;
                case RECORDING_PAUSE:
                    videoEncoder.pauseRecording();
                    recordingStatus = RECORDING_PAUSED;
                    break;
                case RECORDING_RESUMED:
                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
                    videoEncoder.resumeRecording();
                    recordingStatus = RECORDING_ON;
                    break;
                default:
                    throw new RuntimeException("unknown recording status " + recordingStatus);
            }

        } else {
            switch (recordingStatus) {
                case RECORDING_ON:
                case RECORDING_RESUMED:
                case RECORDING_PAUSE:
                case RECORDING_RESUME:
                case RECORDING_PAUSED:
                    videoEncoder.stopRecording();
                    recordingStatus = RECORDING_OFF;
                    break;
                case RECORDING_OFF:
                    break;
                default:
                    throw new RuntimeException("unknown recording status " + recordingStatus);
            }
        }

        if (videoEncoder != null && recordingEnabled && recordingStatus == RECORDING_ON) {
            videoEncoder.setTextureId(fTexture[0]);
            videoEncoder.frameAvailable(mSurfaceTextrue);
        }
        /**绘制显示的filter*/
//        GLES20.glViewport(0, 0, width, height);
//        showFilter.setTextureId(textureID);
//        showFilter.draw();
    }

    /**
     * 设置预览效果的size
     */
    public void setPreviewSize(int width, int height) {
        if (mPreviewWidth != width || mPreviewHeight != height) {
            mPreviewWidth = width;
            mPreviewHeight = height;
        }
        Log.e("hero", "--setPreviewSize-==" + width + "---" + height);
    }


    private void calculateMatrix(int cameraId) {
        OM = MatrixUtils.getOriginalMatrix();
        //后置摄像头 需要顺时针旋转90度并左右翻转
        if (cameraId == 0) {
            Gl2Utils.flip(OM, true, false);
            Gl2Utils.rotate(OM, 90);
        } else {
            //前置摄像头需要顺时针旋转270度
            Gl2Utils.rotate(OM, 270);
        }
//        drawFilter.setMatrix(OM);
        beautyFilter.setMatrix(OM);
        douyinFilter.setMatrix(OM);
//        waterMarkFilter.setMatrix(matrix);
    }

    //根据摄像头设置纹理映射坐标
    public void setCameraId(int id) {
//        calculateMatrix(id);
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

    public void setBeautyLevel(int level) {
        beautyFilter.setBeautyLevel(level);
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
     * 创建显示的texture
     */
    private int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    /**
     * GLES 3.0
     * 创建Texture对象
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

    public void useTexParameter() {
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTextrue;
    }

}