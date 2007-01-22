package org.apache.hivemind.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;

public class ServiceImplementationDefinitionImpl extends ExtensionDefinitionImpl implements
        ImplementationDefinition
{
    private String _serviceModel;

    private ImplementationConstructor _serviceConstructor;

    private boolean _isDefault;

    public ServiceImplementationDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ServiceImplementationDefinitionImpl(ModuleDefinition module, Location location,
            ImplementationConstructor serviceConstructor, String serviceModel, boolean isDefault)
    {
        super(module, location);
        _serviceConstructor = serviceConstructor;
        _serviceModel = serviceModel;
        _isDefault = isDefault;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#getServiceModel()
     */
    public String getServiceModel()
    {
        return _serviceModel;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#setServiceModel(java.lang.String)
     */
    public void setServiceModel(String interfaceClassName)
    {
        _serviceModel = interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#isDefault()
     */
    public boolean isDefault()
    {
        return _isDefault;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#setDefault(boolean)
     */
    public void setDefault(boolean isDefault)
    {
        _isDefault = isDefault;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#getServiceConstructor()
     */
    public ImplementationConstructor getServiceConstructor()
    {
        return _serviceConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.ImplementationDefinition#setServiceConstructor(org.apache.hivemind.definition.ImplementationConstructor)
     */
    public void setServiceConstructor(ImplementationConstructor serviceConstructor)
    {
        _serviceConstructor = serviceConstructor;
    }

}
