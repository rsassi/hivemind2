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
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.ModuleDefinition;

/**
 * Default implementation of {@link ImplementationDefinition}.
 * 
 * @author Achim Huegen
 */
public class ImplementationDefinitionImpl extends ExtensionDefinitionImpl implements
        ImplementationDefinition
{
    private String _serviceModel;

    private ImplementationConstructor _implementationConstructor;

    private boolean _isDefault;

    public ImplementationDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ImplementationDefinitionImpl(ModuleDefinition module, Location location,
            ImplementationConstructor implementationConstructor, String serviceModel, boolean isDefault)
    {
        super(module, location);
        _implementationConstructor = implementationConstructor;
        _serviceModel = serviceModel;
        _isDefault = isDefault;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#getServiceModel()
     */
    public String getServiceModel()
    {
        return _serviceModel;
    }

    /**
     * Sets the service model of the implementation.
     */
    public void setServiceModel(String serviceModel)
    {
        _serviceModel = serviceModel;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#isDefault()
     */
    public boolean isDefault()
    {
        return _isDefault;
    }

    /**
     * Sets the default attribute of the implementation.
     * @param isDefault   true, if this implementation is the default one
     */
    public void setDefault(boolean isDefault)
    {
        _isDefault = isDefault;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#getServiceConstructor()
     */
    public ImplementationConstructor getServiceConstructor()
    {
        return _implementationConstructor;
    }

    /**
     * Sets the constructor implementation that is used for the creation of
     * the implementation instance.
     */
    public void setImplementationConstructor(ImplementationConstructor serviceConstructor)
    {
        _implementationConstructor = serviceConstructor;
    }

}
