package org.apache.hivemind.definition;

import java.util.Collection;

/**
 * Defines a service extension point.
 * The definition includes the service interface, implementations
 * and interceptors.
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
     * Adds a implementation definition to the module.
     * @param implementation  the implementation
     */
    public void addImplementation(ImplementationDefinition implementation);

    public Collection getImplementations();

    public Collection getInterceptors();

    public void addInterceptor(InterceptorDefinition interceptor);

}