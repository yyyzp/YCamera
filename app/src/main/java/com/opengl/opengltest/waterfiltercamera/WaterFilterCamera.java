//package com.opengl.opengltest.waterfiltercamera;
//
//import android.app.Activity;
//import android.graphics.SurfaceTexture;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//
//import com.opengl.opengltest.R;
//
//import java.io.IOException;
//
///**
// * Created by zhipengyang on 2018/8/8.
// */
//
//public class WaterFilterCamera extends Activity implements SurfaceHolder.Callback{
//    SurfaceView mSurfaceView;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_water_filter);
//        mSurfaceView=findViewById(R.id.surfaceview);
//        SurfaceHolder holder=mSurfaceView.getHolder();
//        holder.addCallback(this);
//    }
//    private EglCore mEglCore;
//    private WindowSurface mDisplaySurface;
//    private int mTextureId;
//    private SurfaceTexture mCameraTexture;
//    private FrameRect mFrameRect;
//    private WaterSignature mWaterSign;
//    private int mSignTexId;
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        // 准备好EGL环境，创建渲染介质mDisplaySurface
//        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);
//        mDisplaySurface = new WindowSurface(mEglCore, holder.getSurface(), false);
//        mDisplaySurface.makeCurrent();
//
//        mTextureId = GlUtil.createExternalTextureObject();
//        mCameraTexture = new SurfaceTexture(mTextureId);
//        mCameraTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//            @Override
//            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                mHandler.sendEmptyMessage(MainHandler.MSG_FRAME_AVAILABLE);
//            }
//        });
//
//        mFrameRect.setShaderProgram(new FrameRectSProgram());
//        mWaterSign.setShaderProgram(new WaterSignSProgram());
//        mSignTexId = TextureHelper.loadTexture(ContinuousRecordActivity.this, R.mipmap.name);
//
//        try {
//            Log.d(TAG, "starting camera preview");
//            mCamera.setPreviewTexture(mCameraTexture);
//            mCamera.startPreview();
//        } catch (IOException ioe) {
//            throw new RuntimeException(ioe);
//        }
//
//        recording = mRecordEncoder.isRecording();
//
//        fbo = new FrameBuffer();
//        fbo.setup(VIDEO_HEIGHT, VIDEO_WIDTH);
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//
//    }
//}
