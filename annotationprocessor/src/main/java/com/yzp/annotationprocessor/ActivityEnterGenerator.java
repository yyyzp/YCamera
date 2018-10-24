package com.yzp.annotationprocessor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.yzp.annotation.IntentField;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Created by Answer on 2018/10/24.
 */
/**
 * 这是一个要自动生成跳转功能的.java文件类
 * 主要思路：1.使用javapoet生成一个空方法
 *         2.为方法加上实参
 *         3.方法的里面的代码拼接
 * 主要需要：获取字段的类型和名字，获取将要跳转的类的名字
 */
public class ActivityEnterGenerator implements Generator {
    private static final String SUFFIX = "$Enter";

    private static final String METHOD_NAME = "intentTo";

    @Override
    public void genetate(Element typeElement, List<VariableElement> variableElements, ProcessingEnvironment processingEnv) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);
        //设置生成的METHOD_NAME方法第一个参数
        methodBuilder.addParameter(Object.class, "context");
        methodBuilder.addStatement("android.content.Intent intent = new android.content.Intent()");

        //获取将要跳转的类的名字
        String name = "";

        //VariableElement 主要代表一般字段元素，是Element的一种
        for (VariableElement element : variableElements) {
            //Element 只是一种语言元素，本身并不包含信息，所以我们这里获取TypeMirror
            TypeMirror typeMirror = element.asType();

            //获取注解在身上的字段的类型
            TypeName type = TypeName.get(typeMirror);

            //获取注解在身上字段的名字
            String fileName = element.getSimpleName().toString();

            //设置生成的METHOD_NAME方法第二个参数
            methodBuilder.addParameter(type, fileName);
            methodBuilder.addStatement("intent.putExtra(\"" + fileName + "\"," +fileName + ")");

            //获取注解上的元素
            IntentField toClassName = element.getAnnotation(IntentField.class);
            String name1 = toClassName.value();
            if(name != null && "".equals(name)){
                name = name1;
            }
            //理论上每个界面上的注解value一样，都是要跳转到的那个类名字，否则提示错误
            else if(name1 != null && !name1.equals(name)){
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "同一个界面不能跳转到多个活动，即value必须一致");
            }
        }
        methodBuilder.addStatement("intent.setClass((android.content.Context)context, " + name +".class)");
        methodBuilder.addStatement("((android.content.Context)context).startActivity(intent)");

        /**
         * 自动生成.java文件
         * 第一个参数：要生成的类的名字
         * 第二个参数：生成类所在的包的名字
         * 第三个参数：javapoet 中提供的与自动生成代码的相关的类
         * 第四个参数：能够为注解器提供Elements,Types和Filer
         */
        Utils.writeToFile(typeElement.getSimpleName().toString() + SUFFIX, Utils.getPackageName(typeElement), methodBuilder.build(), processingEnv,null);
    }

}
