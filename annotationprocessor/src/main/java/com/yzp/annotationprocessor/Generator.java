package com.yzp.annotationprocessor;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

/**
 * Created by Answer on 2018/10/24.
 */

public interface Generator {

    void genetate(Element typeElement
            , List<VariableElement> variableElements
            , ProcessingEnvironment processingEnv);

}
