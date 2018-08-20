/*
 *
 * CameraDrawer.java
 */
package com.opengl.opengltest;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.opengl.opengltest.filter.AFilter;
import com.opengl.opengltest.filter.Beauty;
import com.opengl.opengltest.filter.OesFilter;
import com.opengl.opengltest.filter.WaterMarkFilter;
import com.opengl.opengltest.utils.Gl2Utils;
import com.opengl.opengltest.waterfiltercamera.FrameRect;
import com.opengl.opengltest.waterfiltercamera.FrameRectSProgram;
import com.opengl.opengltest.waterfiltercamera.TextureHelper;
import com.opengl.opengltest.waterfiltercamera.WaterSignSProgram;
import com.opengl.opengltest.waterfiltercamera.WaterSignature;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Description:
 */
public class CameraDrawer implements GLSurfaceView.Renderer {

    private float[] matrix = new float[16];
    private SurfaceTexture surfaceTexture;
    private int width, height;
    private int dataWidth, dataHeight;
    private AFilter mOesFilter;
    private WaterMarkFilter waterMarkFilter;
    private Beauty beautyFilter;
    private int cameraId = 1;
    private int mSignTexId;
    private int mTexId;
    private WaterSignature waterSignature;
    private FrameRect frameRect;
    private Resources mRes;
    private final float[] mTmpMatrix = new float[16];

    public CameraDrawer(Resources res) {
        waterSignature = new WaterSignature();
//        frameRect=new FrameRect();
        mRes = res;
//        beautyFilter=new Beauty(res);
        mOesFilter = new OesFilter(res);
        waterMarkFilter = new WaterMarkFilter(res);
        waterMarkFilter.setWaterMark(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher));
        waterMarkFilter.setPosition(30, 50, 0, 0);
    }

    public void setDataSize(int dataWidth, int dataHeight) {
        this.dataWidth = dataWidth;
        this.dataHeight = dataHeight;
        calculateMatrix();
    }

    public void setViewSize(int width, int height) {
        this.width = width;
        this.height = height;
        calculateMatrix();
    }

    private void calculateMatrix() {
        Gl2Utils.getShowMatrix(matrix, this.dataWidth, this.dataHeight, this.width, this.height);
        if (cameraId == 1) {
            Gl2Utils.flip(matrix, true, false);
            Gl2Utils.rotate(matrix, 90);
        } else {
            Gl2Utils.rotate(matrix, 270);
        }
//        beautyFilter.setMatrix(matrix);
        mOesFilter.setMatrix(matrix);
//        waterMarkFilter.setMatrix(matrix);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    public void setCameraId(int id) {
        this.cameraId = id;
        calculateMatrix();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        frameRect.setShaderProgram(new FrameRectSProgram());

        int texture = createTextureID();
        surfaceTexture = new SurfaceTexture(texture);
//        mTexId=texture;
        mOesFilter.create();
        mOesFilter.setTextureId(texture);
//        beautyFilter.create();
//        beautyFilter.setTextureId(texture);
//        waterSignature.setShaderProgram(new WaterSignSProgram());
//        mSignTexId = TextureHelper.loadTexture(mRes, R.mipmap.fei);
        waterMarkFilter.create();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        setViewSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        onClear();
//        GLES20.glViewport(0, 0, width, height);
//        frameRect.drawFrame(mTexId, mTmpMatrix);
//        GLES20.glViewport(0, 0, 288, 144);
//        waterSignature.drawFrame(mSignTexId);
        GLES20.glViewport(0, 0, width, height);
        mOesFilter.draw();
//        beautyFilter.draw();
//        GLES20.glViewport(0, 0, 288, 144);
//        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_ALPHA);
//        waterSignature.drawFrame(mSignTexId);
//        GLES20.glViewport(0, 0, width, height);
//        GLES20.glDisable(GLES20.GL_BLEND);
        waterMarkFilter.draw();

//        waterMarkFilter.draw();

    }

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
     * 清除画布
     */
    protected void onClear() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
}
