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

package org.apache.hivemind.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;

/**
 * Searches for {@link org.apache.hivemind.impl.RegistryProvider} implementations
 * that are defined in manifest files. Creates an instance of each 
 * implementation and returns them in the getProviders method.
 * The manifest file must contain a global attribute <code>hivemind-providers</code>
 * which contains a comma separated list of classes that implement 
 * the {@link org.apache.hivemind.impl.RegistryProvider} interface. 
 * 
 * @author Achim Huegen
 */
public class RegistryProviderAutoDetector
{
    private static final Log LOG = LogFactory.getLog(RegistryProviderAutoDetector.class);
    public static final String PROVIDER_ATTRIBUTE_NAME = "hivemind-providers";
    
    private List _providers = new ArrayList();
    
    public RegistryProviderAutoDetector(ClassResolver resolver)
    {
        String[] providerValues = ManifestReader.getAttributeValues(resolver, PROVIDER_ATTRIBUTE_NAME);
        for (int i = 0; i < providerValues.length; i++)
        {
            String providerValue = providerValues[i];
            handleProviderValue(resolver, providerValue);
        }
    }
    
    /**
     * @return  list with instances of {@link RegistryProvider}
     */
    public List getProviders()
    {
        return _providers;
    }

    /**
     * Parse the provider list in an attribute and load all classes.
     */
    private void handleProviderValue(ClassResolver resolver, String providers)
    {
        StringTokenizer tokenizer = new StringTokenizer(providers, ",");
        while (tokenizer.hasMoreTokens())
        {   
            String providerClassName = tokenizer.nextToken();
            loadProvider(resolver, providerClassName);
        }
    }
    
    /**
     * Load a provider class and create an instance.
     * 
     * @param resolver
     * @param providerClassName
     */
    private void loadProvider(ClassResolver resolver, String providerClassName)
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
            Exception cause = e;
            if (e instanceof InvocationTargetException)
            {
                cause = (InvocationTargetException) e;
            }
            throw new ApplicationRuntimeException(ImplMessages.unableToCreateProvider(providerClassName, e),
                    cause);
        }
        // Check type of provider
        if (!(provider instanceof RegistryProvider)) {
            throw new ApplicationRuntimeException(ImplMessages.providerWrongType(providerClassName, RegistryProvider.class));
        }
        
        _providers.add(provider);
    }

}
