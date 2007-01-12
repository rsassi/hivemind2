package org.apache.hivemind.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ServiceImplementationDefinition;
import org.apache.hivemind.definition.construction.ImplementationConstructor;

public class ServiceImplementationDefinitionImpl extends ExtensionDefinitionImpl implements
        ServiceImplementationDefinition
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
     * @see org.apache.hivemind.definition.ServiceImplementationDefinition#getServiceModel()
     */
    public String getServiceModel()
    {
        return _serviceModel;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceImplementationDefinition#setServiceModel(java.lang.String)
     */
    public void setServiceModel(String interfaceClassName)
    {
        _serviceModel = interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceImplementationDefinition#isDefault()
     */
    public boolean isDefault()
    {
        return _isDefault;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceImplementationDefinition#setDefault(boolean)
     */
    public void setDefault(boolean isDefault)
    {
        _isDefault = isDefault;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceImplementationDefinition#getServiceConstructor()
     */
    public ImplementationConstructor getServiceConstructor()
    {
        return _serviceConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceImplementationDefinition#setServiceConstructor(org.apache.hivemind.definition.construction.ImplementationConstructor)
     */
    public void setServiceConstructor(ImplementationConstructor serviceConstructor)
    {
        _serviceConstructor = serviceConstructor;
    }

}
