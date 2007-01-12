package org.apache.hivemind.definition;

import org.apache.hivemind.definition.construction.ConfigurationParserConstructor;

/**
 * Defines a parser for the processing of configuration data that is provided
 * in a textual format.
 */
public interface ConfigurationParserDefinition extends ExtensionDefinition
{
    /**
     * @return  the format of the data the parser can process 
     */
    public String getInputFormat();
    
    /**
     * @return  a factory for the construction of a parser instance
     */
    public ConfigurationParserConstructor getParserConstructor();
}
