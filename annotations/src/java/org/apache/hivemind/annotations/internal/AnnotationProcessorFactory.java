package org.apache.hivemind.annotations.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.annotations.definition.processors.AnnotationProcessor;

public class AnnotationProcessorFactory
{
    private AnnotationProcessorRegistry _processorRegistry;
    private Map<Class<? extends Annotation>, List<AnnotationProcessor>> processorLists = new HashMap<Class<? extends Annotation>, List<AnnotationProcessor>>();
    
    public AnnotationProcessorFactory(AnnotationProcessorRegistry processorRegistry)
    {
        _processorRegistry = processorRegistry;
    }
    
    public List<AnnotationProcessor> getProcessors(Class<? extends Annotation> annotationClass)
    {
        List<AnnotationProcessor> processorList = processorLists.get(annotationClass);
        if (processorList == null) {
            processorList = createProcessors(annotationClass);
            processorLists.put(annotationClass, processorList);
        }
        return processorList;
    }
    
    private List<AnnotationProcessor> createProcessors(Class<? extends Annotation> annotationClass)
    {
        List<AnnotationProcessor> result = new ArrayList<AnnotationProcessor>();
        
        List<Class<? extends AnnotationProcessor>> processorClasses = _processorRegistry.getProcessorClasses(annotationClass);
        for (Class<? extends AnnotationProcessor> processorClass : processorClasses)
        {
            AnnotationProcessor processor;
            try
            {
                processor = processorClass.newInstance();
                result.add(processor);
            }
            catch (Exception e)
            {
                // TODO: Improve error handling
               throw new ApplicationRuntimeException(e);
            }
        }
        
        return result;
    }

}

