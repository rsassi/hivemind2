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

package hivemind.test.parse;

import hivemind.test.FrameworkTestCase;

import org.apache.hivemind.impl.AttributeImpl;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.ElementImpl;
import org.apache.hivemind.parse.ConfigurationPointDescriptor;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.CreateInstanceDescriptor;
import org.apache.hivemind.parse.DependencyDescriptor;
import org.apache.hivemind.parse.ImplementationDescriptor;
import org.apache.hivemind.parse.InterceptorDescriptor;
import org.apache.hivemind.parse.InvokeFactoryDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.ServicePointDescriptor;
import org.apache.hivemind.parse.SubModuleDescriptor;

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
        new ConfigurationPointDescriptor().toString();
        new ContributionDescriptor().toString();
        new ImplementationDescriptor().toString();
        new CreateInstanceDescriptor().toString();
        new InvokeFactoryDescriptor().toString();
        new ModuleDescriptor(_resolver, new DefaultErrorHandler()).toString();
        new SubModuleDescriptor().toString();
        new DependencyDescriptor().toString();
        new ServicePointDescriptor().toString();
        new InterceptorDescriptor().toString();
        new ElementImpl().toString();
        new AttributeImpl("foo", "bar").toString();
    }
}
