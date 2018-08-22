package com.opengl.opengltest.filter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.opengl.opengltest.utils.Gl2Utils;

/**
 * Created by qqche_000 on 2017/8/20.
 * 水印的Filter
 */

public class WaterMarkFilter extends AFilter {
    /**
     * 水印的放置位置和宽高
     */
    private int x, y, w, h;
    /**
     * 控件的大小
     */
    private int width, height;
    /**
     * 水印图片的bitmap
     */
    private Bitmap mBitmap;
    private float[] matrix = new float[16];

    public WaterMarkFilter(Resources mRes) {
        super(mRes);

    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh",
                "shader/base_fragment.sh");
        createTexture();
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }

    public void setWaterMark(Bitmap bitmap) {
        if (this.mBitmap != null) {
            this.mBitmap.recycle();
        }
        this.mBitmap = bitmap;
    }

    @Override
    public void draw() {
        GLES20.glViewport(x, y, w == 0 ? mBitmap.getWidth() : w, h == 0 ? mBitmap.getHeight() : h);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_ALPHA);
        GLES20.glDisable(GLES20.GL_BLEND);
        super.draw();
    }

    private int[] textures = new int[1];

    private void createTexture() {
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
            matrix = Gl2Utils.getOriginalMatrix();
            //对画面进行矩阵旋转
            Gl2Utils.rotate(matrix, 180);
            setMatrix(matrix);
            setTextureId(textures[0]);
        }
    }

    /**
     *
     */
    @Override
    protected void onClear() {
        Log.e("thread", "---onClear？ 1  " + Thread.currentThread());

//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Log.e("thread", "---onClear？ 2  " + Thread.currentThread());
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Log.e("thread", "---onClear？ 3  ");
    }

    public void setPosition(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.w = width;
        this.h = height;
    }
}
