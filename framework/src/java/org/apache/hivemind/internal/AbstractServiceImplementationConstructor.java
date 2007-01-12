package org.apache.hivemind.internal;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.construction.ImplementationConstructor;
import org.apache.hivemind.impl.BaseLocatable;

/**
 * Ancestor for implementations of {@link ImplementationConstructor}.
 * 
 * @author Achim Huegen
 */
public abstract class AbstractServiceImplementationConstructor extends BaseLocatable implements ImplementationConstructor
{

    public AbstractServiceImplementationConstructor(Location location)
    {
        super(location);
    }

}
