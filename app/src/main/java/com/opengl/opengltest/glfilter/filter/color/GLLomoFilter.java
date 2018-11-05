package com.opengl.opengltest.glfilter.filter.color;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;

import com.opengl.opengltest.R;
import com.opengl.opengltest.glfilter.base.GLImageFilter;
import com.opengl.opengltest.utils.OpenGLUtils;
import com.opengl.opengltest.waterfiltercamera.GlUtil;


/**
 * LOMO
 * Created by cain.huang on 2017/11/16.
 */

public class GLLomoFilter extends GLImageFilter {

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
            " \n" +
            " varying mediump vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputTexture;\n" +
            " uniform sampler2D mapTexture;\n" +
            " uniform sampler2D vignetteTexture;\n" +
            " \n" +
            " uniform float strength;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     vec4 originColor = texture2D(inputTexture, textureCoordinate);\n" +
            "     vec3 texel = texture2D(inputTexture, textureCoordinate).rgb;\n" +
            "\n" +
            "     vec2 red = vec2(texel.r, 0.16666);\n" +
            "     vec2 green = vec2(texel.g, 0.5);\n" +
            "     vec2 blue = vec2(texel.b, 0.83333);\n" +
            "\n" +
            "     texel.rgb = vec3(\n" +
            "                      texture2D(mapTexture, red).r,\n" +
            "                      texture2D(mapTexture, green).g,\n" +
            "                      texture2D(mapTexture, blue).b);\n" +
            "\n" +
            "     vec2 tc = (2.0 * textureCoordinate) - 1.0;\n" +
            "     float d = dot(tc, tc);\n" +
            "     vec2 lookup = vec2(d, texel.r);\n" +
            "     texel.r = texture2D(vignetteTexture, lookup).r;\n" +
            "     lookup.y = texel.g;\n" +
            "     texel.g = texture2D(vignetteTexture, lookup).g;\n" +
            "     lookup.y = texel.b;\n" +
            "     texel.b\t= texture2D(vignetteTexture, lookup).b;\n" +
            "\n" +
            "     texel.rgb = mix(originColor.rgb, texel.rgb, strength);\n" +
            "\n" +
            "     gl_FragColor = vec4(texel,1.0);\n" +
            " }";


    private int mMapTexture;
    private int mMapTextureLoc;

    private int mVignetteTexture;
    private int mVignetteTextureLoc;

    private int mStrengthLoc;

    public GLLomoFilter(Context context) {
        this(context,VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLLomoFilter(Context context,String vertexShader, String fragmentShader) {
        super(context,vertexShader, fragmentShader);

        mMapTextureLoc = GLES30.glGetUniformLocation(mProgramHandle, "mapTexture");
        mVignetteTextureLoc = GLES30.glGetUniformLocation(mProgramHandle, "vignetteTexture");
        mStrengthLoc = GLES30.glGetUniformLocation(mProgramHandle, "strength");
        createTexture();
        setFloat(mStrengthLoc, 1.0f);
    }

    private void createTexture() {
        mMapTexture = OpenGLUtils.createTextureFromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.lomo_map));
        mVignetteTexture =OpenGLUtils.createTextureFromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.lomo_vignette));
    }

    @Override
    public void onDrawFrameBegin() {
        super.onDrawFrameBegin();
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(getTextureType(), mMapTexture);
        GLES30.glUniform1i(mMapTextureLoc, 1);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        GLES30.glBindTexture(getTextureType(), mVignetteTexture);
        GLES30.glUniform1i(mVignetteTextureLoc, 2);
    }

    @Override
    public void release() {
        super.release();
        GLES30.glDeleteTextures(2, new int[]{mMapTexture, mVignetteTexture}, 0);
    }
}
