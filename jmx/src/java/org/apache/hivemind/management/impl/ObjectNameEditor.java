// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.management.impl;

import java.beans.PropertyEditorSupport;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * PropertyEditor for JMX ObjectNames. Converts strings to ObjectNames
 * 
 * @author Achim Huegen
 * @since 1.1
 */
public class ObjectNameEditor extends PropertyEditorSupport
{
    public void setAsText(String text) throws java.lang.IllegalArgumentException
    {
        try
        {
            ObjectName objectName = new ObjectName(text);
            setValue(objectName);
        }
        catch (MalformedObjectNameException e)
        {
            throw new java.lang.IllegalArgumentException(e.getMessage());
        }
    }

    public String getAsText()
    {
        return getValue().toString();
    }
}