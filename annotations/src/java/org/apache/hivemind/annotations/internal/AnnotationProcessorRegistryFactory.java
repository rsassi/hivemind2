package org.apache.hivemind.annotations.internal;

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
    
    public static AnnotationProcessorRegistry createDefaultRegistry()
    {
        AnnotationProcessorRegistry result = new AnnotationProcessorRegistry();
        result.registerProcessor(Service.class, new ServiceProcessor());
        result.registerProcessor(Configuration.class, new ConfigurationProcessor());
        result.registerProcessor(Contribution.class, new ContributionProcessor());
        result.registerProcessor(Submodule.class, new SubmoduleProcessor());
        return result;
    }
}

