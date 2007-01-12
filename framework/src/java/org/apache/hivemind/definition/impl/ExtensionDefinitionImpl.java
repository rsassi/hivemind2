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
