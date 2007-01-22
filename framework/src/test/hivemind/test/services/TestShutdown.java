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
import hivemind.test.services.impl.RunnableImpl;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.impl.ServiceImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.internal.ServiceModel;

/**
 * Tests shutdown on the registry and on deferred and threaded services.
 * 
 * @author Howard Lewis Ship
 */
public class TestShutdown extends FrameworkTestCase
{

    public void testShutdownSingleton() throws Exception
    {
        Registry r = buildFrameworkRegistry(new SimpleModule());
        SimpleService s = (SimpleService) r.getService(
                "hivemind.test.services.Simple",
                SimpleService.class);

        assertEquals(11, s.add(4, 7));

        r.shutdown();

        try
        {
            s.add(9, 5);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }
    }

    public void testRegistryShutdownUnrepeatable() throws Exception
    {
        Registry r = buildFrameworkRegistry(new SimpleModule());

        r.shutdown();

        try
        {
            r.getConfiguration("foo.bar");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }

        try
        {
            r.shutdown();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }
    }

    public void testShutdownThreaded() throws Exception
    {
        Registry r = buildFrameworkRegistry(new StringHolderModule(ServiceModel.THREADED));

        StringHolder h = (StringHolder) r.getService(
                "hivemind.test.services.StringHolder",
                StringHolder.class);

        assertNull(h.getValue());

        h.setValue("fred");

        assertEquals("fred", h.getValue());

        r.shutdown();

        try
        {
            h.getValue();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }
    }

    public void testSingletonCore() throws Exception
    {
        Registry r = createModuleWithShutdownListener(ServiceModel.SINGLETON);

        Runnable s = (Runnable) r.getService("module1.Listener", Runnable.class);

        interceptLogging("hivemind.test.services.impl.RunnableImpl");

        s.run();

        assertLoggedMessage("run -- singleton");

        r.shutdown();

        assertLoggedMessage("registryDidShutdown -- singleton");
    }

    public void testPrimitiveCore() throws Exception
    {
        Registry r = createModuleWithShutdownListener(ServiceModel.PRIMITIVE);

        Runnable s = (Runnable) r.getService("module1.Listener", Runnable.class);

        interceptLogging("hivemind.test.services.impl.RunnableImpl");

        s.run();

        assertLoggedMessage("run -- primitive");

        r.shutdown();

        assertLoggedMessage("registryDidShutdown -- primitive");
    }

    /**
     * Builds a registry containing a single service "Listener" that implements the {@link RegistryShutdownListener}
     * interface.
     */
    private Registry createModuleWithShutdownListener(final String serviceModel)
    {
        ModuleDefinition module = createModuleDefinition("module1");
        
        ServicePointDefinitionImpl sp1 = createServicePointDefinition(module, "Listener", Runnable.class);
        
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(newLocation()) {

            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                RunnableImpl result = new RunnableImpl();
                result.setType(serviceModel);
                return result;
            }};
        
        ImplementationDefinition impl = new ServiceImplementationDefinitionImpl(module, newLocation(),
                constructor, serviceModel, true);
        sp1.addImplementation(impl);
        module.addServicePoint(sp1);
        return buildFrameworkRegistry(module);
    }
    
}