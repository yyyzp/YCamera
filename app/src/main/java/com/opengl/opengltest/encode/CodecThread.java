//package com.opengl.opengltest.encode;
//
//import android.graphics.ImageFormat;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.util.Log;
//
//import com.opengl.opengltest.YVideoEncoder;
//
//import java.util.Iterator;
//import java.util.Queue;
//
///**
// * Created by zhipengyang on 2018/7/19.
// */
//
//public class CodecThread extends Thread {
//    private String TAG="Codec_thread";
//    private final int MSG_ENCODE = 0;
//    private YVideoEncoder yVideoEncoder;
//    Handler codecHandler;
//    private Object mAvcEncLock;
//    private Queue<PreviewBufferInfo> mPreviewBuffers_clean;
//    private Queue<PreviewBufferInfo> mPreviewBuffers_dirty;
//    private byte[] mRawData;
//    private int format;
//    private int width;
//    private int height;
//    public CodecThread(YVideoEncoder encoder,Object mAvcEncLock,Queue<PreviewBufferInfo> mPreviewBuffers_clean,Queue<PreviewBufferInfo> mPreviewBuffers_dirty){
//        this.mAvcEncLock=mAvcEncLock;
//        this.yVideoEncoder=encoder;
//        this.mPreviewBuffers_clean=mPreviewBuffers_clean;
//        this.mPreviewBuffers_dirty=mPreviewBuffers_dirty;
//        format=ImageFormat.YV12;
//        width=1920;
//        height=1080;
//    }
//    @Override
//    public void run() {
//        Looper.prepare();
//        codecHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case MSG_ENCODE:
//                        int res = YVideoEncoder.BUFFER_OK;
//                        synchronized (mAvcEncLock) {
//                            if (mPreviewBuffers_dirty != null && mPreviewBuffers_clean != null) {
//                                Iterator<PreviewBufferInfo> ite = mPreviewBuffers_dirty.iterator();
//                                while (ite.hasNext()) {
//                                    PreviewBufferInfo info = ite.next();
//                                    byte[] data = info.buffer;
//                                    int data_size = info.size;
//                                    if (format == ImageFormat.YV12) {
//                                        if (mRawData == null || mRawData.length < data_size) {
//                                            mRawData = new byte[data_size];
//                                        }
//                                        swapYV12toI420(data, mRawData, width, height);
//                                    } else {
//                                        Log.e(TAG, "preview size MUST be YV12, cur is " + format);
//                                        mRawData = data;
//                                    }
//                                    res = yVideoEncoder.input(mRawData, data_size, info.timestamp);
//                                    if (res != YVideoEncoder.BUFFER_OK) {
////                                            Log.e(TAG, "mEncoder.input, maybe wrong:" + res);
//                                        break;        //the rest buffers shouldn't go into encoder, if the previous one get problem
//                                    } else {
//                                        ite.remove();
//                                        mPreviewBuffers_clean.add(info);
//                                        if (mCamera != null) {
//                                            mCamera.addCallbackBuffer(data);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        codecHandler.sendEmptyMessageDelayed(MSG_ENCODE, 30);
//                        break;
//                }
//            }
//        };
//        Looper.loop();
//    }
//    private void swapYV12toI420(byte[] yv12bytes, byte[] i420bytes, int width, int height) {
//        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
//        System.arraycopy(yv12bytes, width * height + width * height / 4, i420bytes, width * height, width * height / 4);
//        System.arraycopy(yv12bytes, width * height, i420bytes, width * height + width * height / 4, width * height / 4);
//    }
//}