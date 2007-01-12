package org.apache.hivemind.annotations;

import hivemind.test.services.ServiceAutowireTarget;
import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

public class AutowiringModule extends AbstractAnnotatedModule
{
    @Service(id = "AutowireTarget")
    public ServiceAutowireTarget getAutowireTarget()
    {
        ServiceAutowireTarget target = new ServiceAutowireTarget();
        return autowireProperties(target);
    }

    @Service(id = "StringHolder")
    public StringHolder getStringHolder()
    {
        return new StringHolderImpl();
    }
}
