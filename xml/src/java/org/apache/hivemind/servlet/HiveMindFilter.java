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

package org.apache.hivemind.servlet;

import javax.servlet.ServletContext;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.XmlModuleReader;
import org.apache.hivemind.util.ContextResource;

/**
 * Specializations of {@link AutoloadingHiveMindFilter} that additionally loads a xml
 * module from "/WEB-INF/hivemodule.xml".
 * Actually should be called XmlHiveMindFilter but is not for sake of 
 * backward compatibility.
 * 
 * @author Achim Huegen
 */
public class HiveMindFilter extends AutoloadingHiveMindFilter
{
    static final String HIVE_MODULE_XML = "/WEB-INF/hivemodule.xml";

    protected void addWebInfDescriptor(ServletContext context, ClassResolver resolver,
            RegistryBuilder builder)
    {
        ContextResource r = new ContextResource(context, HIVE_MODULE_XML);

        if (r.getResourceURL() != null)
        {
            XmlModuleReader reader = new XmlModuleReader(builder.getRegistryDefinition(),
                    resolver, builder.getErrorHandler());
            reader.readModule(r);
        }
    }

}