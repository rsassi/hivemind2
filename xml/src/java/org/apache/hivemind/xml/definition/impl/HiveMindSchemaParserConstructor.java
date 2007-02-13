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

package org.apache.hivemind.xml.definition.impl;

import org.apache.hivemind.definition.ConfigurationParser;
import org.apache.hivemind.definition.ConfigurationParserConstructor;
import org.apache.hivemind.definition.ConstructionContext;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.util.Defense;

/**
 * Constructor for {@link HiveMindSchemaParser}s.
 */
public class HiveMindSchemaParserConstructor implements ConfigurationParserConstructor
{
    private Schema _schema;
    private String _schemaId;

    public HiveMindSchemaParserConstructor(String schemaId)
    {
        _schemaId = schemaId;
    }

    public HiveMindSchemaParserConstructor(Schema schema)
    {
        _schema = schema;
    }

    public ConfigurationParser constructParser(ConstructionContext context)
    {
        Defense.fieldNotNull(_schema, "schema");
        
        return new HiveMindSchemaParser(_schema);
    }

    public Schema getSchema()
    {
        return _schema;
    }

    public void setSchema(Schema schema)
    {
        _schema = schema;
    }

    public String getSchemaId()
    {
        return _schemaId;
    }

    public void setSchemaId(String schemaId)
    {
        _schemaId = schemaId;
    }

}
