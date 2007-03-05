package org.apache.hivemind.annotations.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext;
import org.apache.hivemind.definition.RegistryDefinition;

public class AnnotationProcessingContextImpl implements AnnotationProcessingContext
{
    private AnnotatedElement _annotatedElement;
    private Location _location;
    private AnnotatedModuleDefinitionImpl _module;
    private ModuleInstanceProvider _moduleInstanceProvider;
    private Annotation _targetAnnotation;
    private RegistryDefinition _registryDefinition;
    private ClassResolver _classResolver;
    private AnnotationProcessorRegistry _annotationProcessorRegistry;
    
    public AnnotationProcessingContextImpl(
            RegistryDefinition registryDefinition, AnnotatedModuleDefinitionImpl module,
            ClassResolver classResolver,
            Annotation targetAnnotation, AnnotatedElement annotatedElement, 
            Location location, ModuleInstanceProvider moduleInstanceProvider,
            AnnotationProcessorRegistry annotationProcessorRegistry)
    {
        super();
        _registryDefinition = registryDefinition;
        _module = module;
        _classResolver = classResolver;
        _targetAnnotation = targetAnnotation;
        _annotatedElement = annotatedElement;
        _location = location;
        _moduleInstanceProvider = moduleInstanceProvider;
        _annotationProcessorRegistry = annotationProcessorRegistry;
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

    public ClassResolver getClassResolver()
    {
        return _classResolver;
    }

    public AnnotatedModuleProcessor createSubmoduleProcessor()
    {
        return new AnnotatedModuleProcessor(_registryDefinition,
                _classResolver, _annotationProcessorRegistry);
    }

}
