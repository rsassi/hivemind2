package org.apache.hivemind.annotations.internal;

import org.apache.hivemind.annotations.Registry;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.service.Autowiring;
import org.apache.hivemind.util.IdUtils;

public class RegistryImpl implements Registry
{
    private Module _callingModule;

    private RegistryInfrastructure _delegate;

    public RegistryImpl(Module callingModule, RegistryInfrastructure delegate)
    {
        _callingModule = callingModule;
        _delegate = delegate;
    }

    public <T> T getConfiguration(String configurationId, Class<T> configurationType)
    {
        String fullyQualifiedConfigurationId = IdUtils.qualify(
                _callingModule.getModuleId(),
                configurationId);
        Object configuration = _delegate.getConfiguration(
                fullyQualifiedConfigurationId,
                _callingModule);
        return (T) configuration;
    }

    public <T> T getConfiguration(Class<T> configurationType)
    {
        // Object configuration = _delegate.getConfiguration(configurationId, _callingModule);
        // throw new UnsupportedOperationException();
        return null;
    }

    public Object getConfiguration(String configurationId)
    {
        String fullyQualifiedConfigurationId = IdUtils.qualify(
                _callingModule.getModuleId(),
                configurationId);
        return _delegate.getConfiguration(fullyQualifiedConfigurationId, _callingModule);
    }

    public <T> T getService(String serviceId, Class<T> serviceInterface)
    {
        String fullyQualifiedServiceId = IdUtils.qualify(
                _callingModule.getModuleId(),
                serviceId);
        Object service = _delegate.getService(fullyQualifiedServiceId, serviceInterface, _callingModule);
        return (T) service;
    }

    public <T> T getService(Class<T> serviceInterface)
    {
        Object service = _delegate.getService(serviceInterface, _callingModule);
        return (T) service;
    }

    public Autowiring getAutowiring()
    {
        return getService(Autowiring.class);
    }

}
