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

package org.apache.hivemind.annotations;

import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.test.HiveMindTestCase;

public class AnnotationTestCase extends HiveMindTestCase
{
    protected TypedRegistry constructRegistry(String ... moduleClassNames)
    {
        AnnotatedRegistryBuilder builder = new AnnotatedRegistryBuilder();
        return builder.constructRegistry(moduleClassNames);
    }

    protected TypedRegistry constructRegistry(Class ... moduleClasses)
    {
        AnnotatedRegistryBuilder builder = new AnnotatedRegistryBuilder();
        return builder.constructRegistry(moduleClasses);
    }

    protected RegistryDefinition constructRegistryDefinition(Class ... moduleClasses)
    {
        RegistryDefinition definition = new RegistryDefinitionImpl();

        for (int i = 0; i < moduleClasses.length; i++)
        {
            AnnotatedModuleReader reader = new AnnotatedModuleReader(definition, getClassResolver(),
                    new DefaultErrorHandler());
            reader.readModule(moduleClasses[i]);
        }

        return definition;
    }

}
