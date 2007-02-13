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

package org.apache.hivemind.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ExtensionDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.Visibility;

public class ServicePointDefinitionImpl extends ExtensionPointDefinitionImpl implements ServicePointDefinition
{
    private String _interfaceClassName;
    
    private Collection _implementations = new ArrayList();

    private Collection _interceptors = new ArrayList(); 

    public ServicePointDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ServicePointDefinitionImpl(ModuleDefinition module, String id, Location location, Visibility visibility, String interfaceClassName)
    {
        super(module, id, location, visibility);
        _interfaceClassName = interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getInterfaceClassName()
     */
    public String getInterfaceClassName()
    {
        return _interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#setInterfaceClassName(java.lang.String)
     */
    public void setInterfaceClassName(String interfaceClassName)
    {
        _interfaceClassName = interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getImplementations()
     */
    public Collection getImplementations()
    {
        return Collections.unmodifiableCollection(_implementations);
    }
    
    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getDefaultImplementation()
     */
    public ImplementationDefinition getDefaultImplementation()
    {
        ImplementationDefinition defaulImplementation = null;
        for (Iterator iter = _implementations.iterator(); iter.hasNext();)
        {
            ImplementationDefinition impl = (ImplementationDefinition) iter.next();
            if (defaulImplementation == null)
                defaulImplementation = impl;
            if (impl.isDefault()) {
                defaulImplementation = impl;
                break;
            }
        }
        
        return defaulImplementation;
    }

    /**
     * Checks if Extension can see this service point. 
     * @throws ApplicationRuntimeException  if not visible
     */
    private void checkVisibility(ExtensionDefinition extension)
    {
        if (Visibility.PRIVATE.equals(getVisibility())
                && !extension.getModuleId().equals(getModuleId()))
        {
            throw new ApplicationRuntimeException(DefinitionMessages.servicePointNotVisible(
                    this,
                    extension.getModule()));
        }
    }
    
    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#addImplementation(org.apache.hivemind.definition.ImplementationDefinition)
     */
    public void addImplementation(ImplementationDefinition implementation)
    {
        checkVisibility(implementation);
        _implementations.add(implementation);
    }
    
    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getInterceptors()
     */
    public Collection getInterceptors()
    {
        return Collections.unmodifiableCollection(_interceptors);
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#addInterceptor(org.apache.hivemind.definition.InterceptorDefinition)
     */
    public void addInterceptor(InterceptorDefinition interceptor)
    {
        checkVisibility(interceptor);
        _interceptors.add(interceptor);
    }
}
