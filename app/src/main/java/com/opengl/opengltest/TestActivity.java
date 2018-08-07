package com.opengl.opengltest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opengl.opengltest.encode.CameraWrapper;
import com.opengl.opengltest.encode.YVideoEncoder;
import com.opengl.opengltest.rencoder.MediaMuxerRunnable;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by zhipengyang on 2018/7/19.
 */

public class TestActivity extends Activity {

    CameraView cameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
        cameraView = new CameraView(this);
        setContentView(cameraView);
    }
}
