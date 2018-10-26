package com.yzp.annotationprocessor;

/**
 * Created by Answer on 2018/10/24.
 */

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.yzp.annotation.IntentField;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * 要生成一个.Java文件，在这个Java文件里生成一个获取上个界面传递过来数据的方法
 * 主要思路：1.使用Javapoet生成一个空的的方法
 *         2.为方法添加需要的形参
 *         3.拼接方法内部的代码
 * 主要需要：获取传递过来字段的类型
 */
public class ActivityInitFieldGenerator implements Generator {

    private static final String SUFFIX = "$Init";

    private static final String METHOD_NAME = "initFields";

    @Override
    public void genetate(Element typeElement, List<VariableElement> variableElements, ProcessingEnvironment processingEnv) {

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(METHOD_NAME)
                .addModifiers(Modifier.PROTECTED)
                .returns(Object.class);

        ArrayList<FieldSpec> listField = new ArrayList<>();

        if(variableElements != null && variableElements.size() != 0){
            VariableElement element = variableElements.get(0);
            //当前接收数据的字段的名字
            IntentField currentClassName = element.getAnnotation(IntentField.class);
            String name = currentClassName.value();

            methodBuilder.addParameter(Object.class, "currentActivity");
            methodBuilder.addStatement(name + " activity = (" + name + ")currentActivity");
            methodBuilder.addStatement("android.content.Intent intent = activity.getIntent()");
        }

        for (VariableElement element : variableElements) {

            //获取接收字段的类型
            TypeName currentTypeName = TypeName.get(element.asType());
            String currentTypeNameStr = currentTypeName.toString();
            String intentTypeName = Utils.getIntentTypeName(currentTypeNameStr);

            //字段的名字，即key值
            Name filedName = element.getSimpleName();

            //创建成员变量
            FieldSpec fieldSpec = FieldSpec.builder(TypeName.get(element.asType()),filedName+"")
                    .addModifiers(Modifier.PUBLIC)
                    .build();
            listField.add(fieldSpec);

            //因为String类型的获取 和 其他基本类型的获取在是否需要默认值问题上不一样，所以需要判断是哪种
            if (Utils.isElementNoDefaultValue(currentTypeNameStr)) {
                methodBuilder.addStatement("this."+filedName+"= intent.get" + intentTypeName + "Extra(\"" + filedName + "\")");
            } else {
                String defaultValue = "default" + element.getSimpleName();
                if (intentTypeName == null) {
                    //当字段类型为null时，需要打印错误信息
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "the type:" + element.asType().toString() + " is not support");
                } else {
                    if("".equals(intentTypeName)){
                        methodBuilder.addStatement("this."+ filedName +"= (" + TypeName.get(element.asType())+ ")intent.getSerializableExtra(\"" + filedName + "\")");
                    }else{
                        methodBuilder.addParameter(TypeName.get(element.asType()), defaultValue);
                        methodBuilder.addStatement("this."+ filedName +"= intent.get"
                                + intentTypeName + "Extra(\"" + filedName + "\", " + defaultValue + ")");
                    }
                }
            }
        }
        methodBuilder.addStatement("return this");

        Utils.writeToFile(typeElement.getSimpleName().toString() + SUFFIX, Utils.getPackageName(typeElement), methodBuilder.build(), processingEnv,listField);
    }


}
