package org.apache.hivemind.definition;

import java.util.Collection;

import org.apache.hivemind.ApplicationRuntimeException;

public interface ConfigurationPointDefinition extends ExtensionPointDefinition
{
    public String getConfigurationTypeName();

    public Occurances getExpectedContributions();

    public Collection getContributions();

    /**
     * @param contribution
     * @throws ApplicationRuntimeException  if this point is not visible from contribution
     */
    public void addContribution(ContributionDefinition contribution);

    /**
     * @param parser
     * @throws ApplicationRuntimeException  if this point is not visible from parser
     */
    public void addParser(ConfigurationParserDefinition parser);
    
    public ConfigurationParserDefinition getParser(String inputFormat);

    public Collection getParsers();
}