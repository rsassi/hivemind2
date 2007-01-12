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

import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;

/**
 * Implementation of the {@link org.apache.hivemind.Location} interface.
 * Uses a line and column based position.
 *
 * @author Howard Lewis Ship
 */
public final class LocationImpl implements Location
{
    private Resource _resource;
    private int _lineNumber = -1;
    private int _columnNumber = -1;

    public LocationImpl(Resource resource)
    {
        _resource = resource;
    }

    public LocationImpl(Resource resource, int lineNumber)
    {
        this(resource);

        _lineNumber = lineNumber;
    }

    public LocationImpl(Resource resource, int lineNumber, int columnNumber)
    {
        this(resource);

        _lineNumber = lineNumber;
        _columnNumber = columnNumber;
    }

    public Resource getResource()
    {
        return _resource;
    }

    public int getLineNumber()
    {
        return _lineNumber;
    }

    public int getColumnNumber()
    {
        return _columnNumber;
    }

    public int hashCode()
    {
        return ((237 + _resource.hashCode()) << 3 + _lineNumber) << 3 + _columnNumber;
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof LocationImpl))
            return false;

        LocationImpl l = (LocationImpl) other;

        if (_lineNumber != l.getLineNumber())
            return false;

        if (_columnNumber != l.getColumnNumber())
            return false;

        return _resource.equals(l.getResource());
    }
    
    /**
     * Returns the position in format "line x, column y"
     * 
     * @see org.apache.hivemind.Location#getPosition()
     */
    public String getPosition()
    {
        String result = "";
        
        if (_lineNumber > 0)
        {
            result += "line " + _lineNumber;
        }

        if (_columnNumber > 0)
        {
            if (result.length() > 0) {
                result += ", "; 
            }
            result += "column " + _columnNumber;
        }
        
        return result;
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer(_resource.toString());
        String position = getPosition();
        if (position.length() > 0) {
            buffer.append(", "); 
            buffer.append(position); 
        }

        return buffer.toString();
    }

 }
