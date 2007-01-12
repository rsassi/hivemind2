package org.apache.hivemind.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.definition.RegistryDefinition;

/**
 * Implementation of {@link RegistryProvider} that loads all xml modules
 * defined in <code>META-INF/hivemodule.xml</code> files. 
 */
public class HivemoduleProvider implements RegistryProvider
{
    private static final Log LOG = LogFactory.getLog(HivemoduleProvider.class);

    /**
     * The default path, within a JAR or the classpath, to the XML HiveMind module deployment
     * descriptor: <code>META-INF/hivemodule.xml</code>. Use this constant with the
     * {@link #XmlRegistryProvider(ClassResolver, String)} constructor.
     */
    public static final String HIVE_MODULE_XML = "META-INF/hivemodule.xml";

    private ClassResolver _classResolver;
    
    private String _resourcePath;

    public HivemoduleProvider()
    {
        this(new DefaultClassResolver(), HIVE_MODULE_XML);
    }

    public HivemoduleProvider(ClassResolver classResolver, String resourcePath)
    {
        _classResolver = classResolver;
        _resourcePath = resourcePath;
    }

    public void process(RegistryDefinition registryDefinition, ErrorHandler errorHandler)
    {
        XmlModuleReader xmlModuleReader = new XmlModuleReader(registryDefinition, _classResolver,
                errorHandler);
        if (LOG.isDebugEnabled())
            LOG.debug("Processing xml modules visible to " + _classResolver);

        xmlModuleReader.readClassPathModules(_resourcePath);
    }

}
