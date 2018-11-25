package com.opengl.opengltest.glfilter.filter.color;

import android.content.Context;
import android.opengl.GLES30;

import com.opengl.opengltest.glfilter.base.GLImageFilter;


/**
 * 饱和度
 * Created by yzp
 */

public class GLSaturationFilter extends GLImageFilter {

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    " \n" +
                    " varying mediump vec2 textureCoordinate;\n" +
                    " \n" +
                    " uniform sampler2D inputTexture;\n" +
                    " \n" +
                    " uniform float saturation;\n" +

                    " vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
                    "\n" +
                    " void main()\n" +
                    " {\n" +
                    "     vec4 textureColor = texture2D(inputTexture, textureCoordinate);\n" +
                    "     vec3 greyScaleColor = vec3(dot(luminanceWeighting,textureColor.rgb));\n" +
                    "     gl_FragColor = vec4(mix(greyScaleColor,textureColor.rgb,saturation),textureColor.w);\n" +
                    " }";

    private int mSaturation;


    public GLSaturationFilter(Context context) {
        this(context, VERTEX_SHADER, FRAGMENT_SHADER);
    }

    public GLSaturationFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
        mSaturation = GLES30.glGetUniformLocation(mProgramHandle, "saturation");
        setFloat(mSaturation, 0.0f);
    }

    public void setSaturation(float strength) {
        setFloat(mSaturation, strength);
    }

}
