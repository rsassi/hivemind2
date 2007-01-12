package org.apache.hivemind.definition.construction;

import org.apache.hivemind.internal.ServicePoint;

public interface ImplementationConstructionContext extends ConstructionContext
{
    /**
     * @return  the service point that is currently constructed
     */
    public ServicePoint getServicePoint();

}
