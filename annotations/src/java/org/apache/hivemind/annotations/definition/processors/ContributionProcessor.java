package org.apache.hivemind.annotations.definition.processors;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.annotations.definition.Contribution;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.internal.CheckTools;
import org.apache.hivemind.annotations.internal.MethodCallContributionConstructor;
import org.apache.hivemind.definition.impl.ContributionDefinitionImpl;
import org.apache.hivemind.util.IdUtils;

public class ContributionProcessor implements AnnotationProcessor
{
    private static final Log _log = LogFactory.getLog(ContributionProcessor.class);

    /**
     * @see org.apache.hivemind.annotations.definition.processors.AnnotationProcessor#processAnnotation(org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext)
     */
    public boolean processAnnotation(AnnotationProcessingContext context)
    {
        Method method = (Method) context.getAnnotatedElement();
        AnnotatedModuleDefinitionImpl module = context.getModule();
        Contribution contributionAnnotation = (Contribution) context.getTargetAnnotation(); 
        
        CheckTools.checkMethodModifiers(method, 0, "contribution");
        
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as contribution.");
        }
        
        org.apache.hivemind.definition.Contribution constructor = new MethodCallContributionConstructor(
                context.getLocation(), method, context.getModuleInstanceProvider());

        ContributionDefinitionImpl cd = new ContributionDefinitionImpl(module, context.getLocation(), constructor, false);
        String qualifiedConfigurationId = IdUtils.qualify(
                module.getId(),
                contributionAnnotation.configurationId());
        module.addContribution(qualifiedConfigurationId, cd);

        return true;
    }

}
