package org.apache.hivemind.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserConstructor;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ModuleDefinition;

public class ConfigurationParserDefinitionImpl extends ExtensionDefinitionImpl implements ConfigurationParserDefinition
{
    private String _inputFormat;
    private ConfigurationParserConstructor _parserConstructor;

    public ConfigurationParserDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }
    
    public ConfigurationParserDefinitionImpl(ModuleDefinition module, Location location,
            String inputFormat, ConfigurationParserConstructor parserConstructor)
    {
        super(module, location);
        _inputFormat = inputFormat;
        _parserConstructor = parserConstructor;
    }
    
    /**
     * @see org.apache.hivemind.definition.ConfigurationParserDefinition#getInputFormat()
     */
    public String getInputFormat()
    {
        return _inputFormat;
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationParserDefinition#getParserConstructor()
     */
    public ConfigurationParserConstructor getParserConstructor()
    {
        return _parserConstructor;
    }
}
