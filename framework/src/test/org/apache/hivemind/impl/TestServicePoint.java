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

import hivemind.test.FrameworkTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.internal.ServiceModelFactory;
import org.easymock.MockControl;

/**
 * Test for {@link org.apache.hivemind.impl.ServicePointImpl}. Much of the testing is done using
 * integration style tests, this just fills in some important gaps.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestServicePoint extends FrameworkTestCase
{
    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();
        result.setModuleId("test");
        result.setClassResolver(getClassResolver());
        result.setPackageName("");
        result.setRegistry(new RegistryInfrastructureImpl(new StrictErrorHandler(), Locale
                .getDefault()));
        return result;
    }

    public void testUnknownInterfaceClass()
    {
        Location l = newLocation();
        Module module = newModule();

        replayControls();

        ModuleDefinition moduleDefinition = createModuleDefinition("module");
        ServicePointDefinitionImpl definition = new ServicePointDefinitionImpl(moduleDefinition, "zip.zap", l, Visibility.PUBLIC, "foo.bar.Baz");
        ServicePointImpl sp = new ServicePointImpl(module, definition);

        try
        {
            sp.getServiceInterface();
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals("Unable to find interface foo.bar.Baz (for service test.zip.zap).", ex
                    .getMessage());
            assertSame(l, ex.getLocation());
        }

        verifyControls();
    }

    public void testResultNotAssignableToServiceInterface()
    {
        Location l = newLocation();

        MockControl modulec = newControl(Module.class);
        Module module = (Module) modulec.getMock();

        ModuleDefinition moduleDef = createModuleDefinition("foo");
        ServicePointDefinitionImpl definition = new ServicePointDefinitionImpl(moduleDef, "bar", l, Visibility.PUBLIC, "java.util.List");
        definition.addImplementation(new ImplementationDefinitionImpl(moduleDef, l, null, "fred", true));
        ServicePointImpl sp = new ServicePointImpl(module, definition);

        Object service = new ArrayList();

        MockControl factoryc = newControl(ServiceModelFactory.class);
        ServiceModelFactory factory = (ServiceModelFactory) factoryc.getMock();

        MockControl modelc = newControl(ServiceModel.class);
        ServiceModel model = (ServiceModel) modelc.getMock();

        module.getServiceModelFactory("fred");
        modulec.setReturnValue(factory);

        factory.createServiceModelForService(sp);
        factoryc.setReturnValue(model);

        model.getService();
        modelc.setReturnValue(service);

        module.getModuleId();
        modulec.setReturnValue("test");
        
        module.resolveType("java.util.List");
        modulec.setReturnValue(List.class);

        replayControls();

        try
        {
            sp.getService(Map.class);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertEquals(
                    "Service test.bar does not implement the requested interface (java.util.Map).  The declared service interface type is java.util.List.",
                    ex.getMessage());
            assertSame(l, ex.getLocation());
        }
    }
}