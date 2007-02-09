package org.apache.hivemind.annotations.internal;

import org.apache.hivemind.annotations.TypedRegistry;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.service.Autowiring;
import org.apache.hivemind.util.IdUtils;

/**
 * Implementation of {@link TypedRegistry}.
 * Wraps an instance of {@link RegistryInfrastructure} to provide registry access.
 * 
 * @author Huegen
 */
public class RegistryImpl implements TypedRegistry
{
    private Module _callingModule;

    private RegistryInfrastructure _delegate;

    /**
     * @param callingModule  the module that gets access registry access by this instance.
     *                       Used for visibility checks when services and configurations are retrieved.
     * @param delegate
     */
    public RegistryImpl(Module callingModule, RegistryInfrastructure delegate)
    {
        _callingModule = callingModule;
        _delegate = delegate;
    }

    /**
     * @see org.apache.hivemind.annotations.TypedRegistry#getConfiguration(java.lang.String, java.lang.Class)
     */
    public <T> T getConfiguration(String configurationId, Class<T> configurationType)
    {
        String qualifiedConfigurationId = IdUtils.qualify(
                _callingModule.getModuleId(),
                configurationId);
        Object configuration = _delegate.getConfiguration(
                qualifiedConfigurationId,
                _callingModule);
        return (T) configuration;
    }

    /**
     * @see org.apache.hivemind.annotations.TypedRegistry#getConfiguration(java.lang.Class)
     */
    public <T> T getConfiguration(Class<T> configurationType)
    {
        Object configuration = _delegate.getConfiguration(configurationType, _callingModule);
        return (T) configuration;
    }

    /**
     * @see org.apache.hivemind.annotations.TypedRegistry#getService(java.lang.String, java.lang.Class)
     */
    public <T> T getService(String serviceId, Class<T> serviceInterface)
    {
        String qualifiedServiceId = IdUtils.qualify(
                _callingModule.getModuleId(),
                serviceId);
        Object service = _delegate.getService(qualifiedServiceId, serviceInterface, _callingModule);
        return (T) service;
    }

    /**
     * @see org.apache.hivemind.annotations.TypedRegistry#getService(java.lang.Class)
     */
    public <T> T getService(Class<T> serviceInterface)
    {
        Object service = _delegate.getService(serviceInterface, _callingModule);
        return (T) service;
    }

    /**
     * @see org.apache.hivemind.annotations.TypedRegistry#getAutowiring()
     */
    public Autowiring getAutowiring()
    {
        return getService(Autowiring.class);
    }

}
