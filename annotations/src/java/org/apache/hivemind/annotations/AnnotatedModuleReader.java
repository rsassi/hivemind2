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
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.annotations.internal.AnnotatedModuleProcessor;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.DefaultErrorHandler;

/**
 * Reads a annotated hivemind module into a {@link RegistryDefinition}. Thus
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

    private AnnotatedModuleProcessor _processor;

    private ErrorHandler _errorHandler;

    private ClassResolver _classResolver;

    /**
     * @param registryDefinition  the registry definition to which the modules are added.
     */
    public AnnotatedModuleReader(RegistryDefinition registryDefinition)
    {
        this(registryDefinition, new DefaultClassResolver(), new DefaultErrorHandler());
    }

    /**
     * @param registryDefinition  the registry definition to which the modules are added.
     * @param classResolver  the {@link ClassResolver} used to resolve all classes referenced from 
     *          elements inside this module.
     * @param errorHandler  errorHandler used for handling recoverable errors
     */
    public AnnotatedModuleReader(RegistryDefinition registryDefinition, ClassResolver classResolver,
            ErrorHandler errorHandler)
    {
        _registryDefinition = registryDefinition;
        _classResolver = classResolver;
        _errorHandler = errorHandler;
        _processor = new AnnotatedModuleProcessor(_registryDefinition, _classResolver, _errorHandler);
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
     * Reads an annotated module.
     * 
     * @param moduleClass  class of the module
     */
    public void readModule(Class moduleClass)
    {
        try
        {
            _processor.processModule(moduleClass);
        }
        catch (RuntimeException ex)
        {
            _errorHandler.error(LOG, ex.getMessage(), HiveMind.getLocation(ex), ex);
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
