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

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Defines a service extension point.
 * The definition includes the service interface, implementations and interceptors.
 * 
 * @author Achim Huegen
 */
public interface ServicePointDefinition extends ExtensionPointDefinition
{
    /**
     * @return  the fully qualified class name of the service interface. 
     * This may be the name of a ordinary class or an interface.
     */
    public String getInterfaceClassName();

    /**
     * @return  the default implementation of the service. The default is selected 
     *   by {@link ImplementationDefinition#isDefault()} if multiple exist.
     */
    public ImplementationDefinition getDefaultImplementation();

    /**
     * Adds an implementation definition to the service point.
     * @param implementation  the implementation
     * @throws ApplicationRuntimeException  if this point is not visible from the module
     *    that defines the implementation
     */
    public void addImplementation(ImplementationDefinition implementation);

    /**
     * @return the impelementations of this service point as instances of {@link ImplementationDefinition}
     */
    public Collection getImplementations();

    /**
     * @return the interceptors of this service point as instances of {@link InterceptorDefinition}
     */
    public Collection getInterceptors();

    /**
     * Adds an interceptor definition to the service point.
     * @param interceptor  the interceptor
     * @throws ApplicationRuntimeException  if this point is not visible from the module 
     *   that defines the interceptor.
     */
    public void addInterceptor(InterceptorDefinition interceptor);

}