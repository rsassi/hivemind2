package org.apache.hivemind.internal;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.InterceptorConstructor;
import org.apache.hivemind.impl.BaseLocatable;

/**
 * Ancestor for implementions of {@link InterceptorConstructor}.
 * 
 * @author Achim Huegen
 */
public abstract class AbstractServiceInterceptorConstructor extends BaseLocatable implements InterceptorConstructor
{

    public AbstractServiceInterceptorConstructor(Location location)
    {
        super(location);
    }
    
    public abstract void constructServiceInterceptor(InterceptorStack interceptorStack, Module contributingModule);

}
