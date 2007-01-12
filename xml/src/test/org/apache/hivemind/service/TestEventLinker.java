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

import hivemind.test.services.ZapEventConsumer;
import hivemind.test.services.ZapEventProducer;

import org.apache.hivemind.Registry;
import org.apache.hivemind.xml.XmlTestCase;

/**
 * Tests for the {@link org.apache.hivemind.service.impl.EventLinkerImpl}class, used by the
 * {@link org.apache.hivemind.service.impl.BuilderFactory}.
 * 
 * @author Howard Lewis Ship
 */
public class TestEventLinker extends XmlTestCase
{
 
    public void testInsideBuilderFactory() throws Exception
    {
        Registry r = buildFrameworkRegistry("EventRegister.xml");

        ZapEventProducer p = (ZapEventProducer) r.getService(
                "hivemind.test.services.ZapEventProducer",
                ZapEventProducer.class);

        ZapEventConsumer c = (ZapEventConsumer) r.getService(
                "hivemind.test.services.ZapEventConsumer",
                ZapEventConsumer.class);

        assertEquals(false, c.getDidZapWiggle());

        p.fireZapDidWiggle(null);

        assertEquals(true, c.getDidZapWiggle());
    }

}