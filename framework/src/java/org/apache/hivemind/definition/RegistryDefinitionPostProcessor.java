package org.apache.hivemind.definition;

import org.apache.hivemind.ErrorHandler;

public interface RegistryDefinitionPostProcessor
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
