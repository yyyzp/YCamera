//package com.opengl.opengltest.encode;
//
//import android.annotation.TargetApi;
//import android.media.MediaCodec;
//import android.media.MediaCodecInfo;
//import android.media.MediaFormat;
//import android.os.Build;
//import android.os.Environment;
//
//import com.opengl.opengltest.MainActivity;
//import com.opengl.opengltest.muxer.YMeidiaMuxer;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.util.concurrent.ArrayBlockingQueue;
//
//import static android.media.MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
//import static android.media.MediaCodec.BUFFER_FLAG_KEY_FRAME;
//
///**
// * Created by zhipengyang on 2018/7/19.
// */
//public class AvcEncoder {
//    private final static String TAG = "MeidaCodec";
//
//    private int TIMEOUT_USEC = 12000;
//
//    private MediaCodec mediaCodec;
//    private MediaFormat mediaFormat;
//    private YMeidiaMuxer yMeidiaMuxer;
//
//    int m_width;
//    int m_height;
//    int m_framerate;
//
//    public byte[] configbyte;
//    //待解码视频缓冲队列
//    public ArrayBlockingQueue<byte[]> YUVQueue;
//
//    public AvcEncoder(ArrayBlockingQueue YUVQueue, int width, int height, int framerate, int bitrate) {
//        this.YUVQueue = YUVQueue;
//        m_width = width;
//        m_height = height;
//        m_framerate = framerate;
//        mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
//        byte[] header_sps = {0, 0, 0, 1, 103, 100, 0, 31, -84, -76, 2, -128, 45, -56};
//        byte[] header_pps = {0, 0, 0, 1, 104, -18, 60, 97, 15, -1, -16, -121, -1, -8, 67, -1, -4, 33, -1, -2, 16, -1, -1, 8, 127, -1, -64};
//        mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(header_sps));
//        mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(header_pps));
//
//        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
//        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 5);
//        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
//        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
//        try {
//            mediaCodec = MediaCodec.createEncoderByType("video/avc");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //配置编码器参数
//        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        //启动编码器
//        mediaCodec.start();
//        yMeidiaMuxer = new YMeidiaMuxer(Environment.getExternalStorageDirectory() + File.separator + "muxer.mp4", mediaFormat);
//
//
//        //创建保存编码后数据的文件
//        createfile();
//    }
//
//    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.h264";
//    private BufferedOutputStream outputStream;
//
//    private void createfile() {
//        File file = new File(path);
//        if (file.exists()) {
//            file.delete();
//        }
//        try {
//            outputStream = new BufferedOutputStream(new FileOutputStream(file));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void StopEncoder() {
//        try {
//            mediaCodec.stop();
//            mediaCodec.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isRuning = false;
//
//    public void StopThread() {
//        isRuning = false;
////        try {
//        StopEncoder();
//        yMeidiaMuxer.stopMuxer();
////            outputStream.flush();
////            outputStream.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//
//    int count = 0;
//
////    public void addData(byte[] yuvData) {
////        if (!isEncoderStart || mParamsRef == null)
////            return;
////        try {
////            if (lastPush == 0) {
////                lastPush = System.currentTimeMillis();
////            }
////            long time = System.currentTimeMillis() - lastPush;
////            if (time >= 0) {
////                time = millisPerframe - time;
////                if (time > 0)
////                    Thread.sleep(time / 2);
////            }
////
////            //前置摄像头旋转270度，后置摄像头旋转90度
////            EncoderParams mParams = mParamsRef.get();
////            int mWidth = mParams.getFrameWidth();
////            int mHeight = mParams.getFrameHeight();
////            // 转换颜色格式
//////            if(mColorFormat == COLOR_FormatYUV420PackedPlanar){
//////                JNIUtil.nV21To420SP(yuvData, mWidth, mHeight);
//////                // 将数据写入编码器
//////                feedMediaCodecData(yuvData);
//////            }else{
////            byte[] resultBytes = new byte[mWidth * mHeight * 3 / 2];
////            YuvUtils.transferColorFormat(yuvData, mWidth, mHeight, resultBytes, mColorFormat);
////            // 将数据写入编码器
////            feedMediaCodecData(resultBytes);
//////            }
//////            JNIUtil.nV21To420SP(yuvData, mWidth, mHeight);
//////            feedMediaCodecData(yuvData);
////
////            if (time > 0)
////                Thread.sleep(time / 2);
////            lastPush = System.currentTimeMillis();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////    }
//
//    @TargetApi(21)
//    private void feedMediaCodecData(byte[] data) {
//        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
//        int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_USEC);
//        if (inputBufferIndex >= 0) {
//            // 绑定一个被空的、可写的输入缓存区inputBuffer到客户端
//            ByteBuffer inputBuffer = null;
//            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                inputBuffer = inputBuffers[inputBufferIndex];
//            } else {
//                inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex);
//            }
//            // 向输入缓存区写入有效原始数据，并提交到编码器中进行编码处理
//            inputBuffer.clear();
//            inputBuffer.put(data);
//            inputBuffer.clear();
//            mediaCodec.queueInputBuffer(inputBufferIndex, 0, data.length, System.nanoTime() / 1000, MediaCodec.BUFFER_FLAG_KEY_FRAME);
//        }
//    }
//
//    public void StartEncoderThread() {
//        Thread EncoderThread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                isRuning = true;
//                byte[] input = null;
//                long pts = 0;
//                long generateIndex = 0;
//
//                while (isRuning) {
//                    //访问用来缓冲待解码数据的队列
//                    if (YUVQueue.size() > 0) {
//                        //从缓冲队列中取出一帧
//                        input = YUVQueue.poll();
//                        byte[] yuv420sp = new byte[m_width * m_height * 3 / 2];
//                        //把待编码的视频帧转换为YUV420格式
//                        NV21ToNV12(input, yuv420sp, m_width, m_height);
//                        input = yuv420sp;
//                    }
//                    if (input != null) {
//                        try {
//                            long startMs = System.currentTimeMillis();
//                            //编码器输入缓冲区
//                            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
//                            //编码器输出缓冲区
//                            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
//                            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
//                            if (inputBufferIndex >= 0) {
//                                pts = computePresentationTime(generateIndex);
//                                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                                inputBuffer.clear();
//                                //把转换后的YUV420格式的视频帧放到编码器输入缓冲区中
//                                inputBuffer.put(input);
//                                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
//                                generateIndex += 1;
//                            }
//
//
//                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
//                            while (outputBufferIndex >= 0) {
//                                //Log.i("AvcEncoder", "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
//                                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//                                byte[] outData = new byte[bufferInfo.size];
//                                outputBuffer.get(outData);
//                                if (bufferInfo.flags == BUFFER_FLAG_CODEC_CONFIG) {
//                                    configbyte = new byte[bufferInfo.size];
//                                    configbyte = outData;
//                                } else if (bufferInfo.flags == BUFFER_FLAG_KEY_FRAME) {
//                                    byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
//                                    System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
//                                    //把编码后的视频帧从编码器输出缓冲区中拷贝出来
//                                    System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
//                                    yMeidiaMuxer.startMuxer(bufferInfo);
////                                    outputStream.write(keyframe, 0, keyframe.length);
//                                } else {
//                                    //写到文件中
//                                    yMeidiaMuxer.startMuxer(bufferInfo);
////                                    outputStream.write(outData, 0, outData.length);
//                                }
//
//                                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
//                                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
//                            }
//
//                        } catch (Throwable t) {
//                            t.printStackTrace();
//                        }
//                    } else {
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//        EncoderThread.start();
//
//    }
//
//    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
//        if (nv21 == null || nv12 == null) return;
//        int framesize = width * height;
//        int i = 0, j = 0;
//        System.arraycopy(nv21, 0, nv12, 0, framesize);
//        for (i = 0; i < framesize; i++) {
//            nv12[i] = nv21[i];
//        }
//        for (j = 0; j < framesize / 2; j += 2) {
//            nv12[framesize + j - 1] = nv21[j + framesize];
//        }
//        for (j = 0; j < framesize / 2; j += 2) {
//            nv12[framesize + j] = nv21[j + framesize - 1];
//        }
//    }
//
//    /**
//     * Generates the presentation time for frame N, in microseconds.
//     */
//    private long computePresentationTime(long frameIndex) {
//        return 132 + frameIndex * 1000000 / m_framerate;
//    }
//}