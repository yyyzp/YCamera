package com.opengl.opengltest;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.Surface;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhipengyang on 2018/7/18.
 */

public class YMediaRecorder {
    String filePath = Environment.getExternalStorageDirectory() + File.separator + "rec.mp4";
    MediaRecorder mediaRecorder;
    Camera mCamera;
    Surface mSurface;

    public YMediaRecorder(Camera camera, Surface surface) {
        this.mCamera = camera;
        //调用camera.unloack 否则 报start failed:-19
        mCamera.unlock();
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaRecorder = new MediaRecorder();

        mediaRecorder.reset();
        //为mediaRecorder设置camera 否则报start failed:-19
        mediaRecorder.setCamera(mCamera);

        // 1.必须先设置音视频采集源
        // 设置从麦克风采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置从摄像头采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // 2.必须在设置声音编码格式、图像编码格式之前设置
        // 设置视频文件的输出格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        // 3.设置声音编码的格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        // 3.设置图像编码的格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // 4.设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoSize(1920, 1080);
        // 4.设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoFrameRate(30);
        // 4.设置录制的视频比特率。必须放在设置编码和格式的后面，否则报错
        mediaRecorder.setVideoEncodingBitRate(1024 * 1024 * 5);
        // 指定使用Surface来预览视频
        mediaRecorder.setPreviewDisplay(surface);
        //指定输出文件目录
        mediaRecorder.setOutputFile(filePath);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void startRecorder() {
        mediaRecorder.start();
    }

    public void stopRecorder() {
        mediaRecorder.stop();
        mediaRecorder.release();
    }
}
