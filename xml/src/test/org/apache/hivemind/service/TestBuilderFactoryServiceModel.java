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

package org.apache.hivemind.service;

import hivemind.test.services.StringHolder;

import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Registry;
import org.apache.hivemind.service.impl.BuilderFactory;
import org.apache.hivemind.xml.XmlTestCase;

/**
 * Tests {@link org.apache.hivemind.impl.servicemodel.PooledServiceModel} integrated with {@link BuilderFactory}. 
 *
 * @author Howard Lewis Ship
 */
public class TestBuilderFactoryServiceModel extends XmlTestCase
{

    /**
     * Tests basic interaction of pooling with thread cleanup.
     */
    public void testManaged() throws Exception
    {
        Registry r = buildFrameworkRegistry("Pooled.xml");

        StringHolder s =
            (StringHolder) r.getService("hivemind.test.services.Managed", StringHolder.class);

        interceptLogging("hivemind.test.services.Managed");

        assertNull(s.getValue());

        assertLoggedMessage("activateService()");

        s.setValue("funky monkey");
        assertEquals("funky monkey", s.getValue());

        ThreadEventNotifier n =
            (ThreadEventNotifier) r.getService(
                HiveMind.THREAD_EVENT_NOTIFIER_SERVICE,
                ThreadEventNotifier.class);

        n.fireThreadCleanup();

        assertLoggedMessage("passivateService()");

        assertNull(s.getValue());
    }

}
