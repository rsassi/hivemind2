package org.apache.hivemind.annotations.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.apache.hivemind.Location;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext;

public class AnnotationProcessingContextImpl implements AnnotationProcessingContext
{
    private AnnotatedElement _annotatedElement;
    private Location _location;
    private AnnotatedModuleDefinitionImpl _module;
    private ModuleInstanceProvider _moduleInstanceProvider;
    private Annotation _targetAnnotation;

    public AnnotationProcessingContextImpl(AnnotatedModuleDefinitionImpl module, Annotation targetAnnotation, AnnotatedElement annotatedElement, Location location, ModuleInstanceProvider moduleInstanceProvider)
    {
        super();
        _module = module;
        _targetAnnotation = targetAnnotation;
        _annotatedElement = annotatedElement;
        _location = location;
        _moduleInstanceProvider = moduleInstanceProvider;
    }

    public Annotation[] getAllAnnotations()
    {
        return _annotatedElement.getAnnotations();
    }

    public AnnotatedElement getAnnotatedElement()
    {
        return _annotatedElement;
    }

    public Location getLocation()
    {
        return _location;
    }

    public AnnotatedModuleDefinitionImpl getModule()
    {
        return _module;
    }

    public ModuleInstanceProvider getModuleInstanceProvider()
    {
        return _moduleInstanceProvider;
    }

    public Annotation getTargetAnnotation()
    {
        return _targetAnnotation;
    }

}
