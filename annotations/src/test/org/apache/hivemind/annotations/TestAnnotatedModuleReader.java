package org.apache.hivemind.annotations;

import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;

public class TestAnnotatedModuleReader extends AnnotationTestCase
{
    public void testSimpleModule()
    {
        Registry registry = constructRegistry(SimpleAnnotatedModule.class);
        Runnable service = (Runnable) registry.getService("org.apache.hivemind.annotations.SimpleAnnotatedModule.Test", Runnable.class);
        service.run();
    }

    /**
     * Tests the string representation of the location of a module and an extension point
     */
    public void testLocation()
    {
        RegistryDefinition registry = constructRegistryDefinition((new Class[] {SimpleAnnotatedModule.class}));
        ModuleDefinition module = registry.getModule("org.apache.hivemind.annotations.SimpleAnnotatedModule");
        assertEquals("Class org.apache.hivemind.annotations.SimpleAnnotatedModule", module.getLocation().toString());
        ServicePointDefinition service = registry.getServicePoint("org.apache.hivemind.annotations.SimpleAnnotatedModule.Test");
        assertEquals("Class org.apache.hivemind.annotations.SimpleAnnotatedModule, method getRunnable", service.getLocation().toString());
    }
    
    public void testModuleId()
    {
        RegistryDefinition registry = constructRegistryDefinition((new Class[] {ModuleWithExplicitId.class}));
        assertNotNull(registry.getModule("Test"));
    }
    
}
