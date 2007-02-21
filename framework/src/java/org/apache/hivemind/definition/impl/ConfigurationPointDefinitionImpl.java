// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.hivemind.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

/**
 * Default implementation of {@link ConfigurationPointDefinition}.
 * 
 * @author Achim Huegen
 */
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
     * Set the class name of the configuration.
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
     * Sets the expected number of contributions to the configuration.
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
        return Collections.unmodifiableCollection(_contributions);
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
        return Collections.unmodifiableCollection(_parsers.values());
    }

    /**
     * @see org.apache.hivemind.definition.ConfigurationPointDefinition#getParser(java.lang.String)
     */
    public ConfigurationParserDefinition getParser(String inputFormat)
    {
        return (ConfigurationParserDefinition) _parsers.get(inputFormat);
    }


}
