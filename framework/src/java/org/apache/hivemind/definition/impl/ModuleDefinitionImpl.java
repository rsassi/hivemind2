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
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.UnresolvedExtension;

public class ModuleDefinitionImpl implements ModuleDefinition
{
    private String _id;

    private Location _location;

    private String _packageName;

    private ClassResolver _classResolver;
    
    private Map _servicePoints = new HashMap();
    
    private Map _configurationPoints = new HashMap();
    
    private Collection _dependencies = new ArrayList();

    private Collection _unresolvedImplementations = new ArrayList();

    private Collection _unresolvedContributions = new ArrayList();

    private Collection _unresolvedInterceptors = new ArrayList();
    
    private Collection _unresolvedConfigurationParsers = new ArrayList();
    
    public ModuleDefinitionImpl()
    {
    }

    /**
     * @param id
     * @param location
     * @param resolver the {@link ClassResolver} used to resolve all classes referenced from 
     *          elements inside this module.
     * @param packageName  name of the package to search for class names within. If null, it defaults to the id 
     */
    public ModuleDefinitionImpl(String id, Location location, ClassResolver resolver, String packageName)
    {
        _id = id;
        _location = location;
        _classResolver = resolver;
        if (packageName != null)
            _packageName = packageName;
        else _packageName = id;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getClassResolver()
     */
    public ClassResolver getClassResolver()
    {
        return _classResolver;
    }

    /**
     * Sets the {@link ClassResolver} used to resolve all classes referenced from elements 
     * inside the module.
     */
    public void setClassResolver(ClassResolver classResolver)
    {
        _classResolver = classResolver;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getLocation()
     */
    public Location getLocation()
    {
        return _location;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#setLocation(org.apache.hivemind.Location)
     */
    public void setLocation(Location location)
    {
        _location = location;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getId()
     */
    public String getId()
    {
        return _id;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#setId(java.lang.String)
     */
    public void setId(String moduleId)
    {
        this._id = moduleId;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getPackageName()
     */
    public String getPackageName()
    {
        return _packageName;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#setPackageName(java.lang.String)
     */
    public void setPackageName(String packageName)
    {
        _packageName = packageName;
    }
    
    /**
     * Adds a service point definition to the module.
     * @param servicePoint  the service point
     * @throws ApplicationRuntimeException  if another service point with the same id has already been defined
     */
    public void addServicePoint(ServicePointDefinition servicePoint)
    {
        if (_servicePoints.containsKey(servicePoint.getId())) {
            throw new ApplicationRuntimeException(DefinitionMessages.duplicateServicePointId(servicePoint.getQualifiedId(), 
                    this));
        }
        _servicePoints.put(servicePoint.getId(), servicePoint);
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getServicePoint(java.lang.String)
     */
    public ServicePointDefinition getServicePoint(String id)
    {
        return (ServicePointDefinition) _servicePoints.get(id);
    }
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getServicePoints()
     */
    public Collection getServicePoints()
    {
        return Collections.unmodifiableCollection(_servicePoints.values());
    }
 
    /**
     * Adds a configuration point definition to the module.
     * @param configurationPoint  the configuration point
     * @throws ApplicationRuntimeException  if another configuration point with the same id has already been defined
     */
    public void addConfigurationPoint(ConfigurationPointDefinition configurationPoint)
    {
        if (_configurationPoints.containsKey(configurationPoint.getId())) {
            throw new ApplicationRuntimeException(DefinitionMessages.duplicateConfigurationPointId(configurationPoint.getId(), 
                    this));
        }
        _configurationPoints.put(configurationPoint.getId(), configurationPoint);
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getConfigurationPoint(java.lang.String)
     */
    public ConfigurationPointDefinition getConfigurationPoint(String id)
    {
        return (ConfigurationPointDefinition) _configurationPoints.get(id);
    }
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getConfigurationPoints()
     */
    public Collection getConfigurationPoints()
    {
        return Collections.unmodifiableCollection(_configurationPoints.values());
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getDependencies()
     */
    public Collection getDependencies()
    {
        return Collections.unmodifiableCollection(_dependencies);
    }
    
    /**
     * Defines a dependency on another module. The presence of that module
     * is checked during registry construction.
     * 
     * @param dependsOnModuleId  the id of the module this module depends on
     */
    public void addDependency(String dependsOnModuleId) 
    {
        _dependencies.add(dependsOnModuleId);
    }
   
    /**
     * Adds a implementation for a service point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedServicePointId  the fully qualified service point id
     * @param implementation  the implementation definition
     */
    public void addImplementation(String qualifiedServicePointId,
            ImplementationDefinition implementation)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(implementation,
                qualifiedServicePointId);
        _unresolvedImplementations.add(unresolvedExtension);
    }

    /**
     * Adds a interceptor for a service point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedServicePointId  the fully qualified service point id
     * @param interceptor  the interceptor definition
     */
    public void addInterceptor(String qualifiedServicePointId,
            InterceptorDefinition interceptor)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(interceptor,
                qualifiedServicePointId);
        _unresolvedInterceptors.add(unresolvedExtension);
    }

    /**
     * Adds a contribution for a configuration point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedConfigurationPointId  the fully qualified configuration point id
     * @param contribution  the contribution definition
     */
    public void addContribution(String qualifiedConfigurationPointId,
            ContributionDefinition contribution)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(contribution,
                qualifiedConfigurationPointId);
        _unresolvedContributions.add(unresolvedExtension);
    }
    
    /**
     * Adds a configuration parser for a configuration point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedConfigurationPointId  the fully qualified configuration point id
     * @param parser  the parser definition
     */
    public void addConfigurationParser(String qualifiedConfigurationPointId, ConfigurationParserDefinition parser)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(parser,
                qualifiedConfigurationPointId);
        _unresolvedConfigurationParsers.add(unresolvedExtension);
    }  
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getContributions()
     */
    public Collection getContributions()
    {
        return _unresolvedContributions;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getImplementations()
     */
    public Collection getImplementations()
    {
        return _unresolvedImplementations;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getInterceptors()
     */
    public Collection getInterceptors()
    {
        return _unresolvedInterceptors;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getConfigurationParsers()
     */
    public Collection getConfigurationParsers()
    {
        return _unresolvedConfigurationParsers;
    }

  
}
