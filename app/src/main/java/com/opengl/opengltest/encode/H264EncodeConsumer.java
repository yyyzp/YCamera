package com.opengl.opengltest.encode;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;

import com.opengl.opengltest.muxer.MediaMuxerUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * Created by zhipengyang on 2018/7/20.
 */

public class H264EncodeConsumer extends Thread {
    private static final String TAG = "H264EncodeConsumer";
    private static final String MIME_TYPE = "video/avc";
    // 间隔1s插入一帧关键帧
    private static final int FRAME_INTERVAL = 1;
    // 绑定编码器缓存区超时时间为10s
    private static final int TIMES_OUT = 10000;

    // 硬编码器
    private MediaCodec mVideoEncodec;
    private int mColorFormat;
    private boolean isExit = false;
    private boolean isEncoderStart = false;

    private boolean isAddKeyFrame = false;
    private WeakReference<EncoderParams> mParamsRef;
    private MediaFormat newFormat;
    private WeakReference<MediaMuxerUtil> mMuxerRef;

    // 码率等级
    public enum Quality{
        LOW, MIDDLE, HIGH
    }
    // 帧率
    public enum FrameRate{
        _20fps,_25fps,_30fps
    }

    public synchronized void setTmpuMuxer(MediaMuxerUtil mMuxer,EncoderParams mParams){
        this.mMuxerRef =  new WeakReference<>(mMuxer);
        this.mParamsRef = new WeakReference<>(mParams);
        MediaMuxerUtil muxer = mMuxerRef.get();

        if (muxer != null && newFormat != null) {
            muxer.addTrack(newFormat, true);
        }
    }

    private int getFrameRate() {
        if(mParamsRef == null)
            return  -1;
        EncoderParams mParams = mParamsRef.get();

        return mParams.getFrameRateDegree() == FrameRate._20fps ? 20 :
                (mParams.getFrameRateDegree()== FrameRate._25fps ? 25 : 30);
    }

    private int getBitrate() {
        if(mParamsRef == null)
            return  -1;
        EncoderParams mParams = mParamsRef.get();
        int mWidth = mParams.getFrameWidth();
        int mHeight = mParams.getFrameHeight();
        int bitRate = (int)(mWidth * mHeight * 20 * 2 *0.07f);
        if(mWidth >= 1920 || mHeight >= 1920){
            switch (mParams.getBitRateQuality()){
                case LOW:
                    bitRate *= 0.75;// 4354Kbps
                    break;
                case MIDDLE:
                    bitRate *= 1.1;// 6386Kbps
                    break;
                case HIGH:
                    bitRate *= 1.5;// 8709Kbps
                    break;
            }
        }else if(mWidth >= 1280 || mHeight >= 1280){
            switch (mParams.getBitRateQuality()){
                case LOW:
                    bitRate *= 1.0;// 2580Kbps
                    break;
                case MIDDLE:
                    bitRate *= 1.4;// 3612Kbps
                    break;
                case HIGH:
                    bitRate *= 1.9;// 4902Kbps
                    break;
            }
        }else if(mWidth >= 640 || mHeight >= 640){
            switch (mParams.getBitRateQuality()){
                case LOW:
                    bitRate *= 1.4;// 1204Kbps
                    break;
                case MIDDLE:
                    bitRate *= 2.1;// 1806Kbps
                    break;
                case HIGH:
                    bitRate *= 3;// 2580Kbps
                    break;
            }
        }
        return bitRate;
    }

    private void startCodec() {
        isExit = false;
        if(mParamsRef == null)
            return;
        EncoderParams mParams = mParamsRef.get();
        try {
            MediaCodecInfo mCodecInfo = selectSupportCodec(MIME_TYPE);
            if (mCodecInfo == null) {
                    Log.d(TAG, "匹配编码器失败" + MIME_TYPE);
                return;
            }
            mColorFormat = selectSupportColorFormat(mCodecInfo, MIME_TYPE);
            mVideoEncodec = MediaCodec.createByCodecName(mCodecInfo.getName());
        } catch (IOException e) {
                Log.e(TAG, "创建编码器失败" + e.getMessage());
            e.printStackTrace();
        }
        MediaFormat mFormat;
        if(mParams.isVertical()){
            // 手机垂直拍摄
            mFormat = MediaFormat.createVideoFormat(MIME_TYPE, mParams.getFrameHeight(), mParams.getFrameWidth());
        } else {
            // 手机水平拍摄
            mFormat = MediaFormat.createVideoFormat(MIME_TYPE, mParams.getFrameWidth(), mParams.getFrameHeight());
        }
        mFormat.setInteger(MediaFormat.KEY_BIT_RATE, getBitrate());
        mFormat.setInteger(MediaFormat.KEY_FRAME_RATE, getFrameRate());
        mFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, mColorFormat);         // 颜色格式
        mFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, FRAME_INTERVAL);
        if (mVideoEncodec != null) {
            mVideoEncodec.configure(mFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mVideoEncodec.start();
            isEncoderStart = true;
                Log.d(TAG, "配置、启动视频编码器");
        }
    }

    private void stopCodec() {
        if (mVideoEncodec != null) {
            mVideoEncodec.stop();
            mVideoEncodec.release();
            mVideoEncodec = null;
            isAddKeyFrame = false;
            isEncoderStart = false;
                Log.d(TAG, "关闭视频编码器");
        }
    }

    long millisPerframe = 1000 / 20;
    long lastPush = 0;


    public void addData(byte[] yuvData) {
        if(! isEncoderStart || mParamsRef == null)
            return;
        try {
            if (lastPush == 0) {
                lastPush = System.currentTimeMillis();
            }
            long time = System.currentTimeMillis() - lastPush;
            if (time >= 0) {
                time = millisPerframe - time;
                if (time > 0)
                    Thread.sleep(time / 2);
            }

            //前置摄像头旋转270度，后置摄像头旋转90度
            EncoderParams mParams = mParamsRef.get();
            int mWidth = mParams.getFrameWidth();
            int mHeight = mParams.getFrameHeight();
            // 转换颜色格式
//            if(mColorFormat == COLOR_FormatYUV420PackedPlanar){
//                JNIUtil.nV21To420SP(yuvData, mWidth, mHeight);
//                // 将数据写入编码器
//                feedMediaCodecData(yuvData);
//            }else{
            byte[] resultBytes = new byte[mWidth * mHeight *3 / 2];
//            YuvUtils.transferColorFormat(yuvData,mWidth,mHeight,resultBytes,mColorFormat);
            NV21ToNV12(yuvData,resultBytes,mWidth,mHeight);
            // 将数据写入编码器
            feedMediaCodecData(resultBytes);
//            }
//            JNIUtil.nV21To420SP(yuvData, mWidth, mHeight);
//            feedMediaCodecData(yuvData);

            if (time > 0)
                Thread.sleep(time / 2);
            lastPush = System.currentTimeMillis();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(21)
    private void feedMediaCodecData(byte[] data) {
        ByteBuffer[] inputBuffers = mVideoEncodec.getInputBuffers();
        int inputBufferIndex = mVideoEncodec.dequeueInputBuffer(TIMES_OUT);
        if (inputBufferIndex >= 0) {
            // 绑定一个被空的、可写的输入缓存区inputBuffer到客户端
            ByteBuffer inputBuffer = null;
            if (!isLollipop()) {
                inputBuffer = inputBuffers[inputBufferIndex];
            } else {
                inputBuffer = mVideoEncodec.getInputBuffer(inputBufferIndex);
            }
            // 向输入缓存区写入有效原始数据，并提交到编码器中进行编码处理
            inputBuffer.clear();
            inputBuffer.put(data);
            inputBuffer.clear();
            mVideoEncodec.queueInputBuffer(inputBufferIndex, 0, data.length, System.nanoTime() / 1000, MediaCodec.BUFFER_FLAG_KEY_FRAME);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        // 初始化编码器
        if (!isEncoderStart) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startCodec();
        }
        // 如果编码器没有启动或者没有图像数据，线程阻塞等待
        while (!isExit) {
            ByteBuffer[] outputBuffers = mVideoEncodec.getOutputBuffers();

            // 返回一个输出缓存区句柄，当为-1时表示当前没有可用的输出缓存区
            // mBufferInfo参数包含被编码好的数据，timesOut参数为超时等待的时间
            MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = -1;
            do {
                outputBufferIndex = mVideoEncodec.dequeueOutputBuffer(mBufferInfo, TIMES_OUT);
                if (outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        Log.i(TAG, "获得编码器输出缓存区超时");
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // 如果API小于21，APP需要重新绑定编码器的输入缓存区；
                    // 如果API大于21，则无需处理INFO_OUTPUT_BUFFERS_CHANGED
                    if (!isLollipop()) {
                        outputBuffers = mVideoEncodec.getOutputBuffers();
                    }
                } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    // 编码器输出缓存区格式改变，通常在存储数据之前且只会改变一次
                    // 这里设置混合器视频轨道，如果音频已经添加则启动混合器（保证音视频同步）
                    synchronized (H264EncodeConsumer.this) {
                        newFormat = mVideoEncodec.getOutputFormat();
                        if(mMuxerRef != null){
                            MediaMuxerUtil muxer = mMuxerRef.get();
                            if (muxer != null) {
                                muxer.addTrack(newFormat, true);
                            }
                        }
                    }
                        Log.i(TAG, "编码器输出缓存区格式改变，添加视频轨道到混合器");
                } else {
                    // 获取一个只读的输出缓存区inputBuffer ，它包含被编码好的数据
                    ByteBuffer outputBuffer = null;
                    if (!isLollipop()) {
                        outputBuffer = outputBuffers[outputBufferIndex];
                    } else {
                        outputBuffer = mVideoEncodec.getOutputBuffer(outputBufferIndex);
                    }
                    // 如果API<=19，需要根据BufferInfo的offset偏移量调整ByteBuffer的位置
                    // 并且限定将要读取缓存区数据的长度，否则输出数据会混乱
                    if (isKITKAT()) {
                        outputBuffer.position(mBufferInfo.offset);
                        outputBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                    }
                    // 根据NALU类型判断帧类型
                    int type = outputBuffer.get(4) & 0x1F;
                        Log.d(TAG, "------还有数据---->" + type);
                    if (type == 7 || type == 8) {
                            Log.e(TAG, "------PPS、SPS帧(非图像数据)，忽略-------");
                        mBufferInfo.size = 0;
                    } else if (type == 5) {
                        // 录像时，第1秒画面会静止，这是由于音视轨没有完全被添加
                        // Muxer没有启动
                        // 添加视频流到混合器
                        if(mMuxerRef != null){
                            MediaMuxerUtil muxer = mMuxerRef.get();
                            if (muxer != null) {
                                Log.i(TAG,"------编码混合  视频关键帧数据-----");
                                muxer.pumpStream(outputBuffer, mBufferInfo, true);
                            }
                            isAddKeyFrame = true;
                        }
                    } else {
                        if (isAddKeyFrame) {
                            // 添加视频流到混合器
                            if(isAddKeyFrame && mMuxerRef != null){
                                MediaMuxerUtil muxer = mMuxerRef.get();
                                if (muxer != null) {
                                    Log.i(TAG,"------编码混合  视频普通帧数据-----"+mBufferInfo.size);
                                    muxer.pumpStream(outputBuffer, mBufferInfo, true);
                                }
                            }
                        }
                    }
                    // 处理结束，释放输出缓存区资源
                    mVideoEncodec.releaseOutputBuffer(outputBufferIndex, false);
                }
            } while (outputBufferIndex >= 0);
        }
        stopCodec();
    }

    public void exit() {
        isExit = true;
    }

    /**
     * 遍历所有编解码器，返回第一个与指定MIME类型匹配的编码器
     * 判断是否有支持指定mime类型的编码器
     */
    private MediaCodecInfo selectSupportCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            // 判断是否为编码器，否则直接进入下一次循环
            if (!codecInfo.isEncoder()) {
                continue;
            }
            // 如果是编码器，判断是否支持Mime类型
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    /**
     * 根据mime类型匹配编码器支持的颜色格式
     */
    private int selectSupportColorFormat(MediaCodecInfo mCodecInfo, String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = mCodecInfo.getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isCodecRecognizedFormat(colorFormat)) {
                return colorFormat;
            }
        }
        return 0;
    }

    private boolean isCodecRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                //video/avc编码器支持COLOR_FormatYUV420SemiPlanar格式
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                return false;
        }
    }

    private boolean isLollipop() {
        // API>=21
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private boolean isKITKAT() {
        // API<=19
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
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
}