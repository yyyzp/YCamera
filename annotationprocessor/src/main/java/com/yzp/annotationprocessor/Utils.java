package com.yzp.annotationprocessor;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Created by Answer on 2018/10/24.
 */

public class Utils {

    private static Set<String> supportTypes = new HashSet<>();


    /**
     * 当getIntent的时候，每种类型写的方式都不一样，所以把每种方式都添加到了Set容器中
     */
    static void init() {
        supportTypes.add(int.class.getSimpleName());
        supportTypes.add(int[].class.getSimpleName());
        supportTypes.add(short.class.getSimpleName());
        supportTypes.add(short[].class.getSimpleName());
        supportTypes.add(String.class.getSimpleName());
        supportTypes.add(String[].class.getSimpleName());
        supportTypes.add(boolean.class.getSimpleName());
        supportTypes.add(boolean[].class.getSimpleName());
        supportTypes.add(long.class.getSimpleName());
        supportTypes.add(long[].class.getSimpleName());
        supportTypes.add(char.class.getSimpleName());
        supportTypes.add(char[].class.getSimpleName());
        supportTypes.add(byte.class.getSimpleName());
        supportTypes.add(byte[].class.getSimpleName());
        supportTypes.add("Bundle");
    }

    /**
     * 获取元素所在的包名
     * @param element
     * @return
     */

    public static String getPackageName(Element element) {
        String clazzSimpleName = element.getSimpleName().toString();
        String clazzName = element.toString();
        return clazzName.substring(0, clazzName.length() - clazzSimpleName.length() - 1);
    }


    /**
     * 判断是否是String类型或者数组或者bundle，因为这三种类型getIntent()不需要默认值
     * @param typeName
     * @return
     */
    public static boolean isElementNoDefaultValue(String typeName) {
        return (String.class.getName().equals(typeName) || typeName.contains("[]") || typeName.contains("Bundle"));
    }

    /**
     * 获得注解要传递参数的类型
     * @param typeName 注解获取到的参数类型
     * @return
     */
    public static String getIntentTypeName(String typeName) {
        for (String name : supportTypes) {
            if (name.equals(getSimpleName(typeName))) {
                return name.replaceFirst(String.valueOf(name.charAt(0)), String.valueOf(name.charAt(0)).toUpperCase())
                        .replace("[]", "Array");
            }
        }
        return "";
    }

    /**
     * 获取类的的名字的字符串
     * @param typeName 可以是包名字符串，也可以是类名字符串
     * @return
     */
    static String getSimpleName(String typeName) {
        if (typeName.contains(".")) {
            return typeName.substring(typeName.lastIndexOf(".") + 1, typeName.length());
        }else {
            return typeName;
        }
    }


    /**
     * 自动生成代码
     */
    public static void writeToFile(String className, String packageName, MethodSpec methodSpec, ProcessingEnvironment processingEnv, ArrayList<FieldSpec> listField) {
        TypeSpec genedClass;
        if(listField == null) {
            genedClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodSpec).build();
        }else{
            genedClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodSpec)
                    .addFields(listField).build();
        }
        JavaFile javaFile = JavaFile.builder(packageName, genedClass)
                .build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
