package org.apache.hivemind.annotations.definition.processors;

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Processes a method in an module class which is annotated with a specific
 * annotation type. The processor gets full access to the module definition
 * and can add extension points and extensions.
 * 
 * The same instance is used for all annotations of one kind which are defined
 * in the same module.
 * 
 * @author Achim Huegen
 */
public interface AnnotationProcessor
{
    /**
     * @param context
     * @return true if annotation has been processed. Used for chaining.
     * @throws ApplicationRuntimeException  if annotation can not be processed due to inconsistent data. 
     */
    public boolean processAnnotation(AnnotationProcessingContext context) throws ApplicationRuntimeException;
}
