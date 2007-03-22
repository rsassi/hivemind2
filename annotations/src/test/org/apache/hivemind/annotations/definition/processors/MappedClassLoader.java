package org.apache.hivemind.annotations.definition.processors;

import java.io.IOException;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MappedClassLoader extends SecureClassLoader
{
    private Map mappedResources = new HashMap();

    public MappedClassLoader(ClassLoader parent)
    {
        super(parent);
    }
    
    public void addResource(String visiblePath, String originalPath)
    {
        mappedResources.put(visiblePath, originalPath);
    }

    /**
     * @see java.lang.ClassLoader#findResource(java.lang.String)
     */
    protected URL findResource(String name)
    {
        return super.findResource(name);
    }

    /**
     * @see java.lang.ClassLoader#findResources(java.lang.String)
     */
    protected Enumeration findResources(String name) throws IOException
    {
        if (mappedResources.containsKey(name)) {
            String originalName = (String) mappedResources.get(name);
            return getParent().getResources(originalName);
        } 
        
        return super.findResources(name);
    }
}
