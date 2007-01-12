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

package org.apache.hivemind.impl.servicemodel;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.definition.ServiceImplementationDefinition;
import org.apache.hivemind.definition.ServiceInterceptorDefinition;
import org.apache.hivemind.definition.construction.ImplementationConstructionContext;
import org.apache.hivemind.definition.construction.ImplementationConstructor;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.impl.ConstructableServicePoint;
import org.apache.hivemind.impl.InterceptorStackImpl;
import org.apache.hivemind.impl.ProxyBuilder;
import org.apache.hivemind.internal.ImplementationConstructionContextImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.util.ConstructorUtils;

/**
 * Base class for implementing {@link org.apache.hivemind.internal.ServiceModel}.
 * 
 * @author Howard Lewis Ship
 */
public abstract class AbstractServiceModelImpl implements ServiceModel
{
    /**
     * This log is created from the log's service id, which is the appropriate place to log any
     * messages related to creating (or managing) the service implementation, proxy, etc. Subclasses
     * should make use of this Log as well.
     */
    protected final Log _log;

    private ConstructableServicePoint _servicePoint;

    /** @since 1.1 */
    private Class _bridgeProxyClass;

    public AbstractServiceModelImpl(ConstructableServicePoint servicePoint)
    {
        _log = LogFactory.getLog(servicePoint.getExtensionPointId());

        _servicePoint = servicePoint;
    }

    protected Object addInterceptors(Object core)
    {
        List interceptors = _servicePoint.getOrderedInterceptorContributions();

        int count = interceptors == null ? 0 : interceptors.size();

        if (count == 0)
            return core;

        InterceptorStackImpl stack = new InterceptorStackImpl(_log, _servicePoint, core);

        // They are sorted into runtime execution order. Since we build from the
        // core service impl outwarads, we have to reverse the runtime execution
        // order to get the build order.
        // That is, if user expects interceptors in order A B C (perhaps using
        // the rules: A before B, C after B).
        // Then that's the order for interceptors list: A B C
        // To get that runtime execution order, we wrap C around the core,
        // wrap B around C, and wrap A around B.

        for (int i = count - 1; i >= 0; i--)
        {
            ServiceInterceptorDefinition id = (ServiceInterceptorDefinition) interceptors
                    .get(i);

            stack.process(id);
        }

        // Whatever's on top is the final service.

        return stack.peek();
    }

    /**
     * Constructs the core service implementation (by invoking the
     * {@link ImplementationConstructor}), and checks that the result is non-null and
     * assignable to the service interface.
     */
    protected Object constructCoreServiceImplementation()
    {
        if (_log.isDebugEnabled())
            _log.debug("Constructing core service implementation for service "
                    + _servicePoint.getExtensionPointId());

        Class serviceInterface = _servicePoint.getServiceInterface();
        Class declaredInterface = _servicePoint.getDeclaredInterface();

        ServiceImplementationDefinition implementationDefinition = _servicePoint.getImplementationDefinition();
        ImplementationConstructor constructor = implementationDefinition.getServiceConstructor();
        // Get a reference to the module that provided the implementation 
        String definingModuleId = implementationDefinition.getModuleId();
        
        Module definingModule = getRegistry().getModule(definingModuleId);
        ImplementationConstructionContext context = new ImplementationConstructionContextImpl(definingModule,
                _servicePoint);
        Object result = constructor.constructCoreServiceImplementation(context);

        if (result == null)
            throw new ApplicationRuntimeException(ServiceModelMessages
                    .factoryReturnedNull(_servicePoint), constructor.getLocation(), null);

        // The factory should provice something that either implements the service interface
        // or the declared interface. Again, they are normally the same, but with services
        // defined in terms of a class (not an interface), the service interface is
        // synthetic, and the declared interface is the actual class.

        if (!(serviceInterface.isInstance(result) || declaredInterface.isInstance(result)))
            throw new ApplicationRuntimeException(ServiceModelMessages.factoryWrongInterface(
                    _servicePoint,
                    result,
                    serviceInterface), constructor.getLocation(), null);

        HiveMind.setLocation(result, constructor.getLocation());

        return result;
    }

    private RegistryInfrastructure getRegistry()
    {
        return _servicePoint.getModule().getRegistry();
    }

    /**
     * Constructs the service implementation; this is invoked from
     * {@link org.apache.hivemind.internal.ServicePoint#getService(Class)} (for singletons), or from
     * the generated deferrable proxy (for most service models). Primarily, invokes
     * {@link #constructNewServiceImplementation()} from within a block that checks for recursive
     * builds.
     */

    protected Object constructServiceImplementation()
    {
        Object result = constructNewServiceImplementation();

        // After succesfully building, we don't need
        // some of the definition stuff again.

        _servicePoint.clearConstructorInformation();

        return result;
    }

    /**
     * Constructs a new implementation of the service, starting with a core implementation, then
     * adding any interceptors.
     */
    protected Object constructNewServiceImplementation()
    {
        try
        {
            Object core = constructCoreServiceImplementation();

            Object intercepted = addInterceptors(core);

            return intercepted;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ServiceModelMessages.unableToConstructService(
                    _servicePoint,
                    ex), ex);
        }

    }

    public ConstructableServicePoint getServicePoint()
    {
        return _servicePoint;
    }

    /**
     * Need to bridge from the service interface to the actual type.
     * 
     * @since 1.1
     */
    protected Object constructBridgeProxy(Object service)
    {
        Class bridgeProxyClass = getBridgeProxyClass(service);

        return ConstructorUtils.invokeConstructor(bridgeProxyClass, new Object[]
        { getServicePoint().getExtensionPointId(), service });
    }

    /**
     * Factored out of {@link #constructBridgeProxy(Object)} to keep the synchronized block as small
     * as possible.
     * 
     * @since 1.2
     */
    private synchronized Class getBridgeProxyClass(Object service)
    {
        if (_bridgeProxyClass == null)
            _bridgeProxyClass = constructBridgeProxyClass(service);

        return _bridgeProxyClass;
    }

    /**
     * Assumes that the factory will keep cranking out instances of the same class.
     * 
     * @since 1.1
     */

    private Class constructBridgeProxyClass(Object service)
    {
        ProxyBuilder builder = new ProxyBuilder("BridgeProxy", getServicePoint().getModule(),
                getServicePoint().getServiceInterface(), getServicePoint().getDeclaredInterface(), false);

        ClassFab cf = builder.getClassFab();

        Class serviceType = service.getClass();

        cf.addField("_service", serviceType);

        cf.addConstructor(new Class[]
        { String.class, serviceType }, null, "{ this($1); _service = $2; }");

        builder.addServiceMethods("_service");

        return cf.createClass();
    }

    /**
     * Invoked after creating a service implementation object; if the object implements
     * {@link org.apache.hivemind.events.RegistryShutdownListener}, then the object is added as a
     * listener.
     * 
     * @param service
     *            the service implementation
     * @see ShutdownCoordinator
     * @since 1.2
     */
    protected void registerWithShutdownCoordinator(Object service)
    {
        if (service instanceof RegistryShutdownListener)
        {
            ShutdownCoordinator coordinator = ((ShutdownCoordinator) getServicePoint().getModule()
                    .getService(ShutdownCoordinator.class));

            RegistryShutdownListener asListener = (RegistryShutdownListener) service;
            coordinator.addRegistryShutdownListener(asListener);
        }
    }
}