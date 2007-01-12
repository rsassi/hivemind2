package org.apache.hivemind.internal;

import org.apache.hivemind.definition.construction.ImplementationConstructionContext;

/**
 * Implementation of {@link ImplementationConstructionContext}.
 * 
 * @author Achim Huegen
 */
public class ImplementationConstructionContextImpl extends AbstractConstructionContext implements
        ImplementationConstructionContext
{
    private ServicePoint _servicePoint;

    public ImplementationConstructionContextImpl(Module definingModule, ServicePoint servicePoint)
    {
        super(definingModule);
        _servicePoint = servicePoint;
    }

    public ServicePoint getServicePoint()
    {
        return _servicePoint;
    }
}
