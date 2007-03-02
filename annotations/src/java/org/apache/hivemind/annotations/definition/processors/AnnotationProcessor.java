package org.apache.hivemind.annotations.definition.processors;

import org.apache.hivemind.ApplicationRuntimeException;

public interface AnnotationProcessor
{
    /**
     * @param context
     * @return true if annotation has been processed. Used for chaining.
     * @throws ApplicationRuntimeException  if annotation can not be processed due to inconsistent data. 
     */
    public boolean processAnnotation(AnnotationProcessingContext context) throws ApplicationRuntimeException;
}
