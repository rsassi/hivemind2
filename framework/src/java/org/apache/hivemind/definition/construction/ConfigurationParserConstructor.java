package org.apache.hivemind.definition.construction;

import org.apache.hivemind.definition.ConfigurationParser;

public interface ConfigurationParserConstructor
{
    public ConfigurationParser constructParser(ConstructionContext context);
}
