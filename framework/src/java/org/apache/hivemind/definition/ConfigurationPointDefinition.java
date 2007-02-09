package org.apache.hivemind.definition;

import java.util.Collection;

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Defines a configuration extension point.
 * The definition includes the configuration type, contributions and parsers.
 * 
 * @author Achim Huegen
 */
public interface ConfigurationPointDefinition extends ExtensionPointDefinition
{
    /**
     * @return  the fully qualified class name of the configuration type
     */
    public String getConfigurationTypeName();

    /**
     * @return  the expected number of contributions to this configuration point
     */
    public Occurances getExpectedContributions();

    /**
     * @return  the contributions to this configuration as instances of {@link ContributionDefinition}
     */
    public Collection getContributions();

    /**
     * Adds a contribution.
     * @param contribution  the contribution
     * @throws ApplicationRuntimeException  if this point is not visible from contribution
     */
    public void addContribution(ContributionDefinition contribution);

    /**
     * Adds a parser definition.
     * @param parser  the parser
     * @throws ApplicationRuntimeException  if this point is not visible from parser
     *   or if a parser for the specified format is already defined
     */
    public void addParser(ConfigurationParserDefinition parser);
    
    /**
     * Returns the parsers which is responsible for processing the specified <code>inputFormat</code>.
     * @param inputFormat  the input format
     * @return the parser  
     */
    public ConfigurationParserDefinition getParser(String inputFormat);

    /**
     * @return  returns all parsers as instances of {@link ConfigurationParserDefinition}
     */
    public Collection getParsers();
}