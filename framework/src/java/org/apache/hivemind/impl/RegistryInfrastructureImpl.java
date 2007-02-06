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

import java.beans.Introspector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.HiveMindMessages;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.ServiceModelFactory;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.internal.ser.ServiceSerializationHelper;
import org.apache.hivemind.internal.ser.ServiceSerializationSupport;
import org.apache.hivemind.internal.ser.ServiceToken;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.util.Defense;
import org.apache.hivemind.util.PropertyUtils;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of {@link RegistryInfrastructure}.
 * 
 * @author Howard Lewis Ship
 */
public final class RegistryInfrastructureImpl implements RegistryInfrastructure,
        ServiceSerializationSupport
{

    /**
     * Map of {@link Module} keyed on module id.
     */
    private Map _modules = new HashMap();

    /**
     * Map of {@link ServicePoint} keyed on fully qualified service id.
     */
    private Map _servicePoints = new HashMap();

    /**
     * Map of List (of {@link ServicePoint}, keyed on class name service interface.
     */
    private Map _servicePointsByInterfaceClassName = new HashMap();
    
    /**
     * Map of List (of {@link ConfigurationPoint}, keyed on class name service interface.
     */
    private Map _configurationPointsByTypeName = new HashMap();

    /**
     * Map of {@link ConfigurationPoint} keyed on fully qualified configuration id.
     */
    private Map _configurationPoints = new HashMap();

    private ErrorHandler _errorHandler;

    private Locale _locale;

    private ShutdownCoordinator _shutdownCoordinator;

    /**
     * Map of {@link org.apache.hivemind.internal.ser.ServiceToken}, keyed on service id.
     * 
     * @since 1.1
     */

    private Map _serviceTokens;

    /**
     * Map of {@link ServiceModelFactory}, keyed on service model name, loaded from
     * <code>hivemind.ServiceModels</code> configuration point.
     */
    private Map _serviceModelFactories;

    private boolean _started = false;

    private boolean _shutdown = false;

    private ThreadEventNotifier _threadEventNotifier;

    public RegistryInfrastructureImpl(ErrorHandler errorHandler, Locale locale)
    {
        _errorHandler = errorHandler;
        _locale = locale;
    }

    public Locale getLocale()
    {
        return _locale;
    }
    
    public void addModule(Module module)
    {
        checkStarted();

        _modules.put(module.getModuleId(), module);

    }

    public void addServicePoint(ServicePoint point)
    {
        checkStarted();

        _servicePoints.put(point.getExtensionPointId(), point);

        addServicePointByInterface(point);
    }

    private void addServicePointByInterface(ServicePoint point)
    {
        String key = point.getServiceInterfaceClassName();

        List l = (List) _servicePointsByInterfaceClassName.get(key);

        if (l == null)
        {
            l = new LinkedList();
            _servicePointsByInterfaceClassName.put(key, l);
        }

        l.add(point);
    }

    public void addConfigurationPoint(ConfigurationPoint point)
    {
        checkStarted();

        _configurationPoints.put(point.getExtensionPointId(), point);
        
        addConfigurationPointByType(point);
    }

    private void addConfigurationPointByType(ConfigurationPoint point)
    {
        String key = point.getConfigurationType().getName();

        List l = (List) _configurationPointsByTypeName.get(key);

        if (l == null)
        {
            l = new LinkedList();
            _configurationPointsByTypeName.put(key, l);
        }

        l.add(point);
    }

    /**
     * @see org.apache.hivemind.internal.RegistryInfrastructure#getServicePoint(java.lang.String, org.apache.hivemind.internal.Module)
     */
    public ServicePoint getServicePoint(String serviceId, Module module)
    {
        checkShutdown();
        ServicePoint result = (ServicePoint) _servicePoints.get(serviceId);
        if (result == null)
        {
            if (serviceId.indexOf('.') == -1)
            {
                final List possibleMatches = getMatchingServiceIds(serviceId);
                if (!possibleMatches.isEmpty())
                {
                    final StringBuffer sb = new StringBuffer();
                    for (Iterator i = possibleMatches.iterator(); i.hasNext();)
                    {
                        final String matching = (String) i.next();
                        sb.append('\"');
                        sb.append(matching);
                        sb.append('\"');
                        if (i.hasNext())
                        {
                            sb.append(", ");
                        }
                    }
                    throw new ApplicationRuntimeException(ImplMessages.unqualifiedServicePoint(
                            serviceId,
                            sb.toString()));
                }
            }
            throw new ApplicationRuntimeException(ImplMessages.noSuchServicePoint(serviceId));
        }

        if (!result.visibleToModule(module))
            throw new ApplicationRuntimeException(ImplMessages.serviceNotVisible(serviceId, module));

        return result;
    }

    private List getMatchingServiceIds(String serviceId)
    {
        final List possibleMatches = new LinkedList();
        for (Iterator i = _servicePoints.values().iterator(); i.hasNext();)
        {
            final ServicePoint servicePoint = (ServicePoint) i.next();
            if (servicePoint.getExtensionPointId().equals(
                    servicePoint.getModule().getModuleId() + "." + serviceId))
            {
                possibleMatches.add(servicePoint.getExtensionPointId());
            }
        }
        return possibleMatches;
    }

    public Object getService(String serviceId, Class serviceInterface, Module module)
    {
        ServicePoint point = getServicePoint(serviceId, module);

        return point.getService(serviceInterface);
    }

    public Object getService(Class serviceInterface, Module module)
    {
        String key = serviceInterface.getName();

        List servicePoints = (List) _servicePointsByInterfaceClassName.get(key);

        if (servicePoints == null)
            servicePoints = Collections.EMPTY_LIST;

        ServicePoint point = null;
        int count = 0;

        Iterator i = servicePoints.iterator();
        while (i.hasNext())
        {
            ServicePoint sp = (ServicePoint) i.next();

            if (!sp.visibleToModule(module))
                continue;

            point = sp;

            count++;
        }

        if (count == 0)
            throw new ApplicationRuntimeException(ImplMessages
                    .noServicePointForInterface(serviceInterface));

        if (count > 1)
            throw new ApplicationRuntimeException(ImplMessages.multipleServicePointsForInterface(
                    serviceInterface,
                    servicePoints));

        return point.getService(serviceInterface);
    }

    public ConfigurationPoint getConfigurationPoint(String configurationId, Module module)
    {
        checkShutdown();

        ConfigurationPoint result = (ConfigurationPoint) _configurationPoints.get(configurationId);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.noSuchConfiguration(configurationId));

        if (!result.visibleToModule(module))
            throw new ApplicationRuntimeException(ImplMessages.configurationNotVisible(
                    configurationId,
                    module));

        return result;
    }

    public Object getConfiguration(String configurationId, Module module)
    {
        ConfigurationPoint point = getConfigurationPoint(configurationId, module);

        return point.getConfiguration();
    }

    /**
     * @see org.apache.hivemind.internal.RegistryInfrastructure#getConfiguration(java.lang.Class, org.apache.hivemind.internal.Module)
     */
    public Object getConfiguration(Class configurationType, Module module)
    {
        String key = configurationType.getName();

        List configurationPoints = (List) _configurationPointsByTypeName.get(key);

        if (configurationPoints == null)
            configurationPoints = Collections.EMPTY_LIST;

        ConfigurationPoint point = null;
        int count = 0;

        Iterator i = configurationPoints.iterator();
        while (i.hasNext())
        {
            ConfigurationPoint cp = (ConfigurationPoint) i.next();

            if (!cp.visibleToModule(module))
                continue;

            point = cp;

            count++;
        }

        if (count == 0)
            throw new ApplicationRuntimeException(ImplMessages
                    .noConfigurationPointForType(configurationType));

        if (count > 1)
            throw new ApplicationRuntimeException(ImplMessages.multipleConfigurationPointsForType(
                    configurationType,
                    configurationPoints));

        return point.getConfiguration();
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);

        builder.append("locale", _locale);

        return builder.toString();
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

    /**
     * Invokes {@link ShutdownCoordinator#shutdown()}, then releases the coordinator, modules and
     * variable sources.
     */
    public synchronized void shutdown()
    {
        checkShutdown();

        ServiceSerializationHelper.setServiceSerializationSupport(null);

        // Allow service implementations and such to shutdown.

        ShutdownCoordinator coordinatorService = (ShutdownCoordinator) getService(
                "hivemind.ShutdownCoordinator",
                ShutdownCoordinator.class,
                null);

         coordinatorService.shutdown();

        // TODO: Should this be moved earlier?

        _shutdown = true;

        // Shutdown infrastructure items, such as proxies.

        _shutdownCoordinator.shutdown();

        _servicePoints = null;
        _servicePointsByInterfaceClassName = null;
        _configurationPointsByTypeName = null;
        _configurationPoints = null;
        _shutdownCoordinator = null;
        _serviceModelFactories = null;
        _threadEventNotifier = null;
        _serviceTokens = null;

        // It is believed that the cache held by PropertyUtils can affect application shutdown
        // and reload in some servlet containers (such as Tomcat); this should clear that up.

        PropertyUtils.clearCache();
        
        synchronized (HiveMind.INTROSPECTOR_MUTEX)
        {
            Introspector.flushCaches();
        }
    }

    /**
     * Technically, this should be a synchronized method, but the _shutdown variable hardly ever
     * changes, and the consequences are pretty minimal. See HIVEMIND-104.
     */

    private void checkShutdown()
    {
        if (_shutdown)
            throw new ApplicationRuntimeException(HiveMindMessages.registryShutdown());
    }

    private void checkStarted()
    {
        if (_started)
            throw new IllegalStateException(ImplMessages.registryAlreadyStarted());
    }

    /**
     * Starts up the Registry after all service and configuration points have been defined. This
     * locks down the Registry so that no further extension points may be added. This method may
     * only be invoked once.
     * <p>
     * This instance is stored into
     * {@link ServiceSerializationHelper#setServiceSerializationSupport(ServiceSerializationSupport)}.
     * This may cause errors (and incorrect behavior) if multiple Registries exist in a single JVM.
     * <p>
     * In addition, the service <code>hivemind.Startup</code> is obtained and <code>run()</code>
     * is invoked on it. This allows additional startup, provided in the
     * <code>hivemind.Startup</code> configuration point, to be executed.
     */
    public void startup()
    {
        checkStarted();

        ServiceSerializationHelper.setServiceSerializationSupport(this);

        _started = true;

        Runnable startup = (Runnable) getService("hivemind.Startup", Runnable.class, null);

        startup.run();
    }

    public synchronized ServiceModelFactory getServiceModelFactory(String name)
    {
        if (_serviceModelFactories == null)
            readServiceModelFactories();

        ServiceModelFactory result = (ServiceModelFactory) _serviceModelFactories.get(name);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.unknownServiceModel(name));

        return result;
    }

    private void readServiceModelFactories()
    {
        Map sm = (Map) getConfiguration("hivemind.ServiceModels", null);

        _serviceModelFactories = new HashMap();

        Iterator i = sm.values().iterator();

        while (i.hasNext())
        {
            ServiceModelContribution smc = (ServiceModelContribution) i.next();

            String name = smc.getName();

            _serviceModelFactories.put(name, smc.getFactory());
        }
    }

    public synchronized void cleanupThread()
    {
        if (_threadEventNotifier == null)
            _threadEventNotifier = (ThreadEventNotifier) getService(
                    "hivemind.ThreadEventNotifier",
                    ThreadEventNotifier.class,
                    null);

        _threadEventNotifier.fireThreadCleanup();
    }

    public boolean containsConfiguration(String configurationId, Module module)
    {
        checkShutdown();

        ConfigurationPoint result = (ConfigurationPoint) _configurationPoints.get(configurationId);

        return result != null && result.visibleToModule(module);
    }

    public boolean containsService(Class serviceInterface, Module module)
    {
        checkShutdown();

        String key = serviceInterface.getName();

        List servicePoints = (List) _servicePointsByInterfaceClassName.get(key);

        if (servicePoints == null)
            return false;

        int count = 0;

        Iterator i = servicePoints.iterator();
        while (i.hasNext())
        {
            ServicePoint point = (ServicePoint) i.next();

            if (point.visibleToModule(module))
                count++;
        }

        return count == 1;
    }

    public boolean containsService(String serviceId, Class serviceInterface, Module module)
    {
        checkShutdown();

        ServicePoint point = (ServicePoint) _servicePoints.get(serviceId);

        if (point == null)
            return false;

        return point.visibleToModule(module)
                && point.getServiceInterface().equals(serviceInterface);
    }

    public ErrorHandler getErrorHander()
    {
        return _errorHandler;
    }

    public Object getServiceFromToken(ServiceToken token)
    {
        Defense.notNull(token, "token");

        checkShutdown();

        String serviceId = token.getServiceId();

        ServicePoint sp = (ServicePoint) _servicePoints.get(serviceId);

        return sp.getService(Object.class);
    }

    public synchronized ServiceToken getServiceTokenForService(String serviceId)
    {
        Defense.notNull(serviceId, "serviceId");

        checkShutdown();

        if (_serviceTokens == null)
            _serviceTokens = new HashMap();

        ServiceToken result = (ServiceToken) _serviceTokens.get(serviceId);

        if (result == null)
        {
            result = new ServiceToken(serviceId);
            _serviceTokens.put(serviceId, result);
        }

        return result;
    }

    /**
     * Sets the current RI up as the ServiceSerializationSupport. Any service proxy tokens that are
     * de-serialized will find their proxies within this Registry.
     * 
     * @since 1.1
     */

    public void setupThread()
    {
        ServiceSerializationHelper.setServiceSerializationSupport(this);
    }

    public Module getModule(String moduleId)
    {
        return (Module) _modules.get(moduleId);
    }

    /**
     * @see org.apache.hivemind.internal.RegistryInfrastructure#getServiceIds(java.lang.Class)
     */
    public List getServiceIds(Class serviceInterface)
    {
        final List serviceIds = new LinkedList();
        if( serviceInterface == null )
        {
            return serviceIds;
        }
        for (Iterator i = _servicePoints.values().iterator(); i.hasNext();)
        {
            final ServicePoint servicePoint = (ServicePoint) i.next();

            if (serviceInterface.getName().equals( servicePoint.getServiceInterfaceClassName() )
                    && servicePoint.visibleToModule(null))
            {
                serviceIds.add(servicePoint.getExtensionPointId());
            }

        }
        return serviceIds;
    }

}