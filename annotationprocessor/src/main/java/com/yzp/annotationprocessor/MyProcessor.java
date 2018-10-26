package com.yzp.annotationprocessor;

import com.google.auto.service.AutoService;
import com.yzp.annotation.IntentField;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;


/**
 * Created by Answer on 2018/10/24.
 */

/**
 * 这是一个自定义注解处理器
 */
@AutoService(javax.annotation.processing.Processor.class)
public class MyProcessor extends AbstractProcessor{

    private Map<Element, List<VariableElement>> items = new HashMap<>();

    private List<Generator> generators = new LinkedList<>();

    /**
     * 做一些初始化工作
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Utils.init();
        generators.add(new ActivityEnterGenerator());
        generators.add(new ActivityInitFieldGenerator());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //获取所有注册IntentField注解的元素
        for (Element elem : roundEnvironment.getElementsAnnotatedWith(IntentField.class)) {
            //主要获取ElementType 是不是null，即class，interface，enum或者注解类型
            if (elem.getEnclosingElement() == null) {
                //直接结束处理器
                return true;
            }

            //如果items的key不存在，则添加一个key
            if (items.get(elem.getEnclosingElement()) == null) {
                items.put(elem.getEnclosingElement(), new LinkedList<VariableElement>());
            }

            //我们这里的IntentField是应用在一般成员变量上的注解
            if (elem.getKind() == ElementKind.FIELD) {
                items.get(elem.getEnclosingElement()).add((VariableElement)elem);
            }
        }

        List<VariableElement> variableElements;
        for (Map.Entry<Element, List<VariableElement>> entry : items.entrySet()) {
            variableElements = entry.getValue();
            if (variableElements == null || variableElements.isEmpty()) {
                return true;
            }
            //去通过自动javapoet生成代码
            for (Generator generator : generators) {
                generator.genetate(entry.getKey(), variableElements, processingEnv);
//                generator.genetate(entry.getKey(), variableElements, processingEnv);
            }
        }
        return false;
    }

    /**
     * 指定当前注解器使用的Java版本
     */
    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 指出注解处理器 处理哪种注解
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>(2);
        annotations.add(IntentField.class.getCanonicalName());
        return annotations;
    }
}
