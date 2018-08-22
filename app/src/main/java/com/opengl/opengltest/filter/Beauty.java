package com.opengl.opengltest.filter;

import android.content.res.Resources;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

/**
 * Created by zhipengyang on 2018/8/13.
 */

public class Beauty extends AFilter {

    private int gHaaCoef;
    private int gHmixCoef;
    private int gHiternum;
    private int gHWidth;
    private int gHHeight;
    private int gHopacity;

    private float aaCoef;
    private float mixCoef;
    private float opacity;
    private int iternum;

    private int mWidth=720;
    private int mHeight=1280;


    public Beauty(Resources res) {
        super(res);
        setFlag(5);
        setSmoothOpacity(Float.valueOf("0.5"));
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/beauty/beauty.vert", "shader/beauty/beauty2.frag");
        gHaaCoef= GLES20.glGetUniformLocation(mProgram,"aaCoef");
        gHmixCoef=GLES20.glGetUniformLocation(mProgram,"mixCoef");
        gHiternum=GLES20.glGetUniformLocation(mProgram,"iternum");
        gHWidth=GLES20.glGetUniformLocation(mProgram,"mWidth");
        gHHeight=GLES20.glGetUniformLocation(mProgram,"mHeight");
        gHopacity=GLES20.glGetUniformLocation(mProgram,"opacity");
    }

    @Override
    public void setFlag(int flag) {
        super.setFlag(flag);
        switch (flag){
            case 1:
                a(1,0.19f,0.54f);
                break;
            case 2:
                a(2,0.29f,0.54f);
                break;
            case 3:
                a(3,0.17f,0.39f);
                break;
            case 4:
                a(3,0.25f,0.54f);
                break;
            case 5:
                a(4,0.13f,0.54f);
                break;
            case 6:
                a(4,0.19f,0.69f);
                break;
            default:
                a(0,0f,0f);
                break;
        }
    }

    private void a(int a,float b,float c){
        this.iternum=a;
        this.aaCoef=b;
        this.mixCoef=c;
    }

    @Override
    protected void onSizeChanged(int width, int height) {
        this.mWidth=width;
        this.mHeight=height;
    }

    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        GLES20.glUniform1i(gHWidth,mWidth);
        GLES20.glUniform1i(gHHeight,mHeight);
        GLES20.glUniform1f(gHaaCoef,aaCoef);
        GLES20.glUniform1f(gHmixCoef,mixCoef);
        GLES20.glUniform1i(gHiternum,iternum);
        GLES20.glUniform1f(gHopacity,opacity);
    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+getTextureType());
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,getTextureId());
        GLES20.glUniform1i(mHTexture,getTextureType());
    }
    /**
     * 设置磨皮程度
     * @param percent 百分比
     */
    public void setSmoothOpacity(float percent) {
        float opacity;
        if (percent <= 0) {
            opacity = 0.0f;
        } else {
            opacity = calculateOpacity(percent);
        }
       this.opacity=opacity;
    }

    /**
     * 根据百分比计算出实际的磨皮程度
     * @param percent
     * @return
     */
    private float calculateOpacity(float percent) {
        float result = 0.0f;

        // TODO 可以加入分段函数，对不同等级的磨皮进行不一样的处理
        result = (float) (1.0f - (1.0f - percent + 0.02) / 2.0f);

        return result;
    }

}