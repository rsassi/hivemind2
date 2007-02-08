package org.apache.hivemind.definition;

import java.util.Collection;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.RegistryPostProcessor;

/**
 * Defines all modules and their service and configuration points 
 * which build a registry. The definition is a blueprint from which
 * a registry instance is constructed. 
 * 
 * After the complete definition of all elements of a registry
 * the definition is passed to {@link RegistryBuilder} for
 * the next phase: the registry construction. 
 * From that moment on the definition shouldn't be changed any longer.
 * 
 * @author Achim Huegen
 */
public interface RegistryDefinition
{

    /**
     * Adds a module definition.
     * @param module  the module 
     * @throws ApplicationRuntimeException  if another module with the same id already exists.
     */
    public void addModule(ModuleDefinition module) throws ApplicationRuntimeException;

    /**
     * @return  a collection of all added {@link ModuleDefinition modules}
     */
    public Collection getModules();

    /**
     * Returns a module that is identified by its module id.
     * @param id  the module id
     * @return  the module
     */
    public ModuleDefinition getModule(String id);

    /**
     * Adds a {@link RegistryPostProcessor}. The processor is called after all
     * module definitions have been processed.
     * @param postProcessor the processor
     */
    public void addPostProcessor(RegistryPostProcessor postProcessor);

    /**
     * @return  a collection of all registered {@link RegistryPostProcessor}s
     */
    public List getPostProcessors();

    /**
     * Adds a {@link RegistryInitializationListener} which is called after the 
     * construction of the registry.
     * @param listener  the listener
     */
    public void addRegistryInitializationListener(RegistryInitializationListener listener);

    /**
     * @return  a collection of all registered {@link RegistryInitializationListener}s
     */
    public List getRegistryInitializationListeners();

    /**
     * Returns a service point that is identified by its id.
     * @param qualifiedServicePointId  the fully qualified service point id
     * @return the service point definition
     */
    public ServicePointDefinition getServicePoint(String qualifiedServicePointId);

    /**
     * Returns a configuration point that is identified by its id.
     * @param qualifiedConfigurationPointId  the fully qualified configuration point id
     * @return the configuration point definition
     */
    public ConfigurationPointDefinition getConfigurationPoint(String qualifiedConfigurationPointId);

}