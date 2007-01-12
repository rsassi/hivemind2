// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind;

import hivemind.test.FrameworkTestCase;

import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ModuleDefinitionHelper;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.internal.ServiceModel;

/**
 * Integration tests to prove that HiveMind supports JavaBeans classes as the "interface" of a
 * service point.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestBeanService extends FrameworkTestCase
{

    private void attempt(String serviceModel) throws Exception
    {
        Registry reg = createRegistryWithPojo(serviceModel);

        Reverser r = (Reverser) reg.getService("bean.Reverser", Reverser.class);

        assertEquals("DNIMEVIH", r.reverse("HIVEMIND"));
        // Call reverse a second time to check for HIVEMIND-118
        r.reverse("HIVEMIND");
        reg.shutdown();

        try
        {
            r.reverse("SHUTDOWN");
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
        }
    }

    public void testSimple() throws Exception
    {
        attempt(ServiceModel.SINGLETON);
    }

    public void testThreaded() throws Exception
    {
        attempt(ServiceModel.THREADED);
    }
    
    public void testPooled() throws Exception
    {
        attempt(ServiceModel.POOLED);
    }    
    
    /**
     * Creates a Registry with one module, that defines a Service "Reverser" which is a POJO
     */
    private Registry createRegistryWithPojo(final String serviceModel)
    {
        ModuleDefinition module = createModuleDefinition("bean");
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(module);

        ServicePointDefinition sp1 = helper.addServicePoint("Reverser", Reverser.class.getName());
        helper.addSimpleServiceImplementation(sp1, Reverser.class.getName(), serviceModel);

        return buildFrameworkRegistry(module);
    }    
}