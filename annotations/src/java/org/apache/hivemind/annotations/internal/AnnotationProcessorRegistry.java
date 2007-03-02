package org.apache.hivemind.annotations.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.annotations.definition.processors.AnnotationProcessor;

public class AnnotationProcessorRegistry
{
    private Map<Class<? extends Annotation>, List<AnnotationProcessor>> processorLists = new HashMap<Class<? extends Annotation>, List<AnnotationProcessor>>();

    public List<AnnotationProcessor> getProcessors(Class<? extends Annotation> annotationClass)
    {
        return processorLists.get(annotationClass);
    }

    public void registerProcessor(Class<? extends Annotation> annotationClass, AnnotationProcessor processor)
    {
        List<AnnotationProcessor> processorList = getProcessors(annotationClass);
        if (processorList == null) {
            processorList = new ArrayList<AnnotationProcessor>();
            processorLists.put(annotationClass, processorList);
        }
        processorList.add(processor);
    }
}

