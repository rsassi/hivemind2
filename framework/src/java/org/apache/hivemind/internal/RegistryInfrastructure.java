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

import java.util.List;
import java.util.Locale;

import org.apache.hivemind.ErrorHandler;

/**
 * Extension of {@link org.apache.hivemind.Registry} provided by some internals of HiveMind to
 * faciliate the creation of services and configurations.
 * 
 * @author Howard Lewis Ship
 */
public interface RegistryInfrastructure
{
    /**
     * Obtains a service from the registry. Typically, what's returned is a proxy, but that's
     * irrelevant to the caller, which simply will invoke methods of the service interface.
     * 
     * @param serviceId
     *            the fully qualified id of the service to obtain
     * @param serviceInterface
     *            the class to which the service will be cast
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the service be public)
     * @return the service
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if the service does not exist (or is not visible), or if it can't be cast to the
     *             specified service interface
     */

    public Object getService(String serviceId, Class serviceInterface, Module module);

    /**
     * Finds a service that implements the provided interface. Exactly one such service may exist or
     * an exception is thrown.
     * 
     * @param serviceInterface
     *            used to locate the service
     * @param module
     *            the referencing module, used for visibility checks. If null, then only public
     *            service points will be considered.
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if either 0, or more than 1, service point is visible to the module
     */
    public Object getService(Class serviceInterface, Module module);

     /**
     * Returns the specified configuration.
     * 
     * @param configurationId
     *            the fully qualified id of the configuration
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the configuration be public)
     * @return  the configuration
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no such configuration extension point exists (or visible)
     */
    public Object getConfiguration(String configurationId, Module module);
    
    /**
     * Finds a configuration of the specified type. Exactly one such configuration may exist or
     * an exception is thrown.
     * 
     * @param configurationType
     *            the configuration type
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the configuration be public)
     * @return  the configuration
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no such configuration extension point exists (or visible)
     */
    public Object getConfiguration(Class configurationType, Module module);
    
    /**
     * Returns the configuration point.
     * 
     * @param configurationId
     *            the fully qualified id of the configuration
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the configuration be public)
     * @return ConfigurationPoint matching the configuration id
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if the configurationId does not exist (or is not visible)
     */

    public ConfigurationPoint getConfigurationPoint(String configurationId, Module module);

    /**
     * Returns the identified service extension point.
     * 
     * @param serviceId
     *            fully qualified id of the service point
     * @param module
     *            the referencing module, used for visibility checks (null means no module, which
     *            requires that the service be public)
     * @throws org.apache.hivemind.ApplicationRuntimeException
     *             if no such service extension point exists (or is visible to the module)
     */

    public ServicePoint getServicePoint(String serviceId, Module module);

    /**
     * Returns a named service-model factory
     */

    public ServiceModelFactory getServiceModelFactory(String name);

    /**
     * Returns the locale for which the registry was created.
     */

    public Locale getLocale();

    /**
     * Returns the {@link org.apache.hivemind.ErrorHandler} for this Registry.
     */

    public ErrorHandler getErrorHander();

    /**
     * Returns true if a configuration for the specified id exists (and is visible to the specified
     * module).
     * 
     * @param configurationId
     *            to search for
     * @param module
     *            the configuration must be visible to, or null for no module (the application's
     *            view
     * @return true if a configuration for the specified id exists (and is visible to the module)
     * @since 1.1
     */
    public boolean containsConfiguration(String configurationId, Module module);

    /**
     * Returns true if a single service exists which implements the specified service interface and
     * is visible to the given module.
     * 
     * @param serviceInterface
     * @param module
     *            the service must be visible to the module (or null for the application's view)
     * @return true if a single visible service for the specified service interface exists
     * @since 1.1
     */
    public boolean containsService(Class serviceInterface, Module module);

    /**
     * Returns true if a single service with the given id exists which implements the specified
     * service interface and is visible to the given module.
     * 
     * @param serviceId
     * @param serviceInterface
     * @param module
     *            the service must be visible to the module (or null for the application's view)
     * @return true if a single visible service for the specified service id and service interface
     *         exists
     * @since 1.1
     */
    public boolean containsService(String serviceId, Class serviceInterface, Module module);

    /**
     * Invoked once, just after the registry infrastructure is constructed. One time startup
     * operations occur, including execution of any contributions to <code>hivemind.Startup</code>.
     * 
     * @since 1.1
     */

    public void startup();

    /**
     * Shuts down the registry; this notifies all
     * {@link org.apache.hivemind.events.RegistryShutdownListener} services and objects. Once the
     * registry is shutdown, it is no longer valid to obtain new services or configurations, or even
     * use existing services and configurations.
     * 
     * @since 1.1
     */

    public void shutdown();

    /**
     * To be invoked at the start of each request in a multi-threaded environment. Ensures that the
     * receiving Registry will be used if any service proxies are de-serialized.
     * 
     * @since 1.1
     * @see org.apache.hivemind.internal.ser.ServiceSerializationHelper
     * @see org.apache.hivemind.internal.ser.ServiceSerializationSupport
     */

    public void setupThread();

    /**
     * Convienience for invoking
     * {@link org.apache.hivemind.service.ThreadEventNotifier#fireThreadCleanup()}.
     * 
     * @since 1.1
     */

    public void cleanupThread();

    /**
     * @param serviceInterface
     */
    public List getServiceIds(Class serviceInterface);

    /**
     * Returns the module with the corresponding module id.
     * 
     * @param moduleId
     * @return the module with the corresponding module id
     */
    public Module getModule(String moduleId);
    
}