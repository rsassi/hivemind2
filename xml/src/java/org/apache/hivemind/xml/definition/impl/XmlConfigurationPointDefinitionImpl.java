package org.apache.hivemind.xml.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;

/**
 * Specialization of {@link ConfigurationPointDefinitionImpl} for configuration points defined in xml.
 * 
 * @author Achim Huegen
 */
public class XmlConfigurationPointDefinitionImpl extends ConfigurationPointDefinitionImpl
{

    public XmlConfigurationPointDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }
    
    public XmlConfigurationPointDefinitionImpl(ModuleDefinition module, String id, Location location, Visibility visibility, String containerClassName, Occurances expectedContributions)
    {
        super(module, id, location, visibility, containerClassName, expectedContributions);
    }
    
    
}
