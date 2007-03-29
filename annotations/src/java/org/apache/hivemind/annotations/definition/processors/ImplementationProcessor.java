package org.apache.hivemind.annotations.definition.processors;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.annotations.definition.Implementation;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.internal.CheckTools;
import org.apache.hivemind.annotations.internal.MethodCallImplementationConstructor;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.impl.ImplementationDefinitionImpl;
import org.apache.hivemind.util.IdUtils;

public class ImplementationProcessor implements AnnotationProcessor
{
    private static final Log _log = LogFactory.getLog(ImplementationProcessor.class);

    /**
     * @see org.apache.hivemind.annotations.definition.processors.AnnotationProcessor#processAnnotation(org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext)
     */
    public boolean processAnnotation(AnnotationProcessingContext context)
    {
        Method method = (Method) context.getAnnotatedElement();
        AnnotatedModuleDefinitionImpl module = context.getModule();
        Implementation implementationAnnotation = (Implementation) context.getTargetAnnotation(); 
        
        CheckTools.checkMethodModifiers(method, 0, "implementation");
        
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as implementation.");
        }
        
        // Create implementation constructor that calls the annotated method 
        ImplementationConstructor constructor = new MethodCallImplementationConstructor(context.getLocation(), 
                method, context.getModuleInstanceProvider());

        ImplementationDefinition id = new ImplementationDefinitionImpl(module, context.getLocation(), 
                constructor, implementationAnnotation.serviceModel(), true);
        
        String qualifiedServiceId = IdUtils.qualify(
                module.getId(), implementationAnnotation.serviceId());

        module.addImplementation(qualifiedServiceId, id);

        return true;
    }

}
