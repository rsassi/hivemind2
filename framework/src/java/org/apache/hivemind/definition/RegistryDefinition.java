package org.apache.hivemind.definition;

import java.util.Collection;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.impl.RegistryPostProcessor;

public interface RegistryDefinition
{

    /**
     * Adds a module definition.
     * @param module  the module 
     * @throws ApplicationRuntimeException  if another module with the same id already exists.
     */
    public void addModule(ModuleDefinition module) throws ApplicationRuntimeException;

    public void addPostProcessor(RegistryPostProcessor postProcessor);

    public List getPostProcessors();

    /**
     * @see RegistryInitializationListener
     */
    public void addRegistryInitializationListener(RegistryInitializationListener listener);

    public List getRegistryInitializationListeners();

    public Collection getModules();

    public ModuleDefinition getModule(String id);

    public ServicePointDefinition getServicePoint(String qualifiedServicePointId);

    public ConfigurationPointDefinition getConfigurationPoint(String qualifiedConfigurationPointId);

}