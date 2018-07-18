package com.opengl.opengltest.utils;

import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;

import java.nio.ByteBuffer;

/**
 * Created by zhipengyang on 2018/7/17.
 */

public class Muxer {
    private static String path= Environment.getExternalStorageDirectory()+"YZPCAMERA"+"mux.mp4";

    public static void mux(){
        MediaFormat videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720);
        byte[] header_sps = {0, 0, 0, 1, 103, 100, 0, 31, -84, -76, 2, -128, 45, -56};
        byte[] header_pps = {0, 0, 0, 1, 104, -18, 60, 97, 15, -1, -16, -121, -1, -8, 67, -1, -4, 33, -1, -2, 16, -1, -1, 8, 127, -1, -64};
        videoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
        videoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        videoFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1920 * 1080);
        videoFormat.setInteger(MediaFormat.KEY_CAPTURE_RATE, 25);

    }

}
