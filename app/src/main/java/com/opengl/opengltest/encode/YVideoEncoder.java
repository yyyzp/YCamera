package com.opengl.opengltest.encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import com.opengl.opengltest.YMediaRecorder;
import com.opengl.opengltest.rencoder.AudioRunnable;
import com.opengl.opengltest.rencoder.MediaMuxerRunnable;
import com.opengl.opengltest.rencoder.VideoRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Created by zhipengyang on 2018/7/18.
 */

public class YVideoEncoder extends Thread {
    public String TAG = "YVideoEncoder";
    private final Object lock = new Object();

    private static final File OUTPUT_DIR = Environment.getExternalStorageDirectory();
    public static final int TRY_AGAIN_LATER = -1;
    public static final int BUFFER_OK = 0;
    public static final int BUFFER_TOO_SMALL = 1;
    public static final int OUTPUT_UPDATE = 2;
    public boolean isEncoding = true;
    public boolean isExit = false;
    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 30;               // 30fps
    private static final int BIT_RATE = 5 * 1024 * 1024;               // 5mb
    private static final int IFRAME_INTERVAL = 1;           // 5 seconds between I-frames
    private static final long DURATION_SEC = 8;             // 8 seconds of video'
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private int format = 0;
    private MediaCodec mediaCodec;
    //    private MediaMuxer mediaMuxer;
    private MediaFormat mediaFormat;
    private int mTrackIndex = -1;
    private boolean mMuxerStarted = false;
    Vector<byte[]> frameBytes;
    byte[] mFrameData;

    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private long BUFFER_TIMEOUT = 1000;
    private MediaCodec.BufferInfo mBufferInfo;
    String outputPath;
    File genFile;
    public WeakReference<YMediaMuxer> mYmediaMuxer;


    public YVideoEncoder(WeakReference<YMediaMuxer> mYmediaMuxer) {
//        createFile();
        frameBytes = new Vector<byte[]>();
        mBufferInfo = new MediaCodec.BufferInfo();
        try {
            mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        format = MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar;
        this.mYmediaMuxer = mYmediaMuxer;
        configure();
    }

    private void createFile() {
        outputPath = OUTPUT_DIR + File.separator + "test.mp4";
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        mediaCodec.start();

        mFrameData = new byte[this.WIDTH * this.HEIGHT * 3 / 2];

    }

    /**
     * 开启编码器，获取输入输出缓冲区
     */
//    public void start() {
//        mediaCodec.start();
//        try {
//            mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        inputBuffers = mediaCodec.getInputBuffers();
//        outputBuffers = mediaCodec.getOutputBuffers();
//        isEncoding = true;
//    }
    public void add(byte[] data) {
//        if (frameBytes != null && isMuxerReady) {
//            frameBytes.add(data);
//        }

        if (frameBytes != null) {
            frameBytes.add(data);
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    /**
     * 向编码器输入数据，此处要求输入YUV420P的数据
     *
     * @param data      YUV数据
     * @param len       数据长度
     * @param timestamp 时间戳
     * @return
     */
    public int input(byte[] data,/* int len,*/ long timestamp) {
        if (isEncoding) {
//            byte[] yuv420sp = new byte[WIDTH * HEIGHT * 3 / 2];
//            NV21ToNV12(data, yuv420sp, WIDTH, HEIGHT);
            int index = mediaCodec.dequeueInputBuffer(BUFFER_TIMEOUT);
            Log.e("...", "" + index);
            if (index >= 0) {
                ByteBuffer inputBuffer = inputBuffers[index];
                inputBuffer.clear();
//            if (inputBuffer.capacity() < len) {
//                mediaCodec.queueInputBuffer(index, 0, 0, timestamp, 0);
//                return BUFFER_TOO_SMALL;
//            }
                inputBuffer.put(data);
                mediaCodec.queueInputBuffer(index, 0, data.length, System.nanoTime() / 1000, 0);
            } else {
                return index;
            }
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

    public void drainEncoder(final boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;

        if (endOfStream) {
            mediaCodec.signalEndOfInputStream();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ByteBuffer[] encoderOutputBuffers = mediaCodec.getOutputBuffers();
                while (isEncoding) {
                    int encoderStatus = mediaCodec.dequeueOutputBuffer(mBufferInfo, BUFFER_TIMEOUT);
                    if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        // no output available yet
//                        if (!endOfStream) {
//                            break;      // out of while
//                        } else {
//                            Log.d(TAG, "no output available, spinning to await EOS");
//                        }
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        // not expected for an encoder
                        encoderOutputBuffers = mediaCodec.getOutputBuffers();
                    } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // should happen before receiving buffers, and should only happen once
                        if (mMuxerStarted) {
                            throw new RuntimeException("format changed twice");
                        }
                        MediaFormat newFormat = mediaCodec.getOutputFormat();
                        Log.d(TAG, "encoder output format changed: " + newFormat);

                        // now that we have the Magic Goodies, start the muxer
//                        mTrackIndex = mediaMuxer.addTrack(newFormat);
//                        mediaMuxer.start();
                        mMuxerStarted = true;
                        YMediaMuxer yMediaMuxer = mYmediaMuxer.get();
                        if (yMediaMuxer != null) {
//                    mediaMuxerRunnable.addTrackIndex(MediaMuxerRunnable.TRACK_VIDEO, newFormat);
                            yMediaMuxer.setMediaFormat(YMediaMuxer.TRACK_VIDEO, newFormat);
                        }

                    } else if (encoderStatus < 0) {
                        Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                                encoderStatus);
                        // let's ignore it
                    } else {
                        ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                        if (encodedData == null) {
                            throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                                    " was null");
                        }

                        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                            // The codec config data was pulled out and fed to the muxer when we got
                            // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                            Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                            mBufferInfo.size = 0;
                        }

                        if (mBufferInfo.size != 0) {
                            if (!mMuxerStarted) {
                                throw new RuntimeException("muxer hasn't started");
                            }
                            YMediaMuxer yMediaMuxer = mYmediaMuxer.get();

                            // adjust the ByteBuffer values to match BufferInfo (not needed?)
                            encodedData.position(mBufferInfo.offset);
                            encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                            if (yMediaMuxer != null) {
                                yMediaMuxer.addMuxerData(new YMediaMuxer.MuxerData(
                                        YMediaMuxer.TRACK_VIDEO, encodedData, mBufferInfo
                                ));
                            }
//                            mediaMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);

                            Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                        }

                        mediaCodec.releaseOutputBuffer(encoderStatus, false);

                        if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            if (!endOfStream) {
                                Log.w(TAG, "reached end of stream unexpectedly");
                            } else {
                                Log.d(TAG, "end of stream reached");
                            }
                            break;      // out of while
                        }
                    }
                }
            }
        }).start();
    }

    public void release() {
        isEncoding = false;
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
        outputBuffers = null;
        inputBuffers = null;
    }

    /**
     * Releases encoder resources.
     */
    public void releaseEncoder() {
        isEncoding = false;
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
            mediaCodec = null;
        }
//
//        if (mediaMuxer != null) {
//            mediaMuxer.stop();
//            mediaMuxer.release();
//            mediaMuxer = null;
//        }
    }

    public boolean getIsEncoding() {
        return isEncoding;
    }

    public void flush() {
        mediaCodec.flush();
    }

    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }

    @Override
    public void run() {
        while (!isExit) {
            if (isEncoding) {
                if (!frameBytes.isEmpty()) {
                    byte[] bytes = this.frameBytes.remove(0);
//                if(DEBUG) Log.e("ang-->", "解码视频数据:" + bytes.length);
                    try {
                        encodeFrame(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (frameBytes.isEmpty()) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
    }

    private void encodeFrame(byte[] input/* , byte[] output */) {
        NV21toI420SemiPlanar(input, mFrameData, this.WIDTH, this.HEIGHT);

        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        int inputBufferIndex = mediaCodec.dequeueInputBuffer(BUFFER_TIMEOUT);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(mFrameData);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0,
                    mFrameData.length, System.nanoTime() / 1000, 0);
        } else {
            // either all in use, or we timed out during initial setup
        }

        int outputBufferIndex = mediaCodec.dequeueOutputBuffer(mBufferInfo, BUFFER_TIMEOUT);
        do {
            if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outputBuffers = mediaCodec.getOutputBuffers();
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mediaCodec.getOutputFormat();
                YMediaMuxer yMediaMuxer = this.mYmediaMuxer.get();
                if (yMediaMuxer != null) {
//                    mediaMuxerRunnable.addTrackIndex(MediaMuxerRunnable.TRACK_VIDEO, newFormat);
                    yMediaMuxer.setMediaFormat(YMediaMuxer.TRACK_VIDEO, newFormat);
                }

            } else if (outputBufferIndex < 0) {
            } else {
                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                if (outputBuffer == null) {
                    throw new RuntimeException("encoderOutputBuffer " + outputBufferIndex +
                            " was null");
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    YMediaMuxer yMediaMuxer = this.mYmediaMuxer.get();

//                    if (mediaMuxerRunnable != null) {
//                        MediaFormat newFormat = mMediaCodec.getOutputFormat();
//                        if (DEBUG) Log.e("angcyo-->", "添加视轨 mBufferInfo " + newFormat.toString());
////                        mediaMuxerRunnable.addTrackIndex(MediaMuxerRunnable.TRACK_VIDEO, newFormat);
//                        mediaMuxerRunnable.setMediaFormat(MediaMuxerRunnable.TRACK_VIDEO, newFormat);
//                    }
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    outputBuffer.position(mBufferInfo.offset);
                    outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);

                    if (yMediaMuxer != null) {
                        yMediaMuxer.addMuxerData(new YMediaMuxer.MuxerData(
                                YMediaMuxer.TRACK_VIDEO, outputBuffer, mBufferInfo
                        ));
                    }
                }
            }
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(mBufferInfo, BUFFER_TIMEOUT);
        } while (outputBufferIndex >= 0);
    }

    private static void NV21toI420SemiPlanar(byte[] nv21bytes, byte[] i420bytes,
                                             int width, int height) {
        System.arraycopy(nv21bytes, 0, i420bytes, 0, width * height);
        for (int i = width * height; i < nv21bytes.length; i += 2) {
            i420bytes[i] = nv21bytes[i + 1];
            i420bytes[i + 1] = nv21bytes[i];
        }
    }

    public void exit() {
        isExit = true;
    }
}
