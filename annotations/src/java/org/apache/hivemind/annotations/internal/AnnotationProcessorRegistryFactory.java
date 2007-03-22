package org.apache.hivemind.annotations.internal;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.annotations.definition.Configuration;
import org.apache.hivemind.annotations.definition.Contribution;
import org.apache.hivemind.annotations.definition.Service;
import org.apache.hivemind.annotations.definition.Submodule;
import org.apache.hivemind.annotations.definition.processors.ConfigurationProcessor;
import org.apache.hivemind.annotations.definition.processors.ContributionProcessor;
import org.apache.hivemind.annotations.definition.processors.ServiceProcessor;
import org.apache.hivemind.annotations.definition.processors.SubmoduleProcessor;

public class AnnotationProcessorRegistryFactory
{
    
    public AnnotationProcessorRegistryFactory()
    {
    }
    
    public AnnotationProcessorRegistry createDefaultRegistry(ClassResolver classResolver)
    {
        AnnotationProcessorRegistry result = new AnnotationProcessorRegistry();
        loadExtensions(classResolver, result);
        registerDefaultProcessors(result);
        return result;
    }

    private void loadExtensions(ClassResolver classResolver, AnnotationProcessorRegistry processorRegistry)
    {
        AnnotationsExtensionLoader extensionLoader = new AnnotationsExtensionLoader();
        extensionLoader.loadExtensions(classResolver, processorRegistry);
    }

    private void registerDefaultProcessors(AnnotationProcessorRegistry processorRegistry)
    {
        processorRegistry.registerProcessor(Service.class, ServiceProcessor.class);
        processorRegistry.registerProcessor(Configuration.class, ConfigurationProcessor.class);
        processorRegistry.registerProcessor(Contribution.class, ContributionProcessor.class);
        processorRegistry.registerProcessor(Submodule.class, SubmoduleProcessor.class);
    }
    
}

