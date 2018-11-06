package com.opengl.opengltest.glfilter;

import android.content.Context;

import com.opengl.opengltest.glfilter.advanced.GLImageEffectIllusionFilter;
import com.opengl.opengltest.glfilter.advanced.GLImageEffectScaleFilter;
import com.opengl.opengltest.glfilter.advanced.GLImageShiftRGBFilter;
import com.opengl.opengltest.glfilter.advanced.GLImageSoulStuffFilter;
import com.opengl.opengltest.glfilter.base.GLImageFilter;
import com.opengl.opengltest.glfilter.filter.GLImageBlackWhiteFilter;
import com.opengl.opengltest.glfilter.filter.color.GLAmaroFilter;
import com.opengl.opengltest.glfilter.filter.color.GLAnitqueFilter;
import com.opengl.opengltest.glfilter.filter.color.GLBlackCatFilter;
import com.opengl.opengltest.glfilter.filter.color.GLBrooklynFilter;
import com.opengl.opengltest.glfilter.filter.color.GLCalmFilter;
import com.opengl.opengltest.glfilter.filter.color.GLCoolFilter;
import com.opengl.opengltest.glfilter.filter.color.GLEarlyBirdFilter;
import com.opengl.opengltest.glfilter.filter.color.GLEmeraldFilter;
import com.opengl.opengltest.glfilter.filter.color.GLEvergreenFilter;
import com.opengl.opengltest.glfilter.filter.color.GLFairyTaleFilter;
import com.opengl.opengltest.glfilter.filter.color.GLFreudFilter;
import com.opengl.opengltest.glfilter.filter.color.GLHealthyFilter;
import com.opengl.opengltest.glfilter.filter.color.GLHefeFilter;
import com.opengl.opengltest.glfilter.filter.color.GLHudsonFilter;
import com.opengl.opengltest.glfilter.filter.color.GLKevinFilter;
import com.opengl.opengltest.glfilter.filter.color.GLLatteFilter;
import com.opengl.opengltest.glfilter.filter.color.GLLomoFilter;
import com.opengl.opengltest.glfilter.filter.color.GLNostalgiaFilter;
import com.opengl.opengltest.glfilter.filter.color.GLRomanceFilter;
import com.opengl.opengltest.glfilter.filter.color.GLSakuraFilter;
import com.opengl.opengltest.glfilter.filter.color.GLSaturationFilter;
import com.opengl.opengltest.glfilter.filter.color.GLSunsetFilter;
import com.opengl.opengltest.glfilter.filter.color.GLWhiteCatFilter;
import com.opengl.opengltest.glfilter.filter.color.GLBrightnessFilter;
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

            // AMARO
            case AMARO:
                return new GLAmaroFilter(context);
            // 古董
            case ANTIQUE:
                return new GLAnitqueFilter(context);

            // 黑猫
            case BLACKCAT:
                return new GLBlackCatFilter(context);

            // 黑白
            case BLACKWHITE:
                return new GLImageBlackWhiteFilter(context);

            // 布鲁克林
            case BROOKLYN:
                return new GLBrooklynFilter(context);

            // 冷静
            case CALM:
                return new GLCalmFilter(context);

            // 冷色调
            case COOL:
                return new GLCoolFilter(context);

            // 晨鸟
            case EARLYBIRD:
                return new GLEarlyBirdFilter(context);

            // 翡翠
            case EMERALD:
                return new GLEmeraldFilter(context);

            // 常绿
            case EVERGREEN:
                return new GLEvergreenFilter(context);

            // 童话
            case FAIRYTALE:
                return new GLFairyTaleFilter(context);

            // 佛洛伊特
            case FREUD:
                return new GLFreudFilter(context);

            // 健康
            case HEALTHY:
                return new GLHealthyFilter(context);

            // 酵母
            case HEFE:
                return new GLHefeFilter(context);

            // 哈德森
            case HUDSON:
                return new GLHudsonFilter(context);

            // 凯文
            case KEVIN:
                return new GLKevinFilter(context);

            // 拿铁
            case LATTE:
                return new GLLatteFilter(context);

            // LOMO
            case LOMO:
                return new GLLomoFilter(context);

            // 怀旧之情
            case NOSTALGIA:
                return new GLNostalgiaFilter(context);

            // 浪漫
            case ROMANCE:
                return new GLRomanceFilter(context);

            // 樱花
            case SAKURA:
                return new GLSakuraFilter(context);
            // 日落
            case SUNSET:
                return new GLSunsetFilter(context);

            // 白猫
            case WHITECAT:
                return new GLWhiteCatFilter(context);
            //亮度
            case BRIGHTNESS:
                return new GLBrightnessFilter(context);
            // 饱和度
            case SATURATION:
                return new GLSaturationFilter(context);
            case NONE:      // 没有滤镜
            case SOURCE:    // 原图
                return new GLImageFilter(context);

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
            case SOULSTUFF:
                return new GLImageSoulStuffFilter(context);
            case SHIFTRGB:
                return new GLImageShiftRGBFilter(context);
            case SCALE:
                return new GLImageEffectScaleFilter(context);
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
        filterTypes.add(GLImageFilterType.ANTIQUE);
        filterTypes.add(GLImageFilterType.AMARO);
        filterTypes.add(GLImageFilterType.BLACKCAT);
        filterTypes.add(GLImageFilterType.BROOKLYN);
        filterTypes.add(GLImageFilterType.CALM);
        filterTypes.add(GLImageFilterType.COOL);
        filterTypes.add(GLImageFilterType.EARLYBIRD);
        filterTypes.add(GLImageFilterType.EMERALD);
        filterTypes.add(GLImageFilterType.EVERGREEN);
        filterTypes.add(GLImageFilterType.FAIRYTALE);
        filterTypes.add(GLImageFilterType.FREUD);
        filterTypes.add(GLImageFilterType.HEALTHY);
        filterTypes.add(GLImageFilterType.HEFE);
        filterTypes.add(GLImageFilterType.HUDSON);
        filterTypes.add(GLImageFilterType.KEVIN);
        filterTypes.add(GLImageFilterType.LATTE);
        filterTypes.add(GLImageFilterType.LOMO);
        filterTypes.add(GLImageFilterType.NOSTALGIA);
        filterTypes.add(GLImageFilterType.ROMANCE);
        filterTypes.add(GLImageFilterType.SAKURA);
        filterTypes.add(GLImageFilterType.SUNSET);
        filterTypes.add(GLImageFilterType.WHITECAT);
        filterTypes.add(GLImageFilterType.BRIGHTNESS);
        filterTypes.add(GLImageFilterType.SATURATION);
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
        filterTypes.add(GLImageFilterType.SOULSTUFF);
        filterTypes.add(GLImageFilterType.EFFECTILLUSION);
        filterTypes.add(GLImageFilterType.SHIFTRGB);
        filterTypes.add(GLImageFilterType.SCALE);
        return filterTypes;
    }

    /**
     * 获取Color滤镜名称
     *
     * @return
     */
    public static List<String> getFilterNames() {
        List<String> filterNames = new ArrayList<>();
        filterNames.add("原图");
        filterNames.add("黑白");
        filterNames.add("古董");
        filterNames.add("AMARO");
        filterNames.add("黑猫");
        filterNames.add("布鲁克林");
        filterNames.add("冷静");
        filterNames.add("冷色调");
        filterNames.add("晨鸟");
        filterNames.add("翡翠");
        filterNames.add("常绿");
        filterNames.add("童话");
        filterNames.add("佛洛伊特");
        filterNames.add("健康");
        filterNames.add("酵母");
        filterNames.add("哈德森");
        filterNames.add("凯文");
        filterNames.add("拿铁");
        filterNames.add("LOMO");
        filterNames.add("怀旧之情");
        filterNames.add("浪漫");
        filterNames.add("樱花");
        filterNames.add("日落");
        filterNames.add("白猫");
        filterNames.add("亮度");
        filterNames.add("饱和度");

        return filterNames;
    }
}
