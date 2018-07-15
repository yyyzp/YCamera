package com.opengl.opengltest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by Answer on 2018/4/14.
 */

public class MyGlSurfaceView extends GLSurfaceView {
    private final MyGLRenderer mRenderer;

    public MyGlSurfaceView(Context context) {
        super(context);
        //创建 opengl es 2.0的上下文
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        // 设置Renderer 到 GLSurfaceView
        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }



}
