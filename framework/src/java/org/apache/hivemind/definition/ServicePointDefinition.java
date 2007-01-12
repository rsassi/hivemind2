package org.apache.hivemind.definition;

import java.util.Collection;

public interface ServicePointDefinition extends ExtensionPointDefinition
{

    public String getInterfaceClassName();

    public Collection getImplementations();

    public ServiceImplementationDefinition getDefaultImplementation();

    public void addImplementation(ServiceImplementationDefinition implementation);

    public Collection getInterceptors();

    public void addInterceptor(ServiceInterceptorDefinition interceptor);

}