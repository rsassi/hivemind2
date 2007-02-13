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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.impl.SchemaAssignment;
import org.apache.hivemind.schema.Schema;

public class XmlModuleDefinitionImpl extends ModuleDefinitionImpl
{
    /**
     * Map of {@link Schema} keyed on schema id.
     */
    private Map _schemas = new HashMap();
    
    /**
     * Collection of {@link SchemaAssignment}s
     */
    private Collection _schemaAssignments = new ArrayList();

    public XmlModuleDefinitionImpl(String id, Location location, ClassResolver resolver, String packageName)
    {
        super(id, location, resolver, packageName);
    }

    public void addSchema(String qualifiedSchemaId, Schema schema)
    {
        _schemas.put(qualifiedSchemaId, schema);
    }

    public Schema getSchema(String qualifiedSchemaId)
    {
        return (Schema) _schemas.get(qualifiedSchemaId);
    }
    
    public Collection getSchemas()
    {
        return _schemas.values();
    }

    public void addSchemaAssignment(SchemaAssignment assignment)
    {
        _schemaAssignments.add(assignment);
    }
    
    public Collection getSchemaAssignments()
    {
        return _schemaAssignments;
    }

}
