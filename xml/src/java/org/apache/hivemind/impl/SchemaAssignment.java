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
