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
import org.apache.hivemind.definition.ExtensionPointDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.util.Defense;

/**
 * Default implementation of {@link ExtensionPointDefinition}.
 * 
 * @author Achim Huegen
 */
public class ExtensionPointDefinitionImpl implements ExtensionPointDefinition
{
    private ModuleDefinition _module;

    private String _id;

    private Location _location;

    private Visibility _visibility;
    
    public ExtensionPointDefinitionImpl(ModuleDefinition module)
    {
        Defense.notNull(module, "module");
        _module = module;
    }

    public ExtensionPointDefinitionImpl(ModuleDefinition module, String id, Location location, Visibility visibility)
    {
        this(module);
        _id = id;
        _location = location;
        _visibility = visibility;
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionPointDefinition#getModuleId()
     */
    public String getModuleId()
    {
        return _module.getId();
    }

    /**
     * @return the module that defined this extension point.
     */
    protected ModuleDefinition getModule()
    {
        return _module;
    }
    
    /**
     * @see org.apache.hivemind.definition.ExtensionPointDefinition#getQualifiedId()
     */
    public String getQualifiedId()
    {
        return getModuleId() + "." + _id;
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionPointDefinition#getId()
     */
    public String getId()
    {
        return _id;
    }

    /**
     * Sets the id of the extension point.
     * @param id  the id (unqualified without module id)
     */
    public void setId(String id)
    {
        _id = id;
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionPointDefinition#getLocation()
     */
    public Location getLocation()
    {
        return _location;
    }

    /**
     * Sets the location of the extension point.
     */
    public void setLocation(Location location)
    {
        _location = location;
    }

    /**
     * @see org.apache.hivemind.definition.ExtensionPointDefinition#getVisibility()
     */
    public Visibility getVisibility()
    {
        return _visibility;
    }

    /**
     * Sets the visibility of the extension point.
     */
    public void setVisibility(Visibility visibility)
    {
        _visibility = visibility;
    }

}
