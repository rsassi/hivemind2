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

package hivemind.test.config;

import java.util.List;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Registry;
import org.apache.hivemind.xml.XmlTestCase;

/** Test for checking the uniqueness of element attributes.
 */
public class TestUniqueElementAttribute extends XmlTestCase
{
    /**
     * Test with unique="true"
     */
    public void testUniquenessViolated() throws Exception
    {
        Registry r =
            buildFrameworkRegistry(
                new String[] { "UniqueAttributeDefinition.xml", "UniqueAttributeBroken.xml" });

        Map elements = (Map) r.getConfiguration("hivemind.test.parse.MyExtensionPoint");

        try
        {
            // This constructs the actual backing list
            elements.size();

            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(
                ex,
                "already contained in map");
        }

    }

    /**
     * Test with unique="false"
     */
    public void testFalseUniqueAttributeConstraint() throws Exception
    {
        Registry r =
            buildFrameworkRegistry(
                new String[] { "UniqueAttributeDefinition.xml", "UniqueAttributeBroken.xml" });

        List elements = (List) r.getConfiguration("hivemind.test.parse.MyExtensionPoint2");

        assertEquals(3, elements.size());
    }

    /**
     * Test with unique unspecified (defaults to false).
     */
    public void testNoUniqueAttributeConstraint() throws Exception
    {
        Registry r =
            buildFrameworkRegistry(
                new String[] { "UniqueAttributeDefinition.xml", "UniqueAttributeBroken.xml" });

        List elements = (List) r.getConfiguration("hivemind.test.parse.MyExtensionPoint3");

        assertEquals(3, elements.size());

    }

}
