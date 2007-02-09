package org.apache.hivemind.definition;

import org.apache.hivemind.Orderable;

/**
 * Defines an interceptor for the methods of a {@link ServicePointDefinition service point}.
 * The interceptor is created by an instance of {@link InterceptorConstructor}.
 * 
 * Interceptors are applied in a certain order which bases on the interceptor names. 
 * If an implementation of this interface wants to effect the order it must
 * implement the {@link Orderable} interface too.
 * 
 * @author Huegen
 */
public interface InterceptorDefinition extends ExtensionDefinition
{
    /**
     * @return  the constructor for the creation of a interceptor instance.
     */
    public InterceptorConstructor getInterceptorConstructor();

    /**
     * @return the name of the interceptor. Used for ordering.
     */
    public String getName();

}