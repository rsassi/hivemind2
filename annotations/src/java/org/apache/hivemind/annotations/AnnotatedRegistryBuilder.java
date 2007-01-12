package org.apache.hivemind.annotations;

import java.util.Locale;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.RegistryDefinition;
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
    
    public AnnotatedRegistryBuilder()
    {
        this(new DefaultClassResolver(), new DefaultErrorHandler(), Locale.getDefault());
    }
    
    public AnnotatedRegistryBuilder(ClassResolver classResolver, ErrorHandler errorHandler,
            Locale locale)
    {
        _classResolver = classResolver;
        _errorHandler = errorHandler;
        _locale = locale;
        
    }
    
    public Registry constructRegistry(String ... moduleClassNames)
    {
        RegistryDefinition definition = constructRegistryDefinition(moduleClassNames);
        return RegistryBuilder.constructRegistry(definition, _errorHandler, _locale);
    }
    
    public Registry constructRegistry(Class ... moduleClasses)
    {
        RegistryDefinition definition = constructRegistryDefinition(moduleClasses);
        return RegistryBuilder.constructRegistry(definition, _errorHandler, _locale);
    }
    
    private RegistryDefinition constructRegistryDefinition(String ... moduleClassNames)
    {
        RegistryDefinition definition = new RegistryDefinition();

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
        RegistryDefinition definition = new RegistryDefinition();

        for (int i = 0; i < moduleClasses.length; i++)
        {
            AnnotatedModuleReader reader = new AnnotatedModuleReader(definition,
                    _classResolver, _errorHandler);
            reader.readModule(moduleClasses[i]);
        }

        return definition;
    }

}
