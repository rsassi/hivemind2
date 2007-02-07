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

package hivemind.test.services;

import hivemind.test.FrameworkTestCase;
import hivemind.test.services.impl.StartupRunnableFixtureImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ContributionContext;
import org.apache.hivemind.definition.ModuleDefinitionHelper;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.impl.StartupImpl;
import org.apache.hivemind.internal.ServiceModel;

/**
 * Tests Registry startup.
 *
 * @author Howard Lewis Ship
 */
public class TestStartup extends FrameworkTestCase
{

    public void testStartupImpl()
    {
        StartupRunnableFixture fixture = new StartupRunnableFixtureImpl();

        List l = new ArrayList();
        l.add(fixture);

        StartupImpl s = new StartupImpl();

        s.setRunnables(l);
        s.run();

        assertEquals(true, fixture.getDidRun());
    }

    public void testStartupContribution() throws Exception
    {
        Registry r = createRegistry();

        StartupRunnableFixture fixture =
            (StartupRunnableFixture) r.getService(
                "hivemind.test.services.StartupRunnableFixture",
                StartupRunnableFixture.class);

        assertEquals(true, fixture.getDidRun());
    }
    
    /**
     * Creates a Registry with one module, that defines a Service "Loud" added to the EagerLoad 
     * configuration.
     */
    private Registry createRegistry()
    {
        ModuleDefinitionImpl module = createModuleDefinition("hivemind.test.services");
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(module);

        ServicePointDefinition sp1 = helper.addServicePoint("StartupRunnableFixture", StartupRunnableFixture.class.getName());
        helper.addSimpleServiceImplementation(sp1, StartupRunnableFixtureImpl.class.getName(), ServiceModel.SINGLETON);

        // Add service point "StartupRunnableFixture" to Startup configuration point
        helper.addContributionDefinition("hivemind.Startup", new Contribution()
        {
            public void contribute(ContributionContext context)
            {
                List contribution = new ArrayList(); 
                contribution.add(context.getService(StartupRunnableFixture.class));
                context.mergeContribution(contribution);
            }
        });

        return buildFrameworkRegistry(module);
    }

}
