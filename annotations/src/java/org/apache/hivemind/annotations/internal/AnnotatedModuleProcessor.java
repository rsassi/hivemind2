package org.apache.hivemind.annotations.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.annotations.Configuration;
import org.apache.hivemind.annotations.Module;
import org.apache.hivemind.annotations.Service;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;
import org.apache.hivemind.definition.impl.ContributionDefinitionImpl;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.ServiceImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.hivemind.util.IdUtils;

/**
 * Does the work for {@link org.apache.hivemind.annotations.AnnotatedModuleReader}. Processes an
 * annotated class and registers the defined extension and extension points in a registry
 * definition.
 * 
 * @author Achim Huegen
 */
public class AnnotatedModuleProcessor
{
    private static final Log _log = LogFactory.getLog(AnnotatedModuleProcessor.class);

    private ClassResolver _classResolver;

    private ErrorHandler _errorHandler;

    private RegistryDefinition _registryDefinition;

    public AnnotatedModuleProcessor(RegistryDefinition registryDefinition,
            ClassResolver classResolver, ErrorHandler errorHandler)
    {
        _registryDefinition = registryDefinition;
        _classResolver = classResolver;
        _errorHandler = errorHandler;
    }

    /**
     * Processes a module. Inspects the class
     * 
     * @param moduleClass
     */
    public void processModule(Class moduleClass)
    {
        ModuleDefinitionImpl module = new ModuleDefinitionImpl(determineModuleId(moduleClass),
                createModuleLocation(moduleClass), _classResolver, moduleClass.getPackage().getName());

        // processServices(moduleClass);

        ModuleInstanceProvider instanceProvider = new ModuleInstanceProviderImpl(moduleClass,
                module.getId());
        // Register provider as initialization provider so it can acquire a reference to the
        // registry
        _registryDefinition.addRegistryInitializationListener(instanceProvider);

        processModuleMethods(moduleClass, module, instanceProvider);
        _registryDefinition.addModule(module);

    }

    public void processModuleMethods(Class moduleClass, ModuleDefinitionImpl module,
            ModuleInstanceProvider instanceProvider)
    {
        Method[] methods = moduleClass.getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            processMethod(method, module, instanceProvider);
        }
    }

    public void processMethod(Method method, ModuleDefinitionImpl module,
            ModuleInstanceProvider instanceProvider)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Checking method " + method.getName() + " for annotations");
        }

        Annotation[] annotations = method.getAnnotations();
        for (int j = 0; j < annotations.length; j++)
        {
            Annotation annotation = annotations[j];

            if (Service.class.equals(annotation.annotationType()))
            {
                processAnnotatedServiceMethod(
                        method,
                        (Service) annotation,
                        module,
                        instanceProvider);
            }
            else if (Configuration.class.equals(annotation.annotationType()))
            {
                processAnnotatedConfigurationMethod(
                        method,
                        (Configuration) annotation,
                        module,
                        instanceProvider);
            }
            else if (org.apache.hivemind.annotations.Contribution.class.equals(annotation.annotationType()))
            {
                processAnnotatedContributionMethod(
                        method,
                        (org.apache.hivemind.annotations.Contribution) annotation,
                        module,
                        instanceProvider);
            }
        }

    }

    private void processAnnotatedServiceMethod(Method method, Service service,
            ModuleDefinitionImpl module, ModuleInstanceProvider instanceProvider)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as service point.");
        }
        
        Location location = new AnnotatedModuleLocation(module.getLocation().getResource(), 
                method.getDeclaringClass(), method);

        ServicePointDefinitionImpl spd = new ServicePointDefinitionImpl(module, service.id(), location, 
                Visibility.PUBLIC, method.getReturnType().getName());
        module.addServicePoint(spd);

        ImplementationConstructor constructor = new FactoryMethodImplementationConstructor(location, 
                method, instanceProvider);

        ImplementationDefinition sid = new ServiceImplementationDefinitionImpl(module, location, 
                constructor, service.serviceModel(), true);

        spd.addImplementation(sid);

    }

    private void processAnnotatedConfigurationMethod(Method method, Configuration configuration, ModuleDefinitionImpl module, ModuleInstanceProvider instanceProvider)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as configuration point.");
        }
        
        Location location = new AnnotatedModuleLocation(module.getLocation().getResource(), 
                method.getDeclaringClass(), method);
        
        ConfigurationPointDefinitionImpl cpd = new ConfigurationPointDefinitionImpl(module, configuration.id(), 
                location, Visibility.PUBLIC, method.getReturnType().getName(), Occurances.UNBOUNDED);
        module.addConfigurationPoint(cpd);
        
        // Add method implementation as initial contribution
        Contribution contribution = new TemplateMethodContributionConstructor(
                location, method, instanceProvider);
        ContributionDefinitionImpl cd = new ContributionDefinitionImpl(module, location, contribution, true);
        cpd.addContribution(cd);
    }

    private void processAnnotatedContributionMethod(Method method, org.apache.hivemind.annotations.Contribution contribution, ModuleDefinitionImpl module, ModuleInstanceProvider instanceProvider)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as contribution.");
        }
        
        Location location = new AnnotatedModuleLocation(module.getLocation().getResource(), 
                method.getDeclaringClass(), method);
        
        Contribution constructor = new TemplateMethodContributionConstructor(
                location, method, instanceProvider);

        ContributionDefinitionImpl cd = new ContributionDefinitionImpl(module, location, constructor, false);
        String qualifiedConfigurationId = IdUtils.qualify(
                module.getId(),
                contribution.configurationId());
        module.addContribution(qualifiedConfigurationId, cd);

    }
    
    /**
     * Creates a location pointing at the module class. 
     */
    protected Location createModuleLocation(Class moduleClass)
    {
        String path = "/" + moduleClass.getName().replace('.', '/');

        Resource r = new ClasspathResource(_classResolver, path);

        return new AnnotatedModuleLocation(r, moduleClass);
    }

    /**
     * Determines the module id of the module defined by the annotated class.
     * First priority has a {@link Module} annotation. If none is defined the
     * id is determined from class and package name.
     * 
     * @param moduleClass
     *            the module class
     * @return the id
     */
    private String determineModuleId(Class moduleClass)
    {
        Module moduleAnnotation = (Module) moduleClass.getAnnotation(Module.class);
        if (moduleAnnotation != null) {
            return moduleAnnotation.id();
        } else {
            return getDefaultModuleId(moduleClass);
        }
    }

    private String getDefaultModuleId(Class moduleClass)
    {
        return moduleClass.getName();
    }
}
