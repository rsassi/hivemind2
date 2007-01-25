package org.apache.hivemind.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.impl.RegistryPostProcessor;
import org.apache.hivemind.util.IdUtils;

/**
 * Implementation of {@link RegistryDefinition}.
 * 
 * The order of additions of 
 * registry parts is arbitrary. For example there is no guarantee that a configuration
 * point is added before a corresponding contribution is added. 
 * 
 * @author Achim Huegen
 */
public class RegistryDefinitionImpl implements RegistryDefinition
{
    private static final Log LOG = LogFactory.getLog(RegistryDefinitionImpl.class);

    private Map _modules = new HashMap();

    private List _postProcessors = new ArrayList();
    
    private List _initializationListeners = new ArrayList();

    public RegistryDefinitionImpl()
    {
    }
    
    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#addModule(org.apache.hivemind.definition.ModuleDefinition)
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
    
    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#addPostProcessor(org.apache.hivemind.impl.RegistryPostProcessor)
     */
    public void addPostProcessor(RegistryPostProcessor postProcessor)
    {
        _postProcessors.add(postProcessor);
    }

    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getPostProcessors()
     */
    public List getPostProcessors()
    {
        return _postProcessors;
    }

    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#addRegistryInitializationListener(org.apache.hivemind.events.RegistryInitializationListener)
     */
    public void addRegistryInitializationListener(RegistryInitializationListener listener)
    {
        _initializationListeners.add(listener);
    }

    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getRegistryInitializationListeners()
     */
    public List getRegistryInitializationListeners()
    {
        return _initializationListeners;
    }
    
    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getModules()
     */
    public Collection getModules()
    {
        return _modules.values();
    }

    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getModule(java.lang.String)
     */
    public ModuleDefinition getModule(String id)
    {
        return (ModuleDefinition) _modules.get(id);
    }

    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getServicePoint(java.lang.String)
     */
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

    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getConfigurationPoint(java.lang.String)
     */
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
