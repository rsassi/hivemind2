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
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ContributionContext;

/**
 * Contributes to a configuration point by passing the configuration container
 * to a method defined in an annotated module by use of the {@link Contribution} annotation. 
 * 
 * @author Achim Huegen
 */
public class TemplateMethodContributionConstructor implements
        Contribution
{
    private Method _templateMethod;

    private ModuleInstanceProvider _moduleInstanceProvider;

    private Location _location;

    public TemplateMethodContributionConstructor(Location location, Method factoryMethod,
            ModuleInstanceProvider moduleInstanceProvider)
    {
        _location = location;
        _templateMethod = factoryMethod;
        _moduleInstanceProvider = moduleInstanceProvider;
    }

    public Location getLocation()
    {
        return _location;
    }

    public void contribute(ContributionContext context)
    {
        try
        {
            if (_templateMethod.getParameterTypes().length == 0) {
                Object result = _templateMethod.invoke(_moduleInstanceProvider.getModuleInstance());
                // a null contribution means: nothing to contribute. This happens for example
                // in configuration point definitions 
                if (result != null) {
                    context.mergeContribution(result);
                }
            } else if (_templateMethod.getParameterTypes().length == 1) {
                Object[] params = new Object[] {context.getConfigurationData()}; 
                _templateMethod.invoke(_moduleInstanceProvider.getModuleInstance(), params);
            } else {
                // TODO: Throw Exception
            }
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
        
    }

}
