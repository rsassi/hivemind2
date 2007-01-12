package org.apache.hivemind.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ExtensionDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.Visibility;

public class ConfigurationPointDefinitionImpl extends ExtensionPointDefinitionImpl implements ConfigurationPointDefinition
{
    private String _containerClassName;

    private Occurances _expectedContributions;

    private Collection _contributions = new ArrayList();
    
    private Map _parsers = new HashMap();

    public ConfigurationPointDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ConfigurationPointDefinitionImpl(ModuleDefinition module, String id, Location location, Visibility visibility,
            String containerClassName, Occurances expectedContributions)
    {
        super(module, id, location, visibility);
        _containerClassName = containerClassName;
        _expectedContributions = expectedContributions;
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#getConfigurationTypeName()
     */
    public String getConfigurationTypeName()
    {
        return _containerClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#setConfigurationTypeName(java.lang.String)
     */
    public void setConfigurationTypeName(String containerClassName)
    {
        _containerClassName = containerClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#getExpectedContributions()
     */
    public Occurances getExpectedContributions()
    {
        return _expectedContributions;
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#setExpectedContributions(org.apache.hivemind.definition.Occurances)
     */
    public void setExpectedContributions(Occurances expectedContributions)
    {
        _expectedContributions = expectedContributions;
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#getContributions()
     */
    public Collection getContributions()
    {
        return _contributions;
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#addContribution(org.apache.hivemind.definition.ContributionDefinition)
     */
    public void addContribution(ContributionDefinition contribution)
    {
        checkVisibility(contribution);
        _contributions.add(contribution);
    }

    /**
     * Checks if Extension can see this configuration point. 
     * @throws ApplicationRuntimeException  if not visible
     */
    private void checkVisibility(ExtensionDefinition extension)
    {
        if (Visibility.PRIVATE.equals(getVisibility())
                && !extension.getModuleId().equals(getModuleId()))
        {
            throw new ApplicationRuntimeException(DefinitionMessages.configurationPointNotVisible(
                    this,
                    extension.getModule()));
        }
    }
    
    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#addParser(org.apache.hivemind.definition.ConfigurationParserDefinition)
     */
    public void addParser(ConfigurationParserDefinition parser)
    {
        checkVisibility(parser);

        if (_parsers.containsKey(parser.getInputFormat())) {
            throw new ApplicationRuntimeException(DefinitionMessages.duplicateParserInputFormat(parser.getInputFormat(), 
                    this));
        }
        _parsers.put(parser.getInputFormat(), parser);
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#getParsers()
     */
    public Collection getParsers()
    {
        return _parsers.values();
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#getParser(java.lang.String)
     */
    public ConfigurationParserDefinition getParser(String inputFormat)
    {
        return (ConfigurationParserDefinition) _parsers.get(inputFormat);
    }


}
