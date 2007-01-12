package org.apache.hivemind.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Resource;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.SubModuleDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.apache.hivemind.util.ClasspathResource;
import org.apache.hivemind.util.URLResource;

/**
 * Reads a hivemind xml module into a {@link RegistryDefinition}. Thus the defined 
 * services configurations and contributions are added to the registry. 
 * Contains convenience methods for reading xml modules from file system and classpath. 
 * 
 * The module files are parsed by {@link XmlResourceProcessor} and handed over
 * to {@link XmlModuleDescriptorProcessor} for interpretation and addition
 * to the registry definition.
 * 
 * @author Achim Huegen
 */
public class XmlModuleReader
{
    private static final Log LOG = LogFactory.getLog(XmlModuleReader.class);

    private RegistryDefinition _registryDefinition;
    
    /**
     * Parser instance used by all parsing of module descriptors.
     */
    private XmlResourceProcessor _parser;
    
    private XmlModuleDescriptorProcessor _processor;
    
    private ErrorHandler _errorHandler;

    private ClassResolver _classResolver;

    public XmlModuleReader(RegistryDefinition registryDefinition)
    {
        this(registryDefinition, new DefaultClassResolver(), new DefaultErrorHandler());
    }
    
    public XmlModuleReader(RegistryDefinition registryDefinition, ClassResolver classResolver,
            ErrorHandler errorHandler)
    {
        _registryDefinition = registryDefinition;
        _classResolver = classResolver;
        _errorHandler = errorHandler;
        _processor = new XmlModuleDescriptorProcessor(_registryDefinition, _errorHandler);
        _parser = new XmlResourceProcessor(_classResolver, _errorHandler);
    }
    
    /**
     * Reads a module specified by a {@link Resource}
     * 
     * @param moduleResource  the resource specifying the location of the module 
     * @throws ApplicationRuntimeException  if resource wasn't found 
     */
   public void readModule(Resource moduleResource)
    {
        if (moduleResource.getResourceURL() == null)
            throw new ApplicationRuntimeException(XmlImplMessages.unableToFindModuleResource(moduleResource));
        
        processResource(moduleResource);
    }
    
    /**
     * Reads the first module named <code>moduleResourceFileName</code> found in the classpath.
     * Uses the instance of {@link ClassResolver} provided during construction or the {@link DefaultClassResolver}
     * for searching the classpath.
     * 
     * @param moduleFileName  filename of module. For format see {@link ClassLoader#getResource(String)}  
     * @throws ApplicationRuntimeException  if module wasn't found 
     */
    public void readClassPathModule(String moduleFileName)
    {
        readModule(new ClasspathResource(_classResolver, moduleFileName));
    }
    
    /**
     * Reads all modules named <code>moduleResourceFileName</code> found in the classpath.
     * Uses the instance of {@link ClassResolver} provided during construction or the {@link DefaultClassResolver}
     * for searching the classpath.
     * 
     * @param moduleFileName  filename of modules. For format see {@link ClassLoader#getResource(String)} 
     * @throws ApplicationRuntimeException  if no modules were found 
     */
    public void readClassPathModules(String moduleFileName)
    {
        Enumeration foundResources = null;
        ClassLoader loader = _classResolver.getClassLoader();

        try
        {
            foundResources = loader.getResources(moduleFileName);
        }
        catch (IOException ex)
        {
            throw new ApplicationRuntimeException(XmlImplMessages.unableToFindModulesError(_classResolver, ex),
                    ex);
        }
        
        if (!foundResources.hasMoreElements())
            throw new ApplicationRuntimeException(XmlImplMessages.unableToFindModuleResource(new ClasspathResource(_classResolver, moduleFileName)));

        while (foundResources.hasMoreElements())
        {
            URL descriptorURL = (URL) foundResources.nextElement();

            readModule(new URLResource(descriptorURL));
        }
    }
   
    private void processResource(Resource resource)
    {
        try
        {
            ModuleDescriptor md = _parser.processResource(resource);

            _processor.processModuleDescriptor(md);
            
            // After parsing a module, parse any additional modules identified
            // within the module (using the <sub-module> element) recursively.
            processSubModules(md);
        }
        catch (RuntimeException ex)
        {
            _errorHandler.error(LOG, ex.getMessage(), HiveMind.getLocation(ex), ex);
        }
    }

    private void processSubModules(ModuleDescriptor moduleDescriptor)
    {
        List subModules = moduleDescriptor.getSubModules();

        if (subModules == null)
            return;

        for (Iterator i = subModules.iterator(); i.hasNext();)
        {
            SubModuleDescriptor smd = (SubModuleDescriptor) i.next();

            Resource descriptorResource = smd.getDescriptor();

            if (descriptorResource.getResourceURL() == null)
            {
                _errorHandler.error(
                        LOG,
                        XmlImplMessages.subModuleDoesNotExist(descriptorResource),
                        smd.getLocation(),
                        null);
                continue;
            }

            processResource(smd.getDescriptor());
        }
    }
    
}
