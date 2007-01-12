package org.apache.hivemind.annotations.internal;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.annotations.Registry;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.util.Defense;
import org.apache.hivemind.util.PropertyUtils;

public class ModuleInstanceProviderImpl implements ModuleInstanceProvider
{
    private static final String REGISTRY_PROPERTY_NAME = "registry";

    private Class _moduleClass;

    private Object _instance;

    private String _moduleId;
    
    public ModuleInstanceProviderImpl(Class moduleClass, String moduleId)
    {
        _moduleClass = moduleClass;
        _moduleId = moduleId;
    }

    public Object getModuleInstance()
    {
        Defense.fieldNotNull(_instance, "instance");
        return _instance;
    }
    
    public void createModuleInstance(RegistryInfrastructure _registry)
    {
        try
        {
            _instance = _moduleClass.newInstance();
            injectRegistry(_instance, _registry);
        }
        catch (Exception ex)
        {
            // TODO: more expressive error message
            throw new ApplicationRuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * Checks if the module contains a property REGISTRY_PROPERTY_NAME and injects
     * the registry interface provided to this class during construction.
     * 
     * @param moduleInstance
     */
    private void injectRegistry(Object moduleInstance, RegistryInfrastructure _registry)
    {
        if (PropertyUtils.isWritable(moduleInstance, REGISTRY_PROPERTY_NAME) 
                && PropertyUtils.getPropertyType(moduleInstance, REGISTRY_PROPERTY_NAME).equals(Registry.class)) {
            
            Module callingModule = _registry.getModule(_moduleId);
            Registry annotatedRegistry = new RegistryImpl(callingModule, _registry);
            PropertyUtils.write(moduleInstance, REGISTRY_PROPERTY_NAME, annotatedRegistry);
        }
    }

    /**
     * Called after initialization of the registry infrastructure. 
     * This is a good moment to create the module instance. If any service defined in the module
     * is initialized during startup (by the EagerLoad service) it will find the registry
     * reference in place.
     * 
     * @see org.apache.hivemind.events.RegistryInitializationListener#registryInitialized(org.apache.hivemind.internal.RegistryInfrastructure)
     */
    public void registryInitialized(RegistryInfrastructure registry)
    {
        createModuleInstance(registry);
    }

}
