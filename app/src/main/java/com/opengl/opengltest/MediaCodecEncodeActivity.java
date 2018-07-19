package com.opengl.opengltest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.opengl.opengltest.encode.AvcEncoder;
import com.opengl.opengltest.encode.PreviewBufferInfo;

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
    TextView capture;
    Button btnStart;
    Button btnStop;
    AvcEncoder avcEncoder;
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
        width = 1080;
        height = 1920;
        frameRate = 30;
        bitRate = 1024 * 1024 * 5;
        avcEncoder = new AvcEncoder(YUVQueue, width, height, frameRate, bitRate);

    }

    public void initPreviewFrameBuffer() {
        ySurfaceView.setFrameCallback(new YSurfaceView.OnFrameCallback() {
            @Override
            public void frameCallback(byte[] bytes, int width, int height) {
                //将当前帧图像保存在队列中
                putYUVData(bytes, bytes.length);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
//                initPreviewFrameBuffer();
                ySurfaceView.getCamera().setOnPreviewFrameCallback(new ICamera.PreviewFrameCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, int width, int height) {
                        //将当前帧图像保存在队列中
                        putYUVData(bytes, bytes.length);
                    }
                });
                //启动编码线程
                avcEncoder.StartEncoderThread();
                break;
            case R.id.btn_stop:
                avcEncoder.StopThread();
                break;
            default:
                break;
        }
    }

    public void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }
}
