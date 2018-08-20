package com.opengl.opengltest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opengl.opengltest.encode.CameraWrapper;
import com.opengl.opengltest.encode.YVideoEncoder;
import com.opengl.opengltest.rencoder.MediaMuxerRunnable;
import com.opengl.opengltest.utils.FileSwapHelper;
import com.opengl.opengltest.widget.CircularProgressView;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhipengyang on 2018/7/19.
 */

public class TestActivity extends Activity implements View.OnClickListener {
    CircularProgressView mCapture;
    CameraView mCameraView;
    private boolean recordFlag = false;//是否正在录制
    private boolean pausing = false;
    private boolean autoPausing = false;
    long timeCount = 0;//用于记录录制时间
    private long timeStep = 50;//进度条刷新的时间
    private static final int maxTime = 20000;//最长录制20s
    ExecutorService executorService;
    File file;
    String mPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/aaaaaaaaaaa.mp4";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        executorService = Executors.newSingleThreadExecutor();
        mCameraView = new CameraView(this);
        mCapture = findViewById(R.id.mCapture);
        mCapture.setOnClickListener(this);
        file=new File(mPath);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mCapture:
                if (!recordFlag) {
                    executorService.execute(recordRunnable);
                } else if (!pausing) {
                    mCameraView.pause(false);
                    pausing = true;
                } else {
                    mCameraView.resume(false);
                    pausing = false;
                }
                break;
            default:
                break;
        }
    }
    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            recordFlag = true;
            pausing = false;
            autoPausing = false;
            timeCount = 0;
            long time = System.currentTimeMillis();
            String savePath = mPath;

            try {
                mCameraView.setSavePath(savePath);
                mCameraView.startRecord();
                while (timeCount <= maxTime && recordFlag) {
                    if (pausing || autoPausing) {
                        continue;
                    }
                    mCapture.setProcess((int) timeCount);
                    Thread.sleep(timeStep);
                    timeCount += timeStep;
                }
                recordFlag = false;
                mCameraView.stopRecord();
                if (timeCount < 2000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TestActivity.this, "录像时间太短", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    recordComplete(savePath);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private void recordComplete(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCapture.setProcess(0);
                Toast.makeText(TestActivity.this, "文件保存路径：" + path, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
