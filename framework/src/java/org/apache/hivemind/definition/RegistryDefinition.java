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

package org.apache.hivemind.definition;

import java.util.Collection;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.impl.RegistryBuilder;

/**
 * Defines all modules and their service and configuration points 
 * which build a registry. The definition is a blueprint from which
 * a registry instance is constructed. 
 * 
 * The definition is passed to {@link RegistryBuilder} for the next phase: 
 * the registry construction. 
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
     * Adds a {@link RegistryDefinitionPostProcessor}. The processor is called after all
     * module definitions have been processed.
     * @param postProcessor the processor
     */
    public void addPostProcessor(RegistryDefinitionPostProcessor postProcessor);

    /**
     * @return  a collection of all registered {@link RegistryDefinitionPostProcessor}s
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