package com.opengl.opengltest.glfilter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.opengl.opengltest.R;

/**
 * Created by zhipengyang on 2018/10/12.
 */

public class GlFilterActivity extends AppCompatActivity {
    private static final String FRAGMENT_CAMERA = "fragment_camera";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_glfilter);

        if (null == savedInstanceState) {
            CameraPreviewFragment fragment = new CameraPreviewFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, FRAGMENT_CAMERA)
                    .commit();
        }
    }
}
