package org.apache.hivemind.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.util.IOUtils;
import org.apache.hivemind.util.URLResource;

/**
 * Reads Manifest-Files (META-INF/MANIFEST.MF) and searches them for certain attributes.
 * The {@link Manifest} instances are cached between two calls for better performance.
 * 
 * @author Achim Huegen
 */
public class ManifestReader
{
    private static final Log LOG = LogFactory.getLog(ManifestReader.class);
    private static final String MANIFEST = "META-INF/MANIFEST.MF";
    public static final String HIVEMIND_SECTION_NAME = "hivemind";
    
    /**
     * Caches loaded manifests keyed by the ClassLoader
     */
    private static Map MANIFEST_CACHE = new HashMap();

    /**
     * Searches all manifest files found in the classpath by a ClassResolver
     * for a attribute whose name is specified by <code>attributeName</code>.
     * @param resolver
     * @param attributeName 
     * @return all found attribute values
     */
    public static String[] getAttributeValues(ClassResolver resolver, String attributeName)
    {
        Manifest[] manifests = getManifests(resolver);
        List results = new ArrayList();
        for (int i = 0; i < manifests.length; i++)
        {
            Manifest manifest = manifests[i];
            String value = getAttributeValue(manifest, attributeName);
            if (value != null) {
                results.add(value);
            }
        }
        return (String[]) results.toArray(new String[results.size()]);
    }
    
    /**
     * Searches a manifest for a attribute of the given name.
     * @return  the attribute value or null if the attribute is not defined
     */
    private static String getAttributeValue(Manifest manifest, String attributeName)
    {
        String value = null;
        // Search for an entry 
        Attributes attributes = manifest.getMainAttributes();
        if (attributes != null) {
            value = attributes.getValue(attributeName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Found attribute '" + attributeName + "' with value '" + value 
                        + "' defined in manifest file'");
            }
        }
        return value;
    }
    
    private static Manifest[] getManifests(ClassResolver resolver)
    {
        // Try to load manifests from static cache
        Manifest[] manifests = (Manifest[]) MANIFEST_CACHE.get(resolver.getClassLoader());
        if (manifests == null) {
            manifests = loadManifestFiles(resolver);
            MANIFEST_CACHE.put(resolver.getClassLoader(), manifests);
        }
        return manifests;
    }
    
    
    /**
     * Loads all manifest files found in the classpath
     * @param resolver  the ClassResolver to use for the search
     * @return an empty array if none have been found.
     */
    private static Manifest[] loadManifestFiles(ClassResolver resolver)
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
            throw new ApplicationRuntimeException(ImplMessages.unableToFindManifests(resolver, ex),
                    ex);
        }

        List result = new ArrayList();
        while (e.hasMoreElements())
        {
            URL descriptorURL = (URL) e.nextElement();

            Manifest manifest = loadManifestFile(resolver, new URLResource(descriptorURL));
            result.add(manifest);
        }
        return (Manifest[]) result.toArray(new Manifest[result.size()]);
    }

    /**
     * Loads a single manifest file.
     * 
     * @param resolver
     * @param resource  pointer to the manifest file
     */
    private static Manifest loadManifestFile(ClassResolver resolver, URLResource resource)
    {
        URL url = resource.getResourceURL();
        InputStream manifestStream = null;
        Manifest manifest;
        try
        {
            manifestStream = IOUtils.openStreamWithoutCaching(url);
            manifest = new Manifest(manifestStream);
        }
        catch (IOException e)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToReadManifest(url, e),
                    e);
        }
        finally
        {
            IOUtils.close(manifestStream);
        }
        return manifest;
    }


}
