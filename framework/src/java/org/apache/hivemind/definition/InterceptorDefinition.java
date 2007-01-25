package org.apache.hivemind.definition;


public interface InterceptorDefinition extends ExtensionDefinition
{

    public InterceptorConstructor getInterceptorConstructor();

    public String getName();

}