package org.apache.hivemind.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ExtensionDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ServiceImplementationDefinition;
import org.apache.hivemind.definition.ServiceInterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.Visibility;

public class ServicePointDefinitionImpl extends ExtensionPointDefinitionImpl implements ServicePointDefinition
{
    private String _interfaceClassName;
    
    private Collection _implementations = new ArrayList();

    private Collection _interceptors = new ArrayList(); 

    public ServicePointDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ServicePointDefinitionImpl(ModuleDefinition module, String id, Location location, Visibility visibility, String interfaceClassName)
    {
        super(module, id, location, visibility);
        _interfaceClassName = interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getInterfaceClassName()
     */
    public String getInterfaceClassName()
    {
        return _interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#setInterfaceClassName(java.lang.String)
     */
    public void setInterfaceClassName(String interfaceClassName)
    {
        _interfaceClassName = interfaceClassName;
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getImplementations()
     */
    public Collection getImplementations()
    {
        return _implementations;
    }
    
    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getDefaultImplementation()
     */
    public ServiceImplementationDefinition getDefaultImplementation()
    {
        ServiceImplementationDefinition defaulImplementation = null;
        for (Iterator iter = _implementations.iterator(); iter.hasNext();)
        {
            ServiceImplementationDefinition impl = (ServiceImplementationDefinition) iter.next();
            if (defaulImplementation == null)
                defaulImplementation = impl;
            if (impl.isDefault()) {
                defaulImplementation = impl;
                break;
            }
        }
        
        return defaulImplementation;
    }

    /**
     * Checks if Extension can see this service point. 
     * @throws ApplicationRuntimeException  if not visible
     */
    private void checkVisibility(ExtensionDefinition extension)
    {
        if (Visibility.PRIVATE.equals(getVisibility())
                && !extension.getModuleId().equals(getModuleId()))
        {
            throw new ApplicationRuntimeException(DefinitionMessages.servicePointNotVisible(
                    this,
                    extension.getModule()));
        }
    }
    
    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#addImplementation(org.apache.hivemind.definition.ServiceImplementationDefinition)
     */
    public void addImplementation(ServiceImplementationDefinition implementation)
    {
        checkVisibility(implementation);
        _implementations.add(implementation);
    }
    
    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#getInterceptors()
     */
    public Collection getInterceptors()
    {
        return _interceptors;
    }

    /**
     * @see org.apache.hivemind.definition.ServicePointDefinition#addInterceptor(org.apache.hivemind.definition.ServiceInterceptorDefinitionImpl)
     */
    public void addInterceptor(ServiceInterceptorDefinition interceptor)
    {
        checkVisibility(interceptor);
        _interceptors.add(interceptor);
    }
}
