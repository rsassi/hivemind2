package org.apache.hivemind.annotations;

import org.apache.hivemind.Registry;
import org.apache.hivemind.annotations.AnnotatedModuleReader;
import org.apache.hivemind.annotations.AnnotatedRegistryBuilder;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.test.HiveMindTestCase;

public class AnnotationTestCase extends HiveMindTestCase
{
    protected Registry constructRegistry(String ... moduleClassNames)
    {
        AnnotatedRegistryBuilder builder = new AnnotatedRegistryBuilder();
        return builder.constructRegistry(moduleClassNames);
    }

    protected Registry constructRegistry(Class ... moduleClasses)
    {
        AnnotatedRegistryBuilder builder = new AnnotatedRegistryBuilder();
        return builder.constructRegistry(moduleClasses);
    }

    protected RegistryDefinition constructRegistryDefinition(Class ... moduleClasses)
    {
        RegistryDefinition definition = new RegistryDefinition();

        for (int i = 0; i < moduleClasses.length; i++)
        {
            AnnotatedModuleReader reader = new AnnotatedModuleReader(definition, getClassResolver(),
                    new DefaultErrorHandler());
            reader.readModule(moduleClasses[i]);
        }

        return definition;
    }

}
