package org.apache.hivemind.annotations.definition.processors;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.annotations.definition.Submodule;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.internal.AnnotatedModuleProcessor;
import org.apache.hivemind.annotations.internal.CheckTools;
import org.apache.hivemind.util.IdUtils;

public class SubmoduleProcessor implements AnnotationProcessor
{
    private static final Log _log = LogFactory.getLog(SubmoduleProcessor.class);

    /**
     * @see org.apache.hivemind.annotations.definition.processors.AnnotationProcessor#processAnnotation(org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext)
     */
    public boolean processAnnotation(AnnotationProcessingContext context)
    {
        Method method = (Method) context.getAnnotatedElement();
        AnnotatedModuleDefinitionImpl module = context.getModule();
        Submodule submoduleAnnotation = (Submodule) context.getTargetAnnotation(); 
        
        CheckTools.checkMethodModifiers(method, 0, "submodule");
        
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as submodule.");
        }
        
        String fullModuleId = IdUtils.qualify(
                module.getId(),
                submoduleAnnotation.id());
        // TODO: Check if return type is defined
        AnnotatedModuleProcessor submoduleProcessor = context.createSubmoduleProcessor();
        submoduleProcessor.processModule(method.getReturnType(), fullModuleId);

        return true;
    }

}
