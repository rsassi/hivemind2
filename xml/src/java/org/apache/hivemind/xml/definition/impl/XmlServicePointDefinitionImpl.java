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
