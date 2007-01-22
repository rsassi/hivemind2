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

package org.apache.hivemind.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Registry;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.service.ThreadCleanupListener;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.servlet.AutoloadingHiveMindFilter}.
 * 
 * @author Howard Lewis Ship
 */
public class TestAutoloadingHiveMindFilter extends HiveMindTestCase
{
    private static class ThreadListenerFixture implements ThreadCleanupListener
    {
        private boolean _cleanup;

        public void threadDidCleanup()
        {
            _cleanup = true;
        }

        public boolean getCleanup()
        {
            return _cleanup;
        }

    }

    private static class ShutdownListenerFixture implements RegistryShutdownListener
    {
        private boolean _didShutdown;

        public void registryDidShutdown()
        {
            _didShutdown = true;
        }

        public boolean getDidShutdown()
        {
            return _didShutdown;
        }

    }

    private static class FailingAutoloadingHiveMindFilterFixture extends AutoloadingHiveMindFilter
    {

        protected Registry constructRegistry(FilterConfig config)
        {
            throw new ApplicationRuntimeException("Forced failure.");
        }

    }

    private static class RegistryExposingAutoloadingHiveMindFilterFixture extends AutoloadingHiveMindFilter
    {

        private Registry _registry;

        public Registry getRegistry()
        {
            return _registry;
        }

        protected Registry constructRegistry(FilterConfig config)
        {
            _registry = super.constructRegistry(config);
            return _registry;
        }

    }

    private static class RebuildRegistryChainFixture implements FilterChain
    {
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
                ServletException
        {
            AutoloadingHiveMindFilter.rebuildRegistry((HttpServletRequest) request);
        }
    }

    public void testBasic() throws Exception
    {
        FilterConfig filterConfig = newFilterConfig();

        replayControls();

        RegistryExposingAutoloadingHiveMindFilterFixture f = new RegistryExposingAutoloadingHiveMindFilterFixture();

        f.init(filterConfig);

        verifyControls();

        Registry r = f.getRegistry();

        assertNotNull(r);

        ThreadEventNotifier t = (ThreadEventNotifier) r.getService(
                HiveMind.THREAD_EVENT_NOTIFIER_SERVICE,
                ThreadEventNotifier.class);

        ThreadListenerFixture l = new ThreadListenerFixture();

        t.addThreadCleanupListener(l);

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) newMock(FilterChain.class);

        request.setAttribute(AutoloadingHiveMindFilter.REQUEST_KEY, r);

        chain.doFilter(request, response);

        request.getAttribute(AutoloadingHiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(null);

        request.getAttribute(AutoloadingHiveMindFilter.REQUEST_KEY);
        requestControl.setReturnValue(r);

        replayControls();

        f.doFilter(request, response, chain);

        assertSame(r, AutoloadingHiveMindFilter.getRegistry(request));

        assertEquals(true, l.getCleanup());

        f.destroy();

        try
        {
            t.addThreadCleanupListener(null);
            unreachable();
        }
        catch (ApplicationRuntimeException ex)
        {
            assertExceptionSubstring(ex, "The HiveMind Registry has been shutdown.");
        }

        verifyControls();
    }

    public void testShutdown() throws Exception
    {
        MockControl contextc = newControl(ServletContext.class);
        ServletContext context = (ServletContext) contextc.getMock();

        MockControl configc = newControl(FilterConfig.class);
        FilterConfig filterConfig = (FilterConfig) configc.getMock();

        filterConfig.getServletContext();
        configc.setReturnValue(context);

        replayControls();

        RegistryExposingAutoloadingHiveMindFilterFixture f = new RegistryExposingAutoloadingHiveMindFilterFixture();

        f.init(filterConfig);

        verifyControls();

        Registry r = f.getRegistry();

        assertNotNull(r);

        ShutdownCoordinator coordinator = (ShutdownCoordinator) r
                .getService(ShutdownCoordinator.class);

        ShutdownListenerFixture l = new ShutdownListenerFixture();

        coordinator.addRegistryShutdownListener(l);

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = new RebuildRegistryChainFixture();

        request.setAttribute(AutoloadingHiveMindFilter.REQUEST_KEY, r);

        request.setAttribute(AutoloadingHiveMindFilter.REBUILD_REQUEST_KEY, Boolean.TRUE);

        request.getAttribute(AutoloadingHiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(Boolean.TRUE);

        filterConfig.getServletContext();
        configc.setReturnValue(context);

        replayControls();

        f.doFilter(request, response, chain);

        verifyControls();

        assertEquals(true, l.getDidShutdown());
    }

    private FilterConfig newFilterConfig() throws Exception
    {
        MockControl control = newControl(ServletContext.class);

        ServletContext context = (ServletContext) control.getMock();

        return newFilterConfig(context);
    }

    private FilterConfig newFilterConfig(ServletContext context)
    {
        MockControl control = newControl(FilterConfig.class);
        FilterConfig config = (FilterConfig) control.getMock();

        config.getServletContext();
        control.setReturnValue(context);

        return config;
    }

    public void testExceptionInInit() throws Exception
    {
        Filter f = new FailingAutoloadingHiveMindFilterFixture();

        interceptLogging(AutoloadingHiveMindFilter.class.getName());

        f.init(null);

        assertLoggedMessage("Forced failure");

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) newMock(FilterChain.class);

        request.setAttribute(AutoloadingHiveMindFilter.REQUEST_KEY, null);

        chain.doFilter(request, response);

        request.getAttribute(AutoloadingHiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(null);

        replayControls();

        f.doFilter(request, response, chain);

        verifyControls();

        f.destroy();
    }

    public void testDestroyWithoutRepository()
    {
        Filter f = new AutoloadingHiveMindFilter();

        f.destroy();
    }

    public void testFilterWithoutRepository() throws Exception
    {
        Filter f = new AutoloadingHiveMindFilter();

        interceptLogging(AutoloadingHiveMindFilter.class.getName());

        MockControl requestControl = newControl(HttpServletRequest.class);
        HttpServletRequest request = (HttpServletRequest) requestControl.getMock();
        HttpServletResponse response = (HttpServletResponse) newMock(HttpServletResponse.class);
        FilterChain chain = (FilterChain) newMock(FilterChain.class);

        request.setAttribute(AutoloadingHiveMindFilter.REQUEST_KEY, null);

        chain.doFilter(request, response);

        request.getAttribute(AutoloadingHiveMindFilter.REBUILD_REQUEST_KEY);
        requestControl.setReturnValue(null);

        replayControls();

        f.doFilter(request, response, chain);

        assertLoggedMessage("Unable to cleanup current thread");

        verifyControls();
    }

}