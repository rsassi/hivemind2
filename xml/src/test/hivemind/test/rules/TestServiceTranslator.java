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

package hivemind.test.rules;

import hivemind.test.services.Constructed;
import hivemind.test.services.SimpleService;

import org.apache.hivemind.Registry;
import org.apache.hivemind.service.ThreadLocalStorage;
import org.apache.hivemind.xml.XmlTestCase;

/**
 * Tests for {@link org.apache.hivemind.schema.rules.ServiceTranslator}
 *
 * @author Howard Lewis Ship
 */
public class TestServiceTranslator extends XmlTestCase
{
    public void testServiceTranslator() throws Exception
    {
        Registry r = buildFrameworkRegistry("ServiceTranslator.xml");

        SimpleService ss =
            (SimpleService) r.getService("hivemind.test.services.Simple", SimpleService.class);

        assertNotNull(ss);

        ThreadLocalStorage tls =
            (ThreadLocalStorage) r.getService(
                "hivemind.ThreadLocalStorage",
                ThreadLocalStorage.class);

        assertNotNull(tls);

        Constructed c =
            (Constructed) r.getService("hivemind.test.services.Constructed", Constructed.class);

        assertNotNull(c);

        assertSame(ss, c.getSimpleService());
        assertSame(tls, c.getThreadLocal());
    }
}
