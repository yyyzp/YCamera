package com.opengl.opengltest.encode;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.opengl.opengltest.rencoder.MediaMuxerRunnable;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * Created by zhipengyang on 2018/7/24.
 */

public class YAudioRecorder extends Thread {
    private AudioRecord mAudioRecord;
    private MediaCodec mMediaCodec;
    private MediaCodecInfo audioCodecInfo;
    private MediaFormat audioFormat;
    private MediaCodec.BufferInfo mBufferInfo;
    private WeakReference<YMediaMuxer> mYmediaMexer;

    public static final boolean DEBUG = true;
    public static final String TAG = "AudioRunnable";
    public static final int SAMPLES_PER_FRAME = 1024;    // AAC, frameBytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25;    // AAC, frame/buffer/sec
    protected static final int TIMEOUT_USEC = 10000;    // 10[msec]
    private static final String MIME_TYPE = "audio/mp4a-latm";// - AAC audio (note, this is raw AAC packets, not packaged in LATM!)
    private int SAMPLE_RATE_INHZ = 44100;
    private static final int BIT_RATE = 16000;
    private boolean isRecording = true;
    private boolean isExit = false;
    private int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    private int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    public YAudioRecorder(WeakReference<YMediaMuxer> mYmediaMexer) {
        this.mYmediaMexer = mYmediaMexer;
        mBufferInfo = new MediaCodec.BufferInfo();
        final int min_buffer_size = AudioRecord.getMinBufferSize(
                SAMPLE_RATE_INHZ, CHANNEL_CONFIG,
                AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE_INHZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, min_buffer_size);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        configure();
    }

    private static final MediaCodecInfo selectAudioCodec(final String mimeType) {
        if (DEBUG) if (DEBUG) Log.v(TAG, "selectAudioCodec:");

        MediaCodecInfo result = null;
        // get the list of available codecs
        final int numCodecs = MediaCodecList.getCodecCount();
        LOOP:
        for (int i = 0; i < numCodecs; i++) {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {    // skipp decoder
                continue;
            }
            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (DEBUG) if (DEBUG)
                    Log.i(TAG, "supportedType:" + codecInfo.getName() + ",MIME=" + types[j]);
                if (types[j].equalsIgnoreCase(mimeType)) {
                    if (result == null) {
                        result = codecInfo;
                        break LOOP;
                    }
                }
            }
        }
        return result;
    }

    private void configure() {
        audioCodecInfo = selectAudioCodec(MIME_TYPE);
        if (audioCodecInfo == null) {
            if (DEBUG) Log.e(TAG, "Unable to find an appropriate codec for " + MIME_TYPE);
            return;
        }
        if (DEBUG) if (DEBUG) Log.i(TAG, "selected codec: " + audioCodecInfo.getName());

        audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE_INHZ, 1);
//        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);//CHANNEL_IN_STEREO 立体声
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE_INHZ);
//		audioFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, inputFile.length());
//      audioFormat.setLong(MediaFormat.KEY_DURATION, (long)durationInMs );
        if (DEBUG) if (DEBUG) Log.i(TAG, "format: " + audioFormat);
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioRecord.startRecording();
        mMediaCodec.start();

    }

//    public void start() {
//        mAudioRecord.startRecording();
//        mMediaCodec.start();
//        isRecording = true;
//    }

//    public void recording() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
//                int readBytes;
//                while (isRecording) {
//
//                    if (mAudioRecord != null) {
//                        buf.clear();
//                        readBytes = mAudioRecord.read(buf, SAMPLES_PER_FRAME);
//                        if (readBytes > 0) {
//                            // set audio data to encoder
//                            buf.position(readBytes);
//                            buf.flip();
////                    if(DEBUG) Log.e("ang-->", "解码音频数据:" + readBytes);
//                            try {
//                                encode(buf, readBytes, System.nanoTime() / 1000);
//                            } catch (Exception e) {
//                                if (DEBUG) Log.e("angcyo-->", "解码音频(Audio)数据 失败");
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//            /**/
//                }
//            }
//        }).start();
//    }

    private void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
        if (isExit) return;

        final ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
        final int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
            /*向编码器输入数据*/
        if (inputBufferIndex >= 0) {
            final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            if (buffer != null) {
                inputBuffer.put(buffer);
            }
//	            if (DEBUG) if(DEBUG) Log.v(TAG, "encode:queueInputBuffer");
            if (length <= 0) {
                // send EOS
//                    mIsEOS = true;
                if (DEBUG) if (DEBUG) Log.i(TAG, "send BUFFER_FLAG_END_OF_STREAM");
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, 0,
                        presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            } else {
                mMediaCodec.queueInputBuffer(inputBufferIndex, 0, length,
                        presentationTimeUs, 0);
            }
        } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
            // wait for MediaCodec encoder is ready to encode
            // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
            // will wait for maximum TIMEOUT_USEC(10msec) on each call
        }

        final YMediaMuxer muxer = mYmediaMexer.get();
        if (muxer == null) {
            if (DEBUG) Log.w(TAG, "MediaMuxerRunnable is unexpectedly null");
            return;
        }
        ByteBuffer[] encoderOutputBuffers = mMediaCodec.getOutputBuffers();
        int encoderStatus;

        do {
            encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                encoderOutputBuffers = mMediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {

                final MediaFormat format = mMediaCodec.getOutputFormat(); // API >= 16
                YMediaMuxer yMediaMuxer = this.mYmediaMexer.get();
                if (yMediaMuxer != null) {
                    if (DEBUG)
                        Log.e("angcyo-->", "添加音轨 INFO_OUTPUT_FORMAT_CHANGED " + format.toString());
//                    mediaMuxerRunnable.addTrackIndex(MediaMuxerRunnable.TRACK_AUDIO, format);
                    yMediaMuxer.setMediaFormat(YMediaMuxer.TRACK_AUDIO, format);
                }

            } else if (encoderStatus < 0) {
            } else {
                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // You shoud set output format to muxer here when you target Android4.3 or less
                    // but MediaCodec#getOutputFormat can not call here(because INFO_OUTPUT_FORMAT_CHANGED don't come yet)
                    // therefor we should expand and prepare output format from buffer data.
                    // This sample is for API>=18(>=Android 4.3), just ignore this flag here
                    if (DEBUG) if (DEBUG) Log.d(TAG, "drain:BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0 && muxer != null) {
                    mBufferInfo.presentationTimeUs = System.nanoTime() / 1000;
                    if (DEBUG) Log.e("angcyo-->", "添加音频数据 " + mBufferInfo.size);
                    muxer.addMuxerData(new YMediaMuxer.MuxerData(
                            YMediaMuxer.TRACK_AUDIO, encodedData, mBufferInfo));
                }
                // return buffer to encoder
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
            }
        } while (encoderStatus >= 0);
    }

    @Override
    public void run() {
        final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
        int readBytes;
        while (!isExit) {
            if (isRecording) {

                if (mAudioRecord != null) {
                    buf.clear();
                    readBytes = mAudioRecord.read(buf, SAMPLES_PER_FRAME);
                    if (readBytes > 0) {
                        // set audio data to encoder
                        buf.position(readBytes);
                        buf.flip();
//                    if(DEBUG) Log.e("ang-->", "解码音频数据:" + readBytes);
                        try {
                            encode(buf, readBytes, System.nanoTime() / 1000);
                        } catch (Exception e) {
                            if (DEBUG) Log.e("angcyo-->", "解码音频(Audio)数据 失败");
                            e.printStackTrace();
                        }
                    }
                }
            /**/
            }
        }
    }

    public void exit() {
        isExit = true;
    }
}
