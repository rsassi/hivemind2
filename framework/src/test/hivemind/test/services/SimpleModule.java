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

package hivemind.test.services;

import hivemind.test.services.impl.SimpleServiceImpl;

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionHelper;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.util.ClasspathResource;

/**
 * Defines a module with one service.
 */
public class SimpleModule extends ModuleDefinitionImpl
{
    public SimpleModule()
    {
        super("hivemind.test.services", createLocation(), new DefaultClassResolver(), null);
        
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(this);
        ServicePointDefinition sp = helper.addServicePoint("Simple", SimpleService.class.getName());
        helper.addSimpleServiceImplementation(sp, SimpleServiceImpl.class.getName(), ServiceModel.SINGLETON);
    }
    
    private static Location createLocation()
    {
        String path = "/" + SimpleModule.class.getName().replace('.', '/');

        Resource r = new ClasspathResource(new DefaultClassResolver(), path);

        return new LocationImpl(r, 1);
    }
    
}
