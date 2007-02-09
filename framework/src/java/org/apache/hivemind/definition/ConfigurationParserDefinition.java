package org.apache.hivemind.definition;

/**
 * Defines a parser for the processing of configuration data that is provided
 * in a textual format.  The parsed data is handled as {@link Contribution} to a 
 * configuration point. Each parser is associated with a certain input format,
 * which is specified by {@link #getInputFormat()}.
 * The parsing is done by an instance of {@link ConfigurationParser}
 * which is created by a {@link ConfigurationParserConstructor}.
 * 
 * @author Huegen
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
