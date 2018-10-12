package com.opengl.opengltest.glfilter;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.opengl.opengltest.R;
import com.opengl.opengltest.glfilter.camera.CameraEngine;
import com.opengl.opengltest.glfilter.camera.CameraParam;
import com.opengl.opengltest.glfilter.listener.OnCameraCallback;
import com.opengl.opengltest.glfilter.listener.OnCaptureListener;
import com.opengl.opengltest.glfilter.listener.OnFpsListener;
import com.opengl.opengltest.glfilter.listener.OnRecordListener;
import com.opengl.opengltest.glfilter.model.AspectRatio;
import com.opengl.opengltest.glfilter.model.GalleryType;
import com.opengl.opengltest.glfilter.multimedia.VideoCombiner;
import com.opengl.opengltest.glfilter.recorder.PreviewRecorder;
import com.opengl.opengltest.glfilter.render.PreviewRenderer;
import com.opengl.opengltest.glfilter.utils.CainSurfaceView;
import com.opengl.opengltest.glfilter.utils.HorizontalIndicatorView;
import com.opengl.opengltest.glfilter.utils.PathConstraints;
import com.opengl.opengltest.glfilter.utils.PermissionConfirmDialogFragment;
import com.opengl.opengltest.glfilter.utils.PermissionErrorDialogFragment;
import com.opengl.opengltest.glfilter.utils.PermissionUtils;
import com.opengl.opengltest.glfilter.utils.ShutterButton;
import com.opengl.opengltest.glfilter.utils.StringUtils;
import com.opengl.opengltest.mediacodecplayer.AspectFrameLayout;
import com.opengl.opengltest.utils.BitmapUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 相机预览页面
 */
public class CameraPreviewFragment extends Fragment implements View.OnClickListener,
        HorizontalIndicatorView.OnIndicatorListener {

    private static final String TAG = "CameraPreviewFragment";
    private static final boolean VERBOSE = true;

    private static final String FRAGMENT_DIALOG = "dialog";

    // 对焦大小
    private static final int FocusSize = 100;

    // 相机权限使能标志
    private boolean mCameraEnable = false;
    // 存储权限使能标志
    private boolean mStorageWriteEnable = false;
    // 是否需要等待录制完成再跳转
    private boolean mNeedToWaitStop = false;
    // 显示贴纸页面
    private boolean isShowingStickers = false;
    // 显示滤镜页面
    private boolean isShowingFilters = false;
    // 当前索引
    private int mFilterIndex = 0;

    // 处于延时拍照状态
    private boolean mDelayTaking = false;

    // 预览参数
    private CameraParam mCameraParam;

    // Fragment主页面
    private View mContentView;
    // 预览部分
    private AspectFrameLayout mAspectLayout;
    private CainSurfaceView mCameraSurfaceView;
    // fps显示
    private TextView mFpsView;
    // 对比按钮
    private Button mBtnCompare;
    // 顶部Button
    private Button mBtnSetting;
    private Button mBtnViewPhoto;
    private Button mBtnSwitch;

    // 倒计时
    private TextView mCountDownView;

    // 快门按钮
    private ShutterButton mBtnShutter;
    // 滤镜按钮
    private Button mBtnEffect;
    // 视频删除按钮
    private Button mBtnRecordDelete;
    // 视频预览按钮
    private Button mBtnRecordPreview;

    // 相机类型指示文字
    private List<String> mIndicatorText = new ArrayList<String>();

    // 主线程Handler
    private Handler mMainHandler;
    // 持有该Fragment的Activity，onAttach/onDetach中绑定/解绑，主要用于解决getActivity() = null的情况
    private Activity mActivity;


    public CameraPreviewFragment() {
        mCameraParam = CameraParam.getInstance();
        mCameraParam.setAspectRatio(AspectRatio.Ratio_16_9);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
            mCameraParam.brightness = -1;
        mMainHandler = new Handler(context.getMainLooper());
        mCameraEnable = PermissionUtils.permissionChecking(mActivity, Manifest.permission.CAMERA);
        mStorageWriteEnable = PermissionUtils.permissionChecking(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        mCameraParam.audioPermitted = PermissionUtils.permissionChecking(mActivity, Manifest.permission.RECORD_AUDIO);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化相机渲染引擎
        PreviewRenderer.getInstance()
                .setCameraCallback(mCameraCallback)
                .setFpsCallback(mFpsListener)
                .initRenderer(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_camera_preview, container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mCameraEnable) {
            initView(mContentView);
        } else {
            requestCameraPermission();
        }
    }

    /**
     * 初始化页面
     * @param view
     */
    private void initView(View view) {
        mAspectLayout = (AspectFrameLayout) view.findViewById(R.id.layout_aspect);
        mAspectLayout.setAspectRatio(mCameraParam.currentRatio);
        mCameraSurfaceView = new CainSurfaceView(mActivity);
        mCameraSurfaceView.addOnTouchScroller(mTouchScroller);
        mCameraSurfaceView.addMultiClickListener(mMultiClickListener);
        mAspectLayout.addView(mCameraSurfaceView);
        mAspectLayout.requestLayout();
        // 绑定需要渲染的SurfaceView
        PreviewRenderer.getInstance().setSurfaceView(mCameraSurfaceView);

        mFpsView = (TextView) view.findViewById(R.id.tv_fps);

        mBtnShutter = (ShutterButton) view.findViewById(R.id.btn_shutter);
        mBtnShutter.setOnShutterListener(mShutterListener);
        mBtnShutter.setOnClickListener(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        registerHomeReceiver();
        mBtnShutter.setEnableOpened(false);
    }


    @Override
    public void onPause() {
        super.onPause();
        unRegisterHomeReceiver();
        mBtnShutter.setEnableOpened(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContentView = null;
    }

    @Override
    public void onDestroy() {

        // 关掉渲染引擎
        PreviewRenderer.getInstance().destroyRenderer();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    /**
     * 处理返回事件
     * @return
     */
    public boolean onBackPressed() {
        if (isShowingFilters) {
            return true;
        } else if (isShowingStickers) {
            isShowingStickers = false;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_shutter) {
            takePicture();
        }
    }

    @Override
    public void onIndicatorChanged(int currentIndex) {
        if (currentIndex == 0) {
            mCameraParam.mGalleryType = GalleryType.GIF;
            mBtnShutter.setIsRecorder(false);
        } else if (currentIndex == 1) {
            mCameraParam.mGalleryType = GalleryType.PICTURE;
            // 拍照状态
            mBtnShutter.setIsRecorder(false);
            if (!mStorageWriteEnable) {
                requestStoragePermission();
            }
        } else if (currentIndex == 2) {
            mCameraParam.mGalleryType = GalleryType.VIDEO;
            // 录制视频状态
            mBtnShutter.setIsRecorder(true);
            // 请求录音权限
            if (!mCameraParam.audioPermitted) {
                requestRecordSoundPermission();
            }
        }
        // 显示时间
        if (currentIndex == 2) {
            mCountDownView.setVisibility(View.VISIBLE);
        } else {
            mCountDownView.setVisibility(View.GONE);
        }
    }



    /**
     * 切换相机
     */
    private void switchCamera() {
        if (!mCameraEnable) {
            requestCameraPermission();
            return;
        }
        PreviewRenderer.getInstance().switchCamera();
    }


    /**
     * 拍照
     */
    private void takePicture() {
        if (mStorageWriteEnable) {
            if (mCameraParam.mGalleryType == GalleryType.PICTURE) {
                if (mCameraParam.takeDelay && !mDelayTaking) {
                    mDelayTaking = true;
                    mMainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDelayTaking = false;
                            PreviewRenderer.getInstance().takePicture();
                        }
                    }, 3000);
                } else {
                    PreviewRenderer.getInstance().takePicture();
                }
            }
        } else {
            requestStoragePermission();
        }
    }


    // ------------------------------- SurfaceView 滑动、点击回调 ----------------------------------
    private CainSurfaceView.OnTouchScroller mTouchScroller = new CainSurfaceView.OnTouchScroller() {

        @Override
        public void swipeBack() {
//            if (mEffectFragment != null) {
//                mFilterIndex = mEffectFragment.getCurrentFilterIndex();
//            }
//            mFilterIndex++;
//            mFilterIndex = mFilterIndex % GLImageFilterManager.getFilterTypes().size();
//            PreviewRenderer.getInstance()
//                    .changeFilterType(GLImageFilterManager.getFilterTypes().get(mFilterIndex));
//            if (mEffectFragment != null) {
//                mEffectFragment.scrollToCurrentFilter(mFilterIndex);
//            }
        }

        @Override
        public void swipeFrontal() {
//            if (mEffectFragment != null) {
//                mFilterIndex = mEffectFragment.getCurrentFilterIndex();
//            }
//            mFilterIndex--;
//            if (mFilterIndex < 0) {
//                int count = GLImageFilterManager.getFilterTypes().size();
//                mFilterIndex = count > 0 ? count - 1 : 0;
//            }
//            PreviewRenderer.getInstance()
//                    .changeFilterType(GLImageFilterManager.getFilterTypes().get(mFilterIndex));
//
//            if (mEffectFragment != null) {
//                mEffectFragment.scrollToCurrentFilter(mFilterIndex);
//            }
        }

        @Override
        public void swipeUpper(boolean startInLeft, float distance) {
            if (VERBOSE) {
                Log.d(TAG, "swipeUpper, startInLeft ? " + startInLeft + ", distance = " + distance);
            }
        }

        @Override
        public void swipeDown(boolean startInLeft, float distance) {
            if (VERBOSE) {
                Log.d(TAG, "swipeDown, startInLeft ? " + startInLeft + ", distance = " + distance);
            }
        }

    };

    /**
     * 单双击回调监听
     */
    private CainSurfaceView.OnMultiClickListener mMultiClickListener = new CainSurfaceView.OnMultiClickListener() {

        @Override
        public void onSurfaceSingleClick(final float x, final float y) {
            // 单击隐藏贴纸和滤镜页面
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
//                    hideStickerView();
//                    hideEffectView();
                }
            });

            // 如果处于触屏拍照状态，则直接拍照，不做对焦处理
            if (mCameraParam.touchTake) {
                takePicture();
                return;
            }

            // 判断是否支持对焦模式
            if (CameraEngine.getInstance().getCamera() != null) {
                List<String> focusModes = CameraEngine.getInstance().getCamera()
                        .getParameters().getSupportedFocusModes();
                if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    CameraEngine.getInstance().setFocusArea(CameraEngine.getFocusArea((int)x, (int)y,
                            mCameraSurfaceView.getWidth(), mCameraSurfaceView.getHeight(), FocusSize));
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCameraSurfaceView.showFocusAnimation();
                        }
                    });
                }
            }
        }

        @Override
        public void onSurfaceDoubleClick(float x, float y) {
            switchCamera();
        }

    };




    // -------------------------------------- fps回调 -------------------------------------------
    private OnFpsListener mFpsListener = new OnFpsListener() {
        @Override
        public void onFpsCallback(final float fps) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mCameraParam.showFps) {
                        mFpsView.setText("fps = " + fps);
                        mFpsView.setVisibility(View.VISIBLE);
                    } else {
                        mFpsView.setVisibility(View.GONE);
                    }
                }
            });
        }
    };



    // ------------------------------------ 预览回调 ---------------------------------------------
    private OnCameraCallback mCameraCallback = new OnCameraCallback() {

        @Override
        public void onCameraOpened() {

        }

        @Override
        public void onPreviewCallback(byte[] data) {
            if (mBtnShutter != null && !mBtnShutter.isEnableOpened()) {
                mBtnShutter.setEnableOpened(true);
            }
            // 这里解决第一次打开时较慢的情况
                requestRender();

        }


    };

    /**
     * 请求渲染
     */
    private void requestRender() {
        PreviewRenderer.getInstance().requestRender();
    }




    // ------------------------------------ 录制回调 -------------------------------------------
    private ShutterButton.OnShutterListener mShutterListener = new ShutterButton.OnShutterListener() {

        @Override
        public void onStartRecord() {
            if (mCameraParam.mGalleryType == GalleryType.PICTURE) {
                return;
            }

            // 隐藏删除按钮
            if (mCameraParam.mGalleryType == GalleryType.VIDEO) {
                mBtnRecordPreview.setVisibility(View.GONE);
                mBtnRecordDelete.setVisibility(View.GONE);
            }
            mBtnShutter.setProgressMax((int) PreviewRecorder.getInstance().getMaxMilliSeconds());
            // 添加分割线
            mBtnShutter.addSplitView();

            // 是否允许录制音频
            boolean enableAudio = mCameraParam.audioPermitted && mCameraParam.recordAudio
                    && mCameraParam.mGalleryType == GalleryType.VIDEO;

            // 计算输入纹理的大小
            int width = mCameraParam.previewWidth;
            int height = mCameraParam.previewHeight;
            if (mCameraParam.orientation == 90 || mCameraParam.orientation == 270) {
                width = mCameraParam.previewHeight;
                height = mCameraParam.previewWidth;
            }
            // 开始录制
            PreviewRecorder.getInstance()
                    .setRecordType(mCameraParam.mGalleryType == GalleryType.VIDEO ? PreviewRecorder.RecordType.Video : PreviewRecorder.RecordType.Gif)
                    .setOutputPath(PathConstraints.getVideoPath())
                    .enableAudio(enableAudio)
                    .setRecordSize(width, height)
                    .setOnRecordListener(mRecordListener)
                    .startRecord();
        }

        @Override
        public void onStopRecord() {
            PreviewRecorder.getInstance().stopRecord();
        }

        @Override
        public void onProgressOver() {
            // 如果最后一秒内点击停止录制，则仅仅关闭录制按钮，因为前面已经停止过了，不做跳转
            // 如果最后一秒内没有停止录制，否则停止录制并跳转至预览页面
            if (PreviewRecorder.getInstance().isLastSecondStop()) {
                // 关闭录制按钮
                mBtnShutter.closeButton();
            } else {
                stopRecordOrPreviewVideo();
            }
        }
    };

    /**
     * 录制监听器
     */
    private OnRecordListener mRecordListener = new OnRecordListener() {

        @Override
        public void onRecordStarted() {
            // 编码器已经进入录制状态，则快门按钮可用
            mBtnShutter.setEnableEncoder(true);
        }

        @Override
        public void onRecordProgressChanged(final long duration) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 设置进度
                    mBtnShutter.setProgress(duration);
                    // 设置时间
                    mCountDownView.setText(StringUtils.generateMillisTime((int) duration));
                }
            });
        }

        @Override
        public void onRecordFinish() {
            // 编码器已经完全释放，则快门按钮可用
            mBtnShutter.setEnableEncoder(true);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    // 处于录制状态点击了预览按钮，则需要等待完成再跳转， 或者是处于录制GIF状态
                    if (mNeedToWaitStop || mCameraParam.mGalleryType == GalleryType.GIF) {
                        // 开始预览
                        stopRecordOrPreviewVideo();
                    }
                    // 显示删除按钮
                    if (mCameraParam.mGalleryType == GalleryType.VIDEO) {
                        mBtnRecordPreview.setVisibility(View.VISIBLE);
                        mBtnRecordDelete.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    };



    /**
     * 停止录制或者预览视频
     */
    private void stopRecordOrPreviewVideo() {
        if (PreviewRecorder.getInstance().isRecording()) {
            mNeedToWaitStop = true;
            PreviewRecorder.getInstance().stopRecord(false);
        } else {
            mNeedToWaitStop = false;
            // 销毁录制线程
            PreviewRecorder.getInstance().destroyRecorder();
//            combinePath = PathConstraints.getVideoPath();
//            PreviewRecorder.getInstance().combineVideo(combinePath, mCombineListener);
        }
    }


    /**
     * 请求相机权限
     */
    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            PermissionConfirmDialogFragment.newInstance(getString(R.string.request_camera_permission), PermissionUtils.REQUEST_CAMERA_PERMISSION, true)
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{ Manifest.permission.CAMERA},
                    PermissionUtils.REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * 请求存储权限
     */
    private void requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionConfirmDialogFragment.newInstance(getString(R.string.request_storage_permission), PermissionUtils.REQUEST_STORAGE_PERMISSION)
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionUtils.REQUEST_STORAGE_PERMISSION);
        }
    }

    /**
     * 请求录音权限
     */
    private void requestRecordSoundPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            PermissionConfirmDialogFragment.newInstance(getString(R.string.request_sound_permission), PermissionUtils.REQUEST_SOUND_PERMISSION)
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{ Manifest.permission.RECORD_AUDIO},
                    PermissionUtils.REQUEST_SOUND_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionErrorDialogFragment.newInstance(getString(R.string.request_camera_permission), PermissionUtils.REQUEST_CAMERA_PERMISSION, true)
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            } else {
                mCameraEnable = true;
                initView(mContentView);
            }
        } else if (requestCode == PermissionUtils.REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionErrorDialogFragment.newInstance(getString(R.string.request_storage_permission), PermissionUtils.REQUEST_STORAGE_PERMISSION)
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            } else {
                mStorageWriteEnable = true;
            }
        } else if (requestCode == PermissionUtils.REQUEST_SOUND_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionErrorDialogFragment.newInstance(getString(R.string.request_sound_permission), PermissionUtils.REQUEST_SOUND_PERMISSION)
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            } else {
                mCameraParam.audioPermitted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 注册服务
     */
    private void registerHomeReceiver() {
        if (mActivity != null) {
            IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            mActivity.registerReceiver(mHomePressReceiver, homeFilter);
        }
    }

    /**
     * 注销服务
     */
    private void unRegisterHomeReceiver() {
        if (mActivity != null) {
            mActivity.unregisterReceiver(mHomePressReceiver);
        }
    }

    /**
     * Home按键监听服务
     */
    private BroadcastReceiver mHomePressReceiver = new BroadcastReceiver() {
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.isEmpty(reason)) {
                    return;
                }
                // 当点击了home键时需要停止预览，防止后台一直持有相机
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    // 停止录制
                    if (PreviewRecorder.getInstance().isRecording()) {
                        // 取消录制
                        PreviewRecorder.getInstance().cancelRecording();
                        // 重置进入条
                        mBtnShutter.setProgress((int) PreviewRecorder.getInstance().getVisibleDuration());
                        // 删除分割线
                        mBtnShutter.deleteSplitView();
                        // 关闭按钮
                        mBtnShutter.closeButton();
                        // 更新时间
                        mCountDownView.setText(PreviewRecorder.getInstance().getVisibleDurationString());
                    }
                }
            }
        }
    };


}
