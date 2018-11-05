package com.opengl.opengltest.glfilter.filter.color;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;

import com.opengl.opengltest.R;
import com.opengl.opengltest.glfilter.base.GLImageFilter;
import com.opengl.opengltest.utils.OpenGLUtils;
import com.opengl.opengltest.waterfiltercamera.GlUtil;


/**
 * 凯文
 * Created by cain.huang on 2017/11/16.
 */

public class GLKevinFilter extends GLImageFilter {

    private static final String FRAGMENT_SHADER =
            " precision mediump float;\n" +
            "\n" +
            " varying mediump vec2 textureCoordinate;\n" +
            "\n" +
            " uniform sampler2D inputTexture;\n" +
            " uniform sampler2D mapTexture;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     vec3 texel = texture2D(inputTexture, textureCoordinate).rgb;\n" +
            "\n" +
            "     vec2 lookup;\n" +
            "     lookup.y = .5;\n" +
            "\n" +
            "     lookup.x = texel.r;\n" +
            "     texel.r = texture2D(mapTexture, lookup).r;\n" +
            "\n" +
            "     lookup.x = texel.g;\n" +
            "     texel.g = texture2D(mapTexture, lookup).g;\n" +
            "\n" +
            "     lookup.x = texel.b;\n" +
            "     texel.b = texture2D(mapTexture, lookup).b;\n" +
            "\n" +
            "     gl_FragColor = vec4(texel, 1.0);\n" +
            " }\n";

    private int mMapTexture;
    private int mMapTextureLoc;

    public GLKevinFilter(Context context) {
        this(context,VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLKevinFilter(Context context,String vertexShader, String fragmentShader) {
        super(context,vertexShader, fragmentShader);
        mMapTextureLoc = GLES30.glGetUniformLocation(mProgramHandle, "mapTexture");
        createTexture();
    }

    private void createTexture() {
        mMapTexture = OpenGLUtils.createTextureFromBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.kevin_map));
    }

    @Override
    public void onDrawFrameBegin() {
        super.onDrawFrameBegin();
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(getTextureType(), mMapTexture);
        GLES30.glUniform1i(mMapTextureLoc, 1);
    }

    @Override
    public void release() {
        super.release();
        GLES30.glDeleteTextures(1, new int[]{mMapTexture}, 0);
    }
}
