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

package org.apache.hivemind.schema.rules;

import java.lang.reflect.Method;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Element;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Rule used to connect a child object to its parent that is a map,
 * by invoking a method on the parent, 
 * passing the child and a key that is taken from a childs attribute.
 * 
 * @author Achim Huegen
 */
public class AddToMapRule extends BaseRule
{
    private String _methodName;

    private String _keyAttribute;
    
    public AddToMapRule(String keyAttribute)
    {
        this("put", keyAttribute);
    }

    public AddToMapRule(String methodName, String keyAttribute)
    {
        _methodName = methodName;
        _keyAttribute = keyAttribute;
    }

    /**
     * Invokes the named method on the parent object (using reflection).
     */
    public void begin(SchemaProcessor processor, Element element)
    {
        Object parent = processor.peek(1);
        Object child = processor.peek();

        // Get all parameters from the stack in the order they where pushed on it
        Object[] parameters = new Object[2];
        parameters[0] = getKeyValue(child);
        parameters[1] = child;
            
        Class[] parameterTypes = new Class[2];
        parameterTypes[0] = getKeyType(child);
        parameterTypes[1] = child.getClass();
        
        try
        {
            Method m = findMethod(parent, _methodName, parameterTypes);

            m.invoke(parent, parameters);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(RulesMessages.errorInvokingMethod(
                    _methodName,
                    parent,
                    getLocation(),
                    ex), getLocation(), ex);
        }
    }

    public String getMethodName()
    {
        return _methodName;
    }

    public void setMethodName(String string)
    {
        _methodName = string;
    }

    /**
     * Searches for the *first* public method the has the right name, and takes a single parameter
     * that is compatible with the parameter type.
     * 
     * @throws NoSuchMethodException
     *             if a method can't be found
     */
    private Method findMethod(Object target, String name, Class[] parameterTypes)
            throws NoSuchMethodException
    {
        Method[] methods = target.getClass().getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            Method m = methods[i];
            Class[] actualParameterTypes = m.getParameterTypes();

            if (actualParameterTypes.length != parameterTypes.length)
                continue;

            if (!m.getName().equals(name))
                continue;

            for (int j = 0; j < actualParameterTypes.length; j++)
            {
                Class actualParameterType = actualParameterTypes[j];
                Class expectedParameterType = parameterTypes[j];
                if ((expectedParameterType != null && actualParameterType.isAssignableFrom(expectedParameterType))
                        || (expectedParameterType == null && !actualParameterType.isPrimitive()))
                    return m;
                
            }
        }

        throw new NoSuchMethodException(name);
    }

    /**
     * @param child
     * @return the type of the attribute of the child that is used as key
     */
    private Class getKeyType(Object child)
    {
        return PropertyUtils.getPropertyType(child, _keyAttribute);
    }

    /**
     * @param child
     * @return the value of the attribute of the child that is used as key
     */
    private Object getKeyValue(Object child)
    {
        return PropertyUtils.read(child, _keyAttribute);
    }

}
