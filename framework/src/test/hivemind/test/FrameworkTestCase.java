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

package hivemind.test;

import java.net.URL;
import java.util.ArrayList;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.ImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.impl.CreateClassServiceConstructor;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Base class for framework tests.
 * 
 * @author Howard Lewis Ship
 */
public abstract class FrameworkTestCase extends HiveMindTestCase
{

    protected ClassResolver _resolver = new DefaultClassResolver();

    /** Returns a filesystem path to a resource within the classpath. */
    protected String getFrameworkPath(String path)
    {
        URL url = getClass().getResource(path);

        String protocol = url.getProtocol();

        if (!protocol.equals("file"))
            throw new RuntimeException("Classpath resource " + path
                    + " is not stored on the filesystem. It is available as " + url);

        return url.getPath();
    }

    protected void interceptLogging()
    {
        interceptLogging("org.apache.hivemind");
    }

    /**
     * Convenience method for creating a {@link ModuleDefinitionImpl}.
     */
    protected ModuleDefinitionImpl createModuleDefinition(String moduleId)
    {
        ModuleDefinitionImpl result = new ModuleDefinitionImpl(moduleId, newLocation(), 
                getClassResolver(), "");

        return result;
    }

    /**
     * Convenience method for creating a {@link ServicePointDefinitionImpl}.
     */
    protected ServicePointDefinitionImpl createServicePointDefinition(ModuleDefinition module, String pointId, Class serviceInterface)
    {
        ServicePointDefinitionImpl result = new ServicePointDefinitionImpl(module, pointId,
                newLocation(), Visibility.PUBLIC, serviceInterface.getName());

        return result;
    }
    
    /**
     * Convenience method for creating a {@link ImplementationDefinition}.
     */
    protected ImplementationDefinition createServiceImplementationDefinition(ModuleDefinition module, Class serviceImplementationClass)
    {
        ImplementationDefinition result = new ImplementationDefinitionImpl(module, newLocation(),
                new CreateClassServiceConstructor(newLocation(), serviceImplementationClass.getName()), 
                "singleton", true);

        return result;
    }

    /**
     * Convenience method for creating a {@link ConfigurationPointDefinition}.
     */
    protected ConfigurationPointDefinition createConfigurationPointDefinition(ModuleDefinition module, String pointId)
    {
        ConfigurationPointDefinitionImpl result = new ConfigurationPointDefinitionImpl(module, pointId,
                newLocation(), Visibility.PUBLIC, ArrayList.class.getName(),
                Occurances.UNBOUNDED);

        return result;
    }
}
