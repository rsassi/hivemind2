package org.apache.hivemind.annotations;

import hivemind.test.services.ServiceAutowireTarget;

import org.apache.hivemind.Registry;

public class TestAnnotatedModules extends AnnotationTestCase
{
    public void testAutowiring()
    {
        Registry registry = constructRegistry(AutowiringModule.class);
        ServiceAutowireTarget service = (ServiceAutowireTarget) registry.getService(ServiceAutowireTarget.class);
        assertNotNull(service.getStringHolder());
    }
    
}
