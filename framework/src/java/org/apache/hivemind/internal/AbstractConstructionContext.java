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

package org.apache.hivemind.internal;

import org.apache.hivemind.definition.ConstructionContext;

/**
 * Default Implementation of {@link ConstructionContext}.
 * 
 * @author Achim Huegen
 */
public abstract class AbstractConstructionContext implements ConstructionContext
{
    private Module _definingModule;

    public AbstractConstructionContext(Module definingModule)
    {
        _definingModule = definingModule;
    }

    public Object getConfiguration(String configurationId)
    {
        return _definingModule.getConfiguration(configurationId);
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getDefiningModule()
     */
    public Module getDefiningModule()
    {
        return _definingModule;
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getService(java.lang.String, java.lang.Class)
     */
    public Object getService(String serviceId, Class serviceInterface)
    {
        return _definingModule.getService(serviceId, serviceInterface);
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getService(java.lang.Class)
     */
    public Object getService(Class serviceInterface)
    {
        return _definingModule.getService(serviceInterface);
    }
    
    /**
     * @see org.apache.hivemind.definition.ConstructionContext#containsService(java.lang.Class)
     */
    public boolean containsService(Class serviceInterface)
    {
        return _definingModule.containsService(serviceInterface);
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getRegistry()
     */
    public RegistryInfrastructure getRegistry()
    {
        return getDefiningModule().getRegistry();
    }

}
