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
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.service.ClassFactory;

/**
 * Tests the {@link org.apache.hivemind.impl.RegistryBuilder} class.
 * 
 * @author Howard Lewis Ship
 */
public class TestRegistryBuilder extends FrameworkTestCase
{

    public void testConstructDefaultRegistry() throws Exception
    {
        Registry r = RegistryBuilder.constructDefaultRegistry();

        ClassFactory factory = (ClassFactory) r.getService(
                "hivemind.ClassFactory",
                ClassFactory.class);

        assertNotNull(factory);
    }

    public void testDuplicateModuleId() throws Exception
    {
        String duplicateModuleId = "non.unique.module";

        RegistryDefinition registryDefinition = new RegistryDefinitionImpl();
        registryDefinition.addModule(createModuleDefinition(duplicateModuleId));
        try
        {
            registryDefinition.addModule(createModuleDefinition(duplicateModuleId));
            fail("Duplicate module id not detected");
        }
        catch (RuntimeException expected)
        {
        }
    }

    public void testDuplicateExtensionPoints() throws Exception
    {
        ModuleDefinition testModule = createModuleDefinition("hivemind.test");

        testModule.addServicePoint(createServicePointDefinition(testModule, "MyService", Comparable.class));
        try
        {
            testModule.addServicePoint(createServicePointDefinition(testModule, "MyService", Comparable.class));
            fail("duplicate service point not detected");
        }
        catch (ApplicationRuntimeException expected)
        {
        }

        testModule.addConfigurationPoint(createConfigurationPointDefinition(testModule, "MyConfig"));
        try
        {
            testModule.addConfigurationPoint(createConfigurationPointDefinition(testModule, "MyConfig"));
            fail("duplicate configuration point not detected");
        }
        catch (ApplicationRuntimeException expected)
        {
        }
    }
}