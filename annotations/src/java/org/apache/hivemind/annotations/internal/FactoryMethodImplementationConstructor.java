package org.apache.hivemind.annotations.internal;

import java.lang.reflect.Method;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Location;
import org.apache.hivemind.annotations.Service;
import org.apache.hivemind.definition.construction.ImplementationConstructionContext;
import org.apache.hivemind.definition.construction.ImplementationConstructor;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;

/**
 * Constructs a service implementation by calling a factory method defined in 
 * an annotated module by use of the {@link Service} annotation. 
 * 
 * @author Achim Huegen
 */
public class FactoryMethodImplementationConstructor extends AbstractServiceImplementationConstructor implements
        ImplementationConstructor
{
    private Method _factoryMethod;

    private ModuleInstanceProvider _moduleInstanceProvider;

    public FactoryMethodImplementationConstructor(Location location, Method factoryMethod,
            ModuleInstanceProvider moduleInstanceProvider)
    {
        super(location);
        _factoryMethod = factoryMethod;
        _moduleInstanceProvider = moduleInstanceProvider;
    }

    public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
    {
        try
        {
            Object result = _factoryMethod.invoke(_moduleInstanceProvider.getModuleInstance(), (Object[]) null);
            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
    }

}
