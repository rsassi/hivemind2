package org.apache.hivemind.annotations.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.annotations.definition.processors.AnnotationProcessor;

public class AnnotationProcessorRegistry
{
    private Map<Class<? extends Annotation>, List<Class<? extends AnnotationProcessor>>> processorLists = new HashMap<Class<? extends Annotation>, List<Class<? extends AnnotationProcessor>>>();

    public List<Class<? extends AnnotationProcessor>> getProcessorClasses(Class<? extends Annotation> annotationClass)
    {
        return processorLists.get(annotationClass);
    }

    public void registerProcessor(Class<? extends Annotation> annotationClass, Class<? extends AnnotationProcessor> processorClass)
    {
        List<Class<? extends AnnotationProcessor>> processorList = getProcessorClasses(annotationClass);
        if (processorList == null) {
            processorList = new ArrayList<Class<? extends AnnotationProcessor>>();
            processorLists.put(annotationClass, processorList);
        }
        processorList.add(processorClass);
    }
}

