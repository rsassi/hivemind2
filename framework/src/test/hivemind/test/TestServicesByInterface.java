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

package hivemind.test;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;

/**
 * Tests the Registry.getService(java.lang.Class) functionality.
 *
 * @author Marcus Brito
 */
public class TestServicesByInterface extends FrameworkTestCase
{
    private Registry registry;

    protected void setUp() throws Exception
    {
    	super.setUp();
    	
        ModuleDefinitionImpl module = createModuleDefinition("hivemind.tests.serviceByInterface");

        ServicePointDefinitionImpl sp1 = createServicePointDefinition(module, "uniqueService", IUniqueService.class);
        ImplementationDefinition impl1 = createServiceImplementationDefinition(module, UniqueServiceImpl.class);
        sp1.addImplementation(impl1);

        module.addServicePoint(sp1);

        ServicePointDefinitionImpl sp2 = createServicePointDefinition(module, "multipleServiceOne", IMultipleService.class);
        ImplementationDefinition impl2 = createServiceImplementationDefinition(module, MultipleServiceImpl.class);
        sp2.addImplementation(impl2);

        module.addServicePoint(sp2);

        ServicePointDefinitionImpl sp3 = createServicePointDefinition(module, "multipleServiceTwo", IMultipleService.class);
        sp3.addImplementation(impl2);
        
        module.addServicePoint(sp3);

        registry = buildFrameworkRegistry(module);
    }

    protected void tearDown() throws Exception
    {
    	super.tearDown();
    	
        registry.shutdown();
    }

    public void testUniqueGetServiceByInterface()
    {
        IUniqueService service = (IUniqueService) registry.getService(IUniqueService.class);

        assertNotNull(service);
    }

    public void testNonExistentGetServiceByInterface()
    {
        try
        {
            registry.getService(INonExistentService.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "There is no service point for interface hivemind.test.INonExistentService.");
        }
    }

    public void testMultipleExistentGetServiceByInterface()
    {
        try
        {
            registry.getService(IMultipleService.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "There are multiple service points for interface hivemind.test.IMultipleService: "
                    + "{hivemind.tests.serviceByInterface.multipleServiceTwo,"
                    + " hivemind.tests.serviceByInterface.multipleServiceOne}.");
        }
    }
}
