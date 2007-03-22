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

import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.annotations.AnnotationsExtensionProvider;
import org.apache.hivemind.annotations.AnnotationsMessages;
import org.apache.hivemind.impl.ManifestReader;

/**
 * Loads all implementations of {@link AnnotationsExtensionProvider} that are defined
 * in MANIFEST.MF files in the classpath.
 * The providers get passed in a reference to {@link AnnotationProcessorRegistry} to which
 * they can add {@link org.apache.hivemind.annotations.definition.processors.AnnotationProcessor} classes.
 * 
 * @author Achim Huegen
 */
public class AnnotationsExtensionLoader
{
    private static final Log LOG = LogFactory.getLog(AnnotationsExtensionLoader.class);
    public static final String PROVIDER_ATTRIBUTE_NAME = "annotations-extension-providers";

    public AnnotationsExtensionLoader()
    {
    }
   
    public void loadExtensions(ClassResolver resolver, AnnotationProcessorRegistry annotationProcessorRegistry)
    {
        String[] providerValues = ManifestReader.getAttributeValues(resolver, PROVIDER_ATTRIBUTE_NAME);
        for (int i = 0; i < providerValues.length; i++)
        {
            String providerValue = providerValues[i];
            handleProviderValue(resolver, annotationProcessorRegistry, providerValue);
        }
    }

    /**
     * Parse the providers list in an attribute and load all classes.
     */
    private void handleProviderValue(ClassResolver resolver, AnnotationProcessorRegistry annotationProcessorRegistry, String providers)
    {
        StringTokenizer tokenizer = new StringTokenizer(providers, ",");
        while (tokenizer.hasMoreTokens())
        {   
            String providerClassName = tokenizer.nextToken();
            loadProvider(resolver, annotationProcessorRegistry, providerClassName);
        }
    }
    
    /**
     * Load a provider class and create an instance.
     * 
     * @param resolver
     * @param providerClassName
     */
    private void loadProvider(ClassResolver resolver, AnnotationProcessorRegistry annotationProcessorRegistry, String providerClassName)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Loading provider " + providerClassName);
        Object provider = null;
        try
        {
            Class providerClass = resolver.findClass(providerClassName);
            provider = providerClass.newInstance();
        }
        catch (Exception e)
        {
            Throwable cause = e;
            if (e instanceof InvocationTargetException)
            {
                cause = ((InvocationTargetException) e).getTargetException();
            }
            throw new ApplicationRuntimeException(AnnotationsMessages.unableToCreateAnnotationsExtensionProvider(providerClassName, e),
                    cause);
        }
        // Check type of provider
        if (!(provider instanceof AnnotationsExtensionProvider)) {
            throw new ApplicationRuntimeException(AnnotationsMessages.annotationsExtensionProviderWrongType(providerClassName, AnnotationsExtensionProvider.class));
        }
        
        // Let the provider register its processors
        ((AnnotationsExtensionProvider) provider).registerAnnotationProcessors(annotationProcessorRegistry);
    }

}