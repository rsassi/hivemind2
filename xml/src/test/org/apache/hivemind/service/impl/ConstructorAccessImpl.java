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

package org.apache.hivemind.service.impl;


import java.util.Map;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Messages;
import org.apache.hivemind.service.ConstructorAccess;

/**
 * Used to test constructor parameter passing of
 * {@link org.apache.hivemind.service.impl.BuilderFactory}.
 */
public class ConstructorAccessImpl implements ConstructorAccess
{

    private String actualMessage;

    private String expectedMessage;

    public ConstructorAccessImpl()
    {
        actualMessage = "()";
    }

    public ConstructorAccessImpl(long l)
    {
        actualMessage = "(long)";
    }

    public ConstructorAccessImpl(String string)
    {
        actualMessage = "(String)";
    }

    public ConstructorAccessImpl(ConstructorAccess service)
    {
        actualMessage = "(ConstructorAccess)";
    }

    public ConstructorAccessImpl(ConstructorAccess service, String s)
    {
        actualMessage = "(ConstructorAccess, String)";
    }

    public ConstructorAccessImpl(Map configurations)
    {
        actualMessage = "(Map)";
    }

    public ConstructorAccessImpl(Map configurations, long l)
    {
        actualMessage = "(Map, long)";
    }

    public ConstructorAccessImpl(Log log, Messages messages)
    {
        actualMessage = "(Log, Messages)";
    }

    public ConstructorAccessImpl(long l, ConstructorAccess nullObject)
    {
        actualMessage = "(long, " + nullObject + ")";
    }
    
    public void setExpectedConstructorMessage(String expectedMessage)
    {
        this.expectedMessage = expectedMessage;
    }

    public void verify() throws AssertionFailedError
    {
        Assert.assertEquals(expectedMessage, actualMessage);
    }

}