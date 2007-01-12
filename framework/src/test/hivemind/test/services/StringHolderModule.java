package hivemind.test.services;

import hivemind.test.services.impl.StringHolderImpl;

import java.lang.reflect.Constructor;
import java.util.Collections;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.definition.ModuleDefinitionHelper;
import org.apache.hivemind.definition.ServiceInterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.construction.InterceptorConstructor;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.ServiceInterceptorDefinitionImpl;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.internal.AbstractServiceInterceptorConstructor;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.impl.LoggingInterceptorClassFactory;

/**
 * Defines a module with one threaded service. The service is intercepted by a LoggingInterceptor.
 */
public class StringHolderModule extends ModuleDefinitionImpl
{
    public StringHolderModule(String serviceModel)
    {
        super("hivemind.test.services", null, new DefaultClassResolver(), null);
        
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(this);
        ServicePointDefinition sp = helper.addServicePoint("StringHolder", StringHolder.class.getName());
        helper.addSimpleServiceImplementation(sp, StringHolderImpl.class.getName(), serviceModel);
        
        InterceptorConstructor constructor = new AbstractServiceInterceptorConstructor(getLocation()) {

            public void constructServiceInterceptor(InterceptorStack interceptorStack, Module contributingModule)
            {
                ClassFactory cf = (ClassFactory) contributingModule.getService(ClassFactory.class);
                // Create the interceptor with the LoggingInterceptorClassFactory which is quite uncomfortable
                // in the moment
                LoggingInterceptorClassFactory f = new LoggingInterceptorClassFactory(cf);
                Class interceptorClass = f.constructInterceptorClass(interceptorStack, Collections.EMPTY_LIST);
                Constructor c = interceptorClass.getConstructors()[0];
                Object interceptor;
                try
                {
                    interceptor = c.newInstance(new Object[] { interceptorStack.getServiceLog(), interceptorStack.peek() });
                }
                catch (Exception e) {
                    throw new ApplicationRuntimeException(e);
                }
                interceptorStack.push(interceptor);
            }};
        ServiceInterceptorDefinition interceptor = new ServiceInterceptorDefinitionImpl(helper.getModule(), "hivemind.LoggingInterceptor", getLocation(), constructor);
        sp.addInterceptor(interceptor);
    }
}
