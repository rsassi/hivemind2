package org.apache.hivemind.annotations;

import org.apache.hivemind.service.Autowiring;

/**
 * Specialized interface for the registry access from annotated modules.
 * Implements typed access to services and configurations by use of Generics. 
 * 
 * @author Achim Huegen
 */
public interface Registry 
{
    public <T> T getService(String serviceId, Class<T> serviceInterface);
    public <T> T getService(Class<T> serviceInterface);
    public <T> T getConfiguration(String configurationId, Class<T> configurationType);
    public <T> T getConfiguration(Class<T> configurationType);
    public Object getConfiguration(String configurationId);
    public Autowiring getAutowiring();
}
