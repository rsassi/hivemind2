package org.apache.hivemind.definition;


public interface InterceptorDefinition extends ExtensionDefinition
{

    public InterceptorConstructor getInterceptorConstructor();

    public void setInterceptorConstructor(InterceptorConstructor serviceConstructor);

    public String getName();

}