package com.opengl.opengltest.glfilter.filter.color;

import android.content.Context;
import android.opengl.GLES30;

import com.opengl.opengltest.glfilter.base.GLImageFilter;


/**
 * 亮度
 * Created by yzp
 */

public class GLBrightnessFilter extends GLImageFilter {

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
            " \n" +
            " varying mediump vec2 textureCoordinate;\n" +
            " \n" +
            " uniform sampler2D inputTexture;\n" +
            " \n" +
            " uniform float strength;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     vec4 textureColor = texture2D(inputTexture, textureCoordinate);\n" +
            "     gl_FragColor = vec4((textureColor.rgb+vec3(strength)),textureColor.w);\n" +
            " }";

    private int mStrengthLoc;


    public GLBrightnessFilter(Context context) {
        this(context,VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLBrightnessFilter(Context context, String vertexShader, String fragmentShader) {
        super(context,vertexShader, fragmentShader);
        mStrengthLoc = GLES30.glGetUniformLocation(mProgramHandle, "strength");
        setFloat(mStrengthLoc, 0.0f);
    }

    public void setStrength(float strength){
        setFloat(mStrengthLoc,strength);
    }

}
