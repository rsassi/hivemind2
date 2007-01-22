package org.apache.hivemind.definition;

import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Interface that provides access to information and services needed
 * during the construction of an {@link ExtensionDefinition extension}
 * or {@link ExtensionPointDefinition extension point}.
 * 
 * @author Achim Huegen
 */
public interface ConstructionContext
{
    /**
     * @return  the module that defined the constructor that is constructed now.
     */
    public Module getDefiningModule();
    
    /**
     * @see Module#getService(String, Class)
     */
    public Object getService(String serviceId, Class serviceInterface);
    
    /**
     * @see Module#getService(String)
     */
    public Object getService(Class serviceInterface);
    
    /**
     * @see Module#containsService(Class)
     */
    public boolean containsService(Class serviceInterface);

    /**
     * @see Module#getConfiguration(String, Class)
     */
    public Object getConfiguration(String configurationId);
    
    public RegistryInfrastructure getRegistry();
}
