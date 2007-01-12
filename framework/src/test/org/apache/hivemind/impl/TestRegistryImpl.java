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

import hivemind.test.services.SimpleModule;

import org.apache.hivemind.Messages;
import org.apache.hivemind.Registry;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * @author James Carman
 * @since 1.1
 */
public class TestRegistryImpl extends HiveMindTestCase
{
    public void testGetModuleMessages() throws Exception
    {
        final Registry reg = buildFrameworkRegistry(new SimpleModule());
        final Messages msgs = reg.getModuleMessages( "hivemind.test.services" );
        assertEquals( "Test Message", msgs.getMessage( "test.message" ) );
    }
}
