package org.apache.hivemind.annotations;

import org.apache.hivemind.service.Autowiring;

public class AbstractAnnotatedModule
{
    private Registry _registry;

    public Registry getRegistry()
    {
        return _registry;
    }

    public void setRegistry(Registry registry)
    {
        _registry = registry;
    }
    
    public Autowiring getAutowiring()
    {
        return _registry.getAutowiring();
    }
    
    public <T> T autowireProperties(T target) 
    {
        return (T) getAutowiring().autowireProperties(target);
    }

}
