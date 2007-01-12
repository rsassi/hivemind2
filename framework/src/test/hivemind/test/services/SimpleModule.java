package hivemind.test.services;

import hivemind.test.services.impl.SimpleServiceImpl;

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.definition.ModuleDefinitionHelper;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.util.ClasspathResource;

/**
 * Defines a module with one service.
 */
public class SimpleModule extends ModuleDefinitionImpl
{
    public SimpleModule()
    {
        super("hivemind.test.services", createLocation(), new DefaultClassResolver(), null);
        
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(this);
        ServicePointDefinition sp = helper.addServicePoint("Simple", SimpleService.class.getName());
        helper.addSimpleServiceImplementation(sp, SimpleServiceImpl.class.getName(), ServiceModel.SINGLETON);
    }
    
    private static Location createLocation()
    {
        String path = "/" + SimpleModule.class.getName().replace('.', '/');

        Resource r = new ClasspathResource(new DefaultClassResolver(), path);

        return new LocationImpl(r, 1);
    }
    
}
