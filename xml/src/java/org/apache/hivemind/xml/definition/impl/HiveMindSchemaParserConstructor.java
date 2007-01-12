package org.apache.hivemind.xml.definition.impl;

import org.apache.hivemind.definition.ConfigurationParser;
import org.apache.hivemind.definition.construction.ConfigurationParserConstructor;
import org.apache.hivemind.definition.construction.ConstructionContext;
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
