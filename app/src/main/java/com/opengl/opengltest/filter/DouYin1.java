package com.opengl.opengltest.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.opengl.opengltest.utils.Gl2Utils;

/**
 * Created by zhipengyang on 2018/8/13.
 */

public class DouYin1 extends AFilter {

    private int[] mHTexture = new int[3];
    private int[] textures = new int[3];

    private float aaCoef;
    private float mixCoef;
    private float opacity;
    private int iternum;

    private int mWidth = 720;
    private int mHeight = 1280;


    public DouYin1(Resources res) {
        super(res);
//        setFlag(5);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/oes_base_vertex.sh", "filter/douyin_huanjue.sh");
        mHTexture[0] = GLES20.glGetUniformLocation(mProgram, "inputTexture");
        mHTexture[1] = GLES20.glGetUniformLocation(mProgram, "inputTextureLast");
        mHTexture[2] = GLES20.glGetUniformLocation(mProgram, "lookupTable");
//        createTexture();
    }
    protected void onDraw(){
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition,2, GLES20.GL_FLOAT, false, 0,mVerBuffer);
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }
    private void a(int a, float b, float c) {
        this.iternum = a;
        this.aaCoef = b;
        this.mixCoef = c;
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();

    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,getTextureId());
        GLES20.glUniform1i(mHTexture[0],0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
        GLES20.glUniform1i(mHTexture[1], 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[2]);
        GLES20.glUniform1i(mHTexture[2], 2);
    }


    private void createTexture() {
        //生成纹理
        GLES20.glGenTextures(2, textures, 1);
        for (int i = 1; i < 3; i++) {
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
//            setTextureId(textures[i]);
        }
    }

}