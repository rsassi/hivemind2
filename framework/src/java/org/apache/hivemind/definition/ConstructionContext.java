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
