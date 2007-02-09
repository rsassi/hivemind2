package org.apache.hivemind.annotations;

import org.apache.hivemind.service.Autowiring;

/**
 * Ancestor for annotated module classes. Provides convenience methods
 * for the access to {@link TypedRegistry} and {@link Autowiring}.
 * 
 * @author Achim Huegen
 */
public class AbstractAnnotatedModule
{
    private TypedRegistry _typedRegistry;

    /**
     * @return  the registry the module is loaded in
     */
    public TypedRegistry getRegistry()
    {
        return _typedRegistry;
    }

    public void setRegistry(TypedRegistry typedRegistry)
    {
        _typedRegistry = typedRegistry;
    }
    
    /**
     * @return  a reference to the {@link Autowiring} service.
     */
    public Autowiring getAutowiring()
    {
        return _typedRegistry.getAutowiring();
    }
    
    /**
     * Autowires any object by use of the {@link Autowiring} service.
     * @param target  the object to wire
     * @return the wired object 
     */
    public <T> T autowireProperties(T target) 
    {
        return (T) getAutowiring().autowireProperties(target);
    }
    
    /**
     * Returns a service from the registry.
     * 
     * @see org.apache.hivemind.Registry#getService(String, Class)
     */
    public <T> T getService(String serviceId, Class<T> serviceInterface)
    {
        return _typedRegistry.getService(serviceId, serviceInterface);
    }

    /**
     * Finds a service that implements the provided interface. 
     * Exactly one such service may exist or an exception is thrown.
     * 
     * @see org.apache.hivemind.Registry#getService(Class)
     */
    public <T> T getService(Class<T> serviceInterface)
    {
        return _typedRegistry.getService(serviceInterface);
    }
    
    /**
     * Returns the specified configuration from the registry.
     * 
     * @see org.apache.hivemind.Registry#getConfiguration(String)
     */
    public <T> T getConfiguration(String configurationId, Class<T> configurationType)
    {
        return _typedRegistry.getConfiguration(configurationId, configurationType);
    }
    
    /**
     * Finds a configuration by its type. 
     * Exactly one such configuration may exist or an exception is thrown.
     * 
     * @see org.apache.hivemind.Registry#getConfiguration(String)
     */
    public <T> T getConfiguration(Class<T> configurationType)
    {
        return _typedRegistry.getConfiguration(configurationType);
    }

}
