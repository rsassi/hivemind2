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

package org.apache.hivemind.schema.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;

/**
 * Interprets a string as an extension point id, and provides the elements for that extension point.
 * Depending on the target property type the elements will be provided as a List or as a Map.
 * 
 * @author Howard Lewis Ship
 */
public class ConfigurationTranslator implements Translator
{
    public Object translate(Module contributingModule, Class propertyType, String inputValue,
            Location location)
    {
        if (HiveMind.isBlank(inputValue))
            return null;

        // For backward compatibility allow configuratin of type map be assigned to
        // properties of type List. Drawback: Lazy construction of the configuration
        // gets lost. The configuration is constructed right here during the conversion
        Object configuration = contributingModule.getConfiguration(inputValue);
        if (configuration instanceof Map && propertyType.equals(List.class)) {
            configuration = new ArrayList(((Map) configuration).values());
        }
        
        return configuration;
    }
}