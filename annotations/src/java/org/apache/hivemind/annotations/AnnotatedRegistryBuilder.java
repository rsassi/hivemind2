package org.apache.hivemind.annotations;

import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.RegistryBuilder;

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
    public Registry constructRegistry(String ... moduleClassNames)
    {
        RegistryDefinition definition = constructRegistryDefinition(moduleClassNames);
        return RegistryBuilder.constructRegistry(definition, _errorHandler, _locale);
    }
    
    /**
     * Constructs a registry from a couple of annotated module classes specified by their class definitions.
     * @param moduleClasses  the annotated module classes 
     * @return  the registry
     */
   public Registry constructRegistry(Class ... moduleClasses)
    {
        RegistryDefinition definition = constructRegistryDefinition(moduleClasses);
        return RegistryBuilder.constructRegistry(definition, _errorHandler, _locale);
    }
    
    private RegistryDefinition constructRegistryDefinition(String ... moduleClassNames)
    {
        RegistryDefinition definition = new RegistryDefinitionImpl();

        for (int i = 0; i < moduleClassNames.length; i++)
        {
            AnnotatedModuleReader reader = new AnnotatedModuleReader(definition,
                    _classResolver, _errorHandler);
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
                    _classResolver, _errorHandler);
            reader.readModule(moduleClasses[i]);
        }

        return definition;
    }

}
