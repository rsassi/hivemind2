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

import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;

/**
 * Tests the module dependencies (specified using &lt;dependency.&gt;).
 * 
 * @author Knut Wannheden
 */
public class TestDependency extends FrameworkTestCase
{

    public void testMissingRequiredModule() throws Exception
    {
        ModuleDefinitionImpl dependingModule = createModuleDefinition(
                "dependency.declaring.module");

        dependingModule.addDependency("required.module");

        interceptLogging();

        buildFrameworkRegistry(new ModuleDefinition[] {dependingModule});

        assertLoggedMessage("Required module required.module does not exist.");
    }

    public void testDependency() throws Exception
    {
        ModuleDefinitionImpl dependingModule = createModuleDefinition(
                "dependency.declaring.module");

        ModuleDefinition requiredModule = createModuleDefinition("required.module");
        
        dependingModule.addDependency(requiredModule.getId());

        buildFrameworkRegistry(new ModuleDefinition[] {dependingModule, requiredModule});
    }

}