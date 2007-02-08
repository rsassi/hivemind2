// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.hivemind.impl;

import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Fed a {@link org.apache.hivemind.definition.RegistryDefinition}s, this class will assemble
 * them into a final {@link org.apache.hivemind.internal.RegistryInfrastructure} as well as perform
 * some validations.
 * <p>
 * This class was extracted from {@link org.apache.hivemind.impl.RegistryBuilder}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class RegistryInfrastructureConstructor
{
    private ErrorHandler _errorHandler;

    private Log _log;

    private RegistryInfrastructureImpl _infrastructure;

    public RegistryInfrastructureConstructor(ErrorHandler errorHandler, Log log, Locale locale)
    {
        _errorHandler = errorHandler;
        _log = log;
        
        _infrastructure = new RegistryInfrastructureImpl(_errorHandler, locale);
    }

    /**
     * Shutdown coordinator shared by all objects.
     */

    private ShutdownCoordinator _shutdownCoordinator = new ShutdownCoordinatorImpl();

    /**
     * Constructs the registry infrastructure, based on a blueprint defined by a {@link RegistryDefinition}. 
     * Expects that all extension resolving has already occured.
     */
    public RegistryInfrastructure constructRegistryInfrastructure(RegistryDefinition definition)
    {
        addModules(definition);
        
        _infrastructure.setShutdownCoordinator(_shutdownCoordinator);

        // The caller is responsible for invoking startup().

        return _infrastructure;
    }

    private void addModules(RegistryDefinition definition)
    {
        // Add each module to the registry.

        Iterator i = definition.getModules().iterator();
        while (i.hasNext())
        {
            ModuleDefinition module = (ModuleDefinition) i.next();

            if (_log.isDebugEnabled())
                _log.debug("Adding module " + module.getId() + " to registry");

            addModule(module);
        }
    }

    private void addModule(ModuleDefinition moduleDefinition)
    {
        String id = moduleDefinition.getId();

        if (_log.isDebugEnabled())
            _log.debug("Processing module " + id);

        if (_infrastructure.getModule(id) != null)
        {
            Module existing = _infrastructure.getModule(id);

            _errorHandler.error(_log, ImplMessages.duplicateModuleId(id, existing.getLocation(), moduleDefinition
                    .getLocation()), null, null);

            // Ignore the duplicate module descriptor.
            return;
        }

        ModuleImpl module = new ModuleImpl();

        module.setLocation(moduleDefinition.getLocation());
        module.setModuleId(id);
        module.setPackageName(moduleDefinition.getPackageName());
        module.setClassResolver(moduleDefinition.getClassResolver());

        addServicePoints(moduleDefinition, module);
        
        addConfigurationPoints(moduleDefinition, module);
        
        module.setRegistry(_infrastructure);
        _infrastructure.addModule(module);

    }

    private void addServicePoints(ModuleDefinition md, Module module)
    {
        String moduleId = md.getId();

        for (Iterator services = md.getServicePoints().iterator(); services.hasNext();)
        {
            ServicePointDefinition sd = (ServicePointDefinition) services.next();

            String pointId = moduleId + "." + sd.getId();

            if (_log.isDebugEnabled())
                _log.debug("Creating service point " + pointId);

            // Choose which class to instantiate based on
            // whether the service is create-on-first-reference
            // or create-on-first-use (deferred).

            ServicePointImpl point = new ServicePointImpl(module, sd);

            point.setShutdownCoordinator(_shutdownCoordinator);

            _infrastructure.addServicePoint(point);
        }
    }

    private void addConfigurationPoints(ModuleDefinition md, Module module)
    {
        String moduleId = md.getId();
        for (Iterator points = md.getConfigurationPoints().iterator(); points.hasNext();)
        {
            ConfigurationPointDefinition cpd = (ConfigurationPointDefinition) points.next();

            String pointId = moduleId + "." + cpd.getId();

            if (_log.isDebugEnabled())
                _log.debug("Creating configuration point " + pointId);

            ConfigurationPointImpl point = new ConfigurationPointImpl(module, cpd);

            point.setShutdownCoordinator(_shutdownCoordinator);

            _infrastructure.addConfigurationPoint(point);

        }
    }

}