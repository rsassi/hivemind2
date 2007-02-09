package org.apache.hivemind.annotations;

import org.apache.hivemind.service.Autowiring;

/**
 * Specialized interface for the registry access from annotated modules.
 * Implements typed access to services and configurations by use of Generics. 
 * 
 * @author Achim Huegen
 */
public interface TypedRegistry 
{
    /**
     * Returns a service from the registry.
     * 
     * @see org.apache.hivemind.Registry#getService(String, Class)
     */
    public <T> T getService(String serviceId, Class<T> serviceInterface);

    /**
     * Finds a service that implements the provided interface. 
     * Exactly one such service may exist or an exception is thrown.
     * 
     * @see org.apache.hivemind.Registry#getService(Class)
     */
    public <T> T getService(Class<T> serviceInterface);
    
    /**
     * Returns the specified configuration from the registry.
     * 
     * @see org.apache.hivemind.Registry#getConfiguration(String)
     */
    public <T> T getConfiguration(String configurationId, Class<T> configurationType);
    
    /**
     * Finds a configuration by its type. 
     * Exactly one such configuration may exist or an exception is thrown.
     * 
     * @see org.apache.hivemind.Registry#getConfiguration(String)
     */
    public <T> T getConfiguration(Class<T> configurationType);
    

    /**
     * Returns a reference to the {@link Autowiring} service.
     */
    public Autowiring getAutowiring();
}
