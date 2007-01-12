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

package org.apache.hivemind.internal;

import java.util.Locale;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Locatable;
import org.apache.hivemind.Messages;

/**
 * The definition of a HiveMind Module. A Module is a container of service extension points and
 * configuration extension points. It also acts as a "gateway" so that services and configurations
 * in other modules may be accessed.
 * <p>
 * Why do we expose the Module rather than the
 * {@link org.apache.hivemind.internal.RegistryInfrastructure}? It's more than just qualifying ids
 * before passing them up to the RI. At some future point, a concept of visibility will be added to
 * HiveMind. This will make many services and configurations private to the module which defines
 * them and the necessary visibility filtering logic will be here.
 * 
 * @author Howard Lewis Ship
 */
public interface Module extends Locatable
{
    /**
     * Returns the unique identifier for this module.
     */
    public String getModuleId();

    /**
     * Returns true if a single service exists which implements the specified service interface and
     * is visible to this module.
     * 
     * @param serviceInterface
     * @return true if a single visible service for the specified service interface exists
     * @since 1.1
     */
    public boolean containsService(Class serviceInterface);

    /**
     * Looks up the {@link ServicePoint} (throwing an exception if not found) and invokes
     * {@link ServicePoint#getService(Class)}.
     * 
     * @param serviceId
     *            an unqualified id for a service within this module, or a fully qualified id for a
     *            service in this or any other module
     * @param serviceInterface
     *            type the result will be cast to
     */
    public Object getService(String serviceId, Class serviceInterface);

    /**
     * Finds a service that implements the provided interface. Exactly one such service may exist or
     * an exception is thrown.
     * 
     * @param serviceInterface
     *            used to locate the service
     */
    public Object getService(Class serviceInterface);

    /**
     * Returns the identified service extension point.
     * 
     * @param serviceId
     *            an unqualified id for a service within this module, or a fully qualified id for a
     *            service in this or any other module
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no such service extension point exists
     */

    public ServicePoint getServicePoint(String serviceId);

    /**
     * Returns the container for the specified configuration point. 
     * 
     * @param configurationId
     *            an unqualified id for a configuration within this module, or a fully qualified id
     *            for a configuration in this or any other module
     * @throws ApplicationRuntimeException
     *             if this module does not contain the specified configuration extension point.
     */
    public Object getConfiguration(String configurationId);

    /**
     * Returns the resource resolver for this module. The resource resolver is used to locate
     * classes by name (using the correct classloader).
     */
    public ClassResolver getClassResolver();

    /**
     * Returns the class matching the type. First, attempts to resolve the type exactly as is. If
     * that fails, resolves the type within the module's defined package.
     * 
     * @param type
     *            the Java type to convert into a class. May be a primitive type, or an array of
     *            objects or primitives.
     * @return the corresponding {@link Class} object.
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if the type may not be converted into a Class.
     * @since 1.1
     */

    public Class resolveType(String type);

    /**
     * Returns an object that can provide and format localized messages for this module. The
     * messages come from a properties file, <code>hivemodule.properties</code> (localized) stored
     * with the HiveMind deployment descriptor in the META-INF folder.
     */

    public Messages getMessages();

    /**
     * @see RegistryInfrastructure#getServiceModelFactory(String)
     */
    public ServiceModelFactory getServiceModelFactory(String name);

    /**
     * @see org.apache.hivemind.Registry#getLocale()
     */
    public Locale getLocale();

    /**
     * Returns the {@link org.apache.hivemind.ErrorHandler} for this Registry.
     */

    public ErrorHandler getErrorHandler();
    
    /**
     * @return the registry infrastructure interface
     */
    public RegistryInfrastructure getRegistry();
    
}