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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.annotations.AnnotationsMessages;
import org.apache.hivemind.annotations.definition.Module;
import org.apache.hivemind.annotations.definition.impl.AnnotatedModuleDefinitionImpl;
import org.apache.hivemind.annotations.definition.processors.AnnotationProcessingContext;
import org.apache.hivemind.annotations.definition.processors.AnnotationProcessor;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.util.ClasspathResource;

/**
 * Does the work for {@link org.apache.hivemind.annotations.AnnotatedModuleReader}. 
 * Processes an annotated class and registers the defined extension and extension points 
 * in a registry definition. For each module class a new instance of this processor is created.
 * 
 * The processor iterates over the methods of the module class and their annotations.
 * Annotations defined in ancestors are included too.
 * For each found annotation a list of registered processors for the annotation type 
 * is requested from the associated {@link AnnotationProcessorRegistry}. 
 * The processors are called and can contribute to the module definition.
 * 
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

    private RegistryDefinition _registryDefinition;
    
    private AnnotationProcessorRegistry _annotationProcessorRegistry;

    public AnnotatedModuleProcessor(RegistryDefinition registryDefinition,
            ClassResolver classResolver, AnnotationProcessorRegistry annotationProcessorRegistry)
    {
        _registryDefinition = registryDefinition;
        _classResolver = classResolver;
        _annotationProcessorRegistry = annotationProcessorRegistry;
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
        
        AnnotatedModuleDefinitionImpl module = new AnnotatedModuleDefinitionImpl(moduleId,
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

    private void processModuleMethods(Class moduleClass, AnnotatedModuleDefinitionImpl module,
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

    private void processMethod(Method method, AnnotatedModuleDefinitionImpl module,
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
            
            Location location = new AnnotatedModuleLocation(module.getLocation().getResource(), 
                    method.getDeclaringClass(), method);

            AnnotationProcessingContext context = new AnnotationProcessingContextImpl(
                    _registryDefinition, module, _classResolver,
                    annotation, method, location, instanceProvider,
                    _annotationProcessorRegistry);
            
            List<AnnotationProcessor> processors = _annotationProcessorRegistry.getProcessors(annotation.annotationType());
            if (processors != null) {
                for (AnnotationProcessor processor : processors)
                {
                    processor.processAnnotation(context);
                }
            }
        }

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
