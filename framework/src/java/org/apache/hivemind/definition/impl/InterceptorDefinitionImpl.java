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

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.InterceptorConstructor;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ModuleDefinition;

/**
 * Default implementation of {@link InterceptorDefinition}.
 * Implementations of this interface may additionally implement the {@link org.apache.hivemind.Orderable}
 * interface if a certain interceptor order is required.
 * 
 * @author Achim Huegen
 */
public class InterceptorDefinitionImpl extends ExtensionDefinitionImpl implements InterceptorDefinition
{
    private InterceptorConstructor _interceptorConstructor;
    private String _name;

    public InterceptorDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public InterceptorDefinitionImpl(ModuleDefinition module, String name, Location location,
            InterceptorConstructor interceptorConstructor)
    {
        super(module, location);
        _name = name;
        _interceptorConstructor = interceptorConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.InterceptorDefinition#getInterceptorConstructor()
     */
    public InterceptorConstructor getInterceptorConstructor()
    {
        return _interceptorConstructor;
    }

    /**
     * Sets the constructor implementation responsible for the creation of interceptor instances.
     */
    public void setInterceptorConstructor(InterceptorConstructor interceptorConstructor)
    {
        _interceptorConstructor = interceptorConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.InterceptorDefinition#getName()
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Sets the name of the interceptor.
     */
    public void setName(String name)
    {
        _name = name;
    }
    
}
