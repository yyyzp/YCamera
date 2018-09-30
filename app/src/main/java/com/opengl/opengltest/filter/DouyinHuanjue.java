package com.opengl.opengltest.filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.opengl.opengltest.utils.Gl2Utils;
import com.opengl.opengltest.utils.MatrixUtils;

/**
 * Created by zhipengyang on 2018/8/13.
 */

public class DouyinHuanjue extends AFilter {

    private  int inputTexture;
    private  int lookupTexture;
    private  int hLookupTexture;
    private  int hInputTexture;

    private Bitmap mBitmap;

    public DouyinHuanjue(Resources res) {
        super(res);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh", "filter/douyin_huanjue.sh");
        hInputTexture = GLES20.glGetUniformLocation(mProgram, "inputTextureLast");
        hLookupTexture = GLES20.glGetUniformLocation(mProgram, "lookupTable");
        setLookupTable(createTexture());
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }


    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();

    }

    @Override
    protected void onBindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, getTextureId());
        GLES20.glUniform1i(mHTexture, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTexture);
        GLES20.glUniform1i(hInputTexture, 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lookupTexture);
        GLES20.glUniform1i(hLookupTexture, 2);

    }

    /**
     * 设置上一次纹理id
     *
     * @param lastTexture
     */
    public void setLastTexture(int lastTexture) {
        inputTexture = lastTexture;
    }

    /**
     * 设置lut纹理id
     *
     * @param lookupTableTexture
     */
    public void setLookupTable(int lookupTableTexture) {
        lookupTexture = lookupTableTexture;
    }

    public void setLookupBitmap(Bitmap bitmap) {
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
        }
        this.mBitmap = bitmap;
    }

    private int createTexture() {
        int textures[]=new int[1];
        if (mBitmap != null) {
            //生成纹理
            GLES20.glGenTextures(1, textures, 0);
            //生成纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        }
        return textures[0];
    }

}