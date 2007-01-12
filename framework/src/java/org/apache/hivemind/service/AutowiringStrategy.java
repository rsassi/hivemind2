package org.apache.hivemind.service;

import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Wires a single property of an object with a service from the hivemind registry. 
 * The implementations of this interface can use different strategies for 
 * finding a matching service. 
 * 
 * @author Achim Huegen
 */
public interface AutowiringStrategy
{
    /**
     * Strategy that searches for services by the type of a property.
     */
    public final static String BY_TYPE = "ByType";

    /**
     * Autowire a single property. 
     * @param registry  registry for lookup of services
     * @param target  the target object
     * @param propertyName  name of the property
     * @return  true if the wiring has succeeded
     */
    public boolean autowireProperty(RegistryInfrastructure registry, Object target, String propertyName);
}
