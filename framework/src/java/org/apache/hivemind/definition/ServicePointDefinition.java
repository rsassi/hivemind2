package org.apache.hivemind.definition;

import java.util.Collection;

/**
 * Defines a service extension point.
 * The definition includes the service interface, implementations and interceptors.
 * 
 * @author Achim Huegen
 */
public interface ServicePointDefinition extends ExtensionPointDefinition
{
    /**
     * @return  the fully qualified class name of the service interface. 
     * This may be the name of a ordinary class or an interface.
     */
    public String getInterfaceClassName();

    /**
     * @return  the default implementation of the service. The default is selected 
     *   by {@link ImplementationDefinition#isDefault()} if multiple exist.
     */
    public ImplementationDefinition getDefaultImplementation();

    /**
     * Adds an implementation definition to the service point.
     * @param implementation  the implementation
     */
    public void addImplementation(ImplementationDefinition implementation);

    /**
     * @return the impelementations of this service point as instances of {@link ImplementationDefinition}
     */
    public Collection getImplementations();

    /**
     * @return the interceptors of this service point as instances of {@link InterceptorDefinition}
     */
    public Collection getInterceptors();

    /**
     * Adds an interceptor definition to the service point.
     * @param interceptor  the interceptor
     */
    public void addInterceptor(InterceptorDefinition interceptor);

}