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

package org.apache.examples.panorama.startup.impl;

import org.apache.examples.panorama.startup.impl.ExecuteStatic;

import junit.framework.TestCase;

/**
 * Tests {@link TestExecuteStatic}.
 *
 * @author Howard Lewis Ship
 */
public class TestExecuteStatic extends TestCase
{
    private static boolean _staticMethodInvoked;

    public static void staticMethod()
    {
        _staticMethodInvoked = true;
    }

    protected void tearDown() throws Exception
    {
    	super.tearDown();
    	
        _staticMethodInvoked = false;
    }

    public void testInvoke() throws Exception
    {
        ExecuteStatic e = new ExecuteStatic();

        e.setMethodName("staticMethod");
        e.setTargetClass(TestExecuteStatic.class);

        e.execute();

        assertEquals(true, _staticMethodInvoked);
    }
}
