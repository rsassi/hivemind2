package org.apache.hivemind.definition;

import org.apache.hivemind.internal.ServicePoint;

/**
 * Provides access to information and services needed
 * during the construction of an {@link ImplementationConstructor implementation}.
 * 
 * @author Huegen
 */
public interface ImplementationConstructionContext extends ConstructionContext
{
    /**
     * @return  the service point that is currently constructed
     */
    public ServicePoint getServicePoint();

}
