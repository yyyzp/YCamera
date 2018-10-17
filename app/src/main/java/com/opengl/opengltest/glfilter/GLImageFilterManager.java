package com.opengl.opengltest.glfilter;

import android.content.Context;

import com.opengl.opengltest.glfilter.advanced.GLImageEffectIllusionFilter;
import com.opengl.opengltest.glfilter.base.GLImageFilter;
import com.opengl.opengltest.glfilter.filter.GLImageBlackWhiteFilter;
import com.opengl.opengltest.glfilter.utils.GLImageFilterIndex;
import com.opengl.opengltest.glfilter.utils.GLImageFilterType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Filter管理类
 * Created by cain on 17-7-25.
 */

public final class GLImageFilterManager {

    private static HashMap<GLImageFilterType, GLImageFilterIndex> mIndexMap = new HashMap<GLImageFilterType, GLImageFilterIndex>();

    static {
        mIndexMap.put(GLImageFilterType.NONE, GLImageFilterIndex.NoneIndex);
        mIndexMap.put(GLImageFilterType.EFFECTILLUSION, GLImageFilterIndex.AdvanceIndex);


    }

    private GLImageFilterManager() {
    }

    public static GLImageFilter getFilter(Context context, GLImageFilterType type) {
        switch (type) {

            case NONE:      // 没有滤镜
            case SOURCE:    // 原图
                return new GLImageFilter(context);
            case BLACKWHITE:
                return new GLImageBlackWhiteFilter(context);
            default:
                return new GLImageFilter(context);
        }
    }

    /**
     * 获取特效滤镜 TODO 暂未实现
     *
     * @param context
     * @param type
     * @return
     */
    public static GLImageFilter getEffectFilter(Context context, GLImageFilterType type) {
        switch (type) {
            case EFFECTILLUSION:
                return new GLImageEffectIllusionFilter(context);
            default:
                return new GLImageFilter(context);
        }
    }

    /**
     * 获取层级
     *
     * @param Type
     * @return
     */
    public static GLImageFilterIndex getIndex(GLImageFilterType Type) {
        GLImageFilterIndex index = mIndexMap.get(Type);
        if (index != null) {
            return index;
        }
        return GLImageFilterIndex.NoneIndex;
    }

    /**
     * 获取滤镜类型
     *
     * @return
     */
    public static List<GLImageFilterType> getFilterTypes() {
        List<GLImageFilterType> filterTypes = new ArrayList<>();
        filterTypes.add(GLImageFilterType.SOURCE);
        filterTypes.add(GLImageFilterType.BLACKWHITE);
        return filterTypes;
    }
    /**
     * 获取特效滤镜类型
     *
     * @return
     */
    public static List<GLImageFilterType> getEffectFilterTypes() {
        List<GLImageFilterType> filterTypes = new ArrayList<>();
        filterTypes.add(GLImageFilterType.SOURCE);
        filterTypes.add(GLImageFilterType.EFFECTILLUSION);
        return filterTypes;
    }
    /**
     * 获取滤镜名称
     *
     * @return
     */
    public static List<String> getFilterNames() {
        List<String> filterNames = new ArrayList<>();

        filterNames.add("原图");
        filterNames.add("黑白");
        filterNames.add("幻觉");

        return filterNames;
    }
}
