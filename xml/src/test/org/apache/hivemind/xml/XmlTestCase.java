// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.hivemind.xml;

import java.util.Locale;

import hivemind.test.FrameworkTestCase;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Registry;
import org.apache.hivemind.Resource;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.parse.DependencyDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Base class for xml specific tests.
 * 
 * @author Achim Huegen
 */
public abstract class XmlTestCase extends FrameworkTestCase
{
    /**
     * Convienience method for invoking {@link #buildFrameworkRegistry(String[])} with only a single
     * file.
     */
    protected Registry buildFrameworkRegistry(String file) throws Exception
    {
        return buildFrameworkRegistry(new String[]
        { file });
    }

    /**
     * Builds a minimal registry, containing only the specified files, plus the master module
     * descriptor (i.e., those visible on the classpath). Files are resolved using
     * {@link HiveMindTestCase#getResource(String)}.
     */
    protected Registry buildFrameworkRegistry(String[] files) throws Exception
    {
        ClassResolver resolver = getClassResolver();
        
        RegistryBuilder builder = new RegistryBuilder();
        builder.autoDetectModules();

        for (int i = 0; i < files.length; i++)
        {
            Resource resource = getResource(files[i]);

            org.apache.hivemind.impl.XmlModuleReader reader = new org.apache.hivemind.impl.XmlModuleReader(builder.getRegistryDefinition(),
                    resolver, builder.getErrorHandler());
            reader.readModule(resource);
        }

        return builder.constructRegistry(Locale.getDefault());
    }
    
    protected ModuleDescriptor parse(String file)
        throws Exception
    {
        Resource location = getResource(file);
        DefaultErrorHandler eh = new DefaultErrorHandler();

        XmlResourceProcessor p = new XmlResourceProcessor(_resolver, eh);

        ModuleDescriptor result = p.processResource(location);

        return result;
    }

    /**
     * Convenience method for creating a
     * {@link org.apache.hivemind.parse.DependencyDescriptor}.
     */
    protected DependencyDescriptor createDependencyDescriptor(String moduleId, String version)
    {
        DependencyDescriptor result = new DependencyDescriptor();

        result.setModuleId(moduleId);
        result.setVersion(version);
        result.setLocation(newLocation());

        return result;
    }

}
