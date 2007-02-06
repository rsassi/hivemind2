package org.apache.hivemind.annotations;

import org.apache.hivemind.service.Autowiring;

/**
 * Ancestor for annotated module classes. Provides convenience methods
 * for the access to {@link Registry} and {@link Autowiring}.
 * 
 * @author Achim Huegen
 */
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
    
    /**
     * @return  a reference to the {@link Autowiring} service.
     */
    public Autowiring getAutowiring()
    {
        return _registry.getAutowiring();
    }
    
    /**
     * Autowires any object by use of the {@link Autowiring} service.
     * @param target  the object to wire
     * @return the wired object 
     */
    public <T> T autowireProperties(T target) 
    {
        return (T) getAutowiring().autowireProperties(target);
    }

}
