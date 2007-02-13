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
     * {@link XmlModuleReader#readClassPathModules(String)} constructor.
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
