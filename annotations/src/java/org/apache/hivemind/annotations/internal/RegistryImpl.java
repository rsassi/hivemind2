// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
