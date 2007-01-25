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

package org.apache.hivemind.service.impl;

import hivemind.test.FrameworkTestCase;

import java.lang.reflect.Constructor;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.definition.impl.ServiceImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.impl.InterceptorStackImpl;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.ClassFactory;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.service.impl.LoggingInterceptorClassFactory}.
 * 
 * @author Howard Lewis Ship
 * @author James Carman
 */
public class TestLoggingInterceptorFactory extends FrameworkTestCase
{
    /**
     * A test for HIVEMIND-55 ... ensure that the LoggingInterceptor can work on
     * top of a JDK proxy.
     */
    public void testLoggingOverProxy() throws Exception
    {
        ClassFactory cf = new ClassFactoryImpl();

        Runnable r = (Runnable) newMock(Runnable.class);
        MockControl logControl = newControl(Log.class);
        Log log = (Log) logControl.getMock();

        LoggingInterceptorClassFactory f = new LoggingInterceptorClassFactory(cf);

        MockControl spControl = newControl(ServicePoint.class);
        ServicePoint sp = (ServicePoint) spControl.getMock();
        
        // Training

        sp.getServiceInterface();
        spControl.setReturnValue(Runnable.class);
                
        sp.getExtensionPointId();
        spControl.setReturnValue("foo.bar");
        
        replayControls();

        // Create interceptor
        InterceptorStackImpl is = new InterceptorStackImpl(log, sp, r);

        Class interceptorClass = f.constructInterceptorClass(is, Collections.EMPTY_LIST);
        Constructor c = interceptorClass.getConstructors()[0];

        Object interceptor =  c.newInstance(new Object[] { is.getServiceLog(), is.peek() });
        is.push(interceptor);

        Runnable ri = (Runnable) is.peek();

        verifyControls();

        // Training
        
        log.isDebugEnabled();
        logControl.setReturnValue(true);

        log.debug("BEGIN run()");
        log.debug("END run()");
        
        r.run();

        replayControls();

        ri.run();

        verifyControls();
    }
    
    public void testJavassistProxies() throws Exception {
        
        Registry reg = createRegistry(new JavassistBeanInterfaceFactory(newLocation(), "module"));
		final BeanInterface bean = ( BeanInterface )reg.getService( "hivemind.tests.serviceByInterface.BeanInterface", BeanInterface.class );
		bean.interfaceMethod();
	}
	
	public void testCglibProxies() throws Exception {
        Registry reg = createRegistry(new CglibBeanInterfaceFactory(newLocation(), "module"));
		final BeanInterface bean = ( BeanInterface )reg.getService( "hivemind.tests.serviceByInterface.BeanInterface", BeanInterface.class );
		bean.interfaceMethod();
	}
	
	public void testJdkProxies() throws Exception {
        Registry reg = createRegistry(new JdkBeanInterfaceFactory(newLocation(), "module"));
		final BeanInterface bean = ( BeanInterface )reg.getService( "hivemind.tests.serviceByInterface.BeanInterface", BeanInterface.class );
		bean.interfaceMethod();
	}
	

    /**
     * Builds a registry containing a service "BeanInterface" that constructs its instance
     * by using the passed constructor.
     */
    private Registry createRegistry(ImplementationConstructor constructor)
    {
        RegistryDefinition definition = new RegistryDefinitionImpl();

        ModuleDefinition module = createModuleDefinition("hivemind.tests.serviceByInterface");
        definition.addModule(module);
        
        ServicePointDefinitionImpl sp1 = createServicePointDefinition(module, "BeanInterface", BeanInterface.class);
        ImplementationDefinition impl = new ServiceImplementationDefinitionImpl(module, newLocation(),
                constructor, ServiceModel.SINGLETON, true);
        sp1.addImplementation(impl);
        module.addServicePoint(sp1);
        Registry reg = buildFrameworkRegistry(module);
        return reg;
    }
    
}