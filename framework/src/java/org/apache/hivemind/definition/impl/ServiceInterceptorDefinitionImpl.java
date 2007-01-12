package org.apache.hivemind.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ServiceInterceptorDefinition;
import org.apache.hivemind.definition.construction.InterceptorConstructor;

/**
 * Implementations of this interface may additionally implement the {@link org.apache.hivemind.Orderable}
 * interface if a certain interceptor order is required.
 */
public class ServiceInterceptorDefinitionImpl extends ExtensionDefinitionImpl implements ServiceInterceptorDefinition
{
    private InterceptorConstructor _interceptorConstructor;
    private String _name;

    public ServiceInterceptorDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ServiceInterceptorDefinitionImpl(ModuleDefinition module, String name, Location location,
            InterceptorConstructor interceptorConstructor)
    {
        super(module, location);
        _name = name;
        _interceptorConstructor = interceptorConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceInterceptorDefinition#getInterceptorConstructor()
     */
    public InterceptorConstructor getInterceptorConstructor()
    {
        return _interceptorConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceInterceptorDefinition#setInterceptorConstructor(org.apache.hivemind.definition.construction.InterceptorConstructor)
     */
    public void setInterceptorConstructor(InterceptorConstructor serviceConstructor)
    {
        _interceptorConstructor = serviceConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceInterceptorDefinition#getName()
     */
    public String getName()
    {
        return _name;
    }

    /**
     * @see org.apache.hivemind.definition.ServiceInterceptorDefinition#setName(java.lang.String)
     */
    public void setName(String name)
    {
        _name = name;
    }
    
}
