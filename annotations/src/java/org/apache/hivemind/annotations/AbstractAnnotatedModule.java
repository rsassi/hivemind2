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

    /**
     * This setter is used to inject the registry reference.
     * @param typedRegistry  the registry
     */
    public void setRegistry(TypedRegistry typedRegistry)
    {
        _typedRegistry = typedRegistry;
    }
    
    /**
     * @return  a reference to the {@link Autowiring} service.
     */
    protected Autowiring getAutowiring()
    {
        return _typedRegistry.getAutowiring();
    }
    
    /**
     * Autowires any object by use of the {@link Autowiring} service.
     * @param target  the object to wire
     * @return the wired object 
     */
    protected <T> T autowireProperties(T target) 
    {
        return (T) getAutowiring().autowireProperties(target);
    }
    
    /**
     * Returns a service from the registry.
     * 
     * @see org.apache.hivemind.Registry#getService(String, Class)
     */
    protected <T> T service(String serviceId, Class<T> serviceInterface)
    {
        return _typedRegistry.getService(serviceId, serviceInterface);
    }

    /**
     * Finds a service that implements the provided interface. 
     * Exactly one such service may exist or an exception is thrown.
     * 
     * @see org.apache.hivemind.Registry#getService(Class)
     */
    protected <T> T service(Class<T> serviceInterface)
    {
        return _typedRegistry.getService(serviceInterface);
    }
    
    /**
     * Returns the specified configuration from the registry.
     * 
     * @see org.apache.hivemind.Registry#getConfiguration(String)
     */
    protected <T> T configuration(String configurationId, Class<T> configurationType)
    {
        return _typedRegistry.getConfiguration(configurationId, configurationType);
    }
    
    /**
     * Finds a configuration by its type. 
     * Exactly one such configuration may exist or an exception is thrown.
     * 
     * @see org.apache.hivemind.Registry#getConfiguration(String)
     */
    protected <T> T configuration(Class<T> configurationType)
    {
        return _typedRegistry.getConfiguration(configurationType);
    }

}
