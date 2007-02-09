package org.apache.hivemind.annotations;

import hivemind.test.services.ServiceAutowireTarget;
import hivemind.test.services.StringHolder;

import org.apache.hivemind.Registry;

public class TestAnnotatedModules extends AnnotationTestCase
{
    public void testAutowiring()
    {
        Registry registry = constructRegistry(AutowiringModule.class);
        ServiceAutowireTarget service = (ServiceAutowireTarget) registry.getService(ServiceAutowireTarget.class);
        assertNotNull(service.getStringHolder());
    }
    
    public void testSubmodule()
    {
        Registry registry = constructRegistry(Supermodule.class);
        StringHolder service = (StringHolder) registry.getService("super.sub.StringHolder", StringHolder.class);
        assertNotNull(service);
    }

}
