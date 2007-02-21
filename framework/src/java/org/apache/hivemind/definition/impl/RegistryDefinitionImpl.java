// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.hivemind.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.apache.hivemind.definition.RegistryDefinitionPostProcessor;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.util.IdUtils;

/**
 * Default implementation of {@link RegistryDefinition}.
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
     * @see org.apache.hivemind.definition.RegistryDefinition#addPostProcessor(org.apache.hivemind.definition.RegistryDefinitionPostProcessor)
     */
    public void addPostProcessor(RegistryDefinitionPostProcessor postProcessor)
    {
        _postProcessors.add(postProcessor);
    }

    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getPostProcessors()
     */
    public List getPostProcessors()
    {
        return Collections.unmodifiableList(_postProcessors);
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
        return Collections.unmodifiableList(_initializationListeners);
    }
    
    /**
     * @see org.apache.hivemind.definition.RegistryDefinition#getModules()
     */
    public Collection getModules()
    {
        return Collections.unmodifiableCollection(_modules.values());
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
