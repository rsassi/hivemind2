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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Test for {@link org.apache.hivemind.util.LocalizedResourceFinder}.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestLocalizedContextResourceFinder extends HiveMindTestCase
{
    public void testFound() throws Exception
    {
        MockControl control = newControl(ServletContext.class);
        ServletContext sc = (ServletContext) control.getMock();

        sc.getResource("/foo/bar/baz_en_US.html");
        control.setReturnValue(null);

        sc.getResource("/foo/bar/baz_en.html");
        control.setReturnValue(new URL("http://foo.com"));

        replayControls();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        LocalizedResource lr = f.resolve("/foo/bar/baz.html", Locale.US);

        assertEquals("/foo/bar/baz_en.html", lr.getResourcePath());
        assertEquals(Locale.ENGLISH, lr.getResourceLocale());

        verifyControls();

    }

    public void testNotFound() throws Exception
    {
        MockControl control = newControl(ServletContext.class);
        ServletContext sc = (ServletContext) control.getMock();

        sc.getResource("/foo/bar/baz_en.html");
        control.setReturnValue(null);

        sc.getResource("/foo/bar/baz.html");
        control.setReturnValue(null);

        replayControls();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        assertNull(f.resolve("/foo/bar/baz.html", Locale.ENGLISH));

        verifyControls();
    }

    public void testNotFoundException() throws Exception
    {
        MockControl control = newControl(ServletContext.class);
        ServletContext sc = (ServletContext) control.getMock();

        sc.getResource("/foo/bar/baz_en.html");
        control.setThrowable(new MalformedURLException());

        sc.getResource("/foo/bar/baz.html");
        control.setThrowable(new MalformedURLException());

        replayControls();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        assertNull(f.resolve("/foo/bar/baz.html", Locale.ENGLISH));

        verifyControls();
    }

    public void testExtensionlessResource() throws Exception
    {
        MockControl control = newControl(ServletContext.class);
        ServletContext sc = (ServletContext) control.getMock();

        sc.getResource("/foo/bar/baz");
        control.setReturnValue(new URL("http://foo.com"));

        replayControls();

        LocalizedContextResourceFinder f = new LocalizedContextResourceFinder(sc);

        LocalizedResource lr = f.resolve("/foo/bar/baz", null);

        assertEquals("/foo/bar/baz", lr.getResourcePath());
        assertNull(lr.getResourceLocale());

        verifyControls();
    }
}