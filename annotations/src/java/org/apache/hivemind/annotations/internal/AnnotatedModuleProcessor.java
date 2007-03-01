// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.hivemind.annotations.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.annotations.AnnotationsMessages;
import org.apache.hivemind.annotations.definition.Configuration;
import org.apache.hivemind.annotations.definition.Module;
import org.apache.hivemind.annotations.definition.Service;
import org.apache.hivemind.annotations.definition.Submodule;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;
import org.apache.hivemind.definition.impl.ContributionDefinitionImpl;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.ImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.hivemind.util.IdUtils;

/**
 * Does the work for {@link org.apache.hivemind.annotations.AnnotatedModuleReader}. Processes an
 * annotated class and registers the defined extension and extension points in a registry
 * definition.
 * The construction of extension points and extensions bases on reflective method calls
 * to an instance of the module class. The module instance is created by a 
 * {@link ModuleInstanceProvider} during registry construction. 
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
    
    public void processModule(Class moduleClass)
    {
        String moduleId = determineModuleId(moduleClass);
        processModule(moduleClass, moduleId);
    }

    /**
     * Processes a module. Inspects the class.
     * 
     * @param moduleClass
     */
    public void processModule(Class moduleClass, String moduleId)
    {
        checkModuleClassPrerequisites(moduleClass);
        
        ModuleDefinitionImpl module = new ModuleDefinitionImpl(moduleId,
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
    
    /**
     * Ensures that a module class fulfills all prerequisites.
     * 
     * @param moduleClass
     */
    protected void checkModuleClassPrerequisites(Class moduleClass)
    {
        // These modifiers are allowed
        final int validModifiers = Modifier.PUBLIC;
        
        int invalidModifiers = moduleClass.getModifiers() & ~validModifiers;
        if (invalidModifiers > 0) {
            throw new ApplicationRuntimeException(AnnotationsMessages.moduleClassHasInvalidModifiers(moduleClass, invalidModifiers));
        }
        
        // Check for package-private access
        if ((moduleClass.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE)) == 0) {
            throw new ApplicationRuntimeException(AnnotationsMessages.moduleClassIsPackagePrivate(moduleClass));
        }
    }

    private void processModuleMethods(Class moduleClass, ModuleDefinitionImpl module,
            ModuleInstanceProvider instanceProvider)
    {
        // We need access to protected methods via getDeclaredMethods
        // That means we must visit all superclasses manually
        Method[] methods = moduleClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++)
        {
            Method method = methods[i];
            processMethod(method, module, instanceProvider);
            // Process superclass
            Class superClass = moduleClass.getSuperclass();
            if (!superClass.equals(Object.class)) {
                processModuleMethods(superClass, module, instanceProvider);
            }
        }
    }

    private void processMethod(Method method, ModuleDefinitionImpl module,
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
            else if (org.apache.hivemind.annotations.definition.Contribution.class.equals(annotation.annotationType()))
            {
                processAnnotatedContributionMethod(
                        method,
                        (org.apache.hivemind.annotations.definition.Contribution) annotation,
                        module,
                        instanceProvider);
            }
            else if (Submodule.class.equals(annotation.annotationType()))
            {
                processAnnotatedSubmoduleMethod(
                        method,
                        (Submodule) annotation,
                        module,
                        instanceProvider);
            }
        }

    }
    
    /**
     * Ensures that an annotated method has only allowed modifiers.
     * By default Modifier.PUBLIC and Modifier.PROTECTED are allowed.
     * @param method  the method
     * @param allowedModifiers  allowed {@link Modifier modifiers}. 
     * @param methodType  used in error messages to describe what the method is used for
     */
    protected void checkMethodModifiers(Method method, int allowedModifiers, String methodType)
    {
        // These modifiers are allowed
        final int validModifiers = Modifier.PUBLIC | Modifier.PROTECTED | allowedModifiers;
        
        int invalidModifiers = method.getModifiers() & ~validModifiers;
        if (invalidModifiers > 0) {
            throw new ApplicationRuntimeException(AnnotationsMessages.annotatedMethodHasInvalidModifiers(method, methodType, invalidModifiers));
        }

        // TODO: Check for package access
        
        // Check for setAccessible-Errors when Modifier.PROTECTED is used
        if (Modifier.isProtected(method.getModifiers())) {
            // Try to set method accessible
            try
            {
                method.setAccessible(true);
            }
            catch (SecurityException e)
            {
                throw new ApplicationRuntimeException(AnnotationsMessages.annotatedMethodIsProtectedAndNotAccessible(method, methodType));
            }
        }
    }

    private void processAnnotatedServiceMethod(Method method, Service service,
            ModuleDefinitionImpl module, ModuleInstanceProvider instanceProvider)
    {
        checkMethodModifiers(method, 0, "service point");
        
        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as service point.");
        }
        
        Location location = new AnnotatedModuleLocation(module.getLocation().getResource(), 
                method.getDeclaringClass(), method);

        Visibility visibility = Visibility.PUBLIC;
        if (Modifier.isProtected(method.getModifiers())) {
            visibility = Visibility.PRIVATE;
        }
        ServicePointDefinitionImpl spd = new ServicePointDefinitionImpl(module, service.id(), location, 
                visibility, method.getReturnType().getName());
        module.addServicePoint(spd);

        ImplementationConstructor constructor = new MethodCallImplementationConstructor(location, 
                method, instanceProvider);

        ImplementationDefinition sid = new ImplementationDefinitionImpl(module, location, 
                constructor, service.serviceModel(), true);

        spd.addImplementation(sid);

    }

    private void processAnnotatedConfigurationMethod(Method method, Configuration configuration, ModuleDefinitionImpl module, ModuleInstanceProvider instanceProvider)
    {
        checkMethodModifiers(method, 0, "configuration point");

        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as configuration point.");
        }
        
        Location location = new AnnotatedModuleLocation(module.getLocation().getResource(), 
                method.getDeclaringClass(), method);
        
        Visibility visibility = Visibility.PUBLIC;
        if (Modifier.isProtected(method.getModifiers())) {
            visibility = Visibility.PRIVATE;
        }
        ConfigurationPointDefinitionImpl cpd = new ConfigurationPointDefinitionImpl(module, configuration.id(), 
                location, visibility, method.getReturnType().getName(), Occurances.UNBOUNDED,
                false);
        module.addConfigurationPoint(cpd);
        
        // Add method implementation as initial contribution
        Contribution contribution = new MethodCallContributionConstructor(
                location, method, instanceProvider);
        ContributionDefinitionImpl cd = new ContributionDefinitionImpl(module, location, contribution, true);
        cpd.addContribution(cd);
    }

    private void processAnnotatedContributionMethod(Method method, org.apache.hivemind.annotations.definition.Contribution contribution, ModuleDefinitionImpl module, ModuleInstanceProvider instanceProvider)
    {
        checkMethodModifiers(method, 0, "contribution");

        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as contribution.");
        }
        
        Location location = new AnnotatedModuleLocation(module.getLocation().getResource(), 
                method.getDeclaringClass(), method);
        
        Contribution constructor = new MethodCallContributionConstructor(
                location, method, instanceProvider);

        ContributionDefinitionImpl cd = new ContributionDefinitionImpl(module, location, constructor, false);
        String qualifiedConfigurationId = IdUtils.qualify(
                module.getId(),
                contribution.configurationId());
        module.addContribution(qualifiedConfigurationId, cd);

    }
    
    /**
     * Processes a method that is marked as submodule definition.
     */
    private void processAnnotatedSubmoduleMethod(Method method, Submodule submodule, ModuleDefinitionImpl module, ModuleInstanceProvider instanceProvider)
    {
        checkMethodModifiers(method, 0, "submodule");

        if (_log.isDebugEnabled())
        {
            _log.debug("Method " + method.getName() + "classified as submodule.");
        }
        
        String fullModuleId = IdUtils.qualify(
                module.getId(),
                submodule.id());
        // TODO: Check if return type is defined
        AnnotatedModuleProcessor submoduleProcessor = new AnnotatedModuleProcessor(_registryDefinition,
                _classResolver, _errorHandler);
        submoduleProcessor.processModule(method.getReturnType(), fullModuleId);
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
