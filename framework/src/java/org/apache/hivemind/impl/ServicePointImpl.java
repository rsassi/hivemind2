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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Orderable;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.internal.ServiceModelFactory;
import org.apache.hivemind.order.Orderer;
import org.apache.hivemind.service.InterfaceSynthesizer;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Abstract implementation of {@link org.apache.hivemind.internal.ServicePoint}. Provides some of
 * the machinery for creating new service instances, delegating most of it to the
 * {@link org.apache.hivemind.internal.ServiceModel} instace for the service.
 * 
 * @author Howard Lewis Ship
 */
public final class ServicePointImpl extends AbstractExtensionPoint implements
        ConstructableServicePoint
{
    private Object _service;

    private boolean _building;

    private Class _serviceInterface;

    private Class _declaredInterface;

    private List _orderedInterceptorDefinitions;

    private boolean _interceptorsOrdered;

    private ShutdownCoordinator _shutdownCoordinator;

    private ServiceModel _serviceModelObject;
    
    public ServicePointImpl(Module module, ServicePointDefinition definition)
    {
        super(module, definition);
    }

    protected void extendDescription(ToStringBuilder builder)
    {
        if (_service != null)
            builder.append("service", _service);

        builder.append("serviceInterfaceName", getServiceInterfaceClassName());
        builder.append("serviceModel", getServiceModel());
        builder.append("interceptorDefinitions", getInterceptorDefinitions());

        if (_building)
            builder.append("building", _building);
    }

    public synchronized Class getServiceInterface()
    {
        if (_serviceInterface == null)
            _serviceInterface = lookupServiceInterface();

        return _serviceInterface;
    }

    public synchronized Class getDeclaredInterface()
    {
        if (_declaredInterface == null)
            _declaredInterface = lookupDeclaredInterface();

        return _declaredInterface;
    }

    public String getServiceInterfaceClassName()
    {
        return getServicePointDefinition().getInterfaceClassName();
    }

    private Object getInterceptorDefinitions()
    {
        return getServicePointDefinition().getInterceptors();
    }

    private Class lookupDeclaredInterface()
    {
        Class result = null;

        try
        {
            result = getModule().resolveType(getServiceInterfaceClassName());
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.badInterface(
                    getServiceInterfaceClassName(),
                    getExtensionPointId()), getLocation(), ex);
        }

        return result;
    }

    private Class lookupServiceInterface()
    {
        Class declaredInterface = getDeclaredInterface();

        if (declaredInterface.isInterface())
            return declaredInterface;

        // Not an interface ... a class. Synthesize an interface from the class itself.

        InterfaceSynthesizer is = (InterfaceSynthesizer) getModule().getService(
                HiveMind.INTERFACE_SYNTHESIZER_SERVICE,
                InterfaceSynthesizer.class);

        return is.synthesizeInterface(declaredInterface);
    }

    /**
     * Invoked by {@link #getService(Class)} to get a service implementation from the
     * {@link ServiceModel}.
     * <p>
     * TODO: I'm concerned that this synchronized method could cause a deadlock. It would take a LOT
     * (mutually dependent services in multiple threads being realized at the same time).
     */
    private synchronized Object getService()
    {
        if (_service == null)
        {

            if (_building)
                throw new ApplicationRuntimeException(ImplMessages.recursiveServiceBuild(this));

            _building = true;

            try
            {

                ServiceModelFactory factory = getModule().getServiceModelFactory(getServiceModel());

                _serviceModelObject = factory.createServiceModelForService(this);

                _service = _serviceModelObject.getService();
            }
            finally
            {
                _building = false;
            }
        }

        return _service;
    }

    public Object getService(Class serviceInterface)
    {
        Object result = getService();

        if (!serviceInterface.isAssignableFrom(result.getClass()))
        {
            throw new ApplicationRuntimeException(ImplMessages.serviceWrongInterface(
                    this,
                    serviceInterface), getLocation(), null);
        }

        return result;
    }

    public String getServiceModel()
    {
        if (getServicePointDefinition() == null)
            return null;

        return getImplementationDefinition().getServiceModel();
    }

    public void clearConstructorInformation()
    {
        _orderedInterceptorDefinitions = null;
    }

    // Hm. Does this need to be synchronized?

    /**
     * @return  Ordered list of {@link InterceptorDefinition}s 
     */
   public List getOrderedInterceptorContributions()
    {
        if (!_interceptorsOrdered)
        {
            _orderedInterceptorDefinitions = orderInterceptors();
            _interceptorsOrdered = true;
        }

        return _orderedInterceptorDefinitions;
    }

    /**
     * @return  Ordered list of {@link InterceptorDefinition}s 
     */
    private List orderInterceptors()
    {
        Collection interceptorDefinitions = getServicePointDefinition().getInterceptors();
        if (HiveMind.isEmpty(interceptorDefinitions))
            return null;

        // Any error logging should go to the extension point
        // we're constructing.

        Log log = LogFactory.getLog(getExtensionPointId());

        Orderer orderer = new Orderer(log, getModule().getErrorHandler(), ImplMessages
                .interceptorContribution());

        Iterator i = interceptorDefinitions.iterator();
        while (i.hasNext())
        {
            InterceptorDefinition sid = (InterceptorDefinition) i.next();

            // Sort them into runtime excecution order. When we build
            // the interceptor stack we'll apply them in reverse order,
            // building outward from the core service implementation.

            String precedingNames = null;
            String followingNames = null;
            // Check if info about ordering is available
            if (sid instanceof Orderable) {
                Orderable orderable = (Orderable) sid;
                precedingNames = orderable.getPrecedingNames();
                followingNames = orderable.getFollowingNames();
            }
            
            orderer.add(sid, sid.getName(), precedingNames, followingNames);
        }

        return orderer.getOrderedObjects();
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

    public void addRegistryShutdownListener(RegistryShutdownListener listener)
    {
        _shutdownCoordinator.addRegistryShutdownListener(listener);
    }

    /**
     * Forces the service into existence.
     */
    public void forceServiceInstantiation()
    {
        getService();

        _serviceModelObject.instantiateService();
    }

    /**
     * Returns the service constructor.
     */

    public ImplementationConstructor getServiceConstructor()
    {
        return getImplementationDefinition().getServiceConstructor();
    }

    public ImplementationDefinition getImplementationDefinition()
    {
        if (getServicePointDefinition().getDefaultImplementation() == null)
            throw new ApplicationRuntimeException(ImplMessages.servicePointDefinitionWithoutImplementation(getServicePointDefinition()));
        return getServicePointDefinition().getDefaultImplementation();
    }
    
    /**
     * @return  the service point definition that describes this service point
     */
    public ServicePointDefinition getServicePointDefinition()
    {
        return (ServicePointDefinition) super.getDefinition();
    }


}