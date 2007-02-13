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

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;

/**
 * Defines a module of a {@link RegistryDefinition}. 
 * A module has its own namespace in which configuration points and service
 * points are defined.
 * It can provide extension to extension points in other modules.
 * 
 * @author Huegen
 */
public interface ModuleDefinition
{
    /**
     * @return  the id of the module. It can contain dots.
     */
    public String getId();
    
    /**
     * @return  the {@link ClassResolver} used to resolve all classes referenced from 
     *          elements inside this module.
     */
    public ClassResolver getClassResolver();

    /** 
     * @return the location of the module
     */
    public Location getLocation();

    /**
     * Returns the name of the package to search for class names within. By default, the package
     * name will match the module id.
     */
    public String getPackageName();

    /**
     * Returns a service point that is identified by its id.
     * @param id  the service point id (unqualified, without module id)
     * @return the service point definition
     */
    public ServicePointDefinition getServicePoint(String id);

    /**
     * @return  all {@link ServicePointDefinition service points} defined in this module
     */
    public Collection getServicePoints();

    /**
     * Returns a configuration point that is identified by its id.
     * @param id  the configuration point id (unqualified, without module id)
     * @return the configuration point definition
     */
    public ConfigurationPointDefinition getConfigurationPoint(String id);

    /**
     * @return  all {@link ConfigurationPointDefinition configuration points} defined in this module
     */
    public Collection getConfigurationPoints();

    /**
     * @return  the ids of all modules this module depends on
     */
    public Collection getDependencies();
    
    /**
     * @return  all {@link ImplementationDefinition implementations} contained in this module
     */
    public Collection getImplementations();

    /**
     * @return  all {@link InterceptorDefinition interceptors} contained in this module
     */
    public Collection getInterceptors();

    /**
     * @return  all {@link ContributionDefinition contributions}  contained in this module.
     */
    public Collection getContributions();

    /**
     * @return  all {@link ConfigurationParserDefinition parsers}  contained in this module
     */
    public Collection getConfigurationParsers();

}