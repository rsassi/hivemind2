package org.apache.hivemind.annotations.definition.processors;

import junit.framework.TestCase;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.annotations.internal.AnnotationsExtensionLoader;
import org.apache.hivemind.impl.DefaultClassResolver;

public class TestCustomProcessor extends TestCase
{

    public void testCustomProcessor()
    {
        MappedClassLoader classLoader = new MappedClassLoader(Thread.currentThread().getContextClassLoader());
        classLoader.addResource("META-INF/MANIFEST.MF", TestCustomProcessor.class.getPackage().getName().replace('.', '/') + "/custom-manifest.mf");
        
        ClassResolver classResolver = new DefaultClassResolver(classLoader);
        AnnotationsExtensionLoader loader = new AnnotationsExtensionLoader();
        loader.loadExtensions(classResolver, null);
    }
}
