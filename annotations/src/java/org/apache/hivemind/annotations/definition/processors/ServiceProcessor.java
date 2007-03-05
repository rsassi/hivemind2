package org.apache.hivemind.annotations.definition.processors;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.annotations.definition.Service;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.internal.CheckTools;
import org.apache.hivemind.annotations.internal.MethodCallImplementationConstructor;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;

public class ServiceProcessor implements AnnotationProcessor
{
    private static final Log _log = LogFactory.getLog(ServiceProcessor.class);

    /**
     * @see org.apache.hivemind.annotations.definition.processors.AnnotationProcessor#processAnnotation(org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext)
     */
    public boolean processAnnotation(AnnotationProcessingContext context)
    {
        Method method = (Method) context.getAnnotatedElement();
        AnnotatedModuleDefinitionImpl module = context.getModule();
        Service serviceAnnotation = (Service) context.getTargetAnnotation(); 
        
        CheckTools.checkMethodModifiers(method, 0, "service point");
        
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as service point.");
        }
        
        Visibility visibility = Visibility.PUBLIC;
        if (Modifier.isProtected(method.getModifiers())) {
            visibility = Visibility.PRIVATE;
        }
        ServicePointDefinitionImpl spd = new ServicePointDefinitionImpl(module, serviceAnnotation.id(), context.getLocation(), 
                visibility, method.getReturnType().getName());
        module.addServicePoint(spd);

        ImplementationConstructor constructor = new MethodCallImplementationConstructor(context.getLocation(), 
                method, context.getModuleInstanceProvider());

        ImplementationDefinition sid = new ImplementationDefinitionImpl(module, context.getLocation(), 
                constructor, serviceAnnotation.serviceModel(), true);

        spd.addImplementation(sid);

        return true;
    }

}
