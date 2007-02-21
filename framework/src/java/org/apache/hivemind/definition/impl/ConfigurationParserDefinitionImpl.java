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

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserConstructor;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ModuleDefinition;

/**
 * Default implementation of {@link ConfigurationParserDefinition}.
 * 
 * @author Achim Huegen
 */
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
