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

package org.apache.hivemind.impl;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Element;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.schema.SchemaProcessor;


/**
 * Used to format messages used in errors and log output for classes within the impl package.
 * 
 * @author Howard Lewis Ship
 */
class XmlImplMessages
{
    private static final MessageFormatter _formatter = new MessageFormatter(XmlImplMessages.class,
            "XmlImplStrings");


    static String unableToResolveSchema(String schemaId)
    {
        return _formatter.format("unable-to-resolve-schema", schemaId);
    }

    static String schemaNotVisible(String schemaId, String moduleId)
    {
        return _formatter.format("schema-not-visible", schemaId, moduleId);
    }

    static String wrongNumberOfParameters(String factoryServiceId, int actualCount,
            Occurances expectation)
    {
        return _formatter.format(
                "wrong-number-of-parameters",
                factoryServiceId,
                contributionCount(actualCount),
                occurances(expectation));
    
    }
    
    static String occurances(Occurances occurances)
    {
        return _formatter.getMessage("occurances." + occurances.getName());
    }

    static String contributionCount(int count)
    {
        return _formatter.format("contribution-count", new Integer(count));
    }

    static String uniqueAttributeConstraintBroken(String name, String value,
            Location priorLocation)
    {
        return _formatter.format("unique-attribute-constraint-broken", name, value, priorLocation);
    }

    static String unknownAttribute(String name)
    {
        return _formatter.format("unknown-attribute", name);
    }

    static String schemaStackViolation(SchemaProcessor processor)
    {
        return _formatter.format("schema-stack-violation", processor.getElementPath());
    }
  
    static String missingAttribute(String name)
    {
        return _formatter.format("missing-attribute", name);
    }

    static String elementErrors(SchemaProcessor processor, Element element)
    {
        return _formatter.format("element-errors", processor.getElementPath(), element
                .getLocation());
    }

    static String unknownElement(SchemaProcessor processor, Element element)
    {
        return _formatter.format("unknown-element", processor.getElementPath());
    }

    static String translatorInstantiationFailure(Class translatorClass, Throwable cause)
    {
        return _formatter.format(
                "translator-instantiation-failure",
                translatorClass.getName(),
                cause);
    }

    static String unknownTranslatorName(String name, String configurationId)
    {
        return _formatter.format("unknown-translator-name", name, configurationId);
    }

    static String duplicateTranslatorName(String name, Location oldLocation)
    {
        return _formatter.format("duplicate-translator-name", name, HiveMind
                .getLocationString(oldLocation));
    }

    static String incompleteTranslator(TranslatorContribution c)
    {
        return _formatter.format("incomplete-translator", c.getName());
    }

    static String subModuleDoesNotExist(Resource subModuleDescriptor)
    {
        return _formatter.format("sub-module-does-not-exist", subModuleDescriptor);
    }

    static String unableToFindModulesError(ClassResolver resolver, Throwable cause)
    {
        return _formatter.format("unable-to-find-modules", resolver, cause);
    }
    
    static String unableToFindModuleResource(Resource resource)
    {
        return _formatter.format("unable-to-find-module-resource", resource);
    }

    static String noSuchSymbol(String name)
    {
        return _formatter.format("no-such-symbol", name);
    }

    static String symbolSourceContribution()
    {
        return _formatter.getMessage("symbol-source-contribution");
    }
    
    static String unknownConfigurationPointOfSchemaAssignment(String configurationId, SchemaAssignment schemaAssignment)
    {
        return _formatter.format("unknown-configurationpoint-in-schema-assignment", configurationId, schemaAssignment.getLocation());
    }

}