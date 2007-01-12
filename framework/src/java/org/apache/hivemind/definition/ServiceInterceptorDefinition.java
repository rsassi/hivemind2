package org.apache.hivemind.definition;

import org.apache.hivemind.definition.construction.InterceptorConstructor;

public interface ServiceInterceptorDefinition extends ExtensionDefinition
{

    public InterceptorConstructor getInterceptorConstructor();

    public void setInterceptorConstructor(InterceptorConstructor serviceConstructor);

    public String getName();

}