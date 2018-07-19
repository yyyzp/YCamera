package com.opengl.opengltest;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zhipengyang on 2018/7/18.
 */

public class YVideoEncoder {
    public static final int TRY_AGAIN_LATER = -1;
    public static final int BUFFER_OK = 0;
    public static final int BUFFER_TOO_SMALL = 1;
    public static final int OUTPUT_UPDATE = 2;

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int BIT_RATE = 5 * 1024 * 1024;               // 5mb
    private static final int IFRAME_INTERVAL = 5;           // 5 seconds between I-frames
    private static final long DURATION_SEC = 8;             // 8 seconds of video'
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private int format = 0;
    private MediaCodec mediaCodec;
    private MediaFormat mediaFormat;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private long BUFFER_TIMEOUT = 0;
    private MediaCodec.BufferInfo mBufferInfo;

    public YVideoEncoder() {
        mBufferInfo = new MediaCodec.BufferInfo();
        try {
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        format = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
    }

    public void initParams() {

    }

    /**
     * 配置编码器，需要配置颜色、帧率、比特率以及视频宽高
     *
     * @param 视频的宽
     * @param 视频的高
     * @param 视频比特率
     * @param 视频帧率
     */
    public void configure() {
        if (mediaFormat == null) {
            mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, WIDTH, HEIGHT);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
            if (format != 0) {
                mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, format);
            }
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL); //关键帧间隔时间 单位s
        }
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
    }

    /**
     * 开启编码器，获取输入输出缓冲区
     */
    public void start() {
        mediaCodec.start();
        inputBuffers = mediaCodec.getInputBuffers();
        outputBuffers = mediaCodec.getOutputBuffers();
    }

    /**
     * 向编码器输入数据，此处要求输入YUV420P的数据
     *
     * @param data      YUV数据
     * @param len       数据长度
     * @param timestamp 时间戳
     * @return
     */
    public int input(byte[] data, int len, long timestamp) {
        int index = mediaCodec.dequeueInputBuffer(BUFFER_TIMEOUT);
        Log.e("...", "" + index);
        if (index >= 0) {
            ByteBuffer inputBuffer = inputBuffers[index];
            inputBuffer.clear();
            if (inputBuffer.capacity() < len) {
                mediaCodec.queueInputBuffer(index, 0, 0, timestamp, 0);
                return BUFFER_TOO_SMALL;
            }
            inputBuffer.put(data, 0, len);
            mediaCodec.queueInputBuffer(index, 0, len, timestamp, 0);
        } else {
            return index;
        }
        return BUFFER_OK;
    }

    /**
     * 输出编码后的数据
     *
     * @param data 数据
     * @param len  有效数据长度
     * @param ts   时间戳
     * @return
     */
    public int output(/*out*/byte[] data,/* out */int[] len,/* out */long[] ts) {
        int i = mediaCodec.dequeueOutputBuffer(mBufferInfo, BUFFER_TIMEOUT);
        if (i >= 0) {
            if (mBufferInfo.size > data.length) return BUFFER_TOO_SMALL;
            outputBuffers[i].position(mBufferInfo.offset);
            outputBuffers[i].limit(mBufferInfo.offset + mBufferInfo.size);
            outputBuffers[i].get(data, 0, mBufferInfo.size);
            len[0] = mBufferInfo.size;
            ts[0] = mBufferInfo.presentationTimeUs;
            mediaCodec.releaseOutputBuffer(i, false);
        } else if (i == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            outputBuffers = mediaCodec.getOutputBuffers();
            return OUTPUT_UPDATE;
        } else if (i == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            mediaFormat = mediaCodec.getOutputFormat();
            return OUTPUT_UPDATE;
        } else if (i == MediaCodec.INFO_TRY_AGAIN_LATER) {
            return TRY_AGAIN_LATER;
        }

        return BUFFER_OK;
    }

    public void release() {
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
        outputBuffers = null;
        inputBuffers = null;
    }

    public void flush() {
        mediaCodec.flush();
    }


}
