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

import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.annotations.internal.TypedRegistryImpl;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Helper class for defining hivemind registries that mainly base on
 * annotated modules.
 * 
 * @author Achim Huegen
 */
public class AnnotatedRegistryBuilder
{
    private ClassResolver _classResolver;
    private ErrorHandler _errorHandler;
    private Locale _locale;
    
    /**
     * Constructor that uses default implementations of ClassResolver, ErrorHandler and Locale.
     */
    public AnnotatedRegistryBuilder()
    {
        this(new DefaultClassResolver(), new DefaultErrorHandler(), Locale.getDefault());
    }
    
    /**
     * Creates a new instance.
     * @param classResolver  the {@link ClassResolver} used to resolve all classes referenced from 
     *          elements inside this module.
     * @param errorHandler  errorHandler used for handling recoverable errors
     * @param locale  the locale to use for the registry
     */
    public AnnotatedRegistryBuilder(ClassResolver classResolver, ErrorHandler errorHandler,
            Locale locale)
    {
        _classResolver = classResolver;
        _errorHandler = errorHandler;
        _locale = locale;
    }
    
    /**
     * Constructs a registry from a couple of annotated module classes specified by their name.
     * @param moduleClassNames  the annotated module class names 
     * @return  the registry
     */
    public TypedRegistry constructRegistry(String ... moduleClassNames)
    {
        RegistryDefinition definition = constructRegistryDefinition(moduleClassNames);
        return constructRegistry(definition);
    }

    /**
     * Constructs a registry from a couple of annotated module classes specified by their class definitions.
     * @param moduleClasses  the annotated module classes 
     * @return  the registry
     */
   public TypedRegistry constructRegistry(Class ... moduleClasses)
    {
        RegistryDefinition definition = constructRegistryDefinition(moduleClasses);
        return constructRegistry(definition);
    }
    
    private RegistryDefinition constructRegistryDefinition(String ... moduleClassNames)
    {
        RegistryDefinition definition = new RegistryDefinitionImpl();

        for (int i = 0; i < moduleClassNames.length; i++)
        {
            AnnotatedModuleReader reader = new AnnotatedModuleReader(definition,
                    _classResolver);
            reader.readModule(moduleClassNames[i]);
        }

        return definition;
    }
    
    private RegistryDefinition constructRegistryDefinition(Class ... moduleClasses)
    {
        RegistryDefinition definition = new RegistryDefinitionImpl();

        for (int i = 0; i < moduleClasses.length; i++)
        {
            AnnotatedModuleReader reader = new AnnotatedModuleReader(definition,
                    _classResolver);
            reader.readModule(moduleClasses[i]);
        }

        return definition;
    }
    
    private TypedRegistry constructRegistry(RegistryDefinition definition)
    {
        // Register a listener that obtains a reference to RegistryInfrastructure
        // which is not visible by other means. 
        RegistryInfrastructureHolder infrastructureHolder = new RegistryInfrastructureHolder();
            
        definition.addRegistryInitializationListener(infrastructureHolder);
        
        RegistryBuilder.constructRegistry(definition, _errorHandler, _locale);
        // Now the RegistryInfrastructureHolder has access to the registry
        return new TypedRegistryImpl(null, infrastructureHolder.getInfrastructure());
    }

    final class RegistryInfrastructureHolder implements RegistryInitializationListener
    {
        private RegistryInfrastructure _infrastructure;

        public void registryInitialized(RegistryInfrastructure registry)
        {
            _infrastructure = registry;   
        }

        public RegistryInfrastructure getInfrastructure()
        {
            return _infrastructure;
        }
    }
}
