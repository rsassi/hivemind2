package org.apache.hivemind.annotations;

import org.apache.hivemind.annotations.internal.AnnotationProcessorRegistry;

public interface AnnotationsExtensionProvider
{
    public void registerAnnotationProcessors(AnnotationProcessorRegistry registry);
}
