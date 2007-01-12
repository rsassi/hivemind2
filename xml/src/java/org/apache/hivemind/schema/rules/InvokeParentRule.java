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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Element;
import org.apache.hivemind.schema.SchemaProcessor;

/**
 * Rule used to connect a child object to its parent by invoking a method on the parent, passing the
 * child. The child object is the top object on the stack and the parent object is the next object
 * down on the stack. Created from the <code>&lt;invoke-parent&gt;</code> element. Generally, this
 * is the last rule in a sequence of rules.
 * 
 * @author Howard Lewis Ship
 */
public class InvokeParentRule extends BaseRule
{
    private String _methodName;

    private int _depth = 1;
    private int _parameterCount = 1;

    public InvokeParentRule()
    {

    }

    public InvokeParentRule(String methodName)
    {
        _methodName = methodName;
    }

    /**
     * Invokes the named method on the parent object (using reflection).
     */
    public void begin(SchemaProcessor processor, Element element)
    {
        Object[] parameters = new Object[_parameterCount];
        Class[] parameterTypes = new Class[_parameterCount];
        // Get all parameters from the stack in the order they where pushed on it
        for (int i = 0; i < parameters.length; i++)
        {
            parameters[i] = processor.peek(_parameterCount - i - 1);
            if (parameterTypes[i] != null)
                parameterTypes[i] = parameters[i].getClass();
        }

        String methodName = _methodName;
        Object parent = processor.peek(_parameterCount + _depth - 1);
        if (processor.isInBackwardCompatibilityModeForMaps() && _parameterCount == 1
                && ("addElement".equals(_methodName) || "add".equals(_methodName))) {
            methodName = "addKeyedElement";
            // We can't call the map (parent) directly, since we must calculate the key attribute
            // value, but don't have access to its definition.
            // Let the processor do the work. It knows the current element-model and its attributes
            parent = processor;
        } else if (parent instanceof List && "addElement".equals(_methodName) ) {
            // For Backward compatiblity: If parent is a list and the method is 'addElement'
            // then change it to add. 
            methodName = "add"; 
        }
        
        try
        {
            Method m = findMethod(parent, methodName, parameterTypes);

            m.invoke(parent, parameters);
        }
        catch (InvocationTargetException ex) {
            throw new ApplicationRuntimeException(RulesMessages.errorInvokingMethod(
                    methodName,
                    parent,
                    getLocation(),
                    ex.getTargetException()), getLocation(), ex.getTargetException());
        } catch (Exception ex)
        {
            throw new ApplicationRuntimeException(RulesMessages.errorInvokingMethod(
                    methodName,
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
     * @since 1.1
     */
    public int getDepth()
    {
        return _depth;
    }

    /**
     * Sets the depth of the parent object. The default is 1.
     */
    public void setDepth(int i)
    {
        _depth = i;
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

    public int getParameterCount()
    {
        return _parameterCount;
    }

    /**
     * Sets the number of parameters that are passed to the invoked method. The default is 1.
     * 
     * @param parameterCount
     */
    public void setParameterCount(int parameterCount)
    {
        _parameterCount = parameterCount;
    }

}
