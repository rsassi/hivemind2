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

package org.apache.hivemind.util;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.test.HiveMindTestCase;

/**
 * Tests for {@link org.apache.hivemind.util.ConstructorUtils}. Alas (for the moment), just fills a
 * few gaps ... most of the code is tested indirectly by code that uses it.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestConstructorUtils extends HiveMindTestCase
{
    public static class Troublemaker
    {
        public Troublemaker(ApplicationRuntimeException ex)
        {
            throw ex;
        }
    }

    public void testInvocationTargetException()
    {
        Throwable inner = new ApplicationRuntimeException("Inner exception.");

        try
        {
            ConstructorUtils.invokeConstructor(Troublemaker.class, new Object[]
            { inner });

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            String message = UtilMessages.invokeFailed(
                    Troublemaker.class.getConstructors()[0],
                    inner);

            assertEquals(message, ex.getMessage());
            assertSame(inner, ex.getCause());
        }
    }
}