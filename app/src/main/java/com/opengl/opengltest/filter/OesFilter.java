/*
 *
 * CameraFilter.java
 * 
 * Created by Wuwang on 2016/11/19
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.opengl.opengltest.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.util.Arrays;

/**
 * Description:
 */
public class OesFilter extends AFilter{

    private int mHCoordMatrix;
    private int mScale;
    private float[] mCoordMatrix= Arrays.copyOf(OM,16);

    public OesFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/oes_base_vertex.sh","shader/oes_base_fragment.sh");
        mHCoordMatrix=GLES20.glGetUniformLocation(mProgram,"vCoordMatrix");
        mScale=GLES20.glGetUniformLocation(mProgram,"scale");
    }

    public void setCoordMatrix(float[] matrix){
        this.mCoordMatrix=matrix;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniformMatrix4fv(mHCoordMatrix,1,false,mCoordMatrix,0);
        GLES20.glUniform1f(mScale,1.50f);

    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,getTextureId());
        GLES20.glUniform1i(mHTexture,getTextureType());
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

}
