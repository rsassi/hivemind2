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

package org.apache.hivemind.xml;

import org.apache.hivemind.Registry;
import org.apache.hivemind.service.impl.MockRunnable;

/**
 * Tests the overriding of a service implementation.
 * 
 * @author Howard Lewis Ship
 */
public class TestServiceOverride extends XmlTestCase
{
    public void testNestedSubModule() throws Exception
    {
        Registry r = buildFrameworkRegistry("OverrideService.xml");

        Runnable service = (Runnable) r.getService(
                "hivemind.test.override.TestService",
                Runnable.class);

        assertTrue(service instanceof MockRunnable);

    }

}