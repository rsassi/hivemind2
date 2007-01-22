// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.hivemind.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.util.InstanceCreationUtils;

/**
 * Constructs a service by instantiating a class.
 * 
 * @author Howard Lewis Ship
 */
public final class CreateClassServiceConstructor extends AbstractServiceImplementationConstructor
{
    private String _instanceClassName;

    public CreateClassServiceConstructor(Location location, String instanceClassName)
    {
        super(location);
        _instanceClassName = instanceClassName;
    }
    
    public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
    {
        return InstanceCreationUtils.createInstance(
                context.getDefiningModule(),
                _instanceClassName,
                getLocation());
    }

    public String getInstanceClassName()
    {
        return _instanceClassName;
    }

    public void setInstanceClassName(String string)
    {
        _instanceClassName = string;
    }


}