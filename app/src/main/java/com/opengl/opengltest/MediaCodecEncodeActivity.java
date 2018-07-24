package com.opengl.opengltest;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opengl.opengltest.encode.AvcEncoder;
import com.opengl.opengltest.encode.CameraWrapper;
import com.opengl.opengltest.encode.H264EncodeConsumer;
import com.opengl.opengltest.encode.PreviewBufferInfo;
import com.opengl.opengltest.encode.YVideoEncoder;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by zhipengyang on 2018/7/19.
 */

public class MediaCodecEncodeActivity extends Activity implements View.OnClickListener, CameraWrapper.CamOpenOverCallback {
    private String TAG = "yzp_camera";
    private static int yuvqueuesize = 10;

    //待解码视频缓冲队列，静态成员！
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);

    YSurfaceView ySurfaceView;
    SurfaceView mSurfaceView;
    YVideoEncoder yVideoEncoder;
    TextView capture;
    Button btnStart;
    Button btnStop;
    private int width;
    private int height;
    private int frameRate;
    private int bitRate;
    SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codec_endode);
//        ySurfaceView = findViewById(R.id.surfaceview);
        mSurfaceView = findViewById(R.id.surfaceview);
        openCamera();
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder=holder;
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        capture = findViewById(R.id.capture);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        capture = findViewById(R.id.capture);
//        initEncode();
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
//                ySurfaceView.getCamera().setOnPreviewFrameCallback(new ICamera.PreviewFrameCallback() {
//                    @Override
//                    public void onPreviewFrame(byte[] bytes, int width, int height) {
//                        yVideoEncoder.input(bytes, -1);
//                    }
//                });
//                yVideoEncoder.drainEncoder(false);
//                initPreviewFrameBuffer();
                CameraWrapper.getInstance().doStartRecorder();


                break;
            case R.id.btn_stop:
//                yVideoEncoder.releaseEncoder();
                CameraWrapper.getInstance().doStopCamera();

                break;
            default:
                break;
        }
    }

    private void openCamera() {
        Thread openThread = new Thread() {
            @Override
            public void run() {
                CameraWrapper.getInstance().doOpenCamera(MediaCodecEncodeActivity.this);
            }
        };
        openThread.start();
    }

    @Override
    public void cameraHasOpened() {
        CameraWrapper.getInstance().doStartPreview(mSurfaceHolder);
    }
}
