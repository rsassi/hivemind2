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

package org.apache.hivemind.impl;

import org.apache.hivemind.Location;

/**
 * Helper class used during the processing of xml descriptors.
 */
public class SchemaAssignment extends BaseLocatable
{
    private String _configurationId;
    private String _schemaId;
    
    public SchemaAssignment(String configurationId, String schemaId, Location location)
    {
        super(location);
        _configurationId = configurationId;
        _schemaId = schemaId;
    }

    /**
     * Returns the id of the configuration the schema should be assigned to
     */
    public String getConfigurationId()
    {
        return _configurationId;
    }

    public void setConfigurationId(String string)
    {
        _configurationId = string;
    }
    
    /**
     * @return  the id of the schema that should be assigned to a configuration point.
     */
    public String getSchemaId()
    {
        return _schemaId;
    }

    public void setSchemaId(String schemaId)
    {
        _schemaId = schemaId;
    }

}
