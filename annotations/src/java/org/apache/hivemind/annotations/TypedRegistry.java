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
 * Specialized interface for the {@link org.apache.hivemind.Registry} access from annotated modules.
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
    
    /**
     * @see org.apache.hivemind.Registry#shutdown()
     */
    public void shutdown();
}
