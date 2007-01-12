package org.apache.hivemind.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.impl.RegistryPostProcessor;
import org.apache.hivemind.util.IdUtils;

/**
 * 
 * The order of additions of 
 * registry parts is arbitrary. For example there is no guarantee that a configuration
 * point is added before a corresponding contribution is added. 
 * 
 * @author Achim Huegen
 */
public class RegistryDefinition
{
    private static final Log LOG = LogFactory.getLog(RegistryDefinition.class);

    private Map _modules = new HashMap();

    private List _postProcessors = new ArrayList();
    
    private List _initializationListeners = new ArrayList();

    public RegistryDefinition()
    {
    }
    
    /**
     * Adds a module definition.
     * @param module  the module 
     * @throws ApplicationRuntimeException  if another module with the same id already exists.
     */
    public void addModule(ModuleDefinition module) throws ApplicationRuntimeException
    {
        if (_modules.containsKey(module.getId()))
        {
            throw new ApplicationRuntimeException(DefinitionMessages.duplicateModuleId(module.getId()));
        }
        else
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Adding module " + module.getId() + " to registry definition");

            _modules.put(module.getId(), module);
        }
    }
    
    public void addPostProcessor(RegistryPostProcessor postProcessor)
    {
        _postProcessors.add(postProcessor);
    }

    public List getPostProcessors()
    {
        return _postProcessors;
    }

    /**
     * @see RegistryInitializationListener
     */
    public void addRegistryInitializationListener(RegistryInitializationListener listener)
    {
        _initializationListeners.add(listener);
    }

    public List getRegistryInitializationListeners()
    {
        return _initializationListeners;
    }
    
    public Collection getModules()
    {
        return _modules.values();
    }

    public ModuleDefinition getModule(String id)
    {
        return (ModuleDefinition) _modules.get(id);
    }

    public ServicePointDefinition getServicePoint(String qualifiedServicePointId)
    {
        String moduleId = IdUtils.extractModule(qualifiedServicePointId);
        String servicePointId = IdUtils.stripModule(qualifiedServicePointId);

        ServicePointDefinition servicePoint = null;
        ModuleDefinition module = getModule(moduleId);
        if (module != null)
        {
            servicePoint = module.getServicePoint(servicePointId);
        }
        return servicePoint;
    }

    public ConfigurationPointDefinition getConfigurationPoint(String qualifiedConfigurationPointId)
    {
        String moduleId = IdUtils.extractModule(qualifiedConfigurationPointId);
        String configurationPointId = IdUtils.stripModule(qualifiedConfigurationPointId);

        ConfigurationPointDefinition configurationPoint = null;
        ModuleDefinition module = getModule(moduleId);
        if (module != null)
        {
            configurationPoint = module.getConfigurationPoint(configurationPointId);
        }
        return configurationPoint;
    }

}
