package com.opengl.opengltest.mediacodecplayer;

/**
 * Created by zhipengyang on 2018/9/12.
 * 播放器状态回调
 */

public interface IPlayStateListener {
    // 视频长宽比
    void videoAspect(int width, int height, float time);
}