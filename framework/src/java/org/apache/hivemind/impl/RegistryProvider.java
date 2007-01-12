package org.apache.hivemind.impl;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.definition.RegistryDefinition;

/**
 * A registry provider contributes module definitions to a {@link RegistryDefinition}.
 * It must be implemented to participate in the registry autoloading mechanism
 * triggered by {@link RegistryBuilder#autoDetectModules()} and described in 
 * {@link RegistryProviderAutoDetector}.
 * The implementations must have a no arguments constructor.
 * 
 * @author Achim Huegen
 */
public interface RegistryProvider
{
    /**
     * Called during the registry definition phase, before the registry is constructed.
     * New module definitions can be added to the registry and existing can be altered.
     * 
     * @param registryDefinition  the registry definition 
     * @param errorHandler  an error handler to call when errors occur.
     */
    public void process(RegistryDefinition registryDefinition, ErrorHandler errorHandler);
    
}
