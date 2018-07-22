package com.opengl.opengltest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opengl.opengltest.encode.AvcEncoder;
import com.opengl.opengltest.encode.H264EncodeConsumer;
import com.opengl.opengltest.encode.PreviewBufferInfo;
import com.opengl.opengltest.encode.YVideoEncoder;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by zhipengyang on 2018/7/19.
 */

public class MediaCodecEncodeActivity extends Activity implements View.OnClickListener {
    private String TAG = "yzp_camera";
    private static int yuvqueuesize = 10;

    //待解码视频缓冲队列，静态成员！
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);

    YSurfaceView ySurfaceView;
    YVideoEncoder yVideoEncoder;
    TextView capture;
    Button btnStart;
    Button btnStop;
    private int width;
    private int height;
    private int frameRate;
    private int bitRate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codec_endode);
        ySurfaceView = findViewById(R.id.surfaceview);
        capture = findViewById(R.id.capture);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        capture = findViewById(R.id.capture);
        initEncode();
    }

    public void initEncode() {
//        yVideoEncoder = new YVideoEncoder();
//        yVideoEncoder.configure();
//        yVideoEncoder.start();
//        width = 1280;
//        height = 720;
//        frameRate = 30;
//        bitRate = 1024 * 1024 * 5;
        yVideoEncoder = new YVideoEncoder();
        yVideoEncoder.configure();
        yVideoEncoder.start();
//        avcEncoder = new AvcEncoder(YUVQueue, width, height, frameRate, bitRate);

    }

    public void initPreviewFrameBuffer() {
        ySurfaceView.setFrameCallback(new YSurfaceView.OnFrameCallback() {
            @Override
            public void frameCallback(byte[] bytes, int width, int height) {
                yVideoEncoder.input(bytes, -1);
                //将当前帧图像保存在队列中
//                putYUVData(bytes, bytes.length);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                ySurfaceView.getCamera().setOnPreviewFrameCallback(new ICamera.PreviewFrameCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, int width, int height) {
                        yVideoEncoder.input(bytes, -1);
                    }
                });
                yVideoEncoder.drainEncoder(false);
//                initPreviewFrameBuffer();


                break;
            case R.id.btn_stop:
                yVideoEncoder.releaseEncoder();
                break;
            default:
                break;
        }
    }


}
