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

import java.util.HashMap;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.definition.construction.ImplementationConstructionContext;
import org.apache.hivemind.impl.CreateClassServiceConstructor;
import org.apache.hivemind.impl.ModuleImpl;
import org.apache.hivemind.impl.ServicePointImpl;
import org.apache.hivemind.internal.ImplementationConstructionContextImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;

/**
 * Additional tests to fill in minor code coverage gaps.
 * 
 * @author Howard Lewis Ship
 */
public class TestMisc extends FrameworkTestCase
{
    private Module newModule()
    {
        ModuleImpl result = new ModuleImpl();
        result.setClassResolver(getClassResolver());

        return result;
    }

    public void testApplicationRuntimeExceptionGetComponent()
    {
        ApplicationRuntimeException ex = new ApplicationRuntimeException("My Message", this, null,
                null);

        assertSame(this, ex.getComponent());
    }

    public void testApplicationRuntimeExceptionThrowableConstructor()
    {
        RuntimeException re = new RuntimeException("Now it can be told.");
        ApplicationRuntimeException ex = new ApplicationRuntimeException(re);

        assertEquals("Now it can be told.", ex.getMessage());
        assertSame(re, ex.getRootCause());
    }

    public void testCreateClassServiceConstructorAccessors()
    {
        replayControls();

        CreateClassServiceConstructor c = new CreateClassServiceConstructor(newLocation(), 
                "java.util.HashMap");

        c.setInstanceClassName("java.util.HashMap");

        assertEquals("java.util.HashMap", c.getInstanceClassName());

        verifyControls();
    }

    public void testCreateClassServiceConstructorTwice()
    {
        Module m = newModule();

        ServicePoint sp = new ServicePointImpl(m, null);
        
        replayControls();

        CreateClassServiceConstructor c = new CreateClassServiceConstructor(newLocation(), 
                "java.util.HashMap");

        ImplementationConstructionContext context = new ImplementationConstructionContextImpl(m, sp);
        Object o1 = c.constructCoreServiceImplementation(context);
        Object o2 = c.constructCoreServiceImplementation(context);

        assertNotSame(o1, o2);

        assertTrue(o1 instanceof HashMap);
        assertTrue(o2 instanceof HashMap);
    }

    /** @since 1.1 */

    public void testCreateInstanceWithInitializer()
    {
        Module m = newModule();
        
        ServicePoint sp = new ServicePointImpl(m, null);

        CreateClassServiceConstructor c = new CreateClassServiceConstructor(newLocation(), 
                SimpleBean.class.getName() + ",value=HiveMind");

        ImplementationConstructionContext context = new ImplementationConstructionContextImpl(m, sp);
        SimpleBean b = (SimpleBean) c.constructCoreServiceImplementation(context);

        assertEquals("HiveMind", b.getValue());
    }

    public void testCreateClassServiceConstructorFailure()
    {
        Module m = newModule();
        
        ServicePoint sp = new ServicePointImpl(m, null);

        CreateClassServiceConstructor c = new CreateClassServiceConstructor(newLocation(), 
                PrivateBean.class.getName());

        try
        {
            ImplementationConstructionContext context = new ImplementationConstructionContextImpl(m, sp);
            c.constructCoreServiceImplementation(context);
            unreachable();
        }
        catch (Exception ex)
        {
            assertExceptionSubstring(
                    ex,
                    "Unable to instantiate instance of class hivemind.test.PrivateBean");
        }

    }
}