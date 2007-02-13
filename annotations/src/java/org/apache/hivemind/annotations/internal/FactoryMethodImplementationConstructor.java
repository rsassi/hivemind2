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

import java.lang.reflect.Method;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.annotations.definition.Service;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;

/**
 * Constructs a service implementation by calling a factory method defined in 
 * an annotated module by use of the {@link Service} annotation. 
 * 
 * @author Achim Huegen
 */
public class FactoryMethodImplementationConstructor extends AbstractServiceImplementationConstructor implements
        ImplementationConstructor
{
    private Method _factoryMethod;

    private ModuleInstanceProvider _moduleInstanceProvider;

    public FactoryMethodImplementationConstructor(Location location, Method factoryMethod,
            ModuleInstanceProvider moduleInstanceProvider)
    {
        super(location);
        _factoryMethod = factoryMethod;
        _moduleInstanceProvider = moduleInstanceProvider;
    }

    public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
    {
        try
        {
            Object result = _factoryMethod.invoke(_moduleInstanceProvider.getModuleInstance(), (Object[]) null);
            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
    }

}
