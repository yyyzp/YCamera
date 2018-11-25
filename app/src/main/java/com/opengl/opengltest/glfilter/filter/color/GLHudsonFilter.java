package com.opengl.opengltest.glfilter.filter.color;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;

import com.opengl.opengltest.R;
import com.opengl.opengltest.glfilter.base.GLImageFilter;
import com.opengl.opengltest.utils.OpenGLUtils;
import com.opengl.opengltest.waterfiltercamera.GlUtil;


/**
 * 哈德森
 * Created by cain.huang on 2017/11/16.
 */

public class GLHudsonFilter extends GLImageFilter {

    private static final String FRAGMENT_SHADER =
            " precision mediump float;\n" +
            "\n" +
            " varying mediump vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputTexture;\n" +
            " uniform sampler2D blowoutTexture; //blowout;\n" +
            " uniform sampler2D overlayTexture; //overlay;\n" +
            " uniform sampler2D mapTexture; //map\n" +
            " \n" +
            " uniform float strength;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "     vec4 originColor = texture2D(inputTexture, textureCoordinate);\n" +
            "     \n" +
            "     vec4 texel = texture2D(inputTexture, textureCoordinate);\n" +
            "     \n" +
            "     vec3 bbTexel = texture2D(blowoutTexture, textureCoordinate).rgb;\n" +
            "     \n" +
            "     texel.r = texture2D(overlayTexture, vec2(bbTexel.r, texel.r)).r;\n" +
            "     texel.g = texture2D(overlayTexture, vec2(bbTexel.g, texel.g)).g;\n" +
            "     texel.b = texture2D(overlayTexture, vec2(bbTexel.b, texel.b)).b;\n" +
            "     \n" +
            "     vec4 mapped;\n" +
            "     mapped.r = texture2D(mapTexture, vec2(texel.r, .16666)).r;\n" +
            "     mapped.g = texture2D(mapTexture, vec2(texel.g, .5)).g;\n" +
            "     mapped.b = texture2D(mapTexture, vec2(texel.b, .83333)).b;\n" +
            "     mapped.a = 1.0;\n" +
            "     \n" +
            "     mapped.rgb = mix(originColor.rgb, mapped.rgb, strength);\n" +
            "\n" +
            "     gl_FragColor = mapped;\n" +
            " }";

    private int mBlowoutTexture;
    private int mBlowoutTextureLoc;

    private int mOverlayTexture;
    private int mOverlayTextureLoc;

    private int mMapTexture;
    private int mMapTextureLoc;

    private int mStrengthLoc;

    public GLHudsonFilter(Context context) {
        this(context,VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLHudsonFilter(Context context,String vertexShader, String fragmentShader) {
        super(context,vertexShader, fragmentShader);

        mBlowoutTextureLoc = GLES30.glGetUniformLocation(mProgramHandle, "blowoutTexture");
        mOverlayTextureLoc = GLES30.glGetUniformLocation(mProgramHandle, "overlayTexture");
        mMapTextureLoc = GLES30.glGetUniformLocation(mProgramHandle, "mapTexture");

        mStrengthLoc = GLES30.glGetUniformLocation(mProgramHandle, "strength");

        createTexture();

        setFloat(mStrengthLoc, 1.0f);

    }

    private void createTexture() {
        mBlowoutTexture =  OpenGLUtils.createTextureFromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.hudson_blowout));

        mOverlayTexture = OpenGLUtils.createTextureFromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.hudson_overlay));

        mMapTexture =  OpenGLUtils.createTextureFromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.hudson_map));
    }

    @Override
    public void onDrawFrameBegin() {
        super.onDrawFrameBegin();

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(getTextureType(), mBlowoutTexture);
        GLES30.glUniform1i(mBlowoutTextureLoc, 1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        GLES30.glBindTexture(getTextureType(), mOverlayTexture);
        GLES30.glUniform1i(mOverlayTextureLoc, 2);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE3);
        GLES30.glBindTexture(getTextureType(), mMapTexture);
        GLES30.glUniform1i(mMapTextureLoc, 3);

    }

    @Override
    public void release() {
        super.release();
        GLES30.glDeleteTextures(3, new int[]{mBlowoutTexture, mOverlayTexture, mMapTexture}, 0);
    }
}
