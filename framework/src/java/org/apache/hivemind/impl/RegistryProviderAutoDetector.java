package org.apache.hivemind.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.util.URLResource;

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
    public static final String MANIFEST = "META-INF/MANIFEST.MF";
    public static final String HIVEMIND_SECTION_NAME = "hivemind";
    public static final String PROVIDER_ATTRIBUTE_NAME = "hivemind-providers";
    
    private List _providers = new ArrayList();
    
    public RegistryProviderAutoDetector(ClassResolver resolver)
    {
        processManifestFiles(resolver);
    }
    
    public List getProviders()
    {
        return _providers;
    }
    
    /**
     * Process all manifest files found in the classpath
     * @param resolver  the ClassResolver to use for the search
     */
    private void processManifestFiles(ClassResolver resolver)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Processing manifest files visible to " + resolver);

        ClassLoader loader = resolver.getClassLoader();
        Enumeration e = null;

        try
        {
            e = loader.getResources(MANIFEST);
        }
        catch (IOException ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToFindProviders(resolver, ex),
                    ex);
        }

        while (e.hasMoreElements())
        {
            URL descriptorURL = (URL) e.nextElement();

            processManifestFile(resolver, new URLResource(descriptorURL));
        }

    }


    /**
     * Process a single manifest file.
     * 
     * @param resolver
     * @param resource  pointer to the manifest file
     */
    private void processManifestFile(ClassResolver resolver, URLResource resource)
    {
        URL url = resource.getResourceURL();
        Manifest manifest;
        try
        {
            manifest = new Manifest(url.openStream());
        }
        catch (IOException e)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToReadManifest(url, e),
                    e);
        }
        // Search for an entry that defines a provider class
        Attributes attributes = manifest.getMainAttributes();
        if (attributes != null) {
            String providers = attributes.getValue(PROVIDER_ATTRIBUTE_NAME);
            if (providers != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Found providers '" + providers + "' defined in manifest file '" + url.toString() + "'");
                }
                handleProviderAttribute(resolver, providers);
            }
        }
    }

    /**
     * Parse the provider list in an attribute and load all classes.
     */
    private void handleProviderAttribute(ClassResolver resolver, String providers)
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
