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
     * @see Module#getService(Class)
     */
    public Object getService(Class serviceInterface);
    
    /**
     * @see Module#containsService(Class)
     */
    public boolean containsService(Class serviceInterface);

    /**
     * @see Module#getConfiguration(String)
     */
    public Object getConfiguration(String configurationId);
    
    /**
     * Returns a reference to the {@link RegistryInfrastructure}. 
     * This allows access to services and configurations without the visibility checks
     * performed by the other getter methods. 
     * 
     * @return  the registry 
     */
    public RegistryInfrastructure getRegistry();
}
