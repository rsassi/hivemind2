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

package org.apache.hivemind.annotations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.annotations.definition.processors.AnnotationProcessor;
import org.apache.hivemind.annotations.internal.AnnotatedModuleProcessor;
import org.apache.hivemind.annotations.internal.AnnotationProcessorRegistry;
import org.apache.hivemind.annotations.internal.AnnotationProcessorRegistryFactory;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.impl.DefaultClassResolver;

/**
 * Reads an annotated hivemind module into a {@link RegistryDefinition}. Thus
 * the defined services configurations and contributions are added to the
 * registry.
 * The class delegates the work to {@link AnnotatedModuleProcessor}
 * 
 * @author Achim Huegen
 */
public class AnnotatedModuleReader
{
    private static final Log LOG = LogFactory.getLog(AnnotatedModuleReader.class);

    private RegistryDefinition _registryDefinition;

    private ClassResolver _classResolver;

    /**
     * @param registryDefinition  the registry definition to which the modules are added.
     */
    public AnnotatedModuleReader(RegistryDefinition registryDefinition)
    {
        this(registryDefinition, new DefaultClassResolver());
    }

    /**
     * @param registryDefinition  the registry definition to which the modules are added.
     * @param classResolver  the {@link ClassResolver} used to resolve all classes referenced from 
     *          elements inside this module.
     */
    public AnnotatedModuleReader(RegistryDefinition registryDefinition, ClassResolver classResolver)
    {
        _registryDefinition = registryDefinition;
        _classResolver = classResolver;
    }

    /**
     * Reads an annotated module specified by its classname. The module must have a no
     * argument constructor.
     * 
     * @param moduleClassName  class name of the module
     */
    public void readModule(String moduleClassName)
    {
        Class moduleClass = findModuleInClassResolver(moduleClassName);
        readModule(moduleClass);
    }

    /**
     * Reads an annotated module. Uses the the default {@link AnnotationProcessorRegistry}.
     * 
     * @param moduleClass  class of the module
     */
    public void readModule(Class moduleClass)
    {
        // TODO: Better cache the factory for performance?
        AnnotationProcessorRegistryFactory factory = new AnnotationProcessorRegistryFactory();
        readModule(moduleClass, factory.createDefaultRegistry(_classResolver));
    }
    
    /**
     * Reads an annotated module.
     * 
     * @param moduleClass  class of the module
     * @param annotationProcessorRegistry  the registry the holds all known {@link AnnotationProcessor annotation processors}
     */
    public void readModule(Class moduleClass, AnnotationProcessorRegistry annotationProcessorRegistry)
    {
        AnnotatedModuleProcessor _processor = new AnnotatedModuleProcessor(
                _registryDefinition, _classResolver, 
                annotationProcessorRegistry);
        try
        {
            _processor.processModule(moduleClass);
        }
        catch (RuntimeException ex)
        {
            // TODO: Improve exception handling
            throw ex;
//            _errorHandler.error(LOG, ex.getMessage(), HiveMind.getLocation(ex), ex);
        }
    }
    
    private Class findModuleInClassResolver(String type)
    {
        Class result = _classResolver.checkForClass(type);

        if (result == null)
            throw new ApplicationRuntimeException(AnnotationsMessages.unableToFindModuleClass(
                    type, _classResolver));

        return result;
    }

}
