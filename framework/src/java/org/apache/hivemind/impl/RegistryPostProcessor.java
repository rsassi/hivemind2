package org.apache.hivemind.impl;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.definition.RegistryDefinition;

public interface RegistryPostProcessor
{
    /**
     * Is called after all registry providers have run through their process method.
     * That means, all extension points of all modules are now known to the registry
     * definition.
     * 
     * @param registryDefinition
     * @param errorHandler
     */
    public void postprocess(RegistryDefinition registryDefinition, ErrorHandler errorHandler);

}
