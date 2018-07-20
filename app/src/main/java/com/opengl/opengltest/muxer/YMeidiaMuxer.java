package com.opengl.opengltest.muxer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Answer on 2018/7/19.
 */

public class YMeidiaMuxer {
    private MediaMuxer mMediaMuxer;
    private MediaFormat mMediaFormat;
    private File file;
    private String mPath;
    private int videoTrackIndex;
    ByteBuffer byteBuffer;
    public YMeidiaMuxer(String path, MediaFormat mediaFormat) {
        this.mPath = path;
        this.mMediaFormat=mediaFormat;
        File file = new File(mPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            mMediaMuxer = new MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuffer = ByteBuffer.allocate(1024 * 1024*5);
        videoTrackIndex = mMediaMuxer.addTrack(mMediaFormat);
        mMediaMuxer.start();
    }

    public void startMuxer(MediaCodec.BufferInfo bufferInfo) {
      /*  MediaFormat videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, 1280, 720);
        byte[] header_sps = {0, 0, 0, 1, 103, 100, 0, 31, -84, -76, 2, -128, 45, -56};
        byte[] header_pps = {0, 0, 0, 1, 104, -18, 60, 97, 15, -1, -16, -121, -1, -8, 67, -1, -4, 33, -1, -2, 16, -1, -1, 8, 127, -1, -64};
        videoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
        videoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar);
        videoFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 1920 * 1080);
        videoFormat.setInteger(MediaFormat.KEY_CAPTURE_RATE, 25);*/

        mMediaMuxer.writeSampleData(videoTrackIndex, byteBuffer, bufferInfo);

    }
    public void stopMuxer(){
        mMediaMuxer.stop();
        mMediaMuxer.release();
    }

}
