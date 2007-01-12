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

package org.apache.hivemind.definition;

import org.apache.hivemind.impl.MessageFormatter;

/**
 * Used to format messages used in errors and log output for classes within the definition package.
 * 
 * @author Achim Huegen
 */
public class DefinitionMessages
{
    private static final MessageFormatter _formatter = new MessageFormatter(DefinitionMessages.class,
            "DefinitionStrings");
    
    public static String duplicateModuleId(String moduleId)
    {
        return _formatter.format(
                "duplicate-module-id",
                moduleId);
    }

    public static String wrongNumberOfContributions(ModuleDefinition definingModule, ConfigurationPointDefinition point, int actualCount,
            Occurances expectation)
    {
        return _formatter.format(
                "wrong-number-of-contributions",
                point.getFullyQualifiedId(),
                contributionCount(actualCount),
                occurances(expectation));
    }

    public static String duplicateServicePointId(String pointId, ModuleDefinition module)
    {
        return _formatter.format("duplicate-service-point", pointId, module.getId());
    }

    public static String duplicateConfigurationPointId(String pointId, ModuleDefinition module)
    {
        return _formatter.format("duplicate-configuration-point", pointId, module.getId());
    }

    public static String contributionCount(int count)
    {
        return _formatter.format("contribution-count", new Integer(count));
    }

    public static String occurances(Occurances occurances)
    {
        return _formatter.getMessage("occurances." + occurances.getName());
    }

    public static String dependencyOnUnknownModule(String toModuleId)
    {
        return _formatter.format("dependency-on-unknown-module", toModuleId);
    }
    
    public static String unknownConfigurationPoint(String moduleId,
            String configurationId)
    {
        return _formatter.format("unknown-configuration-extension-point", moduleId, configurationId);
    }

    public static String unknownServicePoint(String moduleId, String pointId)
    {
        return _formatter.format(
                "unknown-service-extension-point",
                moduleId,
                pointId);
    }

    public static String configurationPointNotVisible(ConfigurationPointDefinition point,
            ModuleDefinition contributingModule)
    {
        return _formatter.format(
                "configuration-point-not-visible",
                point.getFullyQualifiedId(),
                contributingModule.getId());
    }

    public static String duplicateParserInputFormat(String inputFormat, ConfigurationPointDefinition cp)
    {
        return _formatter.format("duplicate-parser-inputformat", inputFormat, cp.getFullyQualifiedId());
    }


    public static String servicePointNotVisible(ServicePointDefinition point, ModuleDefinition contributingModule)
    {
        return _formatter.format(
                "service-point-not-visible",
                point.getFullyQualifiedId(),
                contributingModule.getId());
    }
    
}