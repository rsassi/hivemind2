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

import java.util.List;
import java.util.Locale;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.OrderedInterceptorDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.internal.Module;

/**
 * Test for interceptors.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestInterceptors extends FrameworkTestCase
{
    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();

        result.setClassResolver(getClassResolver());
        result.setPackageName("");
        result.setRegistry(new RegistryInfrastructureImpl(new StrictErrorHandler(), Locale
                .getDefault()));
        return result;
    }

    private ServicePointImpl newServicePoint(ModuleDefinition moduleDefinition, Location l, Module module)
    {
        ServicePointDefinitionImpl definition = new ServicePointDefinitionImpl(moduleDefinition, "zip.zap", l, Visibility.PUBLIC, "foo.bar.Baz");
        ServicePointImpl sp = new ServicePointImpl(module, definition);
        return sp;
    }
   
    public void testDefaultInterceptorOrdering()
    {
        Location l = newLocation();
        Module module = newModule();

        replayControls();

        ModuleDefinition moduleDef = createModuleDefinition("module");
        ServicePointImpl sp = newServicePoint(moduleDef, l, module);
        
        InterceptorDefinition interceptor1 = new OrderedInterceptorDefinitionImpl(moduleDef, "Interceptor1", null, null,
                null, null);
        sp.getServicePointDefinition().addInterceptor(interceptor1);
        InterceptorDefinition interceptor2 = new OrderedInterceptorDefinitionImpl(moduleDef, "Interceptor2", null, null,
                null, null);
        sp.getServicePointDefinition().addInterceptor(interceptor2);
//        sp.getServicePointDefinition()setExtensionPointId("ExtensionPointId");
        final List ordered = sp.getOrderedInterceptorContributions();
        assertNotNull(ordered);
        assertEquals(2, ordered.size());
        assertEquals(interceptor1, ordered.get(0));
        assertEquals(interceptor2, ordered.get(1));
        verifyControls();
    }

    public void testCustomInterceptorOrdering()
    {
        Location l = newLocation();
        Module module = newModule();

        replayControls();

        ModuleDefinition moduleDef = createModuleDefinition("module");
        ServicePointImpl sp = newServicePoint(moduleDef, l, module);
        
        InterceptorDefinition interceptor1 = new OrderedInterceptorDefinitionImpl(moduleDef, "Interceptor1", null, null,
                null, null);
        sp.getServicePointDefinition().addInterceptor(interceptor1);
        InterceptorDefinition interceptor2 = new OrderedInterceptorDefinitionImpl(moduleDef, "Interceptor2", null, null,
                null, "Interceptor1");
        sp.getServicePointDefinition().addInterceptor(interceptor2);
        final List ordered = sp.getOrderedInterceptorContributions();
        assertNotNull(ordered);
        assertEquals(2, ordered.size());
        assertEquals(interceptor2, ordered.get(0));
        assertEquals(interceptor1, ordered.get(1));
        verifyControls();
    }
}