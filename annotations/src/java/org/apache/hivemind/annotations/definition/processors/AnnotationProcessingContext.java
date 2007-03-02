package org.apache.hivemind.annotations.definition.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.apache.hivemind.Location;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.internal.ModuleInstanceProvider;

public interface AnnotationProcessingContext
{
    public AnnotatedElement getAnnotatedElement();
    public Annotation getTargetAnnotation();
    public Annotation[] getAllAnnotations();
    public Location getLocation();
    public AnnotatedModuleDefinitionImpl getModule(); 
    public ModuleInstanceProvider getModuleInstanceProvider();
}
