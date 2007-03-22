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

package org.apache.hivemind.annotations.internal;

import java.lang.reflect.Method;

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;

/**
 * Implementation of the {@link org.apache.hivemind.Location} interface.
 * Uses a method name of a class as position.
 *
 * @author Achim Huegen
 */
public final class AnnotatedModuleLocation implements Location
{
    private Resource _resource;
    private Class _moduleClass;
    private Method _method;

    public AnnotatedModuleLocation(Resource resource, Class moduleClass, Method method)
    {
        _resource = resource;
        _moduleClass = moduleClass;
        _method = method;
    }
    
    public AnnotatedModuleLocation(Resource resource, Class moduleClass)
    {
        this(resource, moduleClass, null);
    }

    public Resource getResource()
    {
        return _resource;
    }
    
    public Method getMethod()
    {
        return _method;
    }

    public Class getModuleClass()
    {
        return _moduleClass;
    }

    public int hashCode()
    {
        return ((237 + _resource.hashCode()) + 13 * _moduleClass.hashCode()) 
            + (_method != null ? _method.hashCode() : 0);
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof AnnotatedModuleLocation))
            return false;

        AnnotatedModuleLocation l = (AnnotatedModuleLocation) other;

        if (_moduleClass.equals(l.getModuleClass()))
            return false;
        
        if (_method.equals(l.getMethod()))
            return false;

        return _resource.equals(l.getResource());
    }

    /**
     * Returns the position in format "class x, method y"
     * 
     * @see org.apache.hivemind.Location#getPosition()
     */
    public String getPosition()
    {
        String result = "Class " + _moduleClass.getName();
        if (_method != null) {
            result += ", method " + _method.getName();
        }
        return result;
    }
    
    public String toString()
    {
        return getPosition();
    }

    /**
     * @see org.apache.hivemind.Location#getColumnNumber()
     */
    public int getColumnNumber()
    {
        return -1;
    }

    /**
     * @see org.apache.hivemind.Location#getLineNumber()
     */
    public int getLineNumber()
    {
        return -1;
    }

 }
