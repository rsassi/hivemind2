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

/**
 * A simple replacement for the more involved version in commons-lang; this is used
 * to help construct the description string returned by an object's
 * <code>toString()</code> method.
 *
 * @author Howard Lewis Ship
 */
public class ToStringBuilder
{
    private StringBuffer _buffer = new StringBuffer();

    private int _mode;
    private int _attributeCount;

    private static int _defaultMode;

    public static final int INCLUDE_PACKAGE_PREFIX = 0x1;
    public static final int INCLUDE_HASHCODE = 0x02;

    public ToStringBuilder(Object target)
    {
        this(target, _defaultMode);
    }

    public ToStringBuilder(Object target, int mode)
    {
        _mode = mode;

        appendClassName(target);
        appendHashCode(target);
    }

    private void appendHashCode(Object target)
    {
        if ((_mode & INCLUDE_HASHCODE) == 0)
            return;

        _buffer.append('@');
        _buffer.append(Integer.toHexString(target.hashCode()));
    }

    private void appendClassName(Object target)
    {
        String className = target.getClass().getName();

        if ((_mode & INCLUDE_PACKAGE_PREFIX) != 0)
        {
            _buffer.append(className);
            return;
        }

        int lastdotx = className.lastIndexOf('.');

        _buffer.append(className.substring(lastdotx + 1));
    }

    public static int getDefaultMode()
    {
        return _defaultMode;
    }

    public static void setDefaultMode(int i)
    {
        _defaultMode = i;
    }

    /**
     * Returns the final assembled string. This may only be invoked once, after
     * all attributes have been appended.
     */
    public String toString()
    {
        if (_attributeCount > 0)
            _buffer.append(']');

        String result = _buffer.toString();

        _buffer = null;

        return result;
    }

    public void append(String attributeName, boolean value)
    {
        append(attributeName, String.valueOf(value));
    }

    public void append(String attributeName, byte value)
    {
        append(attributeName, String.valueOf(value));

    }
    public void append(String attributeName, short value)
    {
        append(attributeName, String.valueOf(value));
    }

    public void append(String attributeName, int value)
    {
        append(attributeName, String.valueOf(value));
    }

    public void append(String attributeName, Object value)
    {
        append(attributeName, String.valueOf(value));
    }

    public void append(String attributeName, String value)
    {
        if (_attributeCount++ == 0)
            _buffer.append('[');

        else
            _buffer.append(' ');

        _buffer.append(attributeName);

        _buffer.append('=');

        _buffer.append(value);
    }
}
