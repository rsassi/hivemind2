package org.apache.hivemind.annotations.definition.processors;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.annotations.definition.Configuration;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.internal.CheckTools;
import org.apache.hivemind.annotations.internal.MethodCallContributionConstructor;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;
import org.apache.hivemind.definition.impl.ContributionDefinitionImpl;

public class ConfigurationProcessor implements AnnotationProcessor
{
    private static final Log _log = LogFactory.getLog(ConfigurationProcessor.class);

    /**
     * @see org.apache.hivemind.annotations.definition.processors.AnnotationProcessor#processAnnotation(org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext)
     */
    public boolean processAnnotation(AnnotationProcessingContext context)
    {
        Method method = (Method) context.getAnnotatedElement();
        AnnotatedModuleDefinitionImpl module = context.getModule();
        Configuration configurationAnnotation = (Configuration) context.getTargetAnnotation(); 
        
        CheckTools.checkMethodModifiers(method, 0, "configuration point");
        
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as configuration point.");
        }
        
        Visibility visibility = Visibility.PUBLIC;
        if (Modifier.isProtected(method.getModifiers())) {
            visibility = Visibility.PRIVATE;
        }
        
        ConfigurationPointDefinitionImpl cpd = new ConfigurationPointDefinitionImpl(module, configurationAnnotation.id(), 
                context.getLocation(), visibility, method.getReturnType().getName(), Occurances.UNBOUNDED,
                false);
        module.addConfigurationPoint(cpd);
        
        // Add method implementation as initial contribution
        Contribution contribution = new MethodCallContributionConstructor(
                context.getLocation(), method, context.getModuleInstanceProvider());
        ContributionDefinitionImpl cd = new ContributionDefinitionImpl(module, context.getLocation(), contribution, true);
        cpd.addContribution(cd);

        return true;
    }

}
