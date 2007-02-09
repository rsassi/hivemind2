package org.apache.hivemind.definition;

/**
 * Factory for the creation of a {@link ConfigurationParser}.
 * 
 * @author Huegen
 */
public interface ConfigurationParserConstructor
{
    /**
     * Constructs a parser instance.
     * @param context  provides access to elements of the registry which are needed for the construction.
     * @return  the parser 
     */
    public ConfigurationParser constructParser(ConstructionContext context);
}
