package org.apache.hivemind.definition;

import java.util.Collection;

public interface ServicePointDefinition extends ExtensionPointDefinition
{

    public String getInterfaceClassName();

    public Collection getImplementations();

    public ImplementationDefinition getDefaultImplementation();

    public void addImplementation(ImplementationDefinition implementation);

    public Collection getInterceptors();

    public void addInterceptor(InterceptorDefinition interceptor);

}