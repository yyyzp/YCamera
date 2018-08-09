package com.opengl.opengltest.waterfiltercamera;

import android.content.Context;
import android.opengl.GLES20;

import com.opengl.opengltest.utils.ShaderUtils;

/**
 * Created by zhipengyang on 2018/8/8.
 */

public class ShaderProgram {

    protected final int programId;

    public ShaderProgram(String vertexShaderResourceStr,
                         String fragmentShaderResourceStr){
        programId = ShaderUtils.createProgram(
                vertexShaderResourceStr,
                fragmentShaderResourceStr);
    }


    public void userProgram() {
        GLES20.glUseProgram(programId);
    }

    public int getShaderProgramId() {
        return programId;
    }

    public void deleteProgram() {
        GLES20.glDeleteProgram(programId);
    }
}