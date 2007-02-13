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

package org.apache.hivemind.service;

import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Wires a single property of an object with a service from the hivemind registry. 
 * The implementations of this interface can use different strategies for 
 * finding a matching service. 
 * 
 * @author Achim Huegen
 */
public interface AutowiringStrategy
{
    /**
     * Strategy that searches for services by the type of a property.
     */
    public final static String BY_TYPE = "ByType";

    /**
     * Autowire a single property. 
     * @param registry  registry for lookup of services
     * @param target  the target object
     * @param propertyName  name of the property
     * @return  true if the wiring has succeeded
     */
    public boolean autowireProperty(RegistryInfrastructure registry, Object target, String propertyName);
}
