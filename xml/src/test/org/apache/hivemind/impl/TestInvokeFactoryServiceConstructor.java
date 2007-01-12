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

import hivemind.test.services.SimpleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.construction.ImplementationConstructionContext;
import org.apache.hivemind.internal.ImplementationConstructionContextImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.xml.XmlTestCase;
import org.apache.hivemind.xml.definition.impl.XmlServicePointDefinitionImpl;
import org.easymock.MockControl;

/**
 * Tests some error conditions related to invoking a service factory.
 * 
 * @author Howard Lewis Ship
 */
public class TestInvokeFactoryServiceConstructor extends XmlTestCase
{
    public void testWrongNumberOfParameters()
    {
        MockControl moduleControl = newControl(Module.class);
        Module module = (Module) moduleControl.getMock();

        MockControl factoryPointControl = newControl(ServicePoint.class);
        ServicePoint factoryPoint = (ServicePoint) factoryPointControl.getMock();

        MockControl factoryControl = newControl(ServiceImplementationFactory.class);
        ServiceImplementationFactory factory = (ServiceImplementationFactory) factoryControl
                .getMock();

        MockControl pointControl = newControl(ServicePoint.class);
        ServicePoint point = (ServicePoint) pointControl.getMock();
        
        SchemaImpl schema = new SchemaImpl("module");
        schema.setRootElementClassName(ArrayList.class.getName());
        
        ModuleDefinition md = createModuleDefinition("test");
        XmlServicePointDefinitionImpl xmlSpd = new XmlServicePointDefinitionImpl(md);
        xmlSpd.setParametersCount(Occurances.REQUIRED);
        xmlSpd.setParametersSchema(schema);
        
        Location location = newLocation();
        InvokeFactoryServiceConstructor c = new InvokeFactoryServiceConstructor(location, "module");

        ErrorLog log = (ErrorLog) newMock(ErrorLog.class);

        // Training !
        
        point.getErrorLog();
        pointControl.setReturnValue(log);

        module.getServicePoint("foo.bar.Baz");
        moduleControl.setReturnValue(factoryPoint);
        
        module.resolveType(ArrayList.class.getName());
        moduleControl.setReturnValue(ArrayList.class);

        factoryPoint.getService(ServiceImplementationFactory.class);
        factoryPointControl.setReturnValue(factory);

        factoryPoint.getServicePointDefinition();
        factoryPointControl.setReturnValue(xmlSpd);
        
        factoryPoint.getModule();
        factoryPointControl.setReturnValue(module);

        String message = XmlImplMessages
                .wrongNumberOfParameters("foo.bar.Baz", 0, Occurances.REQUIRED);

        log.error(message, location, null);

        factory.createCoreServiceImplementation(new ServiceImplementationFactoryParametersImpl(
                point, module, Collections.EMPTY_LIST));
        factoryControl.setReturnValue("THE SERVICE");

        replayControls();

        c.setFactoryServiceId("foo.bar.Baz");
        c.setParameters(Collections.EMPTY_LIST);

        ImplementationConstructionContext context = new ImplementationConstructionContextImpl(module, point);
        assertEquals("THE SERVICE", c.constructCoreServiceImplementation(context));

        verifyControls();
    }

    public void testInvokeFactoryServiceConstructorAccessors()
    {
        String moduleId = "module";
        List p = new ArrayList();
        InvokeFactoryServiceConstructor c = new InvokeFactoryServiceConstructor(newLocation(), moduleId);

        c.setParameters(p);

        assertSame(p, c.getParameters());
    }
    
    public void testComplex() throws Exception
    {
        Registry r = buildFrameworkRegistry("ComplexModule.xml");

        SimpleService s =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);
        CountFactory.reset();

        assertEquals(
            "<SingletonProxy for hivemind.test.services.Simple(hivemind.test.services.SimpleService)>",
            s.toString());

        assertEquals(7, s.add(4, 3));

        assertEquals(1, CountFactory.getCount());

        assertEquals(19, s.add(11, 8));

        assertEquals(2, CountFactory.getCount());
    }
    
}