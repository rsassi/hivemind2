// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.hivemind.xml.definition.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.Element;
import org.apache.hivemind.definition.ConfigurationParser;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.construction.Contribution;
import org.apache.hivemind.definition.construction.ContributionContext;

/**
 * Implements the {@link org.apache.hivemind.definition.construction.Contribution} interface.
 * Contributes data that is defined as xml in a HiveMind xml module.
 * The data is passed in as instances of {@link Element}.
 * 
 * @author Howard Lewis Ship
 */
public final class XmlContributionImpl implements Contribution
{
    private String _contributingModuleId;

    private List _elements;

    public XmlContributionImpl(String moduleId, List elements)
    {
        _contributingModuleId = moduleId;
        _elements = elements;
    }

    public String getContributingModuleId()
    {
        return _contributingModuleId;
    }

    public void addElements(List elements)
    {
        if (_elements == null)
            _elements = new ArrayList(elements);
        else
            _elements.addAll(elements);
    }

    public List getElements()
    {
        if (_elements == null)
            return Collections.EMPTY_LIST;

        return _elements;
    }

    /**
     * @see org.apache.hivemind.definition.construction.Contribution#contribute(org.apache.hivemind.definition.construction.ContributionContext)
     */
    public void contribute(ContributionContext context)
    {
        // Retrieve parser for HiveMind schema
        ConfigurationPointDefinition cpd = context.getConfigurationPoint()
                .getConfigurationPointDefinition();
        ConfigurationParserDefinition parserDef = cpd.getParser(HiveMindSchemaParser.INPUT_FORMAT_NAME);
        if (parserDef == null)
        {
            // This is a valid situation. The unparsed elements are contributed
            context.mergeContribution(getElements());
        } else {
            // Construct and execute the parser
            ConfigurationParser parser = parserDef.getParserConstructor().constructParser(context);
            Object contribution = parser.parse(context, getElements());
            context.mergeContribution(contribution);
        }
    }

}