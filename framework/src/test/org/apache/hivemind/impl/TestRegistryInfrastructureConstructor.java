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

package org.apache.hivemind.impl;

import hivemind.test.FrameworkTestCase;

import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.internal.ServicePoint;

/**
 * Tests for {@link RegistryInfrastructureConstructor}.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public class TestRegistryInfrastructureConstructor extends FrameworkTestCase
{
    public void testFound()
    {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler();

    	RegistryDefinition definition = new RegistryDefinitionImpl();
    	
        ModuleDefinitionImpl fooBar = createModuleDefinition("foo.bar");

        ServicePointDefinitionImpl spd = createServicePointDefinition(fooBar, "sp1", Runnable.class);

        fooBar.addServicePoint(spd);

        ModuleDefinitionImpl zipZoop = createModuleDefinition("zip.zoop");

        ConfigurationPointDefinition cpd = createConfigurationPointDefinition(fooBar, "cp1");
        
        zipZoop.addConfigurationPoint(cpd);
        
        definition.addModule(fooBar);
        definition.addModule(zipZoop);

        RegistryInfrastructureConstructor ric = new RegistryInfrastructureConstructor(errorHandler,
                LogFactory.getLog(TestRegistryInfrastructureConstructor.class), null);

        RegistryInfrastructure registryInfrastructure = ric.constructRegistryInfrastructure(definition);

        ServicePoint sp = registryInfrastructure.getServicePoint("foo.bar.sp1", null);
        assertNotNull(sp);

        ConfigurationPoint cp = registryInfrastructure.getConfigurationPoint("zip.zoop.cp1", null);
        assertNotNull(cp);
    }
}