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

package hivemind.test.services;

import hivemind.test.FrameworkTestCase;
import hivemind.test.services.impl.ArrayServiceImpl;
import hivemind.test.services.impl.DemoServiceImpl;
import hivemind.test.services.impl.SimpleServiceImpl;
import hivemind.test.services.impl.ToStringImpl;
import hivemind.test.services.impl.TrackerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.InterceptorConstructor;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ModuleDefinitionHelper;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.OrderedServiceInterceptorDefinitionImpl;
import org.apache.hivemind.definition.impl.ServiceInterceptorDefinitionImpl;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.internal.AbstractServiceInterceptorConstructor;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodContribution;
import org.apache.hivemind.service.impl.LoggingInterceptorClassFactory;

/**
 * Tests involving creating and using services.
 *
 * @author Howard Lewis Ship
 */
public class TestServices extends FrameworkTestCase
{

    public void testSimple() throws Exception
    {
        Registry r = buildFrameworkRegistry(new SimpleModule());

        assertNotNull(r);

        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertNotNull(s);
        assertEquals(11, s.add(4, 7));
    }

    /**
     * Test that service instances are cached.
     */
    public void testCache() throws Exception
    {
        Registry r = buildFrameworkRegistry(new SimpleModule());

        assertNotNull(r);

        SimpleService s1 =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);
        SimpleService s2 =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertSame(s1, s2);
    }

    // Note: this works when run by Maven, but for some reason
    // is failing inside Eclipse.  It appears the be a Log4J 
    // configuration problem ... but I have no idea why.

    public void testInterceptorSort() throws Exception
    {
        ModuleDefinition module = new SimpleModule();
        ServicePointDefinition servicePoint = module.getServicePoint("Simple");
        
        InterceptorDefinition interceptor1 = new OrderedServiceInterceptorDefinitionImpl(module, "Fred", newLocation(), new TrackerServiceInterceptorConstructor("Fred"), "Barney", null);
        servicePoint.addInterceptor(interceptor1);
        InterceptorDefinition interceptor2 = new OrderedServiceInterceptorDefinitionImpl(module, "Barney", newLocation(), new TrackerServiceInterceptorConstructor("Barney"), null, null);
        servicePoint.addInterceptor(interceptor2);
        InterceptorDefinition interceptor3 = new OrderedServiceInterceptorDefinitionImpl(module, "Wilma", newLocation(), new TrackerServiceInterceptorConstructor("Wilma"), null, "Barney");
        servicePoint.addInterceptor(interceptor3);
        
        Registry r =
            buildFrameworkRegistry(module);

        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        TrackerFactory.reset();

        assertEquals(11, s.add(4, 7));

        assertListsEqual(
            new String[] { "Wilma:add", "Barney:add", "Fred:add" },
            TrackerFactory.getInvocations().toArray());

    }
    
    class TrackerServiceInterceptorConstructor extends AbstractServiceInterceptorConstructor 
    {
        String _name;
        
        public TrackerServiceInterceptorConstructor(String name)
        {
            super(null);
            _name = name;
        }

        public void constructServiceInterceptor(InterceptorStack interceptorStack,
                Module contributingModule)
        {
            TrackerFactory factory = new TrackerFactory();
            factory.setName(_name);
            factory.createInterceptor(interceptorStack, contributingModule, null);
        }

        public Location getLocation()
        {
            return newLocation();
        }

    }
    
    public void testLogging() throws Exception
    {
        interceptLogging("hivemind.test.services.Demo");

        Registry r = createRegistryWithInterceptedService("Demo", DemoService.class.getName(),
                DemoServiceImpl.class.getName(), Collections.EMPTY_LIST);

        DemoService s =
            (DemoService) r.getService("hivemind.test.services.Demo", DemoService.class);

        s.add(5, 3);

        assertLoggedMessages(
            new String[] {
                "Creating SingletonProxy for service hivemind.test.services.Demo",
                "Constructing core service implementation for service hivemind.test.services.Demo",
                "Applying interceptor factory hivemind.LoggingInterceptor",
                "BEGIN add(5, 3)",
                "END add() [8]" });

        s.noResult();

        assertLoggedMessages(new String[] { "BEGIN noResult()", "END noResult()" });

        try
        {
            s.alwaysFail();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Failure in method alwaysFail.");
        }

        assertLoggedMessages(
            new String[] {
                "BEGIN alwaysFail()",
                "EXCEPTION alwaysFail() -- org.apache.hivemind.ApplicationRuntimeException" });

    }

    /**
     * Builds a registry that contains a single service that is intercepted by logging interceptor.
     */
    private Registry createRegistryWithInterceptedService(String serviceName, String serviceInterface, String implementationClass,
            final List interceptedMethods)
    {
        ModuleDefinitionImpl module = createModuleDefinition("hivemind.test.services");
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(module);
        ServicePointDefinition sp = helper.addServicePoint(serviceName, serviceInterface);
        helper.addSimpleServiceImplementation(sp, implementationClass, ServiceModel.SINGLETON);
        
        // Add logging interceptor
        InterceptorConstructor constructor = new AbstractServiceInterceptorConstructor(module.getLocation()) {

            public void constructServiceInterceptor(InterceptorStack interceptorStack, Module contributingModule)
            {
                ClassFactory cf = (ClassFactory) contributingModule.getService(ClassFactory.class);
                // Create the interceptor with the LoggingInterceptorClassFactory which is quite uncomfortable
                // in the moment
                LoggingInterceptorClassFactory f = new LoggingInterceptorClassFactory(cf);
                Class interceptorClass = f.constructInterceptorClass(interceptorStack, Collections.EMPTY_LIST);
                Constructor c = interceptorClass.getConstructors()[0];
                Object interceptor;
                try
                {
                    interceptor = c.newInstance(new Object[] { interceptorStack.getServiceLog(), interceptorStack.peek() });
                }
                catch (Exception e) {
                    throw new ApplicationRuntimeException(e);
                }
                interceptorStack.push(interceptor);
            }};
        InterceptorDefinition interceptor = new ServiceInterceptorDefinitionImpl(module, "hivemind.LoggingInterceptor", module.getLocation(), constructor);
        sp.addInterceptor(interceptor);
        return buildFrameworkRegistry(module);
    }
    
    /**
     * Test the filters; where we include "no*" but exclude "always*". 
     */
    public void testLoggingMethodFilters() throws Exception
    {
        interceptLogging("hivemind.test.services.Demo");

        // configure intercepted methods: include "no*", exclude "always*"
        List interceptedMethods = new ArrayList();
        MethodContribution include = new MethodContribution();
        include.setMethodPattern("no*");
        include.setInclude(true);
        MethodContribution exclude = new MethodContribution();
        exclude.setMethodPattern("always*");
        interceptedMethods.add(include);
        interceptedMethods.add(exclude);
        
        Registry r = createRegistryWithInterceptedService("Demo", DemoService.class.getName(),
                DemoServiceImpl.class.getName(), interceptedMethods);

        DemoService s =
            (DemoService) r.getService("hivemind.test.services.Demo", DemoService.class);

        s.add(5, 3);

        assertLoggedMessages(
            new String[] {
                "Creating SingletonProxy for service hivemind.test.services.Demo",
                "Constructing core service implementation for service hivemind.test.services.Demo",
                "Applying interceptor factory hivemind.LoggingInterceptor",
                "BEGIN add(5, 3)",
                "END add() [8]" });

        s.noResult();

        assertLoggedMessages(new String[] { "BEGIN noResult()", "END noResult()" });

        try
        {
            s.alwaysFail();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "Failure in method alwaysFail.");
        }

        // Check that no logging took place.

        assertLoggedMessages(new String[0]);
    }

    /**
     * Checks for the detection of a recursive service; one that is dependant on
     * itself.
     */
    public void testRecursiveService() throws Exception
    {
        Registry r = createRegistryWithRecursiveService();

        try
        {
            r.getService("hivemind.test.services.Recursive", Object.class);
            unreachable();
        }
        catch (Exception ex)
        {
            assertExceptionSubstring(
                ex,
                "A recursive call to construct service hivemind.test.services.Recursive has occured.");
        }

    }
    
    /**
     * Builds a registry that contains a single service that references itself during construction
     */
    private Registry createRegistryWithRecursiveService()
    {
        ModuleDefinitionImpl module = createModuleDefinition("hivemind.test.services");
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(module);
        ServicePointDefinition sp = helper.addServicePoint("Recursive", SimpleService.class.getName());

        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(module.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                Object result = new SimpleServiceImpl();
                // Here is the recursion
                context.getService("hivemind.test.services.Recursive", SimpleService.class);
                return result;
            }
        };

        helper.addServiceImplementation(sp, constructor, ServiceModel.PRIMITIVE);
        
        return buildFrameworkRegistry(module);
    }

    /**
     * Test that checks that interceptors don't override toString() if toString()
     * is part of the service interface.
     */
    public void testToString() throws Exception
    {
        Registry r = createRegistryWithInterceptedService("ToString", ToString.class.getName(),
                ToStringImpl.class.getName(), Collections.EMPTY_LIST);

        ToString ts = (ToString) r.getService("hivemind.test.services.ToString", ToString.class);

        interceptLogging("hivemind.test.services.ToString");

        assertEquals("ToStringImpl of toString()", ts.toString());

        List events = getInterceptedLogEvents();
        assertLoggedMessage("BEGIN toString()", events);
        assertLoggedMessage("END toString() [ToStringImpl of toString()]", events);

    }

    public void testArrayResult() throws Exception
    {
        Registry r = createRegistryWithInterceptedService("ArrayResult", ArrayService.class.getName(),
                ArrayServiceImpl.class.getName(), Collections.EMPTY_LIST);

        ArrayService s =
            (ArrayService) r.getService("hivemind.test.services.ArrayResult", ArrayService.class);

        interceptLogging("hivemind.test.services.ArrayResult");

        String[] result = s.returnArrayType();

        assertListsEqual(new String[] { "alpha", "beta" }, result);

        assertLoggedMessage("END returnArrayType() [(java.lang.String[]){alpha, beta}]");
    }


}
