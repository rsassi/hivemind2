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

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.schema.Schema;

/**
 * Specialization of {@link ServicePointDefinitionImpl} for service points defined in xml.
 * 
 * @author Achim Huegen
 */
public class XmlServicePointDefinitionImpl extends ServicePointDefinitionImpl
{
    private Schema _parametersSchema;
    private Occurances _parametersCount = Occurances.REQUIRED;
    private String _parametersSchemaId;

    public XmlServicePointDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }
    
    public XmlServicePointDefinitionImpl(ModuleDefinition module, String id, Location location, Visibility visibility, String interfaceClassName)
    {
        super(module, id, location, visibility, interfaceClassName);
    }

    /**
     * Returns the {@link Schema} used to process any parameters passed to the service. Service
     * implementation factories and service interceptor factories allow parameters.
     */
    public Schema getParametersSchema()
    {
        return _parametersSchema;
    }

    public void setParametersSchema(Schema schema)
    {
        _parametersSchema = schema;
    }

    /**
     * Returns the number of parameter object expected; generally this is the default of exactly one (
     * {@link Occurances#REQUIRED}).
     */
    public Occurances getParametersCount()
    {
        return _parametersCount;
    }

    public void setParametersCount(Occurances parametersCount)
    {
        _parametersCount = parametersCount;
    }

    public void setParametersSchemaId(String parametersSchemaId)
    {
        _parametersSchemaId = parametersSchemaId;
    }

    public String getParametersSchemaId()
    {
        return _parametersSchemaId;
    }
    
}
