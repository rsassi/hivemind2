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

import hivemind.test.FrameworkTestCase;

import org.apache.hivemind.Registry;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionHelper;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.internal.ServiceModel;

/**
 * @author James Carman
 * @version 1.0
 */
public class TestRegistryShutdownListenerServices extends FrameworkTestCase
{
    private void executeShutdownListenerTest(String serviceModel, boolean manual) throws Exception
    {
        Registry registry = createRegistryWithSimpleService(serviceModel, manual);
        Simple simple = (Simple) registry.getService("hivemind.lib.test.Simple", Simple.class);
        final Counter counter = new Counter();
        simple.setCounter(counter);
        registry.shutdown();
        assertEquals(1, counter.getValue());
    }

    public void testPooledCalled() throws Exception
    {
        executeShutdownListenerTest(ServiceModel.POOLED, true);
        executeShutdownListenerTest(ServiceModel.POOLED, false);
    }

    public void testSingleton() throws Exception
    {
        executeShutdownListenerTest(ServiceModel.SINGLETON, true);
        executeShutdownListenerTest(ServiceModel.SINGLETON, false);
    }

    public void testPrimitive() throws Exception
    {
        executeShutdownListenerTest(ServiceModel.PRIMITIVE, true);
        executeShutdownListenerTest(ServiceModel.PRIMITIVE, false);
    }
    
    public void testSingletonBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = createRegistryWithPojo(ServiceModel.SINGLETON);
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.RegistryShutdownBean", RegistryShutdownBean.class );
        bean.someMethod();
    }

    public void testThreadedBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = createRegistryWithPojo(ServiceModel.THREADED);
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.RegistryShutdownBean", RegistryShutdownBean.class );
        bean.someMethod();
    }

    public void testPooledBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = createRegistryWithPojo(ServiceModel.POOLED);
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.RegistryShutdownBean", RegistryShutdownBean.class );
        bean.someMethod();
    }

    public void testPrimitiveBeanRegistryShutdownListener() throws Exception
    {
        Registry registry = createRegistryWithPojo(ServiceModel.PRIMITIVE);
        RegistryShutdownBean bean = ( RegistryShutdownBean )registry.getService( "hivemind.lib.test.RegistryShutdownBean", RegistryShutdownBean.class );
        bean.someMethod();
    }
    
    /**
     * Creates a Registry with one module, that defines a Service "Simple" with an interface and 
     * separate implementation class. 
     * @param manual  If true, the service is manually added to the {@link ShutdownCoordinator}
     */
    private Registry createRegistryWithSimpleService(final String serviceModel, final boolean manual)
    {
        ModuleDefinitionImpl module = createModuleDefinition("hivemind.lib.test");
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(module);

        ServicePointDefinition sp1 = helper.addServicePoint("Simple", Simple.class.getName());

        // Define inline implementation constructor, that passes the ShutdownCoordinator service if manual is true 
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(module.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                Object result;
                if (manual) {
                    ShutdownCoordinator coordinator = (ShutdownCoordinator) context.getService(ShutdownCoordinator.class);
                    result = new SimpleImpl(coordinator);
                } else {
                    result = new SimpleImpl();
                }
                return result;
            }
        };
        
        helper.addServiceImplementation(sp1, constructor, serviceModel);

        return buildFrameworkRegistry(module);
    }
  
    /**
     * Creates a registry with one module, that defines a Service "RegistryShutdownBean" which
     * is a pojo
     */
    private Registry createRegistryWithPojo(final String serviceModel)
    {
        ModuleDefinitionImpl module = createModuleDefinition("hivemind.lib.test");
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(module);

        ServicePointDefinition sp1 = helper.addServicePoint("RegistryShutdownBean", RegistryShutdownBean.class.getName());
        helper.addSimpleServiceImplementation(sp1, RegistryShutdownBean.class.getName(), serviceModel);

        return buildFrameworkRegistry(module);
    }
     
}
