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
import org.apache.hivemind.definition.ExtensionDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.util.Defense;

public class ExtensionDefinitionImpl implements ExtensionDefinition
{
    private Location _location;
    private ModuleDefinition _module;

    public ExtensionDefinitionImpl(ModuleDefinition module)
    {
        Defense.notNull(module, "module");
        _module = module;
    }

    public ExtensionDefinitionImpl(ModuleDefinition module, Location location)
    {
        this(module);
        _location = location;
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionDefinition#getModuleId()
     */
    public String getModuleId()
    {
        return _module.getId();
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionDefinition#getModule()
     */
    public ModuleDefinition getModule()
    {
        return _module;
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionDefinition#getLocation()
     */
    public Location getLocation()
    {
        return _location;
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionDefinition#setLocation(org.apache.hivemind.Location)
     */
    public void setLocation(Location location)
    {
        _location = location;
    }

}
