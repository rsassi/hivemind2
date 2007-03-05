// Copyright 2007 The Apache Software Foundation
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

package org.apache.hivemind.annotations;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.annotations.internal.AnnotatedModuleProcessor;
import org.apache.hivemind.annotations.internal.AnnotationProcessorRegistryFactory;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.impl.DefaultClassResolver;

public class TestAnnotatedModuleReader extends AnnotationTestCase
{
    public void testSimpleModule()
    {
        TypedRegistry registry = constructRegistry(SimpleAnnotatedModule.class);
        Runnable service = (Runnable) registry.getService("org.apache.hivemind.annotations.SimpleAnnotatedModule.Test", Runnable.class);
        service.run();
    }

    /**
     * Tests the string representation of the location of a module and an extension point
     */
    public void testLocation()
    {
        RegistryDefinition registry = constructRegistryDefinition(SimpleAnnotatedModule.class);
        ModuleDefinition module = registry.getModule("org.apache.hivemind.annotations.SimpleAnnotatedModule");
        assertEquals("Class org.apache.hivemind.annotations.SimpleAnnotatedModule", module.getLocation().toString());
        ServicePointDefinition service = registry.getServicePoint("org.apache.hivemind.annotations.SimpleAnnotatedModule.Test");
        assertEquals("Class org.apache.hivemind.annotations.SimpleAnnotatedModule, method getRunnable", service.getLocation().toString());
    }
    
    /**
     * Checks that an explicitly defined id in the module annotation takes precedence
     * over the package and class name.
     */
    public void testModuleId()
    {
        RegistryDefinition registry = constructRegistryDefinition(ModuleWithExplicitId.class);
        assertNotNull(registry.getModule("Test"));
    }
  
    public void testModuleClassNotFinal()
    {
        AnnotatedModuleProcessor processor = new AnnotatedModuleProcessor(new RegistryDefinitionImpl(),
                new DefaultClassResolver(), AnnotationProcessorRegistryFactory.createDefaultRegistry());
        try
        {
            processor.processModule(FinalModule.class);
            fail("Final class must not be allowed as module class");
        }
        catch (ApplicationRuntimeException expected)
        {
        }   
    }
    
    public void testModuleClassNotAbstract()
    {
        AnnotatedModuleProcessor processor = new AnnotatedModuleProcessor(new RegistryDefinitionImpl(),
                new DefaultClassResolver(), AnnotationProcessorRegistryFactory.createDefaultRegistry());
        try
        {
            processor.processModule(AbstractModule.class);
            fail("Abstract class must not be allowed as module class");
        }
        catch (ApplicationRuntimeException expected)
        {
        }      
    }
    
    public void testModuleClassPublic()
    {
        AnnotatedModuleProcessor processor = new AnnotatedModuleProcessor(new RegistryDefinitionImpl(),
                new DefaultClassResolver(), AnnotationProcessorRegistryFactory.createDefaultRegistry());
        try
        {
            processor.processModule(NotPublicModule.class);
            fail("Protected class must not be allowed as module class");
        }
        catch (ApplicationRuntimeException expected)
        {
        }        
    }

}
