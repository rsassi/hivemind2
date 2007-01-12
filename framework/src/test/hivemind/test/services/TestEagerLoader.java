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
import hivemind.test.services.impl.LoudRunner;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ModuleDefinitionHelper;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.construction.Contribution;
import org.apache.hivemind.definition.construction.ContributionContext;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.service.impl.EagerLoader;

/**
 * Tests for the {@link org.apache.hivemind.service.impl.EagerLoader} service.
 *
 * @author Howard Lewis Ship
 */
public class TestEagerLoader extends FrameworkTestCase
{
    public void testEagerLoaderImpl()
    {
        EagerLoader el = new EagerLoader();
        List l = new ArrayList();

        ServicePoint sp = (ServicePoint) newMock(ServicePoint.class);

        sp.forceServiceInstantiation();
   
		replayControls();

        l.add(sp);

        el.setServicePoints(l);

        el.run();

        verifyControls();
    }

    public void testEagerLoadSingleton() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        createEagerLoadModule(ServiceModel.SINGLETON);

        assertLoggedMessage("Instantiated.");
    }

    public void testEagerLoadPrimitive() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        createEagerLoadModule(ServiceModel.PRIMITIVE);

        assertLoggedMessage("Instantiated.");
    }

    public void testEagerLoadThreaded() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        createEagerLoadModule(ServiceModel.THREADED);

        assertLoggedMessage("Instantiated.");
    }

    public void testEagerLoadPooled() throws Exception
    {
        interceptLogging("hivemind.test.services.Loud");

        createEagerLoadModule(ServiceModel.POOLED);

        assertLoggedMessage("Instantiated.");
    }
    
    /**
     * Creates a Registry with one module, that defines a Service "Loud" added to the EagerLoad 
     * configuration.
     */
    private Registry createEagerLoadModule(final String serviceModel)
    {
        ModuleDefinition module = createModuleDefinition("hivemind.test.services");
        ModuleDefinitionHelper helper = new ModuleDefinitionHelper(module);

        ServicePointDefinition sp1 = helper.addServicePoint("Loud", Runnable.class.getName());
        helper.addSimpleServiceImplementation(sp1, LoudRunner.class.getName(), serviceModel);

        // Add service point "Loud" to EagerLoad configuration point
        helper.addContributionDefinition("hivemind.EagerLoad", new Contribution()
        {
            public void contribute(ContributionContext context)
            {
                List contribution = new ArrayList();
                contribution.add(context.getDefiningModule().getServicePoint("hivemind.test.services.Loud"));
                context.mergeContribution(contribution);
            }
        });

        return buildFrameworkRegistry(module);
    }
 
}
