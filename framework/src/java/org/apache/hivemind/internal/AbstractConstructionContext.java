package org.apache.hivemind.internal;

import org.apache.hivemind.definition.ConstructionContext;

/**
 * Default Implementation of {@link ConstructionContext}.
 * 
 * @author Achim Huegen
 */
public abstract class AbstractConstructionContext implements ConstructionContext
{
    private Module _definingModule;

    public AbstractConstructionContext(Module definingModule)
    {
        _definingModule = definingModule;
    }

    public Object getConfiguration(String configurationId)
    {
        return _definingModule.getConfiguration(configurationId);
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getDefiningModule()
     */
    public Module getDefiningModule()
    {
        return _definingModule;
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getService(java.lang.String, java.lang.Class)
     */
    public Object getService(String serviceId, Class serviceInterface)
    {
        return _definingModule.getService(serviceId, serviceInterface);
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getService(java.lang.Class)
     */
    public Object getService(Class serviceInterface)
    {
        return _definingModule.getService(serviceInterface);
    }
    
    /**
     * @see org.apache.hivemind.definition.ConstructionContext#containsService(java.lang.Class)
     */
    public boolean containsService(Class serviceInterface)
    {
        return _definingModule.containsService(serviceInterface);
    }

    /**
     * @see org.apache.hivemind.definition.ConstructionContext#getRegistry()
     */
    public RegistryInfrastructure getRegistry()
    {
        return getDefiningModule().getRegistry();
    }

}
