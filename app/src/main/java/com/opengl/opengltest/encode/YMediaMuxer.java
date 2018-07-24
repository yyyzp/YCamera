package com.opengl.opengltest.encode;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.util.Log;


import com.opengl.opengltest.rencoder.AudioRunnable;
import com.opengl.opengltest.rencoder.MediaMuxerRunnable;
import com.opengl.opengltest.rencoder.VideoRunnable;
import com.opengl.opengltest.utils.FileSwapHelper;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Created by zhipengyang on 2018/7/24.
 */

public class YMediaMuxer extends Thread {
    private String path = Environment.getExternalStorageDirectory() + "/yzp.mp4";
    public static final int TRACK_VIDEO = 0;
    public static final int TRACK_AUDIO = 1;
    public static boolean DEBUG = true;
    private Vector<MuxerData> muxerDatas;
    private final Object lock = new Object();
    private MediaMuxer mMediaMuxer;
    private MediaFormat videoMediaFormat;
    private MediaFormat audioMediaFormat;
    private int videoTrackIndex = -1;
    private int audioTrackIndex = -1;
    private boolean isMediaMuxerStart = false;
    private volatile boolean isVideoAdd;
    private volatile boolean isAudioAdd;
    private YAudioRecorder audioRecorder;
    private YVideoEncoder videoEncoder;
    private boolean isExit = false;
    private static YMediaMuxer mediaMuxerThread;

    public static void startMuxer() {
        if (mediaMuxerThread == null) {
            synchronized (MediaMuxerRunnable.class) {
                if (mediaMuxerThread == null) {
                    mediaMuxerThread = new YMediaMuxer();
                    mediaMuxerThread.start();

                }
            }
        }
    }

    public static void stopMuxer() {
        if (mediaMuxerThread != null) {
            mediaMuxerThread.exit();
            try {
                mediaMuxerThread.join();
            } catch (InterruptedException e) {

            }
            mediaMuxerThread = null;
        }
    }

    public YMediaMuxer() {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            mMediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            Log.e("angcyo-->", "临时保存-->" + path);
        } catch (IOException e) {
            Log.e("angcyo-->", "new MediaMuxer.release() 异常:" + e.toString());
            e.printStackTrace();
        }

    }

    private void initMuxer() {
        muxerDatas = new Vector<>();
        audioRecorder = new YAudioRecorder(new WeakReference<YMediaMuxer>(this));
        videoEncoder = new YVideoEncoder(new WeakReference<YMediaMuxer>(this));

        audioRecorder.start();
        videoEncoder.start();
    }

    public synchronized void setMediaFormat(@TrackIndex int index, MediaFormat mediaFormat) {
        if (mMediaMuxer == null) {
            return;
        }

        if (index == TRACK_VIDEO) {
            if (videoMediaFormat == null) {
                videoMediaFormat = mediaFormat;
                videoTrackIndex = mMediaMuxer.addTrack(mediaFormat);
                isVideoAdd = true;
            }
        } else {
            if (audioMediaFormat == null) {
                audioMediaFormat = mediaFormat;
                audioTrackIndex = mMediaMuxer.addTrack(mediaFormat);
                isAudioAdd = true;
            }
        }

        requestStart();
    }

    private void requestStart() {
        synchronized (lock) {
            if (isMuxerStart()) {
                mMediaMuxer.start();
                isMediaMuxerStart = true;
                if (DEBUG) Log.e("angcyo-->", "requestStart 启动混合器 开始等待数据输入...");
                lock.notify();
            }
        }
    }

    private boolean isMuxerStart() {
        return isAudioAdd && isVideoAdd;
    }

    public void addMuxerData(MuxerData data) {
        if (muxerDatas == null) {
            return;
        }
        muxerDatas.add(data);
//        synchronized (lock) {
//            lock.notify();
//        }
    }

    private void exit() {
        if (videoEncoder != null) {
            videoEncoder.exit();
            try {
                videoEncoder.join();
            } catch (InterruptedException e) {

            }
        }
        if (audioRecorder != null) {
            audioRecorder.exit();
            try {
                audioRecorder.join();
            } catch (InterruptedException e) {

            }
        }

        isExit = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void run() {
        initMuxer();
        while (!isExit) {
            if (isMediaMuxerStart) {
                //混合器开启后
                if (muxerDatas.isEmpty()) {
                    synchronized (lock) {
                        try {
                            if (DEBUG) Log.e("ang-->", "混合等待 混合数据...");
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    MuxerData data = muxerDatas.remove(0);
                    int track;
                    if (data.trackIndex == TRACK_VIDEO) {
                        track = videoTrackIndex;
                    } else {
                        track = audioTrackIndex;
                    }
                    if (DEBUG) Log.e("ang-->", "写入混合数据 " + data.bufferInfo.size);
                    try {
                        mMediaMuxer.writeSampleData(track, data.byteBuf, data.bufferInfo);
                    } catch (Exception e) {
//                            e.printStackTrace();
//                            if (DEBUG)
                        Log.e("angcyo-->", "写入混合数据失败!" + e.toString());
                    }
                }
            } else {
                //混合器未开启
                synchronized (lock) {
                    try {
                        if (DEBUG) Log.e("angcyo-->", "混合等待开始...");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        stopMediaMuxer();
//        readyStop();
        if (DEBUG) Log.e("angcyo-->", "混合器退出...");
    }

    public static void addVideoFrameData(byte[] data) {
        if (mediaMuxerThread != null) {
            mediaMuxerThread.addVideoData(data);
        }
    }

    private void addVideoData(byte[] data) {
        if (videoEncoder != null) {
            videoEncoder.add(data);
        }
    }

    @IntDef({TRACK_VIDEO, TRACK_AUDIO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TrackIndex {
    }

    /**
     * 封装需要传输的数据类型
     */
    public static class MuxerData {
        int trackIndex;
        ByteBuffer byteBuf;
        MediaCodec.BufferInfo bufferInfo;

        public MuxerData(@TrackIndex int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {
            this.trackIndex = trackIndex;
            this.byteBuf = byteBuf;
            this.bufferInfo = bufferInfo;
        }
    }

    private void stopMediaMuxer() {
        if (mMediaMuxer != null) {
            try {
                mMediaMuxer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mMediaMuxer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAudioAdd = false;
            isVideoAdd = false;
            isMediaMuxerStart = false;
            mMediaMuxer = null;
        }
    }
}
