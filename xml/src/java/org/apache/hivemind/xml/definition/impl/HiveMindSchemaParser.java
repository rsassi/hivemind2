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

import java.io.InputStream;
import java.util.List;

import org.apache.hivemind.Element;
import org.apache.hivemind.definition.ConfigurationParser;
import org.apache.hivemind.definition.ContributionContext;
import org.apache.hivemind.impl.SchemaProcessorImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.util.Defense;
import org.apache.hivemind.util.InstanceCreationUtils;

/**
 * Implementation of {@link ConfigurationParser} that parses data whose format
 * is described by a HiveMind {@link Schema} definition in a HiveMind xml module.
 */
public final class HiveMindSchemaParser implements ConfigurationParser
{
    public final static String INPUT_FORMAT_NAME = "hivemind-schema";
    
    private Schema _schema;

    public HiveMindSchemaParser(Schema schema)
    {
        _schema = schema;
    }

    /**
     * @param  a list of {@link Element}s
     * @see org.apache.hivemind.definition.ConfigurationParser#parse(org.apache.hivemind.definition.ContributionContext, java.lang.Object)
     */
    public Object parse(ContributionContext context, Object data)
    {
        SchemaProcessorImpl processor = new SchemaProcessorImpl(context.getConfigurationPoint()
                .getErrorLog(), _schema);
        Object contributionObject = constructContributionObject(_schema, context.getDefiningModule());
        processor.process(contributionObject, (List) data, context.getDefiningModule());
        return contributionObject;
    }

    public Object constructContributionObject(Schema contributionsSchema, Module definingModule)
    {
        Defense.notNull(contributionsSchema.getRootElementClassName(), "schema.rootElementClassName");

        return InstanceCreationUtils.createInstance(
                definingModule,
                contributionsSchema.getRootElementClassName(),
                contributionsSchema.getLocation());
    }

    
    public Object parse(ContributionContext context, InputStream data)
    {
        throw new UnsupportedOperationException("Parsing of an InputStream is not currently supported");
    }
    
    public Schema getSchema()
    {
        return _schema;
    }
}