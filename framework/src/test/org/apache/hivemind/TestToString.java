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

package org.apache.hivemind;

import hivemind.test.FrameworkTestCase;

import java.util.Locale;

import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.ImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.impl.InterceptorStackImpl;
import org.apache.hivemind.impl.ModuleImpl;
import org.apache.hivemind.impl.RegistryInfrastructureImpl;
import org.apache.hivemind.impl.ServicePointImpl;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.internal.ServicePoint;
import org.easymock.MockControl;

/**
 * A cheat, for code-coverage reasons.  We check that all the classes have a toString()
 * method.
 *
 * @author Howard Lewis Ship
 */

public class TestToString extends FrameworkTestCase
{

    public void testToString()
    {
        MockControl control = MockControl.createControl(ServicePoint.class);
        ServicePoint mockServicePoint = (ServicePoint) control.getMock();

        ModuleImpl module = new ModuleImpl();
        module.setModuleId("module");
        module.toString();
        new RegistryInfrastructureImpl(null, Locale.ENGLISH).toString();
        new InterceptorStackImpl(null, mockServicePoint, null).toString();

        ModuleDefinition md = new ModuleDefinitionImpl("module", null, null, null);
        ServicePointDefinitionImpl spd = new ServicePointDefinitionImpl(md, "service", null,
                Visibility.PUBLIC, Runnable.class.getName());
        ImplementationDefinition sid = new ImplementationDefinitionImpl(md, 
                null, null, ServiceModel.PRIMITIVE, true);
        spd.addImplementation(sid);
        new ServicePointImpl(module, spd).toString();
    }
}
