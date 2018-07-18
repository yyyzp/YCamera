package com.opengl.opengltest.utils;

import android.hardware.Camera;

import java.util.Comparator;

/**
 * Created by zhipengyang on 2018/7/16.
 */

public class CameraSizeComparator implements Comparator<Camera.Size> {
    public int compare(Camera.Size lhs, Camera.Size rhs) {
        // TODO Auto-generated method stub
        if (lhs.height == rhs.height) {
            return 0;
        } else if (lhs.height > rhs.height) {
            return 1;
        } else {
            return -1;
        }
    }

}
